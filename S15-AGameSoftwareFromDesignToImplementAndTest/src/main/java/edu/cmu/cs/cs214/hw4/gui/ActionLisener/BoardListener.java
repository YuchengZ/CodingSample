package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.NormalTile;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * Board Listener
 * 
 * @author ZhangYC
 *
 */
public class BoardListener implements ActionListener {
	private int row;
	private int col;
	private Game game;
	private JTextField status;
	private List<NormalTile> clickNormal;
	private List<SpecialTile> clickSpecial;

	/**
	 * constructor
	 * 
	 * @param x
	 *            int row index
	 * @param y
	 *            int col index
	 * @param g
	 *            Game
	 * @param clickN
	 *            normalTile the player clicked
	 * @param clickS
	 *            specialTile the player clicked
	 * @param s
	 *            JTextField to show information
	 */
	public BoardListener(int x, int y, Game g, List<NormalTile> clickN,
			List<SpecialTile> clickS, JTextField s) {
		row = x;
		col = y;
		game = g;
		status = s;
		clickNormal = clickN;
		clickSpecial = clickS;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		if (clickNormal.size() != 0) {
			if (!game.hasNormalTile(row, col)) {
				game.placeNormalTile(clickNormal.get(clickNormal.size() - 1),
						row, col);
				clickNormal.removeAll(clickNormal);
			} else {
				status.setText("One square can only has one normalTile");
			}
		} else if (clickSpecial.size() != 0) {
			if (!game.hasNormalTile(row, col)) {
				game.placeSpecialTile(
						clickSpecial.get(clickSpecial.size() - 1), row, col);
				clickSpecial.removeAll(clickSpecial);
			} else {
				status.setText("Special tile can not be put on the square that has normalTile");
			}
		}

	}

}
