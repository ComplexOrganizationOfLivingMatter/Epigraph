package epigraph;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

import org.scijava.ui.DialogPrompt;

import ij.IJ;
import ij.ImagePlus;

/**
 * @author Pedro Gomez-Galvez
 * 
 *         TableDemo is just like SimpleTableDemo, except that it uses a custom
 *         TableModel.
 */
public class MainWindow extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean DEBUG = false;
	JTableModel tableInfo;
	private JScrollPane scrollPane;
	private JTable table;
	private JPanel panel;
	private JButton btnVisualize;
	private JButton btnOpenButton;

	public MainWindow() {
		super(new GridLayout(1, 0));

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel);

		// Create buttons
		btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				VisualizingWindow visualizingWindow = new VisualizingWindow(tableInfo);
				visualizingWindow.setVisible(true);
			}
		});
		btnVisualize.setBounds(342, 255, 93, 29);

		btnOpenButton = new JButton("Open");
		btnOpenButton.setBounds(15, 255, 71, 29);
		btnOpenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ImagePlus raw_img = IJ.openImage();
							if (raw_img != null) {
								ImageProcessingWindow imageProcessing = new ImageProcessingWindow(raw_img, tableInfo);
								imageProcessing.setVisible(true);
							} else {
								JOptionPane.showMessageDialog(panel.getParent(), "You must introduce a valid image or set of images.");
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		panel.setLayout(null);

		// Create table and scroll pane
		this.tableInfo = new JTableModel();
		table = new JTable(tableInfo);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new JColorEditor());
		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(15, 27, 420, 196);
		// scrollPane.setPreferredSize(new Dimension(400, 200));

		panel.add(scrollPane);
		panel.add(btnOpenButton);
		panel.add(btnVisualize);
		
		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExcelClass excelclass = new ExcelClass();				
				ArrayList<String> arrayNames=new ArrayList<String>();
				ArrayList<Float> arrayHexagons=new ArrayList<Float>();
				ArrayList<Float> arrayGDDH=new ArrayList<Float>();
				ArrayList<Float> arraGDDRV=new ArrayList<Float>();
				ArrayList<Float> arrayR=new ArrayList<Float>();
				ArrayList<Float> arrayG=new ArrayList<Float>();
				ArrayList<Float> arrayB=new ArrayList<Float>();
				
				for (BasicGraphletImage graphletImg:tableInfo.getAllGraphletImages()){
					
					arrayNames.add(graphletImg.getLabelName());
					arrayHexagons.add(graphletImg.getPercentageOfHexagons());
					arrayGDDH.add(graphletImg.getDistanceGDDH());
					arraGDDRV.add(graphletImg.getDistanceGDDH());
					arrayR.add((float) graphletImg.getColor().getRed());
					arrayG.add((float) graphletImg.getColor().getGreen());
					arrayB.add((float) graphletImg.getColor().getBlue());

				}			
				excelclass.setR(arrayR);
				excelclass.setG(arrayG);
				excelclass.setB(arrayB);
				excelclass.setImageName(arrayNames);
				excelclass.setGddh(arrayGDDH);
				excelclass.setGddrv(arraGDDRV);
				excelclass.setHexagonsPercentage(arrayHexagons);

				JFrame parentFrame = new JFrame();
				 
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Save data excel");   
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
			    fileChooser.setFileFilter(filter);
			    fileChooser.setAcceptAllFileFilterUsed(false); 
				
				int userSelection = fileChooser.showSaveDialog(parentFrame);
				 
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    File fileToSave = fileChooser.getSelectedFile();
				    System.out.println("Save as file: " + fileToSave.getAbsolutePath() + ".xls");
				    excelclass.exportData(fileToSave.getAbsolutePath().toString() + ".xls");
				}
				
				
				
			}
		});
		btnExport.setBounds(240, 255, 87, 29);
		panel.add(btnExport);
		
		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser chooser = new JFileChooser();
			    chooser.setCurrentDirectory(new java.io.File("."));
			    chooser.setDialogTitle("choosertitle");
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
			    chooser.setFileFilter(filter);
			    chooser.setAcceptAllFileFilterUsed(false);

			    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			      System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
			      System.out.println("getSelectedFile() : " + chooser.getSelectedFile().getPath());
			      
			      ExcelClass excelclass = new ExcelClass();
			      excelclass.importData(chooser.getSelectedFile().getPath());
			      
			      for(int row = 0;row<excelclass.getImageName().size()-1;row++){
//			    	  tableInfo.setValueAt(new Color((float) excelclass.getRow(row+1).get(4),(float) excelclass.getRow(row+1).get(5),(float) excelclass.getRow(row+1).get(6)), row, 0);
//			    	  tableInfo.setValueAt(excelclass.getRow(row+1).get(0), row, 1);//label name
//			    	  tableInfo.setValueAt(excelclass.getRow(row+1).get(1), row, 2);//ggdh
//			    	  tableInfo.setValueAt(excelclass.getRow(row+1).get(2), row, 3);//gddrv
//			    	  tableInfo.setValueAt(excelclass.getRow(row+1).get(3), row, 4);//hexagons
//			    	  tableInfo.setValueAt(true, row, 5);//visualize default
			    	  tableInfo.addImage(new BasicGraphletImage((float) excelclass.getRow(row+1).get(2),(float) excelclass.getRow(row+1).get(1),(float) excelclass.getRow(row+1).get(3), new Color((float) excelclass.getRow(row+1).get(4),(float) excelclass.getRow(row+1).get(5),(float) excelclass.getRow(row+1).get(6)),(String) excelclass.getRow(row+1).get(0)));
			      }
			      
			      
			      
			    } else {
			      System.out.println("No Selection ");
			    }
				
				
				
			}
		});
		btnImport.setBounds(101, 255, 81, 29);
		panel.add(btnImport);

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public void createAndShowGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("Epigraph");

		// Create and set up the content pane.
		MainWindow newContentPane = new MainWindow();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setSize(500, 400);
		frame.setVisible(true);
	}
}
