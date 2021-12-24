package pack1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class App extends JFrame {

	Draw drawing;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public App() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		setTitle("Drawing Application");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (	ClassNotFoundException | 
					InstantiationException | 
					IllegalAccessException | 
					UnsupportedLookAndFeelException e
				) {
			e.printStackTrace();
		}
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(drawing.checkSaved() == 1) {
					System.exit(0);
				}else {
					drawing.saveObjects();
				}
			}
		});
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawing.saveObjects();
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawing.openObjects();
			}
		});
		mnFile.add(mntmOpen);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				App app = getThisApp();
				Preferences frame = new Preferences(app);
				frame.setVisible(true);
			}
		});
		mnFile.add(mntmPreferences);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mnFile.add(mntmExit);
		
		JMenu mnObjects = new JMenu("Objects");
		menuBar.add(mnObjects);
		
		JMenuItem mntmLine = new JMenuItem("Line");
		mntmLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				drawing.setMode(DrawObject.LINES);
				drawing.addObject();
			}
		});
		mnObjects.add(mntmLine);
		
		JMenuItem mntmRectangle = new JMenuItem("Rectangle");
		mntmRectangle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				drawing.setMode(DrawObject.RECT);
				drawing.addObject();
			}
		});
		mnObjects.add(mntmRectangle);
		
		JMenuItem mntmCircle = new JMenuItem("Circle");
		mntmCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawing.setMode(DrawObject.CIRCLE);
				drawing.addObject();
			}
		});
		mnObjects.add(mntmCircle);
		
		JMenuItem mntmPoint = new JMenuItem("Point");
		mntmPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawing.setMode(DrawObject.POINT);
				drawing.addObject();
			}
		});
		mnObjects.add(mntmPoint);
		
		JSeparator separator = new JSeparator();
		mnObjects.add(separator);
		
		JMenuItem menuItem = new JMenuItem("Clear");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				drawing.clearObjects();
				drawing.repaint();
			}
		});
		mnObjects.add(menuItem);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmShortcuts = new JMenuItem("Useful Information");
		mntmShortcuts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				App app = getThisApp();
				Shortcuts frame = new Shortcuts(app);
				frame.setVisible(true);
			}
		});
		mnHelp.add(mntmShortcuts);

		JPanel contentPane = new JPanel();
		
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		
		drawing = new Draw(this);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(drawing, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(drawing, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
		);
		GroupLayout gl_drawing = new GroupLayout(drawing);
		gl_drawing.setHorizontalGroup(
			gl_drawing.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 584, Short.MAX_VALUE)
		);
		gl_drawing.setVerticalGroup(
			gl_drawing.createParallelGroup(Alignment.LEADING)
				.addGap(0, 540, Short.MAX_VALUE)
		);
		drawing.setLayout(gl_drawing);
		contentPane.setLayout(gl_contentPane);
	}
	
	void repaintPanel() {
		drawing.repaint();
	}
	
	App getThisApp() {
		return this;
	}
}
