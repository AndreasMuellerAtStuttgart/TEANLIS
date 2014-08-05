package epoetics.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.SimpleAnalyzer;

import weka.core.Stopwords;

/**
 * This class contains functions for computing the similarity between texts and 
 * functions for computing keyword-sets in the form of tag clouds from arbitrary 
 * levels of the linguistic and conceptual hierarchy.
 * 
 * @author Andreas Müller
 *
 */
public class SimilarityFunctions {
	
	/**
	 * Take a map of term frequencies and a map of inverse document frequencies as 
	 * input and return a map of TFxIDF values.
	 * @param tf_map
	 * @param idf_map
	 * @return
	 */
	public static HashMap<String, Double> MakeTFxIDFMapFromTFMap (
			HashMap<String, Double> tf_map, HashMap<String, Double> idf_map) {
		HashMap<String, Double> tf_idf_map = new HashMap<String, Double>();
		
		for (String key: tf_map.keySet()) {
			tf_idf_map.put(key, tf_map.get(key)*idf_map.get(key));
		}
		
		return tf_idf_map;
	}
	
	/**
	 * Take a corpus in the form of a list of strings as input and return a map of 
	 * inverse document frequencies.
	 * @param corpus
	 * @return
	 */
	public static HashMap<String, Double> MakeIDFMap (
			ArrayList<String> corpus) {
		HashMap<String, Double> idf_map = new HashMap<String, Double>();
		
		for (String document: corpus) {
			String[] document_tokens = document.split(" ");
			
			HashSet<String> unique_document_tokens = new HashSet<String>();
			
			for (int i=0;i<document_tokens.length;i++) {
				unique_document_tokens.add(document_tokens[i]);
			}
			
			for (String unique_document_token: unique_document_tokens) {
				if (idf_map.containsKey(unique_document_token)) {
					idf_map.put(unique_document_token, idf_map.get(
							unique_document_token)+1.0);
				} else {
					idf_map.put(unique_document_token, 1.0);
				}
			}
		}
		
		return idf_map;
	}
	
	/** 
	 * This method computes a vector from each string where everything between two 
	 * whitespaces is taken as a term. In the HashMap which is computed each term in 
	 * a string is a key and its frequency is its value.
	 */
	
    public static ArrayList<HashMap<String, Double>> computeVectors (String doc_1, String doc_2) {
    	// Do simple white-space based tokenization
    	String[] doc_1_array = doc_1.split(" ");
    	String[] doc_2_array = doc_2.split(" ");


    	HashMap<String, Double> doc_1_map = new HashMap<String, Double>();
	
    	// Create term frequency vector for the first document
    	for (int i=0;i<doc_1_array.length;i++) {
    		if (doc_1_map.containsKey(doc_1_array[i])) {
    			doc_1_map.put(doc_1_array[i], doc_1_map.get(doc_1_array[i]));
    		} else {
    			doc_1_map.put(doc_1_array[i], 1.0);
    		}
    	}

    	// Create term frequency vector for the second document
    	HashMap<String, Double> doc_2_map = new HashMap<String, Double>();

    	for (int i=0;i<doc_2_array.length;i++) {
    		if (doc_2_map.containsKey(doc_2_array[i])) {
    			doc_2_map.put(doc_2_array[i], doc_2_map.get(doc_2_array[i]));
    		} else {
    			doc_2_map.put(doc_2_array[i], 1.0);
    		}
    	}

    	ArrayList<HashMap<String, Double>> frequency_map_list = 
    			new ArrayList<HashMap<String, Double>>();
    	
    	frequency_map_list.add(doc_1_map);
    	frequency_map_list.add(doc_2_map);
	
    	return frequency_map_list;
	
    }
    
    /** 
     * This method computes the dot product of two vectors. The vectors are usually 
     * vectors which map terms to some measure of the importance of a term in a 
     * document, e.g., term frequency or TFxIDF.
     */

    public static double dotProduct (HashMap<String, Double> doc_1_map, 
    		HashMap<String, Double> doc_2_map) {
    	double dot_product = 0;
    	Set<String> key_set_1 = doc_1_map.keySet();

    	for (String key: key_set_1) {	

    		if (doc_2_map.get(key) != null) {
    			dot_product = dot_product + doc_1_map.get(key) * doc_2_map.get(key);
    		}
    	}

    	return dot_product;
    }
    
    /**
     * This method computes the normalization factor for length normalization of 
     * vectors in the TFxIDF computation. See the book 
     * "Introduction to Information Retrieval", chapter 6, section "Queries as 
     * vectors", formula 27 (online edition). 
     */
    
    public static double normalizationFactor (HashMap<String, Double> doc_1_map, 
    		HashMap<String, Double> doc_2_map) {
    	// Calculate the normalization factor
    	double euclidean_length_1 = 0;
        double euclidean_length_2 = 0;

        double vector_sum_1 = 0;
        double vector_sum_2 = 0;

        double normalization_factor = 0;

        Set<String> key_set_1 = doc_1_map.keySet();
        Set<String> key_set_2 = doc_2_map.keySet();

        for (String key: key_set_1) {
        	vector_sum_1 = vector_sum_1 + doc_1_map.get(key) * doc_1_map.get(key);
        }
        for (Object key: key_set_2) {
        	vector_sum_2 = vector_sum_2 + doc_2_map.get(key) * doc_2_map.get(key);
        }

        euclidean_length_1 = Math.sqrt(vector_sum_1);
        euclidean_length_2 = Math.sqrt(vector_sum_2);

        normalization_factor = euclidean_length_1 * euclidean_length_2;

        return normalization_factor;
    }
    
    /**
     * This method takes two vectors as input and returns their cosine similarity.
     */
    
    public static double cosineSimilarity (HashMap<String, Double> doc_1_map, 
    		HashMap<String, Double> doc_2_map) {

    	// Compute the dot product
    	double dot_product = dotProduct (doc_1_map, doc_2_map);
    	
    	// Compute the normalization factor
    	double normalization_factor = normalizationFactor (doc_1_map, doc_2_map);

    	double cos_similarity = dot_product / normalization_factor;

    	return cos_similarity;

    }
	
    /**
     * This method takes two strings as input, tokenizes them using the regular 
     * expression [,;.: ] and returns the jaccard coefficient between the resulting 
     * sets of terms representing the two strings.
     * @param text_1
     * @param text_2
     * @return
     */
	public static float JaccardCoefficient (String text_1, String text_2) {
		String[] text_1_tokens = text_1.split("[,;.: ]");
		String[] text_2_tokens = text_2.split("[,;.: ]");
		
		HashSet<String> text_1_set = new HashSet<String>();
		HashSet<String> text_2_set = new HashSet<String>();
		
		for (int i=0;i<text_1_tokens.length;i++) {
			text_1_set.add(text_1_tokens[i]);
		}
		
		for (int i=0;i<text_2_tokens.length;i++) {
			text_2_set.add(text_2_tokens[i]);
		}
		
		HashSet<String> intersection = new HashSet<String>();
		
		for (String token: text_1_set) {
			if (text_2_set.contains(token)) {
				intersection.add(token);
			}
		}
		
		HashSet<String> union = new HashSet<String>();
		
		union.addAll(text_1_set);
		union.addAll(text_2_set);
		
		return (float) intersection.size() / (float) union.size();
		
	}
	
	public static ScoreDoc[] MakeLuceneBasedSimilarityRanking (ArrayList<String> search_units, String query) {
		
		try {
		
		IndexWriter index_writer = new IndexWriter(new RAMDirectory(), new IndexWriterConfig(
				org.apache.lucene.util.Version.LUCENE_35,
				new WhitespaceAnalyzer(org.apache.lucene.util.Version.LUCENE_35)));
		
		for (String search_unit: search_units) {
			Field field = new Field("text",search_unit, org.apache.lucene.document.Field.Store.
					YES, org.apache.lucene.document.Field.Index.ANALYZED);
			org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
			document.add(field);
			index_writer.addDocument(document);
		}
		
		IndexSearcher index_searcher = new IndexSearcher(IndexReader.open(index_writer, false));
		
		BooleanQuery lucene_query = new BooleanQuery();
		
		String[] terms = query.split(" ");
		
		for (int i=0;i<terms.length;i++) {
			Term goethe = new Term("text",terms[i]);
			
			TermQuery term_query = new TermQuery(goethe);
			lucene_query.add(term_query, BooleanClause.Occur.MUST);
		}
		
		TopDocs results = index_searcher.search(lucene_query, search_units.size());
		
		ScoreDoc[] results_list = results.scoreDocs;
		
		return results_list;
		
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static HashMap<Integer, HashMap<String, Double>> 
		ExtractSimpleKeywords_Arbitrary_Hierarchy_Level_Frequency_Cloud (
			ArrayList<Linguistic_Element> elements, Document document) {
		
		Stopwords stopwords = new weka.core.Stopwords();
		
		HashSet<String> keywords = new HashSet<String>();
		
		HashMap<Integer, HashMap<String, Double>> search_units = 
				new HashMap<Integer, HashMap<String, Double>>();
		
		for (int j=0;j<elements.size();j++) {
			
			
			
			Linguistic_Element element = elements.get(j);
			
			int start_token = 0;
			int end_token = 0;
			
			try {
				start_token = document.getOffset_to_token_map().get(
						element.getStart());
				end_token = document.getOffset_to_token_map().get(
						element.getEnd());
			} catch (java.lang.Exception e) {
				end_token = document.getTokens().size()-1;
			}
			
			String element_text = document.getTokens().get(start_token).
					getString(document.getOriginal_text());
			
			Token token = document.getTokens().get(start_token);
			
			if (start_token != -1 && token.getPos() != null && token.getPos().
					startsWith("N") && token.getString(
							document.getOriginal_text()).matches("[A-z]+") && 
									stopwords.isStopword(token.getString(
											document.getOriginal_text()))) {
				keywords.add(document.getTokens().get(start_token).getString(
						document.getOriginal_text()));
			}
			
			for (int i=start_token+1;i<end_token+1;i++) {
				element_text = element_text + " " + document.getTokens().
						get(i).getString(document.getOriginal_text());
				
				token = document.getTokens().get(i);
			
				if (token.getPos() != null && 
						token.getPos().startsWith("N") && token.getString(
								document.getOriginal_text()).matches("[A-z]+")
										&& stopwords.isStopword(token.
												getString(document.
														getOriginal_text()))) {
					keywords.add(document.getTokens().get(i).getString(
							document.getOriginal_text()));
				}
			}
			
			
			
			search_units.put(j,computeVectors(element_text,"").get(0));
		}
		
		return search_units;
	}
	
	public static ArrayList<Tag_Cloud> ExtractSimpleKeywords_Arbitrary_Hierarchy_Level (
			ArrayList<Linguistic_Element> elements, Document document) {
		
		Stopwords stopwords = new weka.core.Stopwords();
		
		HashSet<String> keywords = new HashSet<String>();
		
		HashMap<Integer, String> search_units = new HashMap<Integer, String>();
		
		for (int j=0;j<elements.size();j++) {
			
			
			
			Linguistic_Element element = elements.get(j);
			
			int start_token = 0;
			int end_token = 0;
			
			try {
				start_token = document.getOffset_to_token_map().get(
						element.getStart());
				end_token = document.getOffset_to_token_map().get(
						element.getEnd());
			} catch (java.lang.Exception e) {
				end_token = document.getTokens().size()-1;
			}
			
			String element_text = document.getTokens().get(start_token).
					getString(document.getOriginal_text());
			
			Token token = document.getTokens().get(start_token);
			
			if (start_token != -1 && token.getPos() != null && token.getPos().
					startsWith("N") && token.getString(
							document.getOriginal_text()).matches("[A-z]+") && 
									stopwords.isStopword(token.getString(
											document.getOriginal_text()))) {
				keywords.add(document.getTokens().get(start_token).getString(
						document.getOriginal_text()));
			}
			
			for (int i=start_token+1;i<end_token+1;i++) {
				element_text = element_text + " " + document.getTokens().
						get(i).getString(document.getOriginal_text());
				
				token = document.getTokens().get(i);
			
				if (token.getPos() != null && 
						token.getPos().startsWith("N") && token.getString(
								document.getOriginal_text()).matches("[A-z]+")
										&& stopwords.isStopword(token.
												getString(document.
														getOriginal_text()))) {
					keywords.add(document.getTokens().get(i).getString(
							document.getOriginal_text()));
				}
			}
			
			
			
			search_units.put(j,element_text);
			
			
		}
		
			try {
			
			IndexWriter index_writer = new IndexWriter(new RAMDirectory(), new IndexWriterConfig(
					org.apache.lucene.util.Version.LUCENE_35,
					new WhitespaceAnalyzer(org.apache.lucene.util.Version.LUCENE_35)));
			
			for (Integer key: search_units.keySet()) {
				String search_unit = search_units.get(key);
				Field field = new Field("text",search_unit, org.apache.lucene.document.Field.Store.
						YES, org.apache.lucene.document.Field.Index.ANALYZED);
				org.apache.lucene.document.Document search_document = new org.apache.lucene.document.Document();
				search_document.add(field);
				index_writer.addDocument(search_document);
			}
			
			IndexSearcher index_searcher = new IndexSearcher(IndexReader.open(index_writer, false));
			
			HashMap<Integer, ArrayList<Keyword>> search_results = new HashMap<Integer, 
					ArrayList<Keyword>>();
			
			for (String keyword: keywords) {
			
				Term keyword_term = new Term("text", keyword);

				TermQuery keyword_query = new TermQuery(keyword_term);
			
				TopDocs results = index_searcher.search(keyword_query, search_units.size());
			
				ScoreDoc[] results_list = results.scoreDocs;
			
				for (int i=0;i<results_list.length;i++) {
					ScoreDoc score_doc = results_list[i];
					
					int index = score_doc.doc;
					
					if (search_results.containsKey(index)) {
						ArrayList<Keyword> new_list = search_results.get(index);
						
						Keyword new_key_word_entry = new Keyword();
						
						new_key_word_entry.setKeyword(keyword);
						new_key_word_entry.setScore(score_doc.score);
						new_list.add(new_key_word_entry);
						
						search_results.put(score_doc.doc, new_list);
					} else {
						ArrayList<Keyword> new_list = new ArrayList<Keyword>();
						
						Keyword new_key_word_entry = new Keyword();
						
						new_key_word_entry.setKeyword(keyword);
						new_key_word_entry.setScore(score_doc.score);
						new_list.add(new_key_word_entry);
						
						search_results.put(score_doc.doc, new_list);
					}
				}
			}
			
			ArrayList<Tag_Cloud> tag_cloud_list = new ArrayList<Tag_Cloud>();
			
			for (Integer key: search_results.keySet()) {
			
				HashMap<Float, ArrayList<String>> keywords_at_score = new HashMap<Float, 
						ArrayList<String>>();
			
				ArrayList<Keyword> element_list = search_results.get(key);
			
				for (Keyword keyword: element_list) {
				
					if (keywords_at_score.containsKey(keyword.score)) {
						ArrayList<String> keyword_list = keywords_at_score.get(keyword.score);
					
						keyword_list.add(keyword.getKeyword());
					
						keywords_at_score.put(keyword.score, keyword_list);
					} else {
						ArrayList<String> keyword_list = new ArrayList<String>();
					
						keyword_list.add(keyword.getKeyword());
					
						keywords_at_score.put(keyword.score, keyword_list);
					}
				}
			
				Float[] sorted_key_list = new Float[keywords_at_score.size()];
			
				int key_counter = 0;
			
				for (Float score: keywords_at_score.keySet()) {
					sorted_key_list[key_counter] = score;
					key_counter = key_counter+1;
				}
			
				Arrays.sort(sorted_key_list);
				
				Tag_Cloud tag_cloud = new Tag_Cloud();
				
				tag_cloud.setKeywords_at_score(keywords_at_score);
				tag_cloud.setSorted_score_list(sorted_key_list);
				
				tag_cloud_list.add(tag_cloud);
			}
			
			return tag_cloud_list;
				
				
			
			} catch (java.lang.Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}
	
	public static ArrayList<Tag_Cloud> ExtractSimpleKeywords (Document document) {
		
		HashSet<String> keywords = new HashSet<String>();
		
		
		
		HashMap<Integer, String> search_units = new HashMap<Integer, String>();
		
		for (int j=0;j<document.getSub_chapters().size();j++) {
			Chapter sub_chapter = document.getSub_chapters().get(j);
			String sub_chapter_lemma_text = sub_chapter.getString(document.getOriginal_text());
			
			int start_token = document.getOffset_to_token_map().get(sub_chapter.getStart());
			int end_token = document.getOffset_to_token_map().get(sub_chapter.getEnd());
			
			sub_chapter_lemma_text = document.getTokens().get(start_token).getLemma();
			
			if (start_token != -1 && document.getTokens().get(start_token).getPos() != null &&
					document.getTokens().get(start_token).getPos().startsWith("N") && 
					document.getTokens().get(start_token).getlemma().matches("[A-Z].+")) {
				keywords.add(document.getTokens().get(start_token).getLemma());
			}
			
			for (int i=start_token+1;i<end_token+1;i++) {
				sub_chapter_lemma_text = sub_chapter_lemma_text+" "+document.getTokens().get(i).
						getLemma();
			
				if (document.getTokens().get(i).getPos() != null && 
						document.getTokens().get(i).getPos().startsWith("N") && 
						document.getTokens().get(i).getLemma().matches("[A-Z].+")) {
					keywords.add(document.getTokens().get(i).getLemma());
				}
			}
			
			search_units.put(j,sub_chapter_lemma_text);
		}
		
		
			try {
			
			IndexWriter index_writer = new IndexWriter(new RAMDirectory(), new IndexWriterConfig(
					org.apache.lucene.util.Version.LUCENE_35,
					new WhitespaceAnalyzer(org.apache.lucene.util.Version.LUCENE_35)));
			
			for (Integer key: search_units.keySet()) {
				String search_unit = search_units.get(key);
				Field field = new Field("text",search_unit, org.apache.lucene.document.Field.Store.
						YES, org.apache.lucene.document.Field.Index.ANALYZED);
				org.apache.lucene.document.Document search_document = new org.apache.lucene.document.Document();
				search_document.add(field);
				index_writer.addDocument(search_document);
			}
			
			IndexSearcher index_searcher = new IndexSearcher(IndexReader.open(index_writer, false));
			
			HashMap<Integer, ArrayList<Keyword>> search_results = new HashMap<Integer, 
					ArrayList<Keyword>>();
			
			for (String keyword: keywords) {
			
				Term keyword_term = new Term("text", keyword);
				
				TermQuery keyword_query = new TermQuery(keyword_term);
			
				TopDocs results = index_searcher.search(keyword_query, search_units.size());
			
				ScoreDoc[] results_list = results.scoreDocs;
			
				for (int i=0;i<results_list.length;i++) {
					ScoreDoc score_doc = results_list[i];
					
					int index = score_doc.doc;
					
					if (search_results.containsKey(index)) {
						ArrayList<Keyword> new_list = search_results.get(index);
						
						Keyword new_key_word_entry = new Keyword();
						
						new_key_word_entry.setKeyword(keyword);
						new_key_word_entry.setScore(score_doc.score);
						new_list.add(new_key_word_entry);
						
						search_results.put(score_doc.doc, new_list);
					} else {
						ArrayList<Keyword> new_list = new ArrayList<Keyword>();
						
						Keyword new_key_word_entry = new Keyword();
						
						new_key_word_entry.setKeyword(keyword);
						new_key_word_entry.setScore(score_doc.score);
						new_list.add(new_key_word_entry);
						
						search_results.put(score_doc.doc, new_list);
					}
				}
			}
			
			ArrayList<Tag_Cloud> tag_cloud_list = new ArrayList<Tag_Cloud>();
			
			for (Integer key: search_results.keySet()) {
			
				HashMap<Float, ArrayList<String>> keywords_at_score = new HashMap<Float, 
						ArrayList<String>>();
			
				ArrayList<Keyword> first_sub_chapter_list = search_results.get(key);
			
				for (Keyword keyword: first_sub_chapter_list) {
				
					if (keywords_at_score.containsKey(keyword.score)) {
						ArrayList<String> keyword_list = keywords_at_score.get(keyword.score);
					
						keyword_list.add(keyword.getKeyword());
					
						keywords_at_score.put(keyword.score, keyword_list);
					} else {
						ArrayList<String> keyword_list = new ArrayList<String>();
					
						keyword_list.add(keyword.getKeyword());
					
						keywords_at_score.put(keyword.score, keyword_list);
					}
				}
			
				Float[] sorted_key_list = new Float[keywords_at_score.size()];
			
				int key_counter = 0;
			
				for (Float score: keywords_at_score.keySet()) {
					sorted_key_list[key_counter] = score;
					key_counter = key_counter+1;
				}
			
				Arrays.sort(sorted_key_list);
				
				Tag_Cloud tag_cloud = new Tag_Cloud();
				
				tag_cloud.setKeywords_at_score(keywords_at_score);
				tag_cloud.setSorted_score_list(sorted_key_list);
				
				tag_cloud_list.add(tag_cloud);
			}
			
			return tag_cloud_list;
				
				
			
			} catch (java.lang.Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}
	
	public static ScoreDoc[] SimpleLuceneSearch (Document document, String search_unit, String query) {
		ArrayList<String> search_units = new ArrayList<String>();
		
		if (search_unit.equals("paragraphs")) {
			for (Paragraph paragraph: document.getParagraphs()) {
				search_units.add(paragraph.getString(document.getOriginal_text()));
			}
		} else if (search_unit.equals("sentences")) {
			for (Sentence paragraph: document.getSentences()) {
				search_units.add(paragraph.getString(document.getOriginal_text()));
			}
		} else if (search_unit.equals("pages")) {
			for (Page paragraph: document.getPages()) {
				search_units.add(paragraph.getString(document.getOriginal_text()));
			}
		} else if (search_unit.equals("sub_chapters")) {
			for (Chapter paragraph: document.getSub_chapters()) {
				search_units.add(paragraph.getString(document.getOriginal_text()));
			}
		} else if (search_unit.equals("main_chapters")) {
			for (Chapter paragraph: document.getMain_chapters()) {
				search_units.add(paragraph.getString(document.getOriginal_text()));
			}
		}
		
		return MakeLuceneBasedSimilarityRanking(search_units, query);
	}
	
	public static void main(String[] args) {
		String test_1 = "This is a test";
		String test_2 = "This is another test";
		
		ArrayList<HashMap<String, Double>> vectors = computeVectors(test_1, test_2);
		
		String test = "This is a test";
		
		String[] tokens_in_test = test.split(" ");
		
		for (int i=0;i<tokens_in_test.length;i++) {
			if (weka.core.Stopwords.isStopword(tokens_in_test[i])) {
				System.out.println(tokens_in_test[i]);
			}
		}
		
		/*Document document = new Document();
		
		document.LoadAsOneObject("C:\\Users\\Work\\Documents\\ePoetics\\new_iliad_project_gutenberg");
		
		document.BuildHierarchy();
		
		document.MakeOffsetToTokenMap();
		
		int counter = 0;
		
		ArrayList<String> search_units = new ArrayList<String>();
		
		ArrayList<Linguistic_Element> corpus_to_search = new ArrayList<Linguistic_Element>();
		
		for (Page page: document.getPages()) {
			corpus_to_search.add(page);
		}
		
		ArrayList<Tag_Cloud> tag_cloud_list = ExtractSimpleKeywords_Arbitrary_Hierarchy_Level(
				corpus_to_search, document);
		
		for (int i=383;i<434;i++) {
			
			Tag_Cloud tag_cloud = tag_cloud_list.get(i);
			
			Float[] sorted_score_list = tag_cloud.getSorted_score_list();
			
			tag_cloud.MakeKeywordAtRankMap();
			
			ArrayList<String> top_10_keywords = tag_cloud.getNTopKeywords(25);
			
			for (int j=0;j<top_10_keywords.size();j++) {
				System.out.println(top_10_keywords.get(j));
			}
			
			System.out.println("==========");
		}
		
		ArrayList<Paragraph> copy_paragraph_list = new ArrayList<Paragraph>();
		
		for (Paragraph paragraph: document.getParagraphs()) {
			if (paragraph.getString(document.getOriginal_text()).split("[A-z]").length > 1) {
				copy_paragraph_list.add(paragraph);
			}
		}
		
		document.setParagraphs(copy_paragraph_list);
		
		/*for (Paragraph paragraph: document.getParagraphs()) {
			
			String search_unit = document.getTokens().get(document.getOffset_to_token_map().get(
					paragraph.getStart())).getString(document.getOriginal_text());
			
			for (int i=document.getOffset_to_token_map().get(paragraph.getStart())+1;i<
					document.getOffset_to_token_map().get(paragraph.getEnd());i++) {
				
				if (document.getTokens().get(i).getString(document.getOriginal_text()).
						matches("[A-z]+")) {
					
					search_unit = search_unit + document.getTokens().get(i).getString(
							document.getOriginal_text());
				}
			}
			
			search_units.add(search_unit);
			if (JaccardCoefficient(document.getParagraphs().get(5).getString(
					document.getOriginal_text()), paragraph.getString(document.getOriginal_text())) >
				0.1) {
				System.out.println("Paragraph "+counter);
			System.out.println(JaccardCoefficient(document.getParagraphs().get(5).getString(
					document.getOriginal_text()), paragraph.getString(document.getOriginal_text())));
			}
			counter = counter+1;
		} */
		
		/*String query = "Schiller Goethe";
		
		int index = SimpleLuceneSearch(document, "paragraphs", query)[0].doc;
		
		ArrayList<Tag_Cloud> search_results = 
				ExtractSimpleKeywords (document);
		
		HashMap<Integer, ArrayList<String>> overall_segmentation = new HashMap<Integer, 
				ArrayList<String>>();
		
		overall_segmentation.put(0, new ArrayList<String>());
		
		Integer current_key = 0;
		
		for (int i=0;i<document.getSub_chapters().size();i++) {
		
			Tag_Cloud tag_cloud = search_results.get(i);
		
			tag_cloud.setStart(document.getSub_chapters().get(i).getStart());
			tag_cloud.setEnd(document.getSub_chapters().get(i).getEnd());
		
			ArrayList<String> keywords_at_008 = tag_cloud.getNTopKeywords(10);
			
			Chapter current_sub_chapter = document.getSub_chapters().get(i);
		
			ArrayList<Token> chapter = new ArrayList<Token>();
		
			for (Token token: document.getTokens()) {
			
				if (token.getEnd() > current_sub_chapter.getEnd()) {
					break;
				} else if (token.getStart() >= current_sub_chapter.getStart() &&
						token.getEnd() <= current_sub_chapter.getEnd()) {
					chapter.add(token);
				}
			}
		
			HashMap<Integer, String> segmentation = TagBasedSegmentation.Baseline_1(chapter, 
					keywords_at_008, document);
		
			int[] key_list = new int[segmentation.size()];
		
			int key_counter = 0;
		
			for (Integer key: segmentation.keySet()) {
				key_list[key_counter] = key;
				key_counter = key_counter+1;
			}
		
			Arrays.sort(key_list);
		
			HashMap<Integer, ArrayList<String>> segment_boundaries_to_keywords_map = 
					new HashMap<Integer, ArrayList<String>>();
		
			ArrayList<String> current_list = new ArrayList<String>();
			
			for (Integer key: key_list) {
				if (key - current_key >= 150) {
						ArrayList<String> new_list = new ArrayList<String>();
						for (String list_member: current_list) {
							new_list.add(list_member);
						}
						segment_boundaries_to_keywords_map.put(current_key, new_list);
						current_key = key;
						
						current_list.clear();
				}
				current_list.add(segmentation.get(key));
			}
		
			int[] sorted_boundaries = new int[segment_boundaries_to_keywords_map.keySet().size()];
		
			int boundary_counter = 0;
		
			for (Integer key: segment_boundaries_to_keywords_map.keySet()) {
				sorted_boundaries[boundary_counter] = key;
				boundary_counter = boundary_counter+1;
			}
		
			Arrays.sort(sorted_boundaries);
		
			for (int j=0;j<sorted_boundaries.length;j++) {
				overall_segmentation.put(sorted_boundaries[j], 
						segment_boundaries_to_keywords_map.get(sorted_boundaries[j]));
			}
		}
		
		for (Chapter chapter: document.getMain_chapters()) {
			
		}
		
		System.out.println(overall_segmentation.size());
		
		Set<Integer> keyset = overall_segmentation.keySet();
		
		int[] keyset_list = new int[keyset.size()];
		
		int keyset_list_counter = 0;
		
		for (Integer key: keyset) {
			keyset_list[keyset_list_counter] = key;
			keyset_list_counter = keyset_list_counter+1;
		}
		
		Arrays.sort(keyset_list);
		
		/*String first_segment = document.getOriginal_text().substring(
				document.getLines().get(keyset_list[2]).getStart(),
				document.getLines().get(keyset_list[3]).getEnd());
		
		System.out.println(first_segment);
		
		System.out.println(keyset_list[2]);
		System.out.println(keyset_list[3]); */
		
		/*for (String keyword: overall_segmentation.get(keyset_list[keyset_list.length-1])) {
			System.out.println(keyword);
		} */
	}
}
