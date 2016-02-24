package edu.cmu.cs.cs214.hw4.gui.Panel;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.NormalTile;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.ClickNormalListener;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.ClickSpecialListener;

/**
 * show the rack of currentPlayer
 * 
 * @author ZhangYC
 *
 */
public class RackPanel extends JPanel {

	private static final long serialVersionUID = -664073591524183737L;
	private Game game;
	private static final int NORMAL_NUM = 7;
	private static final int SPECIAL_NUM = 5;
	private JButton[] normalButton;
	private JButton[] specialButton;
	private JPanel rackNormalPanel;
	private JPanel rackSpecialPanel;
	private List<NormalTile> clickNormal;
	private List<SpecialTile> clickSpecial;
	// the index of special tile in specialButton
	private static final int BM = 0;
	private static final int MYOWN = 1;
	private static final int NGT = 2;
	private static final int RVRS = 3;
	private static final int SWTCH = 4;
	private JTextField status;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param s
	 *            JTextField, to show information
	 */
	public RackPanel(Game g, JTextField s) {
		game = g;
		status = s;
		init();
	}

	private void init() {
		clickNormal = new ArrayList<NormalTile>();
		clickSpecial = new ArrayList<SpecialTile>();
		rackNormalPanel = new JPanel();
		normalButton = new JButton[NORMAL_NUM];
		rackNormalPanel.add(new JLabel("Normal Tiles:"));
		for (int i = 0; i < NORMAL_NUM; i++) {
			normalButton[i] = new JButton();
			normalButton[i].setText(" ");
			normalButton[i].addActionListener(new ClickNormalListener(game,
					clickNormal, clickSpecial, i, status));
			rackNormalPanel.add(normalButton[i]);
		}

		rackSpecialPanel = new JPanel();
		specialButton = new JButton[SPECIAL_NUM];
		rackSpecialPanel.add(new JLabel("Special Tiles:"));
		for (int i = 0; i < SPECIAL_NUM; i++) {
			specialButton[i] = new JButton();
			specialButton[i].setText(" ");
			specialButton[i].addActionListener(new ClickSpecialListener(game,
					clickNormal, clickSpecial, i, status));
			rackSpecialPanel.add(specialButton[i]);
		}

		setLayout(new BorderLayout());
		add(rackNormalPanel, BorderLayout.NORTH);
		add(rackSpecialPanel, BorderLayout.SOUTH);
	}

	/**
	 * get the normalTile the player clicked
	 * 
	 * @return a list of NormalTile
	 */
	public List<NormalTile> getClickNormal() {
		return clickNormal;
	}

	/**
	 * get the specialTile the player clicked
	 * 
	 * @return a list of specialTiles
	 */
	public List<SpecialTile> getClickSpecial() {
		return clickSpecial;
	}

	/**
	 * called when game flow changed
	 */
	public void gameFlowChanged() {
		setNormalRack();
		setSpecialRack();

	}

	/**
	 * change the GUI when player buy specialTiles
	 */
	public void buySpecialTileChanged() {
		setSpecialRack();

	}

	/**
	 * changed the GUI when current player changed
	 */
	public void currentPlayerChanged() {
		setNormalRack();
		setSpecialRack();
	}

	/**
	 * change the GUI when the game is overs
	 */
	public void gameOverChanged() {
		for (int i = 0; i < NORMAL_NUM; i++) {
			normalButton[i].setEnabled(false);
		}

		for (int i = 0; i < SPECIAL_NUM; i++) {
			specialButton[i].setEnabled(false);
		}

	}

	/**
	 * change the GUI when player want to retrieves
	 */
	public void retrieveChanged() {
		setNormalRack();
		setSpecialRack();

	}

	private void setNormalRack() {
		for (int i = 0; i < game.getNormalTileOnRackSize(); i++) {
			normalButton[i].setText(game.getNormalTileOnRackName(i));
		}

		for (int i = game.getNormalTileOnRackSize(); i < NORMAL_NUM; i++) {
			normalButton[i].setText(" ");
		}
	}

	private void setSpecialRack() {
		// this list store the number of each kind of special tile
		int[] numbers = new int[SPECIAL_NUM];

		// initial the number to 0
		for (int i = 0; i < SPECIAL_NUM; i++) {
			numbers[i] = 0;
		}
		// change the number of each kind of special tile, Here I need to
		// distinguish each kind of specialTile because I need to show the
		// player how many each kinds of specialTile he has.
		for (int i = 0; i < game.getSpecialTileOnRackSize(); i++) {
			if (game.getSpecialTileOnRackName(i).equals("boom")) {
				numbers[BM] += 1;
			} else if (game.getSpecialTileOnRackName(i).equals("myown")) {
				numbers[MYOWN] += 1;
			} else if (game.getSpecialTileOnRackName(i).equals("negative")) {
				numbers[NGT] += 1;
			} else if (game.getSpecialTileOnRackName(i).equals("reverse")) {
				numbers[RVRS] += 1;
			} else if (game.getSpecialTileOnRackName(i).equals("switch")) {
				numbers[SWTCH] += 1;
			}
		}

		// change the button text
		specialButton[BM].setText(String.format("boom: %d", numbers[BM]));
		specialButton[MYOWN]
				.setText(String.format("myown: %d", numbers[MYOWN]));
		specialButton[NGT].setText(String.format("negative: %d", numbers[NGT]));
		specialButton[RVRS]
				.setText(String.format("reverse: %d", numbers[RVRS]));
		specialButton[SWTCH].setText(String
				.format("switch: %d", numbers[SWTCH]));
	}
}
