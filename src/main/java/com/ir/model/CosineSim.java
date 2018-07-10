package com.ir.model;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ir.tasks.DocFrequency;
import com.ir.tasks.Entry;
import com.ir.tasks.Indexer;
import com.ir.tasks.TextProcessor;
import com.util.Writer;

public class CosineSim implements Model{
	
	private static List<File> queue = new ArrayList<File>();
	private static Map<String, Map<Integer, Entry>> invertedIndex;
	private static Map<String, DocFrequency> docFrequencies;
	List<java.util.Map.Entry<String,Integer>> sortedTfs;
	private Map<Integer, List<String>> results;
	private boolean useStopping;
	private String system;
	
	public CosineSim(){
		
	}
	
	public CosineSim(List<File> queue, boolean useStopping, String system){
		this.system = system;
		this.useStopping = useStopping;
		this.queue = queue;
		this.results = new HashMap<Integer, List<String>>();
	}

	public void create() throws IOException {
		Indexer indexer = new Indexer(1, useStopping);
		for(File f : queue){
			indexer.create(f.getAbsolutePath());
		}
		
		invertedIndex = indexer.getInvertedIndex();
		docFrequencies = indexer.getDocFrequencies();
		Map<String, Integer> unsortedTfs = indexer.getTermFrequencies();
		
        sortedTfs = new ArrayList<java.util.Map.Entry<String,Integer>>(unsortedTfs.entrySet());

        Collections.sort(sortedTfs, 
                new Comparator<java.util.Map.Entry<String,Integer>>() {
                    public int compare(java.util.Map.Entry<String,Integer> e1, java.util.Map.Entry<String,Integer> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );
	}

	public void search(Map<Integer, String> queryMap, String indexDir) throws IOException {
		for(java.util.Map.Entry<Integer, String> e : queryMap.entrySet()){
			List<String> docList = new ArrayList<String>();
			Indexer indexer = new Indexer(1, useStopping);
			Map<String, Double> queryWeights = getQueryWeights(e.getValue());
			List<Entry> finalResults = indexer.calculateScore(queryWeights, e.getValue(), e.getKey(),invertedIndex);
			if(finalResults.size() < 100){
				for(int i=0; i<finalResults.size(); i++){
					docList.add(String.valueOf(finalResults.get(i).getDocId()));
				}
			}
			else{
				for(int i=0; i<100; i++){
					docList.add(String.valueOf(finalResults.get(i).getDocId()));
				}
			}
			results.put(e.getKey(), docList);
			try {
				Writer.resultsToFile(finalResults, e.getKey(), system);
			} catch (IOException e1) {
				System.out.println("Error while writing results to file for query: "+ e.getValue() + " " +e1);
			}
		}		
	}
	
	/**
	 * Get weights for each term in the queries
	 * 
	 * @param query
	 * @param dfs
	 * @param index
	 * @return
	 */
	private static Map<String, Double> getQueryWeights(String query){		
		Map<String, Double> queryWeights = new HashMap<String, Double>();
		DecimalFormat df2 = new DecimalFormat(".##");
		df2.setRoundingMode(RoundingMode.FLOOR);
		String[] terms = query.split(" ");
		for(int i=0; i<terms.length; i++){
			int tf = 0;
			int df = 0;
			double idf = 0.0;
			if(invertedIndex.containsKey(terms[i])){
				tf = 1;
				df = docFrequencies.get(terms[i]).getDf();
				idf = Math.log10(3204/df);
			}
			double termWeight = 0.0;
			if(tf != 0)
				termWeight = 1 + Math.log10(tf);
			
			queryWeights.put(terms[i], Double.valueOf(df2.format(termWeight * idf)));
		}
		return queryWeights;
	}

	public void closeIndex() {
		
	}
	
	public Map<Integer, List<String>> getResults() {
		return results;
	}

	public void setResults(Map<Integer, List<String>> results) {
		this.results = results;
	}

	@Override
	public Map<Integer, String> expandQuery(Map<Integer, String> queryMap, Map<Integer, List<String>> results) {
		for(java.util.Map.Entry<Integer, String> e : queryMap.entrySet()){
			List<String> rankedDocs = results.get(e.getKey());
			queue.clear();
			addToQueue(rankedDocs);
			Model m = new CosineSim(queue, useStopping, system);
			try {
				m.create();
				
				List<String> stopWords = TextProcessor.getStopWords();
				
				List<String> words = new ArrayList<String>();
				
				for(java.util.Map.Entry<String,Integer> tf :sortedTfs){
					if(stopWords.contains(tf.getKey()) || isNumeric(tf.getKey()) || e.getValue().contains(tf.getKey())
							|| !(tf.getKey().length() > 2) || tf.getKey().equals("cacm"))
						continue;
					else{
						words.add(tf.getKey());
					}
				}
				List<String> queryWords = words.subList(0, 10);
				StringBuilder sb = new StringBuilder();
				for(String q : queryWords){
					sb.append(q+" ");
				}
				queryMap.put(e.getKey(), e.getValue()+" "+sb.toString());		
			} catch (IOException e2) {
				System.out.println("Error while creating model: "+e2);
			}
		}
		return queryMap;
	}
	
	private static boolean isNumeric(String term){
		return term.matches("-?\\d+(\\.\\d+)?") || term.matches("-?\\d+([,]\\d+)*(\\.)?\\d+([,]\\d+)*");
	}
	
	private static void addToQueue(List<String> rankedDocs){
		int i = 0;
		for(String file : rankedDocs){
			if(i == 10)
				break;
			queue.add(new File("Corpus/"+file));
			i++;
		}
	}

}
