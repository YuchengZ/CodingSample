package edu.cmu.cs.cs214.hw5.framework;

/**
 * The class the defines a person's personality using several factors. All
 * factors will be labeled in 4 levels: 0, 1, 2, 3. 1. frequencyFactor:
 * indicates the post frequency of the user; 2. postLengthFactor: indicates the
 * average post length of the person; 3. friendFactor: indicates the total
 * number of the user's followers/friends; 4. emoticonFactor: indicates how does
 * the user trend to use emoticons; 5. activePeriod: indicates the active period
 * of the user during a day; 0-midnight (12am-8am), 1-morning(8am-12pm),
 * 2-afternoon(12pm-6pm), 3-night(6pm-12am).
 * 
 * @author Tao
 *
 */
public class Personality {

	// All factors will be labeled in 4 levels: 0, 1, 2, 3
	// 1. frequencyFactor: indicates the post frequency of the user;
	// 2. postLengthFactor: indicates the average post length of the person;
	// 3. friendFactor: indicates the total number of the user's
	// followers/friends;
	// 4. emoticonFactor: indicates how does the user trend to use
	// emoticons;
	// 5. activePeriod: indicates the active period of the user durning a
	// day; 0-midnight (12am-8am), 1-morning(8am-12pm),
	// 2-afternoon(12pm-6pm), 3-night(6pm-12am).

	private int frequencyFactor;
	private int postLengthFactor;
	private int friendFactor;
	private int emoticonFactor;
	private int activePeriod;

	/**
	 * Constructor.
	 * 
	 * @param frequencyF
	 *                The frequencyFactor which indicates the post frequency
	 *                of the user (number of posts per day)
	 * @param postLengthF
	 *                The postLengthFactor which indicates the average post
	 *                length of the person
	 * @param friendF
	 *                The friendFactor which indicates the total number of
	 *                the user's followers/friends
	 * @param emoticonF
	 *                The emoticonFactor which indicates how does the user
	 *                trend to use emoticons
	 * @param activeP
	 *                The activePeriod which indicates the active period of
	 *                the user during a day
	 */
	public Personality(int frequencyF, int postLengthF, int friendF,
			int emoticonF, int activeP) {
		this.frequencyFactor = frequencyF;
		this.postLengthFactor = postLengthF;
		this.friendFactor = friendF;
		this.emoticonFactor = emoticonF;
		this.activePeriod = activeP;
	}

	/**
	 * Get the frequencyFactor which indicates the post frequency of the
	 * user (number of posts per day)
	 * 
	 * @return frequencyFactor
	 */
	public int getFrequencyFactor() {
		return frequencyFactor;
	}

	/**
	 * Get the postLengthFactor which indicates the average post length of
	 * the person
	 * 
	 * @return postLengthFactor
	 */
	public int getPostLengthFactor() {
		return postLengthFactor;
	}

	/**
	 * Get the friendFactor which indicates the total number of the user's
	 * followers/friends
	 * 
	 * @return friendFactor
	 */
	public int getFriendFactor() {
		return friendFactor;
	}

	/**
	 * Get the emoticonFactor which indicates how does the user trend to use
	 * emoticons
	 * 
	 * @return emoticonFactor
	 */
	public int getEmoticonFactor() {
		return emoticonFactor;
	}

	/**
	 * Get the activePeriod which indicates the active period of the user
	 * during a day. 0-midnight (12am-8am), 1-morning(8am-12pm),
	 * 2-afternoon(12pm-6pm), 3-night(6pm-12am).
	 * 
	 * @return activePeriod
	 */
	public int getActivePeriod() {
		return activePeriod;
	}

}
