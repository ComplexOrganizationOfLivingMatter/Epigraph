package epigraph;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import util.opencsv.CSVReader;

/**
 * Visualizing window, here you can visualize the points in the table and the
 * voronoi noise reference
 * 
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 */
public class VisualizingWindow extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	Scatter scatterData;
	Scatter scatterReference;

	private JPanel scatterpanel;
	private Chart chart;

	private JSlider slSizeOfPoints;

	private JButton btnExport;

	private JPanel canvasPanel;

	private JPanel buttonsPanel;

	JComboBox<String> cbGraphletsReference;

	JTableModel tableInfo;

	private Checkbox chbShowVoronoiReference;

	private JLabel lbSizeOfPoints;

	/**
	 * Constructor. Initialize the chart with the info from the main table. Also
	 * it add a reference in case you'd want to view it and compare the points
	 * with something reliable.
	 * 
	 * @param parent parent window
	 * @param tableInfo information of the points we're visualizing
	 */
	public VisualizingWindow(Frame parent, JTableModel tableInfo) {
		super(parent);

		this.tableInfo = tableInfo;

		initGUIItems();

		createScatterPlot(cbGraphletsReference.getSelectedIndex());
		createScatterData();

		initChart();

		initPanels();

		pack();

		setBounds(10, 10, 1000, 700);

		// chart.getView().getCamera().setScreenGridDisplayed(true);
	}

	/**
	 * Create the scatter with the reference voronoi noise
	 * 
	 * @param tableInfo
	 */
	@SuppressWarnings("unchecked")
	private void createScatterPlot(int referenceGraphlets) {
		List<String[]> voronoiReference = new ArrayList<String[]>();
		String fileName = null;

		switch (referenceGraphlets) {
		case 0:
			fileName = "/epigraph/voronoiNoiseReference/Total.txt";
			break;
		case 1:
			fileName = "/epigraph/voronoiNoiseReference/TotalPartial.txt";
			break;
		case 2:
			fileName = "/epigraph/voronoiNoiseReference/Basic.txt";
			break;
		case 3:
			fileName = "/epigraph/voronoiNoiseReference/BasicPartial.txt";
			break;
		}

		try {
			Reader reader = new InputStreamReader(Epigraph.class.getResourceAsStream(fileName));
			CSVReader csvReader = new CSVReader(reader, '\t');
			voronoiReference = csvReader.readAll();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] row;
		int voronoiReferenceSize = 0;

		voronoiReferenceSize = voronoiReference.size();

		Coord3d[] points = new Coord3d[voronoiReferenceSize];
		Color[] colors = new Color[voronoiReferenceSize];

		for (int i = 0; i < voronoiReferenceSize; i++) {
			// creating coord array
			row = voronoiReference.get(i);
			points[i] = new Coord3d(Float.parseFloat(row[2].replace(',', '.')),
					Float.parseFloat(row[3].replace(',', '.')), Float.parseFloat(row[1].replace(',', '.')));
			// creating color array
			colors[i] = new Color(Integer.parseInt(row[4]), Integer.parseInt(row[5]), Integer.parseInt(row[6]));
		}

		scatterReference = new Scatter(points, colors, (float) slSizeOfPoints.getValue());
	}

	/**
	 * Create a scatter plot with the data from the table of the main window
	 */
	private void createScatterData() {
		// Firstly, we look for the number of items, we want to show
		int size_array = 0;
		for (int i = 0; i < tableInfo.getRowCount(); i++) {
			if (tableInfo.getListOfSelected().get(i).booleanValue())
				size_array++;
		}

		Coord3d[] points = new Coord3d[size_array];
		Color[] colors = new Color[size_array];

		// Show only the points that we want to visualized, whose are ticked in
		// the visualizing table
		int numRow = 0;
		for (int i = 0; i < tableInfo.getRowCount(); i++) {
			if (tableInfo.getListOfSelected().get(i).booleanValue()) {
				// creating coord array
				points[numRow] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagonsGraphlets());
				// creating color array
				colors[numRow] = new Color(tableInfo.getAllGraphletImages().get(i).getColor().getRed(),
						tableInfo.getAllGraphletImages().get(i).getColor().getGreen(),
						tableInfo.getAllGraphletImages().get(i).getColor().getBlue());
				numRow++;
			}
		}

		// Insert into the scatter data, the points, the color of the points and
		// the size of the points
		scatterData = new Scatter(points, colors, (float) slSizeOfPoints.getValue());
	}

	/**
	 * Initialize gui items
	 */
	private void initGUIItems() {
		// The quality of the chart
		Quality q2 = new Quality(true, true, true, true, true, true, true);
		// A property to change dpi //TODO: not working
		q2.setPreserveViewportSize(false);
		// Create a chart from a quality and an awt chart
		chart = AWTChartComponentFactory.chart(q2, org.jzy3d.chart.factories.IChartComponentFactory.Toolkit.awt);
		// Add mouse and keyboard handlers, such as move the camera with the
		// mouse.
		chart.addMouseCameraController();

		// Init slider of point size
		slSizeOfPoints = new JSlider(3, 30, 10);
		slSizeOfPoints.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// We change the size of the points with the input of the slider
				scatterData.setWidth((float) slSizeOfPoints.getValue());
				scatterReference.setWidth((float) slSizeOfPoints.getValue());
			}
		});

		lbSizeOfPoints = new JLabel("Size of dots: ");
		lbSizeOfPoints.setLabelFor(slSizeOfPoints);

		btnExport = new JButton("Export view");
		btnExport.addActionListener(this);

		cbGraphletsReference = new JComboBox<String>();
		cbGraphletsReference.setModel(new DefaultComboBoxModel<String>(GraphletImage.KIND_OF_GRAPHLETS));
		cbGraphletsReference.setSelectedIndex(0);
		cbGraphletsReference.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Scatter oldScatter = scatterReference;

				createScatterPlot(cbGraphletsReference.getSelectedIndex());

				chart.getScene().add(scatterReference);

				chart.getScene().remove(oldScatter);
				repaintAll();
			}
		});

		chbShowVoronoiReference = new Checkbox("Show reference", true);
		chbShowVoronoiReference.setState(true);
		chbShowVoronoiReference.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				scatterReference.setDisplayed(chbShowVoronoiReference.getState());
				cbGraphletsReference.setEnabled(chbShowVoronoiReference.getState());
			}
		});
	}

	/**
	 * Repaint all the window
	 */
	private void repaintAll() {

		getContentPane().repaint();

		this.chart.setScale(new Scale(0, 100));
	}

	/**
	 * Initialize all the panels in the window
	 */
	private void initPanels() {
		GridBagLayout genericPanelLayout = new GridBagLayout();
		GridBagConstraints genericPanelConstrainst = new GridBagConstraints();
		genericPanelConstrainst.anchor = GridBagConstraints.CENTER;
		genericPanelConstrainst.fill = GridBagConstraints.BOTH;
		resetConstrainst(genericPanelConstrainst);
		genericPanelConstrainst.insets = new Insets(5, 5, 6, 6);

		// LEFT PANEL
		canvasPanel = new JPanel(new GridLayout(1, 0));
		resetConstrainst(genericPanelConstrainst);

		canvasPanel.add((Component) chart.getCanvas());

		// RIGHT PANEL
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(genericPanelLayout);
		resetConstrainst(genericPanelConstrainst);

		buttonsPanel.add(chbShowVoronoiReference, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(lbSizeOfPoints, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(slSizeOfPoints, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(cbGraphletsReference, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(btnExport, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;

		// GENERAL PANEL
		scatterpanel = new JPanel();
		scatterpanel.setLayout(genericPanelLayout);
		resetConstrainst(genericPanelConstrainst);

		genericPanelConstrainst.weightx = 2;
		genericPanelConstrainst.weighty = 2;
		scatterpanel.add(canvasPanel, genericPanelConstrainst);
		resetConstrainst(genericPanelConstrainst);
		genericPanelConstrainst.gridx++;
		scatterpanel.add(buttonsPanel, genericPanelConstrainst);
		genericPanelConstrainst.gridx++;

		add(scatterpanel);
	}

	/**
	 * Reset constraints of the panel
	 * 
	 * @param genericPanelConstrainst
	 */
	private void resetConstrainst(GridBagConstraints genericPanelConstrainst) {
		genericPanelConstrainst.gridwidth = 1;
		genericPanelConstrainst.gridheight = 1;
		genericPanelConstrainst.gridx = 0;
		genericPanelConstrainst.gridy = 0;
		genericPanelConstrainst.weighty = 0;
		genericPanelConstrainst.weightx = 0;
	}

	/**
	 * Init the chart with the different options: - Proper axes. - Scale from 0
	 * to 100 (axe Y) - Add the data to the the scene - Add the reference data
	 * to the scene
	 */
	private void initChart() {

		IAxeLayout l = this.chart.getAxeLayout();

		// Labelling axes
		l.setXAxeLabel("GDDRV");
		l.setYAxeLabel("GDDH");
		l.setZAxeLabel("Percentage of hexagons");

		l.setXTickRenderer(new FixedDecimalTickRenderer(2));
		l.setYTickRenderer(new FixedDecimalTickRenderer(2));
		l.setZTickRenderer(new FixedDecimalTickRenderer(2));

		this.chart.getScene().add(scatterData);
		this.chart.getScene().add(scatterReference);

		this.chart.setScale(new Scale(0, 100));
	}

	/*
	 * Group all the actions, we'd want to perform with the buttons. Right there
	 * is only 1 button, "Export view". (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnExport) {
			JFileChooser fileChooser = new JFileChooser();
			// set it to be a save dialog
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			// set a default filename (this is where you default extension
			// first comes in)
			fileChooser.setSelectedFile(new File("screenshoot.png"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int userSelection = fileChooser.showSaveDialog(btnExport.getParent());
			if (userSelection == JFileChooser.APPROVE_OPTION) {

				String filename = fileChooser.getSelectedFile().toString();
				
				if (!filename.endsWith(".png"))
					filename += ".png";
				
				if ((fileChooser.getSelectedFile() != null) && fileChooser.getSelectedFile().exists()) {
			        int response = JOptionPane.showConfirmDialog(this,
			          "The file " + fileChooser.getSelectedFile().getName() + 
			          " already exists. Do you want to replace the existing file?",
			          "Ovewrite file", JOptionPane.YES_NO_OPTION,
			          JOptionPane.WARNING_MESSAGE);
			        if (response != JOptionPane.YES_OPTION)
			          return;
			     }
				
				
				

				((CanvasAWT) chart.getCanvas()).setPixelScale(new float[] { 0.1f, 0.1f });
				// Quality q = new Quality(true, false, true, true, true, true,
				// false);
				// q.setPreserveViewportSize(false);
				// Chart exportChart = AWTChartComponentFactory.chart(q,
				// "offscreen,1024,1024");
				// //exportChart.getCanvas().setPixelScale(new float[] { 0.1f,
				// 0.1f });
				// Scatter newScatter = new Scatter();
				// initChart(exportChart, points, colors, newScatter, 300);

				File f = new File(filename);
				try {
					// TextureData p = exportChart.screenshot(f);
					chart.screenshot(f);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}