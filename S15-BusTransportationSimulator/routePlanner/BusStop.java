package edu.cmu.cs.cs214.hw3.routePlanner;

import edu.cmu.cs.cs214.hw3.Location;

/**
 * interface Stop to describe a stop
 * 
 * @author ZhangYC
 *
 */
public interface BusStop {
	/**
	 * set bus stop's name
	 * 
	 * @param name
	 *            String
	 */
	void setName(String name);

	/**
	 * set location
	 * @param loc Location
	 */
	void setLocation(Location loc);

	/**
	 * set time of the stop
	 * 
	 * @param time
	 *            int
	 */
	void setTime(int time);

	/**
	 * set the bus name of the stop
	 * 
	 * @param busName
	 *            String
	 */
	void setBusName(String busName);

	/**
	 * get the bus stop's name
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * get location
	 * @return Location
	 */
	Location getLocation();

	/**
	 * get time
	 * 
	 * @return int
	 */
	int getTime();

	/**
	 * get bus name
	 * 
	 * @return String
	 */
	String getBusName();
}
