package epoetics.core;

/**
 * This class represents a paragraph.
 * 
 * @author Andreas Müller
 *
 */
public class Paragraph extends Linguistic_Element {
	
	/**
	 * The chapter containing the paragraph.
	 */
	int containing_chapter;
	
	/**
	 * The page containing the paragraph.
	 */
	int containing_page;
	
	/**
	 * Get the page the paragraph is contained in.
	 * @return
	 */
	public int getContaining_page() {
		return containing_page;
	}
	
	/**
	 * Set the page the paragraph is contained on.
	 * @param containing_page
	 */
	public void setContaining_page(int containing_page) {
		this.containing_page = containing_page;
	}
	
	/**
	 * Get the chapter containing the paragraph.
	 * @return
	 */
	public int getContaining_chapter() {
		return containing_chapter;
	}
	
	/**
	 * Set the chapter containing the paragraph.
	 * @param chapter
	 */
	public void setContaining_chapter(int chapter) {
		this.containing_chapter = chapter;
	}
}
