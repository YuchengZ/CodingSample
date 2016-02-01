package edu.cmu.cs.cs214.hw3;

/**
 * Represents a (latitude, longitude) location.
 */
public final class Location {

	private double latitude;
	private double longitude;

	/**
	 * Constructor for Location class
	 * 
	 * @param loc
	 *            Location from which to copy coordinates
	 */
	public Location(Location loc) {
		this(loc.latitude, loc.longitude);
	}

	/**
	 * Constructor for Location class
	 * 
	 * @param pLatitude
	 *            The latitude of the Location
	 * @param pLongitude
	 *            The longitude of the Location
	 */
	public Location(double pLatitude, double pLongitude) {
		this.latitude = pLatitude;
		this.longitude = pLongitude;
	}

	/**
	 * @return the latitude of this Location
	 */
	public double getLat() {
		return latitude;
	}

	/**
	 * @return the longitude of this Location
	 */
	public double getLng() {
		return longitude;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Location)) {
			return false;
		}
		Location loc = (Location) o;
		return latitude == loc.latitude && longitude == loc.longitude;
	}

	@Override
	public int hashCode() {
		double result = latitude;
		final int multiplier = 31;
		result = multiplier * result + longitude;
		return (int) result;
	}

	@Override
	public String toString() {
		return String.format("%s(%.4f,%.4f)", getClass().getSimpleName(),
				latitude, longitude);
	}

}
