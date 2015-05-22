package atc.lib;

import java.awt.Graphics;
import java.awt.Rectangle;

import atc.Game;
import atc.type.TYPE;

/**
 * Abstract class from which any class that needs to tick, render, be selected or processed 
 * is extended.
 * @author Matt Bangert
 *
 */
public abstract class Entity {
	/**
	 * <b>P</b>ixels <b>P</b>er <b>N</b>autical <b>M</b>ile.
	 */
	public static double PPNM = Game.GAMEWIDTH / Game.sectorSize;
	
	protected boolean isSelected = false;
	protected TYPE type;
	protected Rectangle area = null;
	protected Coords loc = null;
	

	public abstract Rectangle getArea();
	/**
	 * {@link Handler} depends upon this method for determining where things are. When extending
	 * this class be absolutely <b><u>/certain/</u></b> that this method is implemented in
	 * such a way that it does not return null. In the event you created a new class that
	 * extends Entity and Handler takes a dump every time you try to click on it, the reason
	 * is because getCoords() is returning null. Guaranteed.
	 * @return	The coordinates of this Entity.
	 */
	public abstract Coords getCoords();
	public abstract void deselect();
	public abstract void render(Graphics g);
	public abstract void select();
	public abstract void setCoords(Coords coords);
	public abstract void tick();
}
