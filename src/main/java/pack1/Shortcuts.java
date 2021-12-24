package pack1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;

public class Shortcuts extends JFrame {

	App parentFrame;
	JTextArea tarHelp;
	
	String helpText = 	"Points snap the Grid & to each other!\nWhen a Point is Snapping to something, it will be indicated by a blue square around the Cursor.\n"
						+ "When you close up a Shape, you will Exit the current Mode (e.g. Linedraw-Mode)!\n\n"
						+ "File Saving System:\n"
						+ "\t- mode;x1;y1;x2;y2;...xn;yn\n\n"
						+ "Keys Pressed: \n"
						+ "'l' \t--> new Line\n"
						+ "'r' \t--> new Rectangle\n"
						+ "'c' \t--> new Circle\n"
						+ "'p' \t--> new Point\n"
						+ "'Esc' \t--> Exit Edit Mode for current Object\n"
						+ "'CTRL' \t--> Points don't snap to the Grid while holding\n\n"
						+ "Mouse Clicked:\n"
						+ "Drag and Drop Points\n"
						+ "Right Click a Point to delete it";

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Shortcuts frame = new Shortcuts();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Shortcuts(App app) {
		this.parentFrame = app;
		
		init();
	}
	
	public Shortcuts() {
		init();
	}
	
	public void init() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(2)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
		);
		
		tarHelp = new JTextArea();
		tarHelp.setWrapStyleWord(true);
		tarHelp.setLineWrap(true);
		tarHelp.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tarHelp.setText(helpText);
		tarHelp.setEditable(false);
		scrollPane.setViewportView(tarHelp);
		contentPane.setLayout(gl_contentPane);
		
		Dimension window = new Dimension(450, 450);
		setTitle("Information");
		
		if(parentFrame != null) {
			setBounds((int) (parentFrame.getX() - window.getWidth()), (int) parentFrame.getY(), (int) window.getWidth(), (int) window.getHeight());	
			if(getBounds().getX() < 0 || getBounds().getY() < 0) {
				setBounds(100, 100, (int) window.getWidth(), (int) window.getHeight());
			}
		} else {
			System.exit(0);
		}
	}
}
