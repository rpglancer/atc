package atc.lib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import atc.Game;
import atc.type.TYPE;

public class Aircraft extends Entity{
	private String airline;
	private Vector<Coords> history = new Vector<Coords>();
	private String flightNumber;
	private double headingCurrent;
	private double headingDesired;
	private double turnRateCur = 0;
	private double turnRateMax;
	private int velocityCurrent;
	private int velocityDesired;
	
	private double distTraveled = 0.0;

	public Aircraft(double x, double y, double hdg, int speed, TYPE type){
		loc = new Coords(x,y);
		headingCurrent = hdg;
		headingDesired = headingCurrent;
		velocityCurrent = speed;
		velocityDesired = velocityCurrent;
		this.type = type;
	}
	
	private double getKPS(){
		double vel = velocityCurrent;
		vel /= 60;
		vel /= 60;
		return vel * Game.sweepLength;
	}
	
	public void setVelocityDesired(int kts){
		velocityDesired = kts;
	}
	
	private void move(){
		double ex = (loc.x + (getKPS() * NMPP) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.y - (getKPS() * NMPP) * Math.cos(Math.toRadians(headingCurrent)));
		loc.setX(ex);
		loc.setY(ey);
		distTraveled += getKPS();
		if(distTraveled >= 0.25){
			Coords prev = new Coords(loc.getX(), loc.getY());
			history.add(prev);
			distTraveled = 0.0;
		}

	}
	
	private void setTurnRate(){
	}
	
	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color prev = g2d.getColor();
		for(int i = 0; i < history.size(); i++){
			Coords temp = history.elementAt(i);
			g.setColor(Color.yellow);
			double x = temp.getX();
			double y = temp.getY();
			int w = 3;
			int h = 3;
			g2d.drawOval((int)x-2, (int)y-2, w, h);
		}
		int w = 5;
		int h = 5;
		g2d.setColor(Color.green);
		g2d.drawOval((int)loc.x-2, (int)loc.y-2, w, h);
		g2d.setColor(prev);
	}

	@Override
	public void tick() {
		move();
	}

}
