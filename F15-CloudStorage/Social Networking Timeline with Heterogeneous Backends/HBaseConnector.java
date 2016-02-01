package cc.cmu.edu.minisite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * class to connector to hbase
 * 
 * @author ZhangYC
 *
 */
public class HBaseConnector {
	// table1, stores links
	private HTable shell;
	// table2, store users profile
	private HTable profile;
	// table 3, store users' followee id
	private HTable test;

	private Configuration conf;
	private String hostDNS;
	private String tableName;
	private String tableName2;
	private String tableName3;

	/**
	 * connect to all the tables in constructor
	 */
	public HBaseConnector() {
		tableName = "shell";
		tableName2 = "profile";
		tableName3 = "test";
		hostDNS = "ec2-54-175-117-32.compute-1.amazonaws.com";
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", hostDNS);
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.master", hostDNS + ":60000");

		try {
			HBaseAdmin admin = new HBaseAdmin(conf);
			admin.checkHBaseAvailable(conf);
			System.out.println("Build HBaseAdmin");
			shell = new HTable(conf, Bytes.toBytes(tableName));
			profile = new HTable(conf, Bytes.toBytes(tableName2));
			test = new HTable(conf, Bytes.toBytes(tableName3));

			System.out.println("Connected to HBase: " + shell.getTableName());
			System.out.println("Connected to HBase: " + profile.getTableName());
			System.out.println("Connected to HBase: " + test.getTableName());

		} catch (MasterNotRunningException e) {
			e.printStackTrace();

		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * get followers by user id
	 * @param id String
	 * @return a jsonarrya of followers' ids
	 */
	public JSONArray getFollowers(String id) {

		JSONArray followers = new JSONArray();
		PriorityQueue<Follower> queue = new PriorityQueue<Follower>();


		Result answer;
		String[] ids = null;
		try {
			answer = shell.get(new Get(Bytes.toBytes(id)));
			if (!answer.isEmpty()) {
				for (KeyValue r : answer.list()) {
					ids = Bytes.toString(r.getValue()).split("\\s+");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// get name and url using ids

		if (ids == null) {
			// not find valid answer
			JSONObject follower = new JSONObject();
			follower.put("name", "Unauthorized");
			follower.put("profile", "#");
			followers.put(follower);
		} else {
			// find valid answer
			String name = null;
			String url = null;
			for (String userId : ids) {
				try {
					answer = profile.get(new Get(Bytes.toBytes(userId)));
					if (!answer.isEmpty()) {
						// get value according to column name
						name = Bytes.toString(
								answer.getValue(Bytes.toBytes("data2"),
										Bytes.toBytes("name"))).trim();
						url = Bytes.toString(
								answer.getValue(Bytes.toBytes("data2"),
										Bytes.toBytes("url"))).trim();
						if (name != null && url != null) {
							Follower f = new Follower(name, url);
							queue.add(f);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// final answer format
			while (queue.peek() != null) {
				Follower f = queue.poll();
				JSONObject follower = new JSONObject();
				// System.out.println(f.toString());
				follower.put("name", f.getName());
				follower.put("profile", f.getUrl());
				followers.put(follower);
			}
		}
		return followers;
	}

	/**
	 * get followee
	 * @param id String query id
	 * @return an array of followee's ids
	 */
	public String[] getFollowee(String id) {
		Result answer;
		String[] ids = null;
		try {
			answer = test.get(new Get(Bytes.toBytes(id)));
			if (!answer.isEmpty()) {
				for (KeyValue r : answer.list()) {
					ids = Bytes.toString(r.getValue()).split("\\s+");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return ids;

	}
}
