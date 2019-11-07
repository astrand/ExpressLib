package objects;

import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A sheet of a circuit
 */
public class Sheet extends DefaultMutableTreeNode {
	/**
	 * Name of the sheet
	 */
	public final String name;
	
	/**
	 * Width of the sheet in 1/1000"
	 */
	public final int width;

	/**
	 * Height of the sheet in 1/1000"
	 */
	public final int height;
	
	public Sheet(final String name, final int width, final int height) {
		super();
		this.name = name;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Groups a list of objects to a component
	 * @param objects the objects to be contained in the new component
	 * @param partId the part id of the new component
	 * @param partName the part name of the new component
	 * @param orderNo the order no of the new component
	 * @return the new component
	 */
	public Component groupComponent(List objects, final Text partId, final Text partName, final Text orderNo) {
		Component group = new Component(partId, partName, orderNo);
		for(Iterator it=objects.iterator(); it.hasNext();) {
			CircuitObject o = (CircuitObject) it.next();
			remove(o);
			group.add(o);
		}
		add(group);
		return group;
	}
	
	/**
	 * Groups a list of objects to a symbol
	 * @param objects the objects to be contained in the new symbol
	 * @param netName the netname of the new symbol
	 * @return the new symbol
	 */
	public Symbol groupSymbol(List objects, final Text netName) {
		Symbol group = new Symbol(netName);
		for(Iterator it=objects.iterator(); it.hasNext();) {
			CircuitObject o = (CircuitObject) it.next();
			remove(o);
			group.add(o);
		}
		add(group);
		return group;
	}
	
	public String toString() {
		return "Sheet '" + name + "'";
	}
}
