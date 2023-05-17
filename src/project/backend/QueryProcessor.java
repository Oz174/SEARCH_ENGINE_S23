package project.backend;
import project.frontend.ResultDisplay;

import java.io.IOException;
import java.util.ArrayList;

public class QueryProcessor {
	
	private static ArrayList<ArrayList<String>> process_query(String query){
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> literal_words = new ArrayList<String>();
		try {
			TextProcessor tp = new TextProcessor();
			String[] temp_literal_words = query.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
			String[] temp_words = new String[temp_literal_words.length];
			for (int i = 0; i < temp_literal_words.length; i++){
				temp_words[i] = tp.stem_word(temp_literal_words[i]);
			}
			for (int i = 0; i < temp_words.length; i++){
				if (!temp_words[i].equals("")){
					words.add(temp_words[i]);
					literal_words.add(temp_literal_words[i]);
				}
			}
			return new ArrayList<ArrayList<String>>(){{
				add(words);
				add(literal_words);
			}};
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String search_string_to_sql_query(String search_term){
		if (search_term == null || search_term.equals("")){
			return "";
		}
		if (search_term.charAt(0) == '"' && search_term.charAt(search_term.length() - 1) == '"'){
			ArrayList<ArrayList<String>> words_arr = process_query(search_term.substring(1, search_term.length() - 1));
			ArrayList<String> words = words_arr.get(0);
			ArrayList<String> literal_words = words_arr.get(1);
			return WebRanker.formulate_phrase_search_query(words, literal_words);
		}
		ArrayList<ArrayList<String>> words_arr = process_query(search_term);
		ArrayList<String> words = words_arr.get(0);
		ArrayList<String> literal_words = words_arr.get(1);
		return WebRanker.formulate_search_query(words, literal_words);
	}

	public static void main(String[] args) throws IOException {
		String query = "\"samsung galaxy\"";
		//System.out.println(search_string_to_sql_query(query));
		//System.out.println("\n--------------------------------------------------\n");
		query = "cnn";
		System.out.println(search_string_to_sql_query(query));
		db.connect();
		ResultDisplay.getdocs(query);
		db.disconnect();
	}
}
