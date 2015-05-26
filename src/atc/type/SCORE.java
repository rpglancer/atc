package atc.type;

public enum SCORE {
	PLYRSCORE 	(0),
	PLYRSKILL 	(1),
	ARRIVED 	(2),
	LANDED		(3),
	DEPARTED	(4),
	CRUISEALT	(5),
	HANDOFF		(6),
	HANDOFFMISS	(7),
	OUTOFSEC	(8),
	SEPINCTIME	(9),
	PLAYTIME	(10),
	CRASHED		(11);
	
	private int sID;
	
	SCORE(int sID){
		this.sID = sID;
	}
	
	public int getSID(){
		return this.sID;
	}
	
//	private int handoffs			= 0;		//	Handoffs completed
//	private int handoffsMissed		= 0;		//	Handoffs missed
//	private int score				= 0;		//	Player score
//	private int skill				= 0;		//	Player skill
//	private int planesLanded		= 0;		//	Aircraft landed
//	private int planesDeparted		= 0;		//	Aircraft departed
//	private int planesOutOfSector	= 0;		//	Aircraft out of sector
}
