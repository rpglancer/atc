package atc.lib;

import java.awt.Graphics;

import atc.Game;
import atc.type.TYPE;

public abstract class Entity {
	/**
	 * Nautical Miles Per Pixel
	 */
//	public static double NMPP = (30 * 30) / (Game.WIDTH * Game.HEIGHT);
	public static double NMPP = Game.WIDTH / 30;
	
	protected TYPE type;
	protected Coords loc;
	
	public abstract void render(Graphics g);
	public abstract void tick();
}
