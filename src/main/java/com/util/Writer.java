package com.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import com.ir.tasks.Measure;

/**
 * Utility class to write results into files
 * 
 * @author annuraju
 *
 */
public class Writer {

	/**
	 * Writes the raw content into a file
	 * 
	 * @param doc
	 * @param fileName
	 * @param url
	 * @throws IOException
	 */
	public static void docToFile(Document doc, String name, String url) throws IOException {
		File f = new File(name + ".txt");
		String s = doc.body().html();
		StringBuilder sb = new StringBuilder(url);
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		sb.append(s);
		FileUtils.writeStringToFile(f, sb.toString(), "UTF-8");
	}

	/**
	 * Writes the list of urls into a file along with appending the url as the
	 * first line
	 * 
	 * @param fileName
	 * @param uniqueUrls
	 * @throws IOException
	 */
	public static void urlToFile(String name, List<String> uniqueUrls) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(name)));
			for (String url : uniqueUrls) {
				bw.write(url);
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
	}

	public static void contentToFile(String docId, String content) throws IOException {
		BufferedWriter bw = null;
		try {
			int id = 0;
			if (docId.contains("-")) {
				id = Integer.parseInt(docId.substring(docId.indexOf("-") + 1, docId.indexOf(".html")));
			}
			docId = docId.substring(0, docId.lastIndexOf("/"))+"/"+String.valueOf(id);
			bw = new BufferedWriter(new FileWriter(new File(docId)));
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
	}

	public static void indexToFile(Map<String, Map<Integer, Integer>> index, String fileName) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileName)));
			bw.write("Term -> Entry");
			bw.newLine();
			bw.write("________________________________________________________________________");
			bw.newLine();
			for (Entry<String, Map<Integer, Integer>> e : index.entrySet()) {
				bw.write(e.getKey() + " ->  " + e.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}

	}

	public static void resultsToFile(List<com.ir.tasks.Entry> finalResults, int queryId, String system) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File("Results/"+system+":Q" + queryId)));
			bw.write("query_id	Q0	doc_id	rank Score	system_name	");
			bw.newLine();
			bw.write("___________________________________________________________________");
			bw.newLine();
			int i = 1;
			for (com.ir.tasks.Entry e : finalResults) {
				if (i != 100) {
					bw.write(queryId + " " + "A4 " + e.getDocId() + " " + i + " " + e.getScore() + " "+system);
					bw.newLine();
					i++;
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}

	}

	public static void tfToFile(List<Entry<String, Integer>> sortedTfs, String fileName) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileName)));
			bw.write("Term : Frequency");
			bw.newLine();
			bw.write("________________________________________________________________________");
			bw.newLine();
			for (Entry<String, Integer> e : sortedTfs) {
				bw.write(e.getKey() + " : " + e.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}

	}

	public static void docMapToFile(Map<Integer, String> docMap, String fileName) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileName)));
			bw.write("Id - Name");
			bw.newLine();
			bw.write("________________________________________________________________________");
			bw.newLine();
			for (Entry<Integer, String> e : docMap.entrySet()) {
				bw.write(e.getKey() + " - " + e.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}

	}

	public static void statsToFile(Map<Integer, Map<Integer, Measure>> evalResults, Map<Integer, List<String>> feedback,
			double sumReciprocalRank, double sumPrecision, String system) throws IOException {
		BufferedWriter bw = null, bw2 = null;
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);
		try {
			bw = new BufferedWriter(new FileWriter(new File("Results/Eval_" + system)));
			bw2 = new BufferedWriter(new FileWriter(new File("Results/PRTable_" + system)));
			bw2.write("QueryId System Rank Precision Recall");
			bw2.newLine();
			// Precision Recall table for the system
			for (Entry<Integer, Map<Integer, Measure>> e : evalResults.entrySet()) {
				for (Entry<Integer, Measure> m : e.getValue().entrySet()) {
					bw2.write(e.getKey() + " " + system + " " + m.getKey() + " "
							+ df2.format(m.getValue().getPrecision()) + " " + df2.format(m.getValue().getRecall()));
					bw2.newLine();
				}
			}

			// Summary of Stats for every query for the system
			for (Entry<Integer, Map<Integer, Measure>> e : evalResults.entrySet()) {
				bw.write("Query Id: " + e.getKey());
				bw.newLine();
				bw.write("Precision at 5: " + e.getValue().get(5).getPrecision());
				bw.newLine();
				bw.write("Precision at 20: " + e.getValue().get(20).getPrecision());
				bw.newLine();
				double avgPrecision = (double) sumPrecision / feedback.get(e.getKey()).size();
				bw.write("Average Precision: " + avgPrecision);
				bw.newLine();
				bw.write("Total Number of queries evaluated: " + evalResults.size());
				bw.newLine();
				bw.write("Mean Average Precision: " + avgPrecision / evalResults.size());
				bw.newLine();
				bw.write("Mean Reciprocal Rank: " + (double) sumReciprocalRank / evalResults.size());
				bw.newLine();
				bw.write("________________________________________________________________________");
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
			bw2.close();
		}
	}
}
