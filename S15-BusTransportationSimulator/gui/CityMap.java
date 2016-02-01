package edu.cmu.cs.cs214.hw3.gui;

import java.io.Serializable;

import javax.swing.ImageIcon;

import edu.cmu.cs.cs214.hw3.Location;

/**
 * A Map of a city that has coordinates and maps these coordinates to x and y
 * positions that can easily be displayed on a GUI
 *
 */
public abstract class CityMap implements Serializable {

	private static final long serialVersionUID = 6466394414385765634L;

	private final int width;
	private final int height;
	private final double startLat;
	private final double startLng;
	private final double endLat;
	private final double endLng;

	/**
	 * Constructor for CityMap this should be used by extending classes to pass
	 * parameters
	 * 
	 * @param pWidth
	 *            The width of the space to map coordinates
	 * @param pHeight
	 *            The height of the space to map coordinates
	 * @param pStartLat
	 *            The starting latitude
	 * @param pStartLng
	 *            The starting longitude
	 * @param pEndLat
	 *            The ending latitude
	 * @param pEndLng
	 *            The ending longitude
	 */
	public CityMap(int pWidth, int pHeight, double pStartLat, double pStartLng,
			double pEndLat, double pEndLng) {
		width = pWidth;
		height = pHeight;
		startLat = pStartLat;
		startLng = pStartLng;
		endLat = pEndLat;
		endLng = pEndLng;
	}

	/**
	 * Getter for the width of the map to be drawn
	 * 
	 * @return The width of the map
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Getter for the height of the map to be drawn
	 * 
	 * @return The height of the map
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Translates an {@link Location} in latitude and longitude to an x location
	 * on the map
	 * 
	 * @param location
	 *            The Location for which to determine the x location
	 * @return The x location on the map if the location is within the bounds
	 *         and -1 otherwise
	 * */
	public int getXLocation(Location location) {
		double mapDegrees = endLng - startLng;
		double degrees = location.getLng() - startLng;
		int result = (int) (width * degrees / mapDegrees);
		if (result < 0 || result > width) {
			return -1;
		}
		return result;
	}

	/**
	 * Translates a {@link Location} in latitude and longitude to a y location
	 * on the map
	 * 
	 * @param location
	 *            The Location for which to determine the y location
	 * @return The y location on the map if the location is within the bounds
	 *         and -1 otherwise
	 */
	public int getYLocation(Location location) {
		double mapDegrees = endLat - startLat;
		double degrees = location.getLat() - startLat;
		int result = (int) (height * degrees / mapDegrees);
		if (result < 0 || result > height) {
			return -1;
		}
		return result;
	}

	/**
	 * Returns a picture of the map that is rendered in the background.
	 * 
	 * @return The image of the map
	 */
	public abstract ImageIcon getBackgroundImage();

	/**
	 * Returns a name of the map to be shown to the user
	 * 
	 * @return name of the map
	 */
	public abstract String getName();

}
