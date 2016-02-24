package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.NormalTile;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * Click NormalTile listener when click normalTile on the rack
 * 
 * @author ZhangYC
 *
 */
public class ClickNormalListener implements ActionListener {
	private int index;
	private Game game;
	private List<NormalTile> normalTiles;
	private List<SpecialTile> specialTiles;
	private JTextField status;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param clickNormal
	 *            The NormalTile clicked
	 * @param clickSpecial
	 *            the specsialTile clicked
	 * @param i
	 *            the index of the norsmalTile clicked
	 * @param s
	 *            TextField to show some status
	 */
	public ClickNormalListener(Game g, List<NormalTile> clickNormal,
			List<SpecialTile> clickSpecial, int i, JTextField s) {
		index = i;
		game = g;
		normalTiles = clickNormal;
		specialTiles = clickSpecial;
		status = s;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		specialTiles.removeAll(specialTiles);
		if (index < game.getNormalTileOnRackSize()) {
			normalTiles.add(game.getNormalTileOnRack(index));
		}
	}

}
