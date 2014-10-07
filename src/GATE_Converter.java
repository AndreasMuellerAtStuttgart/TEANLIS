package teanlis;

/**
 * This class contains the methods to convert documents in internal format to
 * GATE format and vice versa.
 * 
 * @author Andreas Müller
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import gate.corpora.DocumentImpl;
import gate.util.*;
import gate.creole.*;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.annotation.NodeImpl;
import gate.AnnotationSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.net.URL;
import java.net.URI;

public class GATE_Converter {
	
	/**
	 * Convert a document in TEANLIS format to a document in GATE format.
	 * @param document The document in TEANLIS format which is converted to the 
	 * document in GATE format.
	 * @return The document in GATE format.
	 */
	public static gate.Document convert_to_gate_document (Document document) {
		
		try {
		Gate.init();
		
		gate.Document gate_document = Factory.newDocument(document.getOriginal_text());
		
		Integer id = 0;
		
		for (Token token: document.getTokens()) {
			FeatureMap token_map = gate.Factory.newFeatureMap();
			
			if (token.getLemma() != null) {
				token_map.put("lemma", token.getLemma());
			} 
			
			if (token.getPos() != null) {
				token_map.put("category", token.getPos());
			}
			
			if (token.getFunction() != null) {
				token_map.put("function", token.getFunction());
			}
			
			if (token.getGovernor() != -1) {
				token_map.put("governor", token.getGovernor());
			}
			
			if (token.getDependency() != null) {
				token_map.put("dependency", token.getDependency());
			}
			
			if (token.getNote_history() != null) {
				for (String key: token.getNote_history().keySet()) {
					token_map.put(key, token.getNote_history().get(key));
				}
			}
			
			gate_document.getAnnotations().add(id, new Long(token.getStart()), 
					new Long(token.getEnd()), "Token", token_map);
			
			id = id+1;
		}
		
		for (Sentence sentence: document.getSentences()) {
			gate_document.getAnnotations().add(id, new Long(sentence.getStart()), 
					new Long(sentence.getEnd()), "Sentence", 
					gate.Factory.newFeatureMap());
			
			id = id+1;
		}
		
		for (Paragraph paragraph: document.getParagraphs()) {
			FeatureMap paragraph_map = gate.Factory.newFeatureMap();
			
			gate_document.getAnnotations().add(id, new Long(paragraph.getStart()), 
					new Long(paragraph.getEnd()), "Paragraph", paragraph_map);
			
			id = id+1;
		}

		if (!document.getPages().isEmpty()) {
			for (Page page: document.getPages()) {
				gate_document.getAnnotations().add(id, new Long(page.getStart()), 
						new Long(page.getEnd()), "Page", gate.Factory.newFeatureMap());
				
				id = id+1;
			}
		}
		
		if (document.getSub_chapters() != null && 
				!document.getSub_chapters().isEmpty()) {
			for (Chapter sub_chapter: document.getSub_chapters()) {
				gate_document.getAnnotations().add(id, new Long(sub_chapter.getStart()), 
						new Long(sub_chapter.getEnd()), "Sub_chapter", 
						gate.Factory.newFeatureMap());
				
				id = id+1;
			}
		}
		
		if (document.getMain_chapters() != null && 
				!document.getMain_chapters().isEmpty()) {
			for (Chapter main_chapter: document.getMain_chapters()) {
				FeatureMap main_chapter_map = gate.Factory.newFeatureMap();
				
				if (main_chapter.getTitle() != null) {
					main_chapter_map.put("title", main_chapter.getTitle());
				}
				
				gate_document.getAnnotations().add(id, new Long(main_chapter.getStart()), 
						new Long(main_chapter.getEnd()), "Main_chapter", 
						main_chapter_map);
				
				id = id+1;
			}
		}
		
		for (String key: document.getText_units().keySet()) {
			ArrayList<Annotation> annotations_of_type = document.getText_units().
					get(key);
			
			for (Annotation annotation_of_type: annotations_of_type) {
				FeatureMap annotation_of_type_map = gate.Factory.newFeatureMap();
				
				annotation_of_type.setType(key);
				
				if (annotation_of_type.getType() != null) {
					annotation_of_type_map.put("type", 
							annotation_of_type.getType());
				} else if (annotation_of_type.getSub_type() != null) {
					annotation_of_type_map.put("sub_type", 
							annotation_of_type.getSub_type());
				}
				
				if (annotation_of_type.getNote_history() != null) {
					for (String property: annotation_of_type.getNote_history().
							keySet()) {
						annotation_of_type_map.put(property, annotation_of_type.
								getNote_history().get(property));
					}
				}
				
				if (annotation_of_type.getPropability() != 0.0) {
					annotation_of_type_map.put("probability", 
							annotation_of_type.getPropability());
				}
				
				if (annotation_of_type.getSub_type() != null && 
						!annotation_of_type.getSub_type().equals("unknown")) {
					gate_document.getAnnotations().add(id, new Long(
							annotation_of_type.getStart()), new Long(
								annotation_of_type.getEnd()), 
									annotation_of_type.getSub_type(), 
										annotation_of_type_map);
				} else {
					gate_document.getAnnotations().add(id, new Long(
							annotation_of_type.getStart()), new Long(
								annotation_of_type.getEnd()), 
									annotation_of_type.getType(), 
										annotation_of_type_map);
				}
				
				id = id+1;
			}
		}
		
		return gate_document;
		
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * Convert a document in GATE format to a document in TEANLIS format.
	 * @param gate_document The GATE document which is converted to a document in 
	 * TEANLIS format.
	 * @return The document in TEANLIS format.
	 */
	public static Document convert_to_document (gate.Document gate_document) {
		
		try {
		
		Gate.init();
		
		Document document = new Document();
		
		document.setOriginal_text(gate_document.getContent().toString());
		
		AnnotationSet default_annotations = gate_document.getAnnotations();
		
		ArrayList<gate.Annotation> default_annotation_list = 
				new ArrayList<gate.Annotation>(default_annotations);
		
		Collections.sort(default_annotation_list, new OffsetComparator());
		
		ArrayList<Token> tokens = new ArrayList<Token>();
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
		ArrayList<Page> pages = new ArrayList<Page>();
		ArrayList<Chapter> sub_chapters = new ArrayList<Chapter>();
		ArrayList<Chapter> main_chapters = new ArrayList<Chapter>();
		
		document.setText_units(new HashMap<String, ArrayList<Annotation>>());
		
		for (gate.Annotation default_annotation: default_annotation_list) {
			if (default_annotation.getType().equals("Token")) {
				Token token = new Token();
				
				token.setStart(default_annotation.getStartNode().getOffset().intValue());
				token.setEnd(default_annotation.getEndNode().getOffset().intValue());
				
				token.setNote_history(new HashMap<String, String>());
				
				if (default_annotation.getFeatures().containsKey("lemma")) {
					token.setLemma((String) default_annotation.getFeatures().
							get("lemma"));
				}
				
				if (default_annotation.getFeatures().containsKey("category")) {
					token.setPos((String) default_annotation.getFeatures().
							get("category"));
				}
				
				if (default_annotation.getFeatures().containsKey("dependency")) {
					token.setDependency((String) default_annotation.getFeatures().
							get("dependency"));
				}
				
				if (default_annotation.getFeatures().containsKey("governor")) {
					token.setGovernor((Integer) default_annotation.getFeatures().
							get("governor"));
				}
				
				if (default_annotation.getFeatures().containsKey("function")) {
					token.setFunction((String) default_annotation.getFeatures().
							get("function"));
				}
				
				for (Object key: default_annotation.getFeatures().keySet()) {
					if (!key.equals("lemma") && !key.equals("category") && 
							!key.equals("governor") && !key.equals("dependency") && 
								!key.equals("function")) {
						token.getNote_history().put(key.toString(), 
								default_annotation.getFeatures().get(key).toString());
					}
				}
				
				tokens.add(token);
			} else if (default_annotation.getType().equals("Sentence")) {
				Sentence sentence = new Sentence();
				
				sentence.setStart(default_annotation.getStartNode().
						getOffset().intValue());
				sentence.setEnd(default_annotation.getEndNode().
						getOffset().intValue());
				
				sentences.add(sentence);
			} else if (default_annotation.getType().equals("Paragraph")) {
				Paragraph paragraph = new Paragraph();
				
				paragraph.setStart(default_annotation.getStartNode().
						getOffset().intValue());
				paragraph.setEnd(default_annotation.getEndNode().
						getOffset().intValue());
				
				paragraphs.add(paragraph);
			} else if (default_annotation.getType().equals("Page")) {
				Page page = new Page();
				
				page.setStart(default_annotation.getStartNode().
						getOffset().intValue());
				page.setEnd(default_annotation.getEndNode().
						getOffset().intValue());
				
				pages.add(page);
			} else {
				Annotation annotation_of_type = new Annotation();
				
				annotation_of_type.setStart(default_annotation.getStartNode().
						getOffset().intValue());
				annotation_of_type.setEnd(default_annotation.getEndNode().
						getOffset().intValue());
				
				if (default_annotation.getFeatures().get("type") != null) {
					annotation_of_type.setType(
							default_annotation.getFeatures().get("type").toString());
				}
				
				if (default_annotation.getFeatures().get("sub_type") != null) {
					annotation_of_type.setSub_type(default_annotation.getFeatures().
							get("sub_type").toString(), document);
				} else {
					annotation_of_type.setSub_type("unknown", document);
				}
				
				if (document.getText_units().containsKey(default_annotation.getType())) {
					document.getText_units().get(default_annotation.getType()).add(
							annotation_of_type);
				} else {
					document.getText_units().put(default_annotation.getType(), 
							new ArrayList<Annotation>());
					
					document.getText_units().get(default_annotation.getType()).add(
							annotation_of_type);
				}
			}
		}
		
		document.setTokens(tokens);
		document.setSentences(sentences);
		document.setParagraphs(paragraphs);
		document.setPages(pages);

		
		return document;
		
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		//document.MakeOffsetToTokenMap();
		
		//document.BuildHierarchy();
		/*for (String key: document.getText_units().keySet()) {
			for (Annotation annotation_of_type: document.getText_units().get(key)) {
				annotation_of_type.setType(key);
			}
		} */
		
		try {
			
		Document document = Utils.LoadDocumentFromGateDocument(
				"C:\\Users\\Andreas\\Documents\\ePoetics\\test.xml");
		
		System.out.println(document.getText_units().get("Chapter").size());
		
		document.MakeOffsetToTokenMap();
		
		/*File document_url = new File("C:\\Users\\Andreas\\Documents\\MC1\\"
				+ "artV1.xml");
		
		gate.Document gate_document = Factory.newDocument(
				document_url.toURI().toURL());
		
		for (gate.Annotation annotation: gate_document.getAnnotations(
				"Original markups").get("Chapter")) {
			try {
				gate_document.getAnnotations().add(
						annotation.getStartNode().getOffset(),
							annotation.getEndNode().getOffset(),
								"Chapter", gate.Factory.newFeatureMap());
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
		
		Gate.init();
		
		//gate.Document gate_document = convert_to_gate_document(document);
		
		String gate_document_string = gate_document.toXml();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				"C:\\Users\\Andreas\\Documents\\ePoetics\\test.xml"));
		
		writer.write(gate_document_string);
		
		writer.close();
			
		/*try {
		
		Gate.init();
		
		File document_url = new File("C:\\Users\\Andreas\\Documents\\ePoetics\\"
				+ "Staiger_without_chapters");
		
		gate.Document gate_document_to_load = Factory.newDocument(
				document_url.toURI().toURL());
		
		Document internal_document = convert_to_document(gate_document_to_load);
		
		internal_document.MakeOffsetToTokenMap();
		
		internal_document.BuildHierarchy();
		
		HashSet<String> drama_nps = new HashSet<String>();
		ArrayList<Annotation> drama_np_list = new ArrayList<Annotation>();
		
		for (Annotation np: internal_document.getText_units().get("NP")) {
			System.out.println(np.getString(
					internal_document.getOriginal_text()));
			
			if (np.getString(internal_document.getOriginal_text()).
					toLowerCase().indexOf("drama") != -1) {
				drama_nps.add(np.getString(internal_document.getOriginal_text()));
				
				Annotation drama_np = new Annotation();
				
				drama_np.setStart(np.getStart());
				drama_np.setEnd(np.getEnd());
				
				drama_np.setType("drama_np");
				
				drama_np_list.add(drama_np);
			}
		}
		
		/*internal_document.getText_units().put("drama_np", drama_np_list);
		
		for (String drama_np: drama_nps) {
			System.out.println(drama_np);
		} */
		
		/*int start = internal_document.getOffset_to_token_map().get(
				internal_document.getOriginal_text().indexOf("DRAMATISCHER STIL: SPANNUNG"));
		int end = internal_document.getOffset_to_token_map().get(
				internal_document.getPages().get(218).getEnd());
		
		ArrayList<String> nouns_in_chapter = new ArrayList<String>();
		
		for (int i=start;i<=end;i++) {
			if (internal_document.getTokens().get(i).getPos() != null && 
					internal_document.getTokens().get(i).getPos().startsWith("N")) {
				if (internal_document.getTokens().get(i).getLemma() != null) {
					nouns_in_chapter.add(internal_document.getTokens().get(i).getLemma());
				}
			}
		}
		
		HashMap<String, Double> frequency_map = new HashMap<String, Double>();
		
		for (String noun_in_chapter: nouns_in_chapter) {
			if (frequency_map.containsKey(noun_in_chapter)) {
				frequency_map.put(noun_in_chapter, frequency_map.get(noun_in_chapter)+1.0);
			} else {
				frequency_map.put(noun_in_chapter, 1.0);
			}
		}
		
		HashMap<String, Double> drama_vector = new HashMap<String, Double>();
		
		for (String key: frequency_map.keySet()) {
			if (frequency_map.get(key) >= 10 && key.matches("[A-Z][A-z]+")) {
				drama_vector.put(key, frequency_map.get(key));
			}
		}
		
		System.out.println(drama_vector.keySet().toString());
		
		start = internal_document.getOffset_to_token_map().get(
				internal_document.getOriginal_text().indexOf("EPISCHER STIL: VORSTELLUNG"));
		end = internal_document.getOffset_to_token_map().get(
				internal_document.getPages().get(154).getEnd());
		
		ArrayList<String> nouns_in_chapter_2 = new ArrayList<String>();
		
		for (int i=start;i<=end;i++) {
			if (internal_document.getTokens().get(i).getPos() != null && 
					internal_document.getTokens().get(i).getPos().startsWith("N")) {
				if (internal_document.getTokens().get(i).getLemma() != null) {
					nouns_in_chapter_2.add(internal_document.getTokens().get(i).getLemma());
				}
			}
		}
		
		HashMap<String, Double> frequency_map_2 = new HashMap<String, Double>();
		
		for (String noun_in_chapter: nouns_in_chapter_2) {
			if (frequency_map_2.containsKey(noun_in_chapter)) {
				frequency_map_2.put(noun_in_chapter, frequency_map_2.get(noun_in_chapter)+1.0);
			} else {
				frequency_map_2.put(noun_in_chapter, 1.0);
			}
		}
		
		HashMap<String, Double> epic_vector = new HashMap<String, Double>();
		
		for (String key: frequency_map_2.keySet()) {
			if (frequency_map_2.get(key) >= 10 && key.matches("[A-Z][A-z]+")) {
				epic_vector.put(key, frequency_map_2.get(key));
			}
		}
		
		System.out.println(epic_vector.keySet().toString());
		
		start = internal_document.getOffset_to_token_map().get(
					internal_document.getPages().get(12).getStart());
		end = internal_document.getOffset_to_token_map().get(
					internal_document.getPages().get(88).getEnd());
		
		ArrayList<String> nouns_in_chapter_3 = new ArrayList<String>();
		
		for (int i=start;i<=end;i++) {
			if (internal_document.getTokens().get(i).getPos() != null && 
					internal_document.getTokens().get(i).getPos().startsWith("N")) {
				if (internal_document.getTokens().get(i).getLemma() != null) {
					nouns_in_chapter_3.add(internal_document.getTokens().get(i).getLemma());
				}
			}
		}
		
		HashMap<String, Double> frequency_map_3 = new HashMap<String, Double>();
		
		for (String noun_in_chapter: nouns_in_chapter_3) {
			if (frequency_map_3.containsKey(noun_in_chapter)) {
				frequency_map_3.put(noun_in_chapter, frequency_map_3.get(noun_in_chapter)+1.0);
			} else {
				frequency_map_3.put(noun_in_chapter, 1.0);
			}
		}
		
		HashMap<String, Double> lyric_vector = new HashMap<String, Double>();
		
		for (String key: frequency_map_3.keySet()) {
			if (frequency_map_3.get(key) >= 10 && key.matches("[A-Z][A-z]+")) {
				lyric_vector.put(key, frequency_map_3.get(key));
			}
		}
		
		Set<String> dramatic_terms = drama_vector.keySet();
		
		for (String epic_term: epic_vector.keySet()) {
			if (dramatic_terms.contains(epic_term)) {
				dramatic_terms.remove(epic_term);
			}
		}
		
		for (String lyric_term: lyric_vector.keySet()) {
			if (dramatic_terms.contains(lyric_term)) {
				dramatic_terms.remove(lyric_term);
			}
		}
		
		for (String dramatic_term: dramatic_terms) {
			if (!drama_vector.containsKey(dramatic_term)) {
				drama_vector.remove(dramatic_term);
			}
		}
		
		Set<String> epic_terms = epic_vector.keySet();
		
		for (String dramatic_term: drama_vector.keySet()) {
			if (epic_terms.contains(dramatic_term)) {
				epic_terms.remove(dramatic_term);
			}
		}
		
		for (String lyric_term: lyric_vector.keySet()) {
			if (epic_terms.contains(lyric_term)) {
				epic_terms.remove(lyric_term);
			}
		}
		
		for (String epic_term: epic_terms) {
			if (!epic_vector.containsKey(epic_term)) {
				epic_vector.remove(epic_term);
			}
		}
		
		Set<String> lyric_terms = lyric_vector.keySet();
		
		for (String dramatic_term: drama_vector.keySet()) {
			if (lyric_terms.contains(dramatic_term)) {
				lyric_terms.remove(dramatic_term);
			}
		}
		
		for (String epic_term: epic_vector.keySet()) {
			if (lyric_terms.contains(epic_term)) {
				lyric_terms.remove(epic_term);
			}
		}
		
		for (String lyric_term: lyric_terms) {
			if (!lyric_vector.containsKey(lyric_term)) {
				lyric_vector.remove(lyric_term);
			}
		}
		
		try {
			FileOutputStream object_output_stream = new FileOutputStream(
					"C:\\Users\\Andreas\\Documents\\ePoetics\\drama_vector");
		    ObjectOutput object_output = new ObjectOutputStream(
		    		object_output_stream);
		    object_output.writeObject(drama_vector);
		    
		    object_output_stream.close();
		    object_output.close();
		    
		    FileOutputStream object_output_stream_2 = new FileOutputStream(
					"C:\\Users\\Andreas\\Documents\\ePoetics\\epic_vector");
		    ObjectOutput object_output_2 = new ObjectOutputStream(
		    		object_output_stream_2);
		    object_output_2.writeObject(epic_vector);
		    
		    object_output_stream_2.close();
		    object_output_2.close();
		    
		    FileOutputStream object_output_stream_3 = new FileOutputStream(
					"C:\\Users\\Andreas\\Documents\\ePoetics\\lyric_vector");
		    ObjectOutput object_output_3 = new ObjectOutputStream(
		    		object_output_stream_3);
		    object_output_3.writeObject(lyric_vector);
		    
		    object_output_stream_3.close();
		    object_output_3.close();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		
		/*System.out.println(dramatic_terms.toString());
		
		ArrayList<Integer> page_labels = new ArrayList<Integer>();
		
		for (int i=0;i<internal_document.getPages().size();i++) {
			page_labels.add(0);
		}
				
		for (int i=0;i<internal_document.getPages().size();i++) {
			Page page = internal_document.getPages().get(i);
			
			start = internal_document.getOffset_to_token_map().get(page.getStart());
			end = internal_document.getOffset_to_token_map().get(page.getEnd());
			
			String paragraph_string = "";
			
			for (int j=start;j<=end;j++) {
				if (internal_document.getTokens().get(j).getPos() != null && 
						internal_document.getTokens().get(j).getPos().startsWith("N")) {
					if (internal_document.getTokens().get(j).getLemma() != null) {
						paragraph_string = paragraph_string+
								internal_document.getTokens().get(j).getLemma()+" ";
					}
				}
			}
			
			HashMap<String, Double> paragraph_vector = 
					SimilarityFunctions.computeVectors(paragraph_string, "").get(0);
			
			double dramatic_score = SimilarityFunctions.cosineSimilarity(
					paragraph_vector, drama_vector);
			
			double epic_score = SimilarityFunctions.cosineSimilarity(
					paragraph_vector, epic_vector);
			
			double lyric_score = SimilarityFunctions.cosineSimilarity(
					paragraph_vector, lyric_vector);
			
			double max_score = 0.0;
			
			if (dramatic_score > max_score) {
				max_score = dramatic_score;
				page_labels.set(i, 1);
			}
			
			if (epic_score > max_score) {
				max_score = epic_score;
				page_labels.set(i, 2);
			}
			
			if (lyric_score > max_score) {
				max_score = lyric_score;
				page_labels.set(i, 3);
			}
		}
		
		internal_document.getText_units().remove("labeled_page");
		
		internal_document.getText_units().remove("dramatic_page");
		internal_document.getText_units().remove("epic_page");
		internal_document.getText_units().remove("lyric_page");
		
		ArrayList<Annotation> labeled_pages_list = new ArrayList<Annotation>();
		
		for (int i=0;i<page_labels.size();i++) {
			Integer page_label = page_labels.get(i);
			
			System.out.println(i);
			
			if (page_label == 1) {
				System.out.println("Dramatic Page");
				System.out.println(internal_document.getPages().get(i).getString(
						internal_document.getOriginal_text()));
				
				Annotation labeled_page = new Annotation();
				
				labeled_page.setStart(
						internal_document.getPages().get(i).getStart());
				labeled_page.setEnd(internal_document.getPages().get(i).getEnd());
				
				labeled_page.setSub_type("dramatic_page", internal_document);
				labeled_page.setType("labeled_page");
				
				labeled_pages_list.add(labeled_page);
			} else if (page_label == 2) {
				System.out.println("Epic Page");
				System.out.println(internal_document.getPages().get(i).getString(
						internal_document.getOriginal_text()));
				
				Annotation labeled_page = new Annotation();
				
				labeled_page.setStart(
						internal_document.getPages().get(i).getStart());
				labeled_page.setEnd(internal_document.getPages().get(i).getEnd());
				
				labeled_page.setSub_type("epic_page", internal_document);
				labeled_page.setType("labeled_page");
				
				labeled_pages_list.add(labeled_page);
			} else if (page_label == 3) {
				System.out.println("Lyric Page");
				System.out.println(internal_document.getPages().get(i).getString(
						internal_document.getOriginal_text()));
				
				Annotation labeled_page = new Annotation();
				
				labeled_page.setStart(
						internal_document.getPages().get(i).getStart());
				labeled_page.setEnd(internal_document.getPages().get(i).getEnd());
				
				labeled_page.setSub_type("lyric_page", internal_document);
				labeled_page.setType("labeled_page");
				
				labeled_pages_list.add(labeled_page);
			}
		}
		
		internal_document.getText_units().put("labeled_page", labeled_pages_list);
		
		gate.Document document_2 = convert_to_gate_document(internal_document);
		
		String document_2_string = document_2.toXml();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\"
				+ "Andreas\\Documents\\ePoetics\\Staiger_without_chapters"));
		
		writer.write(document_2_string);
		
		writer.close();
				
		/*for (Token token: document.getTokens()) {
			if (token.getString(document.getOriginal_text()).indexOf("drama") != -1 && 
					token.getDependency() != null && (token.getDependency().equals("SB") || 
							token.getDependency().startsWith("O"))) {
				System.out.println(document.getSentences().get(token.getContaining_sentence()).
						getString(document.getOriginal_text()));
			}
		} */
		
		/*int counter = 0;
		
		for (Annotation quote: internal_document.getText_units().get("Quote")) {
			if (!quote.getSub_type().equals("unknown")) {
				counter = counter+1;
			}
			
			if (!quote.getSub_type().equals("Hervorhebung") && 
					!quote.getSub_type().equals("Titel") && 
						!quote.getSub_type().equals("Zitat")) {
				quote.setSub_type("unknown", internal_document);
			}
		}
		
		ArrayList<Annotation> classified_quotes = ActiveLearningModule.
				QuoteTrainingAndClassification(internal_document.getText_units().get("Quote"), 
						internal_document);
		
		System.out.println(counter);
		System.out.println(classified_quotes.size());
		
		/*for (Annotation classified_quote: classified_quotes) {
			System.out.println(classified_quote.getString(internal_document.
					getOriginal_text()));
			System.out.println(classified_quote.getPropability());
			System.out.println(classified_quote.getSub_type());
		} */
		
		/*} catch (java.io.IOException e) {
			e.printStackTrace(); */
			
		/*} catch (gate.creole.ResourceInstantiationException e) {
			e.printStackTrace(); */
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		
	}
}
