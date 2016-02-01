package edu.cmu.cs.cs214.hw5.framework;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * The main class of framework core implementation. GUI will only need to
 * communicate with this class.
 * 
 * @author Tao
 *
 */
public class Framework {

	// This number will define the total how many level of friends will be
	// got from social network.
	// private static final int MAX_NUMBER_OF_FRIENDSHIP_LEVEL = 2;

	private String mainUserId; // The id of the source user. Friendship tree
					// will be created based on this person.

	private List<String> friendsIds = new ArrayList<String>();
	private List<AnalysisPluginInterface> analysisPlugins = new ArrayList<AnalysisPluginInterface>();
	private List<DataPluginInterface> dataPlugins = new ArrayList<DataPluginInterface>();
	private List<Person> personList = new ArrayList<Person>();
	private List<GuiChangeListener> guiChangeListeners = new ArrayList<GuiChangeListener>();

	private DataPluginInterface selectedDataPlugin;
	private AnalysisPluginInterface selectedAnalysisPlugin;

	private boolean alreadyBuiltData = false;

	/**
	 * Constructor. Do noting
	 */
	public Framework() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Call to register data plugin.
	 * 
	 * @param plugin
	 *                The plugin to register.
	 */
	public void registerDataPlugin(DataPluginInterface plugin) {
		dataPlugins.add(plugin);
		onDataPluginRegistered(plugin);
	}

	/**
	 * Call to register analysis plugin.
	 * 
	 * @param plugin
	 *                The plugin to register.
	 */
	public void registerAnalysisPlugin(AnalysisPluginInterface plugin) {
		analysisPlugins.add(plugin);
		onAnalysisPluginRegistered(plugin);
	}

	/**
	 * Call to register {@link GuiChangeListener}.
	 * 
	 * @param listener
	 *                The plugin to register.
	 */
	public void registerGuiChangeListener(GuiChangeListener listener) {
		guiChangeListeners.add(listener);
	}

	/**
	 * Use this method to set the main user id.
	 * 
	 * @param id
	 *                The id of the main user.
	 */
	public void setMainUserId(String id) {
		this.mainUserId = id;
	}

	/**
	 * Use this method to add a friends' id.
	 * 
	 * @param id
	 *                The friend's id.
	 */
	public void addFriendId(String id) {
		friendsIds.add(id);
	}

	// /**
	// * Call this method to get a list of {@link DataPluginInterface}s
	// *
	// * @return A list of all registered {@link DataPluginInterface}s
	// */
	// public List<DataPluginInterface> getDataPlugins() {
	// return dataPlugins;
	// }
	//
	// /**
	// * Call this method to get a list of {@link AnalysisPluginInterface}s
	// *
	// * @return A list of all registered {@link AnalysisPluginInterface}s
	// */
	// public List<AnalysisPluginInterface> getAnalysisPlugins() {
	// return analysisPlugins;
	// }

	/**
	 * Use the method to set the data plugin.
	 * 
	 * @param plugin
	 *                The selected data plugin.
	 */
	public void selectDataPlugin(DataPluginInterface plugin) {
		selectedDataPlugin = plugin;
		alreadyBuiltData = false;
	}

	/**
	 * Use this method to set the analysis plugin.
	 * 
	 * @param plugin
	 *                The selected analysis plugin.
	 */
	public void selectAnalysisPlugin(AnalysisPluginInterface plugin) {
		selectedAnalysisPlugin = plugin;
	}

	/**
	 * Call this method to start analysis.
	 */
	public void startAnalysis() {
		try {
			if (!alreadyBuiltData) {
				buildData();
			}

			passDataToAnalysisPlugin();

			// Update analysis plugin and results in GUI
			onAnalysisResultUpdated(selectedAnalysisPlugin
					.getPanel());
			onAnalysisPluginDescriptionChanged(selectedAnalysisPlugin
					.getDescription());
			onAnalysisPluginTitleChanged(selectedAnalysisPlugin
					.getTitle());

		} catch (Exception e) {
			System.err.println("Cannot analyze! Encounter NULL in data.");
		}

	}

	/**
	 * Call this method to clear all friends id.
	 */
	public void clearFriendsIdsListAndPerson() {
		friendsIds.clear();
		personList.clear();
	}

	/**
	 * A help method to get and build data from data plugin.
	 * 
	 * @return True if build succeed, false if not.
	 */
	private boolean buildData() {
		Person thisPerson = selectedDataPlugin.getPerson(mainUserId);
		if (thisPerson == null) {
			onGetMainUserFailed();
			return false;
		} else {
			thisPerson.setFriendshipLevel(0);
			personList.add(thisPerson);

			for (String id : friendsIds) {
				thisPerson = selectedDataPlugin.getPerson(id);
				if (thisPerson != null) {
					thisPerson.setFriendshipLevel(1);
					personList.add(thisPerson);
				}
			}
			alreadyBuiltData = true;
			return true;
		}
	}

	private void passDataToAnalysisPlugin() {
		List<Person> dataCopy = new ArrayList<Person>();
		dataCopy.addAll(personList);
		selectedAnalysisPlugin.setData(dataCopy);
	}

	private void onAnalysisResultUpdated(JPanel panel) {
		for (GuiChangeListener listener : guiChangeListeners) {
			listener.onAnalysisResultUpdated(panel);
		}
	}

	private void onAnalysisPluginTitleChanged(String s) {
		for (GuiChangeListener listener : guiChangeListeners) {
			listener.onAnalysisPluginTitleChanged(s);
		}
	}

	private void onAnalysisPluginDescriptionChanged(String s) {
		for (GuiChangeListener listener : guiChangeListeners) {
			listener.onAnalysisPluginDescriptionChanged(s);
		}
	}

	private void onGetMainUserFailed() {
		for (GuiChangeListener listener : guiChangeListeners) {
			listener.onGetMainUserFailed();
		}
	}

	private void onDataPluginRegistered(DataPluginInterface plugin) {
		for (GuiChangeListener listener : guiChangeListeners) {
			listener.onDataPluginRegistered(plugin);
		}
	}

	private void onAnalysisPluginRegistered(AnalysisPluginInterface plugin) {
		for (GuiChangeListener listener : guiChangeListeners) {
			listener.onAnalysisPluginRegistered(plugin);
		}
	}

	/**
	 * Only for test.
	 * 
	 * @return If personList is empty.
	 */
	public boolean isPersonListEmpty() {
		return personList.isEmpty();
	}
}
