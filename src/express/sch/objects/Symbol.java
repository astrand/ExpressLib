package express.sch.objects;

/**
 * Symbol object containing a number of objects that have been group together
 */
public class Symbol extends CompoundObject {
	/**
	 * Netname of the symbol
	 */
	public final Text netName;
	
	public Symbol(final Text netName) {
		this.netName = netName;
	}
}
