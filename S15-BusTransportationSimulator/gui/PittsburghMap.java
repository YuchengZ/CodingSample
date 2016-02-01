package edu.cmu.cs.cs214.hw3.gui;

import javax.swing.ImageIcon;

import edu.cmu.cs.cs214.hw3.Util;

/**
 * A map with the coordinates that surround Pittsburgh, PA, USA
 * 
 */
public class PittsburghMap extends CityMap {

	private static final long serialVersionUID = -6463665451474180657L;
	private static final double START_LAT = 40.5840;
	private static final double START_LNG = -80.2531;
	private static final double END_LAT = 40.3120;
	private static final double END_LNG = -79.8284;

	private static final ImageIcon IMAGE = Util.loadImage("pgh.png");
	private static final int WIDTH = IMAGE.getIconWidth() / 2;
	private static final int HEIGHT = IMAGE.getIconHeight() / 2;

	/**
	 * Default constructor for the PittsburghMap class
	 */
	public PittsburghMap() {
		super(WIDTH, HEIGHT, START_LAT, START_LNG, END_LAT, END_LNG);
	}

	@Override
	public ImageIcon getBackgroundImage() {
		return IMAGE;
	}


	@Override
	public String getName() {
		return "Pittsburgh";
	}

}
