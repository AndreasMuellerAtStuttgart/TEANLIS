package teanlis;

/**
 * This is a class containing utility functions.
 * 
 * @author Andreas Müller
 */

import gate.Factory;
import gate.Gate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Utility functions for common tasks and convenience methods for tasks which can be
 * implemented with a few lines of code.
 * 
 * @author Andreas Müller
 *
 */
public class Utils {
	
	public static Document LoadFromPlainTextFormat (String path_to_directory) {
		Document document = new Document();
		
		try {
			File directory = new File(path_to_directory);
			
			File[] files_in_directory = directory.listFiles();
			
			ArrayList<Token> token_list = new ArrayList<Token>();
			
			for (int i=0;i<files_in_directory.length;i++) {
				File file = files_in_directory[i];
				
				if (file.getName().equals("tokens")) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					
					String line = "";
					
					while (line != null) {
						line = reader.readLine();
						
						String[] token_properties = line.split(",");
						
						Token token = new Token();
						
						token.setStart(new Integer(token_properties[0]));
						token.setEnd(new Integer(token_properties[1]));
						
						token.setLemma(token_properties[2]);
						token.setFunction(token_properties[3]);
						token.setPos(token_properties[4]);
						token.setDependency(token_properties[5]);
						token.setGovernor(new Integer(token_properties[6]));
						
						token_list.add(token);
					}
				}
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		
		return document;
	}
	
	public static void SaveInPlainTextFormat (String path_to_directory, 
			Document document) {
		try {
			BufferedWriter token_writer = new BufferedWriter(new FileWriter(
					path_to_directory+"\\tokens"));
			
			for (Token token: document.getTokens()) {
				token_writer.write(token.getStart()+","+token.getEnd()+","
						+token.getLemma()+","+token.getFunction()+","+
							token.getPos()+","+token.getDependency()+","
								+token.getGovernor()+"\n");
			}
			
			token_writer.close();
			
			BufferedWriter sentence_writer = new BufferedWriter(new FileWriter(
					path_to_directory+"\\sentences"));
			
			for (Sentence sentence: document.getSentences()) {
				sentence_writer.write(sentence.getStart()+","+sentence.getEnd()+
						","+sentence.getContaining_paragraph()+","
							+sentence.getContaining_chapter()+"\n");
			}
			
			sentence_writer.close();
			
			BufferedWriter paragraph_writer = new BufferedWriter(new FileWriter(
					path_to_directory+"\\paragraphs"));
			
			for (Paragraph paragraph: document.getParagraphs()) {
				paragraph_writer.write(paragraph.getStart()+","+paragraph.getEnd()+
						","+paragraph.getContaining_page()+","
							+paragraph.getContaining_chapter()+"\n");
			}
			
			paragraph_writer.close();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a document which is stored as an xml-file in GATE-format. The document 
	 * is loaded as a GATE document and converted to a TEANLIS document. A TEANLIS 
	 * document is returned.
	 * @param path path to the xml-document which is loaded and converted to a 
	 * TEANLIS document.
	 * @return TEANLIS document.
	 */
	public static Document LoadDocumentFromGateDocument (String path) {
		try {
			Gate.init();
			
			File document_url = new File(path);
			
			gate.Document gate_document_to_load = Factory.newDocument(
					document_url.toURI().toURL());
			
			Document internal_document = GATE_Converter.convert_to_document(
					gate_document_to_load);
			
			return internal_document;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		Document document = MakeDocument.MakeDocumentFromPlainTextFile("C:\\Users\\"
				+ "Andreas\\Documents\\plain_text_staiger_structure", "German");
		
		document.MakeOffsetToTokenMap();
		
		/*SaveInPlainTextFormat("C:\\Users\\Andreas\\Documents\\ePoetics\\"
				+ "Staiger_directory", document); */
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"C:\\Users\\Andreas\\Documents\\GermanPolarityClues-2012\\"
					+ "GermanPolarityClues-Negative-21042012.tsv"));
			
			String line = reader.readLine();
			
			HashSet<String> sentiment_words = new HashSet<String>();
			
			while (line != null) {
				sentiment_words.add(line.split("	")[0]);
				
				line = reader.readLine();
			}
			
			reader.close();
			
			reader = new BufferedReader(new FileReader(
					"C:\\Users\\Andreas\\Documents\\GermanPolarityClues-2012\\"
					+ "GermanPolarityClues-Negative-21042012.tsv"));
			
			line = reader.readLine();
			
			while (line != null) {
				sentiment_words.add(line.split("	")[0]);
				
				line = reader.readLine();
			}
			
			HashSet<String> sentiment_words_in_document = new HashSet<String>();
			
			int sentence_counter = 0;
			
			ArrayList<Annotation> sentiment_annotated_sentences = 
					new ArrayList<Annotation>();
			
			for (Sentence sentence: document.getSentences()) {
				int start = document.getOffset_to_token_map().get(
						sentence.getStart());
				int end = document.getOffset_to_token_map().get(sentence.getEnd());
				
				int counter = 0;
				
				for (int i=start;i<=end;i++) {
					Token token = document.getTokens().get(i);
					
					if (sentiment_words.contains(token.getString(
							document.getOriginal_text()))) {
						sentiment_words_in_document.add(token.getString(
								document.getOriginal_text()));
						
						counter = counter+1;
					}
				}
				
				Annotation sentiment_annotated_sentence = new Annotation();
				
				sentiment_annotated_sentence.setStart(sentence.getStart());
				sentiment_annotated_sentence.setEnd(sentence.getEnd());
				
				sentiment_annotated_sentence.setPropability((double) counter / 
						(double) (end-start));
				
				sentiment_annotated_sentences.add(sentiment_annotated_sentence);
			}
			
			document.getText_units().put("sentiment_annotated_sentences", 
					sentiment_annotated_sentences);
			
			gate.Document gate_document = GATE_Converter.convert_to_gate_document(
					document);
			
			String gate_document_xml = gate_document.toXml();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"C:\\Users\\Andreas\\Documents\\ePoetics\\"
					+ "Staiger_with_simple_annotations.xml"));
			
			writer.write(gate_document_xml);
			
			writer.close();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}		
	}
}
