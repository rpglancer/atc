package atc.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import atc.display.Draw;
import atc.display.Fonts;
import atc.display.Text;
import atc.type.HALIGN;
import atc.type.TYPE;
import atc.type.VALIGN;

public class Fix extends Entity{
	
	private String ID = "SHIGY";
	
	public Fix(Coords coords){
		type = TYPE.FIX;
		setCoords(coords);
	}

	@Override
	public Coords getCoords() {
		return loc;
	}

	@Override
	public void deselect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Graphics g) {
		Color prevC = g.getColor();
		Font prevF = g.getFont();
		FontMetrics fm = g.getFontMetrics(Fonts.radarobj);
		Rectangle rect = new Rectangle((int)loc.getX() - (int)(fm.stringWidth(Fonts.fix)/1.5), (int)loc.getY() - fm.getAscent()/2, fm.stringWidth(Fonts.fix), fm.getHeight());
		g.setColor(Color.darkGray);
		Draw.centeredcircle(g, loc, NMPP*1.5, Color.cyan);
		Text.boxText(g, Fonts.radarobj, rect, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.fix);
		g.setFont(prevF);
		g.setColor(prevC);
	}

	@Override
	public void select() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCoords(Coords coords) {
		if(loc == null){
			loc = new Coords(coords.getX(), coords.getY());
		}
		else{
			loc.setX(coords.getX());
			loc.setY(coords.getY());
		}
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
