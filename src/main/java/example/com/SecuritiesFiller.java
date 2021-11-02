package example.com;

import com.micex.client.Filler;
import com.micex.client.Meta;

public class SecuritiesFiller implements Filler {

	@Override
	public boolean initTableUpdate(final Meta.Message message) {
		System.out.println("initTableUpdate");
		return false;
	}

	@Override
	public void doneTableUpdate(final Meta.Message message) {
		System.out.println("doneTableUpdate");
	}

	@Override
	public boolean initRecordUpdate(final Meta.Message message) {
		System.out.println("initRecordUpdate");
		return false;
	}

	@Override
	public void doneRecordUpdate(final Meta.Message message) {
		System.out.println("doneRecordUpdate");
	}

	@Override
	public int getRecordDecimals() {
		return 0;
	}

	@Override
	public void setRecordDecimals(final int i) {
		System.out.printf("setRecordDecimals: %s%n", i);
	}

	@Override
	public void setKeyValue(final Meta.Field field, final Object o) {
		if (o == null) {
			System.out.printf("setKeyValue: %s - null%n", field.name());
		} else {
			System.out.printf("setKeyValue: %s - %s(%s)%n", field.name(), o, o.getClass());
		}
	}

	@Override
	public void setFieldValue(final Meta.Field field, final Object o) {

	}

	@Override
	public void switchOrderbook(final Meta.Message message, final Meta.Ticker ticker) {
		// for orderbook
	}
}
