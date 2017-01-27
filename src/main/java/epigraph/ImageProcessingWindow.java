package epigraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

/**
 * 
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 *
 */
public class ImageProcessingWindow extends ImageWindow implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<GraphletImage> newGraphletImages;

	private ImageOverlay overlayResult;
	private GraphletImage newGraphletImage;
	private JTextField tfImageName;
	private JButton btnCreateRoi, btnCalculateGraphlets, btnTestNeighbours, btnPickAColor, btnAddToTable;
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

	private JLabel lbImageLegend;

	private JLabel lbSquares;
	private JLabel lbPentagons;
	private JLabel lbHexagons;
	private JLabel lbHeptagons;
	private JLabel lbOctogons;
	private JLabel lblShape;

	private Panel all = new Panel();
	private JPanel graphletsPanel;
	private JPanel polDistPanel;
	private JPanel imgPolDistPanel;
	private RoiManager roiManager;
	private JButton btnSelectCells;
	private JPanel roiPanel;
	private JButton btnToggleOverlay;
	private JButton btnSelectInvalidRegion;

	private boolean selectionMode;
	private Roi invalidRegionRoi;

	/**
	 * 
	 * @param raw_img
	 * @param tableInfo
	 */
	ImageProcessingWindow(ImagePlus raw_img, JTableModel tableInfo) {
		super(raw_img, new CustomCanvas(raw_img));

		canvas = (CustomCanvas) getCanvas();

		newGraphletImages = new ArrayList<GraphletImage>();

		tableInf = tableInfo;

		newGraphletImage = new GraphletImage(raw_img);
		removeAll();

		initGUI();

	}

	/**
	 * 
	 */
	void initGUI() {

		initializeGUIItems();

		/* Generic panel layout */
		GridBagLayout genericPanelLayout = new GridBagLayout();
		GridBagConstraints genericPanelConstrainst = new GridBagConstraints();
		genericPanelConstrainst.anchor = GridBagConstraints.NORTHWEST;
		genericPanelConstrainst.fill = GridBagConstraints.BOTH;
		resetGenericConstrainst(genericPanelConstrainst);
		genericPanelConstrainst.insets = new Insets(5, 5, 6, 6);

		/* RIGHT PANEL FORMED BY THESE 2 PANELS */
		// Setup the config panel
		configPanel = new JPanel();
		configPanel.setLayout(genericPanelLayout);

		// Adding to the panel all the buttons
		configPanel.add(btnToggleOverlay, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		configPanel.add(lblRadius, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		configPanel.add(inputRadiusNeigh, genericPanelConstrainst);
		genericPanelConstrainst.gridx--;
		genericPanelConstrainst.gridy++;
		configPanel.add(lblShape, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		configPanel.add(cbSelectedShape, genericPanelConstrainst);
		genericPanelConstrainst.gridx--;
		genericPanelConstrainst.gridy++;
		configPanel.add(btnTestNeighbours, genericPanelConstrainst);

		// Selection ROI panel
		roiPanel = new JPanel();
		roiPanel.setLayout(genericPanelLayout);

		roiPanel.add(btnCreateRoi, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		roiPanel.add(btnSelectCells, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		roiPanel.add(btnSelectInvalidRegion, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;

		// Graphlet Image properties
		graphletsPanel = new JPanel();
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
		genericPanelConstrainst.gridx++;
		graphletsPanel.add(btnAddToTable, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		genericPanelConstrainst.gridx--;
		graphletsPanel.add(progressBar, genericPanelConstrainst);

		/* LEFT PANEL */
		// Image of polygon distribution
		imgPolDistPanel = new JPanel();
		resetGenericConstrainst(genericPanelConstrainst);
		genericPanelConstrainst.weighty = 0.5;
		imgPolDistPanel.setLayout(genericPanelLayout);
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
		polDistPanelConstrainst.insets = new Insets(5, 5, 6, 6);
		polDistPanelConstrainst.weighty = 1;

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
	 * 
	 */
	private void initializeGUIItems() {
		canvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				Rectangle r = canvas.getBounds();
				canvas.setDstDimensions(r.width, r.height);
			}
		});

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

		btnCalculateGraphlets = new JButton("Calculate graphlets!");
		btnCalculateGraphlets.addActionListener(this);

		cbGraphletsMode = new JComboBox<String>();
		cbGraphletsMode.setModel(new DefaultComboBoxModel<String>(new String[] { "Total (25 graphlets)",
				"Total Partial (16 graphlets)", "Basic (9 graphlets)", "Basic Partial (7 graphlets) " }));
		cbGraphletsMode.setSelectedIndex(0);

		btnAddToTable = new JButton("add to table");
		btnAddToTable.setEnabled(false);
		btnAddToTable.addActionListener(this);

		btnCreateRoi = new JButton("Create RoI");
		btnCreateRoi.addActionListener(this);

		btnSelectCells = new JButton("Select cells");
		btnSelectCells.addActionListener(this);

		btnPickAColor = new JButton("Pick a color");
		btnPickAColor.addActionListener(this);

		colorPicked = new JPanel();
		colorPicked.setBackground(Color.BLACK);

		btnTestNeighbours = new JButton("Test Neighbours");
		btnTestNeighbours.addActionListener(this);

		btnToggleOverlay = new JButton("Toggle overlay");
		btnToggleOverlay.addActionListener(this);

		btnSelectInvalidRegion = new JButton("Add invalid regions");
		btnSelectInvalidRegion.addActionListener(this);

		tfImageName = new JTextField();

		lblImageName = new JLabel("Image label:");
		lblImageName.setLabelFor(tfImageName);

		// Labels for polygon distribution
		lbImageLegend = new JLabel("");
		lbImageLegend.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/legend.jpg")).getImage()));

		lbSquares = new JLabel("");
		lbSquares.setHorizontalAlignment(SwingConstants.CENTER);

		lbPentagons = new JLabel("");
		lbPentagons.setHorizontalAlignment(SwingConstants.CENTER);
		lbPentagons.setVisible(false);

		lbHexagons = new JLabel("");
		lbHexagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbHeptagons = new JLabel("");
		lbHeptagons.setHorizontalAlignment(SwingConstants.CENTER);

		lbOctogons = new JLabel("");
		lbOctogons.setHorizontalAlignment(SwingConstants.CENTER);

	}

	/**
	 * @param genericPanelConstrainst
	 */
	private void resetGenericConstrainst(GridBagConstraints genericPanelConstrainst) {
		genericPanelConstrainst.gridwidth = 1;
		genericPanelConstrainst.gridheight = 1;
		genericPanelConstrainst.gridx = 0;
		genericPanelConstrainst.gridy = 0;
		genericPanelConstrainst.weighty = 0;
		genericPanelConstrainst.weightx = 0;
	}

	private void setupPanels() {
		/* DEFINITION OF RIGHT SIDE PANEL */
		GridBagLayout buttonsLayout = new GridBagLayout();
		GridBagConstraints buttonsConstraints = new GridBagConstraints();
		buttonsPanel.setLayout(buttonsLayout);
		buttonsConstraints.anchor = GridBagConstraints.NORTHWEST;
		buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;
		resetGenericConstrainst(buttonsConstraints);
		buttonsPanel.add(configPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsPanel.add(roiPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsPanel.add(graphletsPanel, buttonsConstraints);
		buttonsConstraints.insets = new Insets(5, 5, 6, 6);

		/* DEFINITION OF LEFT SIDE PANEL */
		GridBagLayout labelsLayout = new GridBagLayout();
		GridBagConstraints labelsConstraints = new GridBagConstraints();
		labelsJPanel.setLayout(labelsLayout);
		labelsConstraints.anchor = GridBagConstraints.NORTHWEST;
		labelsConstraints.fill = GridBagConstraints.BOTH;
		resetGenericConstrainst(labelsConstraints);
		labelsJPanel.add(imgPolDistPanel, labelsConstraints);
		labelsConstraints.gridx++;
		labelsJPanel.add(polDistPanel, labelsConstraints);

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

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnCalculateGraphlets) {
			btnTestNeighbours.setEnabled(false);
			btnCalculateGraphlets.setEnabled(false);
			Task task = new Task();
			task.execute();
			newGraphletImage.setLabelName(tfImageName.getText());
			newGraphletImage.setColor(colorPicked.getBackground());
		}

		if (e.getSource() == btnCreateRoi) {
			if (btnCreateRoi.getText() != "Done") {
				Epigraph.callToolbarRectangle();
				openRoiManager();
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
		}

		if (e.getSource() == btnAddToTable) {
			if (tfImageName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this.getParent(), "You should insert a name for the image");
			} else {
				newGraphletImage.setLabelName(tfImageName.getText());
				int result = JOptionPane.showConfirmDialog(this.getParent(), "Everything is ok?", "Confirm",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
					tableInf.addImage(newGraphletImage, cbGraphletsMode.getSelectedItem().toString());
			}
		}
		if (e.getSource() == btnPickAColor) {
			Color c = JColorChooser.showDialog(this.getParent(), "Choose a Color", colorPicked.getBackground());
			if (c != null) {
				colorPicked.setBackground(c);
				if (newGraphletImage != null) {
					newGraphletImage.setColor(c);
				}
			}
		}
		if (e.getSource() == btnTestNeighbours) {
			ArrayList<String> polDistri;
			if (roiManager != null) {
				if (roiManager.getSelectedRoisAsArray().length > 0) {
					selectionMode = true;
				} else {
					selectionMode = false;
				}
			} else {
				selectionMode = false;
			}
			
			polDistri = newGraphletImage.testNeighbours(cbSelectedShape.getSelectedIndex(),
					(int) inputRadiusNeigh.getValue(), imp, progressBar, selectionMode,
					cbGraphletsMode.getSelectedIndex(), overlayResult);

			lbSquares.setText(polDistri.get(0));
			lbPentagons.setText(polDistri.get(1));
			lbPentagons.setVisible(true);
			lbHexagons.setText(polDistri.get(2));
			lbHeptagons.setText(polDistri.get(3));
			lbOctogons.setText(polDistri.get(4));

			repaintAll();
		}

		if (e.getSource() == btnSelectCells) {
			if (btnSelectCells.getText() != "Done") {
				openRoiManager();
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
		}

		if (e.getSource() == btnToggleOverlay) {
			if (imp.getOverlay() == null && overlayResult != null) {
				canvas.addOverlay(overlayResult);
			} else {
				canvas.clearOverlay();
			}
		}

		if (e.getSource() == btnSelectInvalidRegion) {
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
		}

		imp.updateAndDraw();
		ImageCanvas ic = imp.getCanvas();
		if (ic != null)
			ic.requestFocus();
	}

	public void disableActionButtons() {
		btnCalculateGraphlets.setEnabled(false);
		btnAddToTable.setEnabled(false);
		btnTestNeighbours.setEnabled(false);
	}

	public void enableActionButtons() {
		btnCalculateGraphlets.setEnabled(true);
		btnAddToTable.setEnabled(true);
		btnTestNeighbours.setEnabled(true);
	}

	private void addInvalidRegion() {
		Roi r = this.getImagePlus().getRoi();
		if (r != null) {
			this.newGraphletImage.resetInvalidRegion();
			for (Point point : r) {
				int[] pixelInfo = newGraphletImage.getLabelledImage().getPixel(point.x, point.y);
				this.newGraphletImage.addCellToInvalidRegion(pixelInfo[0]);
			}
			invalidRegionRoi = r;
		}
		this.getImagePlus().deleteRoi();
	}

	/**
	 * 
	 */
	private void addROI() {
		Roi r = this.getImagePlus().getRoi();
		if (r != null) {
			roiManager.addRoi(r);
		}
		this.getImagePlus().deleteRoi();
	}

	/**
	 * Repaint all panels
	 */
	private void repaintAll() {
		getCanvas().repaint();
		this.labelsJPanel.repaint();
		this.buttonsPanel.repaint();
		this.all.repaint();
	}
	
	private void openRoiManager(){
		if (roiManager == null)
			roiManager = RoiManager.getRoiManager();
		else
			roiManager.toFront();
	}

	/**
	 * 
	 * @author Pablo Vicente-Munuera
	 *
	 */
	public class Task extends SwingWorker<Void, Void> {
		/**
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			setProgress(0);
			if (roiManager != null) {
				Roi[] roiArray = roiManager.getSelectedRoisAsArray();
				newGraphletImage.runGraphlets(cbSelectedShape.getSelectedIndex(), (int) inputRadiusNeigh.getValue(),
						(int) cbGraphletsMode.getSelectedIndex(), progressBar, roiArray.length > 0, overlayResult);
			} else {
				newGraphletImage.runGraphlets(cbSelectedShape.getSelectedIndex(), (int) inputRadiusNeigh.getValue(),
						(int) cbGraphletsMode.getSelectedIndex(), progressBar, false, overlayResult);
			}

			return null;
		}

		/**
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			btnCalculateGraphlets.setEnabled(true);
			btnTestNeighbours.setEnabled(true);
			btnAddToTable.setEnabled(true);
		}
	}
}
