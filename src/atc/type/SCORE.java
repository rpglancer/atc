package atc.type;

public enum SCORE {
	PLYRSCORE 	(0),		//	Player Score
	PLYRSKILL 	(1),		//	Player Skill Level
	ARRIVED 	(2),		//	Number of planes arrived in sector
	LANDED		(3),		//	Number of planes landed
	DEPARTED	(4),		//	Number of planes departed from sector
	CRUISEALT	(5),		//	Number of departed planes reached handoff altitude
	HANDOFF		(6),		//	Number of handoffs
	HANDOFFMISS	(7),		//	Number of handoffs missed
	OUTOFSEC	(8),		//	Number of planes flown out of sector
	SEPINCTIME	(9),		//	Total amount of time planes did not meet required separation
	PLAYTIME	(10),		//	Total playtime
	CRASHED		(11),		//	Number of planes crashed
	LOCMISS		(12);		//	Number of planes overshot localizer
	
	private int sID;
	
	SCORE(int sID){
		this.sID = sID;
	}
	
	public int getSID(){
		return this.sID;
	}

}
