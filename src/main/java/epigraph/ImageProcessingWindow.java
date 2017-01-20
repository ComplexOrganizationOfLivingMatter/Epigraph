package epigraph;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import epigraph.ImageProcessingWindow.Task;
import epigraph.Panel_Window.CustomCanvas;
import epigraph.Panel_Window.CustomStackWindow;
import epigraph.Panel_Window.CustomWindow;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.ProgressBar;
import ij.gui.StackWindow;

public class ImageProcessingWindow extends JFrame{
	
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ArrayList<GraphletImage> newGraphletImages;
	private Integer Ibutton1, Ibutton2,IbtnCreateRoi,IbtnCalculateGraphlets,IbtnTestNeighbours,IbtnPickAColor,IbtnAddToTable;
	private GraphletImage newGraphletImage;
	private JTextField tfImageName;
	private CustomWindow customWindow;
	private ImagePlus imgToShow,actualRawImage;
	private JButton button1, button2,btnCreateRoi,btnCalculateGraphlets,btnTestNeighbours,btnPickAColor,btnAddToTable;
  	private JComboBox<String> cbSelectedShape,cbGraphletsMode;
	private JLabel lblRadius,lblImageName,legend,Lsquares,Lpentagons,Lhexagons,Lheptagons,Loctogons;
	private JSpinner inputRadiusNeigh;
	private JPanel colorPicked;
	private JProgressBar progressBar;
	private JTableModel tableInf;

	
	public ImageProcessingWindow(ImagePlus raw_img, JTableModel tableInfo) {
		
		newGraphletImages = new ArrayList<GraphletImage>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		imgToShow = new ImagePlus("", raw_img.getChannelProcessor());
		tableInf=tableInfo;
		
		newGraphletImage = new GraphletImage(raw_img);

		customWindow = new CustomWindow(raw_img,raw_img.getCanvas());
		CustomCanvas cc = new CustomCanvas(raw_img);
        new CustomWindow(raw_img,raw_img.getCanvas());
        cc.requestFocus();
	}
	
	class CustomWindow extends ImageWindow implements ActionListener {
	    
        

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            
            addPanel();
        }
    
        void addPanel() {
            Panel panel = new Panel();
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            
            progressBar= new JProgressBar();
            
            button1 = new JButton(" Invert ");
            button1.addActionListener(this);
            Ibutton1=button1.hashCode();
            panel.add(button1);
            
            button2 = new JButton(" Flip ");
            button2.addActionListener(this);
            Ibutton2=button2.hashCode();
            panel.add(button2);
            
            btnCalculateGraphlets = new JButton("Calculate graphlets!");
            btnCalculateGraphlets.addActionListener(this);
            IbtnCalculateGraphlets=btnCalculateGraphlets.hashCode();
            panel.add(btnCalculateGraphlets);
            
            btnAddToTable = new JButton("add to table");
            btnAddToTable.setEnabled(false);
            IbtnAddToTable=btnAddToTable.hashCode();
            btnAddToTable.addActionListener(this);
            panel.add(btnAddToTable);
            
            btnCreateRoi = new JButton("Create RoI");
            btnCreateRoi.addActionListener(this);
            IbtnCreateRoi=btnCreateRoi.hashCode();
            panel.add(btnCreateRoi);
            
            btnPickAColor = new JButton("Pick a color");
            btnPickAColor.addActionListener(this);
            IbtnPickAColor=btnPickAColor.hashCode();
    		panel.add(btnPickAColor);
    		
    		btnTestNeighbours = new JButton("Test Neighbours");
    		btnTestNeighbours.addActionListener(this);
    		panel.add(btnTestNeighbours);
            IbtnTestNeighbours=btnTestNeighbours.hashCode();
            
            tfImageName = new JTextField();
            panel.add(tfImageName);
            
            add(panel);
            pack();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Point loc = getLocation();
            Dimension size = getSize();
            if (loc.y+size.height>screen.height)
                getCanvas().zoomOut(0, 0);
         }
      
        public void actionPerformed(ActionEvent e) {
        	
        	if(e.getSource()==button1){
        		imp.getProcessor().invert();
                imp.updateAndDraw();
        	}
        	         
        	if(e.getSource()==button2){
				imp.getProcessor().flipVertical();
                imp.updateAndDraw();
        	}
        	if(e.getSource()==btnCalculateGraphlets){
        		if (newGraphletImage.getDistanceGDDH() == -1) {
			        btnTestNeighbours.setEnabled(false);
					btnCalculateGraphlets.setEnabled(false);
					Task task = new Task();
			        task.execute();
					btnAddToTable.setEnabled(true);
				}
				newGraphletImage.setLabelName(tfImageName.getText());
				newGraphletImage.setColor(colorPicked.getBackground());
        	}
        	if(e.getSource()==btnCreateRoi){
				
        	}
        	
        	if(e.getSource()==btnAddToTable){
        		if (tfImageName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(btnAddToTable.getParent(), "You should insert a name for the image");
				} else {
					newGraphletImage.setLabelName(tfImageName.getText());
					int result = JOptionPane.showConfirmDialog(btnAddToTable.getParent(), "Everything is ok?",
							"Confirm", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION)
						tableInf.addImage(newGraphletImage);
				}
        	}
        	if(e.getSource()==btnPickAColor){
        		Color c = JColorChooser.showDialog(btnPickAColor.getParent(), "Choose a Color",
						colorPicked.getBackground());
				if (c != null) {
					colorPicked.setBackground(c);
					if (newGraphletImage != null) {
						newGraphletImage.setColor(c);
					}
				}
        	}
        	if(e.getSource()==btnTestNeighbours){
        		ArrayList<String> polDistri=newGraphletImage.testNeighbours(imp, cbSelectedShape.getSelectedIndex(),(int) inputRadiusNeigh.getValue(), imgToShow, progressBar);
				
				Lsquares.setText(polDistri.get(0));
				Lpentagons.setText(polDistri.get(1));
				Lhexagons.setText(polDistri.get(2));
				Lheptagons.setText(polDistri.get(3));
				Loctogons.setText(polDistri.get(4));
				
				legend.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/legend.jpg")).getImage()));
				//LTitlePoligonDistr.setText("Tested polygon distribution:");
				//canvas.repaint();
        	}
        	
			
            
            
            ImageCanvas ic = imp.getCanvas();
            if (ic!=null)
                ic.requestFocus();
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
    
    }
	
	
	public class Task extends SwingWorker<Void, Void> {

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			setProgress(0);
			newGraphletImage.runGraphlets(actualRawImage, cbSelectedShape.getSelectedIndex(),
					(int) inputRadiusNeigh.getValue(), (int) cbGraphletsMode.getSelectedIndex(), progressBar);
			
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			btnCalculateGraphlets.setEnabled(true);
			btnTestNeighbours.setEnabled(true);
		}
	}
	

	// CustomWindow inner class


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
