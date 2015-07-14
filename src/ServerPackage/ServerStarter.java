package ServerPackage;
import java.io.IOException;
import java.net.UnknownHostException;

public final class ServerStarter {
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		MessageServer messageServer;
		messageServer = new MessageServer();
		GameplayServer gameplayServer = new GameplayServer(messageServer);
		messageServer.gameplayServer = gameplayServer;
		messageServer.Run();

	}

}
