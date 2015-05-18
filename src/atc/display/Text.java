package atc.display;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import atc.type.HALIGN;
import atc.type.VALIGN;

public class Text {
	private static void alignText(Graphics g, Font f, int x, int y, int w, int h, HALIGN ha, String text){
		FontMetrics fm = g.getFontMetrics(f);
		int strlen = fm.stringWidth(text);
		if(ha == HALIGN.CENTER) x = (x + w) - (w / 2) - (strlen / 2);
		if(ha == HALIGN.RIGHT) x = x + w - strlen;
//		y+= fm.getAscent() - fm.getDescent();
		Font tempf = g.getFont();
		g.setFont(f);
		g.drawString(text, x, y);
		g.setFont(tempf);
	}

	public static void boxText(Graphics g, Font f, Rectangle box, HALIGN ha, VALIGN va, String text){
		int lc = lineCount(g, f, box.x + box.width - box.x, text);
		FontMetrics fm = g.getFontMetrics(f);
		int y = box.y;
		int w = box.width;
		if(va == VALIGN.TOP) y = box.y;
		if(va == VALIGN.MIDDLE){
			if(lc == 1) y += (box.height - textHeight(g,f,text)) / 2 + fm.getAscent();
			else y = box.y + box.height/2 - (textHeight(g,f,text)/2) * lc + (fm.getDescent()/2 * lc);
		}
		if(va == VALIGN.BOTTOM) y = box.y + box.height - textHeight(g,f,text) * lc + (fm.getDescent() * lc);
		String[] words = text.split(" ");
		String currentLine = words[0];
		for(int i = 1; i < words.length; i++){
			if(fm.stringWidth(currentLine + " " + words[i]) < box.x + box.width - box.x){
				currentLine += " " + words[i];
			}
			else{
				alignText(g, f, (int)box.getX(), y, w, fm.getHeight(), ha, currentLine);
				y += fm.getAscent();
				currentLine = words[i];
			}
		}
		if(currentLine.trim().length() > 0){
			alignText(g,f, (int)box.getX(), y, w, fm.getHeight(), ha, currentLine);
		}
	}
	
	private static int textHeight(Graphics g, Font f, String text){
		Graphics temp = g.create(0, 0, 800, 480);
		FontMetrics m = g.getFontMetrics(f);
		temp.clipRect(0, 0, m.stringWidth(text), m.getHeight());
		Rectangle2D bounds = m.getStringBounds(text, temp);
		int theight = (int)bounds.getHeight();
		temp.dispose();
		return theight;
	}
	
	private static int textWidth(Graphics g, Font f, String text){
		Graphics temp = g.create(0, 0, 800, 480);
		FontMetrics m = g.getFontMetrics(f);
		temp.clipRect(0, 0, m.stringWidth(text), m.getHeight());
		Rectangle2D bounds = m.getStringBounds(text, temp);
		int twidth = (int)bounds.getWidth();
		temp.dispose();
		return twidth;
	}
	
	private static int lineCount(Graphics g, Font f, int lineWidth, String text){
		int count = 1;
		FontMetrics m = g.getFontMetrics(f);
		if(m.stringWidth(text) < lineWidth) return count;
		else{
			String[] words = text.split(" ");
			String currentLine = words[0];
			for(int i = 1; i < words.length; i++){
				if(m.stringWidth(currentLine + " " + words[i]) < lineWidth){
					currentLine += " " + words[i];
				}
				else{
					count++;
					currentLine = words[i];
				}
			}
			if(currentLine.trim().length() > 0){
				
			}
		}
		return count;
	}
	
}
