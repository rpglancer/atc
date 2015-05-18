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
	private int headingCurrent;
	private int headingDesired;
	private int turnRateCur = 0;
	private int turnRateMax;
	private double altCurrent;
	private double altDesired;
	private int maxAccel = 5;
	private int maxDecel = -3;
	private int accelCur = 0;
	private int kiasCurrent;		//	Knots indicated air speed
	private int kiasDesired;		//	Knots indicated air speed
	private int tasCurrent;			//	Knots ground
	private int tasDesired;			//	Knots ground
	
	private double distTraveled = 0.0;

	public Aircraft(double x, double y, int hdg, int speed, TYPE type){
		loc = new Coords(x,y);
		altCurrent = 5.5;
		altDesired = altCurrent;
		headingCurrent = hdg;
		headingDesired = headingCurrent;
		kiasCurrent = speed;
		kiasDesired = kiasCurrent;
		tasCurrent = kiasToTas(kiasCurrent);
		tasDesired = tasCurrent;
		turnRateMax = 3;
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
	
	public int getHdgCur(){
		return headingCurrent;
	}
	
	public int getHdgDes(){
		return headingDesired;
	}
	
	public int getKIAS(){
		return kiasCurrent;
	}
	
	public int getTAS(){
		return tasCurrent;
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public void setAltitudeDesired(double alt){
		altDesired = alt;
	}
	
	public void setHeadingDesired(Coords coords){
	    double angle = (double) Math.toDegrees(Math.atan2(coords.getY() - loc.getY(), coords.getX() - loc.getX()));

	    if(angle < 0){
	        angle += 360;
	    }
	    
	    angle+=90;
	    if(angle >=360)
	    	angle -= 360;
	    
	    System.out.println("Desired Heading: " + (int)angle);
	    this.headingDesired = (int)angle;
	}

	public void setHeadingDesired(int hdg){
		headingDesired = hdg;
	}
	
	public void setVelocityDesired(int kts){
		kiasDesired = kts;
	}
	
	private void throttle(){
		tasCurrent = kiasToTas(kiasCurrent);
		if(kiasDesired > kiasCurrent){
			if(kiasDesired - kiasCurrent >= maxAccel){
				accelCur = maxAccel;
			}
			else{
				accelCur = kiasDesired - kiasCurrent;
			}
		}
		else if(kiasDesired < kiasCurrent){
			if(kiasCurrent - kiasDesired <= maxDecel){
				accelCur = maxDecel;
			}
			else{
				accelCur = kiasCurrent - kiasDesired;
			}
		}
		else{
			accelCur = 0;
		}
		kiasCurrent += accelCur;
	}
	
	public void deselect(){
		this.isSelected = false;
	}
	
	private void move(){
		throttle();
		setTurnRate();
		turn();
		double ex = (loc.getX() + (getKPS() * NMPP) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - (getKPS() * NMPP) * Math.cos(Math.toRadians(headingCurrent)));
		loc.setX(ex);
		loc.setY(ey);
		distTraveled += getKPS();
		if(distTraveled >= 0.25){
			Coords prev = new Coords(loc.getX(), loc.getY());
			history.add(0, prev);
			if(history.size() == 10)
				history.remove(9);
			distTraveled = 0.0;
		}

	}
	
	private void turn(){
		if(turnRateCur == 0)
			return;
		if(headingDesired - headingCurrent >=0){
			headingCurrent += turnRateCur;
			if(headingCurrent >= 360)
				headingCurrent -= 360;
		}	
		else{
			headingCurrent -= turnRateCur;
			if(headingCurrent <=0)
				headingCurrent += 360;
		}
	}
	
	public void select(){
		this.isSelected = true;
	}
	
	private void setTurnRate(){
		if(Math.abs(headingCurrent - headingDesired) >= turnRateMax * Game.sweepLength)
			turnRateCur = turnRateMax * Game.sweepLength;
		else
			turnRateCur = Math.abs(headingCurrent - headingDesired);//  Game.sweepLength;
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
		if(isSelected)
			g2d.setColor(Color.yellow);
		else
			g2d.setColor(Color.green);
		g2d.drawString(Fonts.flight, (int)loc.getX() - (w/2), (int)loc.getY() + (h/2));
		Draw.centeredcircle(g, this.getCoords(), 0.5*NMPP, Color.cyan);
		double ex = (loc.getX() + (getKPS() * NMPP * 12) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - (getKPS() * NMPP * 12) * Math.cos(Math.toRadians(headingCurrent)));
		g2d.drawLine((int)loc.getX(), (int)loc.getY(), (int)ex, (int)ey);
		g2d.setColor(prevC);
		g2d.setFont(prevF);
		Draw.flightinfo(g, this);
//		if(isSelected()){
//			Draw.hud(g, this);
//		}
	}
	
	public void setCoords(Coords coords){
//		loc.x = coords.getX();
		loc.setX(coords.getX());
		loc.setY(coords.getY());
//		loc.y = coords.getY();
	}

	@Override
	public void tick() {
		move();
	}

}
