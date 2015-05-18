package atc.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import atc.lib.Aircraft;
import atc.lib.Airport;
import atc.lib.Coords;

public class Draw {

	//public static void airport(Graphics g, int x, int y){
	public static void airport(Graphics g, Airport airport){
		int height = 4, width = 4;
		Color prev = g.getColor();
		g.setColor(Color.cyan);
		g.drawRect((int)airport.getCoords().getX() - (width / 2), (int)airport.getCoords().getY() - (height / 2), 5, 5);
		
		Draw.centeredcircle(g, airport.getCoords(), Airport.NMPP * 15, Color.darkGray);
		Draw.centeredcircle(g, airport.getCoords(), Airport.NMPP * 30, Color.darkGray);
		
		g.setColor(prev);
	}

	public static void box(Graphics g, Rectangle rect, int bsize, Color border, Color fill){
		Graphics2D g2d = (Graphics2D) g;
		Color prevC = g2d.getColor();
		Rectangle temp = new Rectangle(rect);
		if(bsize < 1){
			g2d.setColor(fill);
			g2d.fill(temp);
		}
		else{
			g2d.setColor(border);
			g2d.fill(temp);
			temp.setRect(temp.getX() + bsize, temp.getY() + bsize, temp.getWidth() - (bsize *2), temp.getHeight() - (bsize * 2));
			g2d.setColor(fill);
			g2d.fill(temp);
		}
		g2d.setColor(prevC);
	}
	
	public static void centeredcircle(Graphics g, Coords coords, double radius, Color color){
		Graphics2D g2d = (Graphics2D) g;
		Color prevC = g2d.getColor();
		g2d.setColor(color);
		double x = coords.getX() - radius;
		double y = coords.getY() - radius;
		Ellipse2D circle = new Ellipse2D.Double(x,y,radius*2,radius*2);
		g2d.draw(circle);
		g2d.setColor(prevC);
	}
	
	public static void flightinfo(Graphics g, Aircraft a){
		String s;
		Color prevC = g.getColor();
		Font prevF = g.getFont();
		g.setFont(Fonts.radartext);
		FontMetrics fm = g.getFontMetrics(Fonts.radartext);
		
		if(a.getAltDes() > a.getAltCur()){
			s = a.getAltCur() + Fonts.climb + a.getAltDes();
		}
		else if(a.getAltDes() < a.getAltCur()){
			s = a.getAltCur() + Fonts.decent + a.getAltDes();
		}
		else{
			s = a.getAltCur() + "=" + a.getAltDes();
		}
		
		if(a.isSelected())
			g.setColor(Color.yellow);
		else
			g.setColor(Color.green);

		int x = (int)a.getCoords().getX() + 5;
		int y = (int)a.getCoords().getY() + 5;
		g.drawString("FLIGHT", x, y);
		y+=fm.getAscent();
		g.drawString(s, x, y);
		y+=fm.getAscent();
		g.drawString(a.getTAS() / 10 + " " + "INSTRUCT", x, y);
		
		g.setColor(prevC);
		g.setFont(prevF);
	}

	public static void hud(Graphics g, Aircraft a){
		Graphics2D g2d = (Graphics2D) g;
		Font prevF = g2d.getFont();
		Color prevC = g2d.getColor();
		int x = 0;
		int y = 0;
		int w = 192;
		int h = 256;
		Rectangle rect = new Rectangle(x,y,w,h);
		Draw.box(g, rect, 2, Color.green, Color.black);
		
		g2d.setFont(prevF);
		g2d.setColor(prevC);
		
	}
}
