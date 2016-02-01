package edu.cmu.cs.cs214.hw5.plugin;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import edu.cmu.cs.cs214.hw5.framework.AnalysisPluginInterface;
import edu.cmu.cs.cs214.hw5.framework.Person;

/**
 * Analyze the personality of people
 * 
 * @author xuehao
 */
public class PersonalityPlugin implements AnalysisPluginInterface {
	private static final String DES_FRECQUENCY_0 = "Quiet";
	private static final String DES_FRECQUENCY_1 = "Sprightly";
	private static final String DES_LENGTH_0 = "Good Speaker";
	private static final String DES_LENGTH_1 = "Good Listener";
	private static final String DES_FRIEND_0 = "Homebody";
	private static final String DES_FRIEND_1 = "Sociable";
	private static final String DES_EMOTION_0 = "Rational";
	private static final String DES_EMOTION_1 = "Emotional";
	private List<Person> myData;
	private List<Person> same;
	private List<Person> diff;
	private Person user;
	private static final int PERCENT = 100;
	private static final int FONT_NUM = 12;
	private static final int BOUNDARY = 2;
	private static final int WID = 600;
	private static final int HEI = 500;

	/**
	 * constructor
	 */
	public PersonalityPlugin() {
		myData = new ArrayList<Person>();
	}

	@Override
	public String getTitle() {
		return "Personality Analysis";
	}

	@Override
	public String getDescription() {
		String p = "We may know you better than yourself! You are a";
		if (user.getFrequencyFactor() < BOUNDARY)
			p += DES_FRECQUENCY_0 + ",";
		else
			p += DES_FRECQUENCY_1 + ",";

		if (user.getPostLengthFactor() < BOUNDARY)
			p += DES_LENGTH_0 + ",";
		else
			p += DES_LENGTH_1 + ",";

		if (user.getFriendFactor() < BOUNDARY)
			p += DES_FRIEND_0 + ",";
		else
			p += DES_FRIEND_1 + ",";

		if (user.getEmoticonFactor() < BOUNDARY)
			p += DES_EMOTION_0;
		else
			p += DES_EMOTION_1;
		p += "person!";
		return p;
	}

	@Override
	public void setData(List<Person> data) {
		myData = data;
	}

	@Override
	public JPanel getPanel() {
		analyze();
		if(myData.size() == 0)
			throw new RuntimeException("You should  at least add one friend!");
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Similar Personality = "
				+ (same.size() * PERCENT / myData.size()) + "%", same.size()
				* PERCENT / myData.size());
		dataset.setValue("Opposite Personality = "
				+ (diff.size() * PERCENT / myData.size()) + "%", diff.size()
				* PERCENT / myData.size());
		dataset.setValue("Others = " + ((myData.size()-diff.size()-same.size()) * PERCENT / myData.size())
				+ "%", (myData.size()-diff.size()-same.size()) * PERCENT / myData.size());

		JFreeChart chart = ChartFactory.createPieChart("Personality Analysis", 
				dataset, // data
				true, // include legend
				true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, FONT_NUM));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		JPanel p = new ChartPanel(chart);
		p.setSize(WID, HEI);
		return p;

	}

	private void analyze() {
		user = null;
		ArrayList<Factor> factor = new ArrayList<Factor>();
		for (Person p : myData) {
			if (p.getFriendshipLevel() == 0) {
				user = p;
				myData.remove(p);
			}
			break;
		}

		for (int i = 0; i < myData.size(); i++) {
			Person p = myData.get(i);
			int num = 0;
			if (p.getFrequencyFactor() == user.getFrequencyFactor())
				num++;
			if (p.getPostLengthFactor() == user.getPostLengthFactor())
				num++;
			if (p.getEmoticonFactor() == user.getEmoticonFactor())
				num++;
			if (p.getFriendFactor() == user.getFriendFactor())
				num++;
			Factor f = new Factor(i, num);
			factor.add(f);
		}
		Collections.sort(factor);

		same = new ArrayList<Person>();
		diff = new ArrayList<Person>();
		for (Factor f : factor) {
			if (f.getNum() >= 1 + 1 + 1)
				same.add(myData.get(f.getIndex()));
			else if (f.getNum() <= 1)
				diff.add(myData.get(f.getIndex()));
		}

		for (Person p : same)
			System.out.println(p.getName());
	}

	/**
	 * Inner class, used for sorting
	 * @author xuehao
	 *
	 */
	private static class Factor implements Comparable<Factor> {
		private int index;
		private int num;

		public Factor(int id, int n) {
			index = id;
			num = n;
		}

		public int getNum() {
			return num;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public int compareTo(Factor other) {
			return this.getNum() - other.getNum();
		}

	}

}
