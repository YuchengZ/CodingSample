package edu.cmu.cs.cs214.hw3.routePlanner;

import edu.cmu.cs.cs214.hw3.Location;

/**
 * class VertexStop implements Stop
 * 
 * @author ZhangYC
 *
 */
public class VertexStop implements BusStop {
	private String name;
	private Location location;
	private int time;
	private String busName;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}


	@Override
	public int getTime() {
		// TODO Auto-generated method stub
		return time;
	}

	@Override
	public String getBusName() {
		// TODO Auto-generated method stub
		return busName;
	}

	@Override
	public void setName(String s) {
		// TODO Auto-generated method stub
		this.name = s;

	}



	@Override
	public void setTime(int t) {
		// TODO Auto-generated method stub
		this.time = t;

	}

	@Override
	public void setBusName(String s) {
		// TODO Auto-generated method stub
		this.busName = s;

	}


	@Override
	public void setLocation(Location loc) {
		// TODO Auto-generated method stub
		location = loc;
		
	}


	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return location;
	}



}
