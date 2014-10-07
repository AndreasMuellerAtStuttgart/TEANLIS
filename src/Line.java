package teanlis;

/**
 * This class represents a textual line.
 * 
 * @author Andreas Müller
 *
 */

public class Line extends Linguistic_Element {
	
	/**
	 * No-arguments constructor for the line.
	 */
	public Line () {
		// getContainingEverything
	}
	
	/**
	 * The paragraph the line is contained in.
	 */
	int containing_paragraph;
	
	/**
	 * The page the line is contained in.
	 */
	int containing_page;
	
	/**
	 * The sub-chapter the line is contained in.
	 */
	int containing_sub_chapter;
	
	/**
	 * The main-chapter the line is contained in.
	 */
	int containing_main_chapter;
	
	/**
	 * Get the index of the paragraph the line is contained in.
	 * @return The index of the paragraph the line is contained in.
	 */
	public int getContaining_paragraph() {
		return containing_paragraph;
	}
	
	/**
	 * Set the index of the paragraph the line is contained in.
	 * @param containing_paragraph The index of the paragraph the line is contained 
	 * int.
	 */
	public void setContaining_paragraph(int containing_paragraph) {
		this.containing_paragraph = containing_paragraph;
	}
	
	/**
	 * Get the index of the page the line is contained in.
	 * @return The index of the page the line is contained in.
	 */
	public int getContaining_page() {
		return containing_page;
	}
	
	/**
	 * Set the index of the page the line is contained in.
	 * @param containing_page The index of the page the line is contained in.
	 */
	public void setContaining_page(int containing_page) {
		this.containing_page = containing_page;
	}
	
	/**
	 * Get the index of the sub-chapter the line is contained in.
	 * @return The index of the sub-chapter the line is contained in.
	 */
	public int getContaining_sub_chapter() {
		return containing_sub_chapter;
	}
	
	/**
	 * Set the index of the sub-chapter the line is contained in.
	 * @param containing_sub_chapter Set the index of the sub-chapter the line is 
	 * contained in.
	 */
	public void setContaining_sub_chapter(int containing_sub_chapter) {
		this.containing_sub_chapter = containing_sub_chapter;
	}
	
	/**
	 * Get the index of the main-chapter the line is contained in.
	 * @return The index of the main-chapter the line is contained in.
	 */
	public int getContaining_main_chapter() {
		return containing_main_chapter;
	}
	
	/**
	 * Set the index of the main-chapter the line is contained in.
	 * @param containing_main_chapter The index of the main-chapter the line is 
	 * contained in.
	 */
	public void setContaining_main_chapter(int containing_main_chapter) {
		this.containing_main_chapter = containing_main_chapter;
	}
}
