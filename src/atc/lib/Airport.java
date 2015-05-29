package atc.lib;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Vector;

import atc.Game;
import atc.display.Draw;
import atc.display.Text;
import atc.type.FLIGHT;
import atc.type.LAYOUT;
import atc.type.SCORE;
import atc.type.TYPE;

/**
 * Airport is essentially in charge of managing the air space of which the player 
 * is in control. It is responsible for spawning arrivals and departures, 
 * removing aircraft that have landed or wandered out of controled space, 
 * checking for collisions, evaluating separation requirements, opening or closing runways, 
 * and tracking such statistics as are involved in determining the skill so as
 * to adjust the player load [difficulty] accordingly.
 * @author Matt Bangert
 *
 */
public class Airport extends Entity{

	private Random rand = new Random();
	private Vector<Aircraft> aircraft;
	private Vector<Aircraft> delivery;
	private Vector<Fix> fixes;
	private Vector<Rectangle> arrivalSectors;	//	Sectors [rectangles] inside which arrivals can spawn
	private Vector<Runway> availRunways;		//	All runways, only used for checking overlap during placement spawn.
	private Vector<Runway> arivRunways;			//	Arrival Runways
	private Vector<Runway> depRunways;			//	Departure Runways

	private int LAS	 = -1;						//	Last Arrival Sector
	private int LDR	 = -1;						//	Last Departure Runway
	private int SSLA = 0;						//	Sweeps Since Last Arrival
	private int SSLD = 0;						//	Sweeps Since Last Departure
	
	private int curRunwyAriv = 0;				//	Current number of open arrival runways
	private int curRunwyDept = 0;				//	Current number of open departure runways
	private int maxRunwyAriv = 2;				//	Maximum number of arrival runways
	private int maxRunwyDept = 2;				//	Maximum number of departure runways
	
	private static int[] scoreArray = null;

	public Airport(){
		scoreArray = new int[SCORE.values().length];
		for(SCORE score : SCORE.values()){
			scoreArray[score.getSID()] = 0;
		}
		this.type = TYPE.AIRPORT;
		this.loc = new Coords(Game.HUDWIDTH + (Game.GAMEWIDTH/2), Game.HEIGHT/2);
		area = new Rectangle((int)loc.getX() - 2, (int)loc.getY() - 2, 4, 4);
		arivRunways = new Vector<Runway>();
		availRunways = new Vector<Runway>();
		depRunways = new Vector<Runway>();
		
		genRunwyAriv();
		genRunwyDept();
		
		openRunway(TYPE.RUNWAY_ARRIVE);
		openRunway(TYPE.RUNWAY_ARRIVE);
		openRunway(TYPE.RUNWAY_DEPART);
		openRunway(TYPE.RUNWAY_DEPART);
		
		aircraft = new Vector<Aircraft>();
		delivery = new Vector<Aircraft>();
		
		arrivalSectors = new Vector<Rectangle>();
		Rectangle temp = new Rectangle(Game.HUDWIDTH,0,Game.GAMEWIDTH,96);			//	Sector North
		arrivalSectors.addElement(temp);
		temp = new Rectangle(Game.HUDWIDTH, 96, 96, Game.HEIGHT - 192);				//	Sector West	
		arrivalSectors.addElement(temp);
		temp = new Rectangle(Game.HUDWIDTH, Game.HEIGHT-96, Game.GAMEWIDTH, 96);	//	Sector South
		arrivalSectors.addElement(temp);
		temp = new Rectangle(Game.WIDTH-96, 96, 96, Game.HEIGHT- 192);				//	Sector East
		arrivalSectors.addElement(temp);
		
		fixes = new Vector<Fix>();
		genFixes();
		
		newFlight(FLIGHT.ARRIVAL);
		newFlight(FLIGHT.ARRIVAL);
		newFlight(FLIGHT.TAKEOFF);
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	public Vector<Aircraft> getDeliveries(){
		return delivery;
	}
	
	public static int[] getScoreArray(){
		return scoreArray;
	}
	
	@Override
	public void render(Graphics g) {
		Draw.airport(g, this);
		Draw.score(g,this);
	}
	
	public void deselect(){
		isSelected = false;
	}
	
	public void select(){
		isSelected = true;
	}
	
	public void setCoords(Coords coords){
		loc.setX(coords.getX());
		loc.setY(coords.getY());
	}

	@Override
	public void tick() {
		scoreArray[SCORE.PLAYTIME.getSID()] += 1;
		SSLA++;
		SSLD++;
		spawn();
		chkOOB();
		chkLanded();
		chkSeparation();
		chkCrash();
	}

	@Override
	public Rectangle getArea() {
		return area;
	}
	
	public void releaseFlight(int index){
		int nr = 0;
		int dr = 0;
		for(int i = 0; i < depRunways.size(); i++){
			if(depRunways.elementAt(i).isOpen()){
				nr++;
			}
		}
		if(nr > 1){
			if(LDR == -1)
				LDR = 0;
			else{
				if(LDR == 0)
					dr = 1;
				else
					dr = 0;
				LDR = dr;
			}
		}
		else{
			dr = 0;
			LDR = dr;
		}
		Runway d = depRunways.elementAt(dr);
		delivery.elementAt(index).setCoords(new Coords(d.getCoords().getX(), d.getCoords().getY()));
		delivery.elementAt(index).setHeadingCurrent(d.getHdg());
		delivery.elementAt(index).setHeadingDesired(delivery.elementAt(index).getHdgCur());
		delivery.elementAt(index).setFlight(FLIGHT.TAKEOFF);
		aircraft.addElement(delivery.elementAt(index));
		Game.registerWithHandler(delivery.elementAt(index));
		scoreArray[SCORE.DEPARTED.getSID()]++;
		delivery.remove(index);
	}
	
	private void chkCrash(){
		for(int i = 0; i < aircraft.size(); i++){
			if(!aircraft.elementAt(i).isConflict())
				continue;
			for(int n = 0; n < aircraft.size(); n++){
				if(!aircraft.elementAt(n).isConflict())
					continue;
				if(aircraft.elementAt(n).equals(aircraft.elementAt(i)))
					continue;
				double dx = Math.abs(aircraft.elementAt(i).getCoords().getX() - aircraft.elementAt(n).getCoords().getX());
				double dy = Math.abs(aircraft.elementAt(i).getCoords().getY() - aircraft.elementAt(n).getCoords().getY());
				double da = Math.abs(aircraft.elementAt(i).getAltCur() - aircraft.elementAt(n).getAltCur());
				if(dx <= 1 && dy <= 1 && da <= 0.1){
					aircraft.elementAt(i).setFlight(FLIGHT.CRASHING);
					aircraft.elementAt(n).setFlight(FLIGHT.CRASHING);
				}
			}
		}
		for(int i = 0; i < aircraft.size(); i++){
			if(aircraft.elementAt(i).getFlight() != FLIGHT.CRASHING)
				continue;
			if(aircraft.elementAt(i).getAltCur() <= 0){
				endFlight(aircraft.elementAt(i));
				scoreArray[SCORE.PLYRSCORE.getSID()] -= 1000;
			}
		}
	}
	
	private void chkLanded(){
		for(int i = 0; i < aircraft.size(); i++){
			if(aircraft.elementAt(i).getFlight() != FLIGHT.LANDING)
				continue;
			
			if(aircraft.elementAt(i).getKIAS() == 0){
				endFlight(aircraft.elementAt(i));
				scoreArray[SCORE.LANDED.getSID()]++;
			}
				
		}
	}
	
	private void chkOOB(){
		for(int i = 0; i < aircraft.size(); i++){
			Aircraft a = aircraft.elementAt(i);
			Coords c = a.getCoords();
			if(c.getX() <= Game.HUDWIDTH || c.getX() > Game.WIDTH
			|| c.getY() <= 0 || c.getY() >= Game.HEIGHT){
				if(a.getFlight() != FLIGHT.CRUISE){
					scoreArray[SCORE.OUTOFSEC.getSID()]++;
					scoreArray[SCORE.PLYRSCORE.getSID()]-=50;
					scoreArray[SCORE.PLYRSKILL.getSID()]-=1;
					if(a.getFlight() == FLIGHT.HANDOFF_AR || a.getFlight() == FLIGHT.HANDOFF_DE){
						scoreArray[SCORE.HANDOFFMISS.getSID()]++;
						scoreArray[SCORE.PLYRSCORE.getSID()]-=25;
						scoreArray[SCORE.PLYRSKILL.getSID()]-=1;
					}
				}
				a.deselect();
				Game.finalizeWithHandler(a);
				aircraft.remove(a);
			}
		}
	}
	
	private void chkSeparation(){
		for(int i = 0; i < aircraft.size(); i++){
			Aircraft src = aircraft.elementAt(i);
			for(int t = 0; t < aircraft.size(); t++){
				Aircraft tgt = aircraft.elementAt(t);
				if(src == tgt)
					continue;
				else{
					if(Math.abs(src.getAltCur() - tgt.getAltCur()) <= 1){
						if(Calc.distanceNM(src.getCoords(), tgt.getCoords()) <= 1){
							System.out.println("Set conflict");
							src.setConflict(true);
							scoreArray[SCORE.SEPINCTIME.getSID()] += 1;
							break;
						}
					}
					else{
						src.setConflict(false);
						//break;
					}
				}
			}
		}
	}
	
	private void endFlight(Aircraft a){
		if(a.isSelected())
			a.deselect();
		Game.finalizeWithHandler(a);
		aircraft.remove(a);
	}
	
	private void newFlight(FLIGHT flight){
		if(flight == FLIGHT.ARRIVAL){	
			int cx,cy;
			int n = -1;
			do{
				n = rand.nextInt(4);
			}while(n == LAS);
			LAS = n;
			Rectangle s = arrivalSectors.elementAt(n);
			int hdg = 0;
			switch(n){
			case 0:		// N
				cy = 1;
				cx = rand.nextInt((int)(s.getMaxX() - s.getMinX())) + (int)s.getMinX();
				if(cx > Game.GAMEWIDTH/2 + Game.HUDWIDTH){
					hdg = 180 + rand.nextInt(45);
				}
				else{
					hdg = 180 - rand.nextInt(45);
				}
				break;
			case 1:		// W
				cx = Game.HUDWIDTH + 1;
				cy = rand.nextInt((int)(s.getMaxY() - s.getMinY())) + (int)s.getMinY();
				if(cy > Game.HEIGHT / 2)
					hdg = 90 - rand.nextInt(45);
				else
					hdg = 90 + rand.nextInt(45);
				break;
			case 2:		// S
				cy = Game.HEIGHT - 1;
				cx = rand.nextInt((int)(s.getMaxX() - s.getMinX())) + (int)s.getMinX();
				if(cx > Game.GAMEWIDTH/2 + Game.HUDWIDTH)
					hdg = 0 - rand.nextInt(45);
				else
					hdg = 0 + rand.nextInt(45);
				break;
			case 3:		// E
				cx = Game.WIDTH - 1;
				cy = rand.nextInt((int)(s.getMaxY() - s.getMinY())) + (int)s.getMinY();
				if(cy > Game.HEIGHT / 2)
					hdg = 270 + rand.nextInt(45);
				else
					hdg = 270 - rand.nextInt(45);
				break;
			default:
				System.out.println("WARN: newFlight reached default case.");
				cx = Game.WIDTH / 2 + Game.HUDWIDTH;
				cy = Game.HEIGHT / 2;
				hdg = rand.nextInt(360);
				break;
			}
			if(hdg >= 360)
				hdg -= 360;
			if(hdg < 0)
				hdg += 360;
			Aircraft a = new Aircraft(new Coords(cx,cy), hdg, 240, FLIGHT.HANDOFF_AR);
			genFlightInfo(a);
			aircraft.addElement(a);
			Game.registerWithHandler(a);
			scoreArray[SCORE.ARRIVED.getSID()]++;
//			SSLA = 0;
		}
		else if(flight == FLIGHT.DELIVERY){
			Aircraft a = new Aircraft(new Coords(loc.getX(), loc.getY()), 0, 0, FLIGHT.DELIVERY);
			genFlightInfo(a);
			int fix = 0;
			do{
				fix = rand.nextInt(fixes.size());
			}while(Calc.distanceNM(loc, fixes.elementAt(fix).getCoords()) < 25);
			a.setFix(fixes.elementAt(fix));
			delivery.addElement(a);
		}
		else{
			int nr = 0;		// Num runways available for departure
			int dr = 0;		// Departing runway
			for(int i = 0; i < depRunways.size(); i++){
				if(depRunways.elementAt(i).isOpen())
					nr++;
			}
			if(nr > 1){
				if(LDR == -1)
					LDR = 0;
				else{
					if(LDR == 0)
						dr = 1;
					else
						dr = 0;
					LDR = dr;
				}
			}
			Runway d = depRunways.elementAt(dr);
			Aircraft a = new Aircraft(new Coords(d.getCoords().getX(), d.getCoords().getY()), d.getHdg(), 0, FLIGHT.TAKEOFF);
			genFlightInfo(a);
			int fix = 0;
			do{
				fix = rand.nextInt(fixes.size());
			}while(Calc.distanceNM(loc, fixes.elementAt(fix).getCoords()) < 25);
			a.setFix(fixes.elementAt(fix));
//			a.setFix(fixes.elementAt(rand.nextInt(fixes.size())));
			aircraft.addElement(a);
			Game.registerWithHandler(a);
			scoreArray[SCORE.DEPARTED.getSID()]++;
			SSLD = 0;
		}
	}
	
	private void genFixes(){
		Coords temp;
		Fix fix;
		for(int i = 0; i < 8; i++){
			do{
				int x = rand.nextInt(Game.GAMEWIDTH) + Game.HUDWIDTH;
				int y = rand.nextInt(Game.HEIGHT); 
				temp = new Coords(x,y);
				int id = rand.nextInt(26);
				fix = new Fix(temp, Text.fixNames[id]);
			}while(checkFixConflict(fix));
			fixes.addElement(fix);
			Game.registerWithHandler(fix);
		}
	}
	
	private void genFlightInfo(Aircraft a){
		int op = rand.nextInt(Text.airNames.length);
		int fn;
		do{
			fn = rand.nextInt(999) + 100;
		}while(checkFlightNoConflict(fn));
		a.setFlightInfo(Text.airNames[op], fn + "");
	}
	
	private void genRunwyAriv(){
		Runway r;
		for(LAYOUT l : LAYOUT.values()){
			for(int i = 0; i < l.getRwyA(); i++){
				Coords pc = Calc.relativeCoords(loc, l.getRwyAHT()[i], (l.getRwyADT()[i] * PPNM));
				r = new Runway(pc, l.getRwyAHdg()[i], l.getRwyAName()[i], TYPE.RUNWAY_ARRIVE);
				availRunways.addElement(r);
				arivRunways.addElement(r);
			}
		}
	}
	
	private void genRunwyDept(){
		Runway r;
		for(LAYOUT l : LAYOUT.values()){
			for(int i = 0; i < l.getRwyD(); i++){
				Coords pc = Calc.relativeCoords(loc, l.getRwyDHT()[i], l.getRwyDDT()[i] * PPNM);
				r = new Runway(pc, l.getRwyDHdg()[i], l.getRwyDName()[i], TYPE.RUNWAY_DEPART);
				availRunways.addElement(r);
				depRunways.addElement(r);
			}
		}
	}
	
	/**
	 * Method for opening up a runway for aircraft.
	 * @param toOpen	The TYPE of runway to open.
	 */
	private void openRunway(TYPE toOpen){
		if(toOpen == TYPE.RUNWAY_ARRIVE){
			for(int i = 0; i < arivRunways.size(); i++){
				if(arivRunways.elementAt(i).isOpen())
					continue;
				else{
					arivRunways.elementAt(i).open();
					curRunwyAriv++;
					return;
				}	
			}
		}
		else if(toOpen == TYPE.RUNWAY_DEPART){
			for(int i = 0; i < depRunways.size(); i++){
				if(depRunways.elementAt(i).isOpen())
					continue;
				else{
					depRunways.elementAt(i).open();
					curRunwyDept++;
					return;
				}
			}
		}
		else return;
	}
	
	private void closeRunway(TYPE toClose){
		if(toClose == TYPE.RUNWAY_ARRIVE){
			for(int i = arivRunways.size() - 1; i >= 0; i--){
				if(!arivRunways.elementAt(i).isOpen())
					continue;
				else{
					arivRunways.elementAt(i).close();
				}
			}
		}
		else if(toClose == TYPE.RUNWAY_DEPART){
			for(int i = depRunways.size() - 1; i >= 0; i--){
				if(!depRunways.elementAt(i).isOpen())
					continue;
				else{
					depRunways.elementAt(i).close();
				}
			}
		}
	}
	
	private boolean checkFixConflict(Fix fix){
		if(fixes.size() == 0 && Calc.distanceNM(fix.getCoords(), loc) >= 15)
			return false;
		for(int i = 0; i < fixes.size(); i++){
			if(fix.getID().equals(fixes.elementAt(i).getID()))
				return true;
			if(Calc.distanceNM(fix.getCoords(), loc) <= 15)
				return true;
			if(Calc.distanceNM(fix.getCoords(), fixes.elementAt(i).getCoords()) <= 10)
				return true;
			if(Calc.distanceNM(fix.getCoords(), loc) >= 30)
				return true;
		}
		return false;
	}
	
	private boolean checkFlightNoConflict(int num){
		if(aircraft.size() == 0)
			return false;
		String snum = num +"";
		for(int i = 0; i < aircraft.size(); i++){
			if(aircraft.elementAt(i).getFlightNo() == snum)
				return true;
		}
		return false;
	}

	@Deprecated
	private boolean checkRunwayConflict(Runway rwy){
		if(availRunways.size() == 0)
			return false;
		else{
			for(int i = 0; i < availRunways.size(); i++){
				Runway tgt = availRunways.elementAt(i);
				if(Calc.distanceNM(rwy.getCoords(), tgt.getCoords()) <= 3){
					return true;
				}
				if(Calc.lineIntcpt(rwy.getRunwayPath(), tgt.getRunwayPath())){
					return true;
				}
				if(rwy.type == TYPE.RUNWAY_ARRIVE){
					if(tgt.type == TYPE.RUNWAY_ARRIVE && Calc.lineIntcpt(rwy.getLocalizer().getLocPath(), tgt.getLocalizer().getLocPath())){
						return true;
					}
					else if(tgt.type == TYPE.RUNWAY_ARRIVE && Calc.lineIntcpt(rwy.getRunwayPath(), tgt.getLocalizer().getLocPath())){
						return true;
					}
					else if(tgt.type == TYPE.RUNWAY_DEPART && Calc.lineIntcpt(rwy.getLocalizer().getLocPath(), tgt.getDepartPath())){
						return true;
					}
					else if(tgt.type == TYPE.RUNWAY_DEPART && Calc.lineIntcpt(rwy.getRunwayPath(), tgt.getDepartPath())){
						return true;
					}
					else if(Calc.lineIntcpt(rwy.getLocalizer().getLocPath(), tgt.getRunwayPath())){
						return true;
					}
					else if(tgt.type == TYPE.RUNWAY_DEPART){
						int dor = 0 + rwy.getHdg();
						int dot = 0 + tgt.getHdg();
						int doa = Math.abs(dor-dot);
						if(doa < 45) return true;
					}
				}
				if(rwy.type == TYPE.RUNWAY_DEPART){
					if(tgt.type == TYPE.RUNWAY_ARRIVE && Calc.lineIntcpt(rwy.getDepartPath(), tgt.getLocalizer().getLocPath())){
						return true;
					}
					else if(tgt.type == TYPE.RUNWAY_DEPART && Calc.lineIntcpt(rwy.getDepartPath(), tgt.getDepartPath())){
						return true;
					}
					else if(Calc.lineIntcpt(rwy.getDepartPath(), tgt.getRunwayPath())){
						return true;
					}
					else if(tgt.type == TYPE.RUNWAY_ARRIVE){
						int dor = 0 + rwy.getHdg();
						int dot = 0 + tgt.getHdg();
						int doa = Math.abs(dor-dot);
						if(doa < 45) return true;		
					}
				}
			}
		}
		return false;
	}
	
	private void spawn(){
		int ps = scoreArray[SCORE.PLYRSKILL.getSID()];
		int apm = 1;
		int dpm = 1;
		if(ps >=5){
			apm = 2;
			dpm = 2;
		}
		else if(ps >=10){
			apm = 3;
			dpm = 3;
		}
		else if(ps >=15){
			apm = 4;
			dpm = 4;
		}
		if(SSLA == 60/apm){
			newFlight(FLIGHT.ARRIVAL);
			SSLA = 0;
		}
		if(SSLD == 60/dpm){
			newFlight(FLIGHT.DELIVERY);
//			newFlight(FLIGHT.DEPARTURE);
			SSLD = 0;
		}
	}
}
