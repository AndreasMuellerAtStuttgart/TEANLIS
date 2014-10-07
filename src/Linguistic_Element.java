package teanlis;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the base class for all linguistic and organizational elements of a text.
 * It defines the attributes and elements a linguistic and organizational element must posses.
 * The properties any linguistic or organizational element of text in our model has are:
 * 
 * 1. start offset and end offset: The character positions in the original text where the
 * element is located. Attributes of elements, like the part-of-speech tag of a word, are
 * given by String fields of a particular class, but the string the element consists of
 * is only given by offsets.
 * 
 * 2. A note_history: The note history stores the notes a user attaches to a particular element.
 * The keys are dates and the values are the notes themselves.
 * 
 * 3. A getString method: This method returns, given the string representing a text, the
 * substring which represents the element in the text. You have to take care that you
 * call this method with the string of the text the linguistic or organizationl element is
 * derived from, otherwise you will get nonsensical results.
 * 
 * @author Andreas Müller
 */

public class Linguistic_Element implements java.io.Serializable{
	
	/**
	 * A map from dates when the notes where made to notes a user made for the 
	 * linguistic element.
	 */
	HashMap<String, String> note_history; // date to note map
	
	/**
	 * Start offset of the linguistic element in the string of the document the 
	 * linguistic element is contained in.
	 */
	int start; // offset
	
	/**
	 * Get the start offset of the linguistic element.
	 * @return The start offset of the linguistic element.
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Set the start offset of the linguistic element.
	 * @param start Set the start offset of the linguistic element.
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	/**
	 * Get the end offset of the linguistic element.
	 * @return The end offset of the linguistic element.
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Set the end offset of the linguistic element.
	 * @param end The end offset of the linguistic element.
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	/**
	 * End offset of the linguistic element in the original text of the document 
	 * the linguistic element is contained in.
	 */
	int end; // token ids, not offsets in the original document
	
	/**
	 * Get the note history for this linguistic element.
	 * @return The note history for this linguistic element.
	 */
	public HashMap<String, String> getNote_history() {
		return note_history;
	}
	
	/**
	 * Set the note history for this linguistic element.
	 * @param note_history The note history for this linguistic element.
	 */
	public void setNote_history(HashMap<String, String> note_history) {
		this.note_history = note_history;
	}
	
	/**
	 * Get the string constituting the linguistic element. This method needs the 
	 * text of the document the linguistic element is contained in because the 
	 * string of a linguistic element is not stored, so the string of a linguistic 
	 * element is retrieved from the text of the document.
	 * @param original_text The text of the document.
	 * @return Get the string representation of this linguistic element.
	 */
	public String getString(String original_text) {
		String return_string = "";
		
		try {
			return_string = original_text.substring(start, end);
		} catch (java.lang.StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return return_string;
	}
}
