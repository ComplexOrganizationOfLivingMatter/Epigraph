package epigraph;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.StackWindow;

public class ImageProcessingWindowBis extends JFrame{
	public ImageProcessingWindowBis() {
	}
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ArrayList<GraphletImage> newGraphletImages;
	private GraphletImage newGraphletImage;
	private JTextField tfImageName;
	private CustomWindow customWindow;
	
	public ImageProcessingWindowBis(ImagePlus raw_img, JTableModel tableInfo) {
		
		newGraphletImages = new ArrayList<GraphletImage>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		customWindow = new CustomWindow(raw_img,raw_img.getCanvas());
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		ImagePlus imgToShow = new ImagePlus("", raw_img.getChannelProcessor());
		
		newGraphletImage = new GraphletImage(raw_img);
		
	}
	
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


    /*class CustomStackWindow extends StackWindow implements ActionListener {
    
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
        
    }*/

}
