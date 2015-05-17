package atc.lib;

import java.awt.Color;
import java.awt.Graphics;

import atc.Game;

public class Localizer extends Entity{
	private int hdg;
	private Coords rwy;
	private Coords inner;
	private Coords middle;
	private Coords outer;
	private Coords thresh;
	
	public Localizer(Runway rwy){
		this.rwy = rwy.getCoords();
		this.hdg = rwy.getHdg();
		double x, y;
		x = (this.rwy.x + (NMPP*5) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.y + (NMPP*5) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.thresh = new Coords(x,y);
		x = (this.rwy.x + (NMPP*4) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.y + (NMPP*4) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.outer = new Coords(x,y);
		x = (this.rwy.x + (NMPP*2.5) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.y + (NMPP*2.5) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.middle = new Coords(x,y);
		x = (this.rwy.x + (NMPP*1) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.y + (NMPP*1) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.inner = new Coords(x,y);
		Game.registerWithHandler(this);
	}
	@Override
	public Coords getCoords() {
		return rwy;
	}
	@Override
	public void render(Graphics g) {
		Color prevC = g.getColor();
		g.setColor(Color.darkGray);
		g.drawLine((int)rwy.x, (int)rwy.y, (int)thresh.x, (int)thresh.y);
		g.drawOval((int)inner.x - 0, (int)inner.y - 3, 6, 6);
		g.drawOval((int)middle.x - 0, (int)middle.y - 3, 6, 6);
		g.drawOval((int)outer.x - 0, (int)outer.y - 3, 6, 6);
		g.setColor(prevC);
	}
	@Override
	public void setCoords(Coords coords) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
}
