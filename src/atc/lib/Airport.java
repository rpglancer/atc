package atc.lib;

import java.awt.Graphics;

import atc.Game;
import atc.display.Draw;
import atc.type.TYPE;

public class Airport extends Entity{	
	public Airport(){
		this.type = TYPE.AIRPORT;
		this.loc = new Coords(Game.WIDTH/2, Game.HEIGHT/2);
		Runway runway = new Runway(loc.getX(), loc.getY(), 280, TYPE.RUNWAY_DEPART);
	}
	
	@Override
	public void render(Graphics g) {
		Draw.airport(g, (int)loc.getX(), (int)loc.getY());
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
}
