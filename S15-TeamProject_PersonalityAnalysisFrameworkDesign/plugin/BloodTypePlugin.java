package edu.cmu.cs.cs214.hw5.plugin;

import java.awt.Font;
import java.util.ArrayList;
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
 * Perdict the bloodtype of people
 * 
 * @author xuehao
 */
public class BloodTypePlugin implements AnalysisPluginInterface {
	private static final int A_ID = 0;
	private static final int B_ID = 1;
	private static final int O_ID = 2;
	private static final int PERCENT = 100;
	private static final int FONT_NUM = 12;
	private static final int BOUNDARY = 2;
	private static final int WID = 600;
	private static final int HEI = 500;

	private List<Person> myData;
	private List<Person> aList;
	private List<Person> bList;
	private List<Person> oList;
	private List<Person> abList;

	/**
	 * constructor
	 */
	public BloodTypePlugin() {
		myData = new ArrayList<Person>();
	}

	@Override
	public String getTitle() {
		return "BloodType Prediction";
	}

	@Override
	public String getDescription() {
		return "Different bloodtype groups may have different behavior"
				+ " and personality.This plugin analyze you and your friends' "
				+ "personality and predict you and your friends' bloodtype";
	}

	@Override
	public void setData(List<Person> data) {
		myData = data;
	}

	@Override
	public JPanel getPanel() {
		aList = new ArrayList<Person>();
		bList = new ArrayList<Person>();
		oList = new ArrayList<Person>();
		abList = new ArrayList<Person>();
		for (Person p : myData) {
			if (getBloodType(p).equals("A"))
				aList.add(p);
			else if (getBloodType(p).equals("B"))
				bList.add(p);
			else if (getBloodType(p).equals("O"))
				oList.add(p);
			else
				abList.add(p);
		}
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("A = " + (aList.size() * PERCENT / myData.size())
				+ "%", aList.size() * PERCENT / myData.size());
		dataset.setValue("B = " + (bList.size() * PERCENT / myData.size())
				+ "%", bList.size() * PERCENT / myData.size());
		dataset.setValue("O = " + (oList.size() * PERCENT / myData.size())
				+ "%", oList.size() * PERCENT / myData.size());
		dataset.setValue("AB = " + (abList.size() * PERCENT / myData.size())
				+ "%", abList.size() * PERCENT / myData.size());

		JFreeChart chart = ChartFactory.createPieChart("Bloodtype prediction", 
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

	private String getBloodType(Person p) {
		int[][] bloodtype = { { 1, 0, 1, 0 }, { 0, 1, 0, 1 }, { 1, 1, 0, 0 },
				{ 1, 1, 1, 0 } };
		int[] ans = new int[bloodtype.length];

		for (int i = 0; i < bloodtype.length; i++) {
			int[] t = bloodtype[i];
			int num = 0;
			int index = 0;
			if (p.getFrequencyFactor() / BOUNDARY == t[index++])
				num++;
			if (p.getPostLengthFactor() / BOUNDARY == t[index++])
				num++;
			if (p.getFriendFactor() / BOUNDARY == t[index++])
				num++;
			if (p.getEmoticonFactor() / BOUNDARY == t[index])
				num++;
			ans[i] = num;
		}
		int max = 0;
		int index = 0;
		for (int i = 0; i < ans.length; i++) {
			if (max < ans[i]) {
				max = ans[i];
				index = i;
			}
		}

		if (index == A_ID)
			return "A";
		if (index == B_ID)
			return "B";
		if (index == O_ID)
			return "O";
		else
			return "AB";
	}

}
