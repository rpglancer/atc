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
import atc.type.FLIGHT;
import atc.type.TYPE;

public class Aircraft extends Entity{
	
	private Boolean isConflict = false;
	private double altCurrent;		//	Current altitude
	private double altDesired;		//	Assigned [desired] altitude
	private double climbCurrent = 0;//	Current climb rate
	private double climbMax = 2.5;	//	Max climb rate in thousands [Feet:Minute]
	private double distTraveled = 0.0;
	
	private int accelCur = 0;		//	Current acceleration
	private int accelMax = 5;		//	Max acceleration
	private int decelMax = -3;		//	Max deceleration
	private int headingCurrent;		//	Current heading
	private int headingDesired;		//	Assigned [desired] heading
	private int kiasCurrent;		//	Knots indicated air speed
	private int kiasDesired;		//	Knots indicated air speed
	private int tasCurrent;			//	Knots ground [true air speed]
	private int turnRateCur = 0;	//	Current turn rate
	private int turnRateMax;		//	Maximum turn rate

	private Fix toFix = null;		//	Fix destination
	private String airline;			//	Aircraft Operator
	private String flightNumber;	//	Flight number
	private Vector<Coords> history = new Vector<Coords>();
	
	private FLIGHT flight;			//	Flight status
	
	public Aircraft(double x, double y, int hdg, int speed, TYPE type){
		loc = new Coords(x,y);
		altCurrent = 16;
		altDesired = altCurrent;
		headingCurrent = hdg;
		headingDesired = headingCurrent;
		kiasCurrent = speed;
		kiasDesired = kiasCurrent;
		tasCurrent = kiasToTas(kiasCurrent);
		turnRateMax = 3;
		this.type = type;
		flight = FLIGHT.HANDOFF;
	}

	public void deselect(){
		this.isSelected = false;
	}
	
	public Coords getCoords(){
		return loc;
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
	
	public int getKIASDes(){
		return kiasDesired;
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	private int kiasToTas(int kias){
		return (int)(kias + (0.02 * kias * altCurrent));
	}
	
	private double getFPS(){
		double climb = climbCurrent;
		climb /= 60;
		return climb * Game.sweepLength;
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
	
	public int getTAS(){
		return tasCurrent;
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color prevC = g2d.getColor();
		Font prevF = g2d.getFont();
		if(isSelected){
			Draw.history(g, history);
			g2d.setColor(Color.yellow);
		}
		else{
			g2d.setColor(Color.green);
		}
		Draw.centeredsquare(g, loc, NMPP * 0.25, g2d.getColor(), 2.0f);
		Draw.centeredcircle(g, loc, 1*NMPP, Color.cyan);
		double ex = (loc.getX() + (getKPS() * NMPP * 12) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - (getKPS() * NMPP * 12) * Math.cos(Math.toRadians(headingCurrent)));
		g2d.drawLine((int)loc.getX(), (int)loc.getY(), (int)ex, (int)ey);
		g2d.setColor(prevC);
		g2d.setFont(prevF);
		Draw.flightinfo(g, this);
	}
	
	public void select(){
		this.isSelected = true;
	}
	
	public void setAltitudeDesired(double alt){
		altDesired = alt;
	}

	public void setCoords(Coords coords){
		loc.setX(coords.getX());
		loc.setY(coords.getY());
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
	
	@Override
	public void tick() {
		move();
	}
	
	private void altitude(){
		if(altDesired > altCurrent){
			if(altDesired - altCurrent >= 2.5){
				climbCurrent = climbMax;
			}
			else{
				climbCurrent = altDesired - altCurrent;
			}
		}
		else if(altDesired < altCurrent){
			if(altDesired - altCurrent <= -2.5){
				climbCurrent = -climbMax;
			}
			else{
				climbCurrent = altDesired - altCurrent;
			}
		}
		else{
			climbCurrent = 0;
		}
		altCurrent += getFPS();
		if(altCurrent >= altDesired - 0.1 && altCurrent <= altDesired + 0.1)
			altCurrent = altDesired;
	}

	private void heading(){
		setTurnRate();
		if(turnRateCur == 0)
			return;
		int recipOne = 360 - Convert.reciprocal(headingCurrent) + headingDesired;
		int recipTwo = 0 + Convert.reciprocal(headingCurrent) - headingDesired;
		if(recipOne >= 360)
			recipOne -= 360;
		if(recipOne < 0)
			recipOne += 360;
		if(recipTwo >= 360)
			recipTwo -= 360;
		if(recipTwo < 0)
			recipTwo += 360;
		// left
		if(recipOne <= recipTwo){
			headingCurrent -= turnRateCur;
			if(headingCurrent < 0)
				headingCurrent += 360;
		}
		// right
		else{
			headingCurrent += turnRateCur;
			if(headingCurrent >= 360)
				headingCurrent -= 360;
		}
	}
	
	private void move(){
		altitude();
		heading();
		throttle();
		
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

	private void setTurnRate(){
		if(headingCurrent != headingDesired){
			if(Math.abs(headingCurrent - headingDesired) > turnRateMax * Game.sweepLength)
				turnRateCur = turnRateMax * Game.sweepLength;
			else
				turnRateCur = Math.abs(headingCurrent - headingDesired);//  Game.sweepLength;
			System.out.println(headingCurrent + ", " + headingDesired + ", " + turnRateCur);
		}
		else turnRateCur = 0;
	}
	
	private void throttle(){
		tasCurrent = kiasToTas(kiasCurrent);
		if(kiasDesired > kiasCurrent){
			if(kiasDesired - kiasCurrent >= accelMax){
				accelCur = accelMax;
			}
			else{
				accelCur = kiasDesired - kiasCurrent;
			}
		}
		else if(kiasDesired < kiasCurrent){
			if(kiasDesired - kiasCurrent <= decelMax){
				accelCur = decelMax;
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

}
