package express.sch.objects;

/**
 * A text object
 */
public class Text extends CircuitObject {
	/**
	 * Attributes of the text object (font size, style, etc)
	 */
	public final TextAttributes attributes;
	
	/**
	 * The text
	 */
	public final String text;
	
	public Text(final int x, final int y, final TextAttributes attributes, final String text) {
		super(x, y);
		this.attributes = attributes;
		this.text = text;
	}
}
