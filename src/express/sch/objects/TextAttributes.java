package express.sch.objects;

/**
 * Attributes of a text object
 */
public class TextAttributes {
	/**
	 * The direction of the text
	 * @see express.ExpressConstants
	 */
	public final int direction;

	/**
	 * The size of the text font in 1/1000"
	 */
	public final int size;

	/**
	 * The style of the text font
	 * @see express.ExpressConstants
	 */
	public final int style;
	
	/**
	 * Visible flag of the text object
	 */
	public final boolean visible;
	
	public TextAttributes(final int direction, final int size, final int style, final boolean visible) {
		this.direction = direction;
		this.size = size;
		this.style = style;
		this.visible = visible;
	}
}
