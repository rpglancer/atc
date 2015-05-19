package atc.lib;

import java.awt.Graphics;
import java.awt.Rectangle;

import atc.Game;
import atc.display.Draw;
import atc.type.TYPE;

public class Airport extends Entity{	
	public Airport(){
		this.type = TYPE.AIRPORT;
		this.loc = new Coords(Game.HUDWIDTH + (Game.GAMEWIDTH/2), Game.HEIGHT/2);
		Runway runway = new Runway(loc.getX(), loc.getY(), 280, TYPE.RUNWAY_ARRIVE);
	}
	
	public Coords getCoords(){
		return loc;
	}
	
	@Override
	public void render(Graphics g) {
//		Draw.airport(g, (int)loc.getX(), (int)loc.getY());
		Draw.airport(g, this);
	}
	
	public void deselect(){
		
	}
	
	public void select(){
		
	}
	
	public void setCoords(Coords coords){
		loc.setX(coords.getX());
		loc.setY(coords.getY());
//		loc.x = coords.getX();
//		loc.y = coords.getY();
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getArea() {
		return area;
	}
}
