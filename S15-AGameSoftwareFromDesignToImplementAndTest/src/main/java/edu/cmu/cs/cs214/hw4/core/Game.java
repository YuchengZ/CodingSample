package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * The Class Game
 * 
 * @author ZhangYC
 *
 */
public class Game {
	private Board board;
	private List<Player> players;
	private Bag bag;
	private Store store;
	private Dictionary dictionary;
	private Player currentPlayer;
	private int round;
	private int turnTotal;
	private static final int TILE_NUM = 7;
	private int pass = 0;
	private List<GameChangeListener> gameChangeListeners;
	private Player winner = null;

	/**
	 * the construct, create all the fields need in game
	 * 
	 * @param numOfPlayer
	 *            int, the number of players participated in
	 */
	public Game(int numOfPlayer) {
		turnTotal = 0;
		round = 0;
		board = new Board();
		players = new ArrayList<Player>();
		for (int i = 0; i < numOfPlayer; i++) {
			Player player = new Player(String.format("%s", i));
			players.add(player);
		}
		bag = new Bag();
		store = new Store();
		dictionary = new Dictionary();
		currentPlayer = players.get(0);
		gameChangeListeners = new ArrayList<GameChangeListener>();
	}

	/**
	 * add the gameChangeListenner of game
	 * 
	 * @param g
	 *            GameChangeListenner
	 */
	public void addGameChangeListener(GameChangeListener g) {
		gameChangeListeners.add(g);
	}

	/**
	 * the game start. give 7 tiles to each player
	 */
	public void startGame() {
		for (int i = 0; i < players.size(); i++) {
			addNormalTilesToPlayer(TILE_NUM, players.get(i));
		}
		notifyCurrentPlayerChanged();
	}

	/**
	 * set board for game, this method is for testing
	 * 
	 * @param b
	 *            Board, the board created in test
	 */
	public void setBoard(Board b) {
		board = b;
	}

	/**
	 * get the board in game. It is for the later effect on board.
	 * 
	 * @return Board, the board in game
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * return the winner of the game
	 * 
	 * @return Player winner
	 */
	public Player getWinner() {
		return winner;
	}

	/**
	 * get the index of winner in player
	 * 
	 * @return int index
	 */
	public int getWinnerIndex() {
		return players.indexOf(winner);
	}

	/**
	 * get players in game. It is for the later effect on player
	 * 
	 * @return a list of Player of the game
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * get currentPlayer. The player that are playing now.
	 * 
	 * @return A player, who is making move now
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * check the special tile's validation
	 * 
	 * @param gameFlow
	 *            GameFlow, the movement made by currentPlayer
	 * @return boolean, true means the specialTile movement is valid, false
	 *         otherwise
	 */
	public boolean isSpecialTileValid(GameFlow gameFlow) {
		return board.isSpecialTileValid(gameFlow);
	}

	/**
	 * check the normal tile movement'd validation
	 * 
	 * @param gameFlow
	 *            GameFlow, the movement made by currentPlayer
	 * @return true means the normalTile movement is valid, false otherwise
	 */
	public boolean isValid(GameFlow gameFlow) {
		boolean valid;
		List<String> words;

		// check position valid
		valid = board.isValid(gameFlow, round);
		if (valid) {
			// get word in string
			words = board.getWords(gameFlow);
			for (String word : words) {
				// check word valid
				valid = dictionary.isValidWord(word);
				if (!valid) {
					return true;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * if both specailTIle and normlaTIle are valid. make move for this turn
	 * 
	 * @param gameFlow
	 *            GameFlow, the movement made by currentPlayer
	 */
	public void move(GameFlow gameFlow) {
		int score;
		// this turn is not pass
		pass = 0;

		// place normal and special tile first.
		board.placeNormalTile(gameFlow);
		board.placeSpecialTile(gameFlow);

		// then calculate score and active special tile then.
		board.activeBeforeEffect(this, gameFlow);
		score = board.calculateScore(gameFlow);
		score = board.activeAfterEffect(this, gameFlow, score);

		// change currentPlayer's score and add normalTile to 7, and trigger
		// next turn
		currentPlayer.addScore(score);
		// then active the final effect of each specialTile
		board.activeFinalEffect(this, gameFlow);
		addNormalTilesToPlayer(gameFlow.getNormalSize(), currentPlayer);
		clearGameFlow();
		notifyMakeMoveChanged();
		moveToNextPlayer();
	}

	/**
	 * do it when game end
	 */
	public void gameEnd() {
		int lastScore = 0;
		for (Player p : players) {
			if (p.getScore() > lastScore) {
				winner = p;
				lastScore = p.getScore();
			}
		}

		for (Player p : players) {
			if (p.getScore() == winner.getScore() && p != winner) {
				winner = null;
				break;
			}
		}
		notifyGameOver();
	}

	/**
	 * helper function for move, after calculate the score for currentPlayer,
	 * clear the gameflow for player
	 */
	private void clearGameFlow() {
		GameFlow gf = new GameFlow();
		currentPlayer.setGameFlow(gf);
	}

	/**
	 * helper function for move, after make the move, refill the NormalTile for
	 * player
	 * 
	 * @param n
	 *            int, the number needed to refill.
	 * @param p
	 *            Player, who needed to refill
	 */
	private void addNormalTilesToPlayer(int n, Player p) {
		for (int i = 0; i < n; i++) {
			NormalTile nT = bag.getRandomTile();
			if (nT != null) {
				p.addNormalTile(nT);
			}
		}
	}

	/**
	 * helper function for move, trigger next round
	 */
	private void moveToNextPlayer() {
		// change current player
		int index = players.indexOf(currentPlayer);
		int nextIndex = (index + 1) % players.size();
		currentPlayer = players.get(nextIndex);

		/*
		 * change indicator change next player, add 1 to total turn when very
		 * player's turn passed, add 1 to round
		 */
		turnTotal += 1;
		round = turnTotal / players.size();
		notifyCurrentPlayerChanged();

	}

	/**
	 * another kind of movement made by currentPlyer, exchange the tiles in his
	 * rack
	 * 
	 * @param tiles
	 *            The tiles the player want to change
	 */
	public void exchange(List<NormalTile> tiles) {
		// this turn is not pass
		pass = 0;
		for (NormalTile tile : tiles) {
			currentPlayer.deleteNormalTile(tile);
			NormalTile t = bag.exchange(tile);
			currentPlayer.addNormalTile(t);
		}
		retrieve();
		moveToNextPlayer();
	}

	/**
	 * another kind of movement for current player, do nothing, pass the turn
	 */
	public void pass() {
		pass += 1;
		retrieve();
		moveToNextPlayer();
	}

	/**
	 * buy special tile
	 * 
	 * @param name
	 *            String the specialTile's name
	 */
	public void buySpecialTile(String name) {
		store.buySpecialTile(currentPlayer, name);
		notifyBuySpecialTileChanged();
	}

	/**
	 * is game over or not, check at each end of the turn
	 * 
	 * @return boolean true is game over false otherwise
	 */
	public boolean isGameOver() {
		// 1. there is no tiles in bag.
		// 2. all the players hit pass button
		if (bag.getNormalTileSize() == 0) {
			return true;
		} else if (pass == players.size()) {
			return true;
		}
		return false;
	}

	/**
	 * retrieve the placement
	 */
	public void retrieve() {
		currentPlayer.retrieve();
		notifyRetrieveChanged();

	}

	private void notifyGameOver() {
		for (GameChangeListener listener : gameChangeListeners) {
			listener.gameOverChanged();
		}

	}

	private void notifyRetrieveChanged() {
		for (GameChangeListener listener : gameChangeListeners) {
			listener.retrieveChanged();
		}

	}

	private void notifyBuySpecialTileChanged() {
		for (GameChangeListener listener : gameChangeListeners) {
			listener.buySpecialTileChanged();
		}

	}

	private void notifyMakeMoveChanged() {
		for (GameChangeListener listener : gameChangeListeners) {
			listener.makeMoveChanged();
		}
	}

	private void notifyCurrentPlayerChanged() {
		for (GameChangeListener listener : gameChangeListeners) {
			listener.currentPlayerChanged();
		}

	}

	private void notifyGameFlowChanged() {
		for (GameChangeListener listener : gameChangeListeners) {
			listener.gameFlowChanged();
		}

	}

	/**
	 * get square's name
	 * 
	 * @param row
	 *            the row index of the board
	 * @param col
	 *            the col index of the board
	 * @return return the square's name of this square
	 */
	public String getSquareName(int row, int col) {
		return board.getSquareName(row, col);
	}

	/**
	 * get player's name
	 * 
	 * @param i
	 *            the index of the player in players
	 * @return the player's name
	 */
	public String getPlayerName(int i) {
		return players.get(i).getName();
	}

	/**
	 * get player's score
	 * 
	 * @param i
	 *            the index of the player in players
	 * @return the player's score
	 */
	public int getPlayerScore(int i) {
		return players.get(i).getScore();
	}

	/**
	 * get current player's index
	 * 
	 * @return the index of current players
	 */
	public int getCurrentPlayerIndex() {
		return players.indexOf(currentPlayer);
	}

	/**
	 * get current normal rack
	 * 
	 * @return a list of normal tile of current player
	 */
	public List<NormalTile> getCurrentNormalRack() {
		return currentPlayer.getNormalTiles();
	}

	/**
	 * get current special rack
	 * 
	 * @return a list of special tile of current player
	 */
	public List<SpecialTile> getCurrentSpecialRack() {
		return currentPlayer.getSpecialTiles();
	}

	/**
	 * get the special tile on the rack of current player
	 * 
	 * @param i
	 *            int index of special tile
	 * @return the special tile of the index
	 */
	public SpecialTile getSpecialTileOnRack(int i) {
		return currentPlayer.getSpecialTiles().get(i);
	}

	/**
	 * get how many special tile on the rack
	 * 
	 * @return int num of special tile
	 */
	public int getSpecialTileOnRackSize() {
		return currentPlayer.getNumofSpecialTile();
	}

	/**
	 * get the name of normalTlie on the rack of currentPlayer
	 * 
	 * @param i
	 *            the index of normalTile
	 * @return Stirng name of normalTile
	 */
	public String getNormalTileOnRackName(int i) {
		return currentPlayer.getNormalTiles().get(i).getName();
	}

	/**
	 * get the size of normalTile on the rack of currentplayer
	 * 
	 * @return int the size of normalTile
	 */
	public int getNormalTileOnRackSize() {
		return currentPlayer.getNumOfNormalTile();
	}

	/**
	 * get the normalTile on the rack
	 * 
	 * @param i
	 *            int index
	 * @return the normalTile
	 */
	public NormalTile getNormalTileOnRack(int i) {
		return currentPlayer.getNormalTiles().get(i);
	}

	/**
	 * get special tile name on the rack
	 * 
	 * @param i
	 *            index
	 * @return String the name of specialTile
	 */
	public String getSpecialTileOnRackName(int i) {
		return currentPlayer.getSpecialTiles().get(i).getName();
	}

	/**
	 * has normal tile nor not
	 * 
	 * @param row
	 *            the row index of the baord
	 * @param col
	 *            the col index of the board
	 * @return boolean
	 */
	public boolean hasNormalTile(int row, int col) {
		return board.hasNormalTile(row, col);
	}

	/**
	 * place a Normal Tile
	 * 
	 * @param nT
	 *            the normalTile the player placed
	 * @param row
	 *            row index
	 * @param col
	 *            col index
	 */
	public void placeNormalTile(NormalTile nT, int row, int col) {
		Position p = new Position(row, col);
		currentPlayer.placeNormalTile(nT, p);
		notifyGameFlowChanged();
	}

	/**
	 * place a special tile
	 * 
	 * @param sT
	 *            special Tile the player palced
	 * @param row
	 *            row index
	 * @param col
	 *            col index
	 */
	public void placeSpecialTile(SpecialTile sT, int row, int col) {
		Position p = new Position(row, col);
		currentPlayer.placeSpecialTile(sT, p);
		notifyGameFlowChanged();

	}

	/**
	 * get normalTile's name on board
	 * 
	 * @param row
	 *            int row index
	 * @param col
	 *            int col index
	 * @return the name of normaltile
	 */
	public String getNormalTileOnBoardName(int row, int col) {
		return board.getNormalTileOnBoardName(row, col);
	}

	/**
	 * if the currentPlayer has specialTile on this place or not
	 * 
	 * @param row
	 *            row index
	 * @param col
	 *            col index
	 * @return boolean
	 */
	public boolean hasSpecialTileOfCurrentPlayer(int row, int col) {
		return board.hasSpecialTileOfCurrentPlayer(row, col, currentPlayer);
	}

	/**
	 * get current game flow
	 * 
	 * @return the gameFlow
	 */
	public GameFlow getCurrentGameFlow() {
		return currentPlayer.getGameFlow();
	}
}
