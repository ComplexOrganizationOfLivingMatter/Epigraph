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
	JPanelModel mainWindow;

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
		// Build GUI
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainWindow = new JPanelModel();
				//mainWindow.createAndShowGUI();
			}
		});

		// //get current image
		// if (null == WindowManager.getCurrentImage())
		// {
		// raw_img = IJ.openImage();
		// if (null == raw_img) return; // user canceled open dialog
		// }
		// else
		// {
		// raw_img = WindowManager.getCurrentImage().duplicate();
		// raw_img.setSlice(
		// WindowManager.getCurrentImage().getSlice() );
		// }
		//
		// //System.out.println(gddh)
		// ExcelClass ec=new ExcelClass();
		// ec.importData("D:/Pedro/Graphlet/pruebas exportar
		// u3d/TotalParcial_3Ddimensions_test.xls");
		//
		//
		// for (int i = 0; i < ec.getB().size(); i++) {
		// System.out.println(ec.getGddh().get(i));
		// }
		//
		// ec.exportData("D:/Pedro/Graphlet/pruebas exportar
		// u3d/Export_test.xls");

		// GraphletImage graphletImage = new GraphletImage(raw_img);

	}

}