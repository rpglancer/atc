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
	
	private Aircraft aircraft = null;
	
	private Rectangle altBox = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), btnHeight/2, boxWidth, boxHeight);
	private Rectangle spdBox = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)altBox.getMaxY() + 16, boxWidth, boxHeight);
	private Rectangle hdgBox = new Rectangle(0 + (atc.Game.HUDWIDTH / 2) - (boxWidth/2), (int)spdBox.getMaxY() + 16, boxWidth, boxHeight);
	
	private Rectangle incAlt = new Rectangle((int)(altBox.getX() + altBox.getWidth() + 8), (int)altBox.getY(), btnWidth, btnHeight);
	private Rectangle incSpd = new Rectangle((int)(spdBox.getX() + spdBox.getWidth() + 8), (int)spdBox.getY(), btnWidth, btnHeight);;
	private Rectangle incHdg = new Rectangle((int)(hdgBox.getX() + hdgBox.getWidth() + 8), (int)hdgBox.getY(), btnWidth, btnHeight);;
	
	private Rectangle decAlt = new Rectangle((int)altBox.getX() - 40, (int)altBox.getY(), btnWidth, btnHeight);
	private Rectangle decSpd = new Rectangle((int)spdBox.getX() - 40, (int)spdBox.getY(), btnWidth, btnHeight);
	private Rectangle decHdg = new Rectangle((int)hdgBox.getX() - 40, (int)hdgBox.getY(), btnWidth, btnHeight);
	
	private Rectangle selILS =	new Rectangle((int)altBox.getX() - 40, (int)hdgBox.getMaxY() + 16, 64, 32);
	private Rectangle selHld =	new Rectangle((int)selILS.getMaxX() + 16, (int)hdgBox.getMaxY() + 16, 64, 32);
	private Rectangle confirm =	new Rectangle((int)altBox.getX() - 40, (int)selILS.getMaxY() + 16, 64, 32);
	private Rectangle cancel =	new Rectangle((int)confirm.getMaxX() + 16, (int)selILS.getMaxY() + 16, 64, 32);
	
	private Rectangle hndoff =	new Rectangle(24, Game.HUDHEIGHT / 2 - boxHeight / 2, Game.HUDWIDTH - 48, boxHeight);

	
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
		if(aircraft != null){
			aircraft.deselect();
			aircraft = null;
		}
		selectedSpeed = 0;
		selectedAlt = 0;
		selectedHdg = 0;
		isSelected = false;
	}
	
	public void processHud(MouseEvent arg0){
		int mouseX = arg0.getX();
		int mouseY = arg0.getY();
		if(isSelected){
			if(aircraft.getFlight() == FLIGHT.ARRIVAL){
				if(mouseX >= decAlt.getMinX() && mouseX <= decAlt.getMaxX() 
						&& mouseY >= decAlt.getMinY() && mouseY <= decAlt.getMaxY()){
					adjAlt(-0.5);
				}
				else if(mouseX >= incAlt.getMinX() && mouseX <= incAlt.getMaxX() 
						&& mouseY >= incAlt.getMinY() && mouseY <= incAlt.getMaxY()){
					adjAlt(0.5);
				}
				else if(mouseX >= decSpd.getMinX() && mouseX <= decSpd.getMaxX() 
						&& mouseY >= decSpd.getMinY() && mouseY <= decSpd.getMaxY()){
					adjSpd(-10);
				}
				else if(mouseX >= incSpd.getMinX() && mouseX <= incSpd.getMaxX() 
						&& mouseY >= incSpd.getMinY() && mouseY <= incSpd.getMaxY()){
					adjSpd(10);
				}
				else if(mouseX >= decHdg.getMinX() && mouseX <= decHdg.getMaxX() 
						&& mouseY >= decHdg.getMinY() && mouseY <= decHdg.getMaxY()){
					adjHdg(-5);
				}
				else if(mouseX >= incHdg.getMinX() && mouseX <= incHdg.getMaxX() 
						&& mouseY >= incHdg.getMinY() && mouseY <= incHdg.getMaxY()){
					adjHdg(5);
				}
				else if(mouseX >= selILS.getMinX() && mouseX <= selILS.getMaxX()
						&& mouseY >= selILS.getMinY() && mouseY <= selILS.getMaxY()){
					aircraft.toggleILS();
				}
				else if(mouseX >= selHld.getMinX() && mouseX <= selHld.getMaxX()
						&& mouseY >= selHld.getMinY() && mouseY <= selHld.getMaxY()){
					aircraft.toggleHold();
				}
				else if(mouseX >= confirm.getMinX() && mouseX <= confirm.getMaxX()
						&& mouseY >= confirm.getMinX() && mouseY <= confirm.getMaxY()){
					apply();
				}
				else if(mouseX >= cancel.getMinX() && mouseX <= cancel.getMaxX()
						&& mouseY >= cancel.getMinY() && mouseY <= cancel.getMaxY()){
					deselect();
				}
			}
			
			else if(aircraft.getFlight() == FLIGHT.HANDOFF_AR || aircraft.getFlight() == FLIGHT.HANDOFF_DE){
				if(mouseX >= hndoff.getMinX() && mouseX <= hndoff.getMaxX()
					&& mouseY >= hndoff.getMinY() && mouseY <= hndoff.getMaxY()){
					aircraft.contact();
					}
				else if(mouseX >= cancel.getMinX() && mouseX <= cancel.getMaxX()
						&& mouseY >= cancel.getMinY() && mouseY <= cancel.getMaxY()){
					deselect();
					}
			}
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle rect = new Rectangle(0,0,width,height);
		Color prevc = g2d.getColor();
		Font prevf = g2d.getFont();
		if(isSelected){
			Draw.box(g, rect, 2, Color.green, Color.black);

			
			if(aircraft.getFlight() == FLIGHT.HANDOFF_AR || aircraft.getFlight() == FLIGHT.HANDOFF_DE){
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
			Text.boxText(g, Fonts.hudNumber, decAlt, HALIGN.CENTER, VALIGN.MIDDLE, "v");
			Draw.box(g, altBox, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, altBox, HALIGN.CENTER, VALIGN.MIDDLE, "FL"+(int)(selectedAlt * 10));
			Draw.box(g, incAlt, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, incAlt, HALIGN.CENTER, VALIGN.MIDDLE, "^");
			
			Draw.box(g, decSpd, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, decSpd, HALIGN.CENTER, VALIGN.MIDDLE, "v");
			Draw.box(g, spdBox, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, spdBox, HALIGN.CENTER, VALIGN.MIDDLE, selectedSpeed + "");
			Draw.box(g, incSpd, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, incSpd, HALIGN.CENTER, VALIGN.MIDDLE, "^");
			
			Draw.box(g, decHdg, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, decHdg, HALIGN.CENTER, VALIGN.MIDDLE, "v");
			Draw.box(g, hdgBox, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, hdgBox, HALIGN.CENTER, VALIGN.MIDDLE, selectedHdg + "");
			Draw.box(g, incHdg, 2, Color.green, Color.black);
			Text.boxText(g, Fonts.hudNumber, incHdg, HALIGN.CENTER, VALIGN.MIDDLE, "^");
			
			if(aircraft.getFlight() == FLIGHT.DEPARTURE){
				Draw.box(g, selILS, 2, Color.red, Color.darkGray);
				Text.boxText(g, Fonts.hudNumber, selILS, HALIGN.CENTER, VALIGN.MIDDLE, "ILS");
				
				Draw.box(g, selHld, 2, Color.red, Color.darkGray);
				Text.boxText(g, Fonts.hudNumber, selHld, HALIGN.CENTER, VALIGN.MIDDLE, "HOLD");
			}
			
			if(aircraft.getFlight() == FLIGHT.ARRIVAL){
				if(aircraft.isClearILS()){
					Draw.box(g, selILS, 2, Color.green, Color.green);
					g.setColor(Color.black);
					Text.boxText(g, Fonts.hudNumber, selILS, HALIGN.CENTER, VALIGN.MIDDLE, "ILS");
					g.setColor(Color.cyan);
				}
				else{
					Draw.box(g, selILS, 2, Color.green, Color.black);
					Text.boxText(g, Fonts.hudNumber, selILS, HALIGN.CENTER, VALIGN.MIDDLE, "ILS");
				}
				if(aircraft.isHolding()){
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
		else{
			Draw.box(g, rect, 1, Color.black, Color.gray);
		}
		g2d.setColor(prevc);
		g2d.setFont(prevf);
	}

	public void select(Aircraft a) {
		if(a!=null){
			aircraft = a;
			selectedSpeed = a.getKIASDes();
			selectedAlt = a.getAltDes();
			selectedHdg = a.getHdgDes();
			isSelected = true;
		}
	}

	@Override
	public void setCoords(Coords coords) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick() {
		if(isSelected && !aircraft.isSelected)
			deselect();
		if(isSelected && aircraft == null)
			deselect();
		if(isSelected && aircraft.isSelected){
			if(selectedHdg != aircraft.getHdgDes()){
				selectedHdg = aircraft.getHdgDes();
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

	private void apply(){
		aircraft.setVelocityDesired(selectedSpeed);
		aircraft.setHeadingDesired(selectedHdg);
		aircraft.setAltitudeDesired(selectedAlt);
		deselect();
	}

	@Override
	public Rectangle getArea() {
		return area;
	}
	
}
