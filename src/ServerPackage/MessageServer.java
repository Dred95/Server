package ServerPackage;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/*TODO
 * Add get & set to queues
 * 
 * */
public class MessageServer {
	 public  Queue<String> inputQueue = new LinkedList<String>();
	 public  Queue<String> outputQueue = new LinkedList<String>();
	 Map<String,CommandHandler> comandHandlers = new HashMap<String, CommandHandler>();
	
	 SocketListener socketl1; 						
	 SocketListener socketl2;
	 Thread thread1 ; 								
	 Thread thread2;
	 java.net.ServerSocket server;	
	
	 public MessageServer()
	 {
	
	 }
	 
	void InitializeServer() throws IOException
	{
		System.out.println("Start Server");
		server = new ServerSocket(7070);		
	
		socketl1 = new SocketListener(this, server);
		thread1 = new Thread(socketl1,"thread1");
		thread1.setDaemon(true);
		thread1.start();
		System.out.println("Thread 1 started");
		
		socketl2 = new SocketListener(this, server);
		thread2 = new Thread(socketl2,"thread2");
		thread2.setDaemon(true);
		thread2.start();
		System.out.println("Thread 2 started");
		
		
		comandHandlers.put("MovM", new MoveMobHandler(this) );
		comandHandlers.put("MovP", new MovePlanetHandler(this) );
		comandHandlers.put("AtkM", new AttackMobHandler(this) );
		
	}
	//Test commands
	//From1MovM10;5:1,23,4.

boolean TryReadMessage(String input)
	{

		if (input.length()<10)
		{
			System.out.println("Invalid lenght :"+input);
			return false;
		} 
		
		String substring = input.substring(0, 4);
		
		if (!substring.equalsIgnoreCase("From"))
		{
			System.out.println("Invalid header: " + substring);
		}else
		{
			input = input.substring(4, input.length());
			substring = input.substring(0, 1);
			int fromID = Integer.parseInt(substring);
			
			if (fromID >0 && fromID<10)
			{
				System.out.println("Command from id ="+fromID);
				input = input.substring(1, input.length());
				substring = input.substring(0, 4);
			
				if (!comandHandlers.containsKey(substring))
				{
					System.out.println("Invalid Command: " + substring);
					
				}else
				{
				
					input = input.substring(4, input.length());
					if (input.charAt(input.length()-1)!='.')
					{
						System.out.println("Last char not . :"+input.charAt(input.length()-1));
					}else
					{
						comandHandlers.get(substring).Handle(input);
						return true;
					}
					
					
				}
				
			}
		}
		return false;
	}
	
	public  void Run() throws UnknownHostException, IOException, InterruptedException {
		InitializeServer();

		
		String input="";
		System.out.println("Wait for messages");
		
		while (true) {
			if (!inputQueue.isEmpty() || !outputQueue.isEmpty())
			{
				if (!inputQueue.isEmpty()){
					input = inputQueue.poll();
					
					if(!TryReadMessage(input) && !input.equalsIgnoreCase("close"))
					{
						socketl1.Send(input);
						socketl2.Send(input);
						System.out.println("Uncorrect message: "+input);
					}		
				}
				if (!outputQueue.isEmpty()){
					
					socketl1.Send(outputQueue.peek());
					socketl2.Send(outputQueue.poll());
					
				}
			}else
			{
				
				
				Thread.sleep(1000);
				continue;
			}
			
			
		
			if (!thread1.isAlive() && !thread2.isAlive()) break;
			}
		
		socketl1.Close();
		socketl2.Close();
		server.close();
		
	}
	
}


class SocketListener implements Runnable
{
	MessageServer gameServer;
	java.net.ServerSocket server = null;
	BufferedReader in;
	PrintWriter out;
	Socket client;
	public SocketListener(MessageServer creator, java.net.ServerSocket serverSocket)
	{
		this.server = serverSocket;
		this.gameServer = creator;
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
				//input = Thread.currentThread().getName() + ": "+input;
				gameServer.inputQueue.add(input);
				}
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		
		System.out.println(""+Thread.currentThread().getName()+" Closed");
		
	}
	
	public void Send(String message)
	{
		if (out!=null && message!=null && !message.isEmpty())
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
