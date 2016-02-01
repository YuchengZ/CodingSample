package edu.cmu.cs.cs214.hw3.gui;

import javax.swing.ImageIcon;

import edu.cmu.cs.cs214.hw3.Util;

/**
 * A map with the coordinates that surround Oakland
 * 
 */
public class OaklandMap extends CityMap {

	private static final long serialVersionUID = 1196402301839487089L;
	private static final double START_LAT = 40.4716;
	private static final double START_LNG = -79.9963;
	private static final double END_LAT = 40.4036;
	private static final double END_LNG = -79.8902;

	private static final ImageIcon IMAGE = Util.loadImage("oak.png");
	private static final int WIDTH = IMAGE.getIconWidth() / 2;
	private static final int HEIGHT = IMAGE.getIconHeight() / 2;

	/**
	 * Default constructor for the PittsburghMap class
	 */
	public OaklandMap() {
		super(WIDTH, HEIGHT, START_LAT, START_LNG, END_LAT, END_LNG);
	}

	@Override
	public ImageIcon getBackgroundImage() {
		return IMAGE;
	}


	@Override
	public String getName() {
		return "Oakland";
	}

}
