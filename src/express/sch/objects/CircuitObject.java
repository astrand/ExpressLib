package express.sch.objects;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * An object on a sheet or in a component/symbol
 */
public class CircuitObject extends DefaultMutableTreeNode {
	/**
	 * X position of the object on the sheet in 1/1000"
	 */
	public final int x;

	/**
	 * Y position of the object on the sheet in 1/1000"
	 */
	public final int y;

	public CircuitObject(final int x, final int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return getClass().getName();
	}
}
