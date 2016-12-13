package main.java.Epigraph;

/**
 * @author Pablo Vicente-Munuera
 *
 */
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;



public class Epigraph implements PlugIn {
	
	/** image to be used in the training */
	private ImagePlus raw_img = null;
	
//	private class CustomWindow extends StackWindow
//	{
//		
//	}
	
	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = Epigraph.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length() - "classes".length());
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}

	public Epigraph() 
	{
	}
	
	
	
	/**
	 * Plugin run method
	 */
	public void run(String arg)
	{

		// instantiate segmentation backend
		
//
		//get current image
		if (null == WindowManager.getCurrentImage())
		{
			raw_img = IJ.openImage();
			if (null == raw_img) return; // user canceled open dialog
		}
		else
		{
			raw_img = WindowManager.getCurrentImage().duplicate();
			raw_img.setSlice( 
					WindowManager.getCurrentImage().getSlice() );
		}
		
		GraphletImage graphletImage = new GraphletImage(raw_img);
		
		
		
//		wekaSegmentation.setTrainingImage(trainingImage);
		
//		// The display image is a copy of the training image (single image or stack)
//		displayImage = trainingImage.duplicate();
//		displayImage.setSlice( trainingImage.getSlice() );
//		displayImage.setTitle( Weka_Segmentation.PLUGIN_NAME + " " + Weka_Segmentation.PLUGIN_VERSION );

//		ij.gui.Toolbar.getInstance().setTool(ij.gui.Toolbar.FREELINE);
//
//		//Build GUI
//		SwingUtilities.invokeLater(
//				new Runnable() {
//					public void run() {
////						win = new CustomWindow(displayImage);
////						win.pack();
//					}
//				});
	}
	
	

}