package atc.lib;

import java.awt.Graphics;
import java.util.LinkedList;

public class Handler {
	private LinkedList<Entity> e = new LinkedList<Entity>();
	private Entity entity;
	
	public void add(Entity entity){
		e.add(entity);
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
	
}
