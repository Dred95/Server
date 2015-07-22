package ServerPackage;

import java.util.ArrayList;
import java.util.HashMap;
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

class Setp extends DefaultCommand{
	public Map<Integer, Mob> mobs;
	public Map<Integer, Planet> planets;
	public int receiverID;

	public Setp(Map<Integer, Mob> mobs, Map<Integer, Planet> planets, int receiverID){
		name ="Setp";
		this.planets = planets;
		this.mobs = mobs;
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
