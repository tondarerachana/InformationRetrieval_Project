package com.main.task3;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ir.model.CosineSim;
import com.ir.model.Model;
import com.ir.tasks.TextProcessor;
import com.util.FileUtil;

public class Stemming {
	public static void main(String[] args) throws IOException {

		Map<Integer, String> stemmedCorpus = TextProcessor.generateStemmedCorpus("cacm_stem.txt");

		Map<Integer, String> stemmedQueries = TextProcessor.parseStemmedQueries("cacm_stem.query.txt");

		// Add the files in the corpus to the queue
		FileUtil f = new FileUtil();
		f.addFiles(new File("Corpus/"));
		List<File> queue = f.getQueue();
		String system = "Stemming";
		// Create the desired ranking model
		Model model = new CosineSim(queue, false, system);
		model.create();

		// Search the index for the queries
		model.search(stemmedQueries, "index");

	}
}
