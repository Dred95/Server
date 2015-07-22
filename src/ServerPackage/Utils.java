package ServerPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {
	public static int NEUTRAL_OWNER_ID = 10;
	int lastPlanetID = 10;
	int lastMobID = 50;
	public  Gson gson;
	
	public Utils(){
		gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	public String createOutputString(DefaultCommand command){
		return deleteSpaces(gson.toJson(command));
	}
	
	public int GetNewPlanetID(){
		
		return ++lastPlanetID;
	}
	
	public int GetNewMobID(){

		System.out.println("id = "+lastMobID);
		return ++lastMobID;
	}
	
	public String deleteSpaces(String text){
		text = text.replaceAll("\n", "");
		text = text.replaceAll(" ", "");
		return text;
	}
}
