package teanlis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * This class contains methods for constructing a document and attaching
 * linguistic units to it. Documents can either be constructed from plain
 * text or from a given document in TEI conform xml format.
 * 
 * @author Andreas Müller
 */

public class MakeDocument {
	public static Document AttachLemmaMorphPosAndDependencies(Document document, 
			String path_to_lemmatizer_model, String path_to_morph_tagger, 
				String path_to_pos_tagger_model, String path_to_parser_model) {
		is2.lemmatizer.Lemmatizer lemmatizer = new is2.lemmatizer.Lemmatizer(
				path_to_lemmatizer_model);
		is2.mtag.Tagger morph_tagger = new is2.mtag.Tagger(path_to_morph_tagger);
		is2.tag.Tagger pos_tagger = new is2.tag.Tagger(path_to_pos_tagger_model);
		is2.parser.Parser parser = new is2.parser.Parser(path_to_parser_model);
		
		document.MakeOffsetToTokenMap();
		
		String plain_text = document.getOriginal_text();
		
		int counter = 0;
		
		ArrayList<Token> token_list = new ArrayList<Token>();
		
		try {
		for (Sentence sentence: document.getSentences()) {
			is2.data.SentenceData09 sentence_data = new is2.data.SentenceData09();
			
			int start = document.getOffset_to_token_map().get(sentence.getStart());
			int end = document.getOffset_to_token_map().get(sentence.getEnd());
					
			String[] token_strings = new String[(end-start)+1];
			
			for (int i=start;i<=end;i++) {
				token_strings[i-start] = document.getTokens().get(i).getString(
						document.getOriginal_text());
			}
			
			sentence_data.init(token_strings);
			
			sentence_data = lemmatizer.apply(sentence_data);
			
			sentence_data = morph_tagger.apply(sentence_data);
			
			sentence_data = pos_tagger.apply(sentence_data);
			
			//sentence_data = parser.apply(sentence_data);
			
			for (int i=0;i<token_strings.length;i++) {
				Token token = document.getTokens().get(start+i);
				
				token.setLemma(sentence_data.plemmas[i]);
				token.setPos(sentence_data.ppos[i]);
				token.setFunction(sentence_data.pfeats[i]);
				token.setGovernor(start+sentence_data.pheads[i]);
				token.setDependency(sentence_data.plabels[i]);
			}
			
			counter = counter+1;
			
			System.out.println(counter);
			
		} 
		
		return document;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document AttachSentencesAndTokens (
			Document document) {
		try {
			File sentence_detector_model_file = new File(
					"C:\\Users\\Andreas\\Downloads\\de-sent.bin");
			
			File tokenizer_model_file = new File(
					"C:\\Users\\Andreas\\Downloads\\de-token.bin");
			
			String text = document.getOriginal_text();
			
			SentenceModel sentence_detector_model = new SentenceModel(
					sentence_detector_model_file);
		
			SentenceDetectorME sentence_splitter = new SentenceDetectorME(
					sentence_detector_model);
			
			TokenizerModel tokenizer_model = new TokenizerModel(
					tokenizer_model_file);
			
			TokenizerME tokenizer = new TokenizerME(tokenizer_model);
			
			String[] sentence_strings = sentence_splitter.sentDetect(text);
			
			int current_offset = 0;
			
			String original_text = text;
			
			ArrayList<Sentence> sentences = new ArrayList<Sentence>();
			
			ArrayList<Token> tokens = new ArrayList<Token>();
			
			for (int i=0;i<sentence_strings.length;i++) {
				Sentence sentence = new Sentence();
				
				int index = original_text.indexOf(sentence_strings[i]);
				
				sentence.setStart(current_offset+index);
				current_offset = current_offset+index+sentence_strings[i].length();
				sentence.setEnd(current_offset);
				
				sentences.add(sentence);
				
				original_text = document.getOriginal_text().substring(
						current_offset);
				
				String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
				
				int current_token_offset = sentence.getStart();
				
				for (int j=0;j<token_strings.length;j++) {
					Token token = new Token();
					
					int token_index = document.getOriginal_text().substring(
							current_token_offset).indexOf(token_strings[j]);
					
					token.setStart(token_index+current_token_offset);
					current_token_offset = current_token_offset+token_index+
							token_strings[j].length();
					token.setEnd(current_token_offset);
					
					tokens.add(token);
				}
				
				System.out.println(i);
			}
			
			document.setSentences(sentences);
			document.setTokens(tokens);
			
			return document;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document TEILoader (String file_path) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file_path));
			
			String text = "";
			
			ArrayList<String> lines = new ArrayList<String>();
			ArrayList<String> line_identifiers = new ArrayList<String>();
			
			ArrayList<Integer> paragraph_start_offsets = new ArrayList<Integer>();
			ArrayList<Integer> paragraph_end_offsets = new ArrayList<Integer>();
			
			ArrayList<Integer> chapter_level_1_start_offsets = 
					new ArrayList<Integer>();
			ArrayList<Integer> chapter_level_1_end_offsets = 
					new ArrayList<Integer>();
			
			ArrayList<Integer> chapter_level_2_start_offsets = 
					new ArrayList<Integer>();
			ArrayList<Integer> chapter_level_2_end_offsets = 
					new ArrayList<Integer>();
			
			ArrayList<Integer> chapter_level_3_start_offsets = 
					new ArrayList<Integer>();
			ArrayList<Integer> chapter_level_3_end_offsets = 
					new ArrayList<Integer>();
			
			ArrayList<Integer> chapter_level_4_start_offsets = 
					new ArrayList<Integer>();
			ArrayList<Integer> chapter_level_4_end_offsets = 
					new ArrayList<Integer>();
			
			ArrayList<Integer> page_offsets = new ArrayList<Integer>();
			
			String text_line = reader.readLine();
			
			Pattern line_pattern = Pattern.compile("<lb n=\"(.*?)\"");
			
			int counter = 0;
			
			boolean paragraph_start = false;
			
			ArrayList<Line> line_list = new ArrayList<Line>();
			ArrayList<Paragraph> paragraph_list = new ArrayList<Paragraph>();
			ArrayList<Chapter> chapter_level_1_list = new ArrayList<Chapter>();
			
			ArrayList<Chapter> chapter_level_2_list = new ArrayList<Chapter>();
			ArrayList<Chapter> chapter_level_3_list = new ArrayList<Chapter>();
			ArrayList<Chapter> chapter_level_4_list = new ArrayList<Chapter>();
			
			ArrayList<Page> page_list = new ArrayList<Page>();
			
			boolean paragraph_active = false;
			boolean paragraph_inactive = false;
			
			boolean header_found = false;
			
			while (text_line != null) {
				if (text_line.indexOf("</teiHeader>") != -1) {
					header_found = true;
					text_line = reader.readLine();
					continue;
				}
				
				if (!header_found) {
					text_line = reader.readLine();
					continue;
				}
				
				Line line = new Line();
				
				line.setStart(text.length());
				
				text = text+text_line.replaceAll("<.*?>", "")+"\n";
				
				line.setEnd(text.length());
				
				line_list.add(line);
				
				if (text_line.startsWith("<lb ")) {
					Matcher line_matcher = line_pattern.matcher(text_line);
					lines.add(text_line.replaceAll("<.*?>", "")+"\n");
					
					if (line_matcher.find()) {
						line_identifiers.add(line_matcher.group(1));
					}
				}
				
				if (text_line.startsWith("<p")) {
					paragraph_start_offsets.add(text.length());
					paragraph_active = true;
					
					if (!paragraph_inactive) {
						System.out.println(line.getString(text));
					}
					
					paragraph_inactive = false;
				} else if (text_line.startsWith("</p>")) {
					paragraph_end_offsets.add(text.length());
					paragraph_inactive = true;
					
					if (!paragraph_active) {
						System.out.println(line.getString(text));
					}
					
					paragraph_active = false;
				}
				
				if (text_line.startsWith("<div1")) {
					chapter_level_1_start_offsets.add(text.length());
				} else if (text_line.startsWith("</div1>")) {
					chapter_level_1_end_offsets.add(text.length());
				}
				
				if (text_line.startsWith("<div2")) {
					chapter_level_2_start_offsets.add(text.length());
				} else if (text_line.startsWith("</div2>")) {
					chapter_level_2_end_offsets.add(text.length());
				}
				
				if (text_line.startsWith("<div3")) {
					chapter_level_3_start_offsets.add(text.length());
				} else if (text_line.startsWith("</div3>")) {
					chapter_level_3_end_offsets.add(text.length());
				}
				
				if (text_line.startsWith("<div4")) {
					chapter_level_4_start_offsets.add(text.length());
				} else if (text_line.startsWith("</div4>")) {
					chapter_level_4_end_offsets.add(text.length());
				}
				
				if (text_line.startsWith("<pb")) {
					page_offsets.add(text.length());
				}
				
				text_line = reader.readLine();
			}
			
			reader.close();
			
			Document document = new Document();
			
			document.setOriginal_text(text);
			
			document.setLines(line_list);
			
			for (int i=0;i<paragraph_start_offsets.size();i++) {
				Paragraph paragraph = new Paragraph();
				
				paragraph.setStart(paragraph_start_offsets.get(i));
				paragraph.setEnd(paragraph_end_offsets.get(i));
				
				paragraph_list.add(paragraph);
			}
			
			for (int i=0;i<chapter_level_1_start_offsets.size();i++) {
				Chapter chapter = new Chapter();
				
				chapter.setStart(chapter_level_1_start_offsets.get(i));
				chapter.setEnd(chapter_level_1_end_offsets.get(i));
				
				chapter_level_1_list.add(chapter);
			}
			
			for (int i=0;i<chapter_level_2_start_offsets.size();i++) {
				Chapter chapter = new Chapter();
				
				chapter.setStart(chapter_level_2_start_offsets.get(i));
				chapter.setEnd(chapter_level_2_end_offsets.get(i));
				
				chapter_level_2_list.add(chapter);
			}
			
			for (int i=0;i<chapter_level_3_start_offsets.size();i++) {
				Chapter chapter = new Chapter();
				
				chapter.setStart(chapter_level_3_start_offsets.get(i));
				chapter.setEnd(chapter_level_3_end_offsets.get(i));
				
				chapter_level_3_list.add(chapter);
			}
			
			for (int i=0;i<chapter_level_4_start_offsets.size();i++) {
				Chapter chapter = new Chapter();
				
				chapter.setStart(chapter_level_4_start_offsets.get(i));
				chapter.setEnd(chapter_level_4_end_offsets.get(i));
				
				chapter_level_4_list.add(chapter);
			}
			
			int current_page_offset = 0;
			
			for (Integer page_offset: page_offsets) {
				Page page = new Page();
				
				page.setStart(current_page_offset);
				current_page_offset = page_offset;
				page.setEnd(current_page_offset);
				
				page_list.add(page);
			}
			
			document.setParagraphs(paragraph_list);
			
			document.setMain_chapters(chapter_level_1_list);
			document.setSub_chapters(chapter_level_2_list);
			
			document.setPages(page_list);
			
			return document;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document MakeDocumentFromPlainTextFile (String file_path, String language) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file_path));
			
			String line = reader.readLine();
			
			String text = "";
			
			while (line != null) {
				text = text+line+"\n";
				
				line = reader.readLine();
			}
			
			Document document = new Document();
			
			document.setOriginal_text(text);
			
			if (language.equals("unknown")) {
				return document;
			} else if (language.equals("English")) {
				File sentence_detector_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\en-sent.bin");
				
				File tokenizer_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\en-token.bin");
				
				File pos_tag_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\en-pos-maxent.bin");
				
				SentenceModel sentence_detector_model = new SentenceModel(
						sentence_detector_model_file);
			
				SentenceDetectorME sentence_splitter = new SentenceDetectorME(
						sentence_detector_model);
				
				POSModel pos_tag_model = new POSModel(pos_tag_model_file);
				
				TokenizerModel tokenizer_model = new TokenizerModel(tokenizer_model_file);
				
				TokenizerME tokenizer = new TokenizerME(tokenizer_model);
				
				POSTaggerME pos_tagger = new POSTaggerME(pos_tag_model);
				
				String[] sentence_strings = sentence_splitter.sentDetect(text);
				
				int current_offset = 0;
				
				String original_text = text;
				
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				
				ArrayList<Token> tokens = new ArrayList<Token>();
				
				for (int i=0;i<sentence_strings.length;i++) {
					Sentence sentence = new Sentence();
					
					int index = original_text.indexOf(sentence_strings[i]);
					
					sentence.setStart(current_offset+index);
					current_offset = current_offset+index+sentence_strings[i].length();
					sentence.setEnd(current_offset);
					
					sentences.add(sentence);
					
					original_text = document.getOriginal_text().substring(current_offset);
					
					String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
					String[] pos_tag_strings = pos_tagger.tag(token_strings);
					
					int current_token_offset = sentence.getStart();
					
					for (int j=0;j<token_strings.length;j++) {
						Token token = new Token();
						
						token.setPos(pos_tag_strings[j]);
						
						int token_index = document.getOriginal_text().substring(
								current_token_offset).indexOf(token_strings[j]);
						
						token.setStart(token_index+current_token_offset);
						current_token_offset = current_token_offset+token_index+
								token_strings[j].length();
						token.setEnd(current_token_offset);
						
						tokens.add(token);
					}
				}
				
				document.setSentences(sentences);
				document.setTokens(tokens);
				
				return document;
			} else if (language.equals("German")) {
				File sentence_detector_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\de-sent.bin");
				
				File tokenizer_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\de-token.bin");
				
				SentenceModel sentence_detector_model = new SentenceModel(
						sentence_detector_model_file);
			
				SentenceDetectorME sentence_splitter = new SentenceDetectorME(
						sentence_detector_model);
				
				TokenizerModel tokenizer_model = new TokenizerModel(tokenizer_model_file);
				
				TokenizerME tokenizer = new TokenizerME(tokenizer_model);
				
				String[] sentence_strings = sentence_splitter.sentDetect(text);
				
				int current_offset = 0;
				
				String original_text = text;
				
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				
				ArrayList<Token> tokens = new ArrayList<Token>();
				
				for (int i=0;i<sentence_strings.length;i++) {
					Sentence sentence = new Sentence();
					
					int index = original_text.indexOf(sentence_strings[i]);
					
					sentence.setStart(current_offset+index);
					current_offset = current_offset+index+sentence_strings[i].length();
					sentence.setEnd(current_offset);
					
					sentences.add(sentence);
					
					original_text = document.getOriginal_text().substring(current_offset);
					
					String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
					
					int current_token_offset = sentence.getStart();
					
					for (int j=0;j<token_strings.length;j++) {
						Token token = new Token();
						
						int token_index = document.getOriginal_text().substring(
								current_token_offset).indexOf(token_strings[j]);
						
						token.setStart(token_index+current_token_offset);
						current_token_offset = current_token_offset+token_index+
								token_strings[j].length();
						token.setEnd(current_token_offset);
						
						tokens.add(token);
					}
				}
				
				document.setSentences(sentences);
				document.setTokens(tokens);
				
				return document;
			} else if (language.equals("Danish")) {
				File sentence_detector_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\da-sent.bin");
				
				File tokenizer_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\da-token.bin");
				
				SentenceModel sentence_detector_model = new SentenceModel(
						sentence_detector_model_file);
			
				SentenceDetectorME sentence_splitter = new SentenceDetectorME(
						sentence_detector_model);
				
				TokenizerModel tokenizer_model = new TokenizerModel(tokenizer_model_file);
				
				TokenizerME tokenizer = new TokenizerME(tokenizer_model);
				
				String[] sentence_strings = sentence_splitter.sentDetect(text);
				
				int current_offset = 0;
				
				String original_text = text;
				
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				
				ArrayList<Token> tokens = new ArrayList<Token>();
				
				for (int i=0;i<sentence_strings.length;i++) {
					Sentence sentence = new Sentence();
					
					int index = original_text.indexOf(sentence_strings[i]);
					
					sentence.setStart(current_offset+index);
					current_offset = current_offset+index+sentence_strings[i].length();
					sentence.setEnd(current_offset);
					
					sentences.add(sentence);
					
					original_text = document.getOriginal_text().substring(current_offset);
					
					String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
					
					int current_token_offset = sentence.getStart();
					
					for (int j=0;j<token_strings.length;j++) {
						Token token = new Token();
						
						int token_index = document.getOriginal_text().substring(
								current_token_offset).indexOf(token_strings[j]);
						
						token.setStart(token_index+current_token_offset);
						current_token_offset = current_token_offset+token_index+
								token_strings[j].length();
						token.setEnd(current_token_offset);
						
						tokens.add(token);
					}
				}
				
				document.setSentences(sentences);
				document.setTokens(tokens);
				
				return document;
			} else if (language.equals("Spanish")) {
				File sentence_detector_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\es-sent.bin");
				
				File tokenizer_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\es-token.bin");
				
				SentenceModel sentence_detector_model = new SentenceModel(
						sentence_detector_model_file);
			
				SentenceDetectorME sentence_splitter = new SentenceDetectorME(
						sentence_detector_model);
				
				TokenizerModel tokenizer_model = new TokenizerModel(tokenizer_model_file);
				
				TokenizerME tokenizer = new TokenizerME(tokenizer_model);
				
				String[] sentence_strings = sentence_splitter.sentDetect(text);
				
				int current_offset = 0;
				
				String original_text = text;
				
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				
				ArrayList<Token> tokens = new ArrayList<Token>();
				
				for (int i=0;i<sentence_strings.length;i++) {
					Sentence sentence = new Sentence();
					
					int index = original_text.indexOf(sentence_strings[i]);
					
					sentence.setStart(current_offset+index);
					current_offset = current_offset+index+sentence_strings[i].length();
					sentence.setEnd(current_offset);
					
					sentences.add(sentence);
					
					original_text = document.getOriginal_text().substring(current_offset);
					
					String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
					
					int current_token_offset = sentence.getStart();
					
					for (int j=0;j<token_strings.length;j++) {
						Token token = new Token();
						
						int token_index = document.getOriginal_text().substring(
								current_token_offset).indexOf(token_strings[j]);
						
						token.setStart(token_index+current_token_offset);
						current_token_offset = current_token_offset+token_index+
								token_strings[j].length();
						token.setEnd(current_token_offset);
						
						tokens.add(token);
					}
				}
				
				document.setSentences(sentences);
				document.setTokens(tokens);
				
				return document;
			} else if (language.equals("Dutch")) {
				File sentence_detector_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\nl-sent.bin");
				
				File tokenizer_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\nl-token.bin");
				
				SentenceModel sentence_detector_model = new SentenceModel(
						sentence_detector_model_file);
			
				SentenceDetectorME sentence_splitter = new SentenceDetectorME(
						sentence_detector_model);
				
				TokenizerModel tokenizer_model = new TokenizerModel(tokenizer_model_file);
				
				TokenizerME tokenizer = new TokenizerME(tokenizer_model);
				
				String[] sentence_strings = sentence_splitter.sentDetect(text);
				
				int current_offset = 0;
				
				String original_text = text;
				
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				
				ArrayList<Token> tokens = new ArrayList<Token>();
				
				for (int i=0;i<sentence_strings.length;i++) {
					Sentence sentence = new Sentence();
					
					int index = original_text.indexOf(sentence_strings[i]);
					
					sentence.setStart(current_offset+index);
					current_offset = current_offset+index+sentence_strings[i].length();
					sentence.setEnd(current_offset);
					
					sentences.add(sentence);
					
					original_text = document.getOriginal_text().substring(current_offset);
					
					String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
					
					int current_token_offset = sentence.getStart();
					
					for (int j=0;j<token_strings.length;j++) {
						Token token = new Token();
						
						int token_index = document.getOriginal_text().substring(
								current_token_offset).indexOf(token_strings[j]);
						
						token.setStart(token_index+current_token_offset);
						current_token_offset = current_token_offset+token_index+
								token_strings[j].length();
						token.setEnd(current_token_offset);
						
						tokens.add(token);
					}
				}
				
				document.setSentences(sentences);
				document.setTokens(tokens);
				
				return document;
			} else if (language.equals("Protugese")) {
				File sentence_detector_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\pt-sent.bin");
				
				File tokenizer_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\pt-token.bin");
				
				SentenceModel sentence_detector_model = new SentenceModel(
						sentence_detector_model_file);
			
				SentenceDetectorME sentence_splitter = new SentenceDetectorME(
						sentence_detector_model);
				
				TokenizerModel tokenizer_model = new TokenizerModel(tokenizer_model_file);
				
				TokenizerME tokenizer = new TokenizerME(tokenizer_model);
				
				String[] sentence_strings = sentence_splitter.sentDetect(text);
				
				int current_offset = 0;
				
				String original_text = text;
				
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				
				ArrayList<Token> tokens = new ArrayList<Token>();
				
				for (int i=0;i<sentence_strings.length;i++) {
					Sentence sentence = new Sentence();
					
					int index = original_text.indexOf(sentence_strings[i]);
					
					sentence.setStart(current_offset+index);
					current_offset = current_offset+index+sentence_strings[i].length();
					sentence.setEnd(current_offset);
					
					sentences.add(sentence);
					
					original_text = document.getOriginal_text().substring(current_offset);
					
					String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
					
					int current_token_offset = sentence.getStart();
					
					for (int j=0;j<token_strings.length;j++) {
						Token token = new Token();
						
						int token_index = document.getOriginal_text().substring(
								current_token_offset).indexOf(token_strings[j]);
						
						token.setStart(token_index+current_token_offset);
						current_token_offset = current_token_offset+token_index+
								token_strings[j].length();
						token.setEnd(current_token_offset);
						
						tokens.add(token);
					}
				}
				
				document.setSentences(sentences);
				document.setTokens(tokens);
				
				return document;
			} else if (language.equals("Swedish")) {
				File sentence_detector_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\se-sent.bin");
				
				File tokenizer_model_file = new File(
						"C:\\Users\\Andreas\\Downloads\\se-token.bin");
				
				SentenceModel sentence_detector_model = new SentenceModel(
						sentence_detector_model_file);
			
				SentenceDetectorME sentence_splitter = new SentenceDetectorME(
						sentence_detector_model);
				
				TokenizerModel tokenizer_model = new TokenizerModel(tokenizer_model_file);
				
				TokenizerME tokenizer = new TokenizerME(tokenizer_model);
				
				String[] sentence_strings = sentence_splitter.sentDetect(text);
				
				int current_offset = 0;
				
				String original_text = text;
				
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				
				ArrayList<Token> tokens = new ArrayList<Token>();
				
				for (int i=0;i<sentence_strings.length;i++) {
					Sentence sentence = new Sentence();
					
					int index = original_text.indexOf(sentence_strings[i]);
					
					sentence.setStart(current_offset+index);
					current_offset = current_offset+index+sentence_strings[i].length();
					sentence.setEnd(current_offset);
					
					sentences.add(sentence);
					
					original_text = document.getOriginal_text().substring(current_offset);
					
					String[] token_strings = tokenizer.tokenize(sentence_strings[i]);
					
					int current_token_offset = sentence.getStart();
					
					for (int j=0;j<token_strings.length;j++) {
						Token token = new Token();
						
						int token_index = document.getOriginal_text().substring(
								current_token_offset).indexOf(token_strings[j]);
						
						token.setStart(token_index+current_token_offset);
						current_token_offset = current_token_offset+token_index+
								token_strings[j].length();
						token.setEnd(current_token_offset);
						
						tokens.add(token);
					}
				}
				
				document.setSentences(sentences);
				document.setTokens(tokens);
				
				return document;
			} else {
				return document;
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		Document document = Utils.LoadDocumentFromGateDocument("C:\\"
				+ "Users\\Andreas\\Documents\\ePoetics\\Vom_Erhabenen.xml");
		
		//document = AttachSentencesAndTokens(document);
		
		try {
			document = AttachLemmaMorphPosAndDependencies(document, 
				"C:\\Users\\Andreas\\Documents\\ePoetics\\models\\"
				+ "lemma-ger-3.6.model", "C:\\Users\\Andreas\\Documents\\ePoetics\\"
					+ "models\\morphology-ger-3.6.model", "C:\\Users\\Andreas\\"
							+ "Documents\\ePoetics\\models\\tag-ger-3.6.model", 
								"C:\\Users\\Andreas\\Documents\\ePoetics\\models\\"
										+ "parser-ger-3.6.model");
			
			gate.Document gate_document = GATE_Converter.convert_to_gate_document(
					document);
			
			String gate_document_xml = gate_document.toXml();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"C:\\Users\\Andreas\\Documents\\ePoetics\\"
					+ "Vom_Erhabenen.xml"));
			
			writer.write(gate_document_xml);
			
			writer.close();
		
		System.out.println(document.getOriginal_text());
	} catch (java.lang.Exception e) {
		e.printStackTrace();
	}
	}
}
