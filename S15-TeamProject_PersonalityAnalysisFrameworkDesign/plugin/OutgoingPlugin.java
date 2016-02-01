package edu.cmu.cs.cs214.hw5.plugin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.cmu.cs.cs214.hw5.framework.AnalysisPluginInterface;
import edu.cmu.cs.cs214.hw5.framework.Person;

/**
 * Analyze the extroversion level of people
 * 
 * @author xuehao
 */
public class OutgoingPlugin implements AnalysisPluginInterface {
	private static final double ROTATION = 6.0;
	private static final int WID = 600;
	private static final int HEI = 500;
	private static final int MAX = 10;
	private List<Person> myData;
	private List<PersonWithFactor> pfList;

	/**
	 * constructor
	 */
	public OutgoingPlugin() {
		myData = new ArrayList<Person>();
	}

	@Override
	public String getTitle() {
		return "Who is the most outgoing person?";
	}

	@Override
	public String getDescription() {
		return "These analysis plugin analyze the personality of you and your friends and calculate the extroversion level of you and your friends";
	}

	@Override
	public void setData(List<Person> data) {
		myData = data;
	}

	@Override
	public JPanel getPanel() {
		analyze();
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < pfList.size() && i < MAX; i++) {
			dataset.addValue(pfList.get(i).getFactor(), pfList.get(i)
					.getPerson().getName(), pfList.get(i)
					.getPerson().getName());
		}
		JFreeChart chart = ChartFactory.createBarChart(
				"Extroversion Level Analysis", // chart title
				"Person", // domain axis label
				"Extroversion Level", // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / ROTATION));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setSize(WID, HEI);
		return chartPanel;
	}

	private void analyze() {
		pfList = new ArrayList<PersonWithFactor>();
		for (Person p : myData) {
			double num = p.getEmoticonFactor() + p.getFrequencyFactor()
					+ p.getPostLengthFactor() + p.getFriendFactor();
			PersonWithFactor pf = new PersonWithFactor(p, num);
			pfList.add(pf);
		}
		Collections.sort(pfList);
	}

	/**
	 * Inner class , use for sorting
	 * @author xuehao
	 *
	 */
	private static class PersonWithFactor implements
			Comparable<PersonWithFactor> {
		private Person person;
		private double factor;

		public PersonWithFactor(Person p, double f) {
			person = p;
			factor = f;
		}

		public double getFactor() {
			return factor;
		}

		public Person getPerson() {
			return person;
		}

		@Override
		public int compareTo(PersonWithFactor other) {
			return -(int) (this.factor - other.getFactor());
		}

	}

}
