package example.com;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.nio.file.Files.createTempDirectory;

public final class NativeLibLoader {

	private final Path tempDirectory = createTempDirectory("MOEX_LIBS");
	private final String osname = System.getProperty("os.name").toLowerCase(Locale.US).trim().replaceAll("[^a-z0-9]+", "");
	private final String osarch = System.getProperty("os.arch").toLowerCase(Locale.US).trim();

	public NativeLibLoader() throws IOException {
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

	private LibsDescription readDescription() throws IOException {
		final Constructor constructor = new Constructor(LibsDescription.class);
		final Yaml yaml = new Yaml(constructor);
		final ClassLoader classLoader = getClassLoader();

		try (final InputStream inputStream = classLoader.getResourceAsStream("natives/libs.yaml");) {
			return (LibsDescription) yaml.load(inputStream);
		}
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

		final File output = tempDirectory
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

	public enum Mode {
		EMBEDDED,
		MTESRL,
	}

	public static class LibsDescription {
		private Map<Mode, PlatformLibs> modes;
		private PlatformLibs mtejni;

		public Map<Mode, PlatformLibs> getModes() {
			return modes;
		}

		public void setModes(final Map<Mode, PlatformLibs> modes) {
			this.modes = modes;
		}

		public PlatformLibs getMtejni() {
			return mtejni;
		}

		public void setMtejni(final PlatformLibs mtejni) {
			this.mtejni = mtejni;
		}
	}

	public static class PlatformLibs {
		private PlatformDescription linux;
		private PlatformDescription windows;

		public PlatformDescription getLinux() {
			return linux;
		}

		public void setLinux(final PlatformDescription linux) {
			this.linux = linux;
		}

		public PlatformDescription getWindows() {
			return windows;
		}

		public void setWindows(final PlatformDescription windows) {
			this.windows = windows;
		}
	}

	public static class PlatformDescription {
		private List<String> arch32;
		private List<String> arch64;

		public List<String> getArch32() {
			return arch32;
		}

		public void setArch32(final List<String> arch32) {
			this.arch32 = arch32;
		}

		public List<String> getArch64() {
			return arch64;
		}

		public void setArch64(final List<String> arch64) {
			this.arch64 = arch64;
		}
	}
}
