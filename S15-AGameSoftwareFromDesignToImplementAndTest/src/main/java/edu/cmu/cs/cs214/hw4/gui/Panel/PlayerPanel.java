package edu.cmu.cs.cs214.hw4.gui.Panel;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.Player;

/**
 * PlayerPanel
 * 
 * @author ZhangYC
 *
 */
public class PlayerPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6208829211874600004L;
	private Game game;
	private int numOfPlayer;
	private JButton[] playerButton;
	private JLabel playerText;
	private JTextField status;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param num
	 *            the number of player int
	 * @param s
	 *            JTextField to show information
	 */
	public PlayerPanel(Game g, int num, JTextField s) {
		game = g;
		numOfPlayer = num;
		status = s;
		init();
	}

	private void init() {
		playerText = new JLabel("Players:");
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		playerButton = new JButton[numOfPlayer];
		add(playerText);
		for (int i = 0; i < numOfPlayer; i++) {
			playerButton[i] = new JButton();
			String name = game.getPlayerName(i);
			int score = game.getPlayerScore(i);
			playerButton[i].setText(String.format("Player:%s, Score:%d", name,
					score));
			add(playerButton[i]);
		}
	}

	/**
	 * change the panel when player make move
	 */
	public void makeMoveChanged() {
		playerScoreChanged();

	}

	/**
	 * change the panel when player buy special tile
	 */
	public void buySpecialTileChanged() {
		playerScoreChanged();

	}

	/**
	 * change the panel when game cange the currentPlayer
	 */
	public void currentPlayerChanged() {
		int index = game.getCurrentPlayerIndex();
		for (int i = 0; i < numOfPlayer; i++) {
			if (i == index) {
				playerButton[i].setBackground(Color.BLACK);
				playerButton[i].setOpaque(true);
			} else {
				playerButton[i].setBackground(Color.lightGray);
				playerButton[i].setOpaque(true);
			}
		}
	}

	/**
	 * change the panel when game is overs
	 */
	public void gameOverChanged() {
		status.setText(" ");
		Player p = game.getWinner();
		if (p != null) {
			status.setText(String
					.format("The game if over, the winner is player "
							+ p.getName() + ". Click restart to play again"));
			int index = game.getWinnerIndex();
			for (int i = 0; i < numOfPlayer; i++) {
				if (i == index) {
					playerButton[i].setBackground(Color.RED);
					playerButton[i].setOpaque(true);
				} else {
					playerButton[i].setBackground(Color.lightGray);
					playerButton[i].setOpaque(true);
				}
			}
		} else {
			status.setText(String
					.format("The game is over. It is tie. Please click restart to play again"));
		}

	}

	private void playerScoreChanged() {
		for (int i = 0; i < numOfPlayer; i++) {
			String name = game.getPlayerName(i);
			int score = game.getPlayerScore(i);
			playerButton[i].setText(String.format("Player:%s, Score:%d", name,
					score));
		}

	}

}
