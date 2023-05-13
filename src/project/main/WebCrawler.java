package main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.Queue;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import DB.MongoDB;

public class WebCrawler {
	
	private String startUrl;
	MongoDB database;
	private static final int MAX_TO_BE_CRAWLED = 5000;
	private static final int MAX_PER_PAGE = 10;

	private ConcurrentHashMap<String, Boolean> isVisited;
	private ArrayBlockingQueue<String> toVisit;

	public int getNumberofVisitedPages() {
		return this.isVisited.size();
	}

	public int getNumberofPagesToVisit() {
		return this.toVisit.size();
	}

	public WebCrawler(ArrayList <String> toVisit, ArrayList<String> visited,MongoDB database) {

		this.isVisited = new ConcurrentHashMap<String, Boolean>();
		if(visited != null)
		{
			for (String url : visited) {
				this.isVisited.put(url, true);
			}
		}
		this.toVisit = new ArrayBlockingQueue <String> (MAX_TO_BE_CRAWLED);
		if(toVisit != null)
		{
			for (String url : toVisit)
				this.toVisit.offer(url); //add() method throws error when queue is full
			//offer() method returns false in such situation.
		}
		this.database = database;
	}

	private String normalizeLink(String link, String base) {

		try {
			URL u = new URL(base);
			if (link.startsWith("./")) {
				link = link.substring(1, link.length());
				// form the full link
				link = u.getProtocol() + "://" + u.getAuthority() + rmvFileFromPath(u.getPath()) + link;
			} else if (link.startsWith("#")) {
				link = base + "/" + link;
			} else if (link.startsWith("javascript")) {
				link = null;
			} else if (link.startsWith("/") || link.startsWith("../")
					|| (!link.startsWith("http://") && !link.startsWith("https://"))) {

				link = u.getProtocol() + "://" + u.getAuthority() + rmvFileFromPath(u.getPath()) + link;
			}
			if (link != null)
				link = link.toLowerCase();
			return link;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getHTML(String url) {
		URL u;
		try {
			u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestProperty("User-Agent", "BBot/1.0");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			int r_code = connection.getResponseCode();
			if (r_code == HttpURLConnection.HTTP_OK) {
				InputStream is = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				String line;
				String html = "";
				while ((line = reader.readLine()) != null) {
					html += line + "\n";
				}
				html = html.trim();
				return html;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

	public void crawl2() throws MalformedURLException {
		int toVisitSize = 0;
		synchronized (toVisit) {
			toVisitSize = toVisit.size();
			// System.out.println(toVisitSize);
		}
		while (toVisitSize != 0) {
			if (toVisit.size() == 0)
				return;
			// 1- pop one url from tovisit array
			String url = this.toVisit.poll();
			if (url == null)
				continue;

			// 2 --> check if robot is allowed
			String robotFileContent = getRobotFile(url);
			boolean isRobotAllowed = isRobotAllowed(robotFileContent, url);
			if (!isRobotAllowed) {
				database.removeURL(url);
				database.removeLink(url);
				System.out.println("-----------------ROBOT NOT ALLOWED------------------:");
				continue;
			}

			// 2 --> check if url is visited before
			if (this.isVisited.containsKey(url)) {
				System.out.println("-----------------VISITED BEFORE------------------:");
				continue;
			}

			// now url is valid for crawling so get html
			String html = getHTML(url);
			if (html.equals("")) {
				database.removeURL(url);
				database.removeLink(url);
				continue;
			}
			Document doc = Jsoup.parse(html);
			// skip this url if its content already exists in db
			if (database.getHtml(html).size() != 0) {
				database.removeURL(url);
				database.removeLink(url);
				continue;
			}
			// add to visited list
			// synchronized(this.isVisited) {
			this.isVisited.put(url, true);
			// }
			// Insert page and its content in db
			// synchronized(this.database) {

			if (database.getURL(url).size() == 0)
				database.InsertUrl(url, html);
			else {// url exists in db
				String prevHtml = "";
				Object htmlField = database.getURL(url).get(0).get("html");
				if (htmlField != null)
					prevHtml = htmlField.toString();
				if (!prevHtml.equals(html))
					database.setContent(url, html);
			}
			// ArrayList<String> urlId=database.getUrlId(url);
			// this.savehtmlToFile(urlId.get(0)+".txt",doc);
			database.setCrawled(url);
			// }
			// get all links in this page

			Elements elements = doc.select("a");
			System.out.println("Thread " + Thread.currentThread().getName() + " visited page: " + url + " \nFound ("
					+ elements.size() + ") link(s)");
			int counter = 0;
			for (Element e : elements) {

				synchronized (toVisit) {
					toVisitSize = toVisit.size();
					// System.out.println("toVisitSize "+toVisitSize);
				}
				// System.out.println("visited size "+ isVisited.size());
				if (toVisitSize + isVisited.size() <= MAX_TO_BE_CRAWLED && counter <= MAX_PER_PAGE) { // TODO: Add
																										// visited size
																										// to condition
					String href = e.attr("href");
					href = normalizeLink(href, url);
					if (href == null)
						continue;
					// TODO: add to database and toVisit array
					synchronized (this.toVisit) {
						if (!this.toVisit.contains(href) && !this.isVisited.containsKey(href)) {
							// synchronized(this.database) {
							if (database.getURL(href).size() == 0)
								database.InsertUrl(href);
							database.InsertLink(database.getUrlId(url).get(0), href);
							// }
							this.toVisit.offer(href);
							counter++;
						}
					}
				} else
					break;
			}
		}
	}

	private String rmvFileFromPath(String path) {
		int pos = path.lastIndexOf("/");
		return pos <= -1 ? path : path.substring(0, pos + 1);
	}

	public String getRobotFile(String url) throws MalformedURLException {

		URL u = new URL(url);

		String robotFile = u.getProtocol() + "://" + u.getHost() + "/robots.txt";
		URL robotUrl;
		try {
			robotUrl = new URL(robotFile);
		} catch (MalformedURLException e) {
			return null;
		}

		String robotFileContent = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(robotUrl.openStream()));
			String fileLine;
			while ((fileLine = in.readLine()) != null) {

				robotFileContent += fileLine;
				robotFileContent += "\n";
			}
			in.close();
			return robotFileContent.equals("") ? null : robotFileContent;

		} catch (IOException e)// robots.txt not found
		{
			return null;
		}
	}

	public boolean isRobotAllowed(String robotFileContent, String url) throws MalformedURLException {

		if (robotFileContent == null)
			return false;
		if (robotFileContent.contains("Disallow:")) {
			String userAgent = null;
			String[] robotFileLines = robotFileContent.split("\n");
			ArrayList<RobotRule> robotRules = new ArrayList<>();

			for (int i = 0; i < robotFileLines.length; i++) {
				String line = robotFileLines[i].trim();
				if (line.toLowerCase().startsWith("user-agent")) {
					int from = line.indexOf(":") + 1;
					int till = line.length();
					userAgent = line.substring(from, till).trim().toLowerCase();
				} else if (line.startsWith("Disallow:")) {
					if (userAgent != null) {
						int from = line.indexOf(":") + 1;
						int till = line.length();
						RobotRule r = new RobotRule();
						r.userAgent = userAgent;
						r.rule = line.substring(from, till).trim();
						robotRules.add(r);
					}
				}
			}

			for (RobotRule robotRule : robotRules) {
				if (robotRule.rule.length() == 0)
					continue; // allows all
				// disallow when user agent is googlebot or *
				if (robotRule.userAgent.equals("googlebot") || robotRule.userAgent.equals("*")) {
					if (robotRule.rule.equals("/"))
						return false; // disallows all
					URL u = new URL(url);
					String path = u.getPath();
					if (path.length() >= robotRule.rule.length()) {
						String ruleCompare = path.substring(0, robotRule.rule.length());
						if (ruleCompare.equals(robotRule.rule))
							return false;
					}
				}
			}
		}
		return true; // file doesn't contain any Disallow
	}

	class RobotRule {
		public String userAgent;
		public String rule;
	}

	public void saveUrlsToFile(String filename) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);

			for (Object url : this.isVisited.keySet()) {
				try {
					writer.write("\n" + url);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void savehtmlToFile(String filename, Document html) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);

			try {
				writer.write(html.toString());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
class webCrawlerRunnable implements Runnable {
	private WebCrawler webCrawler;

	public webCrawlerRunnable(WebCrawler webCrawler) {
		this.webCrawler = webCrawler;
	}

	public void run() {

		try {
			webCrawler.crawl2();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
