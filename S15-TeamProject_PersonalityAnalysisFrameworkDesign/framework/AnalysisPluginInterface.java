package edu.cmu.cs.cs214.hw5.framework;

import java.util.List;

import javax.swing.JPanel;

/**
 * The interface that defines analysis plugins.
 * 
 * @author Tao
 *
 */
public interface AnalysisPluginInterface {

	/**
	 * The framework will call this method to get the title of this analysis
	 * plugin.
	 * 
	 * @return The title.
	 */
	String getTitle();

	/**
	 * The framework will call this method to get the description of this
	 * analysis plugin.
	 * 
	 * @return The description.
	 */
	String getDescription();

	/**
	 * The framework will use the method to pass by a complete set of user's
	 * data.
	 * 
	 * @param data
	 *                A list of all person objects.
	 */
	void setData(List<Person> data);

	/**
	 * The framework will call this method to get the result. The result
	 * should be presented in a JPanel.
	 * 
	 * @return A JPanel the presents the result.
	 */
	JPanel getPanel();

}
