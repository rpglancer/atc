package atc.lib;

import java.awt.Color;
import java.awt.Graphics;

import javafx.scene.shape.Line;
import atc.Game;
import atc.display.Draw;

public class Localizer extends Entity{
	private int hdg;
	private Coords rwy;
	private Coords inner;
	private Coords middle;
	private Coords outer;
	private Line locpath = null;
	
	public Localizer(Runway rwy){
		double x, y, ex, ey;
		this.rwy = rwy.getCoords();
		this.hdg = rwy.getHdg();
		x = this.rwy.getX();
		y = this.rwy.getY();
		ex = this.rwy.getX() + (NMPP * 15) * Math.sin(Math.toRadians(Convert.reciprocal(this.hdg)));
		ey = this.rwy.getY() + (NMPP * 15) * Math.cos(Math.toRadians(Convert.reciprocal(this.hdg)));
		locpath = new Line(x,y,ex,ey);
		
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
		
		g.drawLine((int)locpath.getStartX(), (int)locpath.getStartY(), (int)locpath.getEndX(), (int)locpath.getEndY());
		
		Draw.centeredcircle(g, inner, 0.25 * NMPP, Color.cyan);
		Draw.centeredcircle(g, middle, 0.25 * NMPP, Color.cyan);
		Draw.centeredcircle(g, outer, 0.25 * NMPP, Color.cyan);
		
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
