package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.NormalTile;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * Click Special Listener
 * 
 * @author ZhangYC
 *
 */
public class ClickSpecialListener implements ActionListener {
	private static final int SPECIAL_NUM = 5;
	private static final int BM = 0;
	private static final int MYOWN = 1;
	private static final int NGT = 2;
	private static final int RVRS = 3;
	private static final int SWTCH = 4;
	private int index;
	private Game game;
	private List<SpecialTile> specialTile;
	private List<NormalTile> normalTile;
	private JTextField status;
	private String[] specialTileName;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param clickN
	 *            a list of NormalTile the player clicked
	 * @param clickS
	 *            a list of SpecialTile the player clicked
	 * @param i
	 *            the index of the specials button clicked
	 * @param s
	 *            JTextField to show information
	 */
	public ClickSpecialListener(Game g, List<NormalTile> clickN,
			List<SpecialTile> clickS, int i, JTextField s) {
		index = i;
		game = g;
		specialTile = clickS;
		normalTile = clickN;
		status = s;
		specialTileName = new String[SPECIAL_NUM];
		specialTileName[BM] = "boom";
		specialTileName[MYOWN] = "myown";
		specialTileName[NGT] = "negative";
		specialTileName[RVRS] = "reverse";
		specialTileName[SWTCH] = "switch";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		normalTile.removeAll(normalTile);
		for (int i = 0; i < game.getSpecialTileOnRackSize(); i++) {
			// because I didn't let specialTile show one by one. I think show by
			// categories and how many this kind of speicailTile make more sense
			// for a game gui So when each button
			// clicked, I don't know which specialTile he clicked because there
			// is not one button-one specialTile. So I have to know which button
			// represents which kind of specialTile and get this specialTile if
			// the player has one.
			if (game.getSpecialTileOnRackName(i).equals(specialTileName[index])) {
				specialTile.add(game.getSpecialTileOnRack(i));
				break;
			}
		}
	}
}
