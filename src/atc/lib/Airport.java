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
	private Vector<Rectangle> arrivalSectors;	//	Sectors [rectangles] inside which arrivals can spawn
	private Vector<Runway> availRunways;		//	All runways, only used for checking overlap during placement spawn.
	private Vector<Runway> arivRunways;			//	Arrival Runways
	private Vector<Runway> depRunways;			//	Departure Runways
	private int handoffs;						//	Handoffs completed
	private int handoffsMissed;					//	Handoffs missed
	private int score;							//	Player score
	private int skill;							//	Player skill
	private int planesLanded;					//	Aircraft landed
	private int planesDeparted;					//	Aircraft departed
	private int planesOutOfSector;				//	Aircraft out of sector
	
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
		openRunway(TYPE.RUNWAY_DEPART);
		
		aircraft = new Vector<Aircraft>();
		
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
			do{
				int randhdg = rand.nextInt(360);
				int randdst = (int)(rand.nextInt(5) * Airport.PPNM);
				int placehdg = rand.nextInt(360);
				Coords placeCoords = Calc.relativeCoords(loc, placehdg, randdst);
				r = new Runway(placeCoords.getX(), placeCoords.getY(), randhdg, TYPE.RUNWAY_ARRIVE);
			}while(checkRunwayConflict(r));
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
				}
			}
		}
		return false;
	}
	
}
