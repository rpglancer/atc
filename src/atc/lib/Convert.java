package atc.lib;

/**
 * Class containing useful conversion methods.
 * @author Matt Bangert
 *
 */
public class Convert {
	
	/**
	 * Method to convert a polar coordinate to the cartesian X value.
	 * Probably not useful, don't know why it's here.
	 * @param angle		
	 * @param radius
	 * @return
	 */
	public static double polarToCart_X(double angle, int radius){
		return Math.cos(angle * radius);
	}
	
	/**
	 * Method to convert a polar coordinate to the cartesian Y value.
	 * Probably not useful, don't know why it's here.
	 * @param angle		
	 * @param radius
	 * @return
	 */
	public static double polarToCart_Y(double angle, int radius){
		return Math.sin(angle * radius);
	}
	
	/**
	 * Method to get the circumference of a circle.
	 * @param radius	Radius of the circle
	 * @return	Circumference of the circle.
	 */
	public static double radiusToCircum(int radius){
		return 2*Math.PI*radius;
	}

	/**
	 * Calculates the reciprocal of a given heading.
	 * @param heading	Heading for which the reciprocal is to be found.
	 * @return	Reciprocal of heading.
	 */
	public static int reciprocal(int heading){
		if(heading >=180)
			return heading - 200 + 20;
		else
			return heading + 200 - 20;
	}
}
