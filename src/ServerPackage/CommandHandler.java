package ServerPackage;
import java.util.ArrayList;

import GameplayPackage.*;
public abstract class CommandHandler {
	MessageServer messageServer;
	CommandHandler(MessageServer creator)
	{
		messageServer = creator;
	}
	abstract public void Handle(String text);
}

class DefaultCommand
{
	String name ="Blank";
	int From = 0;
}

class Move extends DefaultCommand
{
	int x;
	int y;

	int[]mobs;
	
	Move(int x, int y, int[] mobs)
	{
		name ="Move";  //command identifier
		this.x = x;
		this.y = y;
		this.mobs = mobs;
	}
}
class Setp extends DefaultCommand
{
	ArrayList<Mob> mobs;
	ArrayList<Planet> planets;
	int receiverID;
	
	Setp(ArrayList<Mob> mobs, ArrayList<Planet> planets, int receiverID)
	{
		name ="Setp";  //command identifier
		this.planets = planets;
		this.mobs = mobs;
		this.receiverID = receiverID;
	}
}

//Delta update
class Delt extends DefaultCommand	

{
	ArrayList<Mob> mobs;
	ArrayList<Planet> planets;
	
	Delt()
	{
		name ="Delt";  //command identifier
		planets = new ArrayList<Planet>();
		mobs = new ArrayList<>();
	}
}




//typical command 10-5:1,23,4.
class MoveHandler extends CommandHandler
{
	MoveHandler(MessageServer creator) {
		super(creator);
		// TODO Auto-generated constructor stub
	}

	String[] sArray;
	String substring;
	int[] mobs;
	int x,y;
	
	@Override
	public void Handle(String text) {
		
		Move command = messageServer.gson.fromJson(text, Move.class);
		
		System.out.println(String.format("Move to [%d ; %d] mobs: %d", command.x, command.y, command.mobs[0])); // if all fine echo back command
		
		if (command.mobs[0]>49)
		{
			messageServer.gameplayServer.moveToPoint(command.x, command.y, null, command.mobs);
		}
		
		if (command.mobs[0]<50)
		{
			messageServer.gameplayServer.movePlanetToPoint(command.x, command.y, null, command.mobs);
		}
			
		
		
		
		
	
		String temp = messageServer.gson.toJson(command);
		
		temp = temp.replaceAll("\n", "");
		temp = temp.replaceAll(" ", "");
		messageServer.addToOutputQueue(temp);
		
	}


}

class MovePlanetHandler extends CommandHandler
{

	MovePlanetHandler(MessageServer creator) {
		super(creator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Handle(String text) {
		
		System.out.println("Command move planet:" + text);
	}
}

class AttackMobHandler extends CommandHandler
{

	AttackMobHandler(MessageServer creator) {
		super(creator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Handle(String text) {
		
		System.out.println("Command attack by mob:" + text);
	}
}