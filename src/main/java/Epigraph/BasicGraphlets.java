/**
 * 
 */
package Epigraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Pablo Vicente-Munuera
 *
 */
public class BasicGraphlets {

	/**
	 * This will only be seen by this class and the classes whose inherit from
	 * this one.
	 * 
	 * orbit[x][o] - how many times does node x
	 */
	protected int[][] orbit;

	/**
	 * 
	 */
	public BasicGraphlets() {

	}

	/**
	 * 
	 * @param fileName
	 */
	private void readNdump(String fileName) {
		// File class needed to turn stringName to actual file
		File file = new File(fileName);

		try {
			// read from filePooped with Scanner class
			Scanner inputStream = new Scanner(file);
			// hashNext() loops line-by-line
			while (inputStream.hasNext()) {
				// read single line, put in string
				String data = inputStream.next();
				System.out.println(data + "***");

			}
			// after loop, close scanner
			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
