package ServerPackage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import GameplayPackage.Circle;
import GameplayPackage.Mob;
import GameplayPackage.Planet;
import GameplayPackage.SuperFigure;

public final class GameplayServer {
		private MessageServer messageServer;
	    //  private Stack<Planet> planets;
	    private Map<Integer, Mob> mobs = new HashMap<Integer, Mob>();
	    private Map<Integer, Planet> planets;
        private ArrayList<Integer> selectedID = new ArrayList<Integer>();
	    private float mobRadius, HP, reloadTime, attackRadius, damage;
	    private float planetRadius, timeToControl, timeToRespawn;
	    private int size = 20;
	    private Delt deltaUpdate;
	    public Utils utils;
	    private long time,ping1,ping2;
	    
	    private Timer myTimer = new Timer(); // Создаем таймер
	    
	   private final class timerUpdate extends TimerTask {

			@Override
			public void run() 
			{
				update(0.033f);
			}
	    }
	   
	   public void SetPing(int ID)
	   {
		   if(ID == 1)
		   {
			   ping1 = System.currentTimeMillis() - time;
		   }else if( ID == 2)
		   {
			   ping2 = System.currentTimeMillis() - time;
		   } else
		   {
			   System.out.println("SetPing: wrong id: "+ID);
		   }
	   }
	    
	    public Map<Integer, Mob> getMobs(){
	    	return mobs;
	    }
	    
	    public Map<Integer, Planet> getPlanets() {
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
	        timeToRespawn = 10;
	        
	     
	    }

	    /**
	     * Start actions(Server part!)
	     */
	    public void startGame(){
	    	utils = new Utils();
	        
	    	planets = new HashMap<Integer, Planet>();
	    	int planetID = utils.GetNewPlanetID();
	    	planets.put(planetID, new Planet(200, 200, planetRadius, timeToControl, timeToRespawn, 1));
	    	
	    	planetID = utils.GetNewPlanetID();
	    	planets.put(planetID, new Planet(600, 200, planetRadius, timeToControl, timeToRespawn, 2));
	    	
	        for (Map.Entry<Integer, Planet> planet: planets.entrySet()){
	            addMobsToPlanet(planet.getKey());
	        }
	        
	    	Setp setupConfig = new Setp(mobs, planets, 1);
	    	messageServer.SendTo(1, utils.CreateSetupConfig(setupConfig));
	    	
	    	setupConfig.receiverID = 2;
	    	messageServer.SendTo(2, utils.CreateSetupConfig(setupConfig));
	    	
	    	time = System.currentTimeMillis();
	    	messageServer.SendTo(1, utils.CreatePing(1));
	    	
	    	myTimer.schedule(new timerUpdate(), 0, 33);
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
	 	        mobs.put(mobs.size() + 50, new Mob(posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planets.get(planetID).getOwnerID()));
	    	}
	    }

	    /**
	     * Update method
	     * @param delta - delta time
	     */
	    public void update(float delta) {
	    	//System.out.println("прошло 0.03 секунды и сервер до сих пор жив");
	    	
	    	deltaUpdate = new Delt();
	    	
	        for (Map.Entry<Integer, Planet> planet: planets.entrySet()) {
	            planet.getValue().update(this, delta);

	            if(planet.getValue().isNewMobRespawn()){
	                respawnToPlanet(planet.getValue());
	            }
	            planet.getValue().setInvader(whoIsInvader(planet.getValue()));
	            
	            if(planet.getValue().isNewOwner()){
	            	planet.getValue().setNewOwner(false);
	            	deltaUpdate.planets.put(planet.getKey(), planet.getValue());
	            }
	        }

	        for(Map.Entry<Integer, Mob> mob: mobs.entrySet()){
	            if(mob.getValue().isRemove()){
	            	deltaUpdate.mobs.put(mob.getKey(), mob.getValue());
	                mobs.remove(mob.getKey());
	            } else {
	                mob.getValue().update(this, delta);
	            }
	        }
	        
	        if (deltaUpdate.mobs.size()>0 || deltaUpdate.planets.size()>0)
	        {
	        	String text = utils.CreateDeltaUpdate(deltaUpdate);
	 	        text = utils.DeleteSpaces(text);
	 	        messageServer.addToOutputQueue(text);	
	        }
	    }

	    private void respawnToPlanet(Planet planet){
	    
	    	planet.resetMobRestpawn();
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

	            for(Map.Entry<Integer, Mob> mob: mobs.entrySet()){
	                if(mob.getValue().getFigure().overlaps(new Circle(posX, posY, mobRadius))){
	                    number++;
	                    isAdded = false;
	                    break;
	                }
	            }
	            
	            if(isAdded) {
	            	Mob newMob = new Mob(posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planet.getOwnerID());
	            	int id = utils.GetNewMobID();
	            	deltaUpdate.mobs.put(id, newMob);
	                mobs.put(id, newMob);
	            }
	        }
	    }

	    /**
	     * If near planet only one type enemy return his name
	     * @param planet - our planet
	     * @return - enemy name
	     */
	    private int whoIsInvader(Planet planet){
	        int invader = planet.getOwnerID();
	        for (Mob mob: mobs.values()){
	            if(mob.getFigure().overlaps(new Circle(planet.getFigure().x, planet.getFigure().y, 2*planet.getFigure().radius))){
	                if(mob.getOwnerID() == planet.getOwnerID()){
	                    return planet.getOwnerID();
	                }
	                if(invader == Utils.NEUTRAL_OWNER_ID){
	                    invader = mob.getOwnerID();
	                } else if(invader != mob.getOwnerID()){
	                    return planet.getOwnerID();
	                }
	            }
	        }
	        return invader;
	    }
	    
	    public Delt getDeltaUpdate() {
			return deltaUpdate;
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
	    public void moveToPoint(float newX, float newY, int targetID, boolean itIsMine){
	    	for(Integer id: selectedID){
	    		if(mobs.containsKey(id)){
	    			mobs.get(id).setIsSelected(true);
	    			mobs.get(id).setNextPosition(newX, newY, targetID);
	    		} else if(planets.containsKey(id)){
	    			planets.get(id).setIsSelected(true);
	    			planets.get(id).setNextPosition(newX, newY, targetID);
	    		}
	    	}
	    }
	    
	    public void setSelectedID(ArrayList<Integer> selectedID) {
			this.selectedID = selectedID;
		}
}
