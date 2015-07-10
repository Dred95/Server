package ServerPackage;

public abstract class ComandHandler {
	abstract public void Handle(String text);
}

class MoveMobHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
	
		System.out.println("�������� ������� ��������� ����:" + text);
	}
	
}

class MovePlanetHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
		
		System.out.println("�������� ������� ��������� �������:" + text);
	}
}

class AttackMobHandler extends ComandHandler
{

	@Override
	public void Handle(String text) {
		
		System.out.println("�������� ������� ��������� �����:" + text);
	}
}