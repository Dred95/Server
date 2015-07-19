package GameplayPackage;

import ServerPackage.GameplayServer;

/**
 * Parent class for game objects
 */
public class SuperFigure {
    private Circle figure;
    private float stepX, stepY;
    private float newX, newY;
    private int ownerID;
    private Boolean isSelected;
    private Boolean isMove;
	private int targetID;

    /**
     * Constructor
     * @param x - xPosition
     * @param y - yPosition
     * @param radius - radius
     * @param ownerID - player's id who owns the object
     */
    public SuperFigure(float x, float y, float radius, int ownerID){
        figure = new Circle(x, y, radius);
        isSelected = false;
        isMove = false;
        this.ownerID = ownerID;
    }
    

    public void update(GameplayServer gameWorld, float delta){
    	if (isMove) {
            if(targetID == -1) {
                if (!figure.overlaps(new Circle(newX, newY, figure.radius))) {
                    figure.x += stepX;
                    figure.y += stepY;
                } else {
                    isMove = false;
                }
            } else {
                Circle targetFigure = gameWorld.getPlanets().containsKey(targetID)?gameWorld.getPlanets().get(targetID).getFigure():gameWorld.getMobs().get(targetID).getFigure();
                if (!figure.overlaps(targetFigure)) {
                    figure.x += (targetFigure.x - figure.x)/200;
                    figure.y += (targetFigure.y - figure.y)/200;
                }
            }
        }
    }

    public void setNextPosition(float newX, float newY, int targetID){
    	if(isSelected){
    		this.targetID = targetID;
            stepX = (newX - figure.x)/200;
            stepY = (newY - figure.y)/200;
            isSelected = false;
            if(targetID == -1) {
                this.newX = newX;
                this.newY = newY;
            } else {
                this.targetID = targetID;
            }
            isMove = true;
        }
    }

    /**
     * Getter for circle
     * @return - circle
     */
    public Circle getFigure() {
        return figure;
    }

    /**
     * Getter for player's name
     * @return - player's name
     */
    public int getOwnerID() {
        return ownerID;
    }

    /**
     * Setter for player's name
     * @param hostID - player's name
     */
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * Setter for isSelelcted
     * @param isSelected - if an object is selected
     */
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * Getter for isSelected
     * @return - if an object is selected
     */
    public Boolean getIsSelected() {
        return isSelected;
    }

    /**
     * Getter for step x
     * @return - step x
     */
    public float getStepX() {
        return stepX;
    }

    /**
     * Setter for step x
     * @param stepX - step x
     */
    public void setStepX(float stepX) {
        this.stepX = stepX;
    }

    /**
     * Getter for step y
     * @return - step y
     */
    public float getStepY() {
        return stepY;
    }

    /**
     * Setter for step y
     * @param stepY - step y
     */
    public void setStepY(float stepY) {
        this.stepY = stepY;
    }

    /**
     * Getter for isMove
     * @return - true if moving
     */
    public Boolean getIsMove() {
        return isMove;
    }

    /**
     * Setter for isMove
     * @param isMove - move or not move
     */
    public void setIsMove(Boolean isMove) {
        this.isMove = isMove;
    }

    /**
     * Getter for target x position
     * @return - target x position
     */
    public float getNewX() {
        return newX;
    }

    /**
     * Setter for target x position
     * @param newX - target x position
     */
    public void setNewX(float newX) {
        this.newX = newX;
    }

    /**
     * Getter for target y position
     * @return - target y position
     */
    public float getNewY() {
        return newY;
    }

    /**
     * Setter for target y position
     * @param newY - target y position
     */
    public void setNewY(float newY) {
        this.newY = newY;
    }
    
    public int getTargetID() {
		return targetID;
	}
    
    public void setTargetID(int targetID) {
		this.targetID = targetID;
	}

}
