package edu.cmu.cs.cs214.hw5.framework;

import javax.swing.JPanel;

/**
 * The interface that defines the change listener of gui.
 * 
 * @author Tao
 *
 */
public interface GuiChangeListener {

	/**
	 * Call this method to update analysis result.
	 * 
	 * @param panel
	 *                The panel that contains results which will be shown in
	 *                GUI.
	 */
	void onAnalysisResultUpdated(JPanel panel);

	/**
	 * Call this method to update the title of current analysis plugin.
	 * 
	 * @param s
	 *                The title of the plugin.
	 */
	void onAnalysisPluginTitleChanged(String s);

	/**
	 * Call this method to update the description of current analysis
	 * plugin.
	 * 
	 * @param d
	 *                The description of the plugin
	 */
	void onAnalysisPluginDescriptionChanged(String d);

	/**
	 * Call this method when unable to get the main user.
	 */
	void onGetMainUserFailed();

	/**
	 * Call the method when a new data plugin is registerd.
	 * 
	 * @param plugin
	 *                The data plugin
	 */
	void onDataPluginRegistered(DataPluginInterface plugin);

	/**
	 * Call this method when a new analysis plugin is registered
	 * 
	 * @param plugin
	 *                The {@link AnalysisPluginInterface}
	 */
	void onAnalysisPluginRegistered(AnalysisPluginInterface plugin);

}
