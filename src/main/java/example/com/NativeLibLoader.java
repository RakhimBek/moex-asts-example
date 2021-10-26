package example.com;

import java.io.File;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


public final class NativeLibLoader {

	private final List<String> libExtensions = Arrays.asList(".so", ".dll", ".lib");
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

	public void loadFolder(String libFolder) {

		final String resourcesPath = getNativesResourcesPath(libFolder);
		final ClassLoader classLoader = getClassLoader();

		final URL resource = classLoader.getResource(resourcesPath);
		if (resource == null) {
			throw new IllegalArgumentException("Cannot find resource path " + resourcesPath);
		}

		final String path = resource.getPath();
		final List<String> fileNames = Optional.ofNullable(new File(path).list())
				.map(Arrays::asList)
				.orElse(Collections.emptyList());

		for (String fileName : fileNames) {
			if (isLibExtension(fileName)) {
				final String filePath = path.concat(fileName);
				System.out.println(filePath);
				System.load(filePath);
			}
		}
		System.out.println();
	}

	private boolean isLibExtension(String fileName) {
		for (String extension : libExtensions) {
			if (fileName.endsWith(extension)) {
				return true;
			}
		}

		return false;
	}

	private String getNativesResourcesPath(String libFolder) {
		final String folder = osname.startsWith("windows") ? "win" : "linux";
		final String suffix = osarch.contains("64") ? "64" : "32";
		System.out.println(libFolder + folder + suffix);
		return libFolder + folder + suffix + "/";
	}
}
