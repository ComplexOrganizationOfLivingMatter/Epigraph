package epigraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
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

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.io.input.ReaderInputStream;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.SwingChart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import ij.plugin.Slicer;
import util.opencsv.CSVReader;
import javax.swing.JSlider;

/**
 * 
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 *
 */
public class VisualizingWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Scatter scatter;

	private JPanel scatterpanel;
	/* load X Y Z coordenates */

	private Chart chart;

	private JSlider slSizeOfPoints;

	/**
	 * 
	 */
	public VisualizingWindow(JTableModel tableInfo) {
		super();

		scatterpanel = new JPanel(new GridLayout(1, 0));
		add(scatterpanel);

		slSizeOfPoints = new JSlider(3, 20, 6);
		slSizeOfPoints.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				scatter.setWidth((float) slSizeOfPoints.getValue());
			}
		});
		getContentPane().add(slSizeOfPoints, BorderLayout.EAST);
		
		chart = new SwingChart();

		int size_array = tableInfo.getRowCount();
		List<String[]> voronoiReference = new ArrayList<String[]>();
		try {
			Reader reader = new InputStreamReader(
					Epigraph.class.getResourceAsStream("/epigraph/voronoiNoiseReference/Total.txt"));
			CSVReader csvReader = new CSVReader(reader, '\t');
			voronoiReference = csvReader.readAll();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Coord3d[] points = new Coord3d[size_array + voronoiReference.size()];
		Color[] colors = new Color[size_array + voronoiReference.size()];
		for (int i = 0; i < voronoiReference.size(); i++) {
			// creating coord array
			String[] row = voronoiReference.get(i);
			points[i] = new Coord3d(Float.parseFloat(row[1].replace(',', '.')),
					Float.parseFloat(row[2].replace(',', '.')), Float.parseFloat(row[3].replace(',', '.')));
			// creating color array
			colors[i] = new Color(0, 0, 0);
		}

		for (int i = 0; i < size_array; i++) {
			if (tableInfo.getListOfVisualizing().get(i)) {
				// creating coord array
				points[i + voronoiReference.size()] = new Coord3d(
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagons());
				// creating color array
				colors[i + voronoiReference.size()] = new Color(
						tableInfo.getAllGraphletImages().get(i).getColor().getRed(),
						tableInfo.getAllGraphletImages().get(i).getColor().getGreen(),
						tableInfo.getAllGraphletImages().get(i).getColor().getBlue());
			}
		}

		scatter = new Scatter(points, colors, (float) slSizeOfPoints.getValue());

		chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt");
		chart.getScene().add(scatter);
		chart.addMouseCameraController();

		IAxeLayout l = chart.getAxeLayout();

		// Labelling axes
		l.setXAxeLabel("GDDH");
		l.setYAxeLabel("Hexagons percentage");
		l.setZAxeLabel("GDDRV");

		// Presition displaying axes
		l.setXTickRenderer(new FixedDecimalTickRenderer(2));
		l.setYTickRenderer(new FixedDecimalTickRenderer(2));
		l.setZTickRenderer(new FixedDecimalTickRenderer(2));

		scatterpanel.add((Component) chart.getCanvas(), BorderLayout.CENTER);

		pack();
		setBounds(0, 0, 1000, 1000);

		// chart.getView().getCamera().setScreenGridDisplayed(true);
	}
	
	public File getResourceAsFile(String resourcePath) {
	    try {
	        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
	        if (in == null) {
	            return null;
	        }

	        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
	        tempFile.deleteOnExit();

	        try (FileOutputStream out = new FileOutputStream(tempFile)) {
	            //copy stream
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
}