package edu.cmu.cs.cs214.hw4.gui.Panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.GameFlow;
import edu.cmu.cs.cs214.hw4.core.NormalTile;
import edu.cmu.cs.cs214.hw4.core.Position;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.BoardListener;

/**
 * Board Panel show the boards
 * 
 * @author ZhangYC
 *
 */
public class BoardPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3018566359139090197L;
	private Game game;
	private static final int CELL_NUM = 15;
	private static final int CELL_SIZE = 40;
	private JButton[][] boardButton;
	private JTextField status;
	private List<NormalTile> clickNormal;
	private List<SpecialTile> clickSpecial;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param s
	 *            JTextField show information
	 * @param clickN
	 *            a list of normal tile the player clicked
	 * @param clickS
	 *            a list of specialTiles player clicked
	 */
	public BoardPanel(Game g, JTextField s, List<NormalTile> clickN,
			List<SpecialTile> clickS) {
		game = g;
		status = s;
		clickNormal = clickN;
		clickSpecial = clickS;
		init();
	}

	private void init() {
		setLayout(new GridLayout(CELL_NUM, CELL_NUM));
		boardButton = new JButton[CELL_NUM][CELL_NUM];
		for (int row = 0; row < CELL_NUM; row++) {
			for (int col = 0; col < CELL_NUM; col++) {
				boardButton[row][col] = new JButton();
				boardButton[row][col].setPreferredSize(new Dimension(CELL_SIZE,
						CELL_SIZE));
				String name = game.getSquareName(row, col);
				if (name.equals("DL")) {
					boardButton[row][col].setText("DL");
					boardButton[row][col].setBackground(Color.BLUE);
					boardButton[row][col].setOpaque(true);
				} else if (name.equals("DW")) {
					boardButton[row][col].setText("DW");
					boardButton[row][col].setBackground(Color.cyan);
					boardButton[row][col].setOpaque(true);
				} else if (name.equals("TL")) {
					boardButton[row][col].setText("TL");
					boardButton[row][col].setBackground(Color.RED);
					boardButton[row][col].setOpaque(true);
				} else if (name.equals("TW")) {
					boardButton[row][col].setText("TW");
					boardButton[row][col].setBackground(Color.ORANGE);
					boardButton[row][col].setOpaque(true);
				} else {
					boardButton[row][col].setText(" ");
				}
				boardButton[row][col].addActionListener(new BoardListener(row,
						col, game, clickNormal, clickSpecial, status));
				boardButton[row][col].setForeground(Color.LIGHT_GRAY);
				add(boardButton[row][col]);
			}
		}

	}

	/**
	 * change the board panel when player click play button
	 */
	public void makeMoveChanged() {
		setSquareNameOnBoard();
	}

	/**
	 * change the game flow when player place tile
	 */
	public void gameFlowChanged() {
		setSquareNameInGameFlow();

	}

	/**
	 * change the board panel when current player changed
	 */
	public void currentPlayerChanged() {
		setSquareNameOnBoard();
	}

	/**
	 * change the board panel when game is over
	 */
	public void gameOverChanged() {
		for (int row = 0; row < CELL_NUM; row++) {
			for (int col = 0; col < CELL_NUM; col++) {
				boardButton[row][col].setEnabled(false);
			}
		}

	}

	private void setSquareNameInGameFlow() {
		GameFlow gf = game.getCurrentGameFlow();
		HashMap<Position, NormalTile> normalMap = gf.getNormalMap();
		List<Position> normalPosition = gf.getNormalPosition();
		HashMap<Position, SpecialTile> specialMap = gf.getSpecialMap();
		List<Position> specialPosition = gf.getSpecialPosition();

		for (int i = 0; i < specialPosition.size(); i++) {
			Position p = specialPosition.get(i);
			SpecialTile t = specialMap.get(p);
			if (t.getPlayer() == game.getCurrentPlayer()) {
				boardButton[p.getX()][p.getY()].setText("ST");
				boardButton[p.getX()][p.getY()].setForeground(Color.red);
			}
		}

		for (int i = 0; i < normalPosition.size(); i++) {
			Position p = normalPosition.get(i);
			NormalTile t = normalMap.get(p);
			boardButton[p.getX()][p.getY()].setText(t.getName());
			boardButton[p.getX()][p.getY()].setForeground(Color.black);
		}

	}

	/**
	 * board panel changed when player click retrieve button
	 */
	public void retrieveChanged() {
		setSquareNameOnBoard();

	}

	private void setSquareNameOnBoard() {
		for (int row = 0; row < CELL_NUM; row++) {
			for (int col = 0; col < CELL_NUM; col++) {
				String name = game.getSquareName(row, col);
				// show normalTile if has
				if (game.hasNormalTile(row, col)) {
					boardButton[row][col].setText(game
							.getNormalTileOnBoardName(row, col));
					boardButton[row][col].setForeground(Color.black);
				}
				// show specialTile if has
				else if (game.hasSpecialTileOfCurrentPlayer(row, col)) {
					boardButton[row][col].setText("ST");
					boardButton[row][col].setForeground(Color.RED);
				}
				// show square if has
				else {
					if (name.equals("DL")) {
						boardButton[row][col].setText("DL");
					} else if (name.equals("DW")) {
						boardButton[row][col].setText("DW");
					} else if (name.equals("TL")) {
						boardButton[row][col].setText("TL");
					} else if (name.equals("TW")) {
						boardButton[row][col].setText("TW");
					} else {
						boardButton[row][col].setText(" ");
					}
					boardButton[row][col].setForeground(Color.LIGHT_GRAY);
				}
			}
		}
	}

}
