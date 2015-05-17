package atc.display;

import java.awt.Color;
import java.awt.Graphics;

public class Draw {

	public static void airport(Graphics g, int x, int y){
		int height = 4, width = 4;
		Color prev = g.getColor();
		g.setColor(Color.cyan);
		g.drawRect(x - (width / 2), y - (height / 2), 5, 5);
		g.setColor(prev);
	}
	
}
