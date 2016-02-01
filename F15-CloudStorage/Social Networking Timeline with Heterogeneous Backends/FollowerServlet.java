package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Servlet for task 2
 * @author ZhangYC
 *
 */
public class FollowerServlet extends HttpServlet {
	private HBaseConnector hbase;

    

    public FollowerServlet() {
    	// connect to hbase
    	hbase = new HBaseConnector();

    }

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
    	/* query id */
        String id = request.getParameter("id");
        
        /* query from hbase */
        JSONObject result = new JSONObject();
        // get followers according to id
        JSONArray followers = hbase.getFollowers(id);
        result.put("followers", followers);
        
        
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
