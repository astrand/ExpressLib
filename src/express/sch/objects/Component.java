package express.sch.objects;

/**
 * Component object containing a number of objects that have been group together
 */
public class Component extends CompoundObject {
	/**
	 * The part id
	 */
	public final Text partId;

	/**
	 * The part name
	 */
	public final Text partName;

	/**
	 * The order no. Note: orderNo does not have TextAttributes
	 */
	public final Text orderNo;
	
	public Component(final Text partId, final Text partName, final Text orderNo) {
		super();
		this.partId = partId;
		this.partName = partName;
		this.orderNo = orderNo;
	}
}
