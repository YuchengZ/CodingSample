package edu.cmu.cs.cs214.hw5.framework.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import edu.cmu.cs.cs214.hw5.framework.DataPluginInterface;

/**
 * The panel that contains the input field of friend ids.
 * 
 * @author Tao
 *
 */
public class FriendIdInput extends JPanel {

	private static final long serialVersionUID = -3354938626760536401L;
	private static final int AREA_WIDTH = 10;
	private static final int AREA_HEIGHT = 1;

	private static final String FRIEDN_USER_ID = "Firend User ID: ";
	private static final String SUBMIT = "SUBMIT";

	private JLabel desciption;
	private JTextArea inputArea;

	/**
	 * Constructor, pass by the gui and the instruction for id inputs from
	 * data plugin.
	 * 
	 * @param gui
	 *                the framework client.
	 * @param s
	 *                the instruction of id inputs from data plugin.
	 */
	public FriendIdInput(FrameworkGui gui, String s) {
		desciption = new JLabel(s);
		JLabel textFieldLable = new JLabel(FRIEDN_USER_ID);
		inputArea = new JTextArea(AREA_HEIGHT, AREA_WIDTH);

		inputArea.setEditable(false);

		JButton sendButton = new JButton(SUBMIT);

		ActionListener submitTextListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get user input from text area
				String message = inputArea.getText();
				if (!message.isEmpty()) {
					gui.inputFriendId(message);
					inputArea.setText("");
					inputArea.requestFocus();
				} else {
					inputArea.requestFocus();
				}
			}
		};

		sendButton.addActionListener(submitTextListener);

		setLayout(new BorderLayout());
		add(desciption, BorderLayout.NORTH);
		add(textFieldLable, BorderLayout.WEST);
		add(inputArea, BorderLayout.CENTER);
		add(sendButton, BorderLayout.EAST);

		setVisible(true);
	}

	/**
	 * Call this method when changing data plugin.
	 * 
	 * @param plugin
	 *                The data plugin
	 */
	public void onChangeDataPlugin(DataPluginInterface plugin) {
		desciption.setText(plugin.getInputDescription());
		inputArea.setEditable(true);
	}
}
