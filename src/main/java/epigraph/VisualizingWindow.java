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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
 * 
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 *
 */
public class VisualizingWindow extends JDialog implements ActionListener {

	/**
	 * 
	 */
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

	/**
	 * 
	 * @param parent
	 * @param tableInfo
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

		setBounds(10, 10, 1200, 800);

		// chart.getView().getCamera().setScreenGridDisplayed(true);
	}

	/**
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
	 * 
	 */
	private void createScatterData() {
		int size_array = 0;
		for (int i = 0; i < tableInfo.getRowCount(); i++) {
			if (tableInfo.getListOfVisualizing().get(i).booleanValue())
				size_array++;
		}

		Coord3d[] points = new Coord3d[size_array];
		Color[] colors = new Color[size_array];

		int numRow = 0;
		for (int i = 0; i < tableInfo.getRowCount(); i++) {
			if (tableInfo.getListOfVisualizing().get(i).booleanValue()) {
				// creating coord array
				points[numRow] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagons());
				// creating color array
				colors[numRow] = new Color(tableInfo.getAllGraphletImages().get(i).getColor().getRed(),
						tableInfo.getAllGraphletImages().get(i).getColor().getGreen(),
						tableInfo.getAllGraphletImages().get(i).getColor().getBlue());
				numRow++;
			}
		}

		scatterData = new Scatter(points, colors, (float) slSizeOfPoints.getValue());
	}

	/**
	 * 
	 */
	private void initGUIItems() {
		Quality q2 = new Quality(true, true, true, true, true, true, true);
		q2.setPreserveViewportSize(false);
		chart = AWTChartComponentFactory.chart(q2, org.jzy3d.chart.factories.IChartComponentFactory.Toolkit.awt);
		chart.addMouseCameraController();

		slSizeOfPoints = new JSlider(3, 30, 10);
		slSizeOfPoints.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				scatterData.setWidth((float) slSizeOfPoints.getValue());
				scatterReference.setWidth((float) slSizeOfPoints.getValue());
			}
		});

		btnExport = new JButton("Export view");
		btnExport.addActionListener(this);

		cbGraphletsReference = new JComboBox<String>();
		cbGraphletsReference.setModel(new DefaultComboBoxModel<String>(new String[] { "Total (25 graphlets)",
				"Total Partial (16 graphlets)", "Basic (9 graphlets)", "Basic Partial (7 graphlets) " }));
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
	 * 
	 */
	private void repaintAll() {

		getContentPane().repaint();

		this.chart.setScale(new Scale(0, 100));
	}

	/**
	 * 
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
	 * 
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

	/**
	 * 
	 * @param resourcePath
	 * @return
	 */
	public File getResourceAsFile(String resourcePath) {
		try {
			InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
			if (in == null) {
				return null;
			}

			File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
			tempFile.deleteOnExit();

			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				// copy stream
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}
			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param e
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