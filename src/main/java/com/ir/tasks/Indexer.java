package com.ir.tasks;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that handles the Cosine Similarity logic
 * 
 * @author annuraju
 *
 */
public class Indexer{

	//each entry in the inverted index is a posting
	private Map<String, Map<Integer, Entry>> invertedIndex;
	private Map<Integer, Set<String>> docTokens;
	private Map<Integer, Integer> docSize;
	private Map<String, Integer> termFrequencies;
	private Map<String, DocFrequency> docFrequencies;
	private Map<Integer, String> docIds;
	private static int id = 0;
	private int n;
	private int totalDocLength;
	boolean useStopping;
	private List<String> stopWords;

	public Indexer(int n, boolean useStopping) throws IOException {
		this.useStopping = useStopping;
		if(useStopping){
			stopWords = TextProcessor.getStopWords();

		}
		this.invertedIndex = new HashMap<String, Map<Integer, Entry>>();
		this.docTokens = new HashMap<Integer, Set<String>>();
		this.docSize = new HashMap<Integer, Integer>();
		this.termFrequencies = new HashMap<String, Integer>();
		this.docFrequencies = new HashMap<String, DocFrequency>();
		this.docIds = new HashMap<Integer, String>();
		this.n = n;
		
	}

	/**
	 * For every file, it splits the content into tokens and then adds every
	 * term to the inverted index
	 * @param fileName
	 * @throws IOException
	 */
	public void create(String fileName) throws IOException{
		try{
			String[] tokens = getTokens(fileName);	
			String docId = fileName.substring(fileName.lastIndexOf("/")+1);
			// Maintaining doc ids with their doc names in a map
			id = id+1;
			docTokens.put(id, new HashSet<String>(Arrays.asList(tokens)));
			docIds.put(Integer.valueOf(id), docId);
			totalDocLength += tokens.length;
			// Keeping track of the size of each document
			docSize.put(Integer.parseInt(docId), tokens.length);		
			generateNGrams(tokens, Integer.parseInt(docId));	
		}
		catch(StringIndexOutOfBoundsException ex){
			System.out.println(fileName);
		}
	}

	/**
	 * Generates n-grams based on value of n
	 * @param tokens
	 * @param docId
	 */
	public void generateNGrams(String[] tokens, int docId){
		for(int i=0; i<tokens.length-n+1;i++){
			StringBuilder sb = new StringBuilder();
			for(int k=0;k<n;k++){
				sb.append(tokens[i+k] + " ");
			}
			addTermToIndex(sb.toString().trim(), docId);
		}
	}		

	/**
	 * Add each term to the inverted index along with its frequency within each document.
	 * @param term
	 * @param docId
	 */
	private void addTermToIndex(String term, int docId){

		term = term.trim();

		if(term.equals("$"))
			return;
		
		if(term.endsWith(","))
			term = term.substring(0, term.lastIndexOf(","));

		if(term == null || term.length() == 0 || term.equals(" "))
			return;
		
		if(useStopping){
			if(stopWords.contains(term))
				return;
		}

		Map<Integer, Entry> map = invertedIndex.get(term);
		if(!invertedIndex.containsKey(term)){
			map = new HashMap<Integer, Entry>();
		}
		int tf = map.get(docId) == null ? 0 : map.get(docId).getTermFrequency();
		Entry e = new Entry(docId, tf+1, 0);		
		map.put(docId, e);
		invertedIndex.put(term, map);
		addTermFrequency(term);
		addDocFrequency(docId, term);
	}

	private boolean isNumeric(String term){
		return term.matches("-?\\d+(\\.\\d+)?") || term.matches("-?\\d+([,]\\d+)*(\\.)?\\d+([,]\\d+)*");
	}

	private void addTermFrequency(String term){
		int tf = termFrequencies.containsKey(term) ? termFrequencies.get(term) : 0;
		termFrequencies.put(term, tf+1);	
	}

	private void addDocFrequency(int docId, String term){
		DocFrequency docs = null;
		if(docFrequencies.containsKey(term)){
			docs = docFrequencies.get(term);
		}
		else{
			docs = new DocFrequency();
		}
		docs.addDocumentToSet(docId);
		docFrequencies.put(term, docs);
	}


	/**
	 * Splits the content within the file based on space
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String[] getTokens(String fileName) throws IOException{
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(fileName));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = br.readLine()) != null){
				sb.append(line+" ");
			}
			return sb.toString().split(" ");
		}
		catch(IOException ex){

		}
		finally{
			br.close();
		}
		return null;
	}

	public Map<String, Map<Integer,Entry>> getInvertedIndex() {
		return invertedIndex;
	}

	public void setInvertedIndex(Map<String, Map<Integer, Entry>> invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	public Map<String, Integer> getTermFrequencies() {
		return termFrequencies;
	}

	public void setTermFrequencies(Map<String, Integer> termFrequencies) {
		this.termFrequencies = termFrequencies;
	}

	public Map<String, DocFrequency> getDocFrequencies() {
		return docFrequencies;
	}

	public void setDocFrequencies(Map<String, DocFrequency> docFrequencies) {
		this.docFrequencies = docFrequencies;
	}

	public Map<Integer, String> getDocIds() {
		return docIds;
	}

	public void setDocIds(Map<Integer, String> docIds) {
		this.docIds = docIds;
	}
	
	public Map<Integer, Set<String>> getDocTokens() {
		return docTokens;
	}

	public void setDocTokens(Map<Integer, Set<String>> docTokens) {
		this.docTokens = docTokens;
	}


	public void closeIndex() throws IOException {
		// TODO Auto-generated method stub

	}

	public void performSearch(Map<Integer, String> queries, String indexLocation) {
		// TODO Auto-generated method stub

	}

	public List<Entry> calculateScore(Map<String, Double> queryWeights, String query, int id, Map<String, Map<Integer, Entry>> index) {		
		DecimalFormat df2 = new DecimalFormat(".##");
		df2.setRoundingMode(RoundingMode.FLOOR);
		String terms[] = query.split(" ");		
		Map<Integer, Map<String, Double>> termDocScores = new HashMap<Integer, Map<String, Double>>();
		Map<String, Double> docMap = new HashMap<String, Double>();
		for(String term : terms){		
			if(index.containsKey(term)){
				Map<Integer, Entry> docList = index.get(term);
				for(java.util.Map.Entry<Integer, Entry> e : docList.entrySet()){
					double docWeight = 1 + Math.log10(e.getValue().getTermFrequency());
					docMap = termDocScores.containsKey(e.getKey()) ? termDocScores.get(e.getKey()) : new HashMap<String, Double>();
					docMap.put(term, docWeight);
					termDocScores.put(e.getKey(), docMap);					
				}
			}
		}

		int i = 0;
		
		List<Entry> finalResults = new ArrayList<Entry>();
		
		Map<Integer, String> docIds = getDocIds();
		
		for(java.util.Map.Entry<Integer, Map<String, Double>> entry : termDocScores.entrySet()){	
			double cosineScore = 0.0;
			double numerator = 0.0;
			double denominator1 = 0.0, denominator2 = 0.0;
			for(String term : terms){
				double dtf = 0.0;
				if(entry.getValue().containsKey(term)){
					dtf = entry.getValue().get(term);
				}
				numerator +=  dtf * queryWeights.get(term);
				denominator1 += Math.pow(dtf,2);
				denominator2 += Math.pow(queryWeights.get(term),2);
			}		
			cosineScore = numerator/Math.sqrt(denominator1 * denominator2);
						
			finalResults.add(new Entry(entry.getKey(), cosineScore));			
		}
		
		Collections.sort(finalResults);
		
		return finalResults;
		
	}
	
	public double getAvdl() {
		return (double) totalDocLength / docSize.size();
	}
	
	public Map<Integer, Integer> getDocSize() {
		return docSize;
	}

	public void setDocSize(Map<Integer, Integer> docSize) {
		this.docSize = docSize;
	}

}
