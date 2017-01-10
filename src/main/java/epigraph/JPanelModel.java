package epigraph;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;

import ij.IJ;
import ij.ImagePlus;

/**
 * @author Pedro Gomez-Galvez
 * 
 * TableDemo is just like SimpleTableDemo, except that it uses a custom
 * TableModel.
 */
public class JPanelModel extends JPanel {
  private boolean DEBUG = false;
  private ArrayList<GraphletImage> allGraphletImages;

  public JPanelModel() {
    super(new GridLayout(1, 0));
    
    JPanel panel = new JPanel();
    panel.setBorder(new LineBorder(new Color(0, 0, 0)));
    add(panel);
    
    //Create buttons
    JButton btnVisualize = new JButton("Visualize");
    btnVisualize.setBounds(342, 255, 93, 29);
    btnVisualize.setBackground(Color.yellow);
    
    
    JButton btnOpenButton = new JButton("Open");
    btnOpenButton.setBounds(15, 255, 71, 29);
	btnOpenButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						ImagePlus raw_img = IJ.openImage();
						ImageWindow imageProcessing = new ImageWindow(raw_img);
						imageProcessing.setVisible(true);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	});
    panel.setLayout(null);
    
	//Create table and scroll pane  
    JTable table = new JTable(new JTableModel());
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
  //Set up renderer and editor for the Favorite Color column.
    table.setDefaultRenderer(Color.class,
                             new ColorRenderer(true));
    table.setDefaultEditor(Color.class,
            new JColorEditor());
    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBounds(15, 27, 420, 196);
    //scrollPane.setPreferredSize(new Dimension(400, 200));
    
    
    
    panel.add(scrollPane);
    panel.add(btnOpenButton);
    panel.add(btnVisualize);
    
    
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  public void createAndShowGUI() {
    //Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    JFrame frame = new JFrame("TableDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    JPanelModel newContentPane = new JPanelModel();
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setSize(500, 400);
    frame.setVisible(true);
  }
  
  /**
   * 
   * @param newImages
   */
  public void addNewImagesProcessed(ArrayList<GraphletImage> newImages){
	  allGraphletImages.addAll(newImages);
  }
}
