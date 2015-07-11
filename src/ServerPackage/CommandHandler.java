package ServerPackage;

public abstract class CommandHandler {
	MessageServer messageServer;
	CommandHandler(MessageServer creator)
	{
		messageServer = creator;
	}
	abstract public void Handle(String text);
}

//typical command 10-5:1,23,4.
class MoveMobHandler extends CommandHandler
{
	MoveMobHandler(MessageServer creator) {
		super(creator);
		// TODO Auto-generated constructor stub
	}

	String[] sArray;
	String substring;
	int[] mobs;
	int x,y;
	
	@Override
	public void Handle(String text) {
		
		try{
			sArray = text.split(";");
		
			x = Integer.parseInt(sArray[0]);
			text = text.substring(sArray[0].length()+1);
			sArray = text.split(":");
			y = Integer.parseInt(sArray[0]);
		
			text = text.substring(sArray[0].length()+1);
			
		
			text = text.substring(0,text.length()-1);
			//System.out.println("new text: "+text);
			
			sArray =  text.split(",");
			mobs = new int[sArray.length];
			for (int i = 0;i< mobs.length; i++) {
				mobs[i] = Integer.parseInt(sArray[i]);
			}
			
			
		}
		catch(java.lang.NumberFormatException e)
		{
			System.out.println("Invalid numbers");
			return;
		}
		System.out.println(String.format("Move to [%d ; %d] mobs: %s", x, y, text)); // if all fine echo back command
		
		text = "From0MovM" +x+";"+y+":"+mobs[0];
		
		for (int i = 1; i< mobs.length; i++)
		{
			text+=","+ mobs[i];
		}
		text +=".";
		messageServer.outputQueue.add(text);
		
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