package example.com;

import com.micex.client.Filler;
import com.micex.client.Meta;

import java.util.HashSet;
import java.util.Set;

public class SecuritiesFiller implements Filler {
	final Set<String> fields = new HashSet<>();
	int recordDecimals;

	@Override
	public boolean initTableUpdate(final Meta.Message message) {
		System.out.printf("initTableUpdate: %s%n", message.name());
		return false;
	}

	@Override
	public void doneTableUpdate(final Meta.Message message) {
		System.out.printf("doneTableUpdate: %s%n", message.name());

		fields.stream().sorted().forEach(System.out::println);
	}

	@Override
	public boolean initRecordUpdate(final Meta.Message message) {
		System.out.printf("initRecordUpdate: %s%n", message.name());
		return false;
	}

	@Override
	public void doneRecordUpdate(final Meta.Message message) {
		System.out.printf("doneRecordUpdate: %s%n", message.name());
	}

	@Override
	public int getRecordDecimals() {
		System.out.printf("getRecordDecimals: %s%n", recordDecimals);
		return recordDecimals;
	}

	@Override
	public void setRecordDecimals(final int recordDecimals) {
		System.out.printf("setRecordDecimals: %s%n", recordDecimals);
		this.recordDecimals = recordDecimals;
	}

	@Override
	public void setKeyValue(final Meta.Field field, final Object o) {
		if (o == null) {
			System.out.printf("setKeyValue: %s - null%n", field.name());
		} else {
			fields.add(field.name());
			System.out.printf("setKeyValue: %s - %s(%s)%n", field.name(), o, o.getClass());
		}
	}

	@Override
	public void setFieldValue(final Meta.Field field, final Object o) {
		if (o == null) {
			System.out.printf("setFieldValue: %s - null%n", field.name());
		} else {
			fields.add(field.name());
			System.out.printf("setFieldValue: %s - %s (%s)%n", field.name(), o, o.getClass());
		}
	}

	@Override
	public void switchOrderbook(final Meta.Message message, final Meta.Ticker ticker) {
		// for orderbook
		System.out.println("switchOrderbook");
	}
}
