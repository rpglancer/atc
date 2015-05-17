package atc.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import atc.lib.Aircraft;

public class Draw {

	public static void airport(Graphics g, int x, int y){
		int height = 4, width = 4;
		Color prev = g.getColor();
		g.setColor(Color.cyan);
		g.drawRect(x - (width / 2), y - (height / 2), 5, 5);
		g.setColor(prev);
	}
	public static void flightinfo(Graphics g, Aircraft a){
		Color prevC = g.getColor();
		Font prevF = g.getFont();
		g.setColor(Color.green);
		g.setFont(Fonts.radartext);
		FontMetrics fm = g.getFontMetrics(Fonts.radartext);
		int x = (int)a.getCoords().getX() + 5;
		int y = (int)a.getCoords().getY() + 5;
		g.drawString("FLIGHT", x, y);
		y+=fm.getAscent();
		String s;
		if(a.getAltDes() > a.getAltCur()){
			s = a.getAltCur() + Fonts.climb + a.getAltDes() + " " + (a.getTAS()/10);
		}
		else if(a.getAltDes() < a.getAltCur()){
			s = a.getAltCur() + Fonts.decent + a.getAltDes() + " " + (a.getTAS()/10);
		}
		else{
			s = a.getAltCur() + Fonts.level + a.getAltDes() + " " + (a.getTAS()/10);
		}
		g.drawString(s, x, y);
		y+=fm.getAscent();
		g.drawString("INSTRUCT", x, y);
		g.setColor(prevC);
		g.setFont(prevF);
	}
}
