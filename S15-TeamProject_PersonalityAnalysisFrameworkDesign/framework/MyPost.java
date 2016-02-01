package edu.cmu.cs.cs214.hw5.framework;

import java.util.Calendar;

/**
 * The object that defines a post.
 * 
 * @author Tao
 *
 */
public class MyPost {

	private String postContent;
	private Calendar postTime;

	/**
	 * Constructor.
	 * 
	 * @param content
	 *                Post content.
	 * @param t
	 *                The post time.
	 */
	public MyPost(String content, Calendar t) {
		this.postContent = content;
		this.postTime = t;
	}

	/**
	 * Get the post content.
	 * 
	 * @return The post content.
	 */
	public String getPostContent() {
		return postContent;
	}

	/**
	 * Get the post time.
	 * 
	 * @return The post time.
	 */
	public Calendar getPostTime() {
		return postTime;
	}

}
