package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet for task 4
 * @author ZhangYC
 *
 */
public class TimelineServlet extends HttpServlet {
	// reference for three databases
	JDBCJava jdbc = null;
	HBaseConnector hbase;
	DynamoConnector dc;

    public TimelineServlet() throws Exception {
    	// build connection
        try {
            jdbc = new JDBCJava();
            hbase = new HBaseConnector();
            dc = new DynamoConnector();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, 
            final HttpServletResponse response) throws ServletException, IOException {

        JSONObject result = new JSONObject();
        // get query id
        String id = request.getParameter("id");
        System.out.println("id: " + id);
        PriorityQueue<Post> pq = new PriorityQueue<Post>();
        
        /* get profile */
        String[] answer = new String[2];
        answer = jdbc.query(id, null);

        String name = answer[0];
        String profile = answer[1];
        System.out.println("name: " + name);
        System.out.println("profile: " + profile);
        result.put("name", name);
        result.put("profile", profile);
        
        /* get followers */
        JSONArray followers = hbase.getFollowers(id);
        System.out.println("followers size: " + followers.length());
        System.out.println("followers: " + followers);
        result.put("followers", followers);
        
        /* get followees */
        String[] followees = hbase.getFollowee(id);
        
        /* get latest 30 posts */
        for (String followee : followees) {
        	List<Post> posts = dc.getPosts(followee);
        	pq.addAll(posts);
        }
        
        JSONArray jsonPosts = new JSONArray();
        ArrayList<Post> reversPosts = new ArrayList<Post>();
        // get 30 latest post
        if (pq.size() >= 30) {   
            for (int i=0; i<30; i++) {
            	Post post = pq.poll();
                reversPosts.add(post);
            }
        } else {
        	while (pq.peek() != null) {
        		reversPosts.add(pq.poll());
        	}
        }
        
        // sort by timestamp
        for (int i=reversPosts.size()-1; i>=0; i--) {
            JSONObject jsonPost = new JSONObject(reversPosts.get(i).getPost());
            jsonPosts.put(jsonPost);
        }
        result.put("posts", jsonPosts);
        

        PrintWriter out = response.getWriter();
        out.print(String.format("returnRes(%s)", result.toString()));
        out.close();
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
