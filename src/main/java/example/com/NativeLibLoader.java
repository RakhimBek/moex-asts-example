package example.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

import static java.nio.file.Files.createTempDirectory;

public final class NativeLibLoader {

	private final String osname = System.getProperty("os.name").toLowerCase(Locale.US).trim().replaceAll("[^a-z0-9]+", "");
	private final String osarch = System.getProperty("os.arch").toLowerCase(Locale.US).trim();

	public NativeLibLoader() {
		System.out.printf("osarch: %s%n", osname);
		System.out.printf("osname: %s%n", osarch);
		System.out.println();
	}

	private static ClassLoader getClassLoader() {
		if (System.getSecurityManager() == null) {
			return NativeLibLoader.class.getClassLoader();
		}
		return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) NativeLibLoader.class::getClassLoader);
	}

	public void loadFolder(String libFolder, String libName) throws IOException {
		final String resourcesPath = getNativeResourcesPath(libFolder);
		final String nativeLibName = System.mapLibraryName(libName);
		final String filePath = resourcesPath.concat(nativeLibName);
		System.out.println(filePath);

		final File temporaryFile = extract(filePath, nativeLibName);
		System.load(temporaryFile.getPath());

		System.out.println();
	}

	private File extract(String resource, String nativeLibName) throws IOException {
		final ClassLoader classLoader = getClassLoader();
		final URL url = classLoader.getResource(resource);
		if (url == null) {
			throw new UnsatisfiedLinkError("Failed to load " + resource);
		}

		final File output = createTempDirectory(nativeLibName)
				.resolve(nativeLibName)
				.toFile();

		try (
				final InputStream source = url.openStream();
				final OutputStream target = new FileOutputStream(output)
		) {
			final byte[] buffer = new byte[8192];
			int length;
			while ((length = source.read(buffer)) > 0) {
				target.write(buffer, 0, length);
			}
		}

		output.deleteOnExit();
		return output;

	}

	private String getNativeResourcesPath(String libFolder) {
		final String folder = osname.startsWith("windows") ? "win" : "linux";
		final String suffix = osarch.contains("64") ? "64" : "32";
		System.out.println(libFolder + folder + suffix);
		return libFolder + folder + suffix + "/";
	}
}
