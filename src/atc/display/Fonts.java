package atc.display;

import java.awt.Font;

public class Fonts {
	int c = 9650;
	public static final String climb = Character.toString((char)9650);
	public static final String decent = Character.toString((char)9660);
	public static final String level = Character.toString((char)9658);
	
	public static final String fix = Character.toString((char)8710);
	public static final String flight = Character.toString((char)9674); //9679
	
	public static Font delivtext = new Font("Modern", Font.PLAIN, 15);
	public static Font hudNumber = new Font("Modern", Font.PLAIN, 20);
	public static Font radarobj = new Font("Consolas", Font.BOLD, 12);
	public static Font radartext = new Font("Consolas", Font.BOLD, 11);

}
