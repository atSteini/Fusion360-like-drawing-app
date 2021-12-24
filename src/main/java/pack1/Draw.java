package pack1;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Draw extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
	private final JFrame app;
	
	int w, h;
	int mouseX, mouseY;
	int drawX, drawY;
	
	boolean init, mouseInFrame;
	
	int drawingMode = DrawObject.NONE;
	
	final Color bg = color(255), gr = color(240);

	static int GRID_SIZE = 30;
	ArrayList<Point> snapPoints;
	int snapDistanceThreshold = GRID_SIZE/3;
	boolean noSnap = false;
	int smallerGridsInBiggerGrid = 6;
		
	DrawPoint airPoint = new DrawPoint(DrawPoint.AIR, 0, 0);
	
	ArrayList<DrawObject> allObjects = new ArrayList<>();
	int objectIndex = -1;
	
	boolean isSnapped = false;

	Point hoveredPoint = new Point(-1 , -1);		//AllObjIndex, DrawObjIndex
	
	boolean isSaved;
	
	int tempSnapIndex = -1;
	
	public Draw(JFrame App) {
		this.app = App;
		
		addObject();
		isSaved = true;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				snapPoints = getGrid();
			}
		});
	}
	
	//////////////////////////
			//Custom//
	//////////////////////////
	Color color(int c) {
		return new Color(c, c, c);
	}

	Color color(Color c, int i) {
		return color(c.getRed() + i);
	}
	
	ArrayList<Point> getGrid() {
		ArrayList<Point> a = new ArrayList<>();
		
		for(int i = 0; i <= w/GRID_SIZE; i++) {
			for(int j = 0; j <= h/GRID_SIZE; j++) {
				a.add(new Point(i*GRID_SIZE, j*GRID_SIZE));
			}
		}

		return a;
	}
	
	Point getNextSnappable(ArrayList<Point> a, Point p) {
		Point nearest = getNeareastPoint(snapPoints, p);
		
		if(nearest.distance(p) < snapDistanceThreshold && !noSnap) {
			return nearest;
		}else {
			return p;
		}
	}
	
	Point getNeareastPoint(ArrayList<Point> a, Point p) {
		if(a != null && a.size() > 0 && p != null) {
			Point nearest = a.get(0);
			for (Point point : a) {
				if (point.distance(p) < nearest.distance(p)) {
					nearest = point;
				}
			}
			return nearest;
		}else {
			return null;
		}
	}
	
	Point getDrawingPoint() {
		return getNextSnappable(snapPoints, new Point(mouseX, mouseY));
	}
	
	/**
	 * War zu faul um diese Methode sch�ner zu schreiben. Habe die anderen Methoden erst danach geschrieben.
	 */
	void setDrawingPoint() {
		Point nearest = getNeareastPoint(snapPoints, new Point(mouseX, mouseY));
		if(nearest.distance(new Point(mouseX, mouseY)) < snapDistanceThreshold && !noSnap) {
			drawX = (int) nearest.getX();
			drawY = (int) nearest.getY();
			airPoint.setSnapped(true);
		}else {
			drawX = mouseX;
			drawY = mouseY;
			airPoint.setSnapped(false);
		}
	}
	
	void drawGrid(Graphics2D g2d, Color thinColor, Color thickColor) {
		for(int i = 0; i <= w/GRID_SIZE; i ++ ) {
			if(i % smallerGridsInBiggerGrid == 0)
				g2d.setColor(thickColor);
			else
				g2d.setColor(thinColor);
			
			g2d.drawLine(i*GRID_SIZE, 0, i*GRID_SIZE, h);
		}
		
		for(int i = 0; i <= h/GRID_SIZE; i ++) {
			if(i % smallerGridsInBiggerGrid == 0)
				g2d.setColor(thickColor);
			else
				g2d.setColor(thinColor);
			
			g2d.drawLine(0, i*GRID_SIZE, w, i*GRID_SIZE);
		}
	}

	public static double dist(Point p1, Point p2) {
		return dist(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	
	public static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
	
	void updatePanel(Graphics2D g2d) {
		w = getWidth(); h = getHeight();
		if(!init){
			g2d.setBackground(bg);
			
			init = true;
		}

		snapPoints = getGrid();
		
		g2d.clearRect(0, 0, w, h);
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		setDrawingPoint();
	}

	public void setMode(int mode) {
		drawingMode = mode;
		
		if(mode == DrawObject.NONE) {
			for(int i = 0; i < allObjects.size(); i++) {
				if(	allObjects.get(i).size() <= 1 &&
					allObjects.get(i).getLastPoint() != null &&
					allObjects.get(i).getPreviousMode() != DrawObject.POINT) 
				{
						snapPoints.remove(snapPoints.size() - 1);
						removeObject(i);
				}
			}
			
			if(objectIndex >= 0 && allObjects.size() > objectIndex)
				allObjects.get(objectIndex).setMode(DrawObject.NONE);
			
		}
	}
	
	private void removeObject(int i) {
		isSaved = false;
		allObjects.remove(i);
		objectIndex--;
	}

	public void addObject() {
		isSaved = false;
		objectIndex++;
		allObjects.add(objectIndex, new DrawObject(drawingMode));
	}
	
	private DrawPoint getHoveredPoint() {
		DrawPoint p = null;

		for (DrawObject allObject : allObjects) {
			for (int j = 0; j < allObject.size(); j++) {
				DrawPoint temp = allObject.getPoint(j);

				if (dist(temp.getX(), temp.getY(), mouseX, mouseY) < temp.getPlacedSize()) {
					allObject.getPoint(j).setMode(DrawPoint.HOVER);
					p = allObject.getPoint(j);
				} else {
					allObject.getPoint(j).setMode(DrawPoint.PLACED);
				}
			}
		}
		
		return p;
	}

	private void checkOverlap(DrawPoint temp) {
		for (DrawObject allObject : allObjects) {
			for (int j = 0; j < allObject.size(); j++) {
				DrawPoint p = allObject.getPoint(j);
				if (p.getX() == temp.getX() && p.getY() == temp.getY()) {
					if (p.getDrawObject() != temp.getDrawObject()) {
						merge(p.getDrawObject(), temp.getDrawObject());
						break;
					}
				}
			}
		}
	}

	private void merge(int obj1, int obj2) {
		for(int i = 0; i < allObjects.get(obj2).size(); i++) {
			if(obj1 >= 0 && allObjects.size() > obj1 && obj2 >= 0 && allObjects.size() > obj2) {
				allObjects.get(obj1).addPoint(allObjects.get(obj2).getPoint(i));
			}
		}
		allObjects.get(obj1).setMode(allObjects.get(obj2).getMode());
		removeObject(obj2);
	}
	
	static String getObjectsString(ArrayList<DrawObject> a) {
		StringBuilder str = new StringBuilder();
		
		for (DrawObject drawObject : a) {
			str.append(drawObject);
			str.append("\n");
		}
		
		return str.toString();
	}

	public void clearObjects() {
		allObjects.clear();
		objectIndex = -1;
		
		isSaved = true;
		
		repaint();
	}
	
	public void saveObjects() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save File");
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(app);
		
		String path = "";
		if (result == JFileChooser.APPROVE_OPTION) {
			path = fileChooser.getSelectedFile().getAbsolutePath();		
			int endindex = 0;
			
			for(int i = 0; i < path.length(); i++) {
				if(path.charAt(i) == '.') {
					endindex = i;
					break;
				}
			}
			
			if(!path.substring(endindex, path.length()).equals(".txt")){
				
				if(endindex == 0) {
					path += ".txt";
				}else {
					path = path.substring(0, endindex) + ".txt";
				}
			}
		
			try {
				BufferedWriter output = new BufferedWriter(new FileWriter(path));
				output.write(getObjectsString(allObjects));
				
				isSaved = true;
				
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		repaint();
	}
	
	public void openObjects() {
		int confirmDialog = checkSaved();
		
		if(confirmDialog == 1) {
			clearObjects();
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Open File");
	
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
	
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(app);
			
			String path = "";
			if (result == JFileChooser.APPROVE_OPTION) {
				path = fileChooser.getSelectedFile().getAbsolutePath();		
				int endindex = 0;
				
				for(int i = 0; i < path.length(); i++) {
					if(path.charAt(i) == '.') {
						endindex = i;
						break;
					}
				}
				
				if(!path.substring(endindex, path.length()).equals(".txt")){
					JOptionPane.showMessageDialog(null, "Unknown Filetype. Please select some other File!", "Unknown File-Type-Extension", JOptionPane.ERROR_MESSAGE);
				}else {
					BufferedReader input;
					try {
						input = new BufferedReader(new FileReader(path));
						
						String line;
						ArrayList<String> lines = new ArrayList<String>();
						while ((line = input.readLine()) != null) {
							lines.add(line);
						}
						input.close();
						
						if(lines.size() > 0) {
							char splittingChar = getSplittingChar(lines.get(0));
							
							for(int i = 0; i < lines.size(); i++) {
								String[] lineSplit = lines.get(i).split(Character.toString(splittingChar));
								
								addObject();
								allObjects.get(i).setMode(Integer.parseInt(lineSplit[0]));
								
								for(int j = 1; j < lineSplit.length; j += 2) {
									int x = Integer.parseInt(lineSplit[j]);
									int y = Integer.parseInt(lineSplit[j+1]);
									
									DrawPoint temp = new DrawPoint(DrawPoint.PLACED, x, y);
									allObjects.get(objectIndex).addPoint(temp);
								}
								
								allObjects.get(i).setMode(DrawObject.NONE);
							}
						}else {
							JOptionPane.showMessageDialog(this, "The selected File is Empty!\nPlease select some other File.", "Empty File", JOptionPane.ERROR_MESSAGE);
						}
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}else if(confirmDialog == 0) {
			saveObjects();
		}
		
		repaint();
	}

	public int checkSaved() {
		int c = 1;			//0 = yes, 1 = no
		if(!isSaved) {
			c = JOptionPane.showConfirmDialog(	null, "You have got unsaved work. Would you like to save your Masterpiece?", "Unsaved",
															JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
		}
		
		return c;
	}

	char getSplittingChar(String s) {
		for(int i = 0; i < s.length(); i++) {
			if(!isInAlphabet(s.charAt(i)) && !isNumber(Character.toString(s.charAt(i))) ) {
				return s.charAt(i);
			}
		}
		
		System.err.println("Error splitting Input String: " + s);
		return 'e';
	}
	
	boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
		}catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	boolean isInAlphabet(char c) {
		char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ü'};
		
		c = Character.toLowerCase(c);

		for (char value : alphabet) {
			if (c == value) {
				return true;
			}
		}
		
		return false;
	}

	private int getIndex(ArrayList<DrawObject> a, DrawPoint p) {
		int k = -1;
		for(int i = 0; i < a.size(); i++) {
			for(int j = 0; j < a.get(i).size(); j++) {
				if(a.get(i).getPoint(j) == p) {
					k = i;
				}
			}
		}
		
		return k;
	}
	
	public int getIndex(ArrayList<Point> a, Point p) {
		int j = -1;
		for(int i = 0; i < a.size(); i++) {
			if(a.get(i).getX() == p.getX() && a.get(i).getY() == p.getY()) {
				j = i;
			}
		}
		
		return j;
	}
	
	public static int getGridSize() {
		return GRID_SIZE;
	}
	
	//////////////////////////////////
			//Auto-generated//
	//////////////////////////////////
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		updatePanel(g2d);
		
		drawGrid(g2d, gr, color(gr, -15));
		
		g2d.setFont(new Font("Tahoma", Font.PLAIN, 15));
		for (DrawObject drawObject : allObjects) {
			drawObject.draw(g2d);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(mouseInFrame) {
			mouseX = e.getX();
			mouseY = e.getY();
			
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(drawingMode != DrawObject.NONE && allObjects.size() != 0) {
					DrawPoint temp = new DrawPoint(DrawPoint.PLACED, drawX, drawY);
					temp.setDrawObject(objectIndex);
					
					//checkOverlap(temp);			//nette Idee, schlecht ausgef�hrt
					
					if(objectIndex >= 0 && allObjects.size() > objectIndex) {
						allObjects.get(objectIndex).addPoint(temp);
						
						if(		temp.equals(allObjects.get(objectIndex).getPoint(0)) && allObjects.get(objectIndex).size() > 1 
								|| (drawingMode == DrawObject.POINT && allObjects.get(objectIndex).size() > 0)
								|| (drawingMode == DrawObject.CIRCLE && allObjects.get(objectIndex).size() > 1)
								|| (drawingMode == DrawObject.RECT && allObjects.get(objectIndex).size() > 1)) {
							setMode(DrawObject.NONE);
						}
					}
					
					snapPoints.add(temp.toPoint());
				}
			}else if(e.getButton() == MouseEvent.BUTTON3) {
				DrawPoint hov = getHoveredPoint();
				
				if(drawingMode == DrawObject.NONE && hov != null) {
					int drawObjectIndex = getIndex(allObjects, hov);
					System.out.println(drawObjectIndex);
					
					if(drawObjectIndex >= 0) {
						allObjects.get(drawObjectIndex).removePoint(hov.getIndex());

						if(	allObjects.get(drawObjectIndex).size() <= 2 && allObjects.get(drawObjectIndex).getPreviousMode() == DrawObject.CIRCLE ||
							allObjects.get(drawObjectIndex).size() <= 1 && allObjects.get(drawObjectIndex).getPreviousMode() == DrawObject.LINES ||
							allObjects.get(drawObjectIndex).size() <= 2 && allObjects.get(drawObjectIndex).getPreviousMode() == DrawObject.RECT) 
						{
							removeObject(drawObjectIndex);
						}
					}else {
						System.err.println("Error removing " + drawObjectIndex);
					}
				}else {
					setMode(DrawObject.NONE);
				}
			}

			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//Ja, ich wei� ich h�tte es auch anders machen k�nnen aber das konstante Einrasten gef�llt mir nicht. 
		//Ich bevorzuge einen gewissen Radius um den Grid, in dem das Rechteck einrastet.
		//Wie zB. in dem CAD-Programm Fusion360
		if(mouseInFrame) {
			mouseX = e.getX();
			mouseY = e.getY();
			
			airPoint.setCoordinates(drawX, drawY);
			if(objectIndex >= 0 && allObjects.size() > objectIndex) {
				allObjects.get(objectIndex).setAirPoint(airPoint);
			}
			
			getHoveredPoint();		//Setzt auch gleichzeitig den Status aller Punkte.
			
			repaint();	
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseInFrame = true;
		
		repaint();
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouseInFrame = false;
		
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_L:
			setMode(DrawObject.LINES);
			addObject();
			System.out.println("Line");
			break;
		case KeyEvent.VK_P:
			setMode(DrawObject.POINT);
			addObject();
			System.out.println("Point");
			break;
		case KeyEvent.VK_C:
			setMode(DrawObject.CIRCLE);
			addObject();
			System.out.println("Circle");
			break;
		case KeyEvent.VK_R:
			setMode(DrawObject.RECT);
			addObject();
			break;
		case KeyEvent.VK_ESCAPE:
			setMode(DrawObject.NONE);
			break;
		case KeyEvent.VK_CONTROL:
			noSnap = true;
		}
		
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			noSnap = false;
		}
		
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		for(int i = 0; i < allObjects.size(); i++) {
			for(int j = 0; j < allObjects.get(i).size(); j++) {
				// && getHoveredPoint() != allObjects.get(i).getLastPoint()
				if(allObjects.get(i).getPoint(j).isDragged()) {
					allObjects.get(i).getPoint(j).setCoordinates(getNextSnappable(snapPoints, e.getPoint()));
				}
			}
		}
		
		if(getHoveredPoint() != null) {
			if(tempSnapIndex < 0) {
				tempSnapIndex = getIndex(snapPoints, getHoveredPoint().toPoint());
			}
			
			getHoveredPoint().setDragged(true);
		}
		
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(int i = 0; i < allObjects.size(); i++) {
			for(int j = 0; j < allObjects.get(i).size(); j++) {
				if(allObjects.get(i).getPoint(j).isDragged()) {
					allObjects.get(i).getPoint(j).setDragged(false);
					
					if(tempSnapIndex >= 0) {
						snapPoints.remove(tempSnapIndex);
						tempSnapIndex = -1;
					}
					
					snapPoints.add(allObjects.get(i).getPoint(j).toPoint());
				}
			}
		}
		
		repaint();
	}
	//////////////////////////
			//Unused//
	//////////////////////////
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
