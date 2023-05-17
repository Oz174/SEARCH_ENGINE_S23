package project.backend;

import java.util.ArrayList;

public class WebRanker {
	private static final String tag_weights = " CASE "
											+ "WHEN R1.tag = 'keyword' THEN 15 "
											+ "WHEN R1.tag = 'title' THEN 8 "
											+ "WHEN R1.tag = 'h1' THEN 5 "
											+ "ELSE 1 "
											+ "END";
	public static String formulate_phrase_search_query(ArrayList<String> words, ArrayList<String> literal_words) {
		if (words.size() == 0 || words.size() > 3 || words.size() != literal_words.size())
			return "";
		String query = "SELECT TOP 50 link, SUM( (";
		int id = 0;
		String[] cases = new String[words.size()];
		for (String lw : literal_words) {
			id++;
			String temp = "";
			temp += " CASE ";
			temp += "WHEN R" + Integer.toString(id) + ".literal_word = '" + lw + "' THEN 2 ";
			temp += "ELSE 1 ";
			temp += "END";
			cases[id - 1] = temp;
		}
		query += String.join(" +", cases);
		query += ") *";
		query += tag_weights;
		query += ") + visit_count / 20 AS score";
		query += " FROM Ranker_Dictionary R1";
		for (int i = 2; i <= words.size(); i++) {
			String t1 = "R" + Integer.toString(i - 1);
			String t2 = "R" + Integer.toString(i);
			query += " JOIN Ranker_Dictionary " + t2;
			query += " ON " + t1 + ".word = '" + words.get(i - 2) + "'";
			query += " AND " + t2 + ".word = '" + words.get(i - 1) + "'";
			query += " AND " + t1 + ".doc_id = " + t2 + ".doc_id";
			query += " AND " + t1 + ".tag = " + t2 + ".tag";
			query += " AND " + t1 + ".tag_id = " + t2 + ".tag_id";
			query += " AND " + t1 + ".pos = " + t2 + ".pos - 1";
		}
		query += " JOIN Docs";
		query += " ON Docs.doc_id = R1.doc_id";
		if (words.size() == 1)
			query += " WHERE R1.word = '" + words.get(0) + "'";
		query += " GROUP BY link, visit_count";
		query += " HAVING COUNT(*) > 0";
		query += " ORDER BY score DESC;";
		return query;
	}

	public static String formulate_search_query(ArrayList<String> words, ArrayList<String> literal_words) {
		if (words.size() == 0 || literal_words.size() != words.size())
			return "";
		String query = "SELECT TOP 50 link, SUM( (";
		String literal_words_set = "('" + String.join("', '", literal_words) + "')";
		String words_set = "('" + String.join("', '", words) + "')";
		query += " CASE ";
		query += "WHEN R1.literal_word in " + literal_words_set + " THEN 2";
		query += " ELSE 1";
		query += " END";
		query += ") *";
		query += tag_weights;
		query += ") + visit_count / 20 AS score";
		query += " FROM Ranker_Dictionary R1";
		query += " JOIN Docs";
		query += " ON Docs.doc_id = R1.doc_id";
		query += " WHERE R1.word in " + words_set;
		query += " GROUP BY link, visit_count";
		query += " HAVING COUNT(*) > 0";
		query += " ORDER BY score DESC;";
		return query;
	}

	public static void main(String[] args) {
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> literal_words = new ArrayList<String>();
		words.add("samsung");
		words.add("galaxi");
		words.add("cnn");
		literal_words.add("samsung");
		literal_words.add("galaxy");
		literal_words.add("cnn");
		System.out.println(formulate_search_query(words, literal_words));
		System.out.println("\n--------------------------------------------------\n");
		System.out.println(formulate_phrase_search_query(words, literal_words));
	}
}
