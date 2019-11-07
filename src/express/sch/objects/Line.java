package express.sch.objects;

/**
 * A line object
 */
public class Line extends CircuitObject {
	/**
	 * X end position in 1/1000"
	 */
	public final int x2;

	/**
	 * Y end position in 1/1000"
	 */
	public final int y2;
	
	public Line(final int x1, final int y1, final int x2, final int y2) {
		super(x1, y1);
		this.x2 = x2;
		this.y2 = y2;
	}
}
