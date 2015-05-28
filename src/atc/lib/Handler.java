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
			Entity ent = e.get(i);
			if(ent.getCoords() == null || ent.getArea() == null)
				continue;
			else{
				switch(ent.type){
				case AIRCRAFT:		
					if(coords.getX() >= ent.getArea().getMinX() && coords.getX() <= ent.getArea().getMaxX() &&
					coords.getY() >= ent.getArea().getMinY() && coords.getY() <= ent.getArea().getMaxY()){
						Aircraft a = (Aircraft)ent;
						if(a.getFlight() == FLIGHT.ARRIVAL || a.getFlight() == FLIGHT.HANDOFF_AR || 
								a.getFlight() == FLIGHT.HANDOFF_DE || a.getFlight() == FLIGHT.DEPARTURE){
				//			ent.select();
				//			getHud().select((Aircraft) ent);
							return ent;
						}
					}
					break;
				case FIX:
					Fix f = (Fix)ent;
					if(coords.getX() >= ent.getArea().getMinX() && coords.getX() <= ent.getArea().getMaxX()
					&& coords.getY() >= ent.getArea().getMinY() && coords.getY() <= ent.getArea().getMaxY()){
						return f;
					}
					break;
				default:
					continue;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void listAircraft(){
		for(int i = 0; i < e.size(); i++){
			if(e.get(i).type == TYPE.AIRCRAFT){
				Aircraft a = (Aircraft)e.get(i);
				System.out.println(a + " - " + a.getFlight());
			}
		}
	}
}
