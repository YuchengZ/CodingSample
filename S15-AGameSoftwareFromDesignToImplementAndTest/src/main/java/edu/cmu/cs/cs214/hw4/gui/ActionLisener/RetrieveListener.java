package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;

/**
 * retrieve listener when click retrieve button
 * 
 * @author ZhangYC
 *
 */
public class RetrieveListener implements ActionListener {
	private JTextField status;
	private Game game;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param s
	 *            JTextField show information
	 */
	public RetrieveListener(Game g, JTextField s) {
		status = s;
		game = g;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		game.retrieve();
	}

}
