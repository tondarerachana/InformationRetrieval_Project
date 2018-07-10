package com.ir.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.util.Writer;

public class TextProcessor {

	public static void generateCorpus(File file) {
		String rawText = getText(file);
		String processedText = processText(rawText);
		try {
			Writer.contentToFile("Corpus/"+file.getName(), processedText);
		} catch (IOException e) {
			System.out.println("Error while writing corpus to file: " + file.getName());
		}

	}

	private static String getText(File file) {
		File input = new File(file.getAbsolutePath());
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (IOException e) {
			System.out.println("Error while parsing the file: " + file.getAbsolutePath());
		}
		Elements preTags = doc.getElementsByTag("pre");
		StringBuilder sb = new StringBuilder();
		for (Element e : preTags) {
			sb.append(e.text());
		}
		return sb.toString();
	}

	private static String processText(String rawText) {

		// Case folding
		String processedText = rawText.toLowerCase();

		// Remove trailing full stops
		processedText = processedText.replaceAll("\\. ", " ");

		// Removes all punctuations except $.,-
		processedText = processedText.replaceAll("(?![,.:-[$]])\\p{Punct}", "");

		// Retains the $ around digits and ,. within digits
		processedText = processedText.replaceAll("(?<=[a-z])([[$],.]+)", " ");

		// Removes if more than 1 space
		processedText = processedText.replaceAll("\\s+", " ");
		
		return processedText;
	}

	public static Map<Integer, String> parseQueries(File queries, boolean useStopping) throws IOException {

		Map<Integer, String> queryMap = new HashMap<Integer, String>();
		File input = new File(queries.getAbsolutePath());
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (IOException e) {
			System.out.println("Error while parsing the file: " + queries.getAbsolutePath());
		}
		Elements docTags = doc.getElementsByTag("DOC");
		List<String> stopWords = getStopWords();
		String query = null;
		for (Element e : docTags) {
			String id = e.getElementsByTag("DOCNO").text();
			query = e.text().substring(e.text().indexOf(id) + id.length()).trim();
			query = TextProcessor.processText(query);
			if (useStopping) {
				String[] queryWords = query.split(" ");
				for (String word : queryWords) {
					if (stopWords.contains(word)) {
						query = query.replaceAll("\\b"+word+"\\b", "").trim();
					}
				}
			}
			query = query.replaceAll("\\bim\\b", "").trim();
			query = query.replaceAll("\\bid\\b", "").trim();
			query = query.replaceAll("\\s+", " ");
			queryMap.put(Integer.parseInt(id.trim()), query);
		}

		return queryMap;
	}

	public static List<String> getStopWords() throws IOException {

		BufferedReader br = null;
		List<String> stopWords = null;

		try {
			stopWords = new ArrayList<String>();
			br = new BufferedReader(new FileReader("common_words"));
			String line = null;
			while ((line = br.readLine()) != null) {
				stopWords.add(line.trim());
			}
			return stopWords;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			br.close();
		}

		return null;
	}

	@SuppressWarnings("null")
	public static Map<Integer, String> generateStemmedCorpus(String filename) throws IOException {
		Map<Integer, String> docMap = new HashMap<Integer, String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			int docId = 0;
			StringBuilder sb = null;
			while ((line = br.readLine()) != null) {
				if (line.contains("#")) {
					if (!docMap.containsKey(docId) && docId != 0) {
						docMap.put(docId, sb.toString().trim());
						Writer.contentToFile("Stem/"+String.valueOf(docId), sb.toString().trim());
					}
					docId = Integer.parseInt(line.substring(line.indexOf("# ") + 2));
					sb = new StringBuilder();
				} else {
					sb.append(line + " ");
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Error while reading stemmed corpus file" + e);
		} finally {
			br.close();
		}
		return docMap;
	}

	public static Map<Integer, String> parseStemmedQueries(String filename) throws IOException {
		Map<Integer, String> queryMap = new HashMap<Integer, String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			int i = 1;
			while ((line = br.readLine()) != null) {
				queryMap.put(i, line);
				i++;
			}
		} catch (Exception e) {
			System.out.println("Error while parsing stemmed query text" + e);
		} finally {
			br.close();
		}
		return queryMap;

	}
	
	public static Map<Integer, List<String>> getRelFeedback(String filename) throws IOException {
		Map<Integer, List<String>> feedback = new HashMap<Integer, List<String>>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			while ((line = br.readLine()) != null) {
				String words[] = line.split(" ");
				List<String> relDocs = null;
				int queryId = Integer.parseInt(words[0]);
				if(!feedback.containsKey(queryId)){
					relDocs = new ArrayList<String>();
				}
				else{
					relDocs = feedback.get(queryId);
				}

				relDocs.add(words[2].substring(words[2].indexOf("-")+1));
				feedback.put(queryId, relDocs);
			}
		}
	 catch (Exception e) {
		System.out.println("Error while parsing cacm relevance feedback" + e);
	} finally {
		br.close();
	}		
		return feedback;
	}		
}
