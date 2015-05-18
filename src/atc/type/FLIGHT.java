package atc.type;

public enum FLIGHT {
	ARRIVAL,		//	Flight is arriving
	CRUISE,			//	Flight is cruising
	DEPARTURE,		//	Flight is departing
	FINAL,			//	Flight is on final approach [on the localizer]
	HANDOFF;		//	Flight is awaiting acknowledgement
}
