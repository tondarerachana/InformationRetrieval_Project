package com.main.task2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ir.model.CosineSim;
import com.ir.model.Model;
import com.ir.tasks.PrecisionRecall;
import com.ir.tasks.TextProcessor;
import com.util.FileUtil;

/**
 * This is the main class for Task 2 - Implementation of PRF
 * 
 * @author annuraju
 *
 */
public class PseudoRelevance {
	
	public static void main(String[] args) throws IOException {
		
		// Read, process the raw htmls and generate the corpus
		File root = new File("cacm/");
		File[] list = root.listFiles();
		for (File file : list) {
			TextProcessor.generateCorpus(file);
		}
		// Add the files in the corpus to the queue
		FileUtil f = new FileUtil();
		f.addFiles(new File("Corpus/"));		
		List<File> queue = f.getQueue();
		String system = "PSR";
		
		// Create the desired ranking model
		Model model = new CosineSim(queue, false, system);
		model.create();
		
		// Parse the queries into a map
		File queries = new File("cacm.query");
		Map<Integer, String> queryMap = TextProcessor.parseQueries(queries, false);
		
		//Search the index for the queries
		model.search(queryMap, "index");
		
		Map<Integer, List<String>> results = model.getResults();	
		
		queryMap = model.expandQuery(queryMap, results);

		model.search(queryMap, "index");
	}
}
