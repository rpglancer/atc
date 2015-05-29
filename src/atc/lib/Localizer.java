package atc.lib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import atc.display.Draw;
import atc.type.TYPE;

public class Localizer extends Entity{
	private int glideslope;
	private int hdg;
	private Coords rwy;
	private Coords inner;
	private Coords middle;
	private Coords outer;
	private Line2D locpath = null;
	
	private String locID = "";
	
	public Localizer(Runway rwy){
		type = TYPE.LOCALIZER;
		double x, y, ex, ey;
		this.rwy = rwy.getCoords();
		this.hdg = rwy.getHdg();
		locID = "ILS" + rwy.getID();
		x = this.rwy.getX();
		y = this.rwy.getY();
		ex = this.rwy.getX() + (PPNM * 18) * Math.sin(Math.toRadians(Convert.reciprocal(this.hdg)));
		ey = this.rwy.getY() - (PPNM * 18) * Math.cos(Math.toRadians(Convert.reciprocal(this.hdg)));
		locpath = new Line2D.Double(x,y,ex,ey);
		
		x = (this.rwy.getX() + (PPNM*15) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.getY() - (PPNM*15) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.outer = new Coords(x,y);
		x = (this.rwy.getX() + (PPNM*11.25) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.getY() - (PPNM*11.25) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.middle = new Coords(x,y);
		x = (this.rwy.getX() + (PPNM*7.5) * Math.sin(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		y = (this.rwy.getY() - (PPNM*7.5) * Math.cos(Math.toRadians(Convert.reciprocal(rwy.getHdg()))));
		this.inner = new Coords(x,y);
		glideslope = (int)Math.toDegrees(Math.atan(4/15));
	}
	@Override
	public Coords getCoords() {
		return rwy;
	}
	
	public int getHdg(){
		return hdg;
	}
	
	public int getGlideSlope(){
		return glideslope;
	}
	
	public void deselect(){
		
	}
	
	public String getID(){
		return locID;
	}
	
	public Line2D getLocPath(){
		return locpath;
	}
	
	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color prevC = g.getColor();
		g.setColor(Color.darkGray);
		g2d.draw(locpath);
		
		Draw.centeredcircle(g, inner, 0.25 * PPNM, Color.cyan);
		Draw.centeredcircle(g, middle, 0.25 * PPNM, Color.cyan);
		Draw.centeredcircle(g, outer, 0.25 * PPNM, Color.cyan);
		
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
	@Override
	public Rectangle getArea() {
		return area;
	}
}
