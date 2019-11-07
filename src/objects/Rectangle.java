package objects;

/**
 * A rectangle object
 */
public class Rectangle extends CircuitObject {
	/**
	 * Right edge
	 */
	public final int x2;
	
	/**
	 * Bottom edge
	 */
	public final int y2;
	
	public Rectangle(final int x1, final int y1, final int x2, final int y2) {
		super(x1, y1);
		this.x2 = x2;
		this.y2 = y2;
	}
}
