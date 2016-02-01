package edu.cmu.cs.cs214.hw3.bus;

/**
 * 
 * @author ZhangYC
 *
 */
public class BusProperty extends Bus{
	private final Bus busProperty;
	
	/**
	 * constructor
	 * @param property Bus decorator
	 */
	public BusProperty(Bus property){
		busProperty = property;
	}

	@Override
	double driverSpeedUp() {
		return busProperty.driverSpeedUp();
	}	

}
