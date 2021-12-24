package pack1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class DrawPoint {
	private int x, y;

	public static final int AIR = 0, PLACED = 1, HOVER = 2;
	private int mode;

	public static Color airColor = Color.black, placedColor = Color.white, hoverColor = new Color(255, 160, 30), outlineColor = Color.black;
	private Color color;

	private int airSize = 19, placedSize = 7;
	private int size;
	
	private boolean isSnapped, isDragged;
	
	private int drawObject = -1, index = -1;

	public DrawPoint(int mode, int x, int y) {
		super();
		this.x = x;
		this.y = y;
		this.mode = mode;

		this.size = airSize;
	}

	private void drawCross(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.drawLine(x - airSize / 2, y, x + airSize / 2, y);
		g2d.drawLine(x, y - airSize / 2, x, y + airSize / 2);
	}

	private void fillCircle(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.fillOval(x - placedSize/2, y - placedSize/2, placedSize, placedSize);
	}
	
	private void drawCircle(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.drawOval(x - placedSize/2, y - placedSize/2, placedSize, placedSize);
	}
	
	private void drawRect(Graphics2D g2d) {
		g2d.setColor(color);
		int s = airSize*2/3;
		g2d.drawRect(x - s/2, y - s/2, s, s);
	}

	public Point toPoint() {
		return new Point(this.x, this.y);
	}
	
	public void draw(Graphics2D g2d) {
		switch (mode) {
		case AIR:
			color = airColor;
			drawCross(g2d);
			break;
		case PLACED:
			color = placedColor;
			fillCircle(g2d);
			
			color = outlineColor;
			drawCircle(g2d);
			break;
		case HOVER:
			color = hoverColor;
			fillCircle(g2d);
			
			color = outlineColor;
			drawCircle(g2d);
			break;
		default:
			System.err.println("Mode Error! " + this);
		}
		
		if(this.isSnapped || isDragged) {
			color = DrawObject.placedColor;
			drawRect(g2d);
		}
	}

	public void setCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return x + ";" + y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.getClass() == obj.getClass()) {
			DrawPoint p = (DrawPoint) obj;

			return p.getX() == this.getX() && p.getY() == this.getY();
		}

		return false;
	}
	
	public int getAirSize() {
		return airSize;
	}

	public void setAirSize(int airSize) {
		this.airSize = airSize;
	}

	public int getPlacedSize() {
		return placedSize;
	}

	public void setPlacedSize(int placedSize) {
		this.placedSize = placedSize;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public static Color getAirColor() {
		return airColor;
	}

	public static void setAirColor(Color airColor) {
		DrawPoint.airColor = airColor;
	}

	public static Color getPlacedColor() {
		return placedColor;
	}

	public static void setPlacedColor(Color placedColor) {
		DrawPoint.placedColor = placedColor;
	}

	public static Color getHoverColor() {
		return hoverColor;
	}

	public static void setHoverColor(Color hoverColor) {
		DrawPoint.hoverColor = hoverColor;
	}

	public static Color getOutlineColor() {
		return outlineColor;
	}

	public static void setOutlineColor(Color outlineColor) {
		DrawPoint.outlineColor = outlineColor;
	}

	public int getDrawObject() {
		return drawObject;
	}

	public void setDrawObject(int inDrawObject) {
		this.drawObject = inDrawObject;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setSnapped(boolean isSnapped) {
		this.isSnapped = isSnapped;
	}

	public void setCoordinates(Point p) {
		this.x = (int) p.getX();
		this.y = (int) p.getY();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isSnapped() {
		return isSnapped;
	}

	public boolean isDragged() {
		return isDragged;
	}

	public void setDragged(boolean isDragged) {
		this.isDragged = isDragged;
	}
}
