package express;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class contains the basic i/o routines for reading expresssch/expresspcb files.
 * The files are encrypted with a simple scrambling algorithm, that XORs the data with a pseudo
 * random sequence. The seed for the random number generator is contained in the file. 
 */
public class ExpressIO {
	/**
	 * Random sequence generator seed
	 */
	private int key = 0;

	/**
	 * Sets the seed for the random sequence generator
	 * @param key seed for random generator
	 */
	public void setKey(int key) {
		this.key = key;
	}
	
	/**
	 * Reads and decrypts one byte of data from the input stream
	 * @param fis the input stream to read from
	 * @return one byte of decrypted data
	 * @throws IOException
	 */
	public int read1(FileInputStream fis) throws IOException {
		int l = fis.read();
		key = nextRandomNumber(key);
		l = l ^ key;
		l = l & 0xFF;
		return l;
	}

	/**
	 * Reads and decrypts two bytes of data from the input stream
	 * @param fis the input stream to read from
	 * @return two bytes of decrypted data, packed in an int
	 * @throws IOException
	 */
	public int read2(FileInputStream fis) throws IOException {
		int l1 = fis.read();
		int l2 = fis.read();
		int l = (l2 << 8) | l1;
		key = nextRandomNumber(key);
		l = l ^ key;
		l = l & 0xFFFF;
		return l;
	}

	/**
	 * Reads and decrypts three bytes of data from the input stream
	 * @param fis the input stream to read from
	 * @return three bytes of decrypted data, packed in an int
	 * @throws IOException
	 */
	public int read3(FileInputStream fis) throws IOException {
		int l1 = fis.read();
		int l2 = fis.read();
		int l3 = fis.read();
		int l = (l3 << 16) | (l2 << 8) | l1;
		key = nextRandomNumber(key);
		int k = key << 16;
		key = nextRandomNumber(key);
		k = k | key;
		l = l ^ k;
		l = l & 0xFFFFFF;
		return l;
	}

	/**
	 * Generates the next random number from current
	 * @param key the current random number
	 * @return next random number in sequence 
	 */
	private static int nextRandomNumber(int key) {
		int count = 17;
		while (true) {
			short bits = 0;
			if ((key & 32) != 0)
				bits = 1;

			if ((key & 8) != 0)
				bits++;

			if ((key & 4) != 0)
				bits++;

			if ((key & 1) != 0)
				bits++;

			key = key >> 1;
			if ((bits & 1) != 0)
				key += 32768;
				
			count--;
			if (count == 0)
				break;
		}

		return key;
	}

}
