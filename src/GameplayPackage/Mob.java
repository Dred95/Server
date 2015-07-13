package GameplayPackage;

/**
 * Class for mob(point)
 */
public class Mob extends SuperFigure {
    private float HP;
    private float damage;
    private float reloadTime;
    private float atackRadius;

    /**
     * Constructor
     * @param x - xPosition
     * @param y - yPosition
     * @param radius - radius
     * @param HP - HP's mob
     * @param ownerID - owner id
     */
    public Mob(int ID, float x, float y, float radius, float HP, float reloadTime, float atackRadius, float damage, int ownerID) {
        super(ID, x, y, radius, ownerID);
        this.HP = HP;
        this.damage = damage;
        this.atackRadius = atackRadius;
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
}
