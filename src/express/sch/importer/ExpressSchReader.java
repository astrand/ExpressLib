package express.sch.importer;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import express.ExpressConstants;
import express.ExpressIO;
import express.sch.objects.Circle;
import express.sch.objects.Circuit;
import express.sch.objects.CircuitObject;
import express.sch.objects.CompoundObject;
import express.sch.objects.Line;
import express.sch.objects.Pin;
import express.sch.objects.Rectangle;
import express.sch.objects.Sheet;
import express.sch.objects.Text;
import express.sch.objects.TextAttributes;
import express.sch.objects.Wire;

/**
 * This class reads a file that was created with expresssch.exe and returns its
 * contents as a tree of objects.
 */
public class ExpressSchReader implements ExpressConstants {
	/**
	 * Debugging flag
	 */
	private static final boolean DEBUG = false;

	/**
	 * The low level i/o system
	 */
	private ExpressIO io = new ExpressIO();

	/**
	 * Components of components
	 */
	protected Map subComponents;
	
	/**
	 * Edges in wires
	 */
	protected Map edges;
	
	/**
	 * Connections between edges
	 */
	protected Map connections;
	
	/**
	 * Components on a sheet
	 */
	protected List components;
	
	/**
	 * The current sheet
	 */
	protected Sheet currentSheet;

	/**
	 * Reads a .s file and returns a Component object representing the contents
	 * of that file
	 * 
	 * @param file
	 *            the file to read
	 * @return the contents of the file
	 * @throws IOException
	 */
	public synchronized CompoundObject readCompoundObject(File file) throws IOException {
		return readCompoundObject(new FileInputStream(file));
	}

	/**
	 * Reads the contents of a .s file from the stream and returns a Component object representing the contents
	 * of that stream
	 * 
	 * @param is
	 *            the contents of a component file
	 * @return the contents of the file
	 * @throws IOException
	 */
	public synchronized CompoundObject readCompoundObject(InputStream is) throws IOException {
		// read header
		readHeader(is);
		// read random seed
		initIO(is);

		// reset temporary values
		reset();

		// create a dummy sheet
		currentSheet = new Sheet("temp", 10000, 10000);

		try {
			// read values until exception occurs
			readComponents(is);
		} catch (UnsupportedOperationException ex) {
		}

		// return the one component on the sheet
		return (CompoundObject) currentSheet.getChildAt(0);
	}

	/**
	 * Reads a .sch file and returns a tree of objects representing the contents
	 * of that file
	 * 
	 * @param file
	 *            the file to read
	 * @return the contents of the file
	 * @throws IOException
	 */
	public synchronized Circuit readFile(File file) throws IOException {
		// open file
		FileInputStream fis = new FileInputStream(file);

		// read header
		readHeader(fis);
		// read random seed
		initIO(fis);

		// skip some more data
		// purpose also unknown
		for (int i = 0; i < 4; i++)
			fis.read();

		// reset temporary values
		reset();

		// read components
		return readComponents(fis);
	}

	/**
	 * Resets internal values.
	 */
	protected void reset() {
		// reset temporary values
		subComponents = new HashMap();
		edges = new HashMap();
		connections = new HashMap();
		components = new ArrayList();
		currentSheet = null;
	}

	/**
	 * Initialises the random generator with the seed from the file.
	 * 
	 * @param fis
	 *            InputStream from which to read
	 * @throws IOException
	 */
	protected void initIO(InputStream fis) throws IOException {
		// read and initialise random seed
		// stored in file at position 27+28
		int c1 = fis.read();
		int c2 = fis.read();
		int key = ((c2 << 8) & 0xFF00) + (c1 & 0xFF);
		io.setKey(key);
	}

	/**
	 * Reads the header data of the file. Currently the contents of the header
	 * are not understood so this method only skips 26 bytes.
	 * 
	 * @param fis
	 *            InputStream from which to read
	 * @throws IOException
	 */
	protected void readHeader(InputStream fis) throws IOException {
		// skip header
		// purpose unknown
		for (int i = 0; i < 26; i++)
			fis.read();
	}

	/**
	 * Reads the component data from the file and return the file contents in a
	 * Circuit object.
	 * 
	 * @param fis
	 *            InputStream from which to read
	 * @return the file contents in a Circuit object
	 * @throws IOException
	 */
	protected Circuit readComponents(InputStream fis) throws IOException {
		// create root node
		Circuit circuit = new Circuit();

		// read data from file until finished
		while (true) {
			if (DEBUG) {
				try {
					System.out.println("stream position: " + ((FileInputStream) fis).getChannel().position());
				} catch(ClassCastException ex) {
				}
			}

			// read identifier of following object
			int objectId = io.read3(fis);
			// read type of following object
			int type = io.read1(fis);

			// probably the termination marker
			if (type == 30) {
				createComponents();
				createWires();
				break;
			}

			// read id of component
			int componentId = io.read3(fis);
			// read sequence number of object within component
			/* int seqNo = */io.read3(fis);
			// not sure what this is for
			io.read1(fis);

			switch (type) {

			// component/symbol
			case 1: {
				if (DEBUG)
					System.out.println("component/symbol");

				// 2=component
				// 3=symbol
				int compType = io.read1(fis);

				// ???
				io.read3(fis);

				// for symbols: netname
				// for components: component name
				int nameLen = io.read1(fis);
				StringBuffer nameBuf = new StringBuffer();
				for (int i = 0; i < nameLen; i++)
					nameBuf.append((char) io.read1(fis));

				// name position, size, style, etc
				int nameInfo = io.read2(fis);
				int nameX = io.read3(fis);
				int nameY = io.read3(fis);

				// component id
				int idLen = io.read1(fis);
				StringBuffer idBuf = new StringBuffer();
				for (int i = 0; i < idLen; i++)
					idBuf.append((char) io.read1(fis));

				// id position, size, style, etc
				int idInfo = io.read2(fis);
				int idX = io.read3(fis);
				int idY = io.read3(fis);

				// order no
				int orderNoLen = io.read1(fis);
				StringBuffer orderNoBuf = new StringBuffer();
				for (int i = 0; i < orderNoLen; i++)
					orderNoBuf.append((char) io.read1(fis));

				// ???
				io.read2(fis);

				// add to temporary list
				ComponentInfo ci = new ComponentInfo(objectId, compType, nameBuf.toString(), nameInfo, nameX, nameY, idBuf.toString(), idInfo, idX, idY,
						orderNoBuf.toString());
				components.add(ci);
				break;
			}

				// pin
			case 3: {
				if (DEBUG)
					System.out.println("pin");

				// ???
				io.read1(fis);

				// position
				int x = io.read3(fis);
				int y = io.read3(fis);

				// pin name
				int nameLen = io.read1(fis);
				StringBuffer pinNameBuf = new StringBuffer();
				for (int i = 0; i < nameLen; i++)
					pinNameBuf.append((char) io.read1(fis));

				// pin position, size, style, etc
				int pinNameInfo = io.read2(fis);
				int pinNameX = io.read3(fis);
				int pinNameY = io.read3(fis);

				// pin no
				int pinNo = io.read2(fis);
				// pin no size, style, etc
				int pinNoInfo = io.read2(fis);
				// pin no position
				int pinNoX = io.read3(fis);
				int pinNoY = io.read3(fis);

				int extLen = io.read1(fis);
				for (int i = 0; i < extLen; i++)
					io.read1(fis);

				// ???
				io.read2(fis);

				// add pin position to list of edges, so wires can be attached
				edges.put(new Integer(objectId), new Point(x, y));

				Text pinNameText = new Text(pinNameX, pinNameY, getAttributes(pinNameInfo), pinNameBuf.toString());
				Text pinNoText = new Text(pinNoX, pinNoY, getAttributes(pinNoInfo), "" + pinNo);
				Pin pin = new Pin(x, y, pinNo, pinNameText, pinNoText);
				currentSheet.add(pin);
				addSubComponent(subComponents, pin, componentId);
				break;
			}

				// edge
			case 4: {
				if (DEBUG)
					System.out.println("edge");

				// ???
				io.read1(fis);

				// read position
				int x = io.read3(fis);
				int y = io.read3(fis);

				// save edge
				Point p = new Point(x, y);
				edges.put(new Integer(objectId), p);
				break;
			}

				// connection
			case 5: {
				if (DEBUG)
					System.out.println("connection");

				// ???
				io.read1(fis);

				// ids of edges at start and end
				int startId = io.read3(fis);
				int endId = io.read3(fis);
				// ???
				io.read1(fis);

				// save connection info
				Point p = new Point(startId, endId);
				connections.put(new Integer(objectId), p);
				break;
			}

				// text
			case 6: {
				if (DEBUG)
					System.out.println("text");

				// ???
				io.read1(fis);

				// read text
				int l = io.read1(fis);
				StringBuffer textBuffer = new StringBuffer();
				for (int i = 0; i < l; i++)
					textBuffer.append((char) io.read1(fis));

				// read position, size, style, etc
				int fontInfo = io.read2(fis);
				int fontSize = fontInfo / 16;
				int direction = fontInfo & 0xF;
				int style = io.read1(fis);
				int x = io.read3(fis);
				int y = io.read3(fis);

				// ???
				io.read1(fis);

				TextAttributes attributes = new TextAttributes(direction, fontSize, style, true);
				Text text = new Text(x, y, attributes, textBuffer.toString());

				currentSheet.add(text);
				addSubComponent(subComponents, text, componentId);
				break;
			}

				// rectangle
			case 7: {
				if (DEBUG)
					System.out.println("rectangle");

				// ???
				io.read1(fis);

				// read edges
				int x1 = io.read3(fis);
				int y1 = io.read3(fis);
				int x2 = io.read3(fis);
				int y2 = io.read3(fis);

				// ???
				io.read2(fis);

				Rectangle rect = new Rectangle(x1, y1, x2, y2);
				currentSheet.add(rect);
				addSubComponent(subComponents, rect, componentId);
				break;
			}

				// line
			case 8: {
				if (DEBUG)
					System.out.println("line");

				// ???
				io.read1(fis);

				// read start and end position
				int x1 = io.read3(fis);
				int y1 = io.read3(fis);
				int x2 = io.read3(fis);
				int y2 = io.read3(fis);

				// ???
				io.read1(fis);

				Line line = new Line(x1, y1, x2, y2);
				currentSheet.add(line);
				addSubComponent(subComponents, line, componentId);
				break;
			}

				// circle/arc
			case 9: {
				if (DEBUG)
					System.out.println("arc");

				// read position, style, size, etc
				int shapeInfo = io.read1(fis);
				int shape = shapeInfo & 0xF;
				int direction = shapeInfo / 32;
				int x = io.read3(fis);
				int y = io.read3(fis);
				int radius = io.read3(fis);

				// ???
				io.read2(fis);

				Circle circle = new Circle(x, y, shape, radius, direction);
				currentSheet.add(circle);
				addSubComponent(subComponents, circle, componentId);
				break;
			}

				// sheet
			case 31: {
				if (DEBUG)
					System.out.println("sheet");

				// create components and wires from temporary data
				createComponents();
				createWires();

				// read sheet name
				int sheetNameLen = io.read1(fis);
				StringBuffer sheetNameBuf = new StringBuffer();
				for (int i = 0; i < sheetNameLen; i++)
					sheetNameBuf.append((char) io.read1(fis));

				// read unidentified data
				io.read3(fis);
				io.read1(fis);
				io.read3(fis);
				io.read3(fis);

				io.read1(fis);
				io.read1(fis);
				io.read3(fis);
				io.read3(fis);
				io.read3(fis);
				io.read2(fis);

				io.read3(fis);
				io.read3(fis);
				io.read3(fis);
				io.read1(fis);
				io.read3(fis);
				io.read3(fis);

				io.read1(fis);
				io.read1(fis);
				io.read3(fis);
				io.read3(fis);
				io.read3(fis);
				io.read2(fis);

				io.read3(fis);
				io.read3(fis);

				io.read3(fis);
				io.read1(fis);

				io.read3(fis);
				io.read3(fis);

				io.read1(fis);
				io.read1(fis);
				io.read3(fis);

				// read size of sheet
				int sizeX = io.read3(fis);
				int sizeY = io.read3(fis);

				// some more unidentified data
				io.read2(fis);

				io.read3(fis);
				io.read3(fis);
				io.read3(fis);
				io.read1(fis);
				io.read3(fis);
				io.read3(fis);

				io.read1(fis);
				io.read1(fis);
				io.read3(fis);
				io.read3(fis);
				io.read3(fis);
				io.read2(fis);

				io.read3(fis);
				io.read3(fis);

				currentSheet = new Sheet(sheetNameBuf.toString(), sizeX, sizeY);
				circuit.add(currentSheet);
				break;
			}

			default:
				createComponents();
				createWires();
				throw new UnsupportedOperationException("Unknown object type " + type);
			}
		}

		// close file
		fis.close();

		return circuit;
	}

	/**
	 * Creates a component or symbol on the given sheet
	 */
	protected void createComponents() {
		// process each object
		for (Iterator it = components.iterator(); it.hasNext();) {
			// get the stored information
			ComponentInfo ci = (ComponentInfo) it.next();

			// get the list objects that form the component/symbol
			List componentList = (List) subComponents.get(new Integer(ci.objectId));

			if (ci.compType == COMPONENT_TYPE_COMPONENT) {
				// create attributes for component
				Text partIdText = new Text(ci.idX, ci.idY, getAttributes(ci.idInfo), ci.id);
				Text partNameText = new Text(ci.nameX, ci.nameY, getAttributes(ci.nameInfo), ci.name);
				Text orderNoText = new Text(0, 0, null, ci.orderNo);

				// create component
				currentSheet.groupComponent(componentList, partIdText, partNameText, orderNoText);
			} else if (ci.compType == COMPONENT_TYPE_SYMBOL) {
				// create attribute for symbol
				Text netNameText = new Text(ci.nameX, ci.nameY, getAttributes(ci.nameInfo), ci.name);

				// create symbol
				currentSheet.groupSymbol(componentList, netNameText);
			}

			// remove component from active map
			subComponents.remove(new Integer(ci.objectId));
		}
		// clean up
		components.clear();
	}

	/**
	 * Creates single wire objects from the collection of edge and the stored
	 * connection information
	 */
	protected void createWires() {
		// go over each stored connection
		for (Iterator it = connections.keySet().iterator(); it.hasNext();) {
			Object objKey = it.next();

			// find the ids of start/end edge
			Point con = (Point) connections.get(objKey);
			Point p1 = (Point) edges.get(new Integer(con.x));
			Point p2 = (Point) edges.get(new Integer(con.y));

			// create the wire
			Wire wire = new Wire(p1.x, p1.y, p2.x, p2.y);
			currentSheet.add(wire);
		}

		// clean up
		edges.clear();
		connections.clear();
	}

	/**
	 * Stores an object for later creation
	 * 
	 * @param comps
	 *            map of components and their sub-objects
	 * @param c
	 *            the object to store
	 * @param componentId
	 *            the id of component the object belongs to
	 */
	private void addSubComponent(Map comps, CircuitObject c, int componentId) {
		// objects that are not grouped have a null id
		if (componentId == 0)
			return;

		// create a new list or add to existing
		Integer id = new Integer(componentId);
		List l = (List) comps.get(id);
		if (l == null) {
			l = new ArrayList();
			comps.put(id, l);
		}

		if (!l.contains(c))
			l.add(c);
	}

	/**
	 * Creates a TextAttribute object from the bit-info in the file
	 * 
	 * @param attrInfo
	 *            the bit-info from the file
	 * @return the TextAttribute object
	 */
	private TextAttributes getAttributes(int attrInfo) {
		boolean visible = (attrInfo & 8) != 0;
		int direction = (attrInfo & 3);
		int fontSize = (attrInfo / 16);

		return new TextAttributes(direction, fontSize, FONT_PLAIN, visible);
	}

	// ------------------------------------------------

	// temporary storage
	class ComponentInfo {
		public int objectId;

		public int compType;
		public String name;
		public int nameInfo;
		public int nameX;
		public int nameY;

		public String id;
		public int idInfo;
		public int idX;
		public int idY;

		public String orderNo;

		public ComponentInfo(int objectId, int compType, String name, int nameInfo, int nameX, int nameY, String id, int idInfo, int idX, int idY,
				String orderNo) {
			this.objectId = objectId;
			this.compType = compType;
			this.name = name;
			this.nameInfo = nameInfo;
			this.nameX = nameX;
			this.nameY = nameY;
			this.id = id;
			this.idInfo = idInfo;
			this.idX = idX;
			this.idY = idY;
			this.orderNo = orderNo;
		}
	}
}
