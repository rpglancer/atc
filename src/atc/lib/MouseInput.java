package atc.lib;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import atc.Game;
import atc.type.TYPE;

public class MouseInput implements MouseMotionListener, MouseListener{
	private Entity ent = null;
	private Game game;
	private Handler handler;

	public MouseInput(Game game, Handler handler){
		this.game = game;
		this.handler = handler;
	};
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getButton() == arg0.BUTTON1){
			Coords coords = new Coords(arg0.getX(), arg0.getY());
			if(arg0.getX() >= 0 && arg0.getX() <= Game.HUDWIDTH){
				if(arg0.getY() >= 0 && arg0.getY() <= Game.HUDHEIGHT){
					System.out.println("Process HUD!");
					handler.getHud().processHud(arg0);
				}
			}
			else{
				ent = handler.retrieve(coords);
				System.out.println(ent);
			}
		}
		if(arg0.getButton() == arg0.BUTTON3){
			if(ent != null && ent.type == TYPE.AIRCRAFT_ARRIVE){
				Aircraft a = (Aircraft)ent;
				Coords coords = new Coords(arg0.getX(), arg0.getY());
				a.setHeadingDesired(coords);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
