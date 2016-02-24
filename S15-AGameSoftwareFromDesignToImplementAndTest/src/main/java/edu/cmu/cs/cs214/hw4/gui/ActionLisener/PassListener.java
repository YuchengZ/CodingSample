package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;

/**
 * 
 * @author ZhangYC
 *
 */
public class PassListener implements ActionListener {
	private Game game;
	private JTextField status;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param s
	 *            JTextField show information
	 */
	public PassListener(Game g, JTextField s) {
		game = g;
		status = s;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		game.pass();
		if (game.isGameOver()) {
			game.gameEnd();
		}
	}

}
