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
import ij.gui.Roi;
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
		BufferedImage thumbnail;
		try {
			thumbnail = Thumbnails.of(raw_img.getBufferedImage()).height(512).asBufferedImage();
			imgToShow.setImage(thumbnail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OverlayedImageCanvas canvas = new OverlayedImageCanvas(imgToShow);
		canvas.setLocation(419, 47);
		canvas.setShowCursorStatus(false);
		canvas.setShowAllROIs(false);
		canvas.setPaintPending(false);
		canvas.setCustomRoi(false);
		canvas.setSize(102, 587);
		
		JButton btnCalculateGraphlets = new JButton("Calculate graphlets!");
		btnCalculateGraphlets.setBounds(227, 741, 481, 25);
		btnCalculateGraphlets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newGraphletImages.add(new GraphletImage(raw_img));
			}
		});
		
		JButton btnCreateRoi = new JButton("Create RoI");
		btnCreateRoi.setBounds(820, 191, 95, 25);
		btnCreateRoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Roi roi;
				roi = new Roi(4, 2, imgToShow);
				raw_img.setRoi(roi);
			}
		});
		contentPane.setLayout(null);
		contentPane.add(canvas);
		contentPane.add(btnCreateRoi);
		contentPane.add(btnCalculateGraphlets);
		
	}

	public void returnGraphletImages() {
		// TODO Auto-generated method stub
		JPanelModel daddy = (JPanelModel) this.getParent();
		daddy.addNewImagesProcessed(newGraphletImages);
	}
}
