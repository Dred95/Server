package ServerPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {
	public static int NEUTRAL_OWNER_ID = 10;
	int lastPlanetID = 10;
	int lastMobID = 50;
	public  Gson gson;
	
	
	public Utils()
	{
		gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	public String CreateSetupConfig(Setp setp)
	{
		return gson.toJson(setp);
		
	}
	
	public String CreateDeltaUpdate(Delt delt)
	{
		return gson.toJson(delt);
		
	}
	
	public int GetNewPlanetID()
	{
		return ++lastPlanetID;
	}
	
	public int GetNewMobID()
	{
		return ++lastMobID;
	}
	
	public String DeleteSpaces(String text)
	{
		text = text.replaceAll("\n", "");
		text = text.replaceAll(" ", "");
		return text;
	}
	
	
}
