package atc.type;

public enum TYPE {
	WINDOW_INFO (-2),
	WINDOW_HUD (-1),
	AIRCRAFT_ARRIVE (0),
	AIRCRAFT_DEPART (1),
	AIRCRAFT_FINAL (2),
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
