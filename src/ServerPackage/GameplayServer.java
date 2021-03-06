package ServerPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import GameplayPackage.Circle;
import GameplayPackage.Mob;
import GameplayPackage.Planet;

public final class GameplayServer {
		private MessageServer messageServer;
	    //  private Stack<Planet> planets;
	    private Map<Integer, Mob> mobs = new HashMap<Integer, Mob>();
	    private Map<Integer, Planet> planets;
	    private int[] playerValue;
        private ArrayList<Integer> selectedID = new ArrayList<Integer>();
	    private float mobRadius, HP, reloadTime, attackRadius, damage;
	    private float planetRadius, timeToControl, timeToRespawn;
	    private float deltaTime;
	    private int size = 20;
	    public Utils utils;
	    private long time,ping1,ping2;
	    
	    private Timer myTimer = new Timer(); // ������� ������
	    
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
			   System.out.println("Player 1 ping = "+ping1);
		   }else if( ID == 2)
		   {
			   ping2 = System.currentTimeMillis() - time;
		   } else
		   {
			   System.out.println("SetPing: wrong id: "+ID);
			   System.out.println("Player 2 ping = "+ping2);
		   }
		   
	   }
	   
	   public float getPlanetRadius() {
		return planetRadius;
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
	    	deltaTime = 0;
	        //Mob's variables
	        mobRadius = 5;
	        HP = 100;
	        reloadTime = 5;
	        attackRadius = 50;
	        damage = 50;

	        //Planet's variables
	        planetRadius = 30;
	        timeToControl = 5;
	        timeToRespawn = 10;
	        
	        playerValue = new int[2];
	        for(int i = 0; i < playerValue.length; i++){
	        	playerValue[i] = 0;
	        }
	    }

	    /**
	     * Start actions(Server part!)
	     */
	    public void startGame(){
	    	utils = new Utils();
	    	ArrayList<Add> add = new ArrayList<Add>();
	    	planets = new HashMap<Integer, Planet>();
	    	int planetID = utils.GetNewPlanetID();
	    	planets.put(planetID, new Planet(200, 200, planetRadius, timeToControl, timeToRespawn, 1));
	    	add.add(new Add(planetID, 1, 200, 200, planetRadius));
	    	
	    	planetID = utils.GetNewPlanetID();
	    	planets.put(planetID, new Planet(600, 200, planetRadius, timeToControl, timeToRespawn, 2));
	    	add.add(new Add(planetID, 2, 600, 200, planetRadius));

	    	planetID = utils.GetNewPlanetID();
	    	planets.put(planetID, new Planet(300, 400, planetRadius, timeToControl, timeToRespawn, Utils.NEUTRAL_OWNER_ID));
	    	add.add(new Add(planetID, Utils.NEUTRAL_OWNER_ID, 300, 400, planetRadius));

	    	planetID = utils.GetNewPlanetID();
	    	planets.put(planetID, new Planet(300, 100, planetRadius, timeToControl, timeToRespawn, Utils.NEUTRAL_OWNER_ID));
	    	add.add(new Add(planetID, Utils.NEUTRAL_OWNER_ID, 300, 100, planetRadius));
	    	
	        for (Planet planet: planets.values()){
	        	for (int i = 0; i < size; i++){
		    		double angle = (2*Math.PI)/size*i;
		 	        float radius = planet.getFigure().radius + 2*mobRadius;
		 	        float posX = (float) (planet.getFigure().x + radius*Math.cos(angle));
		 	        float posY = (float) (planet.getFigure().y + radius*Math.sin(angle));
		 	        mobs.put(mobs.size() + 50, new Mob(posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planet.getOwnerID()));
		 	        add.add(new Add(mobs.size() + 50, planet.getOwnerID(), posX, posY, mobRadius));
		    	}
	        }

	    	StartConfiguration setupConfig = new StartConfiguration(add, 1, 1024, 600);
	    	
	    	messageServer.SendTo(1, utils.createOutputString(setupConfig));
	    	
	    	setupConfig.receiverID = 2;
	    	messageServer.SendTo(2, utils.createOutputString(setupConfig));
	    	
	    	time = System.currentTimeMillis();
	    	messageServer.SendTo(1, utils.createOutputString(new PingCommand(1)));
	    	
	    	myTimer.schedule(new timerUpdate(), 0, 33);
	    }
	    
	    /**
	     * Update method
	     * @param delta - delta time
	     */
	    public void update(float delta) {
	        for(int i = 0; i < playerValue.length; i++){
	        	playerValue[i] = 0;
	        }
	        for (Map.Entry<Integer, Planet> planet: planets.entrySet()) {
	            planet.getValue().update(this, delta);

	            if(planet.getValue().isNewMobRespawn()){
	                respawnToPlanet(planet.getValue());
	            }
	            planet.getValue().setInvader(whoIsInvader(planet.getValue()));
	            
	            if(planet.getValue().isNewOwner()){
	            	messageServer.addToOutputQueue(utils.createOutputString(new NewOwner(planet.getKey(), planet.getValue().getOwnerID())));
	            	planet.getValue().setNewOwner(false);
	            }
	            if(planet.getValue().getOwnerID() != Utils.NEUTRAL_OWNER_ID){
	            	playerValue[planet.getValue().getOwnerID()-1]++;
	            }
	        }
	        
	        Iterator<Map.Entry<Integer, Mob>> iterator = mobs.entrySet().iterator();
	        while (iterator.hasNext())
	        {
	        	Entry<Integer, Mob> mob = iterator.next();
	            if(mob.getValue().isRemove()){
	            	messageServer.addToOutputQueue(utils.createOutputString(new Remove(mob.getKey())));
	                iterator.remove();
	            } else {
	                if(mob.getValue().isAttack()){
	                	mob.getValue().setAttack(false);
	                	messageServer.addToOutputQueue(utils.createOutputString(new Attack(mob.getValue().getAttackedMob(), mob.getKey())));
	                }
	                mob.getValue().update(this, delta);
	            }
	            if(mob.getValue().getOwnerID() != Utils.NEUTRAL_OWNER_ID){
	            	playerValue[mob.getValue().getOwnerID()-1]++;
	            }
	        }
	        deltaTime += delta;
	        if(deltaTime >= 2){
	        	deltaTime = 0;
	        	ArrayList<Integer> id = new ArrayList<>();
	            ArrayList<Float> xPosition = new ArrayList<>();
	            ArrayList<Float> yPosition = new ArrayList<>();
		        for (Map.Entry<Integer, Planet> planet: planets.entrySet()) {
		        	id.add(planet.getKey());
		        	xPosition.add(planet.getValue().getFigure().x);
		        	yPosition.add(planet.getValue().getFigure().y);
		        }
		        
		        for (Map.Entry<Integer, Mob> mob: mobs.entrySet()) {
		        	id.add(mob.getKey());
		        	xPosition.add(mob.getValue().getFigure().x);
		        	yPosition.add(mob.getValue().getFigure().y);
	        	}
		        
	            messageServer.addToOutputQueue(utils.createOutputString(new DeltaUpdate(id, xPosition, yPosition)));
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
	            	messageServer.addToOutputQueue(utils.createOutputString(new Add(id, planet.getOwnerID(), posX, posY, mobRadius)));
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
	        int invader = -1;
	        for (Mob mob: mobs.values()){
	            if(mob.getFigure().overlaps(new Circle(planet.getFigure().x, planet.getFigure().y, 2*planet.getFigure().radius))){
	                if(planet.getOwnerID() == Utils.NEUTRAL_OWNER_ID){
	                	if(invader == -1){
	                		invader = mob.getOwnerID();
	                	} else if(invader != mob.getOwnerID()){
		            		return -1;
		            	}
	                } else {
	                	if(mob.getOwnerID() != planet.getOwnerID()){
	                		return Utils.NEUTRAL_OWNER_ID;
	                	}
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
	    	selectedID.clear();
	    }
	    
	    public void setSelectedID(ArrayList<Integer> selectedID) {
			this.selectedID = selectedID;
		}
}
