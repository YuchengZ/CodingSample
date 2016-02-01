package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Servlet for task 1
 * @author ZhangYC
 *
 */
public class ProfileServlet extends HttpServlet {
	// connector of mysql database
    private JDBCJava jdbc;

    public ProfileServlet() {
        try {
        	// build connection in constructor
            jdbc = new JDBCJava();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();

        String id = request.getParameter("id");
        String pwd = request.getParameter("pwd");

        // get answer from jdbc query
        String[] answer = new String[2];
        answer = jdbc.query(id, pwd);

        String name = answer[0];
        String profile = answer[1];
        result.put("name", name);
        result.put("profile", profile);

        PrintWriter writer = response.getWriter();
        writer.write(String.format("returnRes(%s)", result.toString()));
        writer.close();
    }


    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }


}


