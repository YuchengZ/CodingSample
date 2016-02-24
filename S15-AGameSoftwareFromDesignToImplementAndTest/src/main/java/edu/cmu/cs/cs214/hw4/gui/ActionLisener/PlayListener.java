package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.GameFlow;

/**
 * play listeners when click play buttons
 * 
 * @author ZhangYC
 *
 */
public class PlayListener implements ActionListener {
	private JTextField status;
	private Game game;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param s
	 *            JTextFile to show infomation
	 */
	public PlayListener(Game g, JTextField s) {
		status = s;
		game = g;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		GameFlow gf = game.getCurrentGameFlow();
		boolean b = game.isValid(gf);
		if (!b) {
			status.setText("Movement is not valid, do it again!");
			game.retrieve();
			return;
		}
		game.move(gf);
		if (game.isGameOver()) {
			game.gameEnd();
		}
	}

}
