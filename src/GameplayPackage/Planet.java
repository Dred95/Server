package GameplayPackage;

import ServerPackage.GameplayServer;
import ServerPackage.Utils;

/**
 * Class for planet
 */
public class Planet extends SuperFigure{
    private float timeToControl;
    private float timeToRespawn;
    private int invader;
    private boolean isNewOwner;
    private boolean isNewMobRespawn;

    /**
     * Constructor
     * @param x - xPosition
     * @param y - yPosition
     * @param radius - radius
     * @param hostName - player who owns the object
     */
    public Planet(int ID, float x, float y, float radius, float timeToControl, float timeToRespawn, int ownerID) {
        super(ID, x, y, radius, ownerID);
        this.timeToControl = timeToControl;
        this.timeToRespawn = timeToRespawn;
        isNewMobRespawn = false;
        isNewOwner = false;
    }
    
    @Override
    public void update(GameplayServer gameWorld, float delta) {
        if (getOwnerID() != 0) {
            if (timeToRespawn >= 0) {
                timeToRespawn -= delta;
            } else {
                isNewMobRespawn = true;
                timeToRespawn = gameWorld.getTimeToRespawn();
                System.out.println("до респауна "+ timeToRespawn);
            }
        }
        if (invader != Utils.NEUTRAL_OWNER_ID) {
            if (getOwnerID() != Utils.NEUTRAL_OWNER_ID) {
                if (timeToControl >= 0) {
                    timeToControl -= delta;
                } else {
                    setIsMove(false);
                    isNewOwner = true;
                    setOwnerID(Utils.NEUTRAL_OWNER_ID);;
                    timeToControl = gameWorld.getTimeToControl();
                }
            } else {
                if (getTimeToControl() >= 0) {
                    timeToControl -= delta;
                } else {
                    setOwnerID(invader);
                    isNewOwner = false;
                    timeToControl = gameWorld.getTimeToControl();
                }
            }
        }

        super.update(gameWorld, delta);
    }

    /**
     * Time for grab planet
     * @return - left time
     */
    public float getTimeToControl() {
        return timeToControl;
    }

    /**
     * Time for grab planet
     * @param timeToControl - time to grab
     */
    public void setTimeToControl(float timeToControl) {
        this.timeToControl = timeToControl;
    }

    /**
     * Getter for time of respawn mob on planet
     * @return - time of respawn
     */
    public float getTimeToRespawn() {
        return timeToRespawn;
    }

    /**
     * Setter for time of respawn mob on planet
     * @param timeToRespawn - new time of respawn
     */
    public void setTimeToRespawn(float timeToRespawn) {
        this.timeToRespawn = timeToRespawn;
    }

    public boolean isNewMobRespawn() {
        return isNewMobRespawn;
    }
    
    public void resetMobRestpawn()
    {
    	isNewMobRespawn = false;
    }

    public void setInvader(int invader) {
		this.invader = invader;
	}
    
    public boolean isNewOwner() {
		return isNewOwner;
	}
}
