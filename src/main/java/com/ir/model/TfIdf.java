package com.ir.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ir.tasks.DocFrequency;
import com.ir.tasks.Entry;
import com.ir.tasks.Indexer;
import com.util.Writer;

public class TfIdf implements Model {

	List<File> queue = new ArrayList<File>();
	private boolean useStopping;
	private String system;

	private static Map<String, Map<Integer, Entry>> invertedIndex;
	private static Map<String, DocFrequency> docFrequencies;
	private static Map<String, Integer> termFrequencies;

	List<java.util.Map.Entry<String, Integer>> sortedTfs;
	private Map<Integer, List<String>> results;

	public TfIdf() {

	}

	public TfIdf(List<File> queue, boolean useStopping, String system) {
		this.queue = queue;
		this.useStopping = useStopping;
		this.system = system;
		this.results = new HashMap<Integer, List<String>>();

	}

	public void create() throws IOException {
		Indexer indexer = new Indexer(1, useStopping);
		for (File f : queue) {
			indexer.create(f.getAbsolutePath());
		}

		invertedIndex = indexer.getInvertedIndex();
		docFrequencies = indexer.getDocFrequencies();
		termFrequencies = indexer.getTermFrequencies();
		Map<String, Integer> unsortedTfs = indexer.getTermFrequencies();

		sortedTfs = new ArrayList<java.util.Map.Entry<String, Integer>>(unsortedTfs.entrySet());

		Collections.sort(sortedTfs, new Comparator<java.util.Map.Entry<String, Integer>>() {
			public int compare(java.util.Map.Entry<String, Integer> e1, java.util.Map.Entry<String, Integer> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});
	}

	public void search(Map<Integer, String> queryMap, String indexDir) {
		for(java.util.Map.Entry<Integer, String> e : queryMap.entrySet()){
			Map<Integer, Double> docScores = new HashMap<Integer, Double>();
			List<Entry> finalResults = new ArrayList<Entry>();
			double totalScore = 0.0;
			String[] queryTerms = e.getValue().split(" ");
			for(String term : queryTerms){
				int tf = 0;
				double score = 0.0;
				int df =  !docFrequencies.containsKey(term) ? 0 : docFrequencies.get(term).getDf();
				if(df ==0)
					continue;
				double idf = Math.log(3204/df);

				Map<Integer, Entry> docList = invertedIndex.get(term);

				for(java.util.Map.Entry<Integer, Entry> d : docList.entrySet()){
					tf = d.getValue().getTermFrequency();
					score = (double) (tf * idf);
					if(!docScores.containsKey(d.getKey())){
						docScores.put(d.getKey(), score);
					}
					else{
						docScores.put(d.getKey(), docScores.get(d.getKey())+score);
					}
				}
				
			}
			
			List<java.util.Map.Entry<Integer, Double>> sortedDocs = new ArrayList<java.util.Map.Entry<Integer, Double>>(docScores.entrySet());

			Collections.sort(sortedDocs, new Comparator<java.util.Map.Entry<Integer, Double>>() {
				@Override
				public int compare(java.util.Map.Entry<Integer, Double> o1, java.util.Map.Entry<Integer, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			int i =0;
			List<String> docList = new ArrayList<String>();
			for(java.util.Map.Entry<Integer, Double> s : sortedDocs){
				finalResults.add(new Entry(s.getKey(), s.getValue()));
				docList.add(String.valueOf(s.getKey()));
				i++;
				if(i>100){
					break;
				}
			}
			results.put(e.getKey(),docList);
			try {
				Writer.resultsToFile(finalResults, e.getKey(), system);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		}
	}

	public void closeIndex() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<Integer, String> expandQuery(Map<Integer, String> queryMap, Map<Integer, List<String>> results) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Integer, List<String>> getResults() {
		return results;
	}

	public void setResults(Map<Integer, List<String>> results) {
		this.results = results;
	}

}
