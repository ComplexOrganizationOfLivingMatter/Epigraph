package epigraph.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.gembox.spreadsheet.CellValueType;
import com.gembox.spreadsheet.ExcelCell;
import com.gembox.spreadsheet.ExcelColumn;
import com.gembox.spreadsheet.ExcelFile;
import com.gembox.spreadsheet.ExcelRow;
import com.gembox.spreadsheet.ExcelWorksheet;
import com.gembox.spreadsheet.SpreadsheetInfo;

import epigraph.BasicGraphletImage;
import epigraph.DiagramsData;
import epigraph.EpiCell;
import epigraph.ExcelClass;
import epigraph.GraphletImage;
import epigraph.JTableModel;
import epigraph.Orca;
import epigraph.GUI.CustomElements.ColorRenderer;
import epigraph.GUI.CustomElements.JColorEditor;
import epigraph.Statistics.Utils;
import ij.IJ;
import ij.ImagePlus;

/**
 * The main window with all its functionality. It will have a table for all of
 * the processed images and 4 buttons: Open: Open image processing window;
 * Import: XLS to the table; Export: Table to XLS; Visualize: Open the
 * visualization window
 * 
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 */
public class MainWindow extends JFrame {
	/**
	 * Default serial. Useless
	 */
	private static final long serialVersionUID = 1L;
	public JTableModel tableInfo;
	private JScrollPane scrollPane;
	private JTable table;
	private JPanel panel;
	private JButton btnVisualize;
	private JButton btnOpenButton;
	private JFrame fatherWindow;
	private JButton btnExport;
	private JButton btnImport;
	private JButton btnSimpleGDD;
	protected VisualizingWindow visualizingWindow;
	private ImageProcessingWindow imageProcessing;
	private JButton btnDeleteRow;
	private JFileChooser fileChooser;
	private DiagramsData diagramsData;
	private String initialDirectory;

	/**
	 * Constructor by default. Setup all the windows and creates the panel. It
	 * initialize all the GUI items as well.
	 */
	public MainWindow() {
		String name = UIManager.getInstalledLookAndFeels()[3].getClassName();
		try {
			UIManager.setLookAndFeel(name);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UIManager.put("Panel.background", Color.WHITE);
		UIManager.put("Slider.background", Color.WHITE);
		fatherWindow = this;
		this.initialDirectory = null;
		setMinimumSize(new Dimension(1200, 600));
		setTitle("EpiGraph");
		// Not close Fiji when Epigraph is closed
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if (imageProcessing != null)
					if (!imageProcessing.isClosed())
						imageProcessing.close();
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// Main panel
		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setLayout(null);
		// Associate this panel to the window
		getContentPane().add(panel);

		initGUIItems();
		

		fileChooser = new JFileChooser();
		
		try {
			diagramsData = new DiagramsData();
		} catch (CloneNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Decimal format for table of main window
	 */
	private class DecimalFormatRenderer extends DefaultTableCellRenderer {
	      private DecimalFormat formatter = new DecimalFormat( "#0.000" );
	 
	      public void set(DecimalFormat formatter){
	    	  this.formatter =formatter;
	      }
	      public Component getTableCellRendererComponent(
	         JTable table, Object value, boolean isSelected,
	         boolean hasFocus, int row, int column) {
	 
	         // First format the cell value as required
	 
	         value = formatter.format((Number)value);
	 
	            // And pass it on to parent class
	 
	         return super.getTableCellRendererComponent(
	            table, value, isSelected, hasFocus, row, column );
	      }
	      
	   }
	
	/**
	 * Initialize the gui items and set up properly within the window
	 */
	private void initGUIItems() {
		btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					visualizingWindow = new VisualizingWindow(fatherWindow, tableInfo);
					visualizingWindow.setVisible(true);
					
					
					visualizingWindow.addWindowListener(new WindowListener(){

						@Override
						public void windowOpened(WindowEvent e) {
							// TODO Auto-generated method stub
							btnVisualize.setEnabled(false);
						}

						@Override
						public void windowClosing(WindowEvent e) {
							// TODO Auto-generated method stub
							btnVisualize.setEnabled(true);
						}

						@Override
						public void windowClosed(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowIconified(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowDeiconified(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowActivated(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowDeactivated(WindowEvent e) {
							// TODO Auto-generated method stub
							
						}
						
					});
					
				}
					
					
				catch (Exception e) {
					String msg = String.format("Unexpected problem: %s", e.getCause().toString());
					JOptionPane.showMessageDialog(panel.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
			
		});

		btnOpenButton = new JButton("Open");
		btnOpenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						initImageProcessingWindow();
					}
				});
			}
		});

		// Create table and scroll pane
		this.tableInfo = new JTableModel();
		table = new JTable(tableInfo);
		table.setRowSelectionAllowed(false);
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new JColorEditor());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setMaxWidth(40); //color
		table.getColumnModel().getColumn(0).setMinWidth(40);
		table.getColumnModel().getColumn(2).setMaxWidth(80);// GDDH
		table.getColumnModel().getColumn(2).setMinWidth(80);
		table.getColumnModel().getColumn(3).setMaxWidth(80);// GDDRV
		table.getColumnModel().getColumn(3).setMinWidth(80);
		table.getColumnModel().getColumn(4).setMinWidth(80);//GDDV5
		table.getColumnModel().getColumn(4).setMaxWidth(80);
		table.getColumnModel().getColumn(5).setMinWidth(75);//% Hexagons
		table.getColumnModel().getColumn(5).setMaxWidth(75);
		table.getColumnModel().getColumn(6).setMinWidth(45);//Radius
		table.getColumnModel().getColumn(6).setMaxWidth(45);
		table.getColumnModel().getColumn(7).setMaxWidth(45);//Shape
		table.getColumnModel().getColumn(7).setMinWidth(45);
		table.getColumnModel().getColumn(8).setMaxWidth(120); //Kind
		table.getColumnModel().getColumn(8).setMinWidth(120);
		table.getColumnModel().getColumn(9).setMaxWidth(60);// Closest diagram
		table.getColumnModel().getColumn(9).setMinWidth(60);
		table.getColumnModel().getColumn(10).setMinWidth(75);// Confidence
		table.getColumnModel().getColumn(10).setMaxWidth(75);
		table.getColumnModel().getColumn(11).setMaxWidth(70); //Select all
		table.getColumnModel().getColumn(11).setMinWidth(70);
		
		 
		
		DecimalFormatRenderer decimalFormat = new DecimalFormatRenderer();
		decimalFormat.setHorizontalAlignment(SwingConstants.LEFT);
		DefaultTableCellRenderer defaultformat = new DefaultTableCellRenderer();
		defaultformat.setHorizontalAlignment(SwingConstants.LEFT);
		

        table.getColumnModel().getColumn(2).setCellRenderer(decimalFormat);
		table.getColumnModel().getColumn(3).setCellRenderer(decimalFormat);
		table.getColumnModel().getColumn(4).setCellRenderer(decimalFormat);
		DecimalFormatRenderer decimalFormat2 = new DecimalFormatRenderer();
		decimalFormat2.set(new DecimalFormat("#0.00"));
		table.getColumnModel().getColumn(5).setCellRenderer(decimalFormat2);
		table.getColumnModel().getColumn(6).setCellRenderer(defaultformat);
		table.getColumnModel().getColumn(9).setCellRenderer(defaultformat);
		table.getColumnModel().getColumn(10).setCellRenderer(decimalFormat2);
		
		table.getTableHeader().setPreferredSize(new Dimension(table.getColumnModel().getTotalColumnWidth(), 35));
		DefaultTableCellRenderer defaultformat3 = new DefaultTableCellRenderer();
		defaultformat3.setHorizontalAlignment(SwingConstants.CENTER);
		table.getTableHeader().setDefaultRenderer(defaultformat3);
		
		// listener
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (table.getRowCount() != 0) {
					int col = table.columnAtPoint(e.getPoint());
					Class<?> classColumn = table.getColumnClass(col);
					String nameClass = classColumn.getName();
					if ("java.lang.Boolean" == nameClass) {
						tableInfo.selectAll();
					}
				}
			}
		});

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		btnExport = new JButton("Export table");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tableInfo.getRowCount()!=0){
					exportTableToXLS();
				}
				
			}
		});

		btnImport = new JButton("Import table");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importXLSToTable(true);
			}
		});
		
		btnSimpleGDD = new JButton("Simple GDD");
		btnSimpleGDD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculateSimpleGDD();
			}
		});

		btnDeleteRow = new JButton("Delete rows");
		btnDeleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean anySelectedRow = false;
				for (Boolean selected : tableInfo.getListOfSelected())
					if (selected) {
						anySelectedRow = true;
						break;
					}

				if (anySelectedRow) {
					int response = JOptionPane.showConfirmDialog(panel, "Are you sure?", "Remove",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (response == JOptionPane.YES_OPTION)
						tableInfo.deleteRow();
				}
			}
		});

		btnSimpleGDD.setBounds(100, 487, 105, 29);
		btnImport.setBounds(336, 487, 105, 29);
		btnExport.setBounds(527, 487, 105, 29);
		btnVisualize.setBounds(703, 487, 93, 29);
		scrollPane.setBounds(26, 30, 1001, 431);
		btnOpenButton.setBounds(188, 487, 71, 29);
		btnDeleteRow.setBounds(1055, 68, 115, 25);

		panel.add(scrollPane);
		panel.add(btnOpenButton);
		panel.add(btnVisualize);
		panel.add(btnSimpleGDD);
		panel.add(btnImport);
		panel.add(btnExport);
		panel.add(btnDeleteRow);

	}
	
	
	
	

	/**
	 * Create the image processing window. However, restrictions are applied
	 * with the selected image. It cannot exceed 3000 neither in width nor
	 * height.
	 */
	public void initImageProcessingWindow() {
		try {
			String filePath = IJ.getFilePath("Choose an image file");
			ImagePlus raw_img = IJ.openImage(filePath);
				
			if (raw_img != null) {
		        if (raw_img.getHeight() >= 3000 || raw_img.getWidth() >= 3000) {
		          JOptionPane.showMessageDialog(panel.getParent(),
		              "Warning! Large image detected. It may take time to process it.");
		        }
		        
		        this.initialDirectory = raw_img.getOriginalFileInfo().directory;
		        imageProcessing = new ImageProcessingWindow(raw_img, tableInfo, diagramsData);
		        imageProcessing.pack();
		
		        imageProcessing.addWindowListener(new WindowListener() {
		
		          @Override
		          public void windowOpened(WindowEvent e) {
		            // TODO Auto-generated method stub
		            btnOpenButton.setEnabled(false);
		          }
		
		          @Override
		          public void windowClosing(WindowEvent e) {
		            // TODO Auto-generated method stub
		            btnOpenButton.setEnabled(true);
		          }
		
		          @Override
		          public void windowClosed(WindowEvent e) {
		            // TODO Auto-generated method stub
		
		          }
		
		          @Override
		          public void windowIconified(WindowEvent e) {
		            // TODO Auto-generated method stub
		
		          }
		
		          @Override
		          public void windowDeiconified(WindowEvent e) {
		            // TODO Auto-generated method stub
		
		          }
		
		          @Override
		          public void windowActivated(WindowEvent e) {
		            // TODO Auto-generated method stub
		
		          }
		
		          @Override
		          public void windowDeactivated(WindowEvent e) {
		            // TODO Auto-generated method stub
		
		          }
		
		        });

			} else {
				JOptionPane.showMessageDialog(panel.getParent(), "You must introduce a valid image or set of images.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export all the information inside the table to a .xls file
	 */
	public void exportTableToXLS() {
		ArrayList<String> arrayNames = new ArrayList<String>();
		ArrayList<Float> arraySquares = new ArrayList<Float>();
		ArrayList<Float> arrayPentagons = new ArrayList<Float>();
		ArrayList<Float> arrayHexagons = new ArrayList<Float>();
		ArrayList<Float> arrayHeptagons = new ArrayList<Float>();
		ArrayList<Float> arrayOctogons = new ArrayList<Float>();
		ArrayList<Float> arrayGDDH = new ArrayList<Float>();
		ArrayList<Float> arrayGDDRV = new ArrayList<Float>();
		ArrayList<Float> arrayGDDV5 = new ArrayList<Float>();
		ArrayList<Float> arrayR = new ArrayList<Float>();
		ArrayList<Float> arrayG = new ArrayList<Float>();
		ArrayList<Float> arrayB = new ArrayList<Float>();
		ArrayList<String> arrayMode = new ArrayList<String>();
		ArrayList<Integer> arrayRadiusOfMask = new ArrayList<Integer>();
		ArrayList<String> arrayShapeOfMask = new ArrayList<String>();
		ArrayList<String> arrayClosestDiagram = new ArrayList<String>();
		ArrayList<Float> arrayDistanceDiagram = new ArrayList<Float>();
		ArrayList<Float> arrayConfidenceDiagram = new ArrayList<Float>();
		
		int cont = 0;
		for (BasicGraphletImage graphletImg : tableInfo.getAllGraphletImages()) {

			arrayNames.add(graphletImg.getLabelName());
			arraySquares.add(graphletImg.getPercentageOfSquares());		
			arrayPentagons.add(graphletImg.getPercentageOfPentagons());
			arrayHexagons.add(graphletImg.getPercentageOfHexagons());
			arrayHeptagons.add(graphletImg.getPercentageOfHeptagons());
			arrayOctogons.add(graphletImg.getPercentageOfOctogons());
			arrayGDDH.add(graphletImg.getDistanceGDDH());
			arrayGDDRV.add(graphletImg.getDistanceGDDRV());
			arrayGDDV5.add(graphletImg.getDistanceGDDV5());
			arrayR.add((float) graphletImg.getColor().getRed());
			arrayG.add((float) graphletImg.getColor().getGreen());
			arrayB.add((float) graphletImg.getColor().getBlue());
			arrayMode.add(tableInfo.getListOfModes().get(cont));
			if (graphletImg.getShapeOfMask() == GraphletImage.CIRCLE_SHAPE) {
				arrayShapeOfMask.add("Circle");
			} else {
				arrayShapeOfMask.add("Square");
			}

			arrayRadiusOfMask.add(graphletImg.getRadiusOfMask());
			
			arrayClosestDiagram.add(Integer.toString(graphletImg.getClosestDiagram()));
			arrayDistanceDiagram.add((float) graphletImg.getDistanceClosestDiagram());
			arrayConfidenceDiagram.add((float) graphletImg.getConfidenceClosestDiagram());
			
			cont++;
		}

		JFrame parentFrame = new JFrame();
		// set it to be a save dialog
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		// set a default filename (this is where you default extension
		// first comes in)
		fileChooser.setSelectedFile(new File(this.initialDirectory.concat("myfile.xls")));
		// Set an extension filter, so the user sees other XML files
		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XLS files", "xls"));

		fileChooser.setAcceptAllFileFilterUsed(false);

		int userSelection = fileChooser.showSaveDialog(parentFrame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {

			String filename = fileChooser.getSelectedFile().toString();
			this.initialDirectory = fileChooser.getSelectedFile().getParentFile().toString().concat(System.getProperty("file.separator"));
			if (!filename.endsWith(".xls"))
				filename += ".xls";

			if ((fileChooser.getSelectedFile() != null) && fileChooser.getSelectedFile().exists()) {
				int response = JOptionPane.showConfirmDialog(this,
						"The file " + fileChooser.getSelectedFile().getName()
								+ " already exists. Do you want to replace the existing file?",
						"Ovewrite file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (response != JOptionPane.YES_OPTION)
					return;
			}

			ExcelClass excelclass = new ExcelClass(filename, arrayNames, arrayGDDH, arrayGDDRV, arrayGDDV5, arrayR,
					arrayG, arrayB, arrayMode, arrayRadiusOfMask, arrayShapeOfMask,arraySquares,arrayPentagons,
					arrayHexagons,arrayHeptagons,arrayOctogons, arrayClosestDiagram, arrayDistanceDiagram, arrayConfidenceDiagram);
			
			excelclass.exportData();
		}
	}

	/**
	 * Import information from an .XLS file to the table
	 * @param saveIntoTable whether it should save the info into the table of the main window or not
	 * @return the arraylist of graphletsImages from the imported data
	 */
	public ArrayList<BasicGraphletImage> importXLSToTable(boolean saveIntoTable) {
		
		fileChooser.setDialogTitle("Import");
		javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("XLS files", "xls");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (this.initialDirectory != null)
			fileChooser.setCurrentDirectory(new File(this.initialDirectory));


		int option = fileChooser.showOpenDialog(this.fatherWindow);
		
		if (option == JFileChooser.APPROVE_OPTION) {
			
//			System.out.println("getCurrentDirectory(): " + fileChooser.getCurrentDirectory());
//			System.out.println("getSelectedFile() : " + fileChooser.getSelectedFile().getPath());

			
			ExcelClass excelclass = new ExcelClass();
			
			FileInputStream path = null;
			try {
				path = new FileInputStream(fileChooser.getSelectedFile());
				this.initialDirectory = fileChooser.getSelectedFile().getParentFile().toString().concat(System.getProperty("file.separator"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				return excelclass.importExcel(path, tableInfo, diagramsData);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void calculateSimpleGDD(){
		
		int radiusNeighs = 3;
		int[] graphletsWeDontWant = {15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
				35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
				62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72};
		int NUMRANDOMVORONOI = 20;
		
		SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
		ExcelFile workbook = new ExcelFile();
		ExcelWorksheet worksheet = workbook.addWorksheet("Sheet");
		int numCol = 1;
		
		fileChooser.setDialogTitle("GDD muscle");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (this.initialDirectory != null)
			fileChooser.setCurrentDirectory(new File(this.initialDirectory));


		int option = fileChooser.showOpenDialog(this.fatherWindow);
		
		ArrayList<ArrayList<Double>> gddDistances = new ArrayList<ArrayList<Double>>();
		if (option == JFileChooser.APPROVE_OPTION) {

			String chosenDir1 = fileChooser.getSelectedFile().getPath();
			File directoryPathChosen1 = new File(chosenDir1);
			String folders1[] = directoryPathChosen1.list();
			
			String chosenDir2 = chosenDir1.replace("CONT", "G93A");
			File directoryPathChosen2 = new File(chosenDir2);
			String foldersG93A1[] = directoryPathChosen2.list();
			
			for (int numFolder1 = 0; numFolder1 < folders1.length; numFolder1++) {
				File directoryPathFolder1 = new File(chosenDir1+"\\"+folders1[numFolder1]);
				String folders2[] = directoryPathFolder1.list();
				
				for (int numFolder2 = 0; numFolder2 < folders2.length; numFolder2++) {
					File directoryPathFolder2 = new File(chosenDir1+"\\"+folders1[numFolder1]+"\\"+folders2[numFolder2]);
					String folders3[] = directoryPathFolder2.list();
					
					for (int numFolder3 = 0; numFolder3 < folders3.length; numFolder3++) {
						if(folders3[numFolder3].endsWith("GDD")){
							
							File directoryPathOrigin = new File(chosenDir1+"\\"+folders1[numFolder1]+"\\"+folders2[numFolder2]+"\\"+folders3[numFolder3]+"\\Original");
							
							// List of all files and directories
							String contents[] = directoryPathOrigin.list();

							ArrayList<Double> newGDDDistance;
							newGDDDistance = new ArrayList<Double>();
							int numRow = 1;

							/////////////////////////////////////////////////////////////77
							for (int numFolderG93A1 = 0; numFolderG93A1 < foldersG93A1.length; numFolderG93A1++) {
								File directoryPathFolderG93A1 = new File(chosenDir2+"\\"+foldersG93A1[numFolderG93A1]);
								String foldersG93A2[] = directoryPathFolderG93A1.list();
								
								for (int numFolderG93A2 = 0; numFolderG93A2 < foldersG93A2.length; numFolderG93A2++) {
									File directoryPathFolderG93A2 = new File(chosenDir2+"\\"+foldersG93A1[numFolderG93A1]+"\\"+foldersG93A2[numFolderG93A2]);
									String foldersG93A3[] = directoryPathFolderG93A2.list();
									
									for (int numFolderG93A3 = 0; numFolderG93A3 < foldersG93A3.length; numFolderG93A3++) {
										if(foldersG93A3[numFolderG93A3].endsWith("GDD")){
											File directoryPathG93A = new File(chosenDir2+"\\"+foldersG93A1[numFolderG93A1]+"\\"+foldersG93A2[numFolderG93A2]+"\\"+foldersG93A3[numFolderG93A3]+"\\Original");
											// List of all files and directories
											String contents2[] = directoryPathG93A.list();
											
											
											for (int numImage1 = 0; numImage1 < contents.length; numImage1++) {
												String fullDirectory = directoryPathOrigin.getAbsolutePath() + System.getProperty("file.separator") + contents[numImage1];
												if (fullDirectory.endsWith(".png")){
													ImagePlus imageOriginal = IJ.openImage(fullDirectory);
													String fullDirectoryXls_1 = fullDirectory.replace("segmentedImage.png", "centroidsSlowCells.csv");
													System.out.println(directoryPathFolder2.getPath());

													File f = new File(fullDirectoryXls_1);
													if(f.exists() == false)
														continue;
													
													
													GraphletImage graphletsImage1 = new GraphletImage(imageOriginal);
													ArrayList<Integer[]> graphlets1 = processSimpleImage(radiusNeighs, graphletsWeDontWant, graphletsImage1.getRaw_img(),
															graphletsImage1, fullDirectoryXls_1);
													
													for (int numImage2 = 0; numImage2 < contents2.length; numImage2++) {
														fullDirectory = directoryPathG93A.getAbsolutePath() + System.getProperty("file.separator")
																+ contents2[numImage2];
														if (fullDirectory.endsWith(".png")){
																ImagePlus imageToCompare = IJ.openImage(fullDirectory);
																String fullDirectoryXls_2 = fullDirectory.replace("segmentedImage.png", "centroidsSlowCells.csv");
																f = new File(fullDirectoryXls_2);
																if(f.exists() == false)
																	continue;
																Double newGDD = compare2GraphletsImages(radiusNeighs, graphletsWeDontWant,
																		graphlets1, imageToCompare, fullDirectoryXls_2);
																newGDDDistance.add(newGDD);
																
																ExcelColumn column1 = worksheet.getColumn(0);
																column1.getCell(numRow).setValue(foldersG93A1[numFolderG93A1]+"_"+foldersG93A2[numFolderG93A2]);
																numRow++;
																System.out.println("-------Row " + newGDDDistance.size() + " of " + NUMRANDOMVORONOI + " finished-------");

														}
													}															
												}
											}
											
										}
									}
								}
							}
							

							ExcelColumn column = worksheet.getColumn(numCol);
							numCol++;
							column.getCell(0).setValue(folders1[numFolder1]+"_"+folders2[numFolder2]);

							int numRow1 = 1;
							for(Iterator iterator = newGDDDistance.iterator(); iterator.hasNext();) {
									Double double1 = (Double) iterator.next();
									column.getCell(numRow1).setValue(double1);
									numRow1++;
							}

							try {
								workbook.save(chosenDir1 + "_gdd_WT_G93A.xlsx");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
							
							
						}				
					
					}
					
				}
				
			
			}

			
			
			
			
			
			
			
			
		}
	}





	/**
	 * @param radiusNeighs
	 * @param graphletsWeDontWant
	 * @param NUMRANDOMVORONOI
	 * @param fullDirectoryXls_2 
	 * @param fullDirectoryXls_1 
	 * @param graphletsImage1
	 * @param graphletsImage2
	 * @return
	 */
	public double compare2GraphletsImages(int radiusNeighs, int[] graphletsWeDontWant,
			ArrayList<Integer[]> graphlets1, ImagePlus imageToCompare, String fullDirectoryXls_2) {
			
			GraphletImage graphletsImage2 = new GraphletImage(imageToCompare);
	
			ArrayList<Integer[]> graphlets2 = processSimpleImage(radiusNeighs, graphletsWeDontWant, graphletsImage2.getRaw_img(),
					graphletsImage2, fullDirectoryXls_2);
			
			double distanceGDD = graphletsImage2.calculateGDD(graphlets1, graphlets2);
			return distanceGDD;
	}

	/**
	 * @param radiusNeighs
	 * @param graphletsWeDontWant
	 * @param imageOriginal
	 * @param originalGraphlets
	 * @param fullDirectoryXls 
	 * @return
	 */
	public ArrayList<Integer[]> processSimpleImage(int radiusNeighs, int[] graphletsWeDontWant, ImagePlus imageOriginal,
			GraphletImage originalGraphlets, String fullDirectoryXls) {
		JProgressBar progressBar = new JProgressBar();
		
        ArrayList<Integer> slowCells = new ArrayList<Integer>();
		if (originalGraphlets.getCells() == null){
			try {
				originalGraphlets.preprocessImage(imageOriginal, 8, progressBar);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Neighbours
			for (int indexEpiCell = 0; indexEpiCell < originalGraphlets.getCells().size(); indexEpiCell++) {
				originalGraphlets.createNeighbourhood(indexEpiCell, 0, radiusNeighs);
			}
			
			Orca orcaProgram = new Orca(originalGraphlets.getAdjacencyMatrix(false));
	
			int[][] graphlets = orcaProgram.getOrbit();
			
			orcaProgram = null;
			for (int i = 0; i < graphlets.length; i++) {
				originalGraphlets.getCells().get(i).setGraphlets(graphlets[i]);
			}
			
			//Consider only slow cells to obtain the graphlets.
			SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
			ExcelFile workbook;
			try {
				workbook = ExcelFile.load(fullDirectoryXls);
				ExcelWorksheet worksheet = workbook.getWorksheet(0);
	            // Iterate through all rows in an Excel worksheet.
	            for (ExcelRow row : worksheet.getRows()) {
	            	int centroid2 = Math.round((float) row.getCell(1).getDoubleValue());;
	            	int centroid1 = Math.round((float) row.getCell(0).getDoubleValue());
	            	int numCell = originalGraphlets.getLabelledImage().getChannelProcessor().get(centroid1, centroid2) - 1;
	            	slowCells.add(numCell);
	            	originalGraphlets.getCells().get(numCell).setSelected(true);
	            }
//	            BufferedImage bi = originalGraphlets.getLabelledImage().getBufferedImage();
//	            File outputfile = new File("saved.png");
//	            ImageIO.write(bi, "png", outputfile);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<Integer[]> graphletsFinal = new ArrayList<Integer[]>();
		Integer[] actualGraphlets;
		for (EpiCell cell2 : originalGraphlets.getCells()) {
			if (cell2.isValid_cell() && cell2.isSelected()) {
				actualGraphlets = cell2.getGraphletsInteger(graphletsWeDontWant);
				graphletsFinal.add(actualGraphlets);
			}
		}
		return graphletsFinal;
	}	
}
