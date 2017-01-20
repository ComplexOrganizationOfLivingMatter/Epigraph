/**
 * epigraph
 */
package epigraph;

import javax.swing.SwingUtilities;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * @author Pablo Vicente-Munuera
 *
 */
public class Epigraph implements PlugIn {

	/** image to be used in the training */
	MainWindow mainWindow;

	/**
	 * 
	 */
	public Epigraph() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins
		// menu
		Class<?> clazz = Epigraph.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(),
				url.length() - clazz.getName().length() - ".class".length() - "classes".length());
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}

	/**
	 * Plugin run method
	 */
	public void run(String arg) {
		ij.gui.Toolbar.getInstance().setTool(ij.gui.Toolbar.POINT);
		// Build GUI
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainWindow = new MainWindow();
				mainWindow.createAndShowGUI();
			}
		});
	}
	
	public static void callToolbarPoint(){
		ij.gui.Toolbar.getInstance().setTool(ij.gui.Toolbar.POINT);
	}

}