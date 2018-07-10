package com.ir.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.codecs.TermStats;

import com.ir.model.Lucene;
import com.ir.model.Model;

public class QueryExpansion {
	private static List<File> queue = new ArrayList<File>();

	public static void init(Map<Integer, String> queryMap, Map<Integer, List<String>> results) {
		for(Entry<Integer, String> e : queryMap.entrySet()){
			List<String> rankedDocs = results.get(e.getKey());
			addToQueue(rankedDocs);
			Model m = new Lucene(queue, "index", "default");			
			try {
				m.create();
			} catch (IOException e1) {
				System.out.println("Error while creating index"+e1);
			}
		}
	}
	
	private static void addToQueue(List<String> rankedDocs){
		for(String file : rankedDocs){
			queue.add(new File("Corpus/"+file));
		}
	}
}
