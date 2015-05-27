package atc.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Random;
import java.util.Vector;

import atc.Game;
import atc.display.Draw;
import atc.type.FLIGHT;
import atc.type.SCORE;
import atc.type.TYPE;

public class Aircraft extends Entity{
	
	private static Random rand = new Random();
	
	private Boolean holdLeg = true;
	private Boolean isConflict = false;	//	Aircraft separation requirements are below threshold
	private Boolean isClearILS = false;	//	Aircraft has been cleared for ILS[landing]
	private Boolean isHolding = false;	//	Aircraft is in a holding pattern
	
	private double altCurrent;			//	Current altitude
	private double altDesired;			//	Assigned [desired] altitude
	private double climbCurrent = 0;	//	Current climb rate [Feet:Sweep]
	private double climbMax = 2.5;		//	Max climb rate in thousands [Feet:Minute]
	private double distTraveled = 0.0;	//	Used for flight history additions. 
	private double holdTraveled = 0.0;	//	Used for flight holding patterns
	private double rundTraveled = 0.0;	//	Used for takeoff calculating distance from runway
	
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

	private Fix fix = null;				//	Fix destination
	private Line2D direction;			//	Magical invisible line that extends in the direction the aircraft is flying.
	private Localizer local = null;		//	Localizer the aircraft is using for landing.
	private String airline;				//	Aircraft Operator
	@Deprecated
	private String model;				//	Model of aircraft
	private String flightNumber;		//	Flight number
	private String instruction;			//	Current instruction [if applicable]
	private Vector<Coords> history;		//	Flight history coordinates
	
	private FLIGHT flight;				//	Flight status
	
	//	Public Methods [alphabetical]
	
	public Aircraft(Coords coords, int hdg, int speed, FLIGHT flight){
		type = TYPE.AIRCRAFT;
		loc = coords;
		area = new Rectangle((int)(loc.getX() - 0.25 * PPNM), (int)(loc.getY() - 0.25 * PPNM), (int)((PPNM * 0.25) * 2),(int)((PPNM * 0.25) * 2) );
		history = new Vector<Coords>();
		if(flight == FLIGHT.HANDOFF_AR)
			altCurrent = 14;
		else
			altCurrent = 0;
		altDesired = altCurrent;
		headingCurrent = hdg;
		headingDesired = headingCurrent;
		kiasCurrent = speed;
		kiasDesired = kiasCurrent;
		tasCurrent = kiasToTas(kiasCurrent);
		direction = new Line2D.Double();
		double ex = (loc.getX() + Game.WIDTH * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - Game.WIDTH * Math.cos(Math.toRadians(headingCurrent)));
		direction.setLine(loc.getX(), loc.getY(), ex, ey);
		this.flight = flight;
		instruction = "";
	}
	
	public void contact(){
		if(flight == FLIGHT.HANDOFF_AR){
			flight = FLIGHT.ARRIVAL;
			Airport.getScoreArray()[SCORE.HANDOFF.getSID()]++;
			Airport.getScoreArray()[SCORE.PLYRSCORE.getSID()]+=5;
		}
		if(flight == FLIGHT.HANDOFF_DE){
			flight = FLIGHT.DEPARTURE;
			Airport.getScoreArray()[SCORE.HANDOFF.getSID()]++;
			Airport.getScoreArray()[SCORE.PLYRSCORE.getSID()]+=5;
		}
		else
			return;
	}

	public void deselect(){
		this.isSelected = false;
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
	
	public Coords getCoords(){
		return loc;
	}
	
	public Fix getFix(){
		return fix;
	}
	
	public FLIGHT getFlight(){
		return flight;
	}
	
	public String getFlightNo(){
		return flightNumber;
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
	
	public Vector<Coords> getHistory(){
		return history;
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
	
	public double getTTC(){
		double dist = Math.abs(altCurrent - altDesired);
		double rate = Math.abs(climbCurrent);	// climb rate per sweep
		return dist/(rate);
	}
	
	public boolean isConflict(){
		return isConflict;
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

	@Override
	public void render(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		Color prevC = g2d.getColor();
		Font prevF = g2d.getFont();
		double ex = (loc.getX() + (getKPS() * PPNM * 12) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - (getKPS() * PPNM * 12) * Math.cos(Math.toRadians(headingCurrent)));
		if(isSelected){
			if(isConflict){
				g2d.setColor(Color.red);
				Draw.history(g, history);
			}
			else{
				g2d.setColor(Color.yellow);
				Draw.history(g, history);
			}
			if(altCurrent != altDesired)
				Draw.cdend(g, this);
		}
			
		else{
			if(isConflict)
				g2d.setColor(Color.red);
			else
				g2d.setColor(Color.green);
		}
			
		Draw.centeredsquare(g, loc, PPNM * 0.25, Color.green, 1.0f);
		Draw.centeredcircle(g, loc, 1*PPNM, g2d.getColor());
		g2d.drawLine((int)loc.getX(), (int)loc.getY(), (int)ex, (int)ey);
		Draw.flightinfo(g, this);
		if(MouseInput.mouseCoords()[0] != null && MouseInput.mouseCoords()[1] != null && isSelected){
			Coords src = MouseInput.mouseCoords()[0];
			Coords tgt = MouseInput.mouseCoords()[1];
			Draw.line(g, src, tgt, Color.yellow);
			if(fix != null){
				Draw.centeredcircle(g, fix.getCoords(), 0.75 * PPNM, Color.yellow);
				Draw.line(g, loc, fix.getCoords(), Color.yellow);
			}
			g2d.drawString((int)Calc.relativeBearing(src, tgt)+"", (int)tgt.getX(), (int)tgt.getY());
		}
		g2d.setColor(prevC);
		g2d.setFont(prevF);	
	}
	
	public void select(){
		this.isSelected = true;
	}
	
	public void setAltitudeDesired(double alt){
		altDesired = alt;
	}
	
	public void setConflict(Boolean conflict){
		isConflict = conflict;
	}

	public void setCoords(Coords coords){
		loc.setX(coords.getX());
		loc.setY(coords.getY());
	}
	
	public void setFix(Fix fix){
//		System.out.println("Set Fix!");
		this.fix = fix;
		if(fix != null)
			instruction = fix.getID();
		else
			instruction = "";
	}
	
	public void setFixHdg(int hdg){
		if(fix == null) return;
//		System.out.println("Set Fix Heading!");
		if(hdg >= 0){
			fixHeading = hdg;
			instruction = fix.getID() + fixHeading;
		}
	}
	
	public void setFlight(FLIGHT flight){
		this.flight = flight;
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
	}
	
	//	Private Methods [alphabetical]
	
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
						if(local == null){
							local = l;
							System.out.println("Found localizer " + local);
							setInstruction(local.getID());
						}
						else{
							if(Calc.distanceNM(loc, l.getCoords()) < Calc.distanceNM(loc, local.getCoords())){
								local = l;
								System.out.println("Found closer localizer " + local);
								setInstruction(local.getID());
							}
						}

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
			if(angle >= 135 && Calc.distanceNM(loc, ci) <= 0.75){		// 0.5 might be too finnicky.
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

	private void flyHold(){
		if(fix != null)
			return;
		else{
			holdTraveled += getKPS();
			if(holdLeg){
				if(holdTraveled >= 6){
					int h = headingCurrent + 90;
					if(h >= 360) h-=360;
					headingDesired = h;
					holdTraveled = 0;
					holdLeg = false;
				}
			}
			else{
				if(holdTraveled >= 3){
					int h = headingCurrent + 90;
					if(h >= 360) h-=360;
					headingDesired = h;
					holdTraveled = 0;
					holdLeg = true;
				}
			}
		}
	}
	
	private double getFPS(){
		return climbCurrent / Game.sweepsPerMin;
	}
	
	public double getKPS(){
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
//		altitude();
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
//		heading();
	}
	
	private void ilsSpd(){
		if(local == null)
			return;
		double drt = Calc.distanceNM(loc, local.getCoords());	// Distance Runway Threshold
		if(drt >= 0) kiasDesired = 135;
		if(drt >= 3) kiasDesired = 140;
		if(drt >= 4) kiasDesired = 150;
	}

	private int kiasToTas(int kias){
		return (int)(kias + (0.02 * kias * altCurrent));
	}
	
	private void move(){
		switch(flight){
		case ARRIVAL:
			if(isClearILS)
				chkILS();
			if(fix != null){
				setHeadingDesired(fix.getCoords());
				if(Calc.distanceNM(loc, fix.getCoords()) <= 0.5 && fixHeading >= 0){
					setHeadingDesired(fixHeading);
					fix = null;
					fixHeading = -1;
					instruction = "";
				}
			}
			if(isHolding)
				flyHold();
			break;
		case CRUISE:
			break;
		case DEPARTURE:
			if(fix != null){
				setHeadingDesired(fix.getCoords());
				if(Calc.distanceNM(loc, fix.getCoords()) <= 0.5 && fixHeading >= 0){
					setHeadingDesired(fixHeading);
					fix = null;
					fixHeading = -1;
					instruction = "";
				}
			}
			if(altCurrent >= 13){
				flight = FLIGHT.CRUISE;
				kiasDesired = 260;
				altDesired = rand.nextInt(13) + 30;
				if(fix != null){
					Airport.getScoreArray()[SCORE.HANDOFF.getSID()]++;
					Airport.getScoreArray()[SCORE.PLYRSKILL.getSID()]++;
					Airport.getScoreArray()[SCORE.PLYRSCORE.getSID()]+=10;
				}
				else{
					Airport.getScoreArray()[SCORE.PLYRSCORE.getSID()]+=5;
				}
				Airport.getScoreArray()[SCORE.CRUISEALT.getSID()]++;
			}
			break;
		case HANDOFF_AR:
//			blink();
			break;
		case HANDOFF_DE:
//			blink();
			break;
		case ILS:
			ilsAlt();
			ilsHdg();
			ilsSpd();
			if(altCurrent <= 0.1){
				flight = FLIGHT.LANDING;
			}
			break;
		case LANDING:
			headingCurrent = local.getHdg();
			kiasDesired = 0;
			altDesired = 0;
			break;
		case TAKEOFF:
			if(rundTraveled >= 10){
				headingDesired = (int)Calc.relativeBearing(loc, fix.getCoords());
				flight = FLIGHT.HANDOFF_DE;
			}
			if(altCurrent == 0){
				kiasDesired = 150;
			}
			else{
				kiasDesired = 180;
			}
			if(kiasCurrent < 150){
				altDesired = 0;
			}
			else{
				altDesired = 4;
			}
			rundTraveled += getKPS();
			break;
		default:
			break;
		}
		altitude();
		heading();
		throttle();
		double ex = (loc.getX() + (getKPS() * PPNM) * Math.sin(Math.toRadians(headingCurrent)));
		double ey = (loc.getY() - (getKPS() * PPNM) * Math.cos(Math.toRadians(headingCurrent)));
		loc.setX(ex);
		loc.setY(ey);
		distTraveled += getKPS();
		updateHistory();
	}

	private void setTurnRate(){
		if(headingCurrent != headingDesired){
			if(Math.abs(headingCurrent - headingDesired) > turnRateMax * Game.sweepLength)
				turnRateCur = turnRateMax * Game.sweepLength;
			else
				turnRateCur = Math.abs(headingCurrent - headingDesired);//  Game.sweepLength;
//			System.out.println(headingCurrent + ", " + headingDesired + ", " + turnRateCur);
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
				accelCur = -12;
		}
		if(flight == FLIGHT.TAKEOFF){
			if(kiasDesired - kiasCurrent >= 12)
				accelCur = 12;
			else
				accelCur = kiasDesired - kiasCurrent;
		}
		kiasCurrent += accelCur;
		if(kiasCurrent < 0)
			kiasCurrent = 0;
	}

	private void updateHistory(){
		if(distTraveled >= 0.50){
			Coords prev = new Coords(loc.getX(), loc.getY());
			history.add(0, prev);
			if(history.size() == 20)
				history.remove(19);
			distTraveled = 0.0;
		}
	}

}
