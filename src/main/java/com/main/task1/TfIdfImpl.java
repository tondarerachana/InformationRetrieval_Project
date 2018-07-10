package com.main.task1;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ir.model.CosineSim;
import com.ir.model.Model;
import com.ir.model.TfIdf;
import com.ir.tasks.TextProcessor;
import com.util.FileUtil;

public class TfIdfImpl {
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
		
		// Create the desired ranking model
		Model model = new TfIdf(queue, false, "TfIdf");
		model.create();
		
		// Parse the queries into a map
		File queries = new File("cacm.query");
		Map<Integer, String> queryMap = TextProcessor.parseQueries(queries, false);
		
		//Search the index for the queries
		model.search(queryMap, "index");
	}

}
