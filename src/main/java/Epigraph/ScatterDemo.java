package epigraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import com.jgoodies.forms.layout.CellConstraints;



public class ScatterDemo extends AbstractAnalysis{
	public static void main(String[] args) throws Exception {
		AnalysisLauncher.open(new ScatterDemo());
	}
	
	private JPanel scatterpanel;
	/*load X Y Z coordenates*/
	
	@Override
    public void init(){
		
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
        Scatter scatter = new Scatter(points, colors, 6f);
        
        //Nicest show dots shape, Advanced show squares shape
        chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt");
        chart.getScene().add(scatter);
        
        
        IAxeLayout l = chart.getAxeLayout();
        
        //Labelling axes
        l.setXAxeLabel("GDDH");
        l.setYAxeLabel("Hexagons percentage");
        l.setZAxeLabel("GDDRV");
                
        //Presition displaying axes
        l.setXTickRenderer(new FixedDecimalTickRenderer(2));
        l.setYTickRenderer(new FixedDecimalTickRenderer(2));
        l.setZTickRenderer(new FixedDecimalTickRenderer(2));
        
        
        
        
        
    }
	
	public void createAndShowF3d() {
		
		scatterpanel = new JPanel();
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Scatter 3d");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        ScatterDemo newContentScatter = new ScatterDemo();
        newContentScatter.getChart();
        scatterpanel.setBorder(new MatteBorder(5, 5, 5, 5, java.awt.Color.BLACK));
        scatterpanel.setLayout(new BorderLayout());
        scatterpanel.add((Component)newContentScatter.getChart().getCanvas(), BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        //frame.setSize(500, 400);
        frame.setVisible(true);
      }
    
	
	
    
	
}