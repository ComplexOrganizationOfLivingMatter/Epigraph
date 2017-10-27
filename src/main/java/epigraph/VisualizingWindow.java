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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import org.jzy3d.maths.BoundingBox3d;
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
	private RangeSlider rangeSliderX;
	private RangeSlider rangeSliderY;
	private RangeSlider rangeSliderZ;
	private JSlider slSizeOfPoints;

	private JButton btnExport;

	private JPanel canvasPanel;
	private JPanel buttonsPanel;

	JComboBox<String> cbGraphletsReference;
	JComboBox<String> cbAxesToRepresent;

	JTableModel tableInfo;

	private Checkbox chbShowVoronoiReference;

	private JLabel lbSizeOfPoints;
	private JLabel xyzLabel;
	private JLabel referenceMotifs;
	private JLabel axisXinterval;
	private JLabel axisYinterval;
	private JLabel axisZinterval;

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
		
		createScatterData();
		createScatterPlot(cbGraphletsReference.getSelectedIndex(),cbAxesToRepresent.getSelectedIndex());
		this.chart.getScene().add(scatterData);
		this.chart.getScene().add(scatterReference);
		

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
	private void createScatterPlot(int referenceGraphlets,int referenceAxes) {
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

		Coord3d[] pointsAxes1 = new Coord3d[voronoiReferenceSize];
		Coord3d[] pointsAxes2 = new Coord3d[voronoiReferenceSize];
		Coord3d[] pointsAxes3 = new Coord3d[voronoiReferenceSize];
		Coord3d[] pointsAxes4 = new Coord3d[voronoiReferenceSize];
		
		Color[] colors = new Color[voronoiReferenceSize];

		for (int i = 0; i < voronoiReferenceSize; i++) {
			// creating coord array
			row = voronoiReference.get(i);
			pointsAxes1[i] = new Coord3d(Float.parseFloat(row[3].replace(',', '.')),
					Float.parseFloat(row[2].replace(',', '.')), Float.parseFloat(row[1].replace(',', '.')));
			pointsAxes2[i] = new Coord3d(Float.parseFloat(row[3].replace(',', '.')),
					Float.parseFloat(row[2].replace(',', '.')), Float.parseFloat(row[7].replace(',', '.')));
			pointsAxes3[i] = new Coord3d(Float.parseFloat(row[3].replace(',', '.')),
					Float.parseFloat(row[7].replace(',', '.')), Float.parseFloat(row[1].replace(',', '.')));
			pointsAxes4[i] = new Coord3d(Float.parseFloat(row[2].replace(',', '.')),
					Float.parseFloat(row[7].replace(',', '.')), Float.parseFloat(row[1].replace(',', '.')));
						
			// creating color array
			colors[i] = new Color(Integer.parseInt(row[4]), Integer.parseInt(row[5]), Integer.parseInt(row[6]));
		}

		Coord3d[] points = new Coord3d[voronoiReferenceSize];
		switch (referenceAxes) {
		case 0:
			points= pointsAxes1;
			break;
		case 1:
			points= pointsAxes2;
			break;
		case 2:
			points= pointsAxes3;
			break;
		case 3:
			points= pointsAxes4;
			break;
		}
		
		scatterReference = new Scatter(points, colors, (float) slSizeOfPoints.getValue());
		
		initChart();
		
	
		
		BoundingBox3d boundBox = new BoundingBox3d(((float) rangeSliderX.getValue())/100,((float) rangeSliderX.getUpperValue())/100,((float) rangeSliderY.getValue())/100,((float) rangeSliderY.getUpperValue())/100,((float) rangeSliderZ.getValue())/100,((float) rangeSliderZ.getUpperValue())/100);
		chart.getView().setBoundManual(boundBox);
		//chart.getAxeLayout().setZTickLabelDisplayed(false);// ONLY FOR PAPER FIGURE
		
				
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
		Coord3d[] pointsAxes1 = new Coord3d[size_array];
		Coord3d[] pointsAxes2 = new Coord3d[size_array];
		Coord3d[] pointsAxes3 = new Coord3d[size_array];
		Coord3d[] pointsAxes4 = new Coord3d[size_array];

		Color[] colors = new Color[size_array];

		// Show only the points that we want to visualized, whose are ticked in
		// the visualizing table
		int numRow = 0;
		for (int i = 0; i<tableInfo.getRowCount();i++){
		//for (int i = 0; i < tableInfo.getRowCount(); i++) {
			if (tableInfo.getListOfSelected().get(i).booleanValue()) {
				// creating coord array
				pointsAxes1[numRow] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV(),
						tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagonsGraphlets());
				pointsAxes2[numRow] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDV5());
				pointsAxes3[numRow] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDV5(),
						tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagonsGraphlets());
				pointsAxes4[numRow] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDV5(),
						tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagonsGraphlets());
				// creating color array
				colors[numRow] = new Color(tableInfo.getAllGraphletImages().get(i).getColor().getRed(),
						tableInfo.getAllGraphletImages().get(i).getColor().getGreen(),
						tableInfo.getAllGraphletImages().get(i).getColor().getBlue());
				numRow++;
			}
		}
		
		switch (cbAxesToRepresent.getSelectedIndex()) {
		case 0:
			points = pointsAxes1;
			break;
		case 1:
			points= pointsAxes2;
			break;
		case 2:
			points= pointsAxes3;
			break;
		case 3:
			points= pointsAxes4;
			break;
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
		
		xyzLabel = new JLabel("Axes of figure:");
		xyzLabel.setLabelFor(xyzLabel);
		
		axisXinterval = new JLabel("limits of X axis:");
		axisXinterval.setLabelFor(axisXinterval);
		axisYinterval = new JLabel("limits of Y axis:");
		axisYinterval.setLabelFor(axisYinterval);
		axisZinterval = new JLabel("limits of Z axis:");
		axisZinterval.setLabelFor(axisZinterval);
		
		referenceMotifs = new JLabel("Motifs of CVTn reference:");
		referenceMotifs.setLabelFor(referenceMotifs);

		btnExport = new JButton("Export view");
		btnExport.addActionListener(this);

		cbGraphletsReference = new JComboBox<String>();
		cbGraphletsReference.setModel(new DefaultComboBoxModel<String>(GraphletImage.KIND_OF_GRAPHLETS));
		cbGraphletsReference.setSelectedIndex(1);
		cbAxesToRepresent = new JComboBox<String>();
		cbAxesToRepresent.setModel(new DefaultComboBoxModel<String>());
		cbAxesToRepresent.addItem("GDDH-GDDRV-% Hexagons");
		cbAxesToRepresent.addItem("GDDH-GDDRV-GDDV5");
		cbAxesToRepresent.addItem("GDDH-GDDV5-% Hexagons");
		cbAxesToRepresent.addItem("GDDRV-GDDV5-% Hexagons");
		cbAxesToRepresent.setSelectedIndex(0);
		
		// Init rangeSliders
		rangeSliderX = new RangeSlider(0,100);
		rangeSliderX.setValue(0);
		rangeSliderX.setUpperValue(100);
		rangeSliderY = new RangeSlider(0,100);
		rangeSliderY.setValue(0);
		rangeSliderY.setUpperValue(100);
		
		rangeSliderZ = new RangeSlider(0,10000);
		rangeSliderZ.setValue(0);
		rangeSliderZ.setUpperValue(10000);
			
		ChangeListener changingZoom =new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
			// We change the box bounds
			
			float factorNoOverlapX;
			float factorNoOverlapY;
			float factorNoOverlapZ;
			if (((float) rangeSliderX.getValue()/100) == (float) (rangeSliderX.getUpperValue())/100){
				factorNoOverlapX = (float) 0.01;
			}else{factorNoOverlapX = 0;}
			if (((float) rangeSliderY.getValue()/100) == (float) (rangeSliderY.getUpperValue())/100){
				factorNoOverlapY = (float) 0.01;
			}else{factorNoOverlapY = 0;}
			if (((float) rangeSliderZ.getValue()/100) == (float) (rangeSliderZ.getUpperValue())/100){
				factorNoOverlapZ = (float) 0.01;
			}else{factorNoOverlapZ = 0;}

			BoundingBox3d boundBox = new BoundingBox3d(((float) rangeSliderX.getValue())/100,((float) (rangeSliderX.getUpperValue())/100)+factorNoOverlapX ,(float) rangeSliderY.getValue()/100,((float) (rangeSliderY.getUpperValue())/100)+factorNoOverlapY,(float) rangeSliderZ.getValue()/100,((float) (rangeSliderZ.getUpperValue())/100)+factorNoOverlapZ);
			chart.getView().setBoundManual(boundBox);
			repaintAll();
			
		}};
		
		rangeSliderX.addChangeListener(changingZoom);
		rangeSliderY.addChangeListener(changingZoom);
		rangeSliderZ.addChangeListener(changingZoom);
		
		ItemListener changeModeOrAxes = new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				Scatter oldScatterReference = scatterReference;
				Scatter oldScatterData = scatterData;
				
				createScatterData();
				chart.getScene().add(scatterData);
				createScatterPlot(cbGraphletsReference.getSelectedIndex(),cbAxesToRepresent.getSelectedIndex());
				scatterReference.setDisplayed(chbShowVoronoiReference.getState());	
				chart.getScene().add(scatterReference);
				chart.getScene().remove(oldScatterReference,false);
				chart.getScene().remove(oldScatterData,false);
				
				rangeSliderX.setValue(rangeSliderX.getMinimum());
				rangeSliderX.setUpperValue(rangeSliderX.getMaximum());
				rangeSliderY.setValue(rangeSliderY.getMinimum());
				rangeSliderY.setUpperValue(rangeSliderY.getMaximum());
				
				if (cbAxesToRepresent.getSelectedIndex()==1){
					rangeSliderZ.setMaximum(100);
				}else{rangeSliderZ.setMaximum(10000);
					}
				rangeSliderZ.setValue(rangeSliderZ.getMinimum());
				rangeSliderZ.setUpperValue(rangeSliderZ.getMaximum());
	
				BoundingBox3d boundBox = new BoundingBox3d(((float) rangeSliderX.getValue())/100,((float) rangeSliderX.getUpperValue())/100,((float) rangeSliderY.getValue())/100,((float) rangeSliderY.getUpperValue())/100,((float) rangeSliderZ.getValue())/100,((float) rangeSliderZ.getUpperValue())/100);
				chart.getView().setBoundManual(boundBox);
				
				chbShowVoronoiReference.setState(chbShowVoronoiReference.getState());
				cbGraphletsReference.setEnabled(chbShowVoronoiReference.getState());
			}
		};
		
		cbGraphletsReference.addItemListener(changeModeOrAxes);
		cbAxesToRepresent.addItemListener(changeModeOrAxes);
		

		chbShowVoronoiReference = new Checkbox("Show reference", true);
		chbShowVoronoiReference.setState(true);
		chbShowVoronoiReference.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				scatterReference.setDisplayed(chbShowVoronoiReference.getState());
				cbGraphletsReference.setEnabled(chbShowVoronoiReference.getState());
				//cbAxesToRepresent.setEnabled(chbShowVoronoiReference.getState());
			}
		});
		
		
		
		
	}

	/**
	 * Repaint all the window
	 */
	private void repaintAll() {
		
		getContentPane().repaint();
		
		
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
		
		buttonsPanel.add(xyzLabel, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(cbAxesToRepresent, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		
		
		buttonsPanel.add(axisXinterval, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(rangeSliderX, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(axisYinterval, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(rangeSliderY, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(axisZinterval, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(rangeSliderZ, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(chbShowVoronoiReference, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(lbSizeOfPoints, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(slSizeOfPoints, genericPanelConstrainst);
		genericPanelConstrainst.gridy++;
		buttonsPanel.add(referenceMotifs, genericPanelConstrainst);
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
		
		switch (cbAxesToRepresent.getSelectedIndex()) {
		case 0:
			l.setXAxeLabel("GDDH");
			l.setYAxeLabel("GDDRV");
			l.setZAxeLabel("Percentage of hexagons");
			break;
		case 1:
			l.setXAxeLabel("GDDH");
			l.setYAxeLabel("GDDRV");
			l.setZAxeLabel("GDDV5");
			break;
		case 2:
			l.setXAxeLabel("GDDH");
			l.setYAxeLabel("GDDV5");
			l.setZAxeLabel("Percentage of hexagons");
			break;
		case 3:
			l.setXAxeLabel("GDDRV");
			l.setYAxeLabel("GDDV5");
			l.setZAxeLabel("Percentage of hexagons");
			break;
		}
		
		l.setXTickRenderer(new FixedDecimalTickRenderer(2));
		l.setYTickRenderer(new FixedDecimalTickRenderer(2));
		l.setZTickRenderer(new FixedDecimalTickRenderer(2));

		

		
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