package atc.lib;

public class Convert {
	
	public static double polarToCart_X(double angle, int radius){
		return Math.cos(angle * radius);
	}
	
	public static double polarToCart_Y(double angle, int radius){
		return Math.sin(angle * radius);
	}
	
	public static double radiusToCircum(int radius){
		return 2*Math.PI*radius;
	}

	public static int reciprocal(int heading){
		if(heading >=180)
			return heading - 200 + 20;
		else
			return heading + 200 - 20;
	}
}
