package atc.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import atc.Game;
import atc.display.Draw;
import atc.display.Fonts;
import atc.display.Text;
import atc.type.FLIGHT;
import atc.type.HALIGN;
import atc.type.TYPE;
import atc.type.VALIGN;

public class Hud extends Entity{
	
	private static final int btnHeight = 32;
	private static final int btnWidth = 32;
	private static final int boxHeight = 32;
	private static final int boxWidth = atc.Game.HUDWIDTH/3;
	private static final int height = atc.Game.HUDHEIGHT;
	private static final int width = atc.Game.HUDWIDTH;
	
	private int selectedSpeed;
	private int selectedHdg;
	private double selectedAlt;
	
//	private Entity ent = null;
	
	private static Rectangle altBox = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), btnHeight/2, boxWidth, boxHeight);
	private static Rectangle spdBox = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)altBox.getMaxY() + 16, boxWidth, boxHeight);
	private static Rectangle hdgBox = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)spdBox.getMaxY() + 16, boxWidth, boxHeight);
	
	private static Rectangle incAlt = new Rectangle((int)(altBox.getX() + altBox.getWidth() + 8), (int)altBox.getY(), btnWidth, btnHeight);
	private static Rectangle incSpd = new Rectangle((int)(spdBox.getX() + spdBox.getWidth() + 8), (int)spdBox.getY(), btnWidth, btnHeight);;
	private static Rectangle incHdg = new Rectangle((int)(hdgBox.getX() + hdgBox.getWidth() + 8), (int)hdgBox.getY(), btnWidth, btnHeight);;
	
	private static Rectangle decAlt = new Rectangle((int)altBox.getX() - 40, (int)altBox.getY(), btnWidth, btnHeight);
	private static Rectangle decSpd = new Rectangle((int)spdBox.getX() - 40, (int)spdBox.getY(), btnWidth, btnHeight);
	private static Rectangle decHdg = new Rectangle((int)hdgBox.getX() - 40, (int)hdgBox.getY(), btnWidth, btnHeight);
	
	private static Rectangle selILS =	new Rectangle((int)altBox.getX() - 40, (int)hdgBox.getMaxY() + 16, 64, 32);
	private static Rectangle selHld =	new Rectangle((int)selILS.getMaxX() + 16, (int)hdgBox.getMaxY() + 16, 64, 32);
	private static Rectangle confirm =	new Rectangle((int)altBox.getX() - 40, (int)selILS.getMaxY() + 16, 64, 32);
	private static Rectangle cancel =	new Rectangle((int)confirm.getMaxX() + 16, (int)selILS.getMaxY() + 16, 64, 32);
	
	private static Rectangle hndoff =	new Rectangle(24, Game.HUDHEIGHT / 2 - boxHeight / 2, Game.HUDWIDTH - 48, boxHeight);
	
	private static Rectangle delivr0 = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), btnHeight/2, boxWidth, boxHeight);
	private static Rectangle delivr1 = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)delivr0.getMaxY() + 16, boxWidth, boxHeight);
	private static Rectangle delivr2 = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)delivr1.getMaxY() + 16, boxWidth, boxHeight);
	private static Rectangle delivr3 = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)delivr2.getMaxY() + 16, boxWidth, boxHeight);
	private static Rectangle delivr4 = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)delivr3.getMaxY() + 16, boxWidth, boxHeight);

	
	public Hud(){
		loc = new Coords(0,0);
		type = TYPE.WINDOW_HUD;
	}

	@Override
	public Coords getCoords() {
		return loc;
	}

	@Override
	public void deselect() {
//		if(ent != null){
//			ent.deselect();
//			ent = null;
//		}
		if(MouseInput.getEntity() != null){
			MouseInput.getEntity().deselect();
			MouseInput.setEntity(null);
		}
		selectedSpeed = 0;
		selectedAlt = 0;
		selectedHdg = 0;
		isSelected = false;
	}
	
	public void processHud(MouseEvent arg0){
		if(isSelected && MouseInput.getEntity() != null){
//		if(isSelected && ent != null){
			switch(MouseInput.getEntity().type){
//			switch(ent.type){
			case AIRCRAFT:
				aircraftHud(arg0);
				break;
			case AIRPORT:
				airportHud(arg0);
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void render(Graphics g){
		Entity temp = MouseInput.getEntity();
//		Entity temp = ent;
		if(temp == null) deselect();
		Font prevF = g.getFont();
		Color prevC = g.getColor();
		Rectangle rect = new Rectangle(0,0,width,height);
		if(isSelected){
			Draw.box(g, rect, 2, Color.green, Color.black);
			switch(temp.type){
			case AIRCRAFT:
				renderAircraftHUD(g, temp);
				break;
			case AIRPORT:
				renderAirportHUD(g, temp);
				break;
			default:
				break;
			}
		}
		else{
			Draw.box(g, rect, 1, Color.gray, Color.gray);
		}
		g.setColor(prevC);
		g.setFont(prevF);
	}

//	public void select(Aircraft a) {
//		if(a!=null){
//			aircraft = a;
//			selectedSpeed = a.getKIASDes();
//			selectedAlt = a.getAltDes();
//			selectedHdg = a.getHdgDes();
//			isSelected = true;
//		}
//	}
	
	public void select(Entity ent){
		if(ent != null){
//			this.ent = ent;
//			switch(this.ent.type){
			switch(ent.type){
			case AIRCRAFT:
				Aircraft a = (Aircraft)ent;
				selectedSpeed = a.getKIASDes();
				selectedAlt = a.getAltDes();
				selectedHdg = a.getHdgDes();
				isSelected = true;
				break;
			default:
				isSelected = true;
				break;
			}
		}
	}
	
	@Override
	public void setCoords(Coords coords) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(){
//		if(isSelected && ent == null)
		if(isSelected && MouseInput.getEntity() == null)
			deselect();
//		if(isSelected && !ent.isSelected)
		if(isSelected && !MouseInput.getEntity().isSelected)
			deselect();
//		if(isSelected && ent.type == TYPE.AIRCRAFT && ent.isSelected){
		if(isSelected && MouseInput.getEntity().type == TYPE.AIRCRAFT && MouseInput.getEntity().isSelected){
//			Aircraft temp = (Aircraft)ent;
			Aircraft temp = (Aircraft)MouseInput.getEntity();
			if(selectedHdg != temp.getHdgDes()){
				selectedHdg = temp.getHdgDes();
			}
		}
	}

	@Override
	public void select() {		
	}
	
	private void adjAlt(double alt){
		if(alt < 0){
			if(selectedAlt > 1.0)
				selectedAlt += alt;
		}
		else{
			if(selectedAlt < 18)
				selectedAlt += alt;
		}
	}
	private void adjSpd(int spd){
		if(spd < 0){
			if(selectedSpeed > 140)
				selectedSpeed += spd;	
		}
		else{
			if(selectedSpeed < 260)
				selectedSpeed += spd;
		}
	}	
	private void adjHdg(int hdg){
		if(hdg < 0){
			selectedHdg += hdg;
			if(selectedHdg < 0)
				selectedHdg += 360;
		}
		else{
			selectedHdg += hdg;
			if(selectedHdg > 359)
				selectedHdg -= 360;
		}
	}

	private void airportHud(MouseEvent arg0){
		int mouseX = arg0.getX();
		int mouseY = arg0.getY();
//		Airport a = (Airport)ent;
		Airport a = (Airport)MouseInput.getEntity();
		int vs = a.getDeliveries().size();
		if(a.getDeliveries().size() == 0){
			if(mouseX >= hndoff.getMinX() && mouseX <= hndoff.getMaxX() && mouseY >= hndoff.getMinY() && mouseY <= hndoff.getMaxY()){
				deselect();
			}
		}
		if(vs >= 1){
			if(mouseX >= delivr0.getMinX() && mouseX <= delivr0.getMaxX() && mouseY >= delivr0.getMinY() && mouseY <= delivr0.getMaxY()){
				a.releaseFlight(0);
				deselect();
			}
		}
		if(vs >= 2){
			if(mouseX >= delivr1.getMinX() && mouseX <= delivr1.getMaxX() && mouseY >= delivr1.getMinY() && mouseY <= delivr1.getMaxY()){
				a.releaseFlight(1);
				deselect();
			}
		}
		if(vs >= 3){
			if(mouseX >= delivr2.getMinX() && mouseX <= delivr2.getMaxX() && mouseY >= delivr2.getMinY() && mouseY <= delivr2.getMaxY()){
				a.releaseFlight(2);
				deselect();
			}
		}
		if(vs >= 4){
			if(mouseX >= delivr3.getMinX() && mouseX <= delivr3.getMaxX() && mouseY >= delivr3.getMinY() && mouseY <= delivr3.getMaxY()){
				a.releaseFlight(3);
				deselect();
			}
		}
		if(vs >= 5){
			if(mouseX >= delivr4.getMinX() && mouseX <= delivr4.getMaxX() && mouseY >= delivr4.getMinY() && mouseY <= delivr4.getMaxY()){
				a.releaseFlight(4);
				deselect();
			}
		}
	}

	private void aircraftHud(MouseEvent arg0){
		int mouseX = arg0.getX();
		int mouseY = arg0.getY();
//		Aircraft aircraft = (Aircraft)ent;
		Aircraft aircraft = (Aircraft)MouseInput.getEntity();
		switch(aircraft.getFlight()){
		case ARRIVAL:
			if(mouseX >= decAlt.getMinX() && mouseX <= decAlt.getMaxX() && mouseY >= decAlt.getMinY() && mouseY <= decAlt.getMaxY()){
				adjAlt(-0.5);
			}
			else if(mouseX >= incAlt.getMinX() && mouseX <= incAlt.getMaxX() && mouseY >= incAlt.getMinY() && mouseY <= incAlt.getMaxY()){
				adjAlt(0.5);
			}
			else if(mouseX >= decSpd.getMinX() && mouseX <= decSpd.getMaxX() && mouseY >= decSpd.getMinY() && mouseY <= decSpd.getMaxY()){
				adjSpd(-10);
			}
			else if(mouseX >= incSpd.getMinX() && mouseX <= incSpd.getMaxX() && mouseY >= incSpd.getMinY() && mouseY <= incSpd.getMaxY()){
				adjSpd(10);
			}
			else if(mouseX >= decHdg.getMinX() && mouseX <= decHdg.getMaxX() && mouseY >= decHdg.getMinY() && mouseY <= decHdg.getMaxY()){
				adjHdg(-5);
			}
			else if(mouseX >= incHdg.getMinX() && mouseX <= incHdg.getMaxX() && mouseY >= incHdg.getMinY() && mouseY <= incHdg.getMaxY()){
				adjHdg(5);
			}
			else if(mouseX >= selILS.getMinX() && mouseX <= selILS.getMaxX() && mouseY >= selILS.getMinY() && mouseY <= selILS.getMaxY()){
				aircraft.toggleILS();
			}
			else if(mouseX >= selHld.getMinX() && mouseX <= selHld.getMaxX() && mouseY >= selHld.getMinY() && mouseY <= selHld.getMaxY()){
				aircraft.toggleHold();
			}
			else if(mouseX >= confirm.getMinX() && mouseX <= confirm.getMaxX() && mouseY >= confirm.getMinX() && mouseY <= confirm.getMaxY()){
				apply();
			}
			else if(mouseX >= cancel.getMinX() && mouseX <= cancel.getMaxX() && mouseY >= cancel.getMinY() && mouseY <= cancel.getMaxY()){
				deselect();
			}
			break;
			
		case DEPARTURE:
			if(mouseX >= decAlt.getMinX() && mouseX <= decAlt.getMaxX() && mouseY >= decAlt.getMinY() && mouseY <= decAlt.getMaxY()){
				adjAlt(-0.5);
			}
			else if(mouseX >= incAlt.getMinX() && mouseX <= incAlt.getMaxX() && mouseY >= incAlt.getMinY() && mouseY <= incAlt.getMaxY()){
				adjAlt(0.5);
			}
			else if(mouseX >= decSpd.getMinX() && mouseX <= decSpd.getMaxX() && mouseY >= decSpd.getMinY() && mouseY <= decSpd.getMaxY()){
				adjSpd(-10);
			}
			else if(mouseX >= incSpd.getMinX() && mouseX <= incSpd.getMaxX() && mouseY >= incSpd.getMinY() && mouseY <= incSpd.getMaxY()){
				adjSpd(10);
			}
			else if(mouseX >= confirm.getMinX() && mouseX <= confirm.getMaxX() && mouseY >= confirm.getMinX() && mouseY <= confirm.getMaxY()){
				apply();
			}
			else if(mouseX >= cancel.getMinX() && mouseX <= cancel.getMaxX() && mouseY >= cancel.getMinY() && mouseY <= cancel.getMaxY()){
				deselect();
			}
			break;
			
		case HANDOFF_AR:
			if(mouseX >= hndoff.getMinX() && mouseX <= hndoff.getMaxX() && mouseY >= hndoff.getMinY() && mouseY <= hndoff.getMaxY()){
				aircraft.contact();
			}
			else if(mouseX >= cancel.getMinX() && mouseX <= cancel.getMaxX() && mouseY >= cancel.getMinY() && mouseY <= cancel.getMaxY()){
				deselect();
			}
			break;
			
		case HANDOFF_DE:
			if(mouseX >= hndoff.getMinX() && mouseX <= hndoff.getMaxX() && mouseY >= hndoff.getMinY() && mouseY <= hndoff.getMaxY()){
				aircraft.contact();
			}
			else if(mouseX >= cancel.getMinX() && mouseX <= cancel.getMaxX() && mouseY >= cancel.getMinY() && mouseY <= cancel.getMaxY()){
				deselect();
			}
			break;
			
		default:
			break;
		}
	}
	
	private void apply(){
//		switch(ent.type){
		switch(MouseInput.getEntity().type){
		case AIRCRAFT:
//			Aircraft a = (Aircraft)ent;
			Aircraft a = (Aircraft)MouseInput.getEntity();
			a.setVelocityDesired(selectedSpeed);
			a.setHeadingDesired(selectedHdg);
			a.setAltitudeDesired(selectedAlt);
			break;
		case AIRPORT:
			break;
		default:
			break;
		}
		deselect();
	}
	
	private void renderAircraftHUD(Graphics g, Entity ent){
		if(ent.type != TYPE.AIRCRAFT) return;
		Aircraft temp = (Aircraft) ent;
		Graphics2D g2d = (Graphics2D)g;
		if(temp.getFlight() == FLIGHT.HANDOFF_AR || temp.getFlight() == FLIGHT.HANDOFF_DE){
			Draw.box(g, hndoff, 2, Color.green, Color.black);
			g.setColor(Color.green);
			Text.boxText(g, Fonts.hudNumber, hndoff, HALIGN.CENTER, VALIGN.MIDDLE, "CONTACT");
			Draw.box(g, cancel, 2, Color.green, Color.black);
			g.setColor(Color.green);
			Text.boxText(g, Fonts.hudNumber, cancel, HALIGN.CENTER, VALIGN.MIDDLE, "X");
			return;
		}
		
		g2d.setColor(Color.cyan);
		g2d.setFont(Fonts.hudNumber);
		
		Draw.box(g, decAlt, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, decAlt, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.decent);
		Draw.box(g, altBox, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, altBox, HALIGN.CENTER, VALIGN.MIDDLE, "FL"+(int)(selectedAlt * 10));
		Draw.box(g, incAlt, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, incAlt, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.climb);
		
		Draw.box(g, decSpd, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, decSpd, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.decent);
		Draw.box(g, spdBox, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, spdBox, HALIGN.CENTER, VALIGN.MIDDLE, selectedSpeed + "");
		Draw.box(g, incSpd, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, incSpd, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.climb);
		
		Draw.box(g, decHdg, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, decHdg, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.decent);
		Draw.box(g, hdgBox, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, hdgBox, HALIGN.CENTER, VALIGN.MIDDLE, selectedHdg + "");
		Draw.box(g, incHdg, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, incHdg, HALIGN.CENTER, VALIGN.MIDDLE, Fonts.climb);
		
		if(temp.getFlight() == FLIGHT.DEPARTURE){
			Draw.box(g, selILS, 2, Color.red, Color.darkGray);
			Text.boxText(g, Fonts.hudNumber, selILS, HALIGN.CENTER, VALIGN.MIDDLE, "ILS");
			
			Draw.box(g, selHld, 2, Color.red, Color.darkGray);
			Text.boxText(g, Fonts.hudNumber, selHld, HALIGN.CENTER, VALIGN.MIDDLE, "HOLD");
		}
		
		if(temp.getFlight() == FLIGHT.ARRIVAL){
			if(temp.isClearILS()){
				Draw.box(g, selILS, 2, Color.green, Color.green);
				g.setColor(Color.black);
				Text.boxText(g, Fonts.hudNumber, selILS, HALIGN.CENTER, VALIGN.MIDDLE, "ILS");
				g.setColor(Color.cyan);
			}
			else{
				Draw.box(g, selILS, 2, Color.green, Color.black);
				Text.boxText(g, Fonts.hudNumber, selILS, HALIGN.CENTER, VALIGN.MIDDLE, "ILS");
			}
			if(temp.isHolding()){
				Draw.box(g, selHld, 2, Color.green, Color.green);
				g.setColor(Color.black);
				Text.boxText(g, Fonts.hudNumber, selHld, HALIGN.CENTER, VALIGN.MIDDLE, "HOLD");
				g.setColor(Color.cyan);
			}
			else{
				Draw.box(g, selHld, 2, Color.green, Color.black);
				Text.boxText(g, Fonts.hudNumber, selHld, HALIGN.CENTER, VALIGN.MIDDLE, "HOLD");
			}
		}
		
		Draw.box(g, confirm, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, confirm, HALIGN.CENTER, VALIGN.MIDDLE, "OK");
		
		Draw.box(g, cancel, 2, Color.green, Color.black);
		Text.boxText(g, Fonts.hudNumber, cancel, HALIGN.CENTER, VALIGN.MIDDLE, "X");	
	}
	
	private void renderAirportHUD(Graphics g, Entity temp){
//		if(ent == null || ent.type != TYPE.AIRPORT)
		if(MouseInput.getEntity() == null || MouseInput.getEntity().type != TYPE.AIRPORT)
			return;
//		Airport a = (Airport)ent;
		Airport a = (Airport)MouseInput.getEntity();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.cyan);
		g2d.setFont(Fonts.hudNumber);
		if(a.getDeliveries().size() == 0){
			Draw.box(g2d, hndoff, 2, Color.green, Color.black);
			Text.boxText(g2d, g2d.getFont(), hndoff, HALIGN.CENTER, VALIGN.MIDDLE, "NO PENDING");
			return;
		}
		if(a.getDeliveries().size() >= 1){
			Draw.box(g2d, delivr0, 2, Color.green, Color.black);
			Text.boxText(g2d, Fonts.delivtext, delivr0, HALIGN.CENTER, VALIGN.TOP, a.getDeliveries().elementAt(0).getName());
			Text.boxText(g2d, Fonts.delivtext, delivr0, HALIGN.CENTER, VALIGN.BOTTOM, a.getDeliveries().elementAt(0).getFix().getID());
		}
		if(a.getDeliveries().size() >= 2){
			Draw.box(g2d, delivr1, 2, Color.green, Color.black);
			Text.boxText(g2d, Fonts.delivtext, delivr1, HALIGN.CENTER, VALIGN.TOP, a.getDeliveries().elementAt(1).getName());
			Text.boxText(g2d, Fonts.delivtext, delivr1, HALIGN.CENTER, VALIGN.BOTTOM, a.getDeliveries().elementAt(1).getFix().getID());
		}
		if(a.getDeliveries().size() >= 3){
			Draw.box(g2d, delivr2, 2, Color.green, Color.black);
			Text.boxText(g2d, Fonts.delivtext, delivr2, HALIGN.CENTER, VALIGN.TOP, a.getDeliveries().elementAt(2).getName());
			Text.boxText(g2d, Fonts.delivtext, delivr2, HALIGN.CENTER, VALIGN.BOTTOM, a.getDeliveries().elementAt(2).getFix().getID());
		}
		if(a.getDeliveries().size() >= 4){
			Draw.box(g2d, delivr3, 2, Color.green, Color.black);
			Text.boxText(g2d, Fonts.delivtext, delivr3, HALIGN.CENTER, VALIGN.TOP, a.getDeliveries().elementAt(3).getName());
			Text.boxText(g2d, Fonts.delivtext, delivr3, HALIGN.CENTER, VALIGN.BOTTOM, a.getDeliveries().elementAt(3).getFix().getID());
		}
		if(a.getDeliveries().size() >= 5){
			Draw.box(g2d, delivr4, 2, Color.green, Color.black);
			Text.boxText(g2d, Fonts.delivtext, delivr4, HALIGN.CENTER, VALIGN.TOP, a.getDeliveries().elementAt(4).getName());
			Text.boxText(g2d, Fonts.delivtext, delivr4, HALIGN.CENTER, VALIGN.BOTTOM, a.getDeliveries().elementAt(4).getFix().getID());
		}
	}
	
	@Override
	public Rectangle getArea() {
		return area;
	}
	
}
