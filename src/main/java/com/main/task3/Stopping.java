package com.main.task3;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ir.model.CosineSim;
import com.ir.model.Model;
import com.ir.tasks.TextProcessor;
import com.util.FileUtil;

public class Stopping {

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
		String system = "Stopping";

		boolean stop = true;

		// Create the desired ranking model
		Model model = new CosineSim(queue, stop, system);
		model.create();

		// Parse the queries into a map
		File queries = new File("cacm.query");
		Map<Integer, String> queryMap = TextProcessor.parseQueries(queries, true);

		// Search the index for the queries
		model.search(queryMap, "index");

		Map<Integer, List<String>> results = model.getResults();

	}
}
