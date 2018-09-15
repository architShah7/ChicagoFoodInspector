package modules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Stores the information related to a facility.
 * 
 * @author Debonair Coders
 */
public class Facility implements Serializable {
	private static final long serialVersionUID = -2956811568112659246L;
	
	//Type of facility
	public enum Type {
		RESTAURANT,
		SCHOOL,
		GROCERYSOTRE,
		CHILDRENSERVICE,
		LIQOUR,
		MOBILEVENDOR,
		DAYCARE,
		CAFETERIA,
		BAKERY,
		LONGTERMCARE,
		CATERING,
		UNKNOWN;

		/**
		 * Parse a Type from a string.
		 * 
		 * @param type The type as a string
		 * @return The Type
		 */
		public static Type parse(String type) {
			if (type.toLowerCase().startsWith("r"))
				return RESTAURANT;
			else if (type.toLowerCase().startsWith("s"))
				return SCHOOL;
			else if (type.toLowerCase().startsWith("g"))
				return GROCERYSOTRE;
			else if (type.toLowerCase().startsWith("ch"))
				return CHILDRENSERVICE;
			else if (type.toLowerCase().startsWith("li"))
				return LIQOUR;
			else if (type.toLowerCase().startsWith("m"))
				return MOBILEVENDOR;
			else if (type.toLowerCase().startsWith("d"))
				return DAYCARE;
			else if (type.toLowerCase().startsWith("caf"))
				return CAFETERIA;
			else if (type.toLowerCase().startsWith("b"))
				return BAKERY;
			else if (type.toLowerCase().startsWith("lo"))
				return LONGTERMCARE;
			else if (type.toLowerCase().startsWith("cat"))
				return CATERING;
			else
				return UNKNOWN;
		}
	}
	
	//Level of risk
	public enum Risk {
		ALL,
		HIGH,
		MEDIUM,
		LOW;

		/**
		 * Parse a Risk from a string.
		 * 
		 * @param risk The risk as a string.
		 * @return The Risk.
		 */
		public static Risk parse(String risk) {
			if (risk.contains("1"))
				return HIGH;
			else if (risk.contains("2"))
				return MEDIUM;
			else if (risk.contains("3"))
				return LOW;
			else
				return ALL;
		}
	}
	
	//Name of facility 
	private String name;
	//License
	private int license;
	
	//Location properties of the facility
	private String address;
	private int zip;

	private double latitude;
	private double longitude;
	
	//Inspection of facility
	private ArrayList<Inspection> inspections;

	private Type type;
	private Risk risk;
	
	/**
	 * Constructs a facility
	 * @param name - The name of the facility
	 * @param license - The license of the facility
	 * @param address - The address of the facility
	 * @param zip - The zip code
	 * @param latitude - The latitude
	 * @param longitude - The longitude
	 * @param type - Type of facility
	 * @param risk - Risk factor
	 * @param inspections - List of inspections
	 */
	public Facility(String name, int license, String address, int zip,
			double latitude, double longitude, Type type, Risk risk,
			Inspection... inspections) {
		this.name = name;
		this.license = license;
		this.address = address;
		this.zip = zip;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
		this.risk = risk;
		this.inspections = new ArrayList<Inspection>(Arrays.asList(inspections));
	}
	
	/**
	 * Adds an inspection to a facility
	 * @param inspection - The inspection
	 */
	public void addInspection(Inspection inspection) {
		this.inspections.add(inspection);
	}

	/**
	 * Gets the type of the facility
	 * @return - The type of the facility
	 */
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Get the address of the facility 
	 * @return - The facility address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Get the latitude of the facility 
	 * @return - the latitude of the facility
	 */
	public double getLatitude() {
		return this.latitude;
	}
	
	/**
	 * Get the longitude of the facility 
	 * @return - the longitude of the facility
	 */
	public double getLongitude() {
		return this.longitude;
	}
	
	/**
	 * Get the license of the facility 
	 * @return - the license of the facility
	 */
	public int getLicense() {
		return this.license;
	}
	
	/**
	 * Set license
	 * @param license - The license to be set
	 */
	public void setLicense(int license) {
		this.license = license;
	}
	
	/**
	 * Get the zip code of the facility 
	 * @return - the zip code of the facility
	 */
	public int getZip() {
		return this.zip;
	}
	
	/**
	 * Get the risk factor of the facility 
	 * @return - the risk factor of the facility
	 */
	public Risk getRisk() {
		return this.risk;
	}
	
	/**
	 * Get the list of inspections of the facility 
	 * @return - the list of inspections of the facility
	 */
	public ArrayList<Inspection> getInspections() {
		return this.inspections;
	}
	
	/**
	 * Get a specific inspection from a facility
	 * @param index - The index of the list
	 * @return The inspection at index i
	 */
	public Inspection getInspection(int index) {
		return this.inspections.get(index);
	}
	
	/**
	 * Get the name of the facility 
	 * @return - the name of the facility
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Calculates a score based on risk, results, and violations
	 * @return - The score
	 */
	public double getRecommndedFactor() {
		if (this.inspections.size() == 0)
			return -1;
		
		Inspection temp = this.getInspection(0);
		return (weightOfRisk() * 0.3 + temp.weightOfResult() * 0.5 + temp
				.weightOfViolations() * 0.2);
	}
	
	/**
	 * Calculates the weightage of risk based on the risk factor
	 * @return - A weight determined by the facility's risk
	 */
	private int weightOfRisk() {
		switch (this.risk) {
		case HIGH:
			return 100;
		case MEDIUM:
			return 50;
		case LOW:
			return 25;
		case ALL:
			return 100;
		default:
			break;
		}
		return 100;
	}

	/**
	 * Formats the output to a facility
	 */
	public String toString() {
		String s = this.getName() + "\t" + this.getAddress() + "\t"
				+ this.getLicense() + "\t" + this.getRisk() + this.getRecommndedFactor() + "\n";

		for (Inspection i : this.getInspections())
			s += "\t- " + i + "\n";

		return s;
	}
}