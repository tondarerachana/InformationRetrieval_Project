package com.ir.tasks;

/**
 * Class that stores the docId, term frequency and score for 
 * a term
 * 
 * @author annuraju
 *
 */
public class Entry implements Comparable<Entry>{

	private int docId;
	private int termFrequency;
	private double score;
	
	public Entry(int docId, double score){
		this.docId = docId;
		this.score = score;
	}

	public Entry(int docId, int tf, double score){
		this.docId = docId;
		this.termFrequency = tf;
		this.score = score;
	}

	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getTermFrequency() {
		return termFrequency;
	}
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return this.docId + " " + this.score + " " + this.termFrequency;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(! (obj instanceof Entry)) return false;
		Entry score = (Entry) obj;
		return this.score == score.getScore();
	}

	@Override
	public int compareTo(Entry e) {
		if(this.score == e.getScore()) return 0;
		return (this.score > e.getScore()) ? -1: 1;
	}
}
