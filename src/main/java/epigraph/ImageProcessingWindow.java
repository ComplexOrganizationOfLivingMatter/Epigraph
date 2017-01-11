package epigraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fiji.util.gui.OverlayedImageCanvas;
import ij.ImagePlus;
import net.coobird.thumbnailator.Thumbnails;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Button;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

/**
 * 
 * @author Pablo Vicente-Munuera
 *
 */
public class ImageProcessingWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int CANVAS_SIZE = 512;

	private JPanel contentPane;
	
	private ArrayList<GraphletImage> newGraphletImages;
	private JTextField tfImageName;

	/**
	 * Create the frame.
	 */
	public ImageProcessingWindow(ImagePlus raw_img, JTableModel tableInfo) {
		
		newGraphletImages = new ArrayList<GraphletImage>();
		setBounds(100, 100, 972, 798);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		
		ImagePlus imgToShow = new ImagePlus("", raw_img.getChannelProcessor());
		BufferedImage thumbnail = null;
		try {
			thumbnail = Thumbnails.of(imgToShow.getBufferedImage()).height(CANVAS_SIZE).width(CANVAS_SIZE).asBufferedImage();
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
				tableInfo.addImage(new GraphletImage(raw_img));
			}
		});
		
		JButton btnCreateRoi = new JButton("Create RoI");
		btnCreateRoi.setBounds(755, 392, 124, 25);
		btnCreateRoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createROI(imgToShow);
			}
		});
		contentPane.setLayout(null);
		contentPane.add(canvas);
		contentPane.add(btnCreateRoi);
		contentPane.add(btnCalculateGraphlets);
		
		tfImageName = new JTextField();
		tfImageName.setBounds(755, 458, 146, 26);
		contentPane.add(tfImageName);
		tfImageName.setColumns(10);
		
		JLabel lblImageName = new JLabel("Image Name");
		lblImageName.setBounds(754, 436, 113, 20);
		contentPane.add(lblImageName);
		
		JPanel colorPicked = new JPanel();
		colorPicked.setBackground(Color.BLACK);
		colorPicked.setBounds(755, 555, 113, 25);
		contentPane.add(colorPicked);
		
		JButton btnPickAColor = new JButton("Pick a color");
		btnPickAColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(null, "Choose a Color", colorPicked.getBackground());
			      if (c != null)
			    	  colorPicked.setBackground(c);
			}
		});
		btnPickAColor.setBounds(752, 510, 115, 29);
		contentPane.add(btnPickAColor);
		
		JButton btnTestNeighbours = new JButton("Test Neighbours");
		btnTestNeighbours.setBounds(755, 180, 162, 29);
		contentPane.add(btnTestNeighbours);
		
		JSpinner inputRadiusNeigh = new JSpinner();
		inputRadiusNeigh.setModel(new SpinnerNumberModel(3, 1, 25, 1));
		inputRadiusNeigh.setBounds(826, 42, 72, 26);
		contentPane.add(inputRadiusNeigh);
		
		JLabel lblRadius = new JLabel("Radius");
		lblRadius.setBounds(757, 45, 69, 20);
		contentPane.add(lblRadius);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Circle", "Square"}));
		comboBox.setBounds(755, 102, 156, 26);
		contentPane.add(comboBox);
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
}
