package atc.lib;

import java.awt.geom.Line2D;

public class Calc {

	public static double angle(Coords src, Coords tgt){

		double angle = (double) Math.toDegrees(Math.atan2(tgt.getY() - src.getY(), tgt.getX() - src.getX()));
		
		if(angle < 0){
			angle += 360;
		}
		angle+=90;
		if(angle >=360)
			angle -= 360;
		System.out.println("Angle: " + angle);
		return angle;
		}
	
	public static boolean lineIntcpt(Line2D src, Line2D tgt){
		return src.intersectsLine(tgt);
	}
}
