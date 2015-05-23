package atc.lib;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import atc.Game;
import atc.type.TYPE;

public class MouseInput implements MouseMotionListener, MouseListener{
	private Coords pressed;
	private Entity ent = null;
	private Handler handler;

	public MouseInput(Game game, Handler handler){
		System.out.println("New MouseInput!");
		this.handler = handler;
	};
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Move all this to mouse  Pressed
		if(arg0.getButton() == MouseEvent.BUTTON1){
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
		if(arg0.getButton() == MouseEvent.BUTTON3){
			if(ent != null && ent.type == TYPE.AIRCRAFT){
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
		if(ent != null && ent.type == TYPE.AIRCRAFT){
			if(arg0.getModifiers() == MouseEvent.META_MASK && arg0.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK){
				Coords temp = new Coords(arg0.getX(), arg0.getY());
				Entity e = handler.retrieve(temp);
				if(e != null && e.type == TYPE.FIX){
					Fix f = (Fix)e;
					pressed = temp;
					Aircraft a = (Aircraft)ent;
					a.setFix(f);
				}
			}
		}
		System.out.println(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(ent != null && ent.type == TYPE.AIRCRAFT){
			if(arg0.getModifiers() == MouseEvent.META_MASK && arg0.getButton() == MouseEvent.BUTTON3 && pressed != null){
				Aircraft a = (Aircraft)ent;
				Coords temp = new Coords(arg0.getX(), arg0.getY());
				if(Calc.distanceNM(pressed, temp) >= 1){
					a.setFixHdg((int)Calc.relativeBearing(pressed, temp));
				}
			}
		}
		System.out.println(arg0);
		pressed = null;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
			// Don't use dragged.
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
