package GameplayPackage;

import java.util.Map;

import ServerPackage.GameplayServer;

/**
 * Class for mob(point)
 */
public class Mob extends SuperFigure {
    private float HP;
    private int attackedMob;
    private float damage;
    private float reloadTime;
    private float atackRadius;
    private boolean isRemove;
    private boolean isAttack;

    /**
     * Constructor
     * @param x - xPosition
     * @param y - yPosition
     * @param radius - radius
     * @param HP - HP's mob
     * @param ownerID - owner id
     */
    public Mob(float x, float y, float radius, float HP, float reloadTime, float atackRadius, float damage, int ownerID) {
        super(x, y, radius, ownerID);
        this.HP = HP;
        this.damage = damage;
        this.atackRadius = atackRadius;
        attackedMob = -1;
        isAttack = false;
    }

    @Override
    public void update(GameplayServer gameWorld, float delta) {
        if(reloadTime >= 0){
            reloadTime -= delta;
        } else{
        	if(attackedMob == -1){
                for(Map.Entry<Integer, Mob> attackMob: gameWorld.getMobs().entrySet()){
	                if(attackMob.getValue().getFigure().overlaps(new Circle(getFigure().x, getFigure().y, getAtackRadius())) && attackMob.getValue().getOwnerID() != getOwnerID()) {
	                    isAttack = true;
	                	attackedMob = attackMob.getKey();
	                	attackMob.getValue().setHP(attackMob.getValue().getHP() - getDamage());
	                    reloadTime = gameWorld.getReloadTime();
	                    if(attackMob.getValue().getHP() < 0){
	                        attackMob.getValue().setRemove(true);
	                    }
	                }
	            }
        	} else if(gameWorld.getMobs().containsKey(attackedMob)) {
        		if(gameWorld.getMobs().get(attackedMob).getFigure().overlaps(new Circle(getFigure().x, getFigure().y, atackRadius))) {
        			isAttack = true;
        			gameWorld.getMobs().get(attackedMob).setHP(gameWorld.getMobs().get(attackedMob).getHP() - getDamage());
                    reloadTime = gameWorld.getReloadTime();
                    if(gameWorld.getMobs().get(attackedMob).getHP() < 0){
                    	gameWorld.getMobs().get(attackedMob).setRemove(true);
                    }
                } else{
        			isAttack = true;
        			attackedMob = -1;
                }
        	} else {
    			isAttack = true;
    			attackedMob = -1;
        	}
        }
        super.update(gameWorld, delta);
    }

    /**
     * Getter for HP mob
     * @return - current HP
     */
    public float getHP() {
        return HP;
    }

    /**
     * Setter for HP mob
     * @param HP - HP mob
     */
    public void setHP(float HP) {
        this.HP = HP;
    }

    /**
     * Getter for damage
     * @return - gamage
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Getter for radius attack
     * @return - radius attack
     */
    public float getAtackRadius() {
        return atackRadius;
    }

    /**
     * Getter for reload weapon time
     * @return - reload time
     */
    public float getReloadTime() {
        return reloadTime;
    }

    /**
     * Setter for reload weapon time
     * @param reloadTime - reload time
     */
    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }
    
    public void setRemove(boolean isRemove) {
		this.isRemove = isRemove;
	}
    
    public boolean isRemove() {
		return isRemove;
	}
    
    public int getAttackedMob() {
		return attackedMob;
	}
    
    public void setAttackedMob(int attackerMob) {
		this.attackedMob = attackerMob;
	}
    
    public boolean isAttack() {
		return isAttack;
	}
    
    public void setAttack(boolean isAttack) {
		this.isAttack = isAttack;
	}
}
