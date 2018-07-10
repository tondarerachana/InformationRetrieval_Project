package com.ir.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Lucene implements Model {
	List<File> queue = new ArrayList<File>();
	private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
	private IndexWriter writer;
	private String model;
	private Map<Integer, List<String>> results;

	public Lucene() {
	}

	public Lucene(List<File> queue, String indexDir, String model) {
		this.queue = queue;
		this.results = new HashMap<Integer, List<String>>();

		FSDirectory dir = null;
		try {
			if(new File(indexDir).exists()){
				new File(indexDir).delete();
			}
			dir = FSDirectory.open(new File(indexDir));
		} catch (IOException e1) {
			System.out.println("Error while opening the index directory:" + e1);
		}
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, sAnalyzer);
		try {
			writer = new IndexWriter(dir, config);
		} catch (IOException e) {
			System.out.println("Error while creating index: " + e);
		}

		this.model = model;
	}

	public void create() throws IOException {
		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			FileReader fr = null;
			try {
				Document doc = new Document();

				// ===================================================
				// add contents of file
				// ===================================================
				fr = new FileReader(f);
				doc.add(new TextField("contents", fr));
				doc.add(new StringField("path", f.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", f.getName(), Field.Store.YES));

				writer.addDocument(doc);
				System.out.println("Added: " + f);
			} catch (Exception e) {
				System.out.println("Could not add: " + f);
			} finally {
				fr.close();
			}
		}

		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("************************");
		System.out.println((newNumDocs - originalNumDocs) + " documents added.");
		System.out.println("************************");
		queue.clear();
	}

	/**
	 * Close the index.
	 * 
	 * @throws java.io.IOException
	 *             when exception closing
	 */
	public void closeIndex() throws IOException {
		writer.close();
	}

	public void search(Map<Integer, String> queryMap, String indexDir) throws IOException {
		BufferedWriter bw = null;
		List<String> docList = null;
		for (Entry<Integer, String> e : queryMap.entrySet()) {
			try {
				if(model.equals("BM25"))
					bw = new BufferedWriter(new FileWriter(new File("Results/BM25:Q" + e.getKey())));
				else
					bw = new BufferedWriter(new FileWriter(new File("Results/Lucene:Q" + e.getKey())));
				bw.write("query_id	Q0	doc_id	rank CosineSim_score	system_name	");
				bw.newLine();
				IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
				IndexSearcher searcher = new IndexSearcher(reader);
				if(this.model.equals("BM25")){
					searcher.setSimilarity(new BM25Similarity());
				}
				TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
				Query q = new QueryParser(Version.LUCENE_47, "contents", sAnalyzer).parse(QueryParser.escape(e.getValue()));
				searcher.search(q, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;
				// 4. display results
				docList = new ArrayList<String>();

				System.out.println("Found " + hits.length + " hits.");
				for (int i = 0; i < hits.length; ++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					String docName = d.get("filename").replace(".txt", "");
					docList.add(docName);
					bw.write(e.getKey() + " " + "A4 " + docName + " " + (i + 1) + " " + hits[i].score + " "+model);
					bw.newLine();
				}
				results.put(e.getKey(), docList);			
			} catch (IOException ex) {

			} catch (ParseException e1) {
				e1.printStackTrace();
			} finally {
				bw.close();
			}
		}
	}

	public Map<Integer, List<String>> getResults() {
		return results;
	}

	public void setResults(Map<Integer, List<String>> results) {
		this.results = results;
	}
	
	@Override
	public Map<Integer, String> expandQuery(Map<Integer, String> queryMap, Map<Integer, List<String>> results) {
		// TODO Auto-generated method stub	
		return null;
	}
	
}
