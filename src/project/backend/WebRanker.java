package project.backend;

import java.util.ArrayList;

public class WebRanker {
	public static String formulate_phrase_search_query(ArrayList<String> words, ArrayList<String> literal_words) {
		if (words.size() == 0 || words.size() > 3 || words.size() != literal_words.size())
			return "";
		String query = "SELECT link, SUM( (";
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
		query += ") * CASE";
		query += " WHEN R1.tag = 'keyword' THEN 15";
		query += " WHEN R1.tag = 'title' THEN 8";
		query += " WHEN R1.tag = 'h1' THEN 5";
		query += " ELSE 1";
		query += " END";
		query += ") AS score";
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
		query += " GROUP BY link";
		query += " HAVING COUNT(*) > 0";
		query += " ORDER BY score DESC;";
		return query;
	}

	public static String formulate_search_query(ArrayList<String> words, ArrayList<String> literal_words) {
		if (words.size() == 0 || literal_words.size() != words.size())
			return "";
		String query = "SELECT link, SUM( (";
		String literal_words_set = "('" + String.join("', '", literal_words) + "')";
		String words_set = "('" + String.join("', '", words) + "')";
		query += " CASE ";
		query += "WHEN R1.literal_word in " + literal_words_set + " THEN 2";
		query += " ELSE 1";
		query += " END";
		query += ") * CASE";
		query += " WHEN R1.tag = 'keyword' THEN 15";
		query += " WHEN R1.tag = 'title' THEN 8";
		query += " WHEN R1.tag = 'h1' THEN 5";
		query += " ELSE 1";
		query += " END";
		query += ") AS score";
		query += " FROM Ranker_Dictionary R1";
		query += " JOIN Docs";
		query += " ON Docs.doc_id = R1.doc_id";
		query += " WHERE R1.word in " + words_set;
		query += " GROUP BY link";
		query += " HAVING COUNT(*) > 0";
		query += " ORDER BY score DESC;";
		return query;
	}

	public static void main(String[] args) {
		// String[] words = new String[3];
		// words[0] = "samsung";
		// words[1] = "galaxi";
		// words[2] = "cnn";
		// String[] literal_words = new String[3];
		// literal_words[0] = "samsung";
		// literal_words[1] = "galaxy";
		// literal_words[2] = "cnn";
		// System.out.println(formulate_search_query(words, literal_words));
		// System.out.println(formulate_phrase_search_query(words, literal_words));
	}
}
