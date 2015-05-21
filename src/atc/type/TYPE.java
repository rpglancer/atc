package atc.type;

/**
 * Enumerator containing the types of Entity to be tracked by the Handler.
 * <list>
 * <li><b>WINDOW_INFO</b>
 * <li><b>WINDOW_HUD</b>
 * <li><b>AIRCRAFT</b>
 * <li><b>LOCALIZER</b>
 * <li><b>AIRPORT</b>
 * <li><b>RUNWAY_ARRIVE</b>
 * <li><b>RUNWAY_DEPART</b>
 * <li><b>FIX</b>
 * </list>
 * @author Matt Bangert
 *
 */
public enum TYPE {
	WINDOW_INFO (-2),
	WINDOW_HUD (-1),
	AIRCRAFT (0),
	LOCALIZER(2),
	AIRPORT (3),
	RUNWAY_ARRIVE (4),
	RUNWAY_DEPART (5),
	FIX (6);
	
	private int tID;
	
	TYPE(int tID){
		this.tID = tID;
	}
	
	public int getID(){
		return tID;
	}
}
