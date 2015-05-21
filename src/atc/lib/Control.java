package atc.lib;

import java.awt.Rectangle;
import java.util.Random;
import java.util.Vector;

import atc.Game;
import atc.type.TYPE;

public class Control {
	private Airport airport;
	private Random rand = new Random();
	private Vector<Rectangle> arrivalSectors;	//	Sectors [rectangles] inside which arrivals can spawn
	private Vector<Runway> availRunways;//
	private Vector<Runway> arivRunways;
	private Vector<Runway> depRunways;
	private int handoffs;				//	Handoffs completed
	private int handoffsMissed;			//	Handoffs missed
	private int score;					//	Player score
	private int skill;					//	Player skill
	private int planesLanded;			//	Aircraft landed
	private int planesDeparted;			//	Aircraft departed
	private int planesOutOfSector;		//	Aircraft out of sector
	
	private int curRunwyAriv = 0;
	private int curRunwyDept = 0;
	private int maxRunwyAriv = 3;
	private int maxRunwyDept = 2;
	
	public Control(Airport airport){
		Runway r;
		this.airport = airport;
		arivRunways = new Vector<Runway>();
		availRunways = new Vector<Runway>();
		depRunways = new Vector<Runway>();
		
		// Generate Arrival Runways	
		for(int i = 0; i < maxRunwyAriv; i++){		
			do{
				int randhdg = rand.nextInt(360);
				int randdst = (int)(rand.nextInt(5) * Airport.PPNM);
				int placehdg = rand.nextInt(360);
				Coords placeCoords = Calc.relativeCoords(airport.getCoords(), placehdg, randdst);
				r = new Runway(placeCoords.getX(), placeCoords.getY(), randhdg, TYPE.RUNWAY_ARRIVE);
			}while(checkRunwayConflict(r));
			availRunways.addElement(r);
			arivRunways.addElement(r);
		}
		
		// Generate Departure Runways
		for(int i = 0; i < maxRunwyDept; i++){
			do{
				int randhdg = rand.nextInt(360);
				int randdst = (int)(rand.nextInt(5) * Airport.PPNM);
				int placehdg = rand.nextInt(360);
				Coords placeCoords = Calc.relativeCoords(airport.getCoords(), placehdg, randdst);
				r = new Runway(placeCoords.getX(), placeCoords.getY(), randhdg, TYPE.RUNWAY_DEPART);
			}while(checkRunwayConflict(r));
			availRunways.addElement(r);
			depRunways.addElement(r);
		}
		
		for(int i = 0; i < availRunways.size(); i++){
			Runway rwy = availRunways.elementAt(i);
			Game.registerWithHandler(rwy);
			if(rwy.type == TYPE.RUNWAY_ARRIVE)
				Game.registerWithHandler(rwy.getLocalizer());
		}
		
		// Generate a few flights to start with
		Aircraft a = new Aircraft(600, 300, 180, 140, TYPE.AIRCRAFT, this);
		Game.registerWithHandler(a);
	}
	
	public void tick(){

	}
	// this is prematurely returning dummy.
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
	
//	private boolean checkRunwayConflict(Runway src, Runway tgt){
//		if(src.type == TYPE.RUNWAY_ARRIVE){
//			if(tgt.type == TYPE.RUNWAY_ARRIVE){
//				return Calc.lineIntcpt(src.getLocalizer().getLocPath(), tgt.getLocalizer().getLocPath());
//			}
//			else if(tgt.type == TYPE.RUNWAY_DEPART){
//				return Calc.lineIntcpt(src.getLocalizer().getLocPath(), tgt.getDepartPath());
//			}
//		}
//		else if(src.type == TYPE.RUNWAY_DEPART){
//			if(tgt.type == TYPE.RUNWAY_ARRIVE){
//				return Calc.lineIntcpt(src.getDepartPath(), tgt.getLocalizer().getLocPath());
//			}
//			else if(tgt.type == TYPE.RUNWAY_DEPART){
//				return Calc.lineIntcpt(src.getDepartPath(), tgt.getDepartPath());
//			}
//		}
//		System.out.println("WARN: checkRunwayConflict() could not find a value to return. Returning false.");
//		return false;
//	}
	
	private void openRunway(){
		
	}
	
	private void closeRunway(){
		
	}
}
