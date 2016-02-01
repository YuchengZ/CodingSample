package edu.cmu.cs.cs214.hw3;

import java.awt.MediaTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

/**
 * Utility class for common tasks.
 */
public final class Util {

	/**
	 * 
	 */
	private static final String RESOURCE = "src/main/resources/";
	private static final Random RANDOM = new Random();

	/**
	 * 
	 */
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
	public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;

	/**
	 * Private constructor to prevent instantiation of the utility class
	 */
	private Util() {
		// This class should not be instantiated.
	}

	/**
	 * Loads an image, given the name of the file (for example, "fox.gif"). This
	 * file must be in the 'resources' folder.
	 *
	 * @param filename
	 *            the name of the image
	 * @return A loaded {@link ImageIcon}
	 */
	public static ImageIcon loadImage(String filename) {
		ImageIcon img = new ImageIcon(RESOURCE + filename);
		if (img.getImageLoadStatus() != MediaTracker.COMPLETE) {
			System.err.println(RESOURCE + filename + " not found");
			return getUnknownImage();
		}
		return img;
	}

	/**
	 * Loads an image named "unknown.gif" under the "resources" folder
	 *
	 * @return A loaded {@link ImageIcon}
	 */
	private static ImageIcon getUnknownImage() {
		ImageIcon img = new ImageIcon(RESOURCE + "unknown.gif");
		if (img.getImageLoadStatus() != MediaTracker.COMPLETE) {
			throw new RuntimeException("images not available, check resources");
		}
		return img;
	}

	/**
	 * Formats the time from seconds from midnight to a human-readable format
	 * 
	 * @param time
	 *            Seconds elapsed since midnight
	 * @return The time of the day formatted in the manner hh:mm
	 */
	public static String formatTime(int time) {
		final int hoursPerDay = 24;
		int hour = time / SECONDS_PER_HOUR % hoursPerDay;
		int minutes = time / SECONDS_PER_MINUTE % SECONDS_PER_MINUTE;
		int seconds = time % SECONDS_PER_MINUTE;
		return String.format("%02d:%02d:%02d", hour, minutes, seconds);
	}


	/**
	 * This method adds noise to the provided value. The resulting value will be
	 * smaller or larger than the resulting values, where smaller deviations are
	 * more common than larger deviations.
	 * <p/>
	 * Based on a normal distribution. The noise level is the standard deviation
	 * of the normal distribution. If you do not understand normal
	 * distributions, put a number in such that you would not want returned
	 * values to vary from that number by 2x more than 5% of the time. i.e. if
	 * you input 5, you can expect roughly 95% of returned numbers will be
	 * within +/-5 of the mean value
	 *
	 * @param value
	 *            The mean value on which to base the noise -- the statistical
	 *            average of all possible return values
	 * @param stdDev
	 *            The standard deviation for the normal distribution.
	 * @return A random number near the mean, based on a normal distribution
	 */
	public static double addNoise(double value, double stdDev) {
		return RANDOM.nextGaussian() * stdDev + value;
	}

	/**
	 * This method adds noise to the provided value. The resulting value will be
	 * smaller or larger than the resulting values, where smaller deviations are
	 * more common than larger deviations.
	 * <p/>
	 * Based on a normal distribution. The noise level is the standard deviation
	 * of the normal distribution. If you do not understand normal
	 * distributions, put a number in such that you would not want returned
	 * values to vary from that number by 2x more than 5% of the time. i.e. if
	 * you input 5, you can expect roughly 95% of returned numbers will be
	 * within +/-5 of the mean value
	 *
	 * @param value
	 *            The mean value on which to base the noise -- the statistical
	 *            average of all possible return values
	 * @param stdDev
	 *            The standard deviation for the normal distribution.
	 * @return A random number near the mean, based on a normal distribution
	 */
	public static int addNoiseInt(int value, int stdDev) {
		return (int) addNoise(value, stdDev);
	}

	/**
	 * Returns the name of a random stop in the bus system. The randomness is
	 * weighted to favor more commonly used stops.
	 *
	 * The stops and weights are hard coded in this class but may be changed.
	 *
	 * @return The name of a pseduo-random bus stop
	 */
	public static String getRandomStop() {
		return stopDistribution.get(RANDOM.nextInt(stopDistribution.size()));
	}

	// CHECKSTYLE:OFF
	/**
	 * Returns the number of riders that are inserted in the simulation per
	 * second. Note that the number is a floating point number.
	 * <p/>
	 * A typical user of this function will have a variable and increase this
	 * variable every second by the value of this function. It will then create
	 * a new rider while this variable is larger than 1 and subtract 1 from this
	 * variable. That is, if riders are added at a rate of 0.25 riders per
	 * second, a new rider is inserted every fourth second.
	 * <p/>
	 * Values are hard-coded with a specific probability per hour, but may be
	 * changed.
	 *
	 * @param time
	 *            time of day in seconds since midnight
	 * @param totalRidersPerDay
	 *            total riders expected for the entire day
	 * @return number of riders to be added this second
	 */
	public static double getRidersPerSecond(int time, int totalRidersPerDay) {
		double[] ridersPerHour = {
				0,
				0,
				0,
				0,
				0.0005614823, // 4am-4:59am
				0.0005614823, // 5am-5:59am
				0.0061763054, 0.0449185851, 0.0583941606, 0.0499719259,
				0.0426726558, 0.0331274565, 0.0308815272, 0.0275126334,
				0.0185289163, 0.0303200449, 0.07523863, 0.112857945,
				0.112857945, 0.0791690062, 0.0724312184, 0.0668163953,
				0.0724312184, 0.064570466 // 23am-23:59am
		};
		int hour = time / SECONDS_PER_HOUR;
		return ridersPerHour[hour % 24] * totalRidersPerDay;
	}


	private static final List<String> stopDistribution = new ArrayList<String>();

	/*
	 * Helper method to build the distribution of stops
	 */
	private static void addStopPropability(String stopname,
			int relativeProbability) {
		for (int i = 0; i < relativeProbability; i++)
			stopDistribution.add(stopname);
	}

	static {
		addStopPropability("5TH AVE AT AIKEN AVE", 1);
		addStopPropability("5TH AVE AT BEECHWOOD BLVD", 1);
		addStopPropability("5TH AVE AT BELLEFIELD AVE", 6);
		addStopPropability("5TH AVE AT BIGELOW BLVD (PITT)", 11);
		addStopPropability("5TH AVE AT BRENHAM ST FS", 1);
		addStopPropability("5TH AVE AT CHESTERFIELD RD", 6);
		addStopPropability("5TH AVE AT CRAIG ST", 11);
		addStopPropability("5TH AVE AT CRAIG ST NS", 10);
		addStopPropability("5TH AVE AT HASTINGS ST FS (PENN AVE)", 13);
		addStopPropability("5TH AVE AT HIGHLAND AVE", 1);
		addStopPropability("5TH AVE AT MAGEE ST", 1);
		addStopPropability("5TH AVE AT MAURICE ST", 1);
		addStopPropability("5TH AVE AT MCKEE PL FS", 2);
		addStopPropability("5TH AVE AT MCPHERSON ST", 1);
		addStopPropability("5TH AVE AT MOREWOOD AVE", 9);
		addStopPropability("5TH AVE AT NEGLEY AVE", 2);
		addStopPropability("5TH AVE AT NEVILLE ST", 1);
		addStopPropability("5TH AVE AT OAKLAND AVE", 4);
		addStopPropability("5TH AVE AT PENN AVE FS", 1);
		addStopPropability("5TH AVE AT ROSS ST", 14);
		addStopPropability("5TH AVE AT SHADY AVE", 4);
		addStopPropability("5TH AVE AT SMITHFIELD ST", 2);
		addStopPropability("5TH AVE AT TENNYSON AVE", 6);
		addStopPropability("5TH AVE AT THACKERAY AVE", 10);
		addStopPropability("5TH AVE AT WASHINGTON PL", 1);
		addStopPropability("5TH AVE AT WILKINS AVE FS", 1);
		addStopPropability("5TH AVE AT WOOD ST", 13);
		addStopPropability("5TH AVE AT WOOD ST NS", 1);
		addStopPropability("5TH AVE OPP #2358", 2);
		addStopPropability("5TH AVE OPP #2410 (ACTA PROGRAM)", 10);
		addStopPropability("5TH AVE OPP AIKEN AVE", 1);
		addStopPropability("5TH AVE OPP ATWOOD ST", 26);
		addStopPropability("5TH AVE OPP BEECHWOOD BLVD", 2);
		addStopPropability("5TH AVE OPP BELLEFONTE ST", 1);
		addStopPropability("5TH AVE OPP CLYDE ST", 1);
		addStopPropability("5TH AVE OPP CRAFT AVE (CARLOW UNIV)", 3);
		addStopPropability("5TH AVE OPP DIAMOND ST", 2);
		addStopPropability("5TH AVE OPP HIGHLAND AVE", 2);
		addStopPropability("5TH AVE OPP SENECA ST", 1);
		addStopPropability("5TH AVE OPP TENNYSON AVE", 2);
		addStopPropability("5TH AVE OPP THACKERAY AVE", 6);
		addStopPropability("5TH AVE OPP VAN BRAAM ST", 2);
		addStopPropability("6TH AVE AT BIGELOW BLVD", 1);
		addStopPropability("6TH AVE AT CENTRE AVE", 3);
		addStopPropability("6TH AVE AT SMITHFIELD ST", 15);
		addStopPropability("6TH AVE AT WOOD ST", 6);
		addStopPropability("7TH AVE AT WILLIAM PENN PL", 1);
		addStopPropability("AMITY ST OPP TARGET DR", 1);
		addStopPropability("BEELER ST AT BEELERMONT PL", 1);
		addStopPropability("BEELER ST AT FORBES AVE", 2);
		addStopPropability("BEELER ST AT FORBES AVE FS", 2);
		addStopPropability("BEELER ST OPP OLYMPIA PL", 1);
		addStopPropability("BRADDOCK AVE AT OVERTON ST", 5);
		addStopPropability("CENTRE AVE AT AIKEN AVE", 11);
		addStopPropability("CENTRE AVE AT CYPRESS ST", 5);
		addStopPropability("CENTRE AVE AT GRAHAM ST", 15);
		addStopPropability("CENTRE AVE AT MILLVALE AVE", 4);
		addStopPropability("CENTRE AVE AT MOREWOOD AVE", 6);
		addStopPropability("CENTRE AVE AT NEGLEY AVE (MARKET DISTRICT)", 6);
		addStopPropability("CENTRE AVE OPP CYPRESS ST", 12);
		addStopPropability("CENTRE AVE OPP NEVILLE ST", 2);
		addStopPropability("CENTRE AVE OPP SHADYSIDE HOSPITAL", 5);
		addStopPropability("CRAIG ST AT 5TH AVE", 17);
		addStopPropability("CRAIG ST AT BAYARD ST", 2);
		addStopPropability("CRAIG ST AT CENTRE AVE FS", 13);
		addStopPropability("CRAIG ST AT FORBES AVE", 1);
		addStopPropability("CRAIG ST AT PARK PLAZA", 3);
		addStopPropability("CRAIG ST AT WINTHROP (BLIND ASSOCIATION)", 4);
		addStopPropability("DALLAS AVE AT REYNOLDS ST", 39);
		addStopPropability("DALLAS AVE OPP EDGERTON AVE", 4);
		addStopPropability("E CARSON ST AT S 19TH ST", 2);
		addStopPropability("E CARSON ST AT S 21ST ST", 6);
		addStopPropability("E CARSON ST AT S 22ND ST", 1);
		addStopPropability("E CARSON ST AT S 27TH ST", 1);
		addStopPropability("EAST BUSWAY AT WILKINSBURG STATION D", 1);
		addStopPropability("ELLSWORTH AVE AT AIKEN AVE", 7);
		addStopPropability("ELLSWORTH AVE AT CATHEDRAL MANSIONS", 2);
		addStopPropability("ELLSWORTH AVE AT COLLEGE ST", 7);
		addStopPropability("ELLSWORTH AVE AT ELLSWORTH PL", 1);
		addStopPropability("ELLSWORTH AVE AT MARYLAND AVE", 2);
		addStopPropability("ELLSWORTH AVE AT SHADY AVE", 1);
		addStopPropability("ELLSWORTH AVE AT SUMMERLEA ST", 2);
		addStopPropability("FORBES AVE AT ATWOOD ST", 4);
		addStopPropability("FORBES AVE AT BEECHWOOD BLVD", 8);
		addStopPropability("FORBES AVE AT BEELER ST", 5);
		addStopPropability("FORBES AVE AT BIGELOW BLVD (SCHENLEY DR)", 1);
		addStopPropability("FORBES AVE AT BOUQUET ST FS", 1);
		addStopPropability("FORBES AVE AT BOYD ST (DUQUESNE UNIV)", 1);
		addStopPropability("FORBES AVE AT BRADDOCK AVE", 4);
		addStopPropability("FORBES AVE AT BRADDOCK AVE FS", 1);
		addStopPropability("FORBES AVE AT CRAFT AVE FS", 1);
		addStopPropability("FORBES AVE AT CRAIG ST", 68);
		addStopPropability("FORBES AVE AT DALLAS AVE", 6);
		addStopPropability("FORBES AVE AT DENNISTON ST", 20);
		addStopPropability("FORBES AVE AT DITHRIDGE ST", 5);
		addStopPropability("FORBES AVE AT EAST END AVE", 1);
		addStopPropability("FORBES AVE AT HAMBURG HALL", 110);
		addStopPropability("FORBES AVE AT MAGEE ST", 1);
		addStopPropability("FORBES AVE AT MCKEE PL", 5);
		addStopPropability("FORBES AVE AT MOREWOOD AVE FS (CARNEGIE MELLON)", 94);
		addStopPropability("FORBES AVE AT MURDOCH ST", 15);
		addStopPropability("FORBES AVE AT MURRAY AVE", 24);
		addStopPropability("FORBES AVE AT MURRAY AVE FS", 16);
		addStopPropability("FORBES AVE AT NORTHUMBERLAND ST", 5);
		addStopPropability("FORBES AVE AT NORTHUMBERLAND ST FS", 4);
		addStopPropability("FORBES AVE AT PEEBLES ST", 4);
		addStopPropability("FORBES AVE AT PLAINFIELD ST", 4);
		addStopPropability("FORBES AVE AT PRIDE ST (MERCY HOSPITAL)", 1);
		addStopPropability("FORBES AVE AT SCHENLEY DR (GOLF COURSE)", 9);
		addStopPropability("FORBES AVE AT SHADY AVE", 16);
		addStopPropability("FORBES AVE AT STEVENSON ST", 1);
		addStopPropability("FORBES AVE AT VAN BRAAM ST", 4);
		addStopPropability("FORBES AVE AT WIGHTMAN ST", 6);
		addStopPropability("FORBES AVE OPP BEELER ST", 20);
		addStopPropability("FORBES AVE OPP BELLEFIELD NS", 4);
		addStopPropability("FORBES AVE OPP CRAIG ST", 20);
		addStopPropability("FORBES AVE OPP HAMBURG HALL", 53);
		addStopPropability("FORBES AVE OPP MARGARET MORRISON ST", 5);
		addStopPropability("FORBES AVE OPP MOREWOOD AVE (CARNEGIE MELLON)", 39);
		addStopPropability("FORBES AVE OPP PLAINFIELD ST", 11);
		addStopPropability("FREEPORT RD OPP CENTER AVE", 11);
		addStopPropability("FREEPORT RD OPP WESTERN AVE", 1);
		addStopPropability("GIANT EAGLE DR AT IGGLE VIDEO", 2);
		addStopPropability("HIGHLAND AVE AT ALDER ST", 5);
		addStopPropability("HIGHLAND AVE AT BAUM BLVD", 1);
		addStopPropability("HIGHLAND AVE AT EAST LIBERTY BLVD", 6);
		addStopPropability("HIGHLAND AVE AT ELLSWORTH AVE STEPS", 3);
		addStopPropability("HIGHLAND AVE AT ELWOOD ST", 1);
		addStopPropability("HIGHLAND AVE AT HAMPTON ST", 1);
		addStopPropability("HIGHLAND AVE AT JACKSON ST", 5);
		addStopPropability("HIGHLAND AVE AT PENN AVE", 3);
		addStopPropability("HIGHLAND AVE AT WALNUT ST", 1);
		addStopPropability("LIBERTY AVE AT 25TH ST", 1);
		addStopPropability("LIBERTY AVE AT 26TH ST", 1);
		addStopPropability("LIBERTY AVE AT 39TH ST", 1);
		addStopPropability("LIBERTY AVE AT 40TH ST NS", 1);
		addStopPropability("LIBERTY AVE AT 5TH AVE", 1);
		addStopPropability("LIBERTY AVE AT EDMOND ST", 15);
		addStopPropability("LIBERTY AVE AT ELLA ST", 3);
		addStopPropability("LIBERTY AVE AT GATEWAY #4", 4);
		addStopPropability("LIBERTY AVE AT MARKET ST", 1);
		addStopPropability("LIBERTY AVE AT MILLVALE AVE (WEST PENN HOSP)", 1);
		addStopPropability("LIBERTY AVE AT WOOD ST", 9);
		addStopPropability("LIBERTY AVE OPP 17TH ST", 2);
		addStopPropability("MILLVALE AVE AT BAUM BLVD", 1);
		addStopPropability("MILLVALE AVE AT CENTRE AVE", 1);
		addStopPropability("MILLVALE AVE AT CYPRESS ST", 10);
		addStopPropability("MILLVALE AVE AT MITRE WAY", 1);
		addStopPropability("MILLVALE AVE AT YEW ST", 2);
		addStopPropability("MONONGAHELA AVE OPP DUQUESNE AVE", 1);
		addStopPropability("MURRAY AVE AT #2825", 2);
		addStopPropability("MURRAY AVE AT BARTLETT ST", 7);
		addStopPropability("MURRAY AVE AT BARTLETT ST FS", 6);
		addStopPropability("MURRAY AVE AT BEACON ST", 6);
		addStopPropability("MURRAY AVE AT BEACON ST NS", 4);
		addStopPropability("MURRAY AVE AT DARLINGTON RD", 6);
		addStopPropability("MURRAY AVE AT FLEMINGTON ST", 5);
		addStopPropability("MURRAY AVE AT FLEMINGTON ST FS", 3);
		addStopPropability("MURRAY AVE AT FORWARD AVE", 53);
		addStopPropability("MURRAY AVE AT HAZELWOOD AVE", 1);
		addStopPropability("MURRAY AVE AT HAZELWOOD AVE FS", 1);
		addStopPropability("MURRAY AVE AT HOBART ST", 6);
		addStopPropability("MURRAY AVE AT LILAC ST", 1);
		addStopPropability("MURRAY AVE AT LORETTA ST", 2);
		addStopPropability("MURRAY AVE AT MORROWFIELD AVE FS", 11);
		addStopPropability("MURRAY AVE AT PHILLIPS AVE", 6);
		addStopPropability("MURRAY AVE AT WELFER ST", 24);
		addStopPropability("MURRAY AVE OPP BURCHFIELD AVE", 1);
		addStopPropability("MURRAY AVE OPP MORROWFIELD AVE", 26);
		addStopPropability("MURRAY AVE OPP MORROWFIELD AVE", 2);
		addStopPropability("NEGLEY AVE AT BRYANT ST", 19);
		addStopPropability("NEGLEY AVE AT CENTRE AVE", 8);
		addStopPropability("NEGLEY AVE AT HAMPTON ST", 6);
		addStopPropability("NEGLEY AVE AT JACKSON ST", 1);
		addStopPropability("NEGLEY AVE AT PENN AVE", 1);
		addStopPropability("NEGLEY AVE AT RIPPEY ST", 1);
		addStopPropability("NEGLEY AVE AT WELLESLEY AVE NS", 2);
		addStopPropability("NEGLEY AVE OPP RIPPEY ST FS", 16);
		addStopPropability("NEGLEY AVE OPP WELLESLEY AVE", 37);
		addStopPropability("NEVILLE ST AT ELLSWORTH AVE NS", 2);
		addStopPropability("NORTH AVE AT REDDOUR ST", 1);
		addStopPropability("PARK MANOR BLVD AT IKEA", 2);
		addStopPropability("PARK MANOR BLVD AT ROBINSON LN", 1);
		addStopPropability("PEEBLES ST AT FORBES AVE", 2);
		addStopPropability("PEEBLES ST AT KELLY AVE", 1);
		addStopPropability("PEEBLES ST OPP FORBES AVE", 2);
		addStopPropability("PENN AVE AT 36TH ST", 2);
		addStopPropability("PENN AVE AT 40TH ST", 1);
		addStopPropability("PENN AVE AT 40TH ST", 3);
		addStopPropability("PENN AVE AT BEATTY ST", 3);
		addStopPropability("PENN AVE AT BRADDOCK AVE", 1);
		addStopPropability("PENN AVE AT BUTLER ST", 1);
		addStopPropability("PENN AVE AT EASTSIDE III DR", 2);
		addStopPropability("PENN AVE AT HIGHLAND AVE", 5);
		addStopPropability("PENN AVE AT LANG AVE", 1);
		addStopPropability("PENN AVE AT LINDEN", 1);
		addStopPropability("PENN AVE AT MAIN ST", 28);
		addStopPropability("PENN AVE AT SHERIDAN SQ", 2);
		addStopPropability("PENN AVE AT ST CLAIR ST", 1);
		addStopPropability("PENN AVE AT VILLAGE OF EASTSIDE SHPG CTR", 2);
		addStopPropability("PENN AVE AT WHITFIELD ST", 12);
		addStopPropability("PENN AVE OPP BAKERY SQUARE", 1);
		addStopPropability("PGH INTERNL AIRPORT LOWER LEVEL (DOOR 6)", 9);
		addStopPropability("RANKIN BLVD OPP BLDG #5", 1);
		addStopPropability("ROBINSON TOWNE CTR DR AT PARK MANOR BLVD", 1);
		addStopPropability("ROWLAND CONNECTOR OPP HIGHRISE", 2);
		addStopPropability("S 18TH ST AT MISSION ST", 1);
		addStopPropability("S 18TH ST AT SARAH ST", 3);
		addStopPropability("S 18TH ST OPP MT OLIVER ST FS", 1);
		addStopPropability("SHJ BUS TURNAROUND SHELTER", 7);
		addStopPropability("ST CLAIR ST AT BRYANT ST", 2);
		addStopPropability("ST CLAIR ST AT BUNKER HILL APTS", 3);
		addStopPropability("STANWIX ST OPP 4TH AVE", 16);
		addStopPropability("SWISSVALE AVE OPP MAPLE AVE", 1);
		addStopPropability("TARGET DR AT TARGET PERIMETER RD", 6);
		addStopPropability("TRENTON AVE AT FRANKLIN", 1);
		addStopPropability("TRENTON AVE AT SOUTH", 1);
		addStopPropability("W CARSON ST AT DUQUESNE INCLINE", 3);
		addStopPropability("W CARSON ST AT GATEWAY VIEW PLAZA", 1);
		addStopPropability("WALLACE AVE AT COAL ST", 1);
		addStopPropability("WALLACE AVE AT HAY ST", 1);
		addStopPropability("WALLACE AVE AT PITT ST", 1);
		addStopPropability("WATERFRONT DR AT 5TH AVE FS", 3);
		addStopPropability("WATERFRONT DR AT LOEW'S THEATRE", 9);
		addStopPropability("WATERWORKS MALL AT GIANT EAGLE", 6);
		addStopPropability("WATERWORKS MALL OPP BED BATH & BEYOND (WALMART)", 1);
		addStopPropability("WATERWORKS MALL OPP MARSHALLS", 1);
		addStopPropability("WATERWORKS PLAZA OPP GIANT EAGLE", 7);
		addStopPropability("WEST BUSWAY AT BELL STATION B", 2);
		addStopPropability("WEST BUSWAY AT BELL STATION C", 2);
		addStopPropability("WEST BUSWAY AT CRAFTON STATION B", 1);
		addStopPropability("WEST BUSWAY AT CRAFTON STATION C", 2);
		addStopPropability("WEST BUSWAY AT IDLEWOOD STATION B", 1);
		addStopPropability("WEST BUSWAY AT IDLEWOOD STATION C", 1);
		addStopPropability("WEST BUSWAY AT INGRAM STATION C", 1);
		addStopPropability("WILKINS AVE AT #6640", 9);
		addStopPropability("WILKINS AVE AT #6647", 8);
		addStopPropability("WILKINS AVE AT BARNSDALE ST", 6);
		addStopPropability("WILKINS AVE AT BEECHWOOD BLVD", 80);
		addStopPropability("WILKINS AVE AT BEECHWOOD BLVD", 3);
		addStopPropability("WILKINS AVE AT BELLEROCK ST", 1);
		addStopPropability("WILKINS AVE AT FAIROAKS ST", 6);
		addStopPropability("WILKINS AVE AT MURRAY AVE", 4);
		addStopPropability("WILKINS AVE AT NEGLEY AVE", 7);
		addStopPropability("WILKINS AVE AT NEGLEY AVE", 1);
		addStopPropability("WILKINS AVE AT WIGHTMAN ST", 76);
		addStopPropability("WILKINS AVE AT WOODLAND RD", 1);
		addStopPropability("WILKINS AVE AT WORTH ST", 8);
		addStopPropability("WILKINS AVE OPP BARNSDALE ST", 23);
		addStopPropability("WILKINS AVE OPP BELLEROCK ST", 2);
		addStopPropability("WILKINS AVE OPP MURRAY AVE", 7);
		addStopPropability("WILKINS AVE OPP WORTH ST", 39);
		addStopPropability("WILKINS AVE OPP WORTH ST", 1);
		addStopPropability("WOOD ST AT FRANKLIN AVE", 1);
		addStopPropability("WOOD ST AT SOUTH AVE", 1);
		addStopPropability("WOOD ST AT SUSQUEHANNA ST FS", 1);
		addStopPropability("WOOD ST AT WALLACE AVE", 5);
		addStopPropability("WOODSTOCK AVE AT WOODSTOCK TERRACE", 1);

	}
	// CHECKSTYLE:ON

}
