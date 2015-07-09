import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

class SocketListener implements Runnable
{
	java.net.ServerSocket server = null;
	BufferedReader in;
	PrintWriter out;
	Socket client;
	public SocketListener(java.net.ServerSocket serverSocket)
	{
		this.server = serverSocket;
	}

	@Override
	public void run(){
		
		
		String input;
		try {
			client = server.accept();
			System.out.println("Thread Initialized "+Thread.currentThread().getName());
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(),true);
			
			while ((input = in.readLine()) != null ) {
				if (input.equalsIgnoreCase("exit")) break;
				//out.println("? ::: "+input);
				//System.out.println(input);
				input = Thread.currentThread().getName() + ": "+input;
				GameServer.inputQueue.add(input);
				}
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		
		System.out.println(""+Thread.currentThread().getName()+" Closed");
		
	}
	
	public void Send(String message)
	{
		if (out!=null)
		{
			out.println(message);
		}
	}
	public void Close() throws IOException
	{
		if (client!=null)
		{
			client.close();
		}
	}
	
}

public class GameServer {
	public static Queue<String> inputQueue = new LinkedList<String>();
	@SuppressWarnings("unused")
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {

		System.out.println("Start Server");
		java.net.ServerSocket server = null;
		server = new ServerSocket(7070);						//сокет сервера
		SocketListener socketl1, socketl2; 						//сокеты клиентов
		Thread thread1,thread2 ; 								//ѕотоки прослушки
	
		socketl1 = new SocketListener(server);
		thread1 = new Thread(socketl1,"thread1");
		thread1.start();
		System.out.println("Thread 1 started");
		
		socketl2 = new SocketListener(server);
		thread2 = new Thread(socketl2,"thread2");
		thread2.start();
		System.out.println("Thread 2 started");
		
		String input="",output;
		System.out.println("Wait for messages");
		
		while (true) {
			if (!inputQueue.isEmpty())
			{
				input = inputQueue.poll();
			}else
			{
				Thread.sleep(1000);
				continue;
			}
			
			socketl1.Send(input);
			socketl2.Send(input);
			if (!thread1.isAlive() && !thread2.isAlive()) break;
			System.out.println(input);
			}
		
		socketl1.Close();
		socketl2.Close();
		server.close();
	}
	
}
