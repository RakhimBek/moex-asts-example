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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.Files.createTempDirectory;

public final class NativeLibLoader {
	final Pattern pattern = Pattern.compile(".+/([^/]+)$");

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

	public void load() throws IOException {
		final LibsDescription libsDescription = readDescription();
		final PlatformLibsDescription mtejniLibsDescription = libsDescription.getMtejni();
		final PlatformLibsDescription embeddedLibsDescription = libsDescription.getModes().get(Mode.EMBEDDED);

		final List<String> mtejniLibs = getPathList(mtejniLibsDescription);
		final List<String> embeddedLibs = getPathList(embeddedLibsDescription);
		for (String path : extract(mtejniLibs, embeddedLibs)) {
			try {
				System.load(path);
				System.out.printf("loaded: %s", path);
			} catch (UnsatisfiedLinkError e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private List<String> extract(List<String> mtejniLibs, List<String> embeddedLibs) throws IOException {
		final List<String> fsPathList = new ArrayList<>();

		for (String path : embeddedLibs) {
			fsPathList.add(extract(path, getLibName(path)).getPath());
		}

		for (String path : mtejniLibs) {
			fsPathList.add(extract(path, getLibName(path)).getPath());
		}

		return fsPathList;
	}

	private String getLibName(String resourcePath) {
		final Matcher matcher = pattern.matcher(resourcePath);
		matcher.matches();
		return matcher.group(1);
	}

	private List<String> getPathList(PlatformLibsDescription libs) {
		if (isWindows()) {
			return getPathList(libs.getWindows());
		} else {
			return getPathList(libs.getLinux());
		}
	}

	private List<String> getPathList(PlatformDescription description) {
		if (is64()) {
			return description.getArch64();
		} else {
			return description.getArch32();
		}
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

		//output.deleteOnExit();
		return output;

	}

	private boolean isWindows() {
		return osname.startsWith("windows");
	}

	private boolean is64() {
		return osarch.contains("64");
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
		private Map<Mode, PlatformLibsDescription> modes;
		private PlatformLibsDescription mtejni;

		public Map<Mode, PlatformLibsDescription> getModes() {
			return modes;
		}

		public void setModes(final Map<Mode, PlatformLibsDescription> modes) {
			this.modes = modes;
		}

		public PlatformLibsDescription getMtejni() {
			return mtejni;
		}

		public void setMtejni(final PlatformLibsDescription mtejni) {
			this.mtejni = mtejni;
		}
	}

	public static class PlatformLibsDescription {
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
