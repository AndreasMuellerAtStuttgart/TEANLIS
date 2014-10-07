package teanlis;

import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

/**
 * 
 * This class represents a document, the logically largest unit, the artefact containing all
 * linguistic elements like chapters, sentences, tokens. This is why only this class has 
 * meta_data, because it is the equivalent of document types like technical manuals, books and 
 * articles, which have meta-data, while chapters usually don't have metadata.
 * 
 * @author Andreas Müller
 */
public class Document extends Linguistic_Element {
	/**
	 * List for recording changes of instances of annotations in the document.
	 */
	ArrayList<String> instance_change_log;
	
	/**
	 * Get the list for recording changes of instances of annotations in the 
	 * document.
	 * @return The list for recording changes of instances of annotations in the 
	 * document.
	 */
	public ArrayList<String> getInstance_change_log() {
		return instance_change_log;
	}
	
	/**
	 * Set the list for recording changes of instances of annotations in the 
	 * document.
	 * @param instance_change_log
	 */
	public void setInstance_change_log(ArrayList<String> instance_change_log) {
		this.instance_change_log = instance_change_log;
	}
	
	/**
	 * List of the textual lines in the document.
	 */
	ArrayList<Line> lines;
	
	/**
	 * Get the list of textual lines in the document.
	 * @return
	 */
	public ArrayList<Line> getLines() {
		return lines;
	}
	
	/**
	 * Set the list of textual lines in the document.
	 * @param lines
	 */
	public void setLines(ArrayList<Line> lines) {
		this.lines = lines;
	}
	
	/**
	 * (?)
	 */
	HashSet<Integer> markup_sentences;
	
	/**
	 * A map from names of metadata fields to the values stored in those fields. All
	 * names and values are stored as strings.
	 */
	HashMap<String, String> meta_data;
	
	/**
	 * List of the tokens in the document.
	 */
	ArrayList<Token> tokens;
	
	/**
	 * List of the sentences in the document.
	 */
	ArrayList<Sentence> sentences;
	
	/**
	 * List of the paragraphs in the document.
	 */
	ArrayList<Paragraph> paragraphs;
	
	/**
	 * A map from type names to lists of annotations of the respective type. This 
	 * map is used for all non-standard (see Conceptual Documentation) annotations.
	 */
	HashMap<String, ArrayList<Annotation>> text_units;
	
	/**
	 * The text of the document.
	 */
	String original_text;
	
	/**
	 * The title of the document.
	 */
	String title;
	
	/**
	 * The list of sub-chapters in the document.
	 */
	ArrayList<Chapter> sub_chapters;
	
	/**
	 * The list of main-chapters in the document.
	 */
	ArrayList<Chapter> main_chapters;
	
	/**
	 * A map from character offsets to tokens. This map links annotations to the 
	 * linguistic and representational (see Conceptual Documentation) hierarchy. 
	 * (?)
	 */
	HashMap<Integer, Integer> offset_to_token_map = new HashMap<Integer, Integer>();
	
	/**
	 * Get the map from character offsets to tokens.
	 * @return
	 */
	public HashMap<Integer, Integer> getOffset_to_token_map() {
		return offset_to_token_map;
	}
	
	/**
	 * Set the map from character offsets to tokens. This method can, but should not
	 * be used directly. The method is used by the MakeOffsetToTokenMap method which
	 * computes the map.
	 * @param offset_to_token_map
	 */
	public void setOffset_to_token_map(HashMap<Integer, Integer> offset_to_token_map) {
		this.offset_to_token_map = offset_to_token_map;
	}
	
	/**
	 * Get the list of main-chapters in the document.
	 * @return
	 */
	public ArrayList<Chapter> getMain_chapters() {
		return main_chapters;
	}
	
	/**
	 * Set the list of main-chapters in the document.
	 * @param main_chapters
	 */
	public void setMain_chapters(ArrayList<Chapter> main_chapters) {
		this.main_chapters = main_chapters;
	}
	
	/**
	 * The list of pages in the document.
	 */
	ArrayList<Page> pages;

	/**
	 * Get the list of pages in the document.
	 * @return
	 */
	public ArrayList<Page> getPages() {
		return pages;
	}
	
	/**
	 * Set the list of pages in the document.
	 * @param pages
	 */
	public void setPages(ArrayList<Page> pages) {
		this.pages = pages;
	}
	
	/**
	 * Get the list of sub-chapters in the document.
	 * @return
	 */
	public ArrayList<Chapter> getSub_chapters() {
		return sub_chapters;
	}
	
	/**
	 * Set the list of sub-chapters in the document.
	 * @param sub_chapters
	 */
	public void setSub_chapters(ArrayList<Chapter> sub_chapters) {
		this.sub_chapters = sub_chapters;
	}
	
	/**
	 * Get the title of the document.
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the title of the document.
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the map which stores the metadata associated with the document.
	 * @return
	 */
	public HashMap<String, String> getMeta_data() {
		return meta_data;
	}
	
	/**
	 * Set the map which stores the metadata associated with the document.
	 * @param meta_data
	 */
	public void setMeta_data(HashMap<String, String> meta_data) {
		this.meta_data = meta_data;
	}
	
	/**
	 * Get the map from type names to the lists of non-standard annotations 
	 * contained in the document.
	 * @return
	 */
	public HashMap<String, ArrayList<Annotation>> getText_units() {
		return text_units;
	}
	
	/**
	 * Set the map from type names to the lists of non-standard annotations 
	 * contained in the document.
	 * @param text_units
	 */
	public void setText_units(HashMap<String, ArrayList<Annotation>> text_units) {
		this.text_units = text_units;
	}
	
	/**
	 * Get the list of tokens in the document.
	 * @return
	 */
	public ArrayList<Token> getTokens() {
		return tokens;
	}
	
	/**
	 * Set the list of tokens in the document.
	 * @param tokens
	 */
	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	/**
	 * Get the list of sentences in the documents.
	 * @return
	 */
	public ArrayList<Sentence> getSentences() {
		return sentences;
	}
	
	/**
	 * Set the list of sentences in the document.
	 * @param sentences
	 */
	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}
	
	/**
	 * Get the text of the document.
	 * @return
	 */
	public String getOriginal_text() {
		return original_text;
	}
	
	/**
	 * Set the text of the document.
	 * @param original_text
	 */
	public void setOriginal_text(String original_text) {
		this.original_text = original_text;
	}
	
	/**
	 * Constructor for the document which takes the text of the document as a 
	 * parameter.
	 * @param original_text
	 */
	public Document (String original_text) {

		this.original_text = original_text;
		this.text_units = new HashMap<String, ArrayList<Annotation>>();
		this.instance_change_log = new ArrayList<String>();
	}
	
	/**
	 * No-arguments constructor for the document.
	 */
	public Document () {
		this.text_units = new HashMap<String, ArrayList<Annotation>>();
		this.instance_change_log = new ArrayList<String>();
	}
	
	/**
	 * Get the list of paragraphs in the document.
	 * @return
	 */
	public ArrayList<Paragraph> getParagraphs() {
		return paragraphs;
	}
	
	/**
	 * Set the list of paragraphs in the document.
	 * @param paragraphs
	 */
	public void setParagraphs(ArrayList<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}
	
	/**
	 * Builds the linguistic and conceptual hierarchy. For example, for each 
	 * sentence the paragraph (or paragraphs in the case of a sentence which 
	 * overlaps more than one paragraph).
	 */
	public void BuildHierarchy () {
		
		int current_start = sentences.get(0).getStart();
		int current_end = sentences.get(0).getEnd();
		
		int current_index = 0;
		
		for (Token token: tokens) {
			if (token.getStart() <= current_end) {
				token.setContaining_sentence(current_index);
			} else if (token.getStart() > current_end) {
				
				if (current_index+1 < sentences.size()) {
				
					token.setContaining_sentence(current_index+1);
					current_start = sentences.get(current_index+1).getStart();
					current_end = sentences.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				} else {
					token.setContaining_sentence(current_index);
				}
			} 
		}
		
		/*current_start = paragraphs.get(0).getStart();
		current_end = paragraphs.get(0).getEnd();
		
		current_index = 0;
		
		for (Line line: lines) {
			if (line.getStart() >= current_start && line.getStart() <= current_end) {
				line.setContaining_paragraph(current_index);
			} else if (line.getStart() > current_end) {
				
				if (current_index+1 < paragraphs.size()) {
				
					line.setContaining_paragraph(current_index+1);
					current_start = paragraphs.get(current_index+1).getStart();
					current_end = paragraphs.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				}
			}
		}
		
		current_start = pages.get(0).getStart();
		current_end = pages.get(0).getEnd();
		
		current_index = 0;
		
		for (Line line: lines) {
			if (line.getStart() >= current_start && line.getStart() <= current_end) {
				line.setContaining_page(current_index);
			} else if (line.getStart() > current_end) {
				if (line.getStart() < pages.get(pages.size()-1).getEnd()) { 
					line.setContaining_page(current_index+1);
					current_start = pages.get(current_index+1).getStart();
					current_end = pages.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				}
			}
		} 
		
		/*current_start = sub_chapters.get(0).getStart();
		current_end = sub_chapters.get(0).getEnd();
		
		current_index = 0;
		
		for (Line line: lines) {
			if (line.getStart() >= current_start && line.getStart() <= current_end) {
				line.setContaining_sub_chapter(current_index);
			} else if (line.getStart() > current_end) {
				if (line.getStart() < sub_chapters.get(sub_chapters.size()-1).getEnd()) { 
					line.setContaining_sub_chapter(current_index+1);
					current_start = sub_chapters.get(current_index+1).getStart();
					current_end = sub_chapters.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				}
			}
		} */
		
		/*current_start = main_chapters.get(0).getStart();
		current_end = main_chapters.get(0).getEnd();
		
		current_index = 0;
		
		for (Line line: lines) {
			if (line.getStart() >= current_start && line.getStart() <= current_end) {
				line.setContaining_main_chapter(current_index);
			} else if (line.getStart() > current_end) {
				if (line.getStart() < main_chapters.get(main_chapters.size()-1).getEnd()) { 
					line.setContaining_main_chapter(current_index+1);
					current_start = main_chapters.get(current_index+1).getStart();
					current_end = main_chapters.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				}
			}
		} */
		
		/*current_start = paragraphs.get(0).getStart();
		current_end = paragraphs.get(0).getEnd();
		
		current_index = 0;
		
		for (Sentence sentence: sentences) {
			if (sentence.getStart() >= current_start && sentence.getStart() <= current_end) {
				sentence.setContaining_paragraph(current_index);
			} else if (sentence.getStart() > current_end) {
				
				if (current_index+1 < paragraphs.size()) {
				
					sentence.setContaining_paragraph(current_index+1);
					current_start = paragraphs.get(current_index+1).getStart();
					current_end = paragraphs.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				}
			}
		}
		
		if (!pages.isEmpty()) {
		
		current_start = pages.get(0).getStart();
		current_end = pages.get(0).getEnd();
		
		// Check out
		
		current_index = 0;
		
		for (Paragraph paragraph: paragraphs) {
			if (paragraph.getStart() >= current_start && paragraph.getEnd() <= current_end) {
				paragraph.setContaining_page(current_index);
			} else if (paragraph.getStart() > current_end) {
				
				if (current_index < this.getPages().size()-1) {
				
					paragraph.setContaining_page(current_index+1);
					current_start = pages.get(current_index+1).getStart();
					current_end = pages.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				} else {
					paragraph.setContaining_page(-1);
				}
			}
		} 
		
		}
		
		current_start = main_chapters.get(0).getStart();
		current_end = main_chapters.get(0).getEnd();
		
		current_index = 0;
		
		for (Page page: pages) {
			if (page.getStart() >= current_start && page.getStart() <= current_end) {
				page.setContaining_chapter(current_index);
			} else if (page.getStart() > current_end) {
				
				if (current_index+1 < main_chapters.size()) {
				
					page.setContaining_chapter(current_index+1);
					current_start = main_chapters.get(current_index+1).getStart();
					current_end = main_chapters.get(current_index+1).getEnd();
				
					current_index = current_index+1;
				}
			}
		} */
	}
	
	/**
	 * Compute the mapping from offsets to tokens which links arbitrary annotations 
	 * to the linguistic and conceptual hierarchy.
	 */
	public void MakeOffsetToTokenMap () {
		offset_to_token_map = new HashMap<Integer, Integer>();
		
		for (int i=0;i<tokens.get(0).getStart();i++) {
			offset_to_token_map.put(i, 0);
		}
		
		for (int i=0;i<tokens.size();i++) {
			Token token = tokens.get(i);
			
			for (int j=token.getStart();j<=token.getEnd();j++) {
				offset_to_token_map.put(new Integer(j),new Integer(i));
			}
			/*if (i < tokens.size()-1) {
				Token next_token = tokens.get(i+1);
				for (int z=token.getEnd()+1;z<next_token.getStart();z++) {
					offset_to_token_map.put(new Integer(z), new Integer(i));
				}
			} */
		}
		
		for (int i=tokens.get(tokens.size()-1).getEnd()+1;
				i<=original_text.length();i++) {
			offset_to_token_map.put(i, tokens.size()-1);
		}
		
		/*for (int i=tokens.get(tokens.size()-1).getEnd()+1;i<=original_text.length();i++) {
			offset_to_token_map.put(i, tokens.size()-1);
		} */
	}
	
	/**
	 * Save the document as one object at the specified path.
	 * @param path
	 */
	public void SaveAsOneObject (String path) {
		
		try {
		
		FileOutputStream object_output_stream = new FileOutputStream(path);
	    ObjectOutput object_output = new ObjectOutputStream(object_output_stream);
	    object_output.writeObject(this);
	    
	    object_output_stream.close();
	    object_output.close();
				
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Load the document from an object stored at the specified path.
	 * @param path
	 */
	public void LoadAsOneObject (String path) {
		
		try {
		
		FileInputStream object_in_stream = new FileInputStream(path);
	    ObjectInputStream object_input = new ObjectInputStream(object_in_stream);
	    Document loaded_document = (Document) object_input.readObject();
	   
	    this.lines = loaded_document.getLines();
	    this.main_chapters = loaded_document.getMain_chapters();
	    this.meta_data = loaded_document.getMeta_data();
	    this.note_history = loaded_document.getNote_history();
	    this.offset_to_token_map = loaded_document.getOffset_to_token_map();
	    this.original_text = loaded_document.getOriginal_text();
	    this.pages = loaded_document.getPages();
	    this.paragraphs = loaded_document.getParagraphs();
	    this.sentences = loaded_document.getSentences();
	    this.sub_chapters = loaded_document.getSub_chapters();
	    this.text_units = loaded_document.getText_units();
	    this.title = loaded_document.getTitle();
	    this.tokens = loaded_document.getTokens();
	    
	    object_in_stream.close();
	    object_input.close();
	    
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (java.lang.ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}