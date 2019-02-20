package epigraph.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import epigraph.BasicGraphletImage;
import epigraph.DiagramsData;
import epigraph.Epigraph;
import epigraph.GraphletImage;
import epigraph.JTableModel;
import epigraph.GUI.CustomElements.CustomCanvas;
import epigraph.GUI.CustomElements.ImageOverlay;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.StackWindow;
import ij.gui.Roi;
import ij.gui.TextRoi;
import ij.gui.ScrollbarWithLabel;
import ij.measure.Calibration;
import ij.plugin.Duplicator;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import util.opencsv.CSVWriter;
import vib.segment.CustomStackWindow;
/**
 * Window that process the image and calculate its graphlets.
 * 
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 */
public class ImageProcessingWindow extends ImageWindow implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static int MIN_GRAPHLETS_ON_IMAGE = 15;
	// For future stack
	private ArrayList<GraphletImage> newGraphletImages;

	private ImageOverlay overlayResult;
	private GraphletImage newGraphletImage;
	private JTextField tfImageName;
	private JButton btnCreateRoi, btnCalculateGraphlets, btnTestNeighbours, btnPickAColor;
	private JComboBox<String> cbSelectedShape, cbGraphletsMode;
	private JLabel lblRadius, lblImageName;
	private JSpinner inputRadiusNeigh;
	private JPanel colorPicked;
	private JProgressBar progressBar;
	private JTableModel tableInf;
	private CustomCanvas canvas;
	private JPanel configPanel = new JPanel();
	private Container buttonsPanel = new Container();
	private Container labelsJPanel = new Container();
	
	private boolean threadFinished;

	private JLabel lbImageLegend;

	private JLabel lbSquares;
	private JLabel lbPentagons;
	private JLabel lbHexagons;
	private JLabel lbHeptagons;
	private JLabel lbOctogons;
	private JLabel lbtitlePolDistGraphlets;
	private JLabel lbRoiSquares;
	private JLabel lbRoiPentagons;
	private JLabel lbRoiHexagons;
	private JLabel lbRoiHeptagons;
	private JLabel lbRoiOctogons;
	private JLabel lbtitlePolDistRoi;
	private JLabel lblShape;

	private Panel all = new Panel();
	private JPanel graphletsPanel;
	private JPanel polDistPanel;
	private JPanel polDistRoiPanel;
	private JPanel imgPolDistPanel;
	private RoiManager roiManager;
	private JButton btnSelectCells;
	private JPanel roiPanel;
	private JButton btnToggleOverlay;
	private JButton btnSelectInvalidRegion;

	private boolean selectionMode;
	private Roi invalidRegionRoi;
	private JPanel preProcessingPanel;
	private JComboBox<Integer> cbConnectivity;
	private JButton btnLabelImage;
	private Task backgroundTask;
	private JPanel progressBarPanel;
	private JButton btnZipData;
	private JLabel lblConnectiviy;

	private DiagramsData diagramsData;
	private String initialDirectory;
	private JScrollPane scrollPanel = null;
	private Scrollbar sliceSelector;
	/** executor service to launch threads for the plugin methods and events */
	private final ExecutorService exec = Executors.newFixedThreadPool(1);
	private boolean showColorOverlay;
	private CustomStackWindow win;
	public void updateResultOverlay()
	{
		ImageProcessor overlay = imp.getImageStack().getProcessor(imp.getCurrentSlice()).duplicate();

		//IJ.log("updating overlay with result from slice " + displayImage.getCurrentSlice());
				
		overlay = overlay.convertToByte(false);
		//win.overlayResult.setImage(overlay);
	}
	
	/**
	 * Constructor
	 * 
	 * @param raw_img
	 *            img to process
	 * @param tableInfo
	 *            information of the table
	 */
	ImageProcessingWindow(ImagePlus raw_img, JTableModel tableInfo, DiagramsData diagramsData) {
		
		super(raw_img, new CustomCanvas(raw_img));
		//super(raw_img, new CustomCanvas(raw_img));
		
		this.initialDirectory = raw_img.getOriginalFileInfo().directory;
		
		this.diagramsData = diagramsData;

		canvas = (CustomCanvas) super.getCanvas();
		
		sliceSelector = new Scrollbar();
		
		newGraphletImages = new ArrayList<GraphletImage>();

		tableInf = tableInfo;

		overlayResult = new ImageOverlay();

		newGraphletImage = new GraphletImage(raw_img);
		removeAll();

		initGUI(raw_img);

		setEnablePanels(false);
		
		threadFinished = false;
	}

	/**
	 * initialize GUI and configure panels
	 */
	private void initGUI(ImagePlus raw_img) {

		initializeGUIItems(raw_img);

		/* Generic panel layout */
		GridBagLayout genericPanelLayout = new GridBagLayout();
		GridBagConstraints genericPanelConstrainst = new GridBagConstraints();
		genericPanelConstrainst.anchor = GridBagConstraints.NORTHWEST;
		genericPanelConstrainst.fill = GridBagConstraints.BOTH;
		resetGenericConstrainst(genericPanelConstrainst);
		genericPanelConstrainst.insets = new Insets(5, 5, 6, 6);

		/* RIGHT PANEL FORMED BY THESE 4 PANELS */
		// Setup labelling panel
		preProcessingPanel = new JPanel();
		resetGenericConstrainst(genericPanelConstrainst);
		preProcessingPanel.setLayout(genericPanelLayout);

		// Adding to the panel the items
		preProcessingPanel.add(lblConnectiviy, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		preProcessingPanel.add(cbConnectivity, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		genericPanelConstrainst.gridx--;
		preProcessingPanel.add(btnLabelImage, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;

		// Setup the config panel
		configPanel = new JPanel();
		configPanel.setBorder(BorderFactory.createTitledBorder("Neighborhood"));
		resetGenericConstrainst(genericPanelConstrainst);
		configPanel.setLayout(genericPanelLayout);

		// Adding to the panel all the buttons
		configPanel.add(lblRadius, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		configPanel.add(inputRadiusNeigh, genericPanelConstrainst);
		genericPanelConstrainst.gridx--;
		genericPanelConstrainst.gridy++;
		configPanel.add(lblShape, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		configPanel.add(cbSelectedShape, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		configPanel.add(btnToggleOverlay, genericPanelConstrainst);
		genericPanelConstrainst.gridx--;
		configPanel.add(btnTestNeighbours, genericPanelConstrainst);

		// Selection ROI panel
		roiPanel = new JPanel();
		roiPanel.setBorder(BorderFactory.createTitledBorder("Region of interest"));
		roiPanel.setLayout(genericPanelLayout);
		resetGenericConstrainst(genericPanelConstrainst);

		roiPanel.add(btnCreateRoi, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		roiPanel.add(btnSelectCells, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		roiPanel.add(btnSelectInvalidRegion, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;

		// Graphlet Image properties
		graphletsPanel = new JPanel();
		graphletsPanel.setBorder(BorderFactory.createTitledBorder("Graphlets"));
		resetGenericConstrainst(genericPanelConstrainst);
		graphletsPanel.setLayout(genericPanelLayout);

		// Adding buttons to panel
		graphletsPanel.add(lblImageName, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(tfImageName, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(btnPickAColor, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		graphletsPanel.add(colorPicked, genericPanelConstrainst);
		genericPanelConstrainst.gridx--;
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(cbGraphletsMode, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(btnCalculateGraphlets, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(btnZipData, genericPanelConstrainst);

		progressBarPanel = new JPanel();
		progressBarPanel.setBorder(BorderFactory.createEtchedBorder());
		resetGenericConstrainst(genericPanelConstrainst);
		progressBarPanel.setLayout(genericPanelLayout);
		progressBarPanel.add(progressBar, genericPanelConstrainst);

		/* LEFT PANEL */
		// Image of polygon distribution
		imgPolDistPanel = new JPanel();
		resetGenericConstrainst(genericPanelConstrainst);
		JLabel emptyLabel = new JLabel(" ");
		JLabel emptyLabel2 = new JLabel(" ");
		JLabel emptyLabel3 = new JLabel(" ");

		imgPolDistPanel.setLayout(genericPanelLayout);

		imgPolDistPanel.add(emptyLabel, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		imgPolDistPanel.add(emptyLabel2, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		imgPolDistPanel.add(emptyLabel3, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		imgPolDistPanel.add(lbImageLegend, genericPanelConstrainst);

		// labels of polygon distribution
		polDistPanel = new JPanel();
		GridBagLayout polDistPanelLayout = new GridBagLayout();
		GridBagConstraints polDistPanelConstrainst = new GridBagConstraints();
		polDistPanelConstrainst.anchor = GridBagConstraints.NORTHWEST;
		polDistPanelConstrainst.fill = GridBagConstraints.BOTH;
		resetGenericConstrainst(polDistPanelConstrainst);
		polDistPanel.setLayout(polDistPanelLayout);
		polDistPanel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		polDistPanelConstrainst.insets = new Insets(15, 5, 0, 6);
		polDistPanelConstrainst.weighty = 1;
		// Minimum size of the labels
		int[] widths = { 80 };
		polDistPanelLayout.columnWidths = widths;

		polDistPanel.add(lbtitlePolDistGraphlets, polDistPanelConstrainst);
		polDistPanelConstrainst.gridy += 1;
		polDistPanel.add(lbSquares, polDistPanelConstrainst);
		polDistPanelConstrainst.gridy += 1;
		polDistPanel.add(lbPentagons, polDistPanelConstrainst);
		polDistPanelConstrainst.gridy += 1;
		polDistPanel.add(lbHexagons, polDistPanelConstrainst);
		polDistPanelConstrainst.gridy += 1;
		polDistPanel.add(lbHeptagons, polDistPanelConstrainst);
		polDistPanelConstrainst.gridy += 1;
		polDistPanel.add(lbOctogons, polDistPanelConstrainst);
		polDistPanelConstrainst.gridy += 1;
		
		// Add Scrollbar
		sliceSelector.setValues(1, imp.getStackSize(), 1, imp.getStackSize());
		polDistPanel.add(sliceSelector, polDistPanelConstrainst);
		
		// labels of polygon distribution ROI
		polDistRoiPanel = new JPanel();
		GridBagLayout polDistRoiPanelLayout = new GridBagLayout();
		GridBagConstraints polDistRoiPanelConstrainst = new GridBagConstraints();
		polDistRoiPanelConstrainst.anchor = GridBagConstraints.NORTHWEST;
		polDistRoiPanelConstrainst.fill = GridBagConstraints.BOTH;
		resetGenericConstrainst(polDistRoiPanelConstrainst);
		polDistRoiPanel.setLayout(polDistRoiPanelLayout);
		polDistRoiPanel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		polDistRoiPanelConstrainst.insets = new Insets(15, 5, 0, 6);
		polDistRoiPanelConstrainst.weighty = 1;
		// Minimum size of the labels
		polDistRoiPanelLayout.columnWidths = widths;

		polDistRoiPanel.add(lbtitlePolDistRoi, polDistRoiPanelConstrainst);
		polDistRoiPanelConstrainst.gridy += 1;
		polDistRoiPanel.add(lbRoiSquares, polDistRoiPanelConstrainst);
		polDistRoiPanelConstrainst.gridy += 1;
		polDistRoiPanel.add(lbRoiPentagons, polDistRoiPanelConstrainst);
		polDistRoiPanelConstrainst.gridy += 1;
		polDistRoiPanel.add(lbRoiHexagons, polDistRoiPanelConstrainst);
		polDistRoiPanelConstrainst.gridy += 1;
		polDistRoiPanel.add(lbRoiHeptagons, polDistRoiPanelConstrainst);
		polDistRoiPanelConstrainst.gridy += 1;
		polDistRoiPanel.add(lbRoiOctogons, polDistRoiPanelConstrainst);
		polDistRoiPanelConstrainst.gridy += 1;

		setupPanels();

		pack();
		setMinimumSize(getPreferredSize());
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Point loc = getLocation();
		Dimension size = getSize();
		if (loc.y + size.height > screen.height)
			getCanvas().zoomOut(0, 0);

	}

	/**
	 * Initialize gui items
	 * 
	 * @param raw_img
	 */
	
	private void initializeGUIItems(ImagePlus raw_img) {
		canvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				Rectangle r = canvas.getBounds();
				canvas.setDstDimensions(r.width, r.height);
					// set slice selector to the correct number
				/*
					sliceSelector.setValue( imp.getSlice() );
					// add adjustment listener to the scroll bar
					sliceSelector.addAdjustmentListener(new AdjustmentListener() 
					{

						public void adjustmentValueChanged(final AdjustmentEvent e) {
							exec.submit(new Runnable() {
								public void run() {							
									if(e.getSource() == sliceSelector)
									{
										//IJ.log("moving scroll");
										imp.killRoi();
										// drawExamples();
										// updateExampleLists();
										if(showColorOverlay)
										{
											updateResultOverlay();
											imp.updateAndDraw();
										}
									}

								}
							});

						}
					});

					// mouse wheel listener to update the rois while scrolling
					addMouseWheelListener(new MouseWheelListener() {

						@Override
						public void mouseWheelMoved(final MouseWheelEvent e) {

							exec.submit(new Runnable() {
								public void run() 
								{
									//IJ.log("moving scroll");
									imp.killRoi();
									// drawExamples();
									// updateExampleLists();
									if(showColorOverlay)
									{
										updateResultOverlay();
										imp.updateAndDraw();
									}
								}
							});

						}
					});
					*/
			}
		});

		// Connectivity
		cbConnectivity = new JComboBox<Integer>();
		cbConnectivity.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 4, 8 }));
		cbConnectivity.setSelectedIndex(1);

		lblConnectiviy = new JLabel("Connectivity (px):");
		lblConnectiviy.setLabelFor(cbConnectivity);

		btnLabelImage = new JButton("Label image");
		btnLabelImage.addActionListener(this);

		// Radius of neighbours
		inputRadiusNeigh = new JSpinner();
		inputRadiusNeigh.setModel(new SpinnerNumberModel(3, 1, 25, 1));

		lblRadius = new JLabel("Radius:");
		lblRadius.setLabelFor(inputRadiusNeigh);

		// The shape of the mask
		cbSelectedShape = new JComboBox<String>();
		cbSelectedShape.setModel(new DefaultComboBoxModel<String>(new String[] { "Circle", "Square" }));
		cbSelectedShape.setSelectedIndex(0);

		lblShape = new JLabel("Shape:");
		lblShape.setLabelFor(cbSelectedShape);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);

		btnCalculateGraphlets = new JButton("Calculate graphlets!");
		btnCalculateGraphlets.addActionListener(this);

		cbGraphletsMode = new JComboBox<String>();
		cbGraphletsMode.setModel(new DefaultComboBoxModel<String>(GraphletImage.KIND_OF_GRAPHLETS));
		cbGraphletsMode.setSelectedIndex(1);
		
		btnCreateRoi = new JButton("Create RoI");
		btnCreateRoi.addActionListener(this);

		btnSelectCells = new JButton("Select cells");
		btnSelectCells.addActionListener(this);

		btnPickAColor = new JButton("Pick a color");
		btnPickAColor.addActionListener(this);

		colorPicked = new JPanel();
		colorPicked.setBackground(Color.RED);

		btnTestNeighbours = new JButton("Test Neighbours");
		btnTestNeighbours.addActionListener(this);

		btnToggleOverlay = new JButton("Toggle overlay");
		btnToggleOverlay.addActionListener(this);

		btnSelectInvalidRegion = new JButton("Add invalid regions");
		btnSelectInvalidRegion.addActionListener(this);

		btnZipData = new JButton("Export graphlet data");
		btnZipData.addActionListener(this);
		btnZipData.setEnabled(false);

		tfImageName = new JTextField();
		tfImageName.setText(raw_img.getTitle());

		lblImageName = new JLabel("Image label:");
		lblImageName.setLabelFor(tfImageName);

		// Labels for polygon distribution
		lbImageLegend = new JLabel("");
		lbImageLegend.setIcon(new ImageIcon(this.getClass().getResource("/epigraph/legend.jpg")));

		lbtitlePolDistGraphlets = new JLabel("");
		lbtitlePolDistGraphlets.setHorizontalAlignment(SwingConstants.CENTER);

		lbSquares = new JLabel("");
		lbSquares.setHorizontalAlignment(SwingConstants.CENTER);

		lbPentagons = new JLabel("");
		lbPentagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbHexagons = new JLabel("");
		lbHexagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbHeptagons = new JLabel("");
		lbHeptagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbOctogons = new JLabel("");
		lbOctogons.setHorizontalAlignment(SwingConstants.CENTER);

		lbtitlePolDistRoi = new JLabel("");
		lbtitlePolDistRoi.setHorizontalAlignment(SwingConstants.CENTER);

		lbRoiSquares = new JLabel("");
		lbRoiSquares.setHorizontalAlignment(SwingConstants.CENTER);

		lbRoiPentagons = new JLabel("");
		lbRoiPentagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbRoiHexagons = new JLabel("");
		lbRoiHexagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbRoiHeptagons = new JLabel("");
		lbRoiHeptagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbRoiOctogons = new JLabel("");
		lbRoiOctogons.setHorizontalAlignment(SwingConstants.CENTER);
	}

	/**
	 * Reset the constraints. All to 0s and width and height to 1.
	 * 
	 * @param genericPanelConstrainst
	 *            the panel constraints to modify
	 */
	protected void resetGenericConstrainst(GridBagConstraints genericPanelConstrainst) {
		genericPanelConstrainst.gridwidth = 1;
		genericPanelConstrainst.gridheight = 1;
		genericPanelConstrainst.gridx = 0;
		genericPanelConstrainst.gridy = 0;
		genericPanelConstrainst.weighty = 0;
		genericPanelConstrainst.weightx = 0;
	}

	/**
	 * Setup panels
	 */
	private void setupPanels() {
		/* DEFINITION OF RIGHT SIDE PANEL */
		GridBagLayout buttonsLayout = new GridBagLayout();
		GridBagConstraints buttonsConstraints = new GridBagConstraints();
		buttonsPanel.setLayout(buttonsLayout);
		buttonsConstraints.anchor = GridBagConstraints.NORTHWEST;
		buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;
		resetGenericConstrainst(buttonsConstraints);
		buttonsPanel.add(preProcessingPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsPanel.add(roiPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsPanel.add(configPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsPanel.add(graphletsPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsConstraints.gridy++;
		buttonsPanel.add(progressBarPanel, buttonsConstraints);
		buttonsConstraints.insets = new Insets(5, 5, 6, 6);

		/* DEFINITION OF LEFT SIDE PANEL */
		GridBagLayout labelsLayout = new GridBagLayout();
		GridBagConstraints labelsConstraints = new GridBagConstraints();
		labelsJPanel.setLayout(labelsLayout);
		labelsConstraints.anchor = GridBagConstraints.NORTHWEST;
		labelsConstraints.fill = GridBagConstraints.VERTICAL;
		resetGenericConstrainst(labelsConstraints);
		labelsJPanel.add(imgPolDistPanel, labelsConstraints);
		labelsConstraints.gridx++;
		labelsJPanel.add(polDistPanel, labelsConstraints);
		labelsConstraints.gridx++;
		labelsJPanel.add(polDistRoiPanel, labelsConstraints);

		/* MAIN DEFINITION OF THE GUI */
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints allConstraints = new GridBagConstraints();
		all.setLayout(layout);

		/* LEFT SIDE */
		allConstraints.anchor = GridBagConstraints.NORTHWEST;
		allConstraints.fill = GridBagConstraints.BOTH;
		allConstraints.gridwidth = 1;
		allConstraints.gridheight = 2;
		allConstraints.gridx = 0;
		allConstraints.gridy = 0;
		allConstraints.weightx = 0;
		allConstraints.weighty = 0;
		all.add(labelsJPanel, allConstraints);

		/* CENTER FOR THE CANVAS IMAGE */
		allConstraints.gridx++;
		allConstraints.weightx = 1;
		allConstraints.weighty = 1;
		allConstraints.gridheight = 1;
		allConstraints.insets = new Insets(5, 10, 6, 10);
		all.add(canvas, allConstraints);

		allConstraints.gridy++;
		allConstraints.weightx = 0;
		allConstraints.weighty = 0;
		allConstraints.gridy--;

		/* RIGHT SIDE */
		allConstraints.gridx++;
		allConstraints.anchor = GridBagConstraints.NORTHEAST;
		allConstraints.weightx = 0;
		allConstraints.weighty = 0;
		allConstraints.gridheight = 1;
		all.add(buttonsPanel, allConstraints);

		GridBagLayout wingb = new GridBagLayout();
		GridBagConstraints winc = new GridBagConstraints();
		winc.anchor = GridBagConstraints.NORTHWEST;
		winc.fill = GridBagConstraints.BOTH;
		winc.weightx = 1;
		winc.weighty = 1;
		setLayout(wingb);
		add(all, winc);
	}

	/*
	 * Group all the actions(non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		roiManager = RoiManager.getInstance();

		if (e.getSource() == btnCalculateGraphlets) {
			if (tfImageName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this.getParent(), "You should insert a name for the image");
			} else {
				newGraphletImage.setLabelName(tfImageName.getText());
				newGraphletImage.setColor(colorPicked.getBackground());
				disableActionButtons();
				
				backgroundTask = new Task(0);
				backgroundTask.execute();
			}
		} else if (e.getSource() == btnCreateRoi) {
			openRoiManager();
			if (btnCreateRoi.getText() != "Done") {
				Epigraph.callToolbarRectangle();
				btnCreateRoi.setText("Done");
				disableActionButtons();
				btnSelectCells.setEnabled(false);
				btnSelectInvalidRegion.setEnabled(false);
			} else {
				addROI();
				btnCreateRoi.setText("Create ROI");
				enableActionButtons();
				btnSelectCells.setEnabled(true);
				btnSelectInvalidRegion.setEnabled(true);
			}

		} else if (e.getSource() == btnPickAColor) {
			Color c = JColorChooser.showDialog(this.getParent(), "Choose a Color", colorPicked.getBackground());
			if (c != null) {
				colorPicked.setBackground(c);
				if (newGraphletImage != null) {
					newGraphletImage.setColor(c);
				}
			}
		} else if (e.getSource() == btnTestNeighbours) {
			disableActionButtons();
			// Execute in background
			backgroundTask = new Task(1);
			backgroundTask.execute();

			repaintAll();
		} else if (e.getSource() == btnSelectCells) {
			openRoiManager();
			if (btnSelectCells.getText() != "Done") {
				Epigraph.callToolbarMultiPoint();
				btnSelectCells.setText("Done");
				disableActionButtons();
				btnCreateRoi.setEnabled(false);
				btnSelectInvalidRegion.setEnabled(false);
			} else {
				// Add selected cells
				addROI();
				btnSelectCells.setText("Select cells");
				enableActionButtons();
				btnCreateRoi.setEnabled(true);
				btnSelectInvalidRegion.setEnabled(true);
			}
		} else if (e.getSource() == btnToggleOverlay) {
			if (overlayResult != null) {
				if (canvas.getImageOverlay() == null) {
					canvas.clearOverlay();
					canvas.addOverlay(overlayResult);
					canvas.setImageOverlay(overlayResult);
				} else {
					overlayResult = new ImageOverlay(overlayResult.getImage());
					canvas.setImageOverlay(null);
					canvas.clearOverlay();
				}
			}
		} else if (e.getSource() == btnSelectInvalidRegion) {
			if (btnSelectInvalidRegion.getText() != "Done") {
				if (invalidRegionRoi != null) {
					int result = JOptionPane.showConfirmDialog(this.getParent(),
							"This will remove the previous invalid region", "New invalid region",
							JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						Epigraph.callToolbarMultiPoint();
						btnSelectInvalidRegion.setText("Done");
						disableActionButtons();
						btnCreateRoi.setEnabled(false);
						btnSelectCells.setEnabled(false);
					}
				} else {
					Epigraph.callToolbarMultiPoint();
					btnSelectInvalidRegion.setText("Done");
					disableActionButtons();
					btnCreateRoi.setEnabled(false);
					btnSelectCells.setEnabled(false);
				}

			} else {
				// Add selected cells
				addInvalidRegion();
				btnSelectInvalidRegion.setText("Pick invalid regions");
				enableActionButtons();
				btnCreateRoi.setEnabled(true);
				btnSelectCells.setEnabled(true);
			}
		} else if (e.getSource() == btnLabelImage) {
			disableActionButtons();
			backgroundTask = new Task(2);
			backgroundTask.execute();
		} else if (e.getSource() == btnZipData) {
			exportDataIntoZip();
		} else if (this.threadFinished == true) { //Removing the finished thread 
			backgroundTask.cancel(true);
			backgroundTask.finalizeThread();
			backgroundTask = null;
			this.threadFinished = false;
		}

		
		// Update the image and canvas
		imp.updateAndDraw();
		ImageCanvas ic = imp.getCanvas();
		if (ic != null)
			ic.requestFocus();

		
	}

	/**
	 * Export data into a .zip file. It will export a file with graphlets, an
	 * image of neighbours and a labelled image with the number of cell
	 */
	public void exportDataIntoZip() {
		JFileChooser fileChooser = new JFileChooser();
		// set it to be a save dialog
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		// set a default filename (this is where you default extension
		// first comes in)
		
		String outputFile = initialDirectory.concat("data.zip");
		
		fileChooser.setSelectedFile(new File(outputFile));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// Set an extension filter, so the user sees other XML files
		fileChooser.setFileFilter(new FileNameExtensionFilter("ZIP files", "zip"));

		fileChooser.setAcceptAllFileFilterUsed(false);

		int userSelection = fileChooser.showSaveDialog(btnZipData.getParent());

		
		if (userSelection == JFileChooser.APPROVE_OPTION) {

			String filename = fileChooser.getSelectedFile().toString();

			if (!filename.endsWith(".zip"))
				filename += ".zip";

			if ((fileChooser.getSelectedFile() != null) && fileChooser.getSelectedFile().exists()) {
				int response = JOptionPane.showConfirmDialog(this,
						"The file " + fileChooser.getSelectedFile().getName()
								+ " already exists. Do you want to replace the existing file?",
						"Ovewrite file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (response != JOptionPane.YES_OPTION)
					return;
			}

			ZipOutputStream out;
			try {
				out = new ZipOutputStream(new FileOutputStream(filename));

				ZipEntry e = new ZipEntry("neighbours.jpg");
				out.putNextEntry(e);
				ImageIO.write(newGraphletImage.getNeighbourImage().getBufferedImage(), "jpg", out);
				out.closeEntry();

				e = new ZipEntry("labelledImage.jpg");
				out.putNextEntry(e);
				ImageIO.write(newGraphletImage.getImageWithLabels().getBufferedImage(), "jpg", out);
				out.closeEntry();
				
				e = new ZipEntry("neighbours_network_cytoscape.sif");
				out.putNextEntry(e);
				BufferedWriter textWriter = new BufferedWriter(new OutputStreamWriter(out));
				int[][] adjacencyMatrixToExport = newGraphletImage.getAdjacencyMatrix();
				for (int row = 0; row < adjacencyMatrixToExport.length; row++){
					for (int col = row + 1; col < adjacencyMatrixToExport[0].length; col++){
						if (adjacencyMatrixToExport[row][col] == 1){
							textWriter.write(Integer.toString(row) + " pp " + Integer.toString(col)); // write the contents
							textWriter.newLine();
						}
					}
				}
				textWriter.flush(); // flush the writer. Very important!
				out.closeEntry();

				e = new ZipEntry("graphletsPerNode.csv");
				out.putNextEntry(e);
				// There is no need for staging the CSV on filesystem or reading
				// bytes into memory. Directly write bytes to the output stream.
				CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));
				String[] header = {"numLabel", "orbit 0", "orbit 1", "orbit 2", "orbit 3", "orbit 4", "orbit 5",
						"orbit 6", "orbit 7", "orbit 8", "orbit 9", "orbit 10", "orbit 11", "orbit 12", "orbit 13",
						"orbit 14", "orbit 15", "orbit 16", "orbit 17", "orbit 18", "orbit 19", "orbit 20", "orbit 21",
						"orbit 22", "orbit 23", "orbit 24", "orbit 25", "orbit 26", "orbit 27", "orbit 28", "orbit 29",
						"orbit 30", "orbit 31", "orbit 32", "orbit 33", "orbit 34", "orbit 35", "orbit 36", "orbit 37",
						"orbit 38", "orbit 39", "orbit 40", "orbit 41", "orbit 42", "orbit 43", "orbit 44", "orbit 45",
						"orbit 46", "orbit 47", "orbit 48", "orbit 49", "orbit 50", "orbit 51", "orbit 52", "orbit 53",
						"orbit 54", "orbit 55", "orbit 56", "orbit 57", "orbit 58", "orbit 59", "orbit 60", "orbit 61",
						"orbit 62", "orbit 63", "orbit 64", "orbit 65", "orbit 66", "orbit 67", "orbit 68", "orbit 69",
						"orbit 70", "orbit 71", "orbit 72" };
				writer.writeNext(header);
				for (String[] row : newGraphletImage.getFinalGraphlets(selectionMode))
					writer.writeNext(row); // write the contents
				writer.flush(); // flush the writer. Very important!
				out.closeEntry();
				
				e = new ZipEntry("orbitWeight.csv");
				out.putNextEntry(e);
				CSVWriter writer2 = new CSVWriter(new OutputStreamWriter(out));
        String[] header2 = {"TypeOfGDD", "TotalWeight", "orbit 0", "orbit 1", "orbit 2", "orbit 3", "orbit 4", "orbit 5",
            "orbit 6", "orbit 7", "orbit 8", "orbit 9", "orbit 10", "orbit 11", "orbit 12", "orbit 13",
            "orbit 14", "orbit 15", "orbit 16", "orbit 17", "orbit 18", "orbit 19", "orbit 20", "orbit 21",
            "orbit 22", "orbit 23", "orbit 24", "orbit 25", "orbit 26", "orbit 27", "orbit 28", "orbit 29",
            "orbit 30", "orbit 31", "orbit 32", "orbit 33", "orbit 34", "orbit 35", "orbit 36", "orbit 37",
            "orbit 38", "orbit 39", "orbit 40", "orbit 41", "orbit 42", "orbit 43", "orbit 44", "orbit 45",
            "orbit 46", "orbit 47", "orbit 48", "orbit 49", "orbit 50", "orbit 51", "orbit 52", "orbit 53",
            "orbit 54", "orbit 55", "orbit 56", "orbit 57", "orbit 58", "orbit 59", "orbit 60", "orbit 61",
            "orbit 62", "orbit 63", "orbit 64", "orbit 65", "orbit 66", "orbit 67", "orbit 68", "orbit 69",
            "orbit 70", "orbit 71", "orbit 72" };
        writer2.writeNext(header2);
        int cont = 0;
        for (float[] row : newGraphletImage.getOrbitsWeights()){
          String[] rowStr = new String[row.length+2];
          
          switch (cont) {
          case 0:
            rowStr[0] = "Epi-Hexagons";
            rowStr[1] = Float.toString(newGraphletImage.getDistanceGDDH());
            break;
          case 1:
            rowStr[0] = "Epi-Random";
            rowStr[1] = Float.toString(newGraphletImage.getDistanceGDDRV());
            break;
          case 2:
            rowStr[0] = "Epi-Voronoi5";
            rowStr[1] = Float.toString(newGraphletImage.getDistanceGDDV5());
            break;
          }
          
          for (int numRow = 0; numRow < row.length; numRow++)
            rowStr[numRow+2] = Float.toString(row[numRow]);
          
          writer2.writeNext(rowStr); // write the contents
          cont++;
        }
        writer2.flush(); // flush the writer. Very important!
        out.closeEntry();

				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Enable/disable all the panels in the window
	 * 
	 * @param enabled
	 *            true it will enable panels, false disable all panels
	 */
	protected void setEnablePanels(boolean enabled) {
		btnLabelImage.setEnabled(true);

		for (Component c : roiPanel.getComponents()) {
			c.setEnabled(enabled);
		}

		for (Component c : configPanel.getComponents()) {
			c.setEnabled(enabled);
		}

		for (Component c : graphletsPanel.getComponents()) {
			c.setEnabled(enabled);
		}

		if (newGraphletImage.getDistanceGDDH() == -1) {
			btnZipData.setEnabled(false);
		}
	}

	/**
	 * Disable all the action buttons
	 */
	protected void disableActionButtons() {
		btnCalculateGraphlets.setEnabled(false);
		btnTestNeighbours.setEnabled(false);
		btnLabelImage.setEnabled(false);
		btnZipData.setEnabled(false);
		cbGraphletsMode.setEnabled(false);
	}

	/**
	 * Enable all the action buttons
	 */
	protected void enableActionButtons() {
		btnCalculateGraphlets.setEnabled(true);
		if (newGraphletImage.getDistanceGDDH() != -1) {
			btnZipData.setEnabled(true);
		}
		btnTestNeighbours.setEnabled(true);
		btnLabelImage.setEnabled(true);
		cbGraphletsMode.setEnabled(true);
	}

	/**
	 * Transform the ROI to an invalid region that will be surrounded by no
	 * valid cells
	 */
	public void addInvalidRegion() {
		this.newGraphletImage.resetInvalidRegion();
		invalidRegionRoi = null;
		Roi r = this.getImagePlus().getRoi();
		if (r != null) {
			for (Point point : r) {
				int[] pixelInfo = newGraphletImage.getLabelledImage().getPixel(point.x, point.y);
				this.newGraphletImage.addCellToInvalidRegion(pixelInfo[0]);
			}
			invalidRegionRoi = r;
		}
		this.getImagePlus().deleteRoi();
	}

	/**
	 * Add the painted Roi to the roiManager
	 */
	public void addROI() {
		Roi r = this.getImagePlus().getRoi();
		if (r != null) {
			roiManager.addRoi(r);
		}
		this.getImagePlus().deleteRoi();
	}

	/**
	 * Repaint all panels
	 */
	protected void repaintAll() {
		getCanvas().repaint();
		this.labelsJPanel.repaint();
		this.buttonsPanel.repaint();
		this.all.repaint();
	}

	/**
	 * Open roi manager and get the reference
	 */
	public void openRoiManager() {
		roiManager = RoiManager.getRoiManager();
		roiManager.toFront();
	}

	/**
	 * @return the roiManager
	 */
	public RoiManager getRoiManager() {
		return roiManager;
	}

	/**
	 * @param roiManager
	 *            the roiManager to set
	 */
	public void setRoiManager(RoiManager roiManager) {
		this.roiManager = roiManager;
	}

	/**
	 * Task to be computed in background without blocking the user interface
	 * 
	 * @author Pablo Vicente-Munuera
	 */
	public class Task extends SwingWorker<Void, Void> {

		int option;

		/**
		 * Constructor
		 * 
		 * @param option
		 *            the operation to calculate
		 */
		public Task(int option) {
			super();
			this.option = option;
		}

		public void finalizeThread() {
			try {
				this.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Main task. Executed in background thread.
		 */
		@Override
		protected Void doInBackground() {
			try {
				setProgress(0);
				switch (option) {
				case 0:
					calculateGraphlets();
					break;
				case 1:
					testNeighbours();
					break;
				case 2:
					labelImage();
					break;
				}
			} catch (Exception e) {
				e.getCause().printStackTrace();
				String msg = String.format("Unexpected problem: %s", e.getCause().toString());
				JOptionPane.showMessageDialog(canvas.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}

		/**
		 * Executed in event dispatching thread
		 */
		@Override
		protected void done() {
			try {
				get(); // get exceptions
				progressBar.setValue(100);
				Toolkit.getDefaultToolkit().beep();
			} catch (ExecutionException e) {
				e.getCause().printStackTrace();
				String msg = String.format("Unexpected problem: %s", e.getCause().toString());
				JOptionPane.showMessageDialog(canvas.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
				progressBar.setValue(0);
			} catch (InterruptedException e) {
				String msg = String.format("Unexpected problem: %s", e.getCause().toString());
				JOptionPane.showMessageDialog(canvas.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
				progressBar.setValue(0);
			}
			
			repaintAll();
			if (option == 2)
				setEnablePanels(true);
			else
				enableActionButtons();
			
			this.cancel(true);
			
			threadFinished = true;
		}

		/**
		 * Calculate graphlets in background
		 * @throws CloneNotSupportedException when using clone if it cannot proceed
		 */
		public void calculateGraphlets() throws CloneNotSupportedException {
			ArrayList<ArrayList<String>> ListPolDistri;
			int maxLength = 0;
			if (cbGraphletsMode.getSelectedIndex() >= 2) {
				maxLength = 4;
			} else {
				maxLength = 5;
			}

			int numberOfValidCellsOfLength = 100;
			int totalGraphlets;
			
			//close roiManager if we try calculate graphlets with a empty roiManager
			if(roiManager != null){
				Roi[] roiArray = roiManager.getSelectedRoisAsArray();
				if(roiArray.length==0){
					roiManager.close();
					roiManager=null;
					}
				}
			if (imp.getStackSize() == 1) {
			if (roiManager != null) {
				Roi[] roiArray = roiManager.getSelectedRoisAsArray();
				ListPolDistri = newGraphletImage.runGraphlets(cbSelectedShape.getSelectedIndex(),
						(int) inputRadiusNeigh.getValue(), (int) cbGraphletsMode.getSelectedIndex(), progressBar,
						roiArray.length > 0, overlayResult);
				numberOfValidCellsOfLength = newGraphletImage.calculateNumberOfValidCellForGraphlets(maxLength,
						roiArray.length > 0);
				totalGraphlets = newGraphletImage.getTotalNumberOfGraphlets((int) cbGraphletsMode.getSelectedIndex(), roiArray.length > 0, maxLength);
				
				ArrayList<String> polDistriRoi = ListPolDistri.get(1);
				newGraphletImage.setPercentageOfSquares(Float.parseFloat(polDistriRoi.get(0).replace("%","").replace(",", ".")));
				newGraphletImage.setPercentageOfPentagons(Float.parseFloat(polDistriRoi.get(1).replace("%","").replace(",", ".")));
				newGraphletImage.setPercentageOfHexagons(Float.parseFloat(polDistriRoi.get(2).replace("%","").replace(",", ".")));
				newGraphletImage.setPercentageOfHeptagons(Float.parseFloat(polDistriRoi.get(3).replace("%","").replace(",", ".")));
				newGraphletImage.setPercentageOfOctogons(Float.parseFloat(polDistriRoi.get(4).replace("%","").replace(",", ".")));
			}
			else {
					ListPolDistri = newGraphletImage.runGraphlets(cbSelectedShape
						.getSelectedIndex(), (int) inputRadiusNeigh.getValue(),
						(int) cbGraphletsMode.getSelectedIndex(), progressBar, false,
						overlayResult);
					numberOfValidCellsOfLength = newGraphletImage
						.calculateNumberOfValidCellForGraphlets(maxLength, false);
					totalGraphlets = newGraphletImage.getTotalNumberOfGraphlets(
						(int) cbGraphletsMode.getSelectedIndex(), false, maxLength);

					ArrayList<String> polDistriGraphlets = ListPolDistri.get(0);

					newGraphletImage.setPercentageOfSquares(Float.parseFloat(
						polDistriGraphlets.get(0).replace("%", "").replace(",", ".")));

					newGraphletImage.setPercentageOfPentagons(Float.parseFloat(
						polDistriGraphlets.get(1).replace("%", "").replace(",", ".")));
					newGraphletImage.setPercentageOfHexagons(Float.parseFloat(
						polDistriGraphlets.get(2).replace("%", "").replace(",", ".")));
					newGraphletImage.setPercentageOfHeptagons(Float.parseFloat(
						polDistriGraphlets.get(3).replace("%", "").replace(",", ".")));
					newGraphletImage.setPercentageOfOctogons(Float.parseFloat(
						polDistriGraphlets.get(4).replace("%", "").replace(",", ".")));
				}
			ArrayList<String> polDistriGraphlets = ListPolDistri.get(0);
			lbtitlePolDistGraphlets.setText("Graphlets");
			lbtitlePolDistGraphlets.setFont(new Font("Tahoma", Font.BOLD, 14));
			lbSquares.setText(polDistriGraphlets.get(0));
			lbPentagons.setText(polDistriGraphlets.get(1));
			lbHexagons.setText(polDistriGraphlets.get(2));
			lbHeptagons.setText(polDistriGraphlets.get(3));
			lbOctogons.setText(polDistriGraphlets.get(4));

			if (ListPolDistri.size() > 1) {
				ArrayList<String> polDistriRoi = ListPolDistri.get(1);
				lbtitlePolDistRoi.setText("ROIs");
				lbtitlePolDistRoi.setFont(new Font("Tahoma", Font.BOLD, 14));
				lbRoiSquares.setText(polDistriRoi.get(0));
				lbRoiPentagons.setText(polDistriRoi.get(1));
				lbRoiHexagons.setText(polDistriRoi.get(2));
				lbRoiHeptagons.setText(polDistriRoi.get(3));
				lbRoiOctogons.setText(polDistriRoi.get(4));
			}
			if (numberOfValidCellsOfLength > 0) {
				if (totalGraphlets < MIN_GRAPHLETS_ON_IMAGE) {// Careful
					JOptionPane.showMessageDialog(canvas.getParent(),
							"Care: Not too many graphlets in the sample selected. You may obtain results with no warranties",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
				

				switch (cbGraphletsMode.getSelectedIndex()){
					case 3: newGraphletImage.calculateClosestDiagram(diagramsData.getMo7());
						break;
					case 2: newGraphletImage.calculateClosestDiagram(diagramsData.getMo10());
						break;
					case 1: newGraphletImage.calculateClosestDiagram(diagramsData.getMO17());
						break;
					case 0: newGraphletImage.calculateClosestDiagram(diagramsData.getMo29());
						break;
				}
				
				tableInf.addImage((GraphletImage) newGraphletImage.clone(), cbGraphletsMode.getSelectedItem().toString());

				JOptionPane pane = new JOptionPane("Graphlet data added to table", JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
			JDialog dialog = pane.createDialog("Info message");

			dialog.addWindowListener(null);

			Timer timer = new Timer(1000, new ActionListener() { // 1 sec
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(false);
					dialog.dispose();
				}
			});

			timer.start();
			dialog.setVisible(true);
		} else { // No calculations
			JOptionPane.showMessageDialog(canvas.getParent(),
					"No valid cells for graphlets, should be at least 4 cells between a valid and a non-valid one",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
			} else {
			ArrayList<ArrayList<String>> TotalListPolDistri = new ArrayList<>();
			for (int j = 0; j < imp.getStackSize(); j++) {
			if (roiManager != null) {				
				Roi[] roiArray = roiManager.getSelectedRoisAsArray();
				ListPolDistri = newGraphletImages.get(j).runGraphlets(cbSelectedShape.getSelectedIndex(),
						(int) inputRadiusNeigh.getValue(), (int) cbGraphletsMode.getSelectedIndex(), progressBar,
						roiArray.length > 0, overlayResult);
				numberOfValidCellsOfLength = newGraphletImages.get(j).calculateNumberOfValidCellForGraphlets(maxLength,
						roiArray.length > 0);
				totalGraphlets = newGraphletImages.get(j).getTotalNumberOfGraphlets((int) cbGraphletsMode.getSelectedIndex(), roiArray.length > 0, maxLength);
				
				ArrayList<String> polDistriRoi = ListPolDistri.get(1);
				newGraphletImages.get(j).setPercentageOfSquares(Float.parseFloat(polDistriRoi.get(0).replace("%","").replace(",", ".")));
				newGraphletImages.get(j).setPercentageOfPentagons(Float.parseFloat(polDistriRoi.get(1).replace("%","").replace(",", ".")));
				newGraphletImages.get(j).setPercentageOfHexagons(Float.parseFloat(polDistriRoi.get(2).replace("%","").replace(",", ".")));
				newGraphletImages.get(j).setPercentageOfHeptagons(Float.parseFloat(polDistriRoi.get(3).replace("%","").replace(",", ".")));
				newGraphletImages.get(j).setPercentageOfOctogons(Float.parseFloat(polDistriRoi.get(4).replace("%","").replace(",", ".")));
			}
			else {
					ListPolDistri = newGraphletImages.get(j).runGraphlets(cbSelectedShape
						.getSelectedIndex(), (int) inputRadiusNeigh.getValue(),
						(int) cbGraphletsMode.getSelectedIndex(), progressBar, false,
						overlayResult);
					numberOfValidCellsOfLength = newGraphletImages.get(j)
						.calculateNumberOfValidCellForGraphlets(maxLength, false);
					totalGraphlets = newGraphletImages.get(j).getTotalNumberOfGraphlets(
						(int) cbGraphletsMode.getSelectedIndex(), false, maxLength);

					ArrayList<String> polDistriGraphlets = ListPolDistri.get(0);

					newGraphletImages.get(j).setPercentageOfSquares(Float.parseFloat(
						polDistriGraphlets.get(0).replace("%", "").replace(",", ".")));

					newGraphletImages.get(j).setPercentageOfPentagons(Float.parseFloat(
						polDistriGraphlets.get(1).replace("%", "").replace(",", ".")));
					newGraphletImages.get(j).setPercentageOfHexagons(Float.parseFloat(
						polDistriGraphlets.get(2).replace("%", "").replace(",", ".")));
					newGraphletImages.get(j).setPercentageOfHeptagons(Float.parseFloat(
						polDistriGraphlets.get(3).replace("%", "").replace(",", ".")));
					newGraphletImages.get(j).setPercentageOfOctogons(Float.parseFloat(
						polDistriGraphlets.get(4).replace("%", "").replace(",", ".")));
					TotalListPolDistri.add(polDistriGraphlets);
					}
			
					ArrayList<String> polDistriGraphlets = ListPolDistri.get(0);
					lbtitlePolDistGraphlets.setText("Graphlets");
					lbtitlePolDistGraphlets.setFont(new Font("Tahoma", Font.BOLD, 14));
					lbSquares.setText(polDistriGraphlets.get(0));
					lbPentagons.setText(polDistriGraphlets.get(1));
					lbHexagons.setText(polDistriGraphlets.get(2));
					lbHeptagons.setText(polDistriGraphlets.get(3));
					lbOctogons.setText(polDistriGraphlets.get(4));
					TotalListPolDistri.add(polDistriGraphlets);
					if (ListPolDistri.size() > 1) {
						ArrayList<String> polDistriRoi = ListPolDistri.get(1);
						ArrayList<ArrayList<String>> TotalpolDistriRoi = new ArrayList<>();
						lbtitlePolDistRoi.setText("ROIs");
						lbtitlePolDistRoi.setFont(new Font("Tahoma", Font.BOLD, 14));
						lbRoiSquares.setText(polDistriRoi.get(0));
						lbRoiPentagons.setText(polDistriRoi.get(1));
						lbRoiHexagons.setText(polDistriRoi.get(2));
						lbRoiHeptagons.setText(polDistriRoi.get(3));
						lbRoiOctogons.setText(polDistriRoi.get(4));
						TotalpolDistriRoi.add(polDistriRoi);
					}
		
					if (numberOfValidCellsOfLength > 0) {
						if (totalGraphlets < MIN_GRAPHLETS_ON_IMAGE) {// Careful
							JOptionPane.showMessageDialog(canvas.getParent(),
								"Care: Not too many graphlets in the sample selected. You may obtain results with no warranties",
								"Warning", JOptionPane.WARNING_MESSAGE);
						}

						switch (cbGraphletsMode.getSelectedIndex()) {
							case 3:
								newGraphletImages.get(j).calculateClosestDiagram(diagramsData
									.getMo7());
								break;
							case 2:
								newGraphletImages.get(j).calculateClosestDiagram(diagramsData
									.getMo10());
								break;
							case 1:
								newGraphletImages.get(j).calculateClosestDiagram(diagramsData
									.getMO17());
								break;
							case 0:
								newGraphletImages.get(j).calculateClosestDiagram(diagramsData
									.getMo29());
								break;
						}

						tableInf.addImages(j, (ArrayList<BasicGraphletImage>) newGraphletImages.clone(), cbGraphletsMode.getSelectedItem().toString());
						
						JOptionPane pane = new JOptionPane("Graphlet data added to table",
							JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
							new Object[] {}, null);
						JDialog dialog = pane.createDialog("Info message");

						dialog.addWindowListener(null);

						Timer timer = new Timer(1000, new ActionListener() { // 1 sec

							public void actionPerformed(ActionEvent e) {
								dialog.setVisible(false);
								dialog.dispose();
							}
						});

						timer.start();
						dialog.setVisible(true);
					}
					else { // No calculations
						JOptionPane.showMessageDialog(canvas.getParent(),
							"No valid cells for graphlets, should be at least 4 cells between a valid and a non-valid one",
							"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
		
		/**
		 * Calculate polygon distribution in background
		 */
		public void testNeighbours() {
			if (roiManager != null) {
				if (roiManager.getSelectedRoisAsArray().length > 0) {
					selectionMode = true;
				} else {
					selectionMode = false;
				}
			} else {
				selectionMode = false;
			}
			
			if (imp.getStackSize()==1) { 
			ArrayList<ArrayList<String>> ListPolDistri = newGraphletImage.testNeighbours(
					cbSelectedShape.getSelectedIndex(), (int) inputRadiusNeigh.getValue(), imp, progressBar,
					selectionMode, cbGraphletsMode.getSelectedIndex(), overlayResult);

			ArrayList<String> polDistriGraphlets = ListPolDistri.get(0);
			lbtitlePolDistGraphlets.setText("Graphlets");
			lbtitlePolDistGraphlets.setFont(new Font("Tahoma", Font.BOLD, 14));
			lbSquares.setText(polDistriGraphlets.get(0));
			lbPentagons.setText(polDistriGraphlets.get(1));
			lbHexagons.setText(polDistriGraphlets.get(2));
			lbHeptagons.setText(polDistriGraphlets.get(3));
			lbOctogons.setText(polDistriGraphlets.get(4));

			if (ListPolDistri.size() > 1) {
				ArrayList<String> polDistriRoi = ListPolDistri.get(1);

				lbtitlePolDistRoi.setText("Rois");
				lbtitlePolDistRoi.setFont(new Font("Tahoma", Font.BOLD, 14));
				lbRoiSquares.setText(polDistriRoi.get(0));
				lbRoiPentagons.setText(polDistriRoi.get(1));
				lbRoiHexagons.setText(polDistriRoi.get(2));
				lbRoiHeptagons.setText(polDistriRoi.get(3));
				lbRoiOctogons.setText(polDistriRoi.get(4));
			}
			} else {
				ArrayList<ArrayList<String>> TotalListPolDistri = new ArrayList<>();
				for (int j = 0; j < imp.getStackSize(); j++) {
					
					ArrayList<ArrayList<String>> ListPolDistri = newGraphletImages.get(j)
						.testNeighbours(cbSelectedShape.getSelectedIndex(),
							(int) inputRadiusNeigh.getValue(), imp, progressBar,
							selectionMode, cbGraphletsMode.getSelectedIndex(), overlayResult);

					ArrayList<String> polDistriGraphlets = ListPolDistri.get(0);
					lbtitlePolDistGraphlets.setText("Graphlets");
					lbtitlePolDistGraphlets.setFont(new Font("Tahoma", Font.BOLD, 14));
					lbSquares.setText(polDistriGraphlets.get(0));
					lbPentagons.setText(polDistriGraphlets.get(1));
					lbHexagons.setText(polDistriGraphlets.get(2));
					lbHeptagons.setText(polDistriGraphlets.get(3));
					lbOctogons.setText(polDistriGraphlets.get(4));
					TotalListPolDistri.add(polDistriGraphlets);
					if (ListPolDistri.size() > 1) {
						ArrayList<String> polDistriRoi = ListPolDistri.get(1);
						ArrayList<ArrayList<String>> TotalpolDistriRoi = new ArrayList<>();
						lbtitlePolDistRoi.setText("Rois");
						lbtitlePolDistRoi.setFont(new Font("Tahoma", Font.BOLD, 14));
						lbRoiSquares.setText(polDistriRoi.get(0));
						lbRoiPentagons.setText(polDistriRoi.get(1));
						lbRoiHexagons.setText(polDistriRoi.get(2));
						lbRoiHeptagons.setText(polDistriRoi.get(3));
						lbRoiOctogons.setText(polDistriRoi.get(4));
						TotalpolDistriRoi.add(polDistriRoi);
					}
				}
			}
		

		}

		/**
		 * Label image and return an image with all the labels. In background
		 * 
		 * @throws Exception
		 *             Min cells exception
		 */
		public void labelImage() throws Exception {
			getImagePlus().deleteRoi(); 
			if (imp.getStackSize() == 1) {
				newGraphletImage.preprocessImage(imp, (int) cbConnectivity
					.getSelectedItem(), progressBar);
				TextRoi text;
				ImagePlus imageWithLabels = new ImagePlus("", imp.getChannelProcessor()
					.convertToRGB().duplicate());
				ArrayList<int[][]> centroids = newGraphletImage.getCentroids();
				for (int i = 0; i < centroids.size(); i++) {
					text = new TextRoi(centroids.get(i)[0][0], centroids.get(i)[0][1],
						Integer.toString(i + 1));
					text.setStrokeColor(Color.red);
					text.setLocation(centroids.get(i)[0][0] - (text.getFloatWidth() / 2),
						centroids.get(i)[0][1] - (text.getFloatHeight() / 2));
					imageWithLabels.getChannelProcessor().drawRoi(text);
				}
				newGraphletImage.setImageWithLabels(imageWithLabels);
				// canvas.addOverlay(new
				// ImageOverlay(imageWithLabels.getChannelProcessor()));
				imp.updateAndDraw();
				repaintAll();
			}
			else {
				for (int j = 0; j < imp.getStackSize(); j++) {
					ImageStack is = new ImageStack(); 
					is = imp.getStack(); 
					ImagePlus imp2 = new ImagePlus();
					ImageProcessor ip = is.getProcessor(j+1); // specify number of slice 
					imp2.setProcessor(ip); 
					GraphletImage newGraphletImage = new GraphletImage();
					newGraphletImage.preprocessImage(imp2, (int) cbConnectivity
						.getSelectedItem(), progressBar);
					TextRoi text;
					ImagePlus imageWithLabels = new ImagePlus(("Slice"+(j+1)), imp2
						.getChannelProcessor().convertToRGB().duplicate());
					ArrayList<int[][]> centroids = newGraphletImage.getCentroids();
					for (int i = 0; i < centroids.size(); i++) {
						text = new TextRoi(centroids.get(i)[0][0], centroids.get(i)[0][1],
							Integer.toString(i + 1));
						text.setStrokeColor(Color.red);
						text.setLocation(centroids.get(i)[0][0] - (text.getFloatWidth() /
							2), centroids.get(i)[0][1] - (text.getFloatHeight() / 2));
						imageWithLabels.getChannelProcessor().drawRoi(text);
					}
					
					newGraphletImage.setImageWithLabels(imageWithLabels);
					newGraphletImage.setLabelName("Slice"+(j+1));
					newGraphletImage.setColor(Color.red);
					newGraphletImages.add(j, newGraphletImage);

					// canvas.addOverlay(new
					// ImageOverlay(imageWithLabels.getChannelProcessor()));
					imp2.updateAndDraw();
					// recalculate minimum size of scroll panel
					repaintAll();

					// stack.addSlice(("Slice" + (j + 1)), imp.getChannelProcessor().convertToRGB().duplicate());
				}
			}
		}
	}
}
