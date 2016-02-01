package edu.cmu.cs.cs214.hw3.bus;

/**
 * 
 * @author ZhangYC
 *
 */
public class BusWithBetterEngine extends BusProperty{
	
	private final double speedUp = 0.1;

	/**
	 * constructor
	 * @param property Bus decorator
	 */
	public BusWithBetterEngine(Bus property) {
		super(property);
	}
	
	@Override
	public double driverSpeedUp(){
		// if this car is full of passage, it cannot speed up even with a better engine.
		if (getSpacePeople()==0){
			return super.driverSpeedUp();
		}
		return super.driverSpeedUp()-speedUp;
	}

}
