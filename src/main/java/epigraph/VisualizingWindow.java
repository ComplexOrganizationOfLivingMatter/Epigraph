package epigraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;

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
        
               
        Coord3d[] points = new Coord3d[size_array];
        Color[]   colors = new Color[size_array];
        
        for(int i=0; i<size_array; i++){
            if (tableInfo.getListOfVisualizing().get(i)){
	        	//creating coord array
	            points[i] = new Coord3d(tableInfo.getAllGraphletImages().get(i).getPercentageOfHexagons(), tableInfo.getAllGraphletImages().get(i).getDistanceGDDH(), tableInfo.getAllGraphletImages().get(i).getDistanceGDDRV());
	            //creating color array
	            colors[i] = new Color(tableInfo.getAllGraphletImages().get(i).getColor().getRed(), tableInfo.getAllGraphletImages().get(i).getColor().getGreen(), tableInfo.getAllGraphletImages().get(i).getColor().getBlue());
            }
        }
        
        //Xf represent the size of dots
        scatter = new Scatter(points, colors, 6f);
        
        //Nicest show dots shape, Advanced show squares shape
        chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt");
        chart.getScene().add(scatter);
        chart.addMouseCameraController();
        
        IAxeLayout l = chart.getAxeLayout();
        
        //Labelling axes
        l.setXAxeLabel("GDDH");
        l.setYAxeLabel("Hexagons percentage");
        l.setZAxeLabel("GDDRV");
                
        //Presition displaying axes
        l.setXTickRenderer(new FixedDecimalTickRenderer(2));
        l.setYTickRenderer(new FixedDecimalTickRenderer(2));
        l.setZTickRenderer(new FixedDecimalTickRenderer(2));
              

        JPanel panel = new JPanel(new BorderLayout());
        panel.add((Component) chart.getCanvas(), BorderLayout.CENTER);
        
        setContentPane(panel);

        pack();
        setBounds(0, 0, 400, 400);
        
        //chart.getView().getCamera().setScreenGridDisplayed(true);
	}
}