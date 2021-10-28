package example.com;

import com.micex.client.API;
import com.micex.client.Client;
import com.micex.client.ClientException;
import com.micex.client.Meta;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws ClientException, IOException {
		final NativeLibLoader loader = new NativeLibLoader();
		loader.loadFolder("natives/embedded/", "mtesrl");
		loader.loadFolder("natives/embedded/", "mustang");
		loader.loadFolder("natives/embedded/", "tsmr");
		loader.loadFolder("natives/mtejni/", "mtejni");

		System.setProperty("com.moex.asts.mtejni.load", "false");

		final Map<String, String> params = new HashMap<>();
		params.put("PacketSize", "60000");
		params.put("Interface", "IFCBroker40");
		params.put("Server", "UAT_GATEWAY");
		params.put("Service", "16411/16412");
		params.put("Broadcast", "91.208.232.211");
		params.put("UserID", "***");
		params.put("Password", "***");
		params.put("Language", "English");
		params.put("LogFolder", "./log/");
		params.put("Logging", "4,2");
		params.put("LogLevel", "30");
		show(params);

		System.out.println("start.");

		final Client client = new Client();
		client.start(params);

		final API.ServerInfo serverInfo = client.getServerInfo();
		System.out.printf("user: %s, system: %s, server: %s%n", serverInfo.userID, serverInfo.systemID, serverInfo.sessionID);


		client.selectBoards(Collections.singleton("TQBR"));

		final Meta.Market marketInfo = client.getMarketInfo();
		System.out.printf("name: %s %s%n", marketInfo.name(), marketInfo.description());

		final Meta.Message securitiesMessage = marketInfo.tables().find(Meta.TableType.Securities);

		client.close();
		System.out.println("end.");
	}

	private static void show(Map<String, String> params) {
		System.out.println("Params:");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			System.out.printf("%s: %s%n", entry.getKey(), entry.getValue());
		}
		System.out.println();
	}
}
