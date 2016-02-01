package edu.cmu.cs.cs214.hw5.framework;

import java.util.Calendar;
import java.util.List;

/**
 * The object that defines a person from social network.
 * 
 * @author Tao
 *
 */
public class Person {

	private static final int MILLISECONDS_PER_DAY = 86400000;
	private static final double NUM_OF_POSTS_PER_DAY_LEVEL1 = 0.15;
	private static final double NUM_OF_POSTS_PER_DAY_LEVEL2 = 0.3;
	private static final double NUM_OF_POSTS_PER_DAY_LEVEL3 = 1.5;

	private static final int LEVEL_1 = 1;
	private static final int LEVEL_2 = 2;
	private static final int LEVEL_3 = 3;

	private static final double POST_LENGTH_LEVEL1 = 30;
	private static final double POST_LENGTH_LEVEL2 = 80;
	private static final double POST_LENGTH_LEVEL3 = 120;

	private static final int FRIEND_NUMBER_LEVEL1 = 200;
	private static final int FRIEND_NUMBER_LEVEL2 = 500;
	private static final int FRIEND_NUMBER_LEVEL3 = 1000;

	private static final double EMOTICON_LEVEL1 = 0.01;
	private static final double EMOTICON_LEVEL2 = 0.025;
	private static final double EMOTICON_LEVEL3 = 0.04;

	private static final int TIME_12AM = 0;
	private static final int TIME_8AM = 8;
	private static final int TIME_12PM = 12;
	private static final int TIME_6PM = 18;

	private static final String EMOTICON_LIST = "!@#$%^&*()-+=:<>?";

	private String name;
	private String id;
	private Calendar joinTime;
	private List<String> friends;
	private int friendshipLevel;
	private List<MyPost> posts;
	private Personality personality;
	private int friendNumber; // Sometimes it's hard to get all friends,
					// this field is to indicate the actual
					// friend number.

	/**
	 * Constructor. Use this constructor if you cannot get a full list of
	 * friends from the social network. Indicate the actual number of
	 * friends in numOfFriend.
	 * 
	 * @param userName
	 *                The name.
	 * @param userId
	 *                The ID.
	 * @param userJoinTime
	 *                The join time of the user. If it is not possible to
	 *                get all posts, then use the time of the first post as
	 *                join time.
	 * @param postList
	 *                A list the contains posts.
	 * @param numOfFriend
	 *                The total number of friends. For some social networks,
	 *                it's hard to get a list of all friends. So the total
	 *                number of friends cannot be calculated from friend
	 *                list. But it's easy to get the number of friends. Use
	 *                this variable.
	 */
	public Person(String userName, String userId, Calendar userJoinTime,
			List<MyPost> postList, int numOfFriend) {
		this.name = userName;
		this.id = userId;
		this.joinTime = userJoinTime;
		this.posts = postList;
		this.friendNumber = numOfFriend;

		calPersonality(); // Calculate and update the person's //
					// personality.
	}

	/**
	 * Call this method to set the friendship level. This method will be
	 * called when creating connection networks.
	 * 
	 * @param l
	 *                The friendship level
	 */
	public void setFriendshipLevel(int l) {
		this.friendshipLevel = l;
	}

	/**
	 * Get the name of the user.
	 * 
	 * @return The name of the user.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the ID of the user.
	 * 
	 * @return The ID of the user.
	 */
	public String getID() {
		return id;
	}

	/**
	 * Get the list of friends' IDs.
	 * 
	 * @return A list of the friends' IDs.
	 */
	public List<String> getFriends() {
		return friends;
	}

	/**
	 * Get the list of posts.
	 * 
	 * @return A list of posts.
	 */
	public List<MyPost> getPosts() {
		return posts;
	}

	/**
	 * Get the friendship level to the source person.
	 * 
	 * @return The friendship level. 0 means the source person. 1 means the
	 *         directly connected. 2 means directly connected to 2end level
	 *         friends...
	 */
	public int getFriendshipLevel() {
		return friendshipLevel;
	}

	/**
	 * Get the join time of the user.
	 * 
	 * @return The join time of the user.
	 */
	public Calendar getJoinTime() {
		return joinTime;
	}

	/**
	 * Get the frequencyFactor which indicates the post frequency of the
	 * user (number of posts per day)
	 * 
	 * @return frequencyFactor, can from 0 to 3, the higher the value, the higher the  post frequency
	 */
	public int getFrequencyFactor() {
		return personality.getFrequencyFactor();
	}

	/**
	 * Get the postLengthFactor which indicates the average post length of
	 * the person
	 * 
	 * @return postLengthFactor, can from 0 to 3, the higher the value, the higher the average post length
	 */
	public int getPostLengthFactor() {
		return personality.getPostLengthFactor();
	}

	/**
	 * Get the friendFactor which indicates the total number of the user's
	 * followers/friends
	 * 
	 * @return friendFactor, can from 0 to 3, the higher the value, the more friends this person has
	 */
	public int getFriendFactor() {
		return personality.getFriendFactor();
	}

	/**
	 * Get the emoticonFactor which indicates how does the user trend to use
	 * emoticons
	 * 
	 * @return emoticonFactor, can from 0 to 3, the higher the value, the higher the emoticon frequency
	 */
	public int getEmoticonFactor() {
		return personality.getEmoticonFactor();
	}

	/**
	 * Get the activePeriod which indicates the active period of the user
	 * during a day
	 * 
	 * @return activePeriod, can from 0 to 3, represent the person's peek time is morning, afternoon, evenning or midnight.
	 */
	public int getActivePeriod() {
		return personality.getActivePeriod();
	}

	private void calPersonality() {
		personality = new Personality(calFrequencyFactor(),
				calPostLengthFactor(), calFriendFactor(),
				calEmoticonFactor(), calActivePeriod());
	}

	private int calFrequencyFactor() {
		Calendar cal = Calendar.getInstance();
		long currentTimeInMillis = cal.getTimeInMillis();

		if (posts.isEmpty()) {
			return 0;
		}

		// No. of posts per day.
		double frequency = (double) posts.size()
				/ (currentTimeInMillis - joinTime
						.getTimeInMillis())
				* MILLISECONDS_PER_DAY;

		if (frequency > NUM_OF_POSTS_PER_DAY_LEVEL3) {
			return LEVEL_3;
		} else if (frequency > NUM_OF_POSTS_PER_DAY_LEVEL2) {
			return LEVEL_2;
		} else if (frequency > NUM_OF_POSTS_PER_DAY_LEVEL1) {
			return LEVEL_1;
		} else {
			return 0;
		}
	}

	private int calPostLengthFactor() {
		int lenghtSum = 0;
		for (MyPost p : posts) {
			lenghtSum += p.getPostContent().length();
		}

		if (posts.size() == 0) {
			return 0;
		}

		double averageLength = lenghtSum / posts.size();

		if (averageLength > POST_LENGTH_LEVEL3) {
			return LEVEL_3;
		} else if (averageLength > POST_LENGTH_LEVEL2) {
			return LEVEL_2;
		} else if (averageLength > POST_LENGTH_LEVEL1) {
			return LEVEL_1;
		} else {
			return 0;
		}
	}

	private int calFriendFactor() {

		if (friendNumber > FRIEND_NUMBER_LEVEL3) {
			return LEVEL_3;
		} else if (friendNumber > FRIEND_NUMBER_LEVEL2) {
			return LEVEL_2;
		} else if (friendNumber > FRIEND_NUMBER_LEVEL1) {
			return LEVEL_1;
		} else {
			return 0;
		}
	}

	private int calEmoticonFactor() {
		int emoticonCounter = 0;
		int lenghtSum = 0;

		for (MyPost p : posts) {

			lenghtSum += p.getPostContent().length();
			for (char c : p.getPostContent().toCharArray()) {
				if (EMOTICON_LIST.contains(String.valueOf(c))) {
					emoticonCounter++;
				}
			}
		}

		if (lenghtSum == 0) {
			return 0;
		}

		double emoticonVsTotalPostLength = (double) emoticonCounter
				/ lenghtSum;

		if (emoticonVsTotalPostLength > EMOTICON_LEVEL3) {
			return LEVEL_3;
		} else if (emoticonVsTotalPostLength > EMOTICON_LEVEL2) {
			return LEVEL_2;
		} else if (emoticonVsTotalPostLength > EMOTICON_LEVEL1) {
			return LEVEL_1;
		} else {
			return 0;
		}
	}

	private int calActivePeriod() {

		int midnigthCounter = 0;
		int morningCounter = 0;
		int afternoonCounter = 0;
		int nightCounter = 0;

		for (MyPost p : posts) {
			@SuppressWarnings("static-access")
			int hour = p.getPostTime().HOUR_OF_DAY;
			if (hour > TIME_6PM) {
				nightCounter++;
			} else if (hour > TIME_12PM) {
				afternoonCounter++;
			} else if (hour > TIME_8AM) {
				morningCounter++;
			} else if (hour > TIME_12AM) {
				midnigthCounter++;
			}
		}

		if (midnigthCounter > morningCounter
				&& morningCounter > afternoonCounter
				&& morningCounter > nightCounter) {
			return 0;
		} else if (morningCounter > afternoonCounter
				&& morningCounter > nightCounter) {
			return LEVEL_1;
		} else if (afternoonCounter > nightCounter) {
			return LEVEL_2;
		} else {
			return LEVEL_3;
		}
	}
}
