package atc.lib;

import java.awt.Graphics;
import java.util.LinkedList;

import atc.type.TYPE;
/**
 * Manages the additions, removals, retrievals ticks and renderings of all Entities in the Game.
 * @author Matt Bangert
 *
 */
public class Handler {
	private LinkedList<Entity> e = new LinkedList<Entity>();
	private Entity entity;
	
	public void add(Entity entity){
		if(entity.type == TYPE.WINDOW_HUD)
			e.addLast(entity);
		if(entity.type == TYPE.WINDOW_INFO)
			e.addLast(entity);
		else
			e.push(entity);
	}
	
	public void tick(){
		for(int i = 0; i < e.size(); i++){
			entity = e.get(i);
			entity.tick();
		}
	}
	
	public void remove(Entity entity){
		e.remove(entity);
	}
	
	public void render(Graphics g){
		for(int i = 0; i < e.size(); i++){
			entity = e.get(i);
			entity.render(g);
		}
	}
	
	public Hud getHud(){
		for(int i = 0; i < e.size(); i++){
			if(e.get(i).type == TYPE.WINDOW_HUD)
				return (Hud)e.get(i);
		}
		return null;
	}
	
	public Entity retrieve(Coords coords){
		for(int i = 0; i < e.size(); i++){
			entity.deselect();
			entity = e.get(i);
			if(coords.getY() >= entity.getCoords().getY() - 2 && coords.getY() <= entity.getCoords().getY() + 2){
				if(coords.getX() >= entity.getCoords().getX() - 2 && coords.getX() <= entity.getCoords().getX() + 2){
					if(entity.type == TYPE.AIRCRAFT_ARRIVE){
						entity.select();
						getHud().select((Aircraft) entity);
						return entity;
					}
				}
			}
		}
		return null;
	}
	
}
