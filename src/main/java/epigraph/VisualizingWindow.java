package epigraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.input.ReaderInputStream;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.SwingChart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.providers.StaticTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import fiji.util.gui.OverlayedImageCanvas;
import ij.gui.ImageCanvas;
import ij.plugin.Slicer;
import util.opencsv.CSVReader;
import javax.swing.JSlider;

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

	Scatter scatter;

	private JPanel scatterpanel;
	/* load X Y Z coordenates */

	private Chart chart;
	
	private JSlider slSizeOfPoints;

	private JButton btnExport;

	private Coord3d[] points;

	private Color[] colors;

	/**
	 * 
	 * @param tableInfo
	 */
	public VisualizingWindow(JTableModel tableInfo) {
		super();
		
		initGUIItems();

		createScatterPlot(tableInfo);
		
		insertPointsToChart(chart, points, colors, null, -1);
		
		pack();

		setBounds(10, 10, 800, 800);

		// chart.getView().getCamera().setScreenGridDisplayed(true);
	}

	/**
	 * @param tableInfo
	 */
	private void createScatterPlot(JTableModel tableInfo) {
		List<String[]> voronoiReference = new ArrayList<String[]>();
		try {
			Reader reader = new InputStreamReader(
					Epigraph.class.getResourceAsStream("/epigraph/voronoiNoiseReference/TotalPartial.txt"));
			CSVReader csvReader = new CSVReader(reader, '\t');
			voronoiReference = csvReader.readAll();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Variable for the number of visualization items.
		int size_array = 0;
		for (int i = 0; i < tableInfo.getRowCount(); i++) {
			if (tableInfo.getListOfVisualizing().get(i).booleanValue())
				size_array++;
		}

		String[] row;
		points = new Coord3d[size_array + voronoiReference.size()];
		colors = new Color[size_array + voronoiReference.size()];

		for (int i = 0; i < voronoiReference.size(); i++) {
			// creating coord array
			row = voronoiReference.get(i);
			points[i] = new Coord3d(Float.parseFloat(row[2].replace(',', '.')),
					Float.parseFloat(row[3].replace(',', '.')), Float.parseFloat(row[1].replace(',', '.')));
			// creating color array
			colors[i] = new Color(Integer.parseInt(row[4]), Integer.parseInt(row[5]), Integer.parseInt(row[6]));
		}

		int numRow = 0;
		for (int i = 0; i < tableInfo.getRowCount(); i++) {
			if (tableInfo.getListOfVisualizing().get(i).booleanValue()) {
				// creating coord array
				points[numRow + voronoiReference.size()] = new Coord3d(
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagons());
				// creating color array
				colors[numRow + voronoiReference.size()] = new Color(
						tableInfo.getAllGraphletImages().get(i).getColor().getRed(),
						tableInfo.getAllGraphletImages().get(i).getColor().getGreen(),
						tableInfo.getAllGraphletImages().get(i).getColor().getBlue());
				numRow++;
			}
		}
	}

	/**
	 * 
	 */
	private void initGUIItems() {
		GridBagLayout genericPanelLayout = new GridBagLayout();
		GridBagConstraints genericPanelConstrainst = new GridBagConstraints();
		genericPanelConstrainst.gridwidth = 1;
		genericPanelConstrainst.gridheight = 1;
		genericPanelConstrainst.gridx = 0;
		genericPanelConstrainst.gridy = 0;
		genericPanelConstrainst.weighty = 0;
		genericPanelConstrainst.weightx = 0;
		scatterpanel = new JPanel(genericPanelLayout);
		add(scatterpanel);
		
		slSizeOfPoints = new JSlider(3, 20, 10);
		slSizeOfPoints.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				scatter.setWidth((float) slSizeOfPoints.getValue());
			}
		});
		getContentPane().add(slSizeOfPoints, BorderLayout.EAST);

		btnExport = new JButton("Export view");
		btnExport.addActionListener(this);
		getContentPane().add(btnExport, BorderLayout.EAST);
		

		Quality q2 = new Quality(true, true, true, true, true, true, true);
		q2.setPreserveViewportSize(false);
		chart = AWTChartComponentFactory.chart(q2, org.jzy3d.chart.factories.IChartComponentFactory.Toolkit.awt);
		chart.addMouseCameraController();
		getContentPane().add((Component) chart.getCanvas(), BorderLayout.CENTER);
	}

	/**
	 * 
	 * @param chart2
	 * @param points
	 * @param colors
	 * @param newScatter
	 * @param pointSize
	 */
	private void insertPointsToChart(Chart chart2, Coord3d[] points, Color[] colors, Scatter newScatter, float pointSize) {

		IAxeLayout l = chart2.getAxeLayout();

		// Labelling axes
		l.setXAxeLabel("GDDRV");
		l.setYAxeLabel("GDDH");
		l.setZAxeLabel("Percentage of hexagons");

		l.setXTickRenderer(new FixedDecimalTickRenderer(2));
		l.setYTickRenderer(new FixedDecimalTickRenderer(2));
		l.setZTickRenderer(new FixedDecimalTickRenderer(2));

		if (newScatter != null){
			newScatter = new Scatter(points, colors, pointSize);
		} else {
			scatter = new Scatter(points, colors, (float) slSizeOfPoints.getValue());
		}

		chart2.getScene().add(scatter);

		chart2.setScale(new Scale(0, 100));
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
		if (e.getSource() == btnExport){
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
				
				((CanvasAWT)chart.getCanvas()).setPixelScale(new float[] { 0.1f, 0.1f });
//				Quality q = new Quality(true, false, true, true, true, true, false);
//				q.setPreserveViewportSize(false);
//				Chart exportChart = AWTChartComponentFactory.chart(q,
//						"offscreen,1024,1024");
//				//exportChart.getCanvas().setPixelScale(new float[] { 0.1f, 0.1f });
//				Scatter newScatter = new Scatter();
//				initChart(exportChart, points, colors, newScatter, 300);
				
				File f = new File(filename);
				try {
					//TextureData p = exportChart.screenshot(f);
					chart.screenshot(f);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}