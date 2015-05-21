package atc.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import atc.display.Fonts;
import atc.type.TYPE;

public class Fix extends Entity{
	
	private String ID = "WIZZL";
	
	public Fix(Coords coords){
		type = TYPE.FIX;
		setCoords(coords);
		area = new Rectangle((int)(loc.getX() - 0.25 * PPNM), (int)(loc.getY() - 0.25 * PPNM), (int)((PPNM * 0.25) * 2),(int)((PPNM * 0.25) * 2) );
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
//		Rectangle rect = new Rectangle((int)loc.getX() - (int)(fm.stringWidth(Fonts.fix)/1.5), (int)loc.getY() - fm.getAscent()/2, fm.stringWidth(Fonts.fix), fm.getHeight());
		g.setColor(Color.ORANGE);
//		Draw.centeredcircle(g, loc, PPNM*1.5, Color.cyan);
//		Text.boxText(g, Fonts.radarobj, rect, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.fix);
		g.setFont(Fonts.radarobj);

		g.drawString(Fonts.fix, (int)(loc.getX() - fm.stringWidth(Fonts.fix)/2), (int)(loc.getY() + fm.getAscent()/2));
		g.setFont(Fonts.radartext);
		fm = g.getFontMetrics(Fonts.radartext);
		g.drawString(ID, (int)(loc.getX() - fm.stringWidth(ID) / 2), (int)(area.getMaxY() + fm.getHeight()));
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
