package epigraph;

import ij.plugin.*;
import ij.*;
import ij.gui.*;
import ij.process.*;
import java.awt.*;
import java.awt.image.*;

import javax.swing.JOptionPane;

import java.awt.event.*;

/**
     Adds a panel containing "Invert" and "Flip" buttons to the current image.
     If no images are open, creates a blank 400x300 byte image and adds
     the panel to it.
*/
public class Panel_Window implements PlugIn {

    static final int WIDTH = 400;
    static final int HEIGHT = 300;

    public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins
		// menu
		Class<?> clazz = Panel_Window.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(),
				url.length() - clazz.getName().length() - ".class".length() - "classes".length());
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
    
    public void run(String arg) {
        
    	try {
			ImagePlus imp = IJ.openImage();
			if (imp==null) {
	            ImageProcessor ip = new ByteProcessor(WIDTH, HEIGHT);
	            ip.setColor(Color.white);
	            ip.fill();
	            imp = new ImagePlus("Panel Window", ip);
	        }
	        CustomCanvas cc = new CustomCanvas(imp);
	        if (imp.getStackSize()>1)
	            new CustomStackWindow(imp, cc);
	        else
	           new CustomWindow(imp, cc);
	        cc.requestFocus();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
        
        
        
    }


    class CustomCanvas extends ImageCanvas {
    
        CustomCanvas(ImagePlus imp) {
            super(imp);
        }
    
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            IJ.log("mousePressed: ("+offScreenX(e.getX())+","+offScreenY(e.getY())+")");
        }
    
    } // CustomCanvas inner class
    
    
    class CustomWindow extends ImageWindow implements ActionListener {
    
        private Button button1, button2;
       
        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
            Panel panel = new Panel();
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            button1 = new Button(" Invert ");
            button1.addActionListener(this);
            panel.add(button1);
            button2 = new Button(" Flip ");
            button2.addActionListener(this);
            panel.add(button2);
            add(panel);
            pack();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Point loc = getLocation();
            Dimension size = getSize();
            if (loc.y+size.height>screen.height)
                getCanvas().zoomOut(0, 0);
         }
      
        public void actionPerformed(ActionEvent e) {
            Object b = e.getSource();
            if (b==button1) {
                imp.getProcessor().invert();
                imp.updateAndDraw();
            } else {
                imp.getProcessor().flipVertical();
                imp.updateAndDraw();
            }
            ImageCanvas ic = imp.getCanvas();
            if (ic!=null)
                ic.requestFocus();
        }
        
    } // CustomWindow inner class


    class CustomStackWindow extends StackWindow implements ActionListener {
    
        private Button button1, button2;
       
        CustomStackWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
       }
    
        void addPanel() {
            Panel panel = new Panel();
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            button1 = new Button(" Invert ");
            button1.addActionListener(this);
            panel.add(button1);
            button2 = new Button(" Flip ");
            button2.addActionListener(this);
            panel.add(button2);
            add(panel);
            pack();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Point loc = getLocation();
            Dimension size = getSize();
            if (loc.y+size.height>screen.height)
                getCanvas().zoomOut(0, 0);
         }
      
        public void actionPerformed(ActionEvent e) {
            Object b = e.getSource();
            if (b==button1) {
                imp.getProcessor().invert();
                imp.updateAndDraw();
            } else {
                imp.getProcessor().flipVertical();
                imp.updateAndDraw();
            }
            ImageCanvas ic = imp.getCanvas();
            if (ic!=null)
                ic.requestFocus();
         }
        
    } // CustomStackWindow inner class

} // Panel_Window class