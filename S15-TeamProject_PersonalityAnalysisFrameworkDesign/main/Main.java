package edu.cmu.cs.cs214.hw5.main;

import javax.swing.SwingUtilities;

import edu.cmu.cs.cs214.hw5.framework.Framework;
import edu.cmu.cs.cs214.hw5.framework.gui.FrameworkGui;
import edu.cmu.cs.cs214.hw5.plugin.BloodTypePlugin;
import edu.cmu.cs.cs214.hw5.plugin.FacebookData;
import edu.cmu.cs.cs214.hw5.plugin.OutgoingPlugin;
import edu.cmu.cs.cs214.hw5.plugin.PersonalityPlugin;
import edu.cmu.cs.cs214.hw5.plugin.TwitterData;

/**
 * The main class. Use this class to register plugins.
 * 
 * @author Tao
 *
 */
public class Main {

	/**
	 * Main method.
	 * 
	 * @param args
	 *                Means nothing.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndStartFramework();
			}
		});
	}

	private static void createAndStartFramework() {
		Framework core = new Framework();
		FrameworkGui gui = new FrameworkGui(core);

		core.registerGuiChangeListener(gui);

		// Register your plugins
		core.registerDataPlugin(new TwitterData());
		core.registerDataPlugin(new FacebookData());
		core.registerAnalysisPlugin(new OutgoingPlugin());
		core.registerAnalysisPlugin(new PersonalityPlugin());
		core.registerAnalysisPlugin(new BloodTypePlugin());
	}

}
