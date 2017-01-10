package epigraph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

import com.jgoodies.forms.layout.CellConstraints;


/**
 * 
 * @author Pedro Gomez-Galvez
 *
 */
public class VisualizingWindow extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Scatter scatter;

	private JPanel scatterpanel;
	/*load X Y Z coordenates*/

	private Chart chart;
	
	/**
	 * 
	 */
	public VisualizingWindow() {
		super();
		 chart = new Chart("swing");
		// TODO Auto-generated constructor stub
		ExcelClass ec = new ExcelClass();
		ec.importData("D:/Pedro/Graphlet/pruebas exportar u3d/TotalParcial_3Ddimensions_test.xls");
		
		
        int size_array = ec.getGddh().size();
        
               
        Coord3d[] points = new Coord3d[size_array];
        Color[]   colors = new Color[size_array];
        
        /*create an color array*/
        
        Random r = new Random();
        r.setSeed(0);
        
        for(int i=0; i<size_array; i++){
            
        	//creating coord array
            points[i] = new Coord3d(ec.getHexagonsPercentage().get(i), ec.getGddh().get(i), ec.getGddrv().get(i));
            //creating color array
            colors[i] = new Color(ec.getR().get(i), ec.getG().get(i), ec.getB().get(i));
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
        //panel.add((Component) chart.getAxeLayout(), BorderLayout.CENTER);
        
        setContentPane(panel);

        pack();
        setBounds(0, 0, 400, 400);
        
        //chart.getView().getCamera().setScreenGridDisplayed(true);
	}
}