package atc.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Vector;

import atc.Game;
import atc.display.Draw;
import atc.type.FLIGHT;
import atc.type.TYPE;

public class Aircraft extends Entity{
	
	private Boolean isConflict = false;	//	Aircraft separation requirements are below threshold
	private Boolean isCrashing = false;	//	Aircraft has collided with another aircraft
	private Boolean isClearILS = false;	//	Aircraft has been cleared for ILS[landing]
	private Boolean isHolding = false;	//	Aircraft is in a holding pattern
	private double altCurrent;			//	Current altitude
	private double altDesired;			//	Assigned [desired] altitude
	private double climbCurrent = 0;	//	Current climb rate
	private double climbMax = 2.5;		//	Max climb rate in thousands [Feet:Minute]
	private double distTraveled = 0.0;	//	Used for flight history additions. 
	private double holdTraveled = 0.0;	//	Used for flight holding patterns
	
	private int accelCur = 0;			//	Current acceleration
	private int accelMax = 5;			//	Max acceleration
	private int decelMax = -3;			//	Max deceleration
	private int fixHeading;				//	Heading to fly once a fix is reached [if applicable]
	private int headingCurrent;			//	Current heading
	private int headingDesired;			//	Assigned [desired] heading
	private int kiasCurrent;			//	Knots indicated air speed
	private int kiasDesired;			//	Knots indicated air speed
	private int tasCurrent;				//	Knots ground [true air speed]
	private int turnRateCur = 0;		//	Current turn rate
	private int turnRateMax = 3;		//	Maximum turn rate

	private Fix fix = null;			//	Fix destination
	private Line2D direction;			//	Magical invisible line that extends in the direction the aircraft is flying.
	private Localizer local = null;		//	Localizer the aircraft is using for landing.
	private String airline;				//	Aircraft Operator
	private String model;				//	Model of aircraft
	private String flightNumber;		//	Flight number
	private String instruction;			//	Current instruction [if applicable]
	private Vector<Coords> history = new Vector<Coords>();	//	Flight history coordinates
	
	private FLIGHT flight;			//	Flight status
	
	public Aircraft(double x, double y, int hdg, int speed, TYPE type){
		loc = new Coords(x,y);
		area = new Rectangle((int)(loc.getX() - 0.25 * PPNM), (int)(loc.getY() - 0.25 * PPNM), (int)((PPNM * 0.25) * 2),(int)((PPNM * 0.25) * 2) );
		altCurrent = 4;
		altDesired = altCurrent;
		headingCurrent = hdg;
		headingDesired = headingCurrent;
		kiasCurrent = speed;
		kiasDesired = kiasCurrent;
		tasCurrent = kiasToTas(kiasCurrent);
		this.type = type;
		direction = new Line2D.Double();
		double ex = (loc.getX() + Game.WIDTH * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - Game.WIDTH * Math.cos(Math.toRadians(headingCurrent)));
		direction.setLine(loc.getX(), loc.getY(), ex, ey);
		flight = FLIGHT.HANDOFF_AR;
		instruction = "";
	}
	
	public void contact(){
		if(flight == FLIGHT.HANDOFF_AR)
			flight = FLIGHT.ARRIVAL;
		if(flight == FLIGHT.HANDOFF_DE)
			flight = FLIGHT.DEPARTURE;
		else
			return;
	}

	public void deselect(){
		this.isSelected = false;
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	public FLIGHT getFlight(){
		return flight;
	}
	
	public int getHdgCur(){
		return headingCurrent;
	}
	
	public int getHdgDes(){
		return headingDesired;
	}
	
	public String getInstruction(){
		return instruction;
	}
	
	public int getKIAS(){
		return kiasCurrent;
	}
	
	public int getKIASDes(){
		return kiasDesired;
	}
	
	public boolean isClearILS(){
		return isClearILS;
	}
	
	public boolean isHolding(){
		return isHolding;
	}
	
	public boolean isSelected(){
		return isSelected;
	}

	public double getAltCur(){
		return altCurrent;
	}
	
	public double getAltDes(){
		return altDesired;
	}
	
	@Override
	public Rectangle getArea() {
		return area;
	}
	
	public String getFlightNo(){
		return flightNumber;
	}
	
	public String getModel(){
		return model;
	}
	
	public String getName(){
		return airline+flightNumber;
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
			if(altCurrent != altDesired){
				double ex = (loc.getX() + (getKPS()) * PPNM * (getTTC() * 12) * Math.sin(Math.toRadians(headingCurrent)) );
				double ey = (loc.getY() - (getKPS()) * PPNM * (getTTC() * 12) * Math.cos(Math.toRadians(headingCurrent)) );	
				Draw.centeredcircle(g, new Coords(ex,ey), 0.5*PPNM, Color.magenta);
			}
		}
		else{
//			if(flight == FLIGHT.CRUISE) g2d.setColor(Color.darkGray);
			g2d.setColor(Color.green);
		}
		Draw.centeredsquare(g, loc, PPNM * 0.25, g2d.getColor(), 2.0f);
		Draw.centeredcircle(g, loc, 1*PPNM, Color.cyan);
		double ex = (loc.getX() + (getKPS() * PPNM * 12) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - (getKPS() * PPNM * 12) * Math.cos(Math.toRadians(headingCurrent)));
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
	
	public void setFlightInfo(String oper, String number, String model){
		airline = oper;
		flightNumber = number;
		this.model = model;
	}
	
	public void setHeadingDesired(Coords coords){
		headingDesired = (int)Calc.relativeBearing(loc, coords);
	}

	public void setHeadingDesired(int hdg){
		headingDesired = hdg;
	}
	
	public void setInstruction(String instruction){
		this.instruction = instruction;
	}
	
	public void setVelocityDesired(int kts){
		kiasDesired = kts;
	}
	
	public void toggleHold(){
		if(isHolding)
			isHolding = false;
		else
			isHolding = true;
	}
	
	public void toggleILS(){
		if(isClearILS)
			isClearILS = false;
		else
			isClearILS = true;
	}
	
	@Override
	public void tick() {
		move();
		area.setBounds((int)(loc.getX() - 0.25 * PPNM), (int)(loc.getY() - 0.25 * PPNM), (int)((PPNM * 0.25) * 2),(int)((PPNM * 0.25) * 2) );
		double ex = (loc.getX() + Game.WIDTH * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - Game.WIDTH * Math.cos(Math.toRadians(headingCurrent)));
		direction.setLine(loc.getX(), loc.getY(), ex, ey);
/*
		if(flight == FLIGHT.ARRIVAL){
			if(isClearILS && local == null){
				for(int i = 0; i < Handler.getLocalizers().size(); i++){
					Localizer l = Handler.getLocalizers().elementAt(i);
					if(Calc.lineIntcpt(direction, l.getLocPath())){
						Coords ci = Calc.intersection(direction, l.getLocPath());
						if(Calc.distance(loc, ci) < 2 * PPNM){
							local = l;
							System.out.println("Found localizer " + local);
							setInstruction(local.getID());
							break;
						}
					}
				}
			}
			if(local != null){
				Coords ci = Calc.intersection(direction, local.getLocPath());
				if(ci == null){
					local = null;
					System.out.println("Lost the localizer!");
					setInstruction("");
					flight = FLIGHT.ARRIVAL;
					return;
				}
				double angle = Math.toDegrees(Calc.approachAngle(local.getCoords(), ci, loc));
				if(angle >= 135 && Calc.distanceNM(loc, ci) <= 0.5){		// 0.5 might be too finnicky.
					// Check for altitude here.
					Coords intcpt = Calc.intersection(direction, local.getLocPath());
					if(intcpt == null)
						return;
					if(Calc.distanceNM(intcpt, local.getCoords()) >= 15 && altCurrent <= 4.0)
						flight = FLIGHT.ILS;
					else if(Calc.distanceNM(intcpt, local.getCoords()) >= 11.25 && altCurrent <= 3.0)
						flight = FLIGHT.ILS;
					else if(Calc.distance(intcpt, local.getCoords()) >= 7.5 && altCurrent <= 2.0)
						flight = FLIGHT.ILS;
					if(flight == FLIGHT.ILS)
						System.out.println("Established on the localizer!");
				}
			}
		}
		*/
	}
	
	private void altitude(){
		if(altCurrent == altDesired)
			return;
		if(altDesired > altCurrent){
			climbCurrent = climbMax;
			altCurrent += getFPS();
			if(altCurrent > altDesired){
				altCurrent = altDesired;
				climbCurrent = 0;
			}
		}
		else{
			climbCurrent = -climbMax;
			altCurrent += getFPS();
			if(altCurrent < altDesired){
				altCurrent = altDesired;
				climbCurrent = 0;
			}
		}	
	}

	private void chkILS(){
		if(local == null){
			//orig
			for(int i = 0; i < Handler.getLocalizers().size(); i++){
				Localizer l = Handler.getLocalizers().elementAt(i);
				if(Calc.lineIntcpt(direction, l.getLocPath())){
					Coords ci = Calc.intersection(direction, l.getLocPath());
					if(Calc.distance(loc, ci) < 2 * PPNM){
						local = l;
						System.out.println("Found localizer " + local);
						setInstruction(local.getID());
						break;
					}
				}
			}
		}
		if(local != null){
			Coords ci = Calc.intersection(direction, local.getLocPath());
			if(ci == null){
				local = null;
				System.out.println("Lost the localizer!");
				setInstruction("");
				flight = FLIGHT.ARRIVAL;
				return;
			}
			double angle = Math.toDegrees(Calc.approachAngle(local.getCoords(), ci, loc));
			if(angle >= 135 && Calc.distanceNM(loc, ci) <= 0.5){		// 0.5 might be too finnicky.
				// Check for altitude here.
				Coords intcpt = Calc.intersection(direction, local.getLocPath());
				if(intcpt == null)
					return;
				if(Calc.distanceNM(intcpt, local.getCoords()) >= 15 && altCurrent <= 4.0)
					flight = FLIGHT.ILS;
				else if(Calc.distanceNM(intcpt, local.getCoords()) >= 11.25 && altCurrent <= 3.0)
					flight = FLIGHT.ILS;
				else if(Calc.distance(intcpt, local.getCoords()) >= 7.5 && altCurrent <= 2.0)
					flight = FLIGHT.ILS;
				if(flight == FLIGHT.ILS){
					System.out.println("Established on the localizer!");
				}
					
			}
		}
	}
	
	private double getTTC(){
		double dist = Math.abs(altCurrent - altDesired);
		double rate = Math.abs(climbCurrent);	// climb rate per sweep
		return dist/(rate);
	}

	private double getFPS(){
		return climbCurrent / Game.sweepsPerMin;
	}
	
	private double getKPS(){
		double vel = tasCurrent;
		vel /= 60;
		vel /= 60;
		return vel * Game.sweepLength;
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
	
	private void ilsAlt(){
		if(altCurrent > Calc.distanceNM(loc, local.getCoords()) / 3.75)
			altDesired = Calc.distanceNM(loc, local.getCoords()) / 3.75;		//	TODO: Should use a var for 3.75 to have different glide paths...
		altitude();
	}
	
	private void ilsHdg(){
		int maxHdgAdjust = 1;
		if((int)Calc.relativeBearing(loc, local.getCoords()) > local.getHdg()){
			turnRateCur = maxHdgAdjust * Game.sweepLength;
			headingDesired = local.getHdg() + 5;
			if(headingDesired > 359)
				headingDesired -= 360;
		}
		else if((int)Calc.relativeBearing(loc, local.getCoords()) < local.getHdg()){
			turnRateCur = maxHdgAdjust * Game.sweepLength;
			headingDesired = local.getHdg() - 5;
			if(headingDesired < 0)
				headingDesired +=360;
		}
		else{
			turnRateCur = 0;
			headingDesired = local.getHdg();
		}
		heading();
	}
	
	private void ilsSpd(){
		
	}

	private int kiasToTas(int kias){
		return (int)(kias + (0.02 * kias * altCurrent));
	}
	
	private void move(){
		if(isClearILS && flight == FLIGHT.ARRIVAL){
			chkILS();
		}
		if(flight == FLIGHT.ILS){
			ilsAlt();
			ilsHdg();
			ilsSpd();
			if(altCurrent < 0.1){
				flight = FLIGHT.LANDING;
			}
		}
		else if(flight == FLIGHT.LANDING){
			headingCurrent = local.getHdg();
			kiasDesired = 0;
			altDesired = 0;
			altitude();
			throttle();
		}
		else{
			altitude();
			heading();
			throttle();
		}
		double ex = (loc.getX() + (getKPS() * PPNM) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - (getKPS() * PPNM) * Math.cos(Math.toRadians(headingCurrent)));
		loc.setX(ex);
		loc.setY(ey);
		distTraveled += getKPS();
		if(distTraveled >= 0.50){
			Coords prev = new Coords(loc.getX(), loc.getY());
			history.add(0, prev);
			if(history.size() == 20)
				history.remove(19);
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
				accelCur = kiasDesired - kiasCurrent;
			}
		}
		else{
			accelCur = 0;
		}
		if(flight == FLIGHT.LANDING){
			if(altCurrent == 0)
				accelCur = -15;
		}
		kiasCurrent += accelCur;
		if(kiasCurrent < 0)
			kiasCurrent = 0;
	}
}
