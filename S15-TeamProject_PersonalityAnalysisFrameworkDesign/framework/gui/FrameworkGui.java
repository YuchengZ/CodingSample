package edu.cmu.cs.cs214.hw5.framework.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import edu.cmu.cs.cs214.hw5.framework.AnalysisPluginInterface;
import edu.cmu.cs.cs214.hw5.framework.DataPluginInterface;
import edu.cmu.cs.cs214.hw5.framework.Framework;
import edu.cmu.cs.cs214.hw5.framework.GuiChangeListener;

/**
 * The framework GUI, client.
 * 
 * @author Tao
 *
 */
public class FrameworkGui implements GuiChangeListener {

	private static final String INPUT_ERROR = "Input error";
	private static final String NO_VALID_MAIN_USER = "The main user is not valid!";

	// Default JFrame title.
	private static final String DEFAULT_TITLE = "Social network framework";

	// Menu titles.
	private static final String MENU_TITLE = "File";
	private static final String MENU_DATA_SOURCE = "Data source";
	private static final String MENU_ANALYSIS_METHOD = "Analysis method";
	private static final String MENU_ANALYZE = "Analyze";
	private static final String MENU_EXIT = "Exit";
	private static final String INI_DATA_PLUGIN_DESCRIPTION = "Please slect a data plugin first";

	// Dialog titles and messages.
	// private static final String ADD_PLAYER_TITLE = "Add New Player";

	// The parent JFrame window.
	private final JFrame frame;

	private final JPanel outerPanel;

	// Menu-related stuff.
	private final JMenuItem analyzeMenuButton;
	private final JMenu dataPluginMenu;
	private final JMenu analysisMenu;
	private final ButtonGroup dataPluginGroup = new ButtonGroup();
	private final ButtonGroup analysisPluginGroup = new ButtonGroup();

	private MainUserInput mainUserText = new MainUserInput(this,
			INI_DATA_PLUGIN_DESCRIPTION);
	private FriendIdInput friendInputText = new FriendIdInput(this,
			INI_DATA_PLUGIN_DESCRIPTION);
	private AnalysisResultPanel analysisResultPanel = new AnalysisResultPanel();

	private Framework core;

	/**
	 * Constructor, pass by the {@link FrameworkGui}.
	 * 
	 * @param fw
	 *                The {@link FrameworkGui}.
	 */
	public FrameworkGui(Framework fw) {
		core = fw;
		frame = new JFrame(DEFAULT_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setPreferredSize(new Dimension(500, 500));
		frame.pack();

		// Add the frame's panels to the view.
		outerPanel = new JPanel(new BorderLayout());

		outerPanel.add(mainUserText, BorderLayout.CENTER);

		frame.add(outerPanel);

		// Set-up the menu bar.
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(MENU_TITLE);
		fileMenu.setMnemonic(KeyEvent.VK_F);

		// Add an 'Data source' menu item.
		dataPluginMenu = new JMenu(MENU_DATA_SOURCE);
		dataPluginMenu.setMnemonic(KeyEvent.VK_N);
		fileMenu.add(dataPluginMenu);

		// Add an 'Analysis' menu item.
		analysisMenu = new JMenu(MENU_ANALYSIS_METHOD);
		analysisMenu.setMnemonic(KeyEvent.VK_N);
		fileMenu.add(analysisMenu);
		analysisMenu.setEnabled(false);

		// Add an 'Analyze' menu item.
		analyzeMenuButton = new JMenuItem(MENU_ANALYZE);
		analyzeMenuButton.setMnemonic(KeyEvent.VK_N);
		analyzeMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				core.startAnalysis();
				activateAnalysisResult();
			}
		});
		analyzeMenuButton.setEnabled(false);
		fileMenu.add(analyzeMenuButton);

		// Add a separator between 'New Game' and 'Exit' menu items.
		fileMenu.addSeparator();

		// Add an 'Exit' menu item.
		JMenuItem exitMenuItem = new JMenuItem(MENU_EXIT);
		exitMenuItem.setMnemonic(KeyEvent.VK_X);
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);

		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Call this method to set the main user id through GUI.
	 * 
	 * @param s
	 *                The user id.
	 */
	public void inputMainUser(String s) {
		core.setMainUserId(s);
	}

	/**
	 * Call this method to add a friend id through GUI.
	 * 
	 * @param s
	 *                The friend id.
	 */
	public void inputFriendId(String s) {
		core.addFriendId(s);
	}

	/**
	 * Call this method to activate main user id input panel.
	 */
	public void activateMainUserInputPanel() {
		outerPanel.removeAll();
		outerPanel.add(mainUserText);
		outerPanel.revalidate();
		outerPanel.repaint();
		frame.pack();
	}

	/**
	 * Call this method to activate friend id input panel.
	 */
	public void activateFriendIdInputPanel() {
		outerPanel.removeAll();
		outerPanel.add(friendInputText);
		outerPanel.revalidate();
		outerPanel.repaint();
		frame.pack();
	}

	@Override
	public void onAnalysisResultUpdated(JPanel panel) {
		analysisResultPanel.onResultChanged(panel);

	}

	@Override
	public void onAnalysisPluginTitleChanged(String s) {
		analysisResultPanel.onTitleChanged(s);

	}

	@Override
	public void onAnalysisPluginDescriptionChanged(String d) {
		analysisResultPanel.onDescriptionChanged(d);

	}

	@Override
	public void onGetMainUserFailed() {
		showErrorDialog(frame, INPUT_ERROR, NO_VALID_MAIN_USER);
		core.clearFriendsIdsListAndPerson();
		activateMainUserInputPanel();

	}

	@Override
	public void onDataPluginRegistered(final DataPluginInterface plugin) {
		JRadioButtonMenuItem dataPluginMenuItem = new JRadioButtonMenuItem(
				plugin.getPluginName());
		dataPluginMenuItem.setSelected(false);
		dataPluginMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				core.selectDataPlugin(plugin);
				mainUserText.onChangeDataPlugin(plugin);
				friendInputText.onChangeDataPlugin(plugin);
				core.clearFriendsIdsListAndPerson();
				activateMainUserInputPanel();
				analysisMenu.setEnabled(true);
			}
		});
		dataPluginGroup.add(dataPluginMenuItem);
		dataPluginMenu.add(dataPluginMenuItem);

	}

	@Override
	public void onAnalysisPluginRegistered(
			final AnalysisPluginInterface plugin) {

		JRadioButtonMenuItem analysisPluginMenuItem = new JRadioButtonMenuItem(
				plugin.getTitle());
		analysisPluginMenuItem.setSelected(false);
		analysisPluginMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				core.selectAnalysisPlugin(plugin);
				analyzeMenuButton.setEnabled(true);

			}
		});
		analysisPluginGroup.add(analysisPluginMenuItem);
		analysisMenu.add(analysisPluginMenuItem);
	}

	private static void showErrorDialog(Component c, String title,
			String msg) {
		JOptionPane.showMessageDialog(c, msg, title,
				JOptionPane.ERROR_MESSAGE);
	}

	private void activateAnalysisResult() {
		outerPanel.removeAll();
		outerPanel.add(analysisResultPanel);
		outerPanel.revalidate();
		outerPanel.repaint();
		frame.pack();
	}
}
