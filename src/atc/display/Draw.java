package atc.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Vector;

import atc.lib.Aircraft;
import atc.lib.Airport;
import atc.lib.Coords;

public class Draw {

	public static void airport(Graphics g, Airport airport){
		int height = 4, width = 4;
		Color prev = g.getColor();
		g.setColor(Color.cyan);
		g.drawRect((int)airport.getCoords().getX() - (width / 2), (int)airport.getCoords().getY() - (height / 2), 5, 5);
		
		Draw.centeredcircle(g, airport.getCoords(), Airport.PPNM * 15, Color.darkGray);
		Draw.centeredcircle(g, airport.getCoords(), Airport.PPNM * 30, Color.darkGray);
		
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
		g2d.drawOval((int)(coords.getX() - radius), (int)(coords.getY() - radius), (int)radius *2, (int)radius * 2);
		g2d.setColor(prevC);
	}
	
	public static void centeredsquare(Graphics g, Coords coords, double half, Color color, float stroke){
		BasicStroke acs = new BasicStroke(stroke);
		Rectangle rect = new Rectangle((int)(coords.getX() - half), (int)(coords.getY() - half), (int)(half * 2), (int)(half * 2));
		
		Graphics2D g2d = (Graphics2D) g;
		Color prevC = g2d.getColor();
		BasicStroke prevS = (BasicStroke)g2d.getStroke();
		
		g2d.setColor(color);
		g2d.setStroke(acs);
		g2d.draw(rect);
		
		g2d.setColor(prevC);
		g2d.setStroke(prevS);
	}
	
	public static void flightinfo(Graphics g, Aircraft a){
		String s;
		Color prevC = g.getColor();
		Font prevF = g.getFont();
		g.setFont(Fonts.radartext);
		FontMetrics fm = g.getFontMetrics(Fonts.radartext);
		
		if(a.getAltDes() > a.getAltCur()){
			s = (int)(a.getAltCur()*10) + Fonts.climb + (int)(a.getAltDes() * 10);
		}
		else if(a.getAltDes() < a.getAltCur()){
			s = (int)(a.getAltCur()*10) + Fonts.decent + (int)(a.getAltDes() * 10);
		}
		else{
			s = (int)(a.getAltCur()*10) + "=" + (int)(a.getAltDes() * 10);
		}
		
		if(a.isSelected())
			g.setColor(Color.yellow);
		else
			g.setColor(Color.green);

		int x = (int)a.getCoords().getX() + (int)(1*Aircraft.PPNM);
		int y = (int)a.getCoords().getY() + (int)(1*Aircraft.PPNM);
		g.drawString(a.getName(), x, y);
		y+=fm.getAscent();
		g.drawString(s, x, y);
		y+=fm.getAscent();
		g.drawString(a.getTAS() / 10 + " " + a.getInstruction(), x, y);
		
		g.setColor(prevC);
		g.setFont(prevF);
	}

	public static void history(Graphics g, Vector<Coords>history){
		Color prevC = g.getColor();
		Font prevF = g.getFont();
		g.setColor(Color.yellow);
		for(int i = 0; i < history.size(); i++){
			Coords temp = history.elementAt(i);
			if(i%3 == 0)
				g.setColor(g.getColor().darker());
			centeredcircle(g, temp, Aircraft.PPNM * 0.125, g.getColor());
		}
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
	
	public static void line(Graphics g, Coords begin, Coords end, Color color){
		Color prevC = g.getColor();
		g.setColor(color);
		g.drawLine((int)begin.getX(), (int)begin.getY(), (int)end.getX(), (int)end.getY());
		g.setColor(prevC);
		
	}
}
