package ServerPackage;
import java.util.ArrayList;
import java.util.Iterator;

import GameplayPackage.Circle;
import GameplayPackage.Mob;
import GameplayPackage.Planet;
import GameplayPackage.SuperFigure;

public class GameplayServer {
		private MessageServer messageServer;
	    //  private Stack<Planet> planets;
	    private ArrayList<Mob> mobs = new ArrayList<Mob>();
	    private ArrayList<Planet> planets = new ArrayList<Planet>();
	    private float mobRadius, HP, reloadTime, attackRadius, damage;
	    private float planetRadius, timeToControl, timeToRespawn;
	    private int size = 20;
	    private Delt deltaUpdate;
	    public Utils utils;
	    
	    public ArrayList<Mob> getMobs(){
	    	return mobs;
	    }
	    
	    public ArrayList<Planet> getPlanets() {
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
	    	planets.add((new Planet(planetID, 5, 5, planetRadius, timeToControl, timeToRespawn, 1)));
	    	
	    	planetID = utils.GetNewPlanetID();
	    	planets.add(new Planet(planetID, 200, 200, planetRadius, timeToControl, timeToRespawn, 2));
	    	
	        mobs = new ArrayList<Mob>();
	        for (int i = 0; i < planets.size(); i++){
	            addMobsToPlanet(i);
	        }
	        
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
	    	for (int i = 0; i< size; i++){
	    		double angle = (2*Math.PI)/size*i;
	 	        float radius = planets.get(planetID).getFigure().radius + mobRadius;
	 	        float posX = (float) (planets.get(planetID).getFigure().x + radius*Math.cos(angle));
	 	        float posY = (float) (planets.get(planetID).getFigure().y + radius*Math.sin(angle));
	 	        mobs.add(new Mob(mobs.size() + 50, posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planets.get(planetID).getOwnerID()));
	    	}
	    }

	    /**
	     * Update method
	     * @param delta - delta time
	     */
	    public void update(float delta) {
	    	deltaUpdate = new Delt();
	    	
	        for (Planet planet : planets) {
	            planet.update(this, delta);
	            if(planet.isNewMobRespawn()){
	                respawnToPlanet(planet);
	            }
	            planet.setInvader(whoIsInvader(planet));
	            if(planet.isNewOwner()){
	            	deltaUpdate.planets.add(planet);
	            }
	        }
	        Iterator<Mob> iter = mobs.iterator();
	        while (iter.hasNext()) {
	        	Mob mob = iter.next();
	            if(mob.isRemove()){
	            	deltaUpdate.mobs.add(mob);
	                iter.remove();
	            } else {
	                mob.update(this, delta);
	            }
	        }
	        
	        String text = utils.CreateDeltaUpdate(deltaUpdate);
	        text = utils.DeleteSpaces(text);
	        messageServer.addToOutputQueue(text);
	    }

	    private void respawnToPlanet(Planet planet){
	        float radius = planet.getFigure().radius + 2*mobRadius;
	        boolean isAdded = false;
	        int number = 0;
	        while(!isAdded) {
	            if(number%size==0){
	                radius += 4*mobRadius;
	            }
	            isAdded = true;
	            double angle = (2 * Math.PI) / size * number;
	            float posX = (float) (planet.getFigure().x + radius * Math.cos(angle));
	            float posY = (float) (planet.getFigure().y + radius * Math.sin(angle));
	            for(Mob mob: mobs){
	                if(mob.getFigure().overlaps(new Circle(posX, posY, mobRadius))){
	                    number++;
	                    isAdded = false;
	                    break;
	                }
	            }
	            if(isAdded) {
	            	Mob newMob = new Mob(mobs.size(), posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planet.getOwnerID());
	            	deltaUpdate.mobs.add(newMob);
	                mobs.add(newMob);
	            }
	        }
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
	                if(mob.getOwnerID() == planet.getOwnerID()){
	                    return Utils.NEUTRAL_OWNER_ID;
	                }
	                if(invader == Utils.NEUTRAL_OWNER_ID){
	                    invader = mob.getOwnerID();
	                } else if(invader != mob.getOwnerID()){
	                    return Utils.NEUTRAL_OWNER_ID;
	                }
	            }
	        }
	        return invader;
	    }
	    
	    public float getTimeToControl() {
			return timeToControl;
		}
	    
	    public float getTimeToRespawn() {
			return timeToRespawn;
		}
	    
	    public float getReloadTime() {
			return reloadTime;
		}

	    /**
	     * Move object to free point or follow for target
	     * @param newX - target position x
	     * @param newY - target position y
	     * @param target - target
	     */
	    public void moveToPoint(float newX, float newY, SuperFigure target, int[] IDarray){
	        for(Mob mob: mobs){
	            if(mob.getIsSelected()){
		    		for (int i = 0; i < IDarray.length; i++){	
		    			mob.setNextPosition(newX, newY, target);
		    		}
	            }
	        }
	    }

	    public void movePlanetToPoint(float newX, float newY, SuperFigure target, int[] IDarray){
	        for(Planet planet: planets){
	    		for (int i = 0; i < IDarray.length; i++){	
		        	if (planet.getID() == IDarray[i]){
		        		planet.setNextPosition(newX, newY, target);
		        	}
	    		}
	    	}
	    }
}
