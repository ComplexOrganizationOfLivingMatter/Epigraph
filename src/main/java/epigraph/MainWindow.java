package epigraph;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;

import org.scijava.ui.DialogPrompt;

import ij.IJ;
import ij.ImagePlus;

/**
 * @author Pedro Gomez-Galvez
 * 
 *         TableDemo is just like SimpleTableDemo, except that it uses a custom
 *         TableModel.
 */
public class MainWindow extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean DEBUG = false;
	JTableModel tableInfo;

	public MainWindow() {
		super(new GridLayout(1, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel);

		// Create buttons
		JButton btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				VisualizingWindow visualizingWindow = new VisualizingWindow();
				visualizingWindow.setVisible(true);
			}
		});
		btnVisualize.setBounds(342, 255, 93, 29);

		JButton btnOpenButton = new JButton("Open");
		btnOpenButton.setBounds(15, 255, 71, 29);
		btnOpenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ImagePlus raw_img = IJ.openImage();
							if (raw_img != null) {
								ImageProcessingWindow imageProcessing = new ImageProcessingWindow(raw_img);
								imageProcessing.setVisible(true);
							} else {
								JOptionPane.showMessageDialog(panel.getParent(), "You must introduce a valid image or set of images.");
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		panel.setLayout(null);

		// Create table and scroll pane
		this.tableInfo = new JTableModel();
		JTable table = new JTable(tableInfo);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new JColorEditor());
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(15, 27, 420, 196);
		// scrollPane.setPreferredSize(new Dimension(400, 200));

		panel.add(scrollPane);
		panel.add(btnOpenButton);
		panel.add(btnVisualize);

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public void createAndShowGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("TableDemo");

		// Create and set up the content pane.
		MainWindow newContentPane = new MainWindow();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setSize(500, 400);
		frame.setVisible(true);
	}

	/**
	 * 
	 * @param newImages
	 */
	public void addNewImagesProcessed(ArrayList<GraphletImage> newImages) {
		this.tableInfo.addImages(newImages);
	}
}
