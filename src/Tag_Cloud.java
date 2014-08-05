package epoetics.core;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * This class represents a tag cloud containing words which are significant for the 
 * linguistic element it is computed from. The class contains methods for computing 
 * the ranks of the keywords in the cloud. The class also stores the scores of the 
 * keywords in the cloud.
 * @author Andreas
 *
 */
public class Tag_Cloud extends Linguistic_Element {
	
	/**
	 * The sorted list of the scores of the keywords in the cloud.
	 */
	Float[] sorted_score_list;
	
	/**
	 * A map from scores to keywords in the cloud which have a particular score.
	 */
	HashMap<Float, ArrayList<String>> keywords_at_score;
	
	/**
	 * A map from keywords to the ranks of the keywords.
	 */
	HashMap<String, Integer> keyword_at_rank;
	
	/**
	 * Get the map from keywords to the ranks of the keywords.
	 * @return
	 */
	public HashMap<String, Integer> getKeyword_at_rank() {
		return keyword_at_rank;
	}
	
	/**
	 * Set the map from the keywords to the ranks of the keywords.
	 * @param keyword_at_rank
	 */
	public void setKeyword_at_rank(HashMap<String, Integer> keyword_at_rank) {
		this.keyword_at_rank = keyword_at_rank;
	}
	
	/**
	 * Get the sorted list of scores of the keywords in the cloud.
	 * @return
	 */
	public Float[] getSorted_score_list() {
		return sorted_score_list;
	}
	
	/**
	 * Set the sorted list of scores of the keywords in the cloud.
	 * @param sorted_score_list
	 */
	public void setSorted_score_list(Float[] sorted_score_list) {
		this.sorted_score_list = sorted_score_list;
	}
	
	/**
	 * Get the map from scores to lists of keywords which have a particular score.
	 * @return
	 */
	public HashMap<Float, ArrayList<String>> getKeywords_at_score() {
		return keywords_at_score;
	}
	
	/**
	 * Set the map from scores to lists of keywords which have a particular score.
	 * @param keywords_at_score
	 */
	public void setKeywords_at_score(
			HashMap<Float, ArrayList<String>> keywords_at_score) {
		this.keywords_at_score = keywords_at_score;
	}
	
	/**
	 * Get the list of keywords for which the score is greater than the specified 
	 * threshold.
	 * @param threshold
	 * @return
	 */
	public ArrayList<String> getScoresAtLevel (Float threshold) {
		ArrayList<String> list = new ArrayList<String>();
		
		for (Float key: keywords_at_score.keySet()) {
			if (key > threshold) {
				list.addAll(keywords_at_score.get(key));
			}
		}
		
		return list;
	}
	
	/**
	 * Get the list of keywords with the n best scores.
	 * @param n
	 * @return
	 */
	public ArrayList<String> getNTopKeywords (int n) {
		ArrayList<String> list = new ArrayList<String>(n);
		
		int length = sorted_score_list.length-1;
		
		for (int i=0;i<sorted_score_list.length;i++) {
			if (list.size() > n-1) {
				break;
			}
			ArrayList<String> keywords = keywords_at_score.get(sorted_score_list[length-i]);
			
			for (String keyword: keywords) {
				if (list.size() < n) {
					list.add(keyword);
				} else {
					break;
				}
			}
		}
		return list;
	}
	
	/**
	 * Compute the map from keywords to the ranks of those keywords.
	 */
	public void MakeKeywordAtRankMap () {
		
		keyword_at_rank = new HashMap<String, Integer>();
		
		for (int i=0;i<sorted_score_list.length;i++) {
			ArrayList<String> keywords_at_i = keywords_at_score.get(
					sorted_score_list[sorted_score_list.length-1-i]);
			
			for (String keyword: keywords_at_i) {
				keyword_at_rank.put(keyword, i+1);
			}
		}
	}
}
