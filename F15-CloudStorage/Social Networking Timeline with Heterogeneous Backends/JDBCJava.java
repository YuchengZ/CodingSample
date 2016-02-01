package cc.cmu.edu.minisite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * connect to mysql database
 * @author ZhangYC
 *
 */
public class JDBCJava {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/my_db";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "15319project";
	Connection conn = null;

	JDBCJava() throws SQLException {
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
	}

	public String[] query(String id, String password) {
		String name = null;
		String url = null;

		Statement stmt = null;
		try {
			//  Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//  Execute a query
			stmt = conn.createStatement();
			String sql;

			if (password != null) {
				// for task 1
				sql = "SELECT profile.name, profile.url from users, profile where users.id="
						+ id
						+ " and users.password=\""
						+ password
						+ "\" and users.id = profile.id";
			} else {
				// for task 4
				sql = "SELECT profile.name, profile.url from profile where id="
						+ id;
			}
			ResultSet rs = stmt.executeQuery(sql);

			// Extract data from result set
			if (rs.next()) {
				name = rs.getString("name");
				url = rs.getString("url");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
		}
		// if pwd or id are incorrect
		if (name == null) {
			name = "Unauthorized";
		}

		if (url == null) {
			url = "#";
		}

		return new String[] { name, url };

	}
}
