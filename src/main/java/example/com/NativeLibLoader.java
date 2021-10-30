package example.com;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private final Gson gson = new GsonBuilder().create();
	private final Pattern pattern = Pattern.compile(".+/([^/]+)$");
	private final Path tempDirectory = createTempDirectory("MOEX_LIBS");
	private final boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.US).contains("windows");
	private final boolean is64 = System.getProperty("os.arch").contains("64");

	public NativeLibLoader() throws IOException {
	}

	private static ClassLoader getClassLoader() {
		if (System.getSecurityManager() == null) {
			return NativeLibLoader.class.getClassLoader();
		}
		return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) NativeLibLoader.class::getClassLoader);
	}

	private LibsDescription readDescriptionYaml() throws IOException {
		final Constructor constructor = new Constructor(LibsDescription.class);
		final Yaml yaml = new Yaml(constructor);
		final ClassLoader classLoader = getClassLoader();

		try (final InputStream inputStream = classLoader.getResourceAsStream("natives/libs.yaml");) {
			return (LibsDescription) yaml.load(inputStream);
		}
	}

	private LibsDescription readDescriptionJSON() throws IOException {
		final ClassLoader classLoader = getClassLoader();

		try (
				final InputStream inputStream = classLoader.getResourceAsStream("natives/libs.json");
				final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		) {
			return gson.fromJson(inputStreamReader, LibsDescription.class);
		}
	}

	public void load() throws IOException {
		final LibsDescription libsDescription = readDescriptionJSON();
		final PlatformLibsDescription mtejniLibsDescription = libsDescription.getMtejni();
		final PlatformLibsDescription embeddedLibsDescription = libsDescription.getModes().get(Mode.EMBEDDED);

		final List<String> mtejniLibs = getResourceList(mtejniLibsDescription);
		final List<String> embeddedLibs = getResourceList(embeddedLibsDescription);
		for (String path : extract(mtejniLibs, embeddedLibs)) {
			try {
				System.load(path);
				System.out.printf("NativeLibLoader.load. '%s' - OK%n", path);
			} catch (UnsatisfiedLinkError e) {
				System.out.printf("NativeLibLoader.load: '%s' - %s%n", path, e.getMessage());
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

	private List<String> getResourceList(PlatformLibsDescription libs) {
		if (isWindows) {
			return getResourceList(libs.getWindows());
		} else {
			return getResourceList(libs.getLinux());
		}
	}

	private List<String> getResourceList(PlatformDescription description) {
		if (is64) {
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

		output.deleteOnExit();
		return output;

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
