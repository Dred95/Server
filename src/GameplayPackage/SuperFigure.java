package GameplayPackage;

import ServerPackage.GameplayServer;

/**
 * Parent class for game objects
 */
public class SuperFigure {
    private Circle figure;
    private float stepX, stepY;
    private float newX, newY;
    private SuperFigure target;
    private int ownerID;
    private Boolean isSelected;
    private Boolean isMove;
    private int ID;

    /**
     * Constructor
     * @param x - xPosition
     * @param y - yPosition
     * @param radius - radius
     * @param ownerID - player's id who owns the object
     */
    public SuperFigure(int ID, float x, float y, float radius, int ownerID){
        this.ID = ID;
       
        figure = new Circle(x, y, radius);
        isSelected = false;
        isMove = false;
        this.ownerID = ownerID;
    }
    

    public void update(GameplayServer gameWorld, float delta){
        moving();
    }

    /**
     * Method for moving to target
     */
    public void moving() {
        if (isMove) {
            if(target == null) {
                if (!figure.overlaps(new Circle(newX, newY, figure.radius))) {
                    figure.x += stepX;
                    figure.y += stepY;
                }
            } else {
                if (!figure.overlaps(target.getFigure())) {
                    figure.x += (target.getFigure().x - figure.x)/200;
                    figure.y += (target.getFigure().y - figure.y)/200;
                }
            }
        }
    }

    public void setNextPosition(float newX, float newY, SuperFigure target){
        if(isSelected){
            stepX = (newX - figure.x)/200;
            stepY = (newY - figure.y)/200;
            isSelected = false;
            if(target == null) {
                this.newX = newX;
                this.newY = newY;
            } else {
                this.target = target;
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

    /**
     * Setter for new target
     * @param target - new target
     */
    public void setTarget(SuperFigure target) {
        this.target = target;
    }

    /**
     * Getter for current target
     * @return - current target
     */
    public SuperFigure getTarget() {
        return target;
    }

    /**
     * Getter for ID
     * @return - ID
     */
    public int getID() {
        return ID;
    }

}
