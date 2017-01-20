package epigraph;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
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

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.frame.RoiManager;

public class ImageProcessingWindow extends ImageWindow implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<GraphletImage> newGraphletImages;
	
	private GraphletImage newGraphletImage;
	private JTextField tfImageName;
	private ImagePlus imgToShow, actualRawImage;
	private JButton button1, button2, btnCreateRoi, btnCalculateGraphlets, btnTestNeighbours, btnPickAColor,
			btnAddToTable;
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

	/**
	 * 
	 * @param raw_img
	 * @param tableInfo
	 */
	ImageProcessingWindow(ImagePlus raw_img, JTableModel tableInfo) {
		super(raw_img, new CustomCanvas(raw_img));
		
		IJ.log("Initializing...");

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

		/* RIGHT PANEL FORMED BY THESE 2 PANELS*/
		// Setup the config panel
		configPanel = new JPanel();
		configPanel.setLayout(genericPanelLayout);

		// Adding to the panel all the buttons
		configPanel.add(inputRadiusNeigh, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		configPanel.add(cbSelectedShape, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		configPanel.add(btnTestNeighbours, genericPanelConstrainst);
		
		// Selection ROI panel
		roiPanel = new JPanel();
		roiPanel.setLayout(genericPanelLayout);
		
		roiPanel.add(btnCreateRoi, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		roiPanel.add(btnSelectCells, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		
		// Graphlet Image properties
		graphletsPanel = new JPanel();
		resetGenericConstrainst(genericPanelConstrainst);
		graphletsPanel.setLayout(genericPanelLayout);

		// Adding buttons to panel
		graphletsPanel.add(tfImageName, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(btnPickAColor, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		graphletsPanel.add(colorPicked, genericPanelConstrainst);
		genericPanelConstrainst.gridx--;
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(btnCalculateGraphlets, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		graphletsPanel.add(progressBar, genericPanelConstrainst);
		
		/* LEFT PANEL */
		//Image of polygon distribution
		imgPolDistPanel = new JPanel();
		resetGenericConstrainst(genericPanelConstrainst);
		imgPolDistPanel.setLayout(genericPanelLayout);
		imgPolDistPanel.add(lbImageLegend, genericPanelConstrainst);
		
		//labels of polygon distribution
		polDistPanel = new JPanel();
		resetGenericConstrainst(genericPanelConstrainst);
		polDistPanel.setLayout(genericPanelLayout);
		polDistPanel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		//Adding buttons
		polDistPanel.add(lbSquares, genericPanelConstrainst);
		genericPanelConstrainst.gridy += 1;
		polDistPanel.add(lbSquares, genericPanelConstrainst);
		genericPanelConstrainst.gridy += 1;
		polDistPanel.add(lbPentagons, genericPanelConstrainst);
		genericPanelConstrainst.gridy += 1;
		polDistPanel.add(lbHexagons, genericPanelConstrainst);
		genericPanelConstrainst.gridy += 1;
		polDistPanel.add(lbHeptagons, genericPanelConstrainst);
		genericPanelConstrainst.gridy += 1;
		polDistPanel.add(lbOctogons, genericPanelConstrainst);
		genericPanelConstrainst.gridy += 1;
		
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

		// The shape of the mask
		cbSelectedShape = new JComboBox<String>();
		cbSelectedShape.setModel(new DefaultComboBoxModel<String>(new String[] { "Circle", "Square" }));
		cbSelectedShape.setSelectedIndex(0);

		progressBar = new JProgressBar();

		btnCalculateGraphlets = new JButton("Calculate graphlets!");
		btnCalculateGraphlets.addActionListener(this);

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

		tfImageName = new JTextField();
		
		//Labels for polygon distribution
		lbImageLegend = new JLabel("");
		
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
		
	}

	/**
	 * @param genericPanelConstrainst
	 */
	private void resetGenericConstrainst(GridBagConstraints genericPanelConstrainst) {
		genericPanelConstrainst.gridwidth = 1;
		genericPanelConstrainst.gridheight = 1;
		genericPanelConstrainst.gridx = 0;
		genericPanelConstrainst.gridy = 0;
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
		buttonsPanel.add(graphletsPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsPanel.add(roiPanel, buttonsConstraints);
		buttonsConstraints.insets = new Insets(5, 5, 6, 6);
		
		/* DEFINITION OF LEFT SIDE PANEL */
		GridBagLayout labelsLayout = new GridBagLayout();
		GridBagConstraints labelsConstraints = new GridBagConstraints();
		labelsJPanel.setLayout( labelsLayout );
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
		if (e.getSource() == btnCreateRoi) {
			RoiManager.getRoiManager();
		}

		if (e.getSource() == btnAddToTable) {
			if (tfImageName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this.getParent(), "You should insert a name for the image");
			} else {
				newGraphletImage.setLabelName(tfImageName.getText());
				int result = JOptionPane.showConfirmDialog(this.getParent(), "Everything is ok?", "Confirm",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
					tableInf.addImage(newGraphletImage);
			}
		}
		if (e.getSource() == btnPickAColor) {
			Color c = JColorChooser.showDialog(this.getParent(), "Choose a Color",
					colorPicked.getBackground());
			if (c != null) {
				colorPicked.setBackground(c);
				if (newGraphletImage != null) {
					newGraphletImage.setColor(c);
				}
			}
		}
		if (e.getSource() == btnTestNeighbours) {
			ArrayList<String> polDistri = newGraphletImage.testNeighbours(cbSelectedShape.getSelectedIndex(),
					(int) inputRadiusNeigh.getValue(), imp, progressBar);

			lbSquares.setText(polDistri.get(0));
			lbPentagons.setText(polDistri.get(1));
			lbHexagons.setText(polDistri.get(2));
			lbHeptagons.setText(polDistri.get(3));
			lbOctogons.setText(polDistri.get(4));

			lbImageLegend.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/legend.jpg")).getImage()));
			repaintAll();
		}
		
		if (e.getSource() == btnSelectCells){
			if (btnSelectCells.getText() != "Done"){
				Epigraph.callToolbarPoint();
				btnSelectCells.setText("Done");
			} else {
				//Add selected cells
				btnSelectCells.setText("Select cells");
			}
		}

		ImageCanvas ic = imp.getCanvas();
		if (ic != null)
			ic.requestFocus();
	}

	/**
	 * Repaint all panels
	 */
	private void repaintAll()
	{
		this.labelsJPanel.repaint();
		getCanvas().repaint();
		this.buttonsPanel.repaint();
		this.all.repaint();
	}

	public class Task extends SwingWorker<Void, Void> {

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			setProgress(0);
			newGraphletImage.runGraphlets(cbSelectedShape.getSelectedIndex(),
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
}
