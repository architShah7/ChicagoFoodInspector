package gui;

import modules.Facility.Risk;
import modules.Facility.Type;
import modules.Inspection.Result;
/**
 * Helper class to determine the checkboxes that are checked in a combobox
 */ 
public class FilterData {
	public boolean name;
	public boolean address;
	public boolean zip;
	public boolean type;
	public boolean risk;
	public boolean result;

	public String namedata;
	public String addressdata;
	public int zipdata;
	public Type typedata;
	public Risk riskdata;
	public Result resultdata;
	
	/**
	 *
	 * Constructor for filterData 
	 * @param name - True if name is checkmarked
	 * @param address - True if address is checkMarked
	 * @param zip - True if zip is checkmarked
	 * @param type - True if type is checkmarked
	 * @param risk - True if risk is checkmarked
	 * @param result - True if result is checkmarked
	 * @param namedata - Facility Name
	 * @param addressdata - Address Information 
	 * @param zipdata - Zip data
	 * @param typedata - Type of Facility
	 * @param riskdata - Associated Risk
	 * @param resultdata - Inspection Result 
	 */
	public FilterData(boolean name, boolean address, boolean zip, boolean type,
			boolean risk, boolean result, String namedata, String addressdata,
			int zipdata, Type typedata, Risk riskdata, Result resultdata) {
		super();
		this.name = name;
		this.address = address;
		this.zip = zip;
		this.type = type;
		this.risk = risk;
		this.result = result;
		this.namedata = namedata;
		this.addressdata = addressdata;
		this.zipdata = zipdata;
		this.typedata = typedata;
		this.riskdata = riskdata;
		this.resultdata = resultdata;
	}
	/**
	 * Default constructor
	 */
	public FilterData() {
	}
}
