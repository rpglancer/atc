package atc.lib;

import java.awt.Color;
import java.awt.Graphics;

import atc.Game;
import atc.display.Draw;

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
		x = (this.rwy.getX() + (NMPP*15) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.getY() + (NMPP*15) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.thresh = new Coords(x,y);
		x = (this.rwy.getX() + (NMPP*12) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.getY() + (NMPP*12) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.outer = new Coords(x,y);
		x = (this.rwy.getX() + (NMPP*8) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.getY() + (NMPP*8) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.middle = new Coords(x,y);
		x = (this.rwy.getX() + (NMPP*4) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.getY() + (NMPP*4) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.inner = new Coords(x,y);
		Game.registerWithHandler(this);
	}
	@Override
	public Coords getCoords() {
		return rwy;
	}
	
	public void deselect(){
		
	}
	
	@Override
	public void render(Graphics g) {
		Color prevC = g.getColor();
		g.setColor(Color.darkGray);
		g.drawLine((int)rwy.getX(), (int)rwy.getY(), (int)thresh.getX(), (int)thresh.getY());
		Draw.centeredcircle(g, inner, 0.25 * NMPP, Color.cyan);
		Draw.centeredcircle(g, middle, 0.25 * NMPP, Color.cyan);
		Draw.centeredcircle(g, outer, 0.25 * NMPP, Color.cyan);
//		g.drawOval((int)inner.getX() - 0, (int)inner.getY() - 3, 6, 6);
//		g.drawOval((int)middle.getX() - 0, (int)middle.getY() - 3, 6, 6);
//		g.drawOval((int)outer.getX() - 0, (int)outer.getY() - 3, 6, 6);
		g.setColor(prevC);
	}
	
	public void select(){
		
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
