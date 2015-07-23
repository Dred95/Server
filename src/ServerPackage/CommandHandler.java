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
	public float x;
	public float y;
	public SuperFigure superFigure;
	public ArrayList<Integer> mobs;

	public Move(float x, float y, SuperFigure superFigure, ArrayList<Integer> mobs){
		name = "Move";
		this.x = x;
		this.y = y;
		this.superFigure = superFigure;
		this.mobs = mobs;
	}
}

class Setp extends DefaultCommand{
	public ArrayList<Mob> mobs;
	public ArrayList<Planet> planets;
	public int receiverID;

	public Setp(ArrayList<Mob> mobs, ArrayList<Planet> planets, int receiverID){
		name ="Setp";
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

class MoveHandler extends CommandHandler
{
	public MoveHandler(MessageServer creator) {
		super(creator);
	}
	
	@Override
	public void Handle(String text) {
		Move command = messageServer.gson.fromJson(text, Move.class);
		
		messageServer.gameplayServer.setSelectedID(command.mobs);
		messageServer.gameplayServer.moveToPoint(command.x, command.y, command.superFigure, false);
		
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