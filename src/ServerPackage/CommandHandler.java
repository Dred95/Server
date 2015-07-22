package ServerPackage;

public abstract class CommandHandler {
	MessageServer messageServer;
	
	public CommandHandler(MessageServer creator)
	{
		messageServer = creator;
	}
	abstract public void Handle(String text);
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

class MoveHandler extends CommandHandler{
	public MoveHandler(MessageServer creator) {
		super(creator);
	}
	
	@Override
	public void Handle(String text) {
		Move command = messageServer.gson.fromJson(text, Move.class);
		
		messageServer.gameplayServer.setSelectedID(command.mobs);
		messageServer.gameplayServer.moveToPoint(command.x, command.y, command.targetID, false);
		
		command.From = 0;
		
		messageServer.addToOutputQueue(text);
	}
}