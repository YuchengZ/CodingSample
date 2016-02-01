package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * servlet for task 3
 * 
 * @author ZhangYC
 *
 */
public class HomepageServlet extends HttpServlet {
	// feilds for connection of dynamo
	DynamoConnector dc;

	public HomepageServlet() {
		// build connection with 
		dc = new DynamoConnector();
	}

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		JSONObject result = new JSONObject();

		String id = request.getParameter("id");
		// get a list of post according to user id
		List<Post> betweenReplies = dc.getPosts(id);

		JSONArray jsonPosts = new JSONArray();

		/*
		 * store post in result
		 */
		for (Post post : betweenReplies) {
			JSONObject jsonPost = new JSONObject(post.getPost());
			jsonPosts.put(jsonPost);
		}
		result.put("posts", jsonPosts);
		// implement the functionalities in doGet method.
		// you can add any helper methods or classes to accomplish this task

		PrintWriter writer = response.getWriter();
		writer.write(String.format("returnRes(%s)", result.toString()));
		writer.close();
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}
}
