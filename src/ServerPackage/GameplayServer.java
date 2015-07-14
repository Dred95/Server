package ServerPackage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import GameplayPackage.Circle;
import GameplayPackage.Mob;
import GameplayPackage.Planet;
import GameplayPackage.SuperFigure;

public final class GameplayServer {
		private MessageServer messageServer;
	    //  private Stack<Planet> planets;
	    private ArrayList<Mob> mobs = new ArrayList<Mob>();
	    private ArrayList<Planet> planets = new ArrayList<Planet>();
	    private float mobRadius, HP, reloadTime, attackRadius, damage;
	    private float planetRadius, timeToControl, timeToRespawn;
	    private int size = 20;
	    private Delt deltaUpdate;
	    public Utils utils;
	    
	    private Timer myTimer = new Timer(); // Создаем таймер
	    
	   private final class timerUpdate extends TimerTask {

			@Override
			public void run() 
			{
				update(0.033f);
			}
	    }
	    
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

	     
	    }

	    /**
	     * Start actions(Server part!)
	     */
	    public void startGame(){
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
	 	        mobs.add(new Mob(mobs.size() + 50, posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planets.get(planetID).getOwnerID()));
	    	}
	    }

	    /**
	     * Update method
	     * @param delta - delta time
	     */
	    public void update(float delta) {
	    	//System.out.println("прошло 0.03 секунды и сервер до сих пор жив");
	    	
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
	            for(Mob mob: mobs){
	                if(mob.getFigure().overlaps(new Circle(posX, posY, mobRadius))){
	                    number++;
	                    isAdded = false;
	                    break;
	                }
	            }
	            
	            if(isAdded) {
	            	Mob newMob = new Mob(utils.GetNewMobID(), posX, posY, mobRadius, HP, reloadTime, attackRadius, damage, planet.getOwnerID());
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
	    public void moveToPoint(float newX, float newY, SuperFigure target, ArrayList<Integer> IDarray){
	    	for(Mob mob: mobs)
	    	{
		    	if(IDarray.contains(mob.getID()))
		    	{
		    		mob.setNextPosition(newX, newY, target);
		    	}
	    	}
	    	for(Planet planet: planets)
	    	{
		    	if (IDarray.contains(planet.getID()))
		    	{
		    		planet.setNextPosition(newX, newY, target);
		    	}
	    	}
	    }
}
