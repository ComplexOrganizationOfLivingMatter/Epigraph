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
import java.awt.Component;
import java.awt.Button;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;

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

	private OverlayedImageCanvas canvas;

	private JButton btnCalculateGraphlets;

	private JButton btnCreateRoi;

	private JComboBox<String> comboBox;

	private JLabel lblRadius;

	private JSpinner inputRadiusNeigh;

	private JButton btnTestNeighbours;

	private JButton btnPickAColor;

	private JPanel colorPicked;

	private JLabel lblImageName;
	private JButton btnAddToTable;

	private GraphletImage newGraphletImage;

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
			thumbnail = Thumbnails.of(imgToShow.getBufferedImage()).height(CANVAS_SIZE).width(CANVAS_SIZE)
					.asBufferedImage();
			imgToShow.setImage(thumbnail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		canvas = new OverlayedImageCanvas(imgToShow);
		canvas.setLocation(199, 42);
		canvas.setShowCursorStatus(false);
		canvas.setShowAllROIs(false);
		canvas.setPaintPending(false);
		canvas.setCustomRoi(false);
		canvas.setSize(CANVAS_SIZE, CANVAS_SIZE);

		btnCalculateGraphlets = new JButton("Calculate graphlets!");
		btnCalculateGraphlets.setBounds(199, 596, 329, 49);
		btnCalculateGraphlets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (newGraphletImage == null) {
					newGraphletImage = new GraphletImage(raw_img);
				}
				newGraphletImage.setLabelName(tfImageName.getText());
				newGraphletImage.setColor(colorPicked.getBackground());
			}
		});

		btnCreateRoi = new JButton("Create RoI");
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
		tfImageName.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent event) {
			}

			public void inputMethodTextChanged(InputMethodEvent event) {
				if (newGraphletImage != null) {
					newGraphletImage.setLabelName(tfImageName.getText());
				}
			}
		});
		tfImageName.setBounds(755, 458, 146, 26);
		contentPane.add(tfImageName);
		tfImageName.setColumns(10);

		lblImageName = new JLabel("Image Name");
		lblImageName.setBounds(754, 436, 113, 20);
		contentPane.add(lblImageName);

		colorPicked = new JPanel();
		colorPicked.setBackground(Color.BLACK);
		colorPicked.setBounds(755, 555, 113, 25);
		contentPane.add(colorPicked);

		btnPickAColor = new JButton("Pick a color");
		btnPickAColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Color c = JColorChooser.showDialog(btnPickAColor.getParent(), "Choose a Color", colorPicked.getBackground());
				if (c != null){
					colorPicked.setBackground(c);
					if (newGraphletImage != null){
						newGraphletImage.setColor(c);
					}
				}
				
			}
		});
		btnPickAColor.setBounds(752, 510, 115, 29);
		contentPane.add(btnPickAColor);

		btnTestNeighbours = new JButton("Test Neighbours");
		btnTestNeighbours.setBounds(755, 180, 162, 29);
		contentPane.add(btnTestNeighbours);

		inputRadiusNeigh = new JSpinner();
		inputRadiusNeigh.setModel(new SpinnerNumberModel(3, 1, 25, 1));
		inputRadiusNeigh.setBounds(826, 42, 72, 26);
		contentPane.add(inputRadiusNeigh);

		lblRadius = new JLabel("Radius");
		lblRadius.setBounds(757, 45, 69, 20);
		contentPane.add(lblRadius);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Circle", "Square" }));
		comboBox.setBounds(755, 102, 156, 26);
		contentPane.add(comboBox);

		btnAddToTable = new JButton("Add to table");
		btnAddToTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tfImageName.getText().isEmpty()){
					JOptionPane.showMessageDialog(btnAddToTable.getParent(), "You should insert a name for the image");
				} else {
					int result = JOptionPane.showConfirmDialog(btnAddToTable.getParent(), "Everything is ok?",
					        "Confirm", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION)
						tableInfo.addImage(newGraphletImage);
				}
			}
		});
		btnAddToTable.setBounds(558, 606, 153, 29);
		contentPane.add(btnAddToTable);
	}

	private void createROI(ImagePlus imgToShow) {
		// TODO Auto-generated method stub

		// imgToShow.getChannelProcessor().drawRect(x, y, width, height);
		//
		// WaitForUserDialog wtd = new WaitForUserDialog("USER ROI
		// SELECTION","select a Roi");
		// wtd.show();
		// Roi newRoi = new Roi();
		// imgToShow.getChannelProcessor().drawRoi(newRoi);
	}
}
