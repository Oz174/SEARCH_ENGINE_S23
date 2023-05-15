package project.backend;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class WebCrawler {

	public static db database;
	private static final int MAX_TO_BE_CRAWLED = 50;
	private static final int MAX_PER_PAGE = 10;

	private ConcurrentHashMap<String, Boolean> isVisited;
	private ArrayBlockingQueue<String> toVisit;

	public int getNumberofVisitedPages() {
		return this.isVisited.size();
	}

	public int getNumberofPagesToVisit() {
		return this.toVisit.size();
	}

	public WebCrawler(ArrayList<String> toVisit, ArrayList<String> visited, db sql) {

		this.isVisited = new ConcurrentHashMap<String, Boolean>();
		if (visited != null) {
			for (String url : visited) {
				this.isVisited.put(url, true);
			}
		}
		this.toVisit = new ArrayBlockingQueue<String>(MAX_TO_BE_CRAWLED);
		if (toVisit != null) {
			for (String url : toVisit)
				this.toVisit.offer(url); // add() method throws error when queue is full
			// offer() method returns false in such situation.
		}
		database = sql;
	}

	private String normalizeLink(String url) {
		// try {
		if (url == null || url.equals(""))
			return null;
		url = url.replaceAll("^(.*?)(#.*)+$", "$1$2");
		url = url.replaceAll("^(https?://[^/]+)(/+)(.*)$", "$1/$3");
		// last char in url
		char lastChar = url.charAt(url.length() - 1);
		if (lastChar != '/')
			url += '/';
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		return url;
		// } catch (Exception e) {
		// e.printStackTrace();
		// return null;
		// }
	}

	public void crawl2(){
		int toVisitSize = 0;
		synchronized (toVisit) {
			toVisitSize = toVisit.size();
		}
		while (toVisitSize != 0) {
			if (toVisit.size() == 0)
				return;
			// 1- pop one url from tovisit array
			String url = this.toVisit.poll();
			if (url == null)
				continue;

			// 2 --> check if robot is allowed
			boolean isRobotAllowed = false;
			try {
				String robotFileContent = getRobotFile(url);
				isRobotAllowed = isRobotAllowed(robotFileContent, url);
			} catch (MalformedURLException e) {
				db.remove_url(url);
				continue;
			}
			if (!isRobotAllowed) {
				db.remove_url(url);

				System.out.println("-----------------ROBOT NOT ALLOWED------------------:");
				continue;
			}

			// 2 --> check if url is visited before
			if (this.isVisited.containsKey(url)) {
				System.out.println("-----------------VISITED BEFORE------------------:");
				continue;
			}
			//Get html document
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			}
			catch (Exception e) {
				db.remove_url(url);
				continue;
			}

			this.isVisited.put(url, true);

			if (db.get_doc_id(url) == -1)
				db.add_url(url);
			// get all links in this page

			Elements elements = doc.select("a");
			System.out.println("Thread " + Thread.currentThread().getName() + " visited page: " + url + " \nFound ("
					+ elements.size() + ") link(s)");
			int counter = 0;
			boolean completelyCrawled = true;
			Collections.shuffle(elements);
			for (Element e : elements) {

				synchronized (toVisit) {
					toVisitSize = toVisit.size();
				}

				if (toVisitSize + isVisited.size() <= MAX_TO_BE_CRAWLED && counter <= MAX_PER_PAGE) {
					String href = e.absUrl("href");
					// Clean link here
					href = normalizeLink(href);
					if (href == null)
						continue;
					synchronized (this.toVisit) {
						if (!this.toVisit.contains(href) && !this.isVisited.containsKey(href) && db.get_doc_id(href) == -1) {
							db.add_url(href);
							this.toVisit.offer(href);
							counter++;
						}
					}
					continue;
				}
				if (counter <= MAX_PER_PAGE) {
					completelyCrawled = false;
				}
				break;
			}
			synchronized (toVisit) {
				toVisitSize = toVisit.size();
			}
			if (completelyCrawled)
				db.set_crawled(url);
		}
	}

	public String getRobotFile(String url) throws MalformedURLException {
		URL u = new URL(url);

		String domain = u.getHost();
		String[] parts = domain.split("\\.");
		domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
		String robotFile = u.getProtocol() + "://" + domain + "/robots.txt";		
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
					writer.write(url + "\n");
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
		webCrawler.crawl2();
	}
}
