package ServerPackage;
import GameplayPackage.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GameplayServer {


		private MessageServer messageServer;
	    private String messageText;
	    //  private Stack<Planet> planets;
	    private ArrayList<Mob> mobs = new ArrayList<Mob>();
	    private ArrayList<Planet> planets = new ArrayList<Planet>();
	    private float mobRadius, HP, reloadTime, attackRadius, damage;
	    private float planetRadius, timeToControl, timeToRespawn;
	    private int size = 20;
	    private Delt deltaUpdate;
	    public Utils utils;
	    
	    
	    
	    
	    
	    
	    
	    public ArrayList<Mob> GetMobs() 
	    {
	    	return mobs;
	    	
	    }
	    
	    public ArrayList<Planet> GetPlanets() 
	    {
	    	return planets;
	    	
	    }
	    
	    
	    
	    
	    
	    
	    /**
	     * Constructor
	     * @param game - super game class
	     */
	    
	    
	    public GameplayServer(MessageServer messageServer){
	    	
	    	this.messageServer = messageServer;
	        //Mob's variables
	        mobRadius = 5;
	        HP = 100;
	        reloadTime = 5;
	        attackRadius = 50;
	        damage = 10;

	        //Planet's variables
	        planetRadius = 30;
	        timeToControl = 5;
	        timeToRespawn = 1;

	        startGame();
	    }

	    /**
	     * Start actions(Server part!)
	     */
	    private void startGame(){
	    	utils = new Utils();
	        
	    	int planetID = utils.GetNewPlanetID();
	    	planets.add((new Planet(planetID, 5, 5, planetRadius, timeToControl, timeToRespawn, 0, 1)));
	    	
	    	planetID = utils.GetNewPlanetID();
	    	planets.add(planetID, new Planet(planetID, 200, 200, planetRadius, timeToControl, timeToRespawn, 0, 2));
	    	
	        /* mobs = new Stack<Mob>();
	        for (int i = 0; i < planets.size(); i++){
	            addMobsToPlanet(i);
	        }*/
	    	
	    	
	    	Setp setupConfig = new Setp(mobs, planets, 1);
	    	messageServer.SendTo(1, utils.CreateSetupConfig(setupConfig));
	    	
	    	setupConfig.receiverID = 2;
	    	messageServer.SendTo(2, utils.CreateSetupConfig(setupConfig));
	    	
	    }

	    /**
	     * Generate mobs on planet(Server part!)
	     * @param planetID - size planet's
	     */
	    private void addMobsToPlanet(int planetID){
	    	
	    	for (int i = 0; i< planets.size(); i++)
	    	{
	    		if (planets.get(i).getID() == planetID)
	    		{
	    			double angle = (2*Math.PI)/size*i;
	 	            float radius = planets.get(i).getFigure().radius + mobRadius;
	 	            float posX = (float) (planets.get(i).getFigure().x + radius*Math.cos(angle));
	 	            float posY = (float) (planets.get(i).getFigure().y + radius*Math.sin(angle));
	 	            mobs.add(new Mob(i, posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planets.get(i).getOwnerID()));
	 	            return;
	    		}
	    	}
	    	
	    	System.out.println("Wrong planetID: "+planetID);
	    }

	    /**
	     * Update method
	     * @param delta - delta time
	     */
	    public void update(float delta) {
	    	deltaUpdate = new Delt();
	    	
	    	
	        for (int i = 0; i< planets.size(); i++){	
	        	
	            if(planets.get(i).getTimeToRespawn() >= 0){
	                planets.get(i).setTimeToRespawn(planets.get(i).getTimeToRespawn() - delta);
	            } else
	            {
	                respawnToPlanet(planets.get(i));
	                planets.get(i).setTimeToRespawn(timeToRespawn);
	            }
	            if(onlyEnemiesNearPlanet(planets.get(i)))
	            {
	                if(planets.get(i).getOwnerID() != Utils.NEUTRAL_OWNER_ID) 
	                {
	                    if (planets.get(i).getTimeToControl() >= 0) 
	                    {
	                        planets.get(i).setTimeToControl(planets.get(i).getTimeToControl() - delta);
	                    } else 
	                    {
	                        planets.get(i).setOwnerID(Utils.NEUTRAL_OWNER_ID);
	                        planets.get(i).setTimeToControl(5);
	                    }
	                } else
	                {
	                    if(whoIsInvader(planets.get(i)) != Utils.NEUTRAL_OWNER_ID)
	                    {
	                        if (planets.get(i).getTimeToControl() >= 0) 
	                        {
	                            planets.get(i).setTimeToControl(planets.get(i).getTimeToControl() - delta);
	                        } else 
	                        {
	                            planets.get(i).setOwnerID(whoIsInvader(planets.get(i)));
	                            planets.get(i).setTimeToControl(5);
	                        }
	                    }
	                }
	            } else 
	            {
	                moving(planets.get(i));
	            }
	        }
	        for (Mob mob: mobs){
	            if(mob.getReloadTime() >= 0){
	                mob.setReloadTime(mob.getReloadTime() - delta);
	            } else{
	                for(Mob attackMob: mobs){
	                    if(attackMob.getFigure().overlaps(new Circle(mob.getFigure().x, mob.getFigure().y, mob.getAtackRadius())) && attackMob.getOwnerID() != mob.getOwnerID()) {
	                       
	                        attackMob.setHP(attackMob.getHP() - mob.getDamage());
	                        mob.setReloadTime(reloadTime);
	                        if(attackMob.getHP() < 0){
	                        	deltaUpdate.mobs.add(attackMob);
	                            mobs.remove(attackMob);
	                            return;
	                        }
	                    }
	                }
	            }
	            moving(mob);
	        }
	        
	        String text = utils.CreateDeltaUpdate(deltaUpdate);
	        text = utils.DeleteSpaces(text);
	        messageServer.addToOutputQueue(text);
	        
	        
	    }

	    private void respawnToPlanet(Planet planet){
	        float radius = planet.getFigure().radius + mobRadius;
	        Boolean isAdded = false;
	        while(!isAdded) {
	            if(planet.getNumberMobs() >= size){
	                radius += mobRadius;
	            }
	            double angle = (2 * Math.PI) / size * planet.getNumberMobs();
	            float posX = (float) (planet.getFigure().x + radius * Math.cos(angle));
	            float posY = (float) (planet.getFigure().y + radius * Math.sin(angle));
	            for(Mob mob: mobs){
	                if(mob.getFigure().overlaps(new Circle(posX, posY, mobRadius))){
	                    planet.setNumberMobs(planet.getNumberMobs() + 1);
	                    break;
	                }
	            }
	            isAdded = true;
	            Mob newMob = new Mob(utils.GetNewMobID(), posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planet.getOwnerID());
	            mobs.add(newMob);
	            deltaUpdate.mobs.add(newMob);
	        }

	    }

	 
	    private Boolean onlyEnemiesNearPlanet(Planet planet){
	        int count = 0;
	        for(Mob mob: mobs){
	            if(mob.getFigure().overlaps(new Circle(planet.getFigure().x, planet.getFigure().y, 2*planet.getFigure().radius)) && mob.getOwnerID() == planet.getOwnerID()){
	                return false;
	            } else if(mob.getFigure().overlaps(new Circle(planet.getFigure().x, planet.getFigure().y, 2*planet.getFigure().radius)) && mob.getOwnerID() != planet.getOwnerID()){
	                count++;
	            }
	        }
	        if(count > 0){
	            return true;
	        }
	        return false;
	    }

	    /**
	     * If near planet only one type enemy return his name
	     * @param planet - our planet
	     * @return - enemy name
	     */
	    private int whoIsInvader(Planet planet){
	        int invader = Utils.NEUTRAL_OWNER_ID;
	        for (Mob mob: mobs){
	            if(mob.getFigure().overlaps(new Circle(planet.getFigure().x, planet.getFigure().y, 2*planet.getFigure().radius))){
	                if(invader == Utils.NEUTRAL_OWNER_ID){
	                    invader = mob.getOwnerID();
	                } else if(invader !=mob.getOwnerID()){
	                    return Utils.NEUTRAL_OWNER_ID;
	                }
	            }
	        }
	        return invader;
	    }

	    /**
	     * Method for moving to target
	     * @param superFigure - object for moving
	     */
	    private void moving(SuperFigure superFigure) {
	        if (superFigure.getIsMove()) {
	            if(superFigure.getTarget() == null) {
	                if (!superFigure.getFigure().overlaps(new Circle(superFigure.getNewX(), superFigure.getNewY(), superFigure.getFigure().radius))) {
	                    superFigure.getFigure().x += superFigure.getStepX();
	                    superFigure.getFigure().y += superFigure.getStepY();
	                }
	            } else {
	                if (!superFigure.getFigure().overlaps(superFigure.getTarget().getFigure())) {
	                    superFigure.getFigure().x += (superFigure.getTarget().getFigure().x - superFigure.getFigure().x)/200;
	                    superFigure.getFigure().y += (superFigure.getTarget().getFigure().y - superFigure.getFigure().y)/200;
	                }
	            }
	        }
	    }

	    /**
	     * Move object to free point or follow for target
	     * @param newX - target position x
	     * @param newY - target position y
	     * @param target - target
	     */
	    public void moveToPoint(float newX, float newY, SuperFigure target, int[] IDarray){
	        String command;
	        if(target == null){
	            command = "MovM" + (int)newX +"-"+(int)newY;
	        } else{
	            command = "FolM" + (target instanceof Planet?"P":"M") + target.getID();
	        }
	        messageText = "From" + 1 + command +":";
	        Boolean isFirst = true;
	        for(Mob mob: mobs){
	            if(mob.getIsSelected()){
	                if(isFirst){
	                    messageText += mob.getID();
	                    isFirst = false;
	                } else{
	                    messageText += "," + mob.getID();
	                }
	                float k = (newX - mob.getFigure().x)/Math.abs(newY - mob.getFigure().y);
	                mob.setStepX((newX - mob.getFigure().x)/200);
	                mob.setStepY((newY - mob.getFigure().y)/200);
	                mob.setIsSelected(false);
	                mob.setNewX(newX);
	                mob.setNewY(newY);
	                mob.setTarget(target);
	                mob.setIsMove(true);
	            }
	        }
	        System.out.println(messageText);
	    }

	    public void movePlanetToPoint(float newX, float newY, SuperFigure target, int[] IDarray)
	    {
	      
	    	for (int j = 0; j< planets.size(); j++)
	    	{
	    		for (int i = 0; i < IDarray.length; i++)
	        	{	
	        	
	    			if (planets.get(j).getID() == IDarray[i])
	    			{
	    			 	
			    	    float k = (newX - planets.get(j).getFigure().x)/Math.abs(newY - planets.get(j).getFigure().y);
			            planets.get(j).setStepX((newX - planets.get(j).getFigure().x)/200);
			            planets.get(j).setStepY((newY - planets.get(j).getFigure().y)/200);
			            planets.get(j).setIsSelected(false);
			            planets.get(j).setNewX(newX);
			            planets.get(j).setNewY(newY);
			            planets.get(j).setTarget(target);
			            planets.get(j).setIsMove(true);
	    			}
	        	}
	    	}
	        	
        	
	    }


	    /**
	     * Check on hit on object
	     * @param helpCircle - help figure for detect on hit
	     * @return - object or null
	     */

	

}
