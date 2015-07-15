package ServerPackage;
import java.util.ArrayList;

import GameplayPackage.*;

public abstract class CommandHandler {
	MessageServer messageServer;
	
	public CommandHandler(MessageServer creator)
	{
		messageServer = creator;
	}
	abstract public void Handle(String text);
}

class DefaultCommand{
	String name ="Blank";
	int From = 0;
}

class PingCommand extends DefaultCommand
{
	int number = 0;
	public PingCommand (int number)
	{
		this.number = number;
		name = "Ping";
	}
	
}

class PingHandler extends CommandHandler
{

	public PingHandler(MessageServer creator) {
		super(creator);
		
	}

	@Override
	public void Handle(String text) {
		// TODO Auto-generated method stub
		PingCommand command = messageServer.gson.fromJson(text, PingCommand.class);
		messageServer.gameplayServer.SetPing(command.From);
		
	}
	
}


class Move extends DefaultCommand{
	int x;
	int y;

	ArrayList<Integer> mobs;
	
	public Move(int x, int y, ArrayList<Integer> mobs){
		name ="Move";  //command identifier
		this.x = x;
		this.y = y;
		this.mobs = mobs;
	}
}
class Setp extends DefaultCommand{
	ArrayList<Mob> mobs;
	ArrayList<Planet> planets;
	int receiverID;
	
	public Setp(ArrayList<Mob> mobs, ArrayList<Planet> planets, int receiverID){
		name ="Setp";  //command identifier
		this.planets = planets;
		this.mobs = mobs;
		this.receiverID = receiverID;
	}
}

//Delta update
class Delt extends DefaultCommand{
	ArrayList<Mob> mobs;
	ArrayList<Planet> planets;
	
	public Delt(){
		name ="Delt";  //command identifier
		planets = new ArrayList<Planet>();
		mobs = new ArrayList<>();
	}
}

//typical command 10-5:1,23,4.
class MoveHandler extends CommandHandler
{
	public MoveHandler(MessageServer creator) {
		super(creator);
		// TODO Auto-generated constructor stub
	}

	String[] sArray;
	String substring;
	ArrayList<Integer> mobs;
	int x,y;
	
	@Override
	public void Handle(String text) {
		Move command = messageServer.gson.fromJson(text, Move.class);
		
		System.out.println(String.format("Move to [%d ; %d] mobs: %d", command.x, command.y, 0)); // if all fine echo back command
		
		messageServer.gameplayServer.moveToPoint(command.x, command.y, null, command.mobs);
		
		command.From = 0;
		String temp = messageServer.gson.toJson(command);
		
		temp = temp.replaceAll("\n", "");
		temp = temp.replaceAll(" ", "");
		messageServer.addToOutputQueue(temp);
	}
}

class MovePlanetHandler extends CommandHandler{
	
	public MovePlanetHandler(MessageServer creator) {
		super(creator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Handle(String text) {
		System.out.println("Command move planet:" + text);
	}
}

class AttackMobHandler extends CommandHandler{
	
	public AttackMobHandler(MessageServer creator) {
		super(creator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Handle(String text) {
		System.out.println("Command attack by mob:" + text);
	}
}