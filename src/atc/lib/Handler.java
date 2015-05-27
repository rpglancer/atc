package atc.lib;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Vector;

import atc.type.FLIGHT;
import atc.type.TYPE;
/**
 * Manages the additions, removals, retrievals, ticks and renderings of all Entities in the Game.
 * @author Matt Bangert
 *
 */
public class Handler {
	private LinkedList<Entity> e = new LinkedList<Entity>();
	private static Vector<Localizer> localizers = new Vector<Localizer>();
	private Entity entity;
	
	public void add(Entity entity){
		if(entity.type == TYPE.LOCALIZER){
			localizers.addElement((Localizer)entity);
		}
		if(entity.type == TYPE.WINDOW_HUD){
			e.add((e.size()), entity);
		}
		else if(entity.type == TYPE.RUNWAY_ARRIVE){
			e.add(seekLastIndex(TYPE.RUNWAY_ARRIVE)+1, entity);
		}
		else if(entity.type == TYPE.RUNWAY_DEPART){
			e.add(seekLastIndex(TYPE.RUNWAY_DEPART)+1, entity);
		}
		else if(entity.type == TYPE.FIX){
			int fix = -1;
			for(int i = 0; i < e.size(); i++){
				if(e.get(i).type == TYPE.LOCALIZER || e.get(i).type == TYPE.RUNWAY_ARRIVE || e.get(i).type == TYPE.RUNWAY_DEPART)
					fix = i;
			}
			e.add(fix + 1, entity);
		}
		else if(entity.type == TYPE.WINDOW_INFO){
			e.addLast(entity);
		}
		else if(entity.type == TYPE.AIRCRAFT){
			int fix = -1;
			for(int i = 0; i < e.size(); i++){
				if(e.get(i).type == TYPE.FIX)
					fix = i;
			}
			e.add(fix+1, entity);
		}
		else{
			e.push(entity);
		}
	}
	
	private int seekFirstIndex(TYPE type){
		for(int i = 0; i < e.size(); i++){
			if(e.get(i).type == type){
				return i;
			}
		}
		return -1;
	}
	
	private int seekLastIndex(TYPE type){
		int index = -1;
		for(int i = 0; i < e.size(); i++){
			if(e.get(i).type == type)
				index = i;
		}
		return index;
	}
	
	public void tick(){
		for(int i = 0; i < e.size(); i++){
			e.get(i).tick();
		}
	}
	
	public void remove(Entity entity){
		e.remove(entity);
	}
	
	/**
	 * Renders all entites currently registered with the Handler.
	 * @param g	Graphics context.
	 */
	public void render(Graphics g){
		for(int i = 0; i < e.size(); i++){
			e.get(i).render(g);
		}
	}
	
	public Hud getHud(){
		for(int i = 0; i < e.size(); i++){
			if(e.get(i).type == TYPE.WINDOW_HUD)
				return (Hud)e.get(i);
		}
		return null;
	}
	
	public static Vector<Localizer> getLocalizers(){
		return localizers;
	}
	
	public Entity retrieve(Coords coords){
		for(int i = 0; i < e.size(); i++){
			entity = e.get(i);
			if(entity.getCoords() == null || entity.getArea() == null)
				continue;
			else{
				switch(entity.type){
				case AIRCRAFT:		
					if(coords.getX() >= entity.getArea().getMinX() && coords.getX() <= entity.getArea().getMaxX() &&
					coords.getY() >= entity.getArea().getMinY() && coords.getY() <= entity.getArea().getMaxY()){
						Aircraft temp = (Aircraft)entity;
						if(temp.getFlight() == FLIGHT.ARRIVAL || temp.getFlight() == FLIGHT.HANDOFF_AR || 
								temp.getFlight() == FLIGHT.HANDOFF_DE || temp.getFlight() == FLIGHT.DEPARTURE){
							entity.select();
							getHud().select((Aircraft) entity);
							return entity;
						}
					}
				case FIX:
					if(coords.getX() >= entity.getArea().getMinX() && coords.getX() <= entity.getArea().getMaxX()
					&& coords.getY() >= entity.getArea().getMinY() && coords.getY() <= entity.getArea().getMaxY()){
						return entity;
					}
					
				default:
					if(entity != null){
					//	getHud().deselect();
					}
						
					continue;			
				}

			}
		}
		return null;
	}
}
