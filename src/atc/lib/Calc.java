package atc.lib;

import java.awt.geom.Line2D;

/**
 * Class containing useful static mathematical methods.
 * @author Matt Bangert
 *
 */
public class Calc {

	/**
	 * Method to calculate the approach angle of an aircraft on a localizer.
	 * @param runway		Threshold coordinate of the runway
	 * @param intercept		Point at which the path of the aircraft intercepts the path of the localizer.
	 * @param aircraft		Coordinate of the aircraft
	 * @return				The angle at which the path of the aircraft and the localizer cross.
	 */
	public static double approachAngle(Coords runway, Coords intercept, Coords aircraft){
		
		double a = distance(intercept, aircraft);
		double b = distance(runway, aircraft);
		double c = distance(runway, intercept);
		
//		double A = (b*b + c*c - a*a) / (2*b*c);		// Kept for reference, only angle B needs to be calculated.
		double B = (a*a + c*c - b*b) / (2*a*c);
		
		double alpha = (Math.acos(B));
//		System.out.println("Approach Angle: " + Math.toDegrees(alpha));
		return alpha;	// This should probably be returned in degrees and save the work for other methods.
	}
	
	/**
	 * Method to calculate the distance between two coordinates in pixels.
	 * @param src	Coordinate A
	 * @param tgt	Coordinate B
	 * @return	Distance in pixels
	 */
	public static double distance (Coords src, Coords tgt){
		double a = src.getX() - tgt.getX();
		double b = src.getY() - tgt.getY();
		double c = (a * a) + (b * b);
		return Math.sqrt(c);
	}
	
	/**
	 * Method to calculate the distance between two coordinates in nautical miles [NM].
	 * @param src	Coordinate A
	 * @param tgt	Coordinate B
	 * @return	Distance in nautical miles.
	 */
	public static double distanceNM (Coords src, Coords tgt){
		double a = src.getX() - tgt.getX();
		double b = src.getY() - tgt.getY();
		double c = (a * a) + (b * b);
		return Math.sqrt(c * Aircraft.NMPP);
	}
	
	/**
	 * Method to calculate the true bearing of a target coordinate relative to a source coordinate.
	 * @param src	Source Coordinate
	 * @param tgt	Target Coordinate
	 * @return	Relative bearing of target from source [in degrees].
	 */
	public static double relativeBearing(Coords src, Coords tgt){
		double angle = (double) Math.toDegrees(Math.atan2(tgt.getY() - src.getY(), tgt.getX() - src.getX()));
		
		if(angle < 0){
			angle += 360;
		}
		angle+=90;
		if(angle >=360)
			angle -= 360;
		return angle;
		}
	
	/**
	 * Method to determine if two lines cross.
	 * @param src	Line A
	 * @param tgt	Line B
	 * @return	True if the intersect, false otherwise.
	 */
	public static boolean lineIntcpt(Line2D src, Line2D tgt){
		return src.intersectsLine(tgt);
	}
	
	/**
	 * Method to determine the cartesian [x,y] coordinates at which two lines intersect. This method 
	 * lacks some accuracy as it does not use doubles or floats, but given the fact that you cannot 
	 * represent an object on screen in terms of fractions of a pixel, I do not think it matters.
	 * @param src	Line A
	 * @param tgt	Line B
	 * @return	Coords [x,y] if the lines intersect, null otherwise.
	 */
	public static Coords intersection(Line2D src, Line2D tgt){
		if(!src.intersectsLine(tgt))
			return null;
		int x1 = (int)src.getX1();
		int y1 = (int)src.getY1();
		int x2 = (int)src.getX2();
		int y2 = (int)src.getY2();
		int x3 = (int)tgt.getX1();
		int y3 = (int)tgt.getY1();
		int x4 = (int)tgt.getX2();
		int y4 = (int)tgt.getY2();
		
		int d = (x1-x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if(d == 0) return null;
		
		int xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
		int yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
		
		return new Coords(xi, yi);	
	}	
}
