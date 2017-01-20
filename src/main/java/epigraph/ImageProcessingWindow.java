package epigraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

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
import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;

public class ImageProcessingWindow extends ImageWindow implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
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
	private ImageCanvas canvas;
	

	/**
	 * 
	 * @param raw_img
	 * @param tableInfo
	 */
	ImageProcessingWindow(ImagePlus raw_img, JTableModel tableInfo) {
		super(raw_img, raw_img.getCanvas());

		newGraphletImages = new ArrayList<GraphletImage>();

		tableInf = tableInfo;

		newGraphletImage = new GraphletImage(raw_img);
		
		canvas = getCanvas();
		Dimension dim = new Dimension(Math.min(512, raw_img.getWidth()), Math.min(512, raw_img.getHeight()));
		canvas.setMinimumSize(dim);
		canvas.setSize(dim.width, dim.height);
			
		canvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				Rectangle r = canvas.getBounds();
				canvas.setSize(r.width, r.height);
			}
		});	
		
		addPanel();
		
	}

	/**
	 * 
	 */
	void addPanel() {
		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		progressBar = new JProgressBar();

		btnCalculateGraphlets = new JButton("Calculate graphlets!");
		btnCalculateGraphlets.addActionListener(this);
		IbtnCalculateGraphlets = btnCalculateGraphlets.hashCode();
		panel.add(btnCalculateGraphlets);

		btnAddToTable = new JButton("add to table");
		btnAddToTable.setEnabled(false);
		IbtnAddToTable = btnAddToTable.hashCode();
		btnAddToTable.addActionListener(this);
		panel.add(btnAddToTable);

		btnCreateRoi = new JButton("Create RoI");
		btnCreateRoi.addActionListener(this);
		IbtnCreateRoi = btnCreateRoi.hashCode();
		panel.add(btnCreateRoi);

		btnPickAColor = new JButton("Pick a color");
		btnPickAColor.addActionListener(this);
		IbtnPickAColor = btnPickAColor.hashCode();
		panel.add(btnPickAColor);

		btnTestNeighbours = new JButton("Test Neighbours");
		btnTestNeighbours.addActionListener(this);
		panel.add(btnTestNeighbours);
		IbtnTestNeighbours = btnTestNeighbours.hashCode();

		tfImageName = new JTextField();
		panel.add(tfImageName);

		add(panel);
		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Point loc = getLocation();
		Dimension size = getSize();
		if (loc.y + size.height > screen.height)
			getCanvas().zoomOut(0, 0);
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
