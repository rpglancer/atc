package atc.lib;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import atc.Game;
import atc.type.FLIGHT;
import atc.type.TYPE;

public class MouseInput implements MouseMotionListener, MouseListener{
	private static Coords pressed;
	private static Coords at;
	private Entity ent = null;
	private Handler handler;

	public MouseInput(Game game, Handler handler){
		this.handler = handler;
	};
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
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
	public void mousePressed(MouseEvent arg0){
		switch(arg0.getButton()){
		case MouseEvent.BUTTON1:
			if(arg0.getX() >= 0 && arg0.getX() <= Game.HUDWIDTH &&
			arg0.getY() >= 0 && arg0.getY() <= Game.HUDHEIGHT){
				handler.getHud().processHud(arg0);
			}
			else{
				if(ent != null) ent.deselect();
				ent = null;
				ent = handler.retrieve(new Coords(arg0.getX(),arg0.getY()));
				if(ent != null && ent.type==TYPE.AIRCRAFT){
					ent.select();
					handler.getHud().select(ent);
				}
				else if(ent != null && ent.type == TYPE.AIRPORT){
					ent.select();
					handler.getHud().select(ent);
				}
				if(ent == null){
					handler.getHud().deselect();
				}
			}
			break;
		case MouseEvent.BUTTON3:
			if(ent != null && ent.type == TYPE.AIRCRAFT){
				Aircraft a = (Aircraft)ent;
				Coords temp = new Coords(arg0.getX(), arg0.getY());
				if(arg0.getModifiers() == MouseEvent.META_MASK && arg0.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK){
					Entity e = handler.retrieve(temp);
					if(e != null && e.type == TYPE.FIX){
						Fix f = (Fix)e;
						pressed = temp;
						at = a.getCoords();
						a.setFix(f);
					}
					else{
						pressed = a.getCoords();
						at = new Coords(arg0.getX(), arg0.getY());
						a.setFix(null);
						a.setFixHdg(-1);
					}
				}
			}
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(ent != null && ent.type == TYPE.AIRCRAFT){
			Aircraft a = (Aircraft) ent;
			if(arg0.getModifiers() == MouseEvent.META_MASK && arg0.getButton() == MouseEvent.BUTTON3){
				if(handler.retrieve(pressed).type == TYPE.FIX){
					if(Calc.distanceNM(pressed, at) >= 1)
						a.setFixHdg((int)Calc.relativeBearing(pressed, at));
				}
				else{
					if(a.getFlight() == FLIGHT.ARRIVAL)
						a.setHeadingDesired((int)Calc.relativeBearing(pressed, at));
				}
			}
		}
		pressed = null;
		at = null;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		at = new Coords(arg0.getX(), arg0.getY());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public static Coords[] mouseCoords(){
		Coords[] mc = {pressed, at};
		return mc;
	}
}
