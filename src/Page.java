package epoetics.core;

/**
 * This class represents a page. The field number contains the number assigned
 * to the page by the author, not necessarily the number you would get when
 * counting the pages from the first to the last. The field containing_chapter
 * contains the index of the chapter the page is contained in. The index is the
 * index in the chapter-list of the document the page is contained in.
 * 
 * @author Andreas Müller
 */

public class Page extends Linguistic_Element {
	
	/**
	 * The number of the page. This is the number printed on the page, not the index
	 * of the page in the list of pages contained in the document.
	 */
	int number;
	
	/**
	 * The chapter the page is contained in.
	 */
	int containing_chapter;
	
	/**
	 * Get the chapter the page is contained in.
	 * @return
	 */
	public int getContaining_chapter() {
		return containing_chapter;
	}

	/**
	 * Set the chapter the page is contained in.
	 * @param containing_sub_chapter
	 */
	public void setContaining_chapter(int containing_sub_chapter) {
		this.containing_chapter = containing_sub_chapter;
	}
	
	/**
	 * Get the number of the page.
	 * @return
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Set the number of the page.
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
}
