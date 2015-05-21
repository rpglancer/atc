package atc.lib;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import atc.Game;
import atc.type.TYPE;

public class Runway extends Entity{
	private Line2D departPath = null;
	private Line2D runwayPath = null;
	private Localizer localizer;
	private boolean isOpen = false;
	private double heading;
	private int length;					// Kind of redundant now that Runway has Line2D for the runway itself.
	TYPE type;

	public Runway(double x, double y, double heading, TYPE type){
		loc = new Coords(x, y);
		this.heading = heading;
		this.type = type;
		length = (int)(2.5 * Runway.PPNM);
		
		//	calculate the runway
		double ex = loc.getX() + (length) * Math.sin(Math.toRadians(this.heading));
		double ey = loc.getY() - (length) * Math.cos(Math.toRadians(this.heading));
		runwayPath = new Line2D.Double(loc.getX(), loc.getY(), ex, ey);
		
		if(this.type == TYPE.RUNWAY_ARRIVE){
			localizer = new Localizer(this);
		}
		if(this.type == TYPE.RUNWAY_DEPART){
			// calculate the departure line
			ex = loc.getX() + (PPNM * 8) * Math.sin(Math.toRadians(this.heading));
			ey = loc.getY() - (PPNM * 8) * Math.cos(Math.toRadians(this.heading));
			departPath = new Line2D.Double(loc.getX(),loc.getY(),ex,ey);
		}
//		Game.registerWithHandler(this);
	}
	
	public void deselect(){
		
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	
	public Line2D getDepartPath(){
		return departPath;
	}
	
	public int getHdg(){
		return (int)this.heading;
	}
	
	public Localizer getLocalizer(){
		return localizer;
	}
	
	public Line2D getRunwayPath(){
		return runwayPath;
	}
	
	public void close(){
		isOpen = false;
		if(type == TYPE.RUNWAY_ARRIVE)
			Game.finalizeWithHandler(localizer);
		Game.finalizeWithHandler(this);
	}
	
	public void open(){
		isOpen = true;
		Game.registerWithHandler(this);
		if(type == TYPE.RUNWAY_ARRIVE)
			Game.registerWithHandler(localizer);
	}
	
	@Override
	public void render(Graphics g) {
		BasicStroke runway = new BasicStroke(3.0f);
		
		Graphics2D g2d = (Graphics2D) g;
		Color prev = g2d.getColor();
		BasicStroke ps = (BasicStroke)g2d.getStroke();
		
		g.setColor(Color.cyan);
		g2d.setStroke(runway);
		
		g2d.draw(runwayPath);
		
		g2d.setStroke(ps);
		if(type == TYPE.RUNWAY_DEPART){
			g2d.setColor(Color.magenta);
			g2d.drawLine((int)departPath.getX1(), (int)departPath.getY1(), (int)departPath.getX2(), (int)departPath.getY2());
		}
		g.setColor(prev);
	}
	
	public void select(){
		
	}
	
	public void setCoords(Coords coords){
		loc.setX(coords.getX());
		loc.setY(coords.getY());
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getArea() {
		return area;
	}

}
