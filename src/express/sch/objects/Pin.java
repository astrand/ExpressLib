package express.sch.objects;

/**
 * A pin object
 */
public class Pin extends CircuitObject {
	/**
	 * Pin number numeric
	 */
	public final int pinNo;
	
	/**
	 * The pin name
	 */
	public final Text pinNameText;
	
	/**
	 * The pin no
	 */
	public final Text pinNoText;
	
	public Pin(final int x, final int y, final int pinNo, final Text pinNameText, final Text pinNoText) {
		super(x, y);
		this.pinNo = pinNo;
		this.pinNameText = pinNameText;
		this.pinNoText = pinNoText;
	}
}
