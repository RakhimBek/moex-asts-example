package example.com;

import com.micex.client.Filler;
import com.micex.client.Meta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SecuritiesFiller implements Filler {
	final File file = new File("securities.csv");

	final FileWriter fileWriter = new FileWriter(file, Charset.forName("UTF-8"));
	final Map<String, Record> records = new HashMap<>();
	private final Map<String, String> currentKeys = new LinkedHashMap<>();
	final Set<String> fields = new HashSet<>();
	private int currentRecordDecimals;
	private String currentKey;
	private Record currentRecord;

	public SecuritiesFiller() throws IOException {
	}

	@Override
	public boolean initTableUpdate(final Meta.Message message) {
		System.out.printf("initTableUpdate: %s%n", message.name());
		return false;
	}

	@Override
	public void doneTableUpdate(final Meta.Message message) {
		System.out.printf("doneTableUpdate: %s%n", message.name());
		System.out.println(fileWriter.getEncoding());
		System.out.println(System.getProperty("file.encoding"));

		try {
			fileWriter.write(String.join("\t", fields));
			fileWriter.write(String.format("%n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		records.forEach((key, row) -> {

			final String csvRow = fields.stream()
					.sorted()
					.map(field -> {
						final String value = row.get(field);
						if (value != null) {
							return new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
						}

						return "";
					})
					.collect(Collectors.joining("\t"));

			try {
				fileWriter.write(csvRow);
				fileWriter.write(String.format("%n"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public boolean initRecordUpdate(final Meta.Message message) {
		this.currentKey = currentKeys.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Map.Entry::getValue)
				.collect(Collectors.joining("-"));

		this.currentRecord = records.get(currentKey);
		if (currentRecord != null) {
			return false;
		}

		this.currentRecord = new Record();
		records.put(currentKey, this.currentRecord);
		return true;
	}

	@Override
	public void doneRecordUpdate(final Meta.Message message) {
		currentKeys.clear();
	}

	@Override
	public int getRecordDecimals() {
		return currentRecordDecimals;
	}

	@Override
	public void setRecordDecimals(final int recordDecimals) {
		this.currentRecordDecimals = recordDecimals;
	}

	@Override
	public void setKeyValue(final Meta.Field field, final Object o) {
		currentKeys.put(field.name(), Optional.ofNullable(o).map(Object::toString).orElse(null));
	}

	@Override
	public void setFieldValue(final Meta.Field field, final Object o) {
		fields.add(field.name());

		currentRecord.put(field.name(), Optional.ofNullable(o).map(Object::toString).orElse(null));
	}

	@Override
	public void switchOrderbook(final Meta.Message message, final Meta.Ticker ticker) {
		// for orderbook
	}

	static class Record {
		final Map<String, String> values = new LinkedHashMap<>();

		public void put(String column, String value) {
			values.put(column, value);
		}

		public String get(final String field) {
			return values.get(field);
		}
	}
}
