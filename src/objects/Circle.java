package objects;

/**
 * Circle/Arc object
 */
public class Circle extends CircuitObject {
	/**
	 * Shape of the circle/arc
	 * @see express.ExpressConstants 
	 */
	public final int shape;
	
	/**
	 * Radius of the circle/arc in 1/1000"
	 */
	public final int radius;

	/**
	 * Direction of the circle/arc
	 * @see express.ExpressConstants 
	 */
	public final int direction;
	
	public Circle(final int x, final int y, final int shape, final int radius, final int direction) {
		super(x, y);
		this.shape = shape;
		this.radius = radius;
		this.direction = direction;
	}
}
