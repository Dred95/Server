package GameplayPackage;
import java.io.Serializable;

public class Circle implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public float x, y;
    public float radius;

    /** Constructs a new circle with all values set to zero */
    public Circle () {

    }

    /** Constructs a new circle with the given X and Y coordinates and the given radius.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param radius The radius of the circle */
    public Circle (float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    /** Sets a new location and radius for this circle.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param radius Circle radius */
    public void set (float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }


    /** Sets a new location and radius for this circle, based upon another circle.
     *
     * @param circle The circle to copy the position and radius of. */
    public void set (Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.radius = circle.radius;
    }

    /** Sets the x and y-coordinates of circle center
     * @param x The x-coordinate
     * @param y The y-coordinate */
    public void setPosition (float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** Sets the x-coordinate of circle center
     * @param x The x-coordinate */
    public void setX (float x) {
        this.x = x;
    }

    /** Sets the y-coordinate of circle center
     * @param y The y-coordinate */
    public void setY (float y) {
        this.y = y;
    }

    /** Sets the radius of circle
     * @param radius The radius */
    public void setRadius (float radius) {
        this.radius = radius;
    }

    /** Checks whether or not this circle contains a given point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return true if this circle contains the given point. */
    public boolean contains (float x, float y) {
        x = this.x - x;
        y = this.y - y;
        return x * x + y * y <= radius * radius;
    }

    /** @param c the other {@link Circle}
     * @return whether this circle contains the other circle. */
    public boolean contains (Circle c) {
        final float dx = x - c.x;
        final float dy = y - c.y;
        final float dst = dx * dx + dy * dy;
        final float radiusDiff = radius - c.radius;
        final float radiusSum = radius + c.radius;
        return (!(radiusDiff * radiusDiff < dst) && (dst < radiusSum * radiusSum));
    }

    /** @param c the other {@link Circle}
     * @return whether this circle overlaps the other circle. */
    public boolean overlaps (Circle c) {
        float dx = x - c.x;
        float dy = y - c.y;
        float distance = dx * dx + dy * dy;
        float radiusSum = radius + c.radius;
        return distance < radiusSum * radiusSum;
    }

    /** Returns a {@link String} representation of this {@link Circle} of the form {@code x,y,radius}. */
    @Override
    public String toString () {
        return x + "," + y + "," + radius;
    }

    @Override
    public boolean equals (Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Circle c = (Circle)o;
        return this.x == c.x && this.y == c.y && this.radius == c.radius;
    }


}
