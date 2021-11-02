package example.com;

import com.micex.client.Binder;
import com.micex.client.Filler;
import com.micex.client.Meta;

public class BinderImpl implements Binder {
	@Override
	public Filler getFiller(final Meta.Message message) {
		return new SecuritiesFiller();
	}
}
