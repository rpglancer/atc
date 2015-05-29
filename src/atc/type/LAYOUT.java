package atc.type;

/**
 * Concept work for an enumerator that can be used to generate airports. The perks of this method
 * would be accurate and functional runway layouts. The drawbacks; however, are significant. The most
 * major drawback is the sheer amount of data that the enumerator would have to contain, including
 * runway numbers, runway headings, runway distance from tower and runway heading from tower. Another
 * major drawback [though not insurmountable] is devising a way to have the airport generator pull a
 * single value-set from the enumerator.
 * @author Matt Bangert
 *
 */
public enum LAYOUT {

	SFO ("SFO", 2, 2, 
			280, "28L", 2.0, 90,  
			280, "28R", 2.5, 70,
			10, "1L", 0.75, 130,
			10, "1R", 1.25, 115);
	
	private String aptID;
	private int rwyAriv, rwyDept;									//	Number of arrival and departure runways
	private int arwy0hdg, arwy1hdg, drwy2hdg, drwy3hdg;				//	Runway headings
	private double arwy0dt, arwy1dt, drwy2dt, drwy3dt;				//	Runway distance from tower
	private int arwy0ht, arwy1ht, drwy2ht, drwy3ht;					//	Runway heading from tower
	private String arwy0name, arwy1name, drwy2name, drwy3name;		//	Runway names
	
	LAYOUT(String ID, int rwyAriv, int rwyDept, 
	int arwy0hdg, String arwy0name, double arwy0dt, int arwy0ht,
	int arwy1hdg, String arwy1name, double arwy1dt, int arwy1ht,
	int drwy2hdg, String drwy2name, double drwy2dt, int drwy2ht,
	int drwy3hdg, String drwy3name, double drwy3dt, int drwy3ht){
		aptID = ID;
		this.rwyAriv = rwyAriv;
		this.rwyDept = rwyDept;
		
		this.arwy0hdg = arwy0hdg;
		this.arwy1hdg = arwy1hdg;
		this.drwy2hdg = drwy2hdg;
		this.drwy3hdg = drwy3hdg;
		
		this.arwy0name = arwy0name;
		this.arwy1name = arwy1name;
		this.drwy2name = drwy2name;
		this.drwy3name = drwy3name;
		
		this.arwy0dt = arwy0dt;
		this.arwy1dt = arwy1dt;
		this.drwy2dt = drwy2dt;
		this.drwy3dt = drwy3dt;
		
		this.arwy0ht = arwy0ht;
		this.arwy1ht = arwy1ht;
		this.drwy2ht = drwy2ht;
		this.drwy3ht = drwy3ht;		
	}

	public String getID(){
		return aptID;
	}
	public int getRwyA(){
		return rwyAriv;
	}
	public int getRwyD(){
		return rwyDept;
	}
	public int[] getRwyAHdg(){
		int[] rah = {arwy0hdg, arwy1hdg};
		return rah;
	}
	public int[] getRwyDHdg(){
		int[] rdh = {drwy2hdg, drwy3hdg};
		return rdh;
	}
	public int[] getRwyAHT(){
		int[] aht = {arwy0ht, arwy1ht};
		return aht;
		}
	public int[] getRwyDHT(){
		int[] dht = {drwy2ht, drwy3ht};
		return dht;
	}
	public double[] getRwyADT(){
		double[] adt = {arwy0dt, arwy1dt};
		return adt;
	}
	public double[] getRwyDDT(){
		double[] ddt = {drwy2dt, drwy3dt};
		return ddt;
	}
	
	public String[] getRwyAName(){
		String[] ran = {arwy0name, arwy1name};
		return ran;
	}
	public String[] getRwyDName(){
		String[] rdn = {drwy2name, drwy3name};
		return rdn;
	}	
}
