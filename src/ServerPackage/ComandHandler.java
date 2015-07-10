package ServerPackage;

public abstract class ComandHandler {
	abstract public void Handle(String text);
}

class MoveMobHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
	
		System.out.println("Command move mob:" + text);
	}
	
}

class MovePlanetHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
		
		System.out.println("Command move planet:" + text);
	}
}

class AttackMobHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
		
		System.out.println("Command attack by mob:" + text);
	}
}