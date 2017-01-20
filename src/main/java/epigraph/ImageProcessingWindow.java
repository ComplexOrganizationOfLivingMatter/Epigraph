package epigraph;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;

public class ImageProcessingWindow extends ImageWindow implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<GraphletImage> newGraphletImages;
	private Integer Ibutton1, Ibutton2, IbtnCreateRoi, IbtnCalculateGraphlets, IbtnTestNeighbours, IbtnPickAColor,
			IbtnAddToTable;
	private GraphletImage newGraphletImage;
	private JTextField tfImageName;
	private ImagePlus imgToShow, actualRawImage;
	private JButton button1, button2, btnCreateRoi, btnCalculateGraphlets, btnTestNeighbours, btnPickAColor,
			btnAddToTable;
	private JComboBox<String> cbSelectedShape, cbGraphletsMode;
	private JLabel lblRadius, lblImageName, legend, Lsquares, Lpentagons, Lhexagons, Lheptagons, Loctogons;
	private JSpinner inputRadiusNeigh;
	private JPanel colorPicked;
	private JProgressBar progressBar;
	private JTableModel tableInf;
	private CustomCanvas canvas;
	private JPanel configPanel = new JPanel();
	private Container buttonsPanel = new Container();
	
	private Panel all = new Panel();

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

		addPanel();

	}

	/**
	 * 
	 */
	void addPanel() {

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
		IbtnCalculateGraphlets = btnCalculateGraphlets.hashCode();

		btnAddToTable = new JButton("add to table");
		btnAddToTable.setEnabled(false);
		IbtnAddToTable = btnAddToTable.hashCode();
		btnAddToTable.addActionListener(this);

		btnCreateRoi = new JButton("Create RoI");
		btnCreateRoi.addActionListener(this);
		IbtnCreateRoi = btnCreateRoi.hashCode();

		btnPickAColor = new JButton("Pick a color");
		btnPickAColor.addActionListener(this);
		IbtnPickAColor = btnPickAColor.hashCode();

		btnTestNeighbours = new JButton("Test Neighbours");
		btnTestNeighbours.addActionListener(this);
		IbtnTestNeighbours = btnTestNeighbours.hashCode();

		tfImageName = new JTextField();

		// Setup the config panel
		configPanel = new JPanel();
		configPanel.setBorder(BorderFactory.createTitledBorder("Training"));
		GridBagLayout configPanelLayout = new GridBagLayout();
		GridBagConstraints configPanelConstrainst = new GridBagConstraints();
		configPanelConstrainst.anchor = GridBagConstraints.NORTHEAST;
		configPanelConstrainst.fill = GridBagConstraints.HORIZONTAL;
		configPanelConstrainst.gridwidth = 1;
		configPanelConstrainst.gridheight = 1;
		configPanelConstrainst.gridx = 0;
		configPanelConstrainst.gridy = 0;
		configPanelConstrainst.insets = new Insets(5, 5, 6, 6);
		configPanel.setLayout(configPanelLayout);

		// Adding to the panel all the buttons
		configPanel.add(inputRadiusNeigh, configPanelConstrainst);
		configPanelConstrainst.gridy++;
		configPanel.add(cbSelectedShape, configPanelConstrainst);
		configPanelConstrainst.gridy++;
		configPanel.add(btnTestNeighbours, configPanelConstrainst);

		setupPanel();

		pack();
		setMinimumSize(getPreferredSize());
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Point loc = getLocation();
		Dimension size = getSize();
		if (loc.y + size.height > screen.height)
			getCanvas().zoomOut(0, 0);

		
	}

	private void setupPanel() {
		// TODO Auto-generated method stub
		// Buttons panel (including training and options)
		GridBagLayout buttonsLayout = new GridBagLayout();
		GridBagConstraints buttonsConstraints = new GridBagConstraints();
		buttonsPanel.setLayout(buttonsLayout);
		buttonsConstraints.anchor = GridBagConstraints.NORTHWEST;
		buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;
		buttonsConstraints.gridwidth = 1;
		buttonsConstraints.gridheight = 1;
		buttonsConstraints.gridx = 0;
		buttonsConstraints.gridy = 0;
		buttonsPanel.add(configPanel, buttonsConstraints);
		buttonsConstraints.gridy++;
		buttonsConstraints.insets = new Insets(5, 5, 6, 6);

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints allConstraints = new GridBagConstraints();
		all.setLayout(layout);

		allConstraints.anchor = GridBagConstraints.NORTHWEST;
		allConstraints.fill = GridBagConstraints.BOTH;
		allConstraints.gridwidth = 1;
		allConstraints.gridheight = 2;
		allConstraints.gridx = 0;
		allConstraints.gridy = 0;
		allConstraints.weightx = 0;
		allConstraints.weighty = 0;
		all.add(buttonsPanel, allConstraints);

		allConstraints.gridx++;
		allConstraints.weightx = 1;
		allConstraints.weighty = 1;
		allConstraints.gridheight = 1;
		all.add(canvas, allConstraints);

		allConstraints.gridy++;
		allConstraints.weightx = 0;
		allConstraints.weighty = 0;
		allConstraints.gridy--;

		allConstraints.gridx++;
		allConstraints.anchor = GridBagConstraints.NORTHEAST;
		allConstraints.weightx = 0;
		allConstraints.weighty = 0;
		allConstraints.gridheight = 1;

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

		}

		if (e.getSource() == btnAddToTable) {
			if (tfImageName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(btnAddToTable.getParent(), "You should insert a name for the image");
			} else {
				newGraphletImage.setLabelName(tfImageName.getText());
				int result = JOptionPane.showConfirmDialog(btnAddToTable.getParent(), "Everything is ok?", "Confirm",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
					tableInf.addImage(newGraphletImage);
			}
		}
		if (e.getSource() == btnPickAColor) {
			Color c = JColorChooser.showDialog(btnPickAColor.getParent(), "Choose a Color",
					colorPicked.getBackground());
			if (c != null) {
				colorPicked.setBackground(c);
				if (newGraphletImage != null) {
					newGraphletImage.setColor(c);
				}
			}
		}
		if (e.getSource() == btnTestNeighbours) {
			ArrayList<String> polDistri = newGraphletImage.testNeighbours(imp, cbSelectedShape.getSelectedIndex(),
					(int) inputRadiusNeigh.getValue(), imgToShow, progressBar);

			Lsquares.setText(polDistri.get(0));
			Lpentagons.setText(polDistri.get(1));
			Lhexagons.setText(polDistri.get(2));
			Lheptagons.setText(polDistri.get(3));
			Loctogons.setText(polDistri.get(4));

			legend.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/legend.jpg")).getImage()));
			// LTitlePoligonDistr.setText("Tested polygon distribution:");
			// canvas.repaint();
		}

		ImageCanvas ic = imp.getCanvas();
		if (ic != null)
			ic.requestFocus();
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
}
