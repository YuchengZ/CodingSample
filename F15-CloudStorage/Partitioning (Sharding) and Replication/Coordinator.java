import java.sql.Timestamp;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.PriorityBlockingQueue;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class Coordinator extends Verticle {
	// Default mode: replication. Possible string values are "replication" and
	// "sharding"
	private static String storageType = "replication";

	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances
	 */
	private static final String dataCenter1 = "ec2-54-175-136-57.compute-1.amazonaws.com";
	private static final String dataCenter2 = "ec2-54-175-235-31.compute-1.amazonaws.com";
	private static final String dataCenter3 = "ec2-54-175-156-223.compute-1.amazonaws.com";

	private static final String GET = "get";
	private static final String PUT = "put";

	final HashMap<String, PriorityBlockingQueue<Request>> table = new HashMap<String, PriorityBlockingQueue<Request>>();

	public class myComparator implements Comparator<Request> {

		@Override
		public int compare(Request o1, Request o2) {
			return o1.getTime().compareTo(o2.getTime());
		}

	}

	public class Request implements Comparable<Request> {
		private String key;
		private String value;
		private String time;
		private String name;

		public Request(String key, String value, String time, String name) {
			this.key = key;
			this.value = value;
			this.time = time;
			this.name = name;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String getTime() {
			return time;
		}

		public String getName() {
			return name;
		}

		@Override
		public int compareTo(Request o) {
			return time.compareTo(o.getTime());
		}

	}

	public class putThread extends Thread {
		private Request request;
		private String dataCenter;

		public putThread(Request r, String dataCenter) {
			this.request = r;
			this.dataCenter = dataCenter;
		}

		@Override
		public void run() {
			try {
				KeyValueLib.PUT(dataCenter, request.getKey(),
						request.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void putIntoTable(Request newRequest) {
		// if (table.contains(newRequest.getKey())) {
		if (table.containsKey(newRequest.getKey())) {
			// already have request in queue
			table.get(newRequest.getKey()).add(newRequest);
		} else {
			// no request for this key
			PriorityBlockingQueue<Request> q = new PriorityBlockingQueue<Request>();
			q.add(newRequest);
			table.put(newRequest.getKey(), q);

		}
	}

	@Override
	public void start() {
		// DO NOT MODIFY THIS
		KeyValueLib.dataCenters.put(dataCenter1, 1);
		KeyValueLib.dataCenters.put(dataCenter2, 2);
		KeyValueLib.dataCenters.put(dataCenter3, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String value = map.get("value");
				// You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System
						.currentTimeMillis()
						+ TimeZone.getTimeZone("EST").getRawOffset())
						.toString();

				Thread t = new Thread(new Runnable() {
					public void run() {
						/* put into table */
						// request
						Request newRequest = new Request(key, value, timestamp,
								PUT);
						putIntoTable(newRequest);
						
						/* put into datastore */
						PriorityBlockingQueue<Request> q = table.get(key);
						synchronized (key) {

							if (!q.isEmpty()) {
								Request request = q.peek();
								if (request.getName().equals(PUT)) {
									request = q.poll();
									// put request to datacenter1
									Thread thread1 = new putThread(request, dataCenter1);

									// put request to datacenter2
									Thread thread2 = new putThread(request, dataCenter2);

									// put request to datacenter3
									Thread thread3 = new putThread(request, dataCenter3);

									thread1.start();
									thread2.start();								
									thread3.start();
								} else {
									notifyAll();
								}
							}
						}
					}

				});
				t.start();
				

				req.response().end(); // Do not remove this
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String loc = map.get("loc");
				// You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System
						.currentTimeMillis()
						+ TimeZone.getTimeZone("EST").getRawOffset())
						.toString();

				Thread t = new Thread(new Runnable() {
					public void run() {
						/* put get request in queue */
						Request newRequest = new Request(key, loc, timestamp,
								GET);
						putIntoTable(newRequest);

						while (table.get(key).isEmpty()
								|| table.get(key).peek() != newRequest) {
							try {
								wait();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						String value = new String();
						synchronized (key) {
							Request r = table.get(key).poll();
							String i = r.getValue();
							String dns;
							if (i.equals("1")) {
								dns = dataCenter1;
							} else if (i.equals("2")) {
								dns = dataCenter2;
							} else {
								dns = dataCenter3;
							}
							try {
								value = KeyValueLib.GET(dns, key);
							} catch (Exception e) {
								e.printStackTrace();
							}

							// check next one
							if (!table.get(key).isEmpty()
									&& table.get(key).peek().getName()
											.equals(GET)) {
								notifyAll();
							}
						}

						req.response().end(value);
					}
				});
				t.start();
			}
		});

		routeMatcher.get("/storage", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				storageType = map.get("storage");
				// This endpoint will be used by the auto-grader to set the
				// consistency type that your key-value store has to support.
				// You can initialize/re-initialize the required data structures
				// here
				req.response().end();
			}
		});

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type", "text/html");
				String response = "Not found.";
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(8080);
	}
}
