package atc.lib;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import atc.Game;
import atc.type.TYPE;

public class Runway extends Entity{
	Localizer localizer;
	private double heading;
	private int length;
	TYPE type;

	public Runway(double x, double y, double heading, TYPE type){
		loc = new Coords(x, y);
		this.heading = heading;
		this.type = type;
		length = 32;
		if(this.type == TYPE.RUNWAY_ARRIVE){
			localizer = new Localizer(this);
		}
		Game.registerWithHandler(this);
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	public int getHdg(){
		return (int)this.heading;
	}
	
	@Override
	public void render(Graphics g) {
		BasicStroke runway = new BasicStroke(3.0f);
		
		Graphics2D g2d = (Graphics2D) g;
		Color prev = g2d.getColor();
		BasicStroke ps = (BasicStroke)g2d.getStroke();
		
		g.setColor(Color.cyan);
		g2d.setStroke(runway);		
		
		double ex = (loc.x + length * Math.sin(Math.toRadians(heading)));
		double ey = (loc.y + length * Math.cos(Math.toRadians(heading)));
		
		g2d.drawLine((int)loc.x, (int)loc.y, (int)ex, (int)ey);
		
		g2d.setStroke(ps);
		g.setColor(prev);
	}
	
	public void setCoords(Coords coords){
		loc.x = coords.getX();
		loc.y = coords.getY();
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

}
