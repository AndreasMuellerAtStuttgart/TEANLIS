package epoetics.core;

import java.util.HashMap;

/**
 * This class represents tokens. Tokens are the second smallest unit of the 
 * linguistic hierarchy (the smallest being characters). A token is more complex 
 * than other linguistic or organizational units in that it can have more properties.
 * Typical properties of a token include:
 * 
 * 1. A lemma
 * 2. Morphological properties
 * 3. A part-of-speech tag
 * 4. A token related to the token by a grammatical relation
 * 5. The name of the grammatical relation
 * 6. The sentence it is contained in
 * 
 * This class has fields for all those properties. In addition, it has an id-field to
 * make it easier to convert the datastructure to TEI-format, because xml-elements in
 * TEI should have an id. In this framework the id is not really important because we
 * use the position in the list of tokens a document consists of to relate tokens to
 * each other.
 * 
 * @author Andreas Müller
 *
 */

public class Token extends Linguistic_Element implements java.io.Serializable {
	
	/**
	 * The sentence containing the token.
	 */
	int containing_sentence;
	
	/**
	 * Get the sentence containing the token.
	 * @return The sentence which contains the token.
	 */
	public int getContaining_sentence() {
		return containing_sentence;
	}
	
	/**
	 * Get the lemma of the token.
	 * @return The lemma of the token.
	 */
	public String getLemma() {
		return lemma;
	}
	
	/**
	 * Set the lemma of the token.
	 * @param lemma The lemma of the token.
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	/**
	 * Get the part-of-speech tag of the token.
	 * @return The part-of-speech tag of the token.
	 */
	public String getPos() {
		return pos;
	}
	
	/**
	 * Set the part-of-speech tag of the token.
	 * @param pos The part-of-speech tag of the token.
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}
	
	/**
	 * Get the name of the grammatical dependency relation which relates the token 
	 * to another token.
	 * @return
	 */
	public String getDependency() {
		return dependency;
	}
	
	/**
	 * Set the name of the grammatical dependency relation which relates the token 
	 * to another token.
	 * @param dependency
	 */
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	
	/**
	 * Get the sentence containing the token.
	 * @return The sentence containing the token.
	 */
	public int getSentence() {
		return containing_sentence;
	}
	
	/**
	 * Set the sentence containing the token.
	 * @param containing_sentence The sentence containing the token.
	 */
	public void setSentence(int containing_sentence) {
		this.containing_sentence = containing_sentence;
	}
	
	/**
	 * Get the morphological properties of the token.
	 * @return The morphological properties of the token.
	 */
	public String getFunction() {
		return function;
	}
	
	/**
	 * Set the morphological properties of the token.
	 * @param function The morphological properties of the token.
	 */
	public void setFunction(String function) {
		this.function = function;
	}
	
	/**
	 * Get the index of the token this token is related to by the grammatical 
	 * dependency relation dependency.
	 * @return The index of the token this token is related to by the grammatical 
	 * dependency relation dependency.
	 */
	public int getGovernor() {
		return governor;
	}
	
	/**
	 * Set the index of the token this token is related to by the grammatical 
	 * dependency relation dependency.
	 * @param governor The index of the token this token is related to by the 
	 * grammatical dependency relation dependency.
	 */
	public void setGovernor(int governor) {
		this.governor = governor;
	}
	
	/**
	 * Set the sentence containing the token.
	 * @param containing_sentence The sentence containing the token.
	 */
	public void setContaining_sentence(int containing_sentence) {
		this.containing_sentence = containing_sentence;
	}
	
	/**
	 * The id of the token.
	 */
	int id;
	
	/**
	 * The lemma of the token.
	 */
	String lemma;
	
	/**
	 * The part-of-speech tag of the token.
	 */
	String pos;
	
	/**
	 * The name of the grammatical dependency relation which relates this token to 
	 * another token in the document.
	 */
	String dependency;
	
	/**
	 * The morphological properties of the token.
	 */
	String function;
	
	/**
	 * The index of the token this token is related to by the grammatical 
	 * dependency relation dependency.
	 */
	int governor;
	
	/**
	 * Get the lemma of the token.
	 * @return
	 */
	public String getlemma () {
		return this.lemma;
	}
	
	/**
	 * Get the id of the token.
	 * @return The id of the token.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Set the id of the token.
	 * @param id The id of the token.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * No-arguments constructor for the token.
	 */
	public Token () {
		this.start = -1;
		this.end = -1;
		
		this.containing_sentence = -1;
	}
	
	/**
	 * Constructor for the token which takes the start and offset of the token as 
	 * arguments.
	 * @param start_char The start offset of the token.
	 * @param end_char The end offset of the token.
	 */
	public Token (int start_char, int end_char) {
		this.start = start_char;
		this.end = end_char;
	}
}
