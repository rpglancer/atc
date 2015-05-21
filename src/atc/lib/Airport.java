package atc.lib;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Vector;

import atc.Game;
import atc.display.Draw;
import atc.type.TYPE;

public class Airport extends Entity{

	private Control control;
	private Random rand = new Random();
	private Vector<Runway> runways = new Vector<Runway>();
	
	public Airport(){
		this.type = TYPE.AIRPORT;
		this.loc = new Coords(Game.HUDWIDTH + (Game.GAMEWIDTH/2), Game.HEIGHT/2);
		control = new Control(this);
//		int randhdg = rand.nextInt(360);
//		int randdst = (int)(rand.nextInt(5) * PPNM);
//		int placehdg = rand.nextInt(360);
//		Coords placeCoords = Calc.relativeCoords(loc, placehdg, randdst);
//		runways.addElement(new Runway(placeCoords.getX(), placeCoords.getY(), randhdg, TYPE.RUNWAY_ARRIVE));
//		randhdg = rand.nextInt(360);
//		randdst = (int)(rand.nextInt(5) * PPNM);
//		placehdg = rand.nextInt(360);
//		placeCoords = Calc.relativeCoords(loc, placehdg, randdst);
//		runways.addElement(new Runway(placeCoords.getX(), placeCoords.getY(), randhdg, TYPE.RUNWAY_DEPART));
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	public Vector<Runway> getOpenRunways(){
		return runways;
	}
	
	public void openRunway(Runway runway){
		runways.add(runway);
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
		control.tick();
		// TODO Auto-generated method stub	
	}

	@Override
	public Rectangle getArea() {
		return area;
	}
}
