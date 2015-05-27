package atc.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Vector;

import atc.Game;
import atc.lib.Aircraft;
import atc.lib.Airport;
import atc.lib.Calc;
import atc.lib.Convert;
import atc.lib.Coords;
import atc.type.FLIGHT;
import atc.type.SCORE;

public class Draw {
	private static Rectangle score = new Rectangle(0,Game.HUDHEIGHT, Game.HUDWIDTH, Game.INFOHEIGHT);
	
	public static void aircraft(Graphics g, Aircraft a){}

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
	
	public static void cdend(Graphics g, Aircraft a){
		if(a == null)return;
		Color prevC = g.getColor();
	//	double dist = a.getTTC() * Aircraft.PPNM;
		double dist = (a.getTTC() * 12 * a.getKPS() * Aircraft.PPNM);
		Coords fin = Calc.relativeCoords(a.getCoords(), a.getHdgCur(), dist);
		int blh = a.getHdgCur() + 90;
		int elh = a.getHdgCur() - 90;
		if(blh >= 360) blh-=360;
		if(elh < 0) elh += 360;
		Coords blc = Calc.relativeCoords(fin, blh, 0.33 * Aircraft.PPNM);
		Coords elc = Calc.relativeCoords(fin, elh, 0.33 *Aircraft.PPNM);
		line(g,blc,elc,Color.yellow);
		g.setColor(prevC);
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
		rect.setFrameFromCenter(coords.getX(), coords.getY(), coords.getX() - half, coords.getY() - half);
		
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
		Color prevC = g.getColor();
		Font prevF = g.getFont();
		g.setFont(Fonts.radartext);
		FontMetrics fm = g.getFontMetrics(Fonts.radartext);
		
		int x = (int)a.getCoords().getX() + (int)(1*Aircraft.PPNM);
		int y = (int)a.getCoords().getY();
		
		if(a.getFlight() == FLIGHT.HANDOFF_AR || a.getFlight() == FLIGHT.HANDOFF_DE ||
				a.getFlight() == FLIGHT.CRUISE || a.getFlight() == FLIGHT.TAKEOFF)
			g.setColor(Color.magenta);
		else
			g.setColor(Color.green);
		
		if(a.getFlight() != FLIGHT.ARRIVAL && a.getFlight() != FLIGHT.DEPARTURE){
			g.drawString((int)(a.getAltCur()*10) + "", x, y);
			y+=fm.getAscent();
			g.drawString(a.getTAS() + "", x, y);
		}
		else{
			String s = "";
			if(a.getAltDes() > a.getAltCur())
				s = (int)(a.getAltCur()*10) + Fonts.climb + (int)(a.getAltDes() * 10);
			else if(a.getAltDes() < a.getAltCur())
				s = (int)(a.getAltCur()*10) + Fonts.decent + (int)(a.getAltDes() * 10);
			else
				s = (int)(a.getAltCur()*10) + "=" + (int)(a.getAltDes() * 10);
			g.drawString(a.getName(), x, y);
			y+=fm.getAscent();
			g.drawString(s, x, y);
			y+=fm.getAscent();
			g.drawString(a.getTAS() + " " + a.getInstruction(), x, y);
		}
		g.setFont(prevF);
		g.setColor(prevC);
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
	
	public static void score(Graphics g, Airport a){
	Color prevC = g.getColor();
	Font prevF = g.getFont();
	Graphics2D g2d = (Graphics2D)g;
	g2d.setColor(Color.gray);
	g2d.fill(score);
	g2d.setColor(Color.black);
	g2d.setFont(Fonts.radartext);
	FontMetrics fm = g2d.getFontMetrics(Fonts.radartext);
	int midX = Game.HUDWIDTH/2;
	int y = Game.HUDHEIGHT + fm.getHeight();
	int x = 0;
//	PLYRSCORE 	(0),
//	PLYRSKILL 	(1),
//	ARRIVED 	(2),
//	LANDED		(3),
//	DEPARTED	(4),
//	CRUISEALT	(5),
//	HANDOFF		(6),
//	HANDOFFMISS	(7),
//	OUTOFSEC	(8),
//	SEPINCTIME	(9),
//	PLAYTIME	(10);
	g2d.drawString("Score: " + Airport.getScoreArray()[SCORE.PLYRSCORE.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Skill: " + Airport.getScoreArray()[SCORE.PLYRSKILL.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Arrived: " + Airport.getScoreArray()[SCORE.ARRIVED.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Landed: " + Airport.getScoreArray()[SCORE.LANDED.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Departed: " + Airport.getScoreArray()[SCORE.DEPARTED.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Cruised: " + Airport.getScoreArray()[SCORE.CRUISEALT.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Handoffs: " + Airport.getScoreArray()[SCORE.HANDOFF.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Missed Handoff: " + Airport.getScoreArray()[SCORE.HANDOFFMISS.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Left Airspace: " + Airport.getScoreArray()[SCORE.OUTOFSEC.getSID()], x, y);
	y+=fm.getHeight();
	g2d.drawString("Sep. Incident Time: " + Airport.getScoreArray()[SCORE.SEPINCTIME.getSID()], x, y);
	y+=fm.getHeight();
	int[] pt = Calc.time(Convert.ticksToSeconds(Airport.getScoreArray()[SCORE.PLAYTIME.getSID()]));
	g2d.drawString("Play Time: " + pt[0]+":"+pt[1]+":"+pt[2] ,x, y);
	y+=fm.getHeight();
	g2d.drawString("Play Time[Ticks]: " + Airport.getScoreArray()[SCORE.PLAYTIME.getSID()], x, y);
	g.setColor(prevC);
	g.setFont(prevF);
	}
}
