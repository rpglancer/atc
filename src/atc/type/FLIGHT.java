package atc.type;

/**
 * Enumerator for tracking the status of an aircraft. 
 * Flights generally follow these paths during their lifetime:<br>
 * <br>
 * TAKEOFF -> HANDOFF_DE -> DEPARTURE -> CRUISE<br>
 * HANDOFF_AR -> ARRIVAL -> ILS -> LANDING<br>
 * <br>
 * Once a flight reaches cruise, points are added and the flight culled once
 * it exceeds the dimensions of the controlled airspace.<br>
 * <br>
 * Once a flight lands and reduces its speed to 0 points are added and the flight
 * culled.
 * @author Matt Bangert
 *
 */
public enum FLIGHT {
	ARRIVAL,		//	Flight is arriving
	CRUISE,			//	Flight is cruising
	DEPARTURE,		//	Flight is departing
	HANDOFF_AR,		//	Arriving flight is awaiting acknowledgement
	HANDOFF_DE,		//	Departing flight is awaiting acknowledgement
	ILS,			//	Flight is on final approach [on the localizer]
	LANDING,		//	Flight is landing
	TAKEOFF,		//	Flight is taking off
	CRASHING;		//	Flight is crashing		
}
