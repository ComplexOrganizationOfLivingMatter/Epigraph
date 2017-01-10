package epigraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fiji.util.gui.OverlayedImageCanvas;
import ij.ImagePlus;
import net.coobird.thumbnailator.Thumbnails;

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
	
	private static final int CANVAS_SIZE = 512;

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
		setBounds(100, 100, 935, 798);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		
		ImagePlus imgToShow = new ImagePlus("", raw_img.getChannelProcessor());
		BufferedImage thumbnail = null;
		try {
			thumbnail = Thumbnails.of(raw_img.getBufferedImage()).height(CANVAS_SIZE).width(CANVAS_SIZE).asBufferedImage();
			imgToShow.setImage(thumbnail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OverlayedImageCanvas canvas = new OverlayedImageCanvas(imgToShow);
		canvas.setLocation(199, 42);
		canvas.setShowCursorStatus(false);
		canvas.setShowAllROIs(false);
		canvas.setPaintPending(false);
		canvas.setCustomRoi(false);
		canvas.setSize(CANVAS_SIZE, CANVAS_SIZE);
		
		JButton btnCalculateGraphlets = new JButton("Calculate graphlets!");
		btnCalculateGraphlets.setBounds(199, 596, 481, 49);
		btnCalculateGraphlets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newGraphletImages.add(new GraphletImage(raw_img));
			}
		});
		
		JButton btnCreateRoi = new JButton("Create RoI");
		btnCreateRoi.setBounds(752, 191, 124, 25);
		btnCreateRoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createROI(imgToShow);
			}
		});
		contentPane.setLayout(null);
		contentPane.add(canvas);
		contentPane.add(btnCreateRoi);
		contentPane.add(btnCalculateGraphlets);
		
	}

	private void createROI(ImagePlus imgToShow) {
		// TODO Auto-generated method stub
		
//		imgToShow.getChannelProcessor().drawRect(x, y, width, height);
//		
//		WaitForUserDialog wtd = new WaitForUserDialog("USER ROI SELECTION","select a Roi");
//		wtd.show();
//		Roi newRoi = new Roi();
//		imgToShow.getChannelProcessor().drawRoi(newRoi);
	}

	public void returnGraphletImages() {
		// TODO Auto-generated method stub
		MainWindow daddy = (MainWindow) this.getParent();
		daddy.addNewImagesProcessed(newGraphletImages);
	}
}
