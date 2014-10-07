package teanlis;

/**
 * 
 * This class represents chapters in a book, sections in a scientific work and in 
 * general every textual unit of organization which is potentially larger than a 
 * single page. Chapters are essentially just linguistic units which have a title,
 * but the intended use is to mark the units of textual organization described 
 * above.
 * 
 * @author Andreas Müller
 */

public class Chapter extends Linguistic_Element {
	
	/**
	 * The tile of the chapter.
	 */
	String title;
	
	/**
	 * The index of the sentence the chapter starts with. More precisely, the 
	 * sentence which contains the start offset of the chapter.
	 */
	int start_sentence;
	
	/**
	 * The index of the sentence the chapter ends with. More precisely, the sentence
	 * which contains the end offset of the chapter.
	 */
	int end_sentence;

	/**
	 * Get the title of the chapter.
	 * @return The title of the chapter.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the title of the chapter.
	 * @param title The title of the chapter.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the index of the sentence the chapter starts with.
	 * @return The index of the sentence the chapter starts with.
	 */
	public int getStart_sentence() {
		return start_sentence;
	}
	
	/**
	 * Set the index of the sentence the chapter starts with.
	 * @param start_sentence The index of the sentence the chapter starts with.
	 */
	public void setStart_sentence(int start_sentence) {
		this.start_sentence = start_sentence;
	}
	
	/**
	 * Get the index of the sentence the chapter ends with.
	 * @return The index of the sentence the chapter ends with.
	 */
	public int getEnd_sentence() {
		return end_sentence;
	}
	
	/**
	 * Set the index of the sentence the chapter ends with.
	 * @param end_sentence The index of the sentence the chapter ends with.
	 */
	public void setEnd_sentence(int end_sentence) {
		this.end_sentence = end_sentence;
	}
	
	/**
	 * No-arguments constructor for the chapter.
	 */
	public Chapter () {
		
	}
	
	/**
	 * A constructor for the chapter which takes the title of the chapter as its 
	 * argument.
	 * @param title The title of the chapter.
	 */
	public Chapter (String title) {
		this.title = title;
	}
	
	
}
