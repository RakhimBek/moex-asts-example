package example.com;

import com.micex.client.Binder;
import com.micex.client.Filler;
import com.micex.client.Meta;

import java.io.IOException;

public class BinderImpl implements Binder {
	@Override
	public Filler getFiller(final Meta.Message message) {
		try {
			return new SecuritiesFiller();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
