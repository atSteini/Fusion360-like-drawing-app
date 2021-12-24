package pack1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Preferences extends JFrame {
	App parentFrame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Preferences frame = new Preferences();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Preferences(pack1.App app) {
		this.parentFrame = app;
		
		init();
	}
	
	public Preferences() {
		init();
	}
	
	public void init() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		final JCheckBox chckbxShowpoints = new JCheckBox("show Points");
		chckbxShowpoints.setSelected(DrawObject.showPoints);
		chckbxShowpoints.setBackground(Color.WHITE);
		chckbxShowpoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DrawObject.showPoints = chckbxShowpoints.isSelected();
				parentFrame.repaintPanel();
			}
		});
		
		JLabel lblGridSize = new JLabel("Grid Size");

		final JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(Draw.getGridSize(), 1, null, 1));
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Draw.GRID_SIZE = (int) spinner.getValue();
				parentFrame.repaintPanel();
			}
		});

		
		Dimension window = new Dimension(450, 450);
		setTitle("Preferences");

		contentPane.add(chckbxShowpoints);
		contentPane.add(lblGridSize);
		contentPane.add(spinner);

		if(parentFrame != null) {
			setBounds((int) (parentFrame.getX() - window.getWidth()), (int) parentFrame.getY(), (int) window.getWidth(), (int) window.getHeight());	
			if(getBounds().getX() < 0 || getBounds().getY() < 0) {
				setBounds(100, 100, (int) window.getWidth(), (int) window.getHeight());
			}
		}else {
			System.exit(0);
		}
	}
}
