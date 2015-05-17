package atc.type;

public enum TYPE {
	AIRCRAFT_ARRIVE (0),
	AIRCRAFT_DEPART (1),
	AIRCRAFT_FINAL (2),
	AIRPORT (3),
	RUNWAY_ARRIVE (4),
	RUNWAY_DEPART (5),
	XING (6);
	
	private int tID;
	
	TYPE(int tID){
		this.tID = tID;
	}
	
	public int getID(){
		return tID;
	}
}
