package epoetics.core;

/**
 * This class represents a sentence. The fields containing_paragraph and
 * containing_chapter contain the index of the paragraph and chapter the
 * sentence is contained in respectively. The values of those fields are
 * computed by the BuildHierarchy method in the document the sentence is
 * contained in.
 * 
 * @author Andreas Müller
 */

public class Sentence extends Linguistic_Element {
	
	/**
	 * The paragraph containing the sentence.
	 */
	int containing_paragraph;
	
	/**
	 * The chapter containing the sentence.
	 */
	int containing_chapter;
	
	/**
	 * No-arguments constructor for the sentence.
	 */
	public Sentence () {
		this.containing_paragraph = -1;
		this.containing_chapter = -1;
	}
	
	/**
	 * Get the paragraph containing the sentence.
	 * @return
	 */
	public int getContaining_paragraph() {
		return containing_paragraph;
	}
	
	/**
	 * Set the paragraph containing the sentence.
	 * @param containing_paragraph
	 */
	public void setContaining_paragraph(int containing_paragraph) {
		this.containing_paragraph = containing_paragraph;
	}
	
	/**
	 * Get the chapter containing the sentence.
	 * @return
	 */
	public int getContaining_chapter() {
		return containing_chapter;
	}
	
	/**
	 * Set the chapter containing the sentence.
	 * @param containing_chapter
	 */
	public void setContaining_chapter(int containing_chapter) {
		this.containing_chapter = containing_chapter;
	}
}
