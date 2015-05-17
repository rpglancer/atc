package atc.type;

public enum TYPE {
	AIRCRAFT_ARRIVE (0),
	AIRCRAFT_DEPART (1),
	AIRPORT (2),
	RUNWAY_ARRIVE (3),
	RUNWAY_DEPART (4),
	XING (5);
	
	private int tID;
	
	TYPE(int tID){
		this.tID = tID;
	}
	
	public int getID(){
		return tID;
	}
}
