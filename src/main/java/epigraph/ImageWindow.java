package epigraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import fiji.util.gui.OverlayedImageCanvas;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 
 * @author Pablo Vicente-Munuera
 *
 */
public class ImageWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private ArrayList<GraphletImage> newGraphletImages;

	/**
	 * Create the frame.
	 */
	public ImageWindow(ImagePlus raw_img) {
		
		newGraphletImages = new ArrayList<GraphletImage>();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				returnGraphletImages();
			}
		});
		setBounds(100, 100, 562, 531);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("21px"),
				ColumnSpec.decode("147px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("184px"),
				RowSpec.decode("33px"),
				RowSpec.decode("25px"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		
		ImagePlus imgToShow = new ImagePlus("", raw_img.getChannelProcessor());
		BufferedImage thumbnail;
		try {
			thumbnail = Thumbnails.of(raw_img.getBufferedImage()).height(400).asBufferedImage();
			imgToShow.setImage(thumbnail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OverlayedImageCanvas canvas = new OverlayedImageCanvas(imgToShow);
		canvas.setShowCursorStatus(false);
		canvas.setShowAllROIs(false);
		canvas.setPaintPending(false);
		canvas.setScaleToFit(true);
		canvas.setCustomRoi(false);
		Roi roi;
		roi = new Roi(0, 0, imgToShow);
		raw_img.setRoi(roi);
		contentPane.add(canvas, "4, 2, 7, 13, center, top");
		
		JButton btnNewButton = new JButton("Calculate graphlets!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newGraphletImages.add(new GraphletImage(raw_img));
			}
		});
		contentPane.add(btnNewButton, "6, 18, center, default");
		
	}

	public void returnGraphletImages() {
		// TODO Auto-generated method stub
		JPanelModel daddy = (JPanelModel) this.getParent();
		daddy.addNewImagesProcessed(newGraphletImages);
	}
}
