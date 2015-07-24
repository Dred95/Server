package ServerPackage;

import java.util.ArrayList;
import java.util.Map;

import GameplayPackage.Mob;
import GameplayPackage.Planet;

public class DefaultCommand{
	public String name ="Blank";
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


class Move extends DefaultCommand{
	public float x;
	public float y;
	public int targetID;
	public ArrayList<Integer> mobs;

	public Move(float x, float y, int targetID, ArrayList<Integer> mobs){
		name = "Move";
		this.x = x;
		this.y = y;
		this.targetID = targetID;
		this.mobs = mobs;
	}
}

class StartConfiguration extends DefaultCommand{
	public ArrayList<Add> add;
	public int receiverID;
	public int levelWidth;
	public int levelHeight;

	public StartConfiguration(ArrayList<Add> add, int receiverID, int levelWidth, int levelHeight){
		name ="StartConfiguration";
		this.add = add;
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
		this.receiverID = receiverID;
	}
}

class Attack extends DefaultCommand{
    public int attackedID;
    public int attackerID;

    public Attack(int attackedID, int attackerID){
        name = "Attack";
        this.attackedID = attackedID;
        this.attackerID = attackerID;
    }
}

class Add extends DefaultCommand {
    public float x;
    public float y;
    public float radius;
    public int id;
    public int ownerID;

    public Add(int id, int ownerID, float x, float y, float radius){
        name = "Add";
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.id = id;
        this.ownerID = ownerID;
    }
}

class Remove extends DefaultCommand {
    public int id;

    public Remove(int id){
        name = "Remove";
        this.id = id;
    }
}

class NewOwner extends DefaultCommand {
    public int id;
    public int ownerID;

    public NewOwner(int id, int ownerID){
        name = "NewOwner";
        this.id = id;
        this.ownerID = ownerID;
    }
}

class DeltaUpdate extends DefaultCommand{
	public ArrayList<Integer> id;
    public ArrayList<Float> xPosition;
    public ArrayList<Float> yPosition;

    public DeltaUpdate(ArrayList<Integer> id, ArrayList<Float> xPosition, ArrayList<Float> yPosition){
        name = "DeltaUpdate";
        this.id = id;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}