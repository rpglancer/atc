package atc.lib;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Vector;

import atc.Game;
import atc.display.Draw;
import atc.display.Text;
import atc.type.FLIGHT;
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
	private Vector<Fix> fixes;
	private Vector<Rectangle> arrivalSectors;	//	Sectors [rectangles] inside which arrivals can spawn
	private Vector<Runway> availRunways;		//	All runways, only used for checking overlap during placement spawn.
	private Vector<Runway> arivRunways;			//	Arrival Runways
	private Vector<Runway> depRunways;			//	Departure Runways

	private int handoffs			= 0;		//	Handoffs completed
	private int handoffsMissed		= 0;		//	Handoffs missed
	private int score				= 0;		//	Player score
	private int skill				= 0;		//	Player skill
	private int planesLanded		= 0;		//	Aircraft landed
	private int planesDeparted		= 0;		//	Aircraft departed
	private int planesOutOfSector	= 0;		//	Aircraft out of sector

	private int LAS	 = -1;						//	Last Arrival Sector
	private int LDR	 = -1;						//	Last Departure Runway
	private int SSLA = 0;						//	Sweeps Since Last Arrival
	private int SSLD = 0;						//	Sweeps Since Last Departure
	
	private int maxRunwyAriv = 3;				//	Maximum number of arrival runways
	private int maxRunwyDept = 2;				//	Maximum number of departure runways

	public Airport(){
		this.type = TYPE.AIRPORT;
		this.loc = new Coords(Game.HUDWIDTH + (Game.GAMEWIDTH/2), Game.HEIGHT/2);
		arivRunways = new Vector<Runway>();
		availRunways = new Vector<Runway>();
		depRunways = new Vector<Runway>();
		
		genRunwyAriv();
		genRunwyDept();
		
		openRunway(TYPE.RUNWAY_ARRIVE);
		openRunway(TYPE.RUNWAY_ARRIVE);
		openRunway(TYPE.RUNWAY_ARRIVE);
		openRunway(TYPE.RUNWAY_DEPART);
		openRunway(TYPE.RUNWAY_DEPART);
		
		aircraft = new Vector<Aircraft>();
		
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
		
		newFlight();
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	@Override
	public void render(Graphics g) {
		Draw.airport(g, this);
	}
	
	public void deselect(){
		
	}
	
	public void select(){
		
	}
	
	public void setCoords(Coords coords){
		loc.setX(coords.getX());
		loc.setY(coords.getY());
	}

	@Override
	public void tick() {
		SSLA++;
		SSLD++;
		chkLanded();
	}

	@Override
	public Rectangle getArea() {
		return area;
	}

	private void chkLanded(){
		for(int i = 0; i < aircraft.size(); i++){
			if(aircraft.elementAt(i).getFlight() != FLIGHT.LANDING)
				continue;
			
			if(aircraft.elementAt(i).getKIAS() == 0)
				endFlight(aircraft.elementAt(i));
		}
	}
	
	private void endFlight(Aircraft a){
		a.deselect();
		Game.finalizeWithHandler(a);
		aircraft.remove(a);
	}
	
	private void newFlight(){
		Aircraft a = new Aircraft(600, 300, 180, 140, TYPE.AIRCRAFT);//, this);
		genFlightInfo(a);
		aircraft.addElement(a);
		Game.registerWithHandler(a);
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
		int mo = rand.nextInt(Text.airTypes.length);
		int op = rand.nextInt(Text.airNames.length);
		int fn;
		do{
			fn = rand.nextInt(999) + 100;
		}while(checkFlightNoConflict(fn));
		a.setFlightInfo(Text.airNames[op], fn+"", Text.airTypes[mo]);
	}
	
	/**
	 * Method to randomly generate runways for Arriving aircraft
	 */
	private void genRunwyAriv(){
		Runway r;
		for(int i = 0; i < maxRunwyAriv; i++){
			if(arivRunways.size() == 0){
				int randhdg = rand.nextInt(360);
				int randdst = (int)(rand.nextInt(5) * Airport.PPNM);
				int placehdg = rand.nextInt(360);
				Coords pc = Calc.relativeCoords(loc, placehdg, randdst);
				r = new Runway(pc.getX(), pc.getY(), randhdg, TYPE.RUNWAY_ARRIVE);
			}
			else{
				int hdg = arivRunways.elementAt(0).getHdg();
				int randdst = (int)(5 * Airport.PPNM);
				int placeHdg = arivRunways.elementAt(0).getHdg() - 90;
				if(placeHdg < 0) placeHdg += 360;
				Coords pc = Calc.relativeCoords(arivRunways.elementAt(arivRunways.size() - 1).getCoords(), placeHdg, randdst);
				r = new Runway(pc.getX(), pc.getY(), hdg, TYPE.RUNWAY_ARRIVE);
			}
			availRunways.addElement(r);
			arivRunways.addElement(r);
		}
	}
	
	/**
	 * Method to generate runways for departing aircraft.
	 */
	private void genRunwyDept(){
		Runway r;
		for(int i = 0; i < maxRunwyDept; i++){
			do{
				int randhdg = rand.nextInt(360);
				int randdst = (int)(rand.nextInt(5) * Airport.PPNM);
				int placehdg = rand.nextInt(360);
				Coords placeCoords = Calc.relativeCoords(loc, placehdg, randdst);
				r = new Runway(placeCoords.getX(), placeCoords.getY(), randhdg, TYPE.RUNWAY_DEPART);
			}while(checkRunwayConflict(r));
			availRunways.addElement(r);
			depRunways.addElement(r);
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
		if(fixes.size() == 0)
			return false;
		for(int i = 0; i < fixes.size(); i++){
			if(fix.getID().equals(fixes.elementAt(i).getID()))
				return true;
			if(Calc.distanceNM(fix.getCoords(), fixes.elementAt(i).getCoords()) < 10)
				return true;
			if(Calc.distanceNM(fix.getCoords(), loc) >= 25)
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
	
}
