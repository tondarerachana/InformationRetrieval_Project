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

public class EvalStemming {
	
	public static void main(String[] args) throws IOException {
		
		Map<Integer, String> stemmedCorpus = TextProcessor.generateStemmedCorpus("cacm_stem.txt");
		
		Map<Integer, String> stemmedQueries = TextProcessor.parseStemmedQueries("cacm_stem.query.txt");
		
		// Add the files in the corpus to the queue
		FileUtil f = new FileUtil();
		f.addFiles(new File("Stem/"));		
		List<File> queue = f.getQueue();

		// Create the desired ranking model
		Model model = new CosineSim(queue, false, "Cosine");
		model.create();
		
		//Search the index for the queries
		model.search(stemmedQueries, "index");

		String system = "Stemming";

		Map<Integer, List<String>> results = model.getResults();	

		//Read cacm_relevant judgement file
		Map<Integer, List<String>> relFeedback = TextProcessor.getRelFeedback("cacm.rel");		
	
		PrecisionRecall pr = new PrecisionRecall(system);
		pr.calculateStats(results, relFeedback);
		
		System.out.println("Evaluation has been completed. Check the Eval_"+system+" file in the results folder");


	}

}
