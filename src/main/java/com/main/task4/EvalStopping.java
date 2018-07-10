package com.main.task4;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ir.model.CosineSim;
import com.ir.model.Model;
import com.ir.tasks.PrecisionRecall;
import com.ir.tasks.TextProcessor;
import com.util.FileUtil;

public class EvalStopping {
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
	
	List<String> stopWords = TextProcessor.getStopWords();
	
	boolean stop = true;

	// Create the desired ranking model
	Model model = new CosineSim(queue, stop, "Cosine");
	model.create();
	
	// Parse the queries into a map
	File queries = new File("cacm.query");
	Map<Integer, String> queryMap = TextProcessor.parseQueries(queries, true);
	
	//Search the index for the queries
	model.search(queryMap, "index");
	
	Map<Integer, List<String>> results = model.getResults();	
	
	//Read cacm_relevant judgement file
	Map<Integer, List<String>> relFeedback = TextProcessor.getRelFeedback("cacm.rel");		

	String system = "Stopping";
	PrecisionRecall pr = new PrecisionRecall(system);
	pr.calculateStats(results, relFeedback);
	
	System.out.println("Evaluation has been completed. Check the Eval_"+system+" file in the results folder");

	
	}

}
