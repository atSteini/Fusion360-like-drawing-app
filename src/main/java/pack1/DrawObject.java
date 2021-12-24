package pack1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

public class DrawObject {
	private ArrayList<DrawPoint> points;
	private DrawPoint airPoint = new DrawPoint(DrawPoint.AIR, 0, 0);
	
	private int mode, previousMode;
	private boolean isSnapped = false;
	
	public static boolean showPoints = true;
	
	public static final int RECT_CORNER = -2;			//Negativ damit man nicht aus versehen die Modi vertauscht
	int rectMode = RECT_CORNER;
	DrawPoint rectangleSecondPoint;
	
	//Ich h�tte es gerne mit Vererbung und mehereren Objekten gemacht, aber ich war zu faul
	public static final int NONE = 0, LINES = 1, POINT = 2, CIRCLE = 3, RECT = 4;

	private Color color;
	public static Color airColor = Color.black, placedColor = new Color(30, 160, 255);
	
	private boolean drawLength = true, drawAngle = true;
	double distanceCircle, distanceRect;
	
	float strokeW = 1.0f;
	
	public DrawObject(int mode) {
		if(mode != NONE) {
			previousMode = mode;
		}
		
		this.mode = mode;
		this.points = new ArrayList<>();
	}
	
	public void draw(Graphics2D g2d) {
		switch(mode) {
		case LINES:
			drawLines(g2d);
			drawPoints(g2d);
			drawAirPoint(g2d);
			break;
		case POINT:
			drawAirPoint(g2d);
			drawPoints(g2d);
			break;
		case CIRCLE:
			drawAngle = false;
			drawAirPoint(g2d);
			drawCircle(g2d);
			break;
		case RECT:
			drawAngle = false;
			drawAirPoint(g2d);
			drawRect(g2d);
			break;
		case NONE:
			if(previousMode == LINES) {
				drawLines(g2d);
			}else if(previousMode == CIRCLE) {
				drawCircle(g2d);
			}else if(previousMode == RECT) {
				drawRect(g2d);
			}
			if(showPoints)
				drawPoints(g2d);
			break;
		default:
			System.err.println("DrawObject Error! Mode " + mode + " is not defined!");
		}
	}

	private void drawRect(Graphics2D g2d) {
		if(size() == 1) {
			g2d.setColor(airColor);
			if(mode == RECT) {
				rectangleSecondPoint = airPoint;
			}
		}
		else if(size() == 2) {
			g2d.setColor(placedColor);
			rectangleSecondPoint = getPoint(1);
		}

		if(size() > 0) {
			distanceRect = Draw.dist(getPoint(0).toPoint(), rectangleSecondPoint.toPoint());
			g2d.setStroke(new BasicStroke(strokeW));
			
			if(rectMode == RECT_CORNER) {
				double XLength = rectangleSecondPoint.getX() - getPoint(0).getX(); 
				double YLength = rectangleSecondPoint.getY() - getPoint(0).getY();
				String X = Double.toString(Math.abs(XLength));
				String Y = Double.toString(Math.abs(YLength));
				
				//g2d.drawString(Y, getPoint(0).getX() - getStringWidth(Y, g2d), (int)(getPoint(0).getY() + YLength/2 + getFontHeight(g2d)/2));
				drawRectPointToPoint(getPoint(0), rectangleSecondPoint, g2d);
			}else {
				System.err.println("Rectangle Mode " + rectMode + " is not defined");
			}
		}
	}

	void drawRectPointToPoint(DrawPoint p1, DrawPoint p2, Graphics2D g2d) {
		Rectangle rect = new Rectangle(p1.toPoint());
		rect.add(p2.toPoint());

		g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	private void drawCircle(Graphics2D g2d) {
		if(size() == 1) {
			g2d.setColor(airColor);
			if(mode == CIRCLE) {
				distanceCircle = Draw.dist(getPoint(0).toPoint(), airPoint.toPoint());
			}
		}else if(size() == 2) {
			g2d.setColor(placedColor);
			if(getPoint(1).isDragged()) {
				distanceCircle = Draw.dist(getPoint(0).toPoint(), getPoint(1).toPoint());
			}
			if(getPoint(0).isDragged()) {
				getPoint(1).setCoordinates(	(int) (getPoint(0).getX() + (distanceCircle * Math.cos(getAngleRadians(getPoint(0), getPoint(1)))) ), 
											(int) (getPoint(0).getY() + (distanceCircle * Math.sin(getAngleRadians(getPoint(0), getPoint(1)))) ));
			}
		}
		
		if(size() > 0) {
			g2d.drawOval((int) (getPoint(0).getX() - distanceCircle), (int) (getPoint(0).getY() - distanceCircle), (int) distanceCircle*2, (int) distanceCircle*2);
		}
	}

	private void drawAirPoint(Graphics2D g2d) {
		if(airPoint != null) {
			airPoint.draw(g2d);
			
			setColor(airPoint, g2d);
			if(getLastPoint() != null) {
				drawLine(getLastPoint(), airPoint, g2d);
				
				double distance = Draw.dist(getLastPoint().getX(), getLastPoint().getY(), airPoint.getX(), airPoint.getY());
				
				if(distance > 45) {
					if(drawLength) {
						double angle = getAngleRadians(getLastPoint(), airPoint);
						
						String distanceString = String.format("%.2f", distance) + " px";
						
						double rotateX = distance * Math.cos(angle) + getLastPoint().getX();
						double rotateY = distance * Math.sin(angle) + getLastPoint().getY();
						
						int stringDrawX =(int) (rotateX - distance/2 - getStringWidth(distanceString, g2d)/2);
						
						int stringDrawY = 0;
						
						double angleDeg = Math.toDegrees(angle);
						if(isBetween(angleDeg, -90, 0) || isBetween(angleDeg, 90, 180)) {
							stringDrawY = (int) (rotateY) - getFontHeight(g2d)/2;
						}else if(isBetween(angleDeg, -90, -180) || isBetween(angleDeg, 0, 90)){
							stringDrawY = (int) (rotateY) + getFontHeight(g2d);
						}
						
						if(isBetween(angle, Math.toRadians(-90), Math.toRadians(89))) {
							g2d.rotate(angle, rotateX, rotateY);
							g2d.drawString(distanceString, stringDrawX, stringDrawY);
							g2d.rotate(-angle, rotateX, rotateY);
						}else {
							stringDrawX += distance;
							g2d.rotate(angle + Math.toRadians(180), rotateX, rotateY);
							g2d.drawString(distanceString, stringDrawX, stringDrawY);
							g2d.rotate(-angle + Math.toRadians(180), rotateX, rotateY);
						}
					}
					if(drawAngle) {
						double angle = getAngleRadians(getLastPoint(), airPoint);
						double angleDeg = (Math.toDegrees(angle)) % 360 * -1;
						String angleString = String.format("%.2f", angleDeg);

						double radius = distance;

						Arc2D arc = new Arc2D.Double(getLastPoint().getX() - radius/2, getLastPoint().getY() - radius/2, radius, radius, 0, -Math.toDegrees(angle), Arc2D.OPEN);
						g2d.draw(arc);
						
						g2d.drawLine(getLastPoint().getX(), getLastPoint().getY(), getLastPoint().getX() + (int) radius/2 + (int) radius/10, getLastPoint().getY());
							
						int drawRadius = (int) (radius/2 + radius/10);
						g2d.drawString(angleString +"°", (int) (getLastPoint().getX() + ( Math.cos(angle/2) * drawRadius)), (int) (getLastPoint().getY() + ( Math.sin(angle/2) * drawRadius)));
					}
				}
			}

		}
	}
	
	public int getPreviousMode() {
		return previousMode;
	}

	public void setPreviousMode(int previousMode) {
		this.previousMode = previousMode;
	}

	public boolean isSnapped() {
		return isSnapped;
	}

	public void setSnapped(boolean isSnapped) {
		this.isSnapped = isSnapped;
	}

	public boolean isDrawLength() {
		return drawLength;
	}

	public void setDrawLength(boolean drawLength) {
		this.drawLength = drawLength;
	}

	public boolean isDrawAngle() {
		return drawAngle;
	}

	public void setDrawAngle(boolean drawAngle) {
		this.drawAngle = drawAngle;
	}

	boolean isBetween(double x, double a, double b) {
		if(a < b)
			return x >= a && x <= b;
		else
			return x >= b && x <= a;
	}
	
	int getFontHeight(Graphics2D g2d) {
		return g2d.getFontMetrics().getHeight();
	}
	
	int getStringWidth(String s, Graphics2D g2d) {
		return g2d.getFontMetrics().stringWidth(s);
	}
	
	private void drawPoints(Graphics2D g2d, int notStart, int notEnd) {
		for (int i = 0; i < points.size(); i++) {
			if(i < notStart || i > notEnd) {
				points.get(i).draw(g2d);
			}
		}
	}
	
	private void drawPoints(Graphics2D g2d) {
		for (DrawPoint drawPoint : points) {
			drawPoint.draw(g2d);
		}	
	}
	
	private void drawLine(DrawPoint p1, DrawPoint p2, Graphics2D g2d) {
		if(mode == RECT) {
			g2d.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
		}else {
			g2d.setStroke(new BasicStroke(strokeW));
		}
		g2d.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	
	private void drawLines(Graphics2D g2d) {
		DrawPoint now, next;
		
		for(int i = 0; i < points.size() - 1; i++) {
			now = points.get(i);
			next = points.get(i+1);
			
			setColor(now, g2d);
			drawLine(now, next, g2d);
		}

	}
	
	double getAngleRadians(DrawPoint p1, DrawPoint p2) {
		return Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
	}
	
	double getAngleDegrees(DrawPoint p1, DrawPoint p2) {
		return Math.toDegrees(getAngleRadians(p1, p2));
	}
	
	private void setColor(DrawPoint p, Graphics2D g2d) {
		if(p.getMode() == DrawPoint.AIR)
			g2d.setColor(airColor);
		else if(p.getMode() == DrawPoint.PLACED)
			g2d.setColor(placedColor);
		else
			g2d.setColor(placedColor);
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append(previousMode + ";");
		
		for(int i = 0; i < points.size(); i++) {
			str.append(getPoint(i));
			
			if(i < size() - 1) {
				str.append(";");
			}
		}
		
		return str.toString();
	}
	
	public DrawPoint getAirPoint() {
		return airPoint;
	}

	public void setAirPoint(DrawPoint airPoint) {
		this.airPoint = airPoint;
	}

	public DrawPoint getPoint(int index) {
		updateIndex();
		if(index >= 0 && index < points.size())
			return points.get(index);
		else
			System.err.println("getDrawPoint Error! Index must be inbetween Bounds: 0 - " + (size()-1) + " ! Index: " + index);
			return null;
	}
	
	public void updateIndex() {
		for(int i = 0; i < size(); i++) {
			points.get(i).setIndex(i);
		}
	}
	
	public int size() {
		return points.size();
	}
	
	public void clearPoints() {
		points.clear();
	}
	
	public void removePoint(int index) {
		updateIndex();
		if(index >= 0 && index < points.size()) {
			points.remove(index);
			updateIndex();
		}
		else
			System.err.println("RemovePoint Error! Index must be inbetween Bounds: 0 - " + (size()-1) + " ! Index: " + index);
	}
	
	public void addPoint(DrawPoint p) {
		p.setIndex(points.size());
		points.add(p);
		updateIndex();
	}

	public void setPoint(int index, DrawPoint p) {
		p.setIndex(index);
		points.set(index, p);
		updateIndex();
	}
	
	public DrawPoint getLastPoint() {
		if(size() > 0)
			return points.get(size() - 1);
		else
			return null;
	}
	
	public void setLastPoint(DrawPoint p) {
		if(size() > 0)
			points.set(size() - 1, p);
	}
	
	public void removeLastPoint() {
		if(size() > 0)
			points.remove(size() - 1);
	}
	
	public ArrayList<DrawPoint> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<DrawPoint> points) {
		this.points = points;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		if(mode != NONE) {
			previousMode = mode;
		}
		this.mode = mode;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public static Color getAirColor() {
		return airColor;
	}

	public static void setAirColor(Color airColor) {
		DrawObject.airColor = airColor;
	}

	public static Color getPlacedColor() {
		return placedColor;
	}

	public static void setPlacedColor(Color placedColor) {
		DrawObject.placedColor = placedColor;
	}
	
	public int getRectMode() {
		return rectMode;
	}
	
	public void setRectMode(int rectMode) {
		this.rectMode = rectMode;
	}
}
