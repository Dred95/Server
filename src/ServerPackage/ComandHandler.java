package ServerPackage;

public abstract class ComandHandler {
	abstract public void Handle(String text);
}

class MoveMobHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
	
		System.out.println("Получена команда подвинуть моба:" + text);
	}
	
}

class MovePlanetHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
		
		System.out.println("Получена команда подвинуть планету:" + text);
	}
}

class AttackMobHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
		
		System.out.println("Получена команда атаковать мобом:" + text);
	}
}