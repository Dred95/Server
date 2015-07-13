package ServerPackage;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;



import com.google.gson.*;

public class MessageServer {
	 private Queue<String> inputQueue = new LinkedList<String>();
	 private  Queue<String> outputQueue = new LinkedList<String>();
	 private  Map<String,CommandHandler> commandHandlers = new HashMap<String, CommandHandler>();
	 
	 private  SocketListener socketl1; 						
	 private  SocketListener socketl2;
	 private  Thread thread1; 								
	 private  Thread thread2;
	 private  java.net.ServerSocket server;	
     public  Gson gson;
	 private DefaultCommand command;
	 public Utils utils;
	 public GameplayServer gameplayServer;
	 
	 
	 public void addToInputQueue(String command){
		 if (command!=null && !command.isEmpty()){
			 inputQueue.add(command);
		 }
	 }
	 public void addToOutputQueue(String command){
		 if (command!=null && !command.isEmpty()){
			 outputQueue.add(command);
		 }
	 }
	 
	 public void SetGameplayServer(GameplayServer gameplayServer){
		 this.gameplayServer = gameplayServer;
	 }
	 
	 private  void InitializeServer() throws IOException{
		utils = new Utils();
		System.out.println("Start Server");
		server = new ServerSocket(7070);		
	
		gson = new GsonBuilder().setPrettyPrinting().create();
		
		socketl1 = new SocketListener(this, server);
		thread1 = new Thread(socketl1,"thread1");
		thread1.setDaemon(true);
		thread1.start();
		System.out.println("Thread 1 started");
		socketl1.Send("From0Setp1:");
		
		socketl2 = new SocketListener(this, server);
		thread2 = new Thread(socketl2,"thread2");
		thread2.setDaemon(true);
		thread2.start();
		System.out.println("Thread 2 started");
		
		commandHandlers.put("MovM", new MoveHandler(this) );
		commandHandlers.put("MovP", new MovePlanetHandler(this) );
		commandHandlers.put("AtkM", new AttackMobHandler(this) );
		
	}
	/*Test commands
{  "x": 5,  "y": 10,  "mobs": [    13,    14  ],  "name": "MovM", "From": 1}
	 */
	 private  boolean TryReadMessage(String input){
		command = gson.fromJson(input, DefaultCommand.class);
		
		if(commandHandlers.containsKey(command.name)){
			commandHandlers.get(command.name).Handle(input);
			return true;
		}
		
		return false;
	}
	 
	public void SendTo(int receiverID, String message){
		if (receiverID == 1){
			message = utils.DeleteSpaces(message);
			socketl1.Send(message);
		} else if(receiverID == 2){
			message = utils.DeleteSpaces(message);
			socketl2.Send(message);
		}else {
			System.out.println("Wrong receiver ID: "+ receiverID);
		}	
	}
	
	public  void Run() throws UnknownHostException, IOException, InterruptedException {
		InitializeServer();
			
		String input="";
		System.out.println("Wait for messages");
		
		while (true) {
			if (!inputQueue.isEmpty() || !outputQueue.isEmpty()){
				if (!inputQueue.isEmpty()){
					input = inputQueue.poll();
					
					if(!TryReadMessage(input) && !input.equalsIgnoreCase("close")){
						socketl1.Send(input);
						socketl2.Send(input);
						System.out.println("Uncorrect message: "+input);
					}		
				}
				if (!outputQueue.isEmpty()){
					socketl1.Send(outputQueue.peek());
					socketl2.Send(outputQueue.poll());
				}
			} else {
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

class SocketListener implements Runnable{
	private  MessageServer gameServer;
	private  java.net.ServerSocket server = null;
	private  BufferedReader in;
	private  PrintWriter out;
	private  Socket client;
	
	public SocketListener(MessageServer creator, java.net.ServerSocket serverSocket){
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
				gameServer.addToInputQueue(input);
				}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(""+Thread.currentThread().getName()+" Closed");
	}
	
	public void Send(String message){
		if (out!=null && message!=null && !message.isEmpty()){
			out.println(message);
		}
	}
	
	public void Close() throws IOException{
		if (client!=null){
			client.close();
		}
	}
}
