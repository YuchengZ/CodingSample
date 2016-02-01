package edu.cmu.cs.cs214.hw5.plugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import edu.cmu.cs.cs214.hw5.framework.DataPluginInterface;
import edu.cmu.cs.cs214.hw5.framework.Person;
import edu.cmu.cs.cs214.hw5.framework.MyPost;
import twitter4j.Paging;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
/**
 * twitter data implements the DataPluginInterface.
 * @author ZhangYC
 *
 */
public class TwitterData implements DataPluginInterface {
	private static final int POST_NUM = 5;
	private User user;
	private Twitter twitter;
    
	@Override
	public Person getPerson(String screenName) {
		twitter = TwitterFactory.getSingleton();
		twitter.addRateLimitStatusListener(new RateLimitStatusListener() {
			@Override
			public void onRateLimitReached(RateLimitStatusEvent arg0) {
				System.out.println("reach rate limit!");
			}
            
			@Override
			public void onRateLimitStatus(RateLimitStatusEvent arg0) {
			}
            
		});
        
		try {
			user = twitter.showUser(screenName);
            
		} catch (TwitterException e) {
			System.out.println("can not find user");
			return null;
		}
		
		String id = getID();
		List<MyPost> posts = getPosts();
		
		// has to call after get posts
		Calendar date = getFirstDateOfPost(posts);
		int numOfFriends = user.getFriendsCount();
		Person person = new Person(screenName, id, date, posts, numOfFriends);
		return person;
	}
    
	private Calendar getFirstDateOfPost(List<MyPost> posts) {
		if (posts.size() == 0) {
			return null;
		}
        
		Calendar minDate = posts.get(0).getPostTime();
		for (int i = 1; i < posts.size(); i++) {
			Calendar thisDate = posts.get(i).getPostTime();
			if (thisDate.compareTo(minDate) < 0) {
				minDate = thisDate;
			}
		}
		
		return minDate;
	}
    
	private List<MyPost> getPosts() {
		List<Status> status = new ArrayList<Status>();
		List<MyPost> posts = new ArrayList<MyPost>();
        
		try {
			long id = user.getId();
			status.addAll(twitter.getUserTimeline(id, new Paging(1, POST_NUM)));
		} catch (TwitterException e) {
			System.out.println("Cannot find status");
		}
        
		for (int i = 0; i < status.size() && i<POST_NUM; i++) {
            
			String content = status.get(i).getText();
			Date date = status.get(i).getCreatedAt();
			Calendar d = new GregorianCalendar();
			d.setTime(date);
			posts.add(new MyPost(content, d));
		}
		return posts;
	}
    
	private String getID() {
		long idint = user.getId();
		String id = String.format("%d", idint);
		return id;
	}
    
	@Override
	public String getPluginName() {
		return "Twitter Data";
	}
    
	@Override
	public String getInputDescription() {
		return "Screen Name (like: kate_yucheng)";
	}
}
