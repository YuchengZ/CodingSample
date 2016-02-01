package edu.cmu.cs.cs214.hw5.plugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import edu.cmu.cs.cs214.hw5.framework.DataPluginInterface;
import edu.cmu.cs.cs214.hw5.framework.Person;
import edu.cmu.cs.cs214.hw5.framework.MyPost;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.auth.AccessToken;
/**
 * facebook data plugin implements DataPluginInterface
 * @author ZhangYC
 *
 */
public class FacebookData implements DataPluginInterface {
	private Facebook facebook;
	private User user;

	@Override
	public Person getPerson(String id) {
		facebook = new FacebookFactory().getInstance();
		AccessToken token;
		try {
			token = facebook.getOAuthAppAccessToken();
			facebook.setOAuthAccessToken(token);
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			System.out.println("error accured when set OAuth Access Token!");
		}
		try {
			user = facebook.getUser(id);
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			System.out.println("Can not find user");
			return null;
		}
		String name = user.getName();
		List<MyPost> posts = getPosts();

		// has to be called after get posts
		Calendar date = getFirstDateOfPost(posts);

		Person person = new Person(name, id, date, posts, 0);
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
		List<MyPost> posts = new ArrayList<MyPost>();
		try {
			ResponseList<Post> thePost = facebook.getPosts(user.getId());
			for (int i = 0; i < thePost.size(); i++) {
				Post p = thePost.get(i);
				String content = p.getMessage();
				if (content == null){
					continue;
				}
				Calendar d = new GregorianCalendar();
				Date date = p.getUpdatedTime();
				d.setTime(date);
				posts.add(new MyPost(content, d));
			}
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			System.out.println("cannot get posts!");
		}
		return posts;
	}

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return "Facebook Data";
	}

	@Override
	public String getInputDescription() {
		// TODO Auto-generated method stub
		return "Facebook ID (like: 100008131746007)";
	}
}
