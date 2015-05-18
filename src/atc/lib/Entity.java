package atc.lib;

import java.awt.Graphics;

import atc.Game;
import atc.type.TYPE;

public abstract class Entity {
	/**
	 * Nautical Miles Per Pixel
	 */
//	public static double NMPP = (30 * 30) / (Game.WIDTH * Game.HEIGHT);
//	public static double NMPP = Game.WIDTH / 30;
	public static double NMPP = Game.GAMEWIDTH / 60;
	
	protected boolean isSelected = false;
	protected TYPE type;
	protected Coords loc;
	
	public abstract Coords getCoords();
	public abstract void deselect();
	public abstract void render(Graphics g);
	public abstract void select();
	public abstract void setCoords(Coords coords);
	public abstract void tick();
}
