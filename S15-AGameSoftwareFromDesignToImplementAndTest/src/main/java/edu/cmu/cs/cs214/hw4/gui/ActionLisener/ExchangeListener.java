package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.NormalTile;

/**
 * ExchangeListenner of exchange button
 * 
 * @author ZhangYC
 *
 */
public class ExchangeListener implements ActionListener {
	private Game game;
	private JTextField status;
	private List<NormalTile> clickNormal;

	/**
	 * constructor of exchange listener
	 * 
	 * @param g
	 *            Game
	 * @param s
	 *            JTextField to show some status
	 * @param clickN
	 *            the normalTile that the user clicked
	 */
	public ExchangeListener(Game g, JTextField s, List<NormalTile> clickN) {
		game = g;
		status = s;
		clickNormal = clickN;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		if (clickNormal.size() == 0) {
			status.setText("Please click the normal tile you want to exchange, then click the exchange button!");
			return;
		}
		List<NormalTile> exchangedTile = new ArrayList<NormalTile>();
		for (NormalTile n : clickNormal) {
			if (!exchangedTile.contains(n)) {
				exchangedTile.add(n);
			}
		}

		clickNormal.removeAll(clickNormal);
		game.exchange(exchangedTile);
		if (game.isGameOver()) {
			game.gameEnd();
		}
	}

}
