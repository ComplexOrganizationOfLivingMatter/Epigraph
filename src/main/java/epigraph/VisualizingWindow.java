package epigraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import util.opencsv.CSVReader;

/**
 * 
 * @author Pedro Gomez-Galvez
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

	/**
	 * 
	 */
	public VisualizingWindow(JTableModel tableInfo) {
		super();
		setModal(true);
		chart = new Chart("swing");

		int size_array = tableInfo.getRowCount();
		List<String[]> voronoiReference = new ArrayList<String[]>();
		try {
			CSVReader reader = new CSVReader(
					new FileReader(Epigraph.class.getResource("/epigraph/voronoiNoiseReference/Total.txt").getFile()), '\t');
			voronoiReference = reader.readAll();

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
			points[i] = new Coord3d(Float.parseFloat(row[3].replace(',', '.')), Float.parseFloat(row[2].replace(',', '.')), Float.parseFloat(row[1].replace(',', '.')));
			// creating color array
			colors[i] = new Color(0, 0, 0);
		}

		for (int i = 0; i < size_array; i++) {
			if (tableInfo.getListOfVisualizing().get(i)) {
				// creating coord array
				points[i + voronoiReference.size()] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagons(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(),
						tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV());
				// creating color array
				colors[i + voronoiReference.size()] = new Color(tableInfo.getAllGraphletImages().get(i).getColor().getRed(),
						tableInfo.getAllGraphletImages().get(i).getColor().getGreen(),
						tableInfo.getAllGraphletImages().get(i).getColor().getBlue());
			}
		}

		// Xf represent the size of dots
		scatter = new Scatter(points, colors, 6f);

		// Nicest show dots shape, Advanced show squares shape
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

		scatterpanel = new JPanel(new BorderLayout());
		scatterpanel.add((Component) chart.getCanvas(), BorderLayout.CENTER);
		setContentPane(scatterpanel);

		pack();
		setBounds(0, 0, 700, 700);

		// chart.getView().getCamera().setScreenGridDisplayed(true);
	}
}