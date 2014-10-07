package teanlis;

/**
 * This class represents a keyword. A keyword is represented by a string and a score
 * which represents the importance of the keyword in the linguistic unit it is 
 * computed from.
 * 
 * @author Andreas Müller
 *
 */
public class Keyword {
	
	/**
	 * Return the keyword as a string.
	 * @return The keyword as a string.
	 */
	public String getKeyword() {
		return keyword;
	}
	
	/**
	 * Set the keyword.
	 * @param keyword The keyword.
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	/**
	 * Get the score for this keyword.
	 * @return The score for this keyword.
	 */
	public float getScore() {
		return score;
	}
	
	/**
	 * Set the score for this keyword.
	 * @param score The score for this keyword.
	 */
	public void setScore(float score) {
		this.score = score;
	}
	
	/**
	 * The string representing this keyword.
	 */
	String keyword;
	
	/**
	 * The score of this keyword representing, for example, the importance of the 
	 * keyword in a word cloud.
	 */
	float score;
}
