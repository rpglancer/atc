package atc.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import atc.Game;
import atc.display.Draw;
import atc.display.Fonts;
import atc.type.TYPE;

public class Aircraft extends Entity{
	private String airline;
	private Vector<Coords> history = new Vector<Coords>();
	private String flightNumber;
	private double headingCurrent;
	private double headingDesired;
	private double turnRateCur = 0;
	private double turnRateMax;
	private double altCurrent;
	private double altDesired;
	private int kiasCurrent;		//	Knots indicated air speed
	private int kiasDesired;		//	Knots indicated air speed
	private int tasCurrent;			//	Knots ground
	private int tasDesired;			//	Knots ground
	
	private double distTraveled = 0.0;

	public Aircraft(double x, double y, double hdg, int speed, TYPE type){
		loc = new Coords(x,y);
		altCurrent = 5.5;
		altDesired = altCurrent;
		headingCurrent = hdg;
		headingDesired = headingCurrent;
		kiasCurrent = speed;
		kiasDesired = kiasCurrent;
		tasCurrent = kiasToTas(kiasCurrent);
		tasDesired = tasCurrent;
		this.type = type;
	}
	
	private int kiasToTas(int kias){
		return (int)(kias + (0.02 * kias * altCurrent));
	}
	
	private double getKPS(){
		double vel = tasCurrent;
		vel /= 60;
		vel /= 60;
		return vel * Game.sweepLength;
	}
	
	public double getAltCur(){
		return altCurrent;
	}
	
	public double getAltDes(){
		return altDesired;
	}
	
	public int getKIAS(){
		return kiasCurrent;
	}
	
	public int getTAS(){
		return tasCurrent;
	}
	
	public void setVelocityDesired(int kts){
		kiasDesired = kts;
	}
	
	private void move(){
		double ex = (loc.x + (getKPS() * NMPP) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.y - (getKPS() * NMPP) * Math.cos(Math.toRadians(headingCurrent)));
		loc.setX(ex);
		loc.setY(ey);
		distTraveled += getKPS();
		if(distTraveled >= 0.25){
			Coords prev = new Coords(loc.getX(), loc.getY());
//			history.add(prev);
			history.add(0, prev);
			if(history.size() == 10)
				history.remove(9);
			distTraveled = 0.0;
		}

	}
	
	private void setTurnRate(){
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color prevC = g2d.getColor();
		Font prevF = g2d.getFont();
		g.setColor(Color.yellow);
		for(int i = 0; i < history.size(); i++){
			Coords temp = history.elementAt(i);
//			g.setColor(Color.yellow);
			double x = temp.getX();
			double y = temp.getY();
			int w = 3;
			int h = 3;
			g.setColor(g.getColor().darker());
			g2d.drawOval((int)x-2, (int)y-2, w, h);
		}
		FontMetrics fm = g2d.getFontMetrics(Fonts.radarobj);
		int w = fm.stringWidth(Fonts.flight);
		int h = fm.getAscent() + fm.getDescent();
		g2d.setFont(Fonts.radarobj);
		g2d.setColor(Color.green);
		g2d.drawString(Fonts.flight, (int)loc.x - (w/2), (int)loc.y + (h/2));
		double ex = (loc.x + (getKPS() * NMPP * 12) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.y - (getKPS() * NMPP * 12) * Math.cos(Math.toRadians(headingCurrent)));
		g2d.drawLine((int)loc.x, (int)loc.y, (int)ex, (int)ey);
		g2d.setColor(prevC);
		g2d.setFont(prevF);
		Draw.flightinfo(g, this);
	}
	
	public void setCoords(Coords coords){
		loc.x = coords.getX();
		loc.y = coords.getY();
	}

	@Override
	public void tick() {
		move();
	}

}
