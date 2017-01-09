package epigraph;

import java.util.Random;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;



public class ScatterDemo extends AbstractAnalysis{
	public static void main(String[] args) throws Exception {
		AnalysisLauncher.open(new ScatterDemo());
	}
	
	
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
        l.setXAxeLabel("Hexagons percentage");
        l.setYAxeLabel("GDDH");
        l.setZAxeLabel("GDDRV");
                
        //Presition displaying axes
        l.setXTickRenderer(new FixedDecimalTickRenderer(2));
        l.setYTickRenderer(new FixedDecimalTickRenderer(2));
        l.setZTickRenderer(new FixedDecimalTickRenderer(2));

    }
}