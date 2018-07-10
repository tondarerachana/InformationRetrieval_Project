package com.ir.tasks;

import java.util.HashSet;
import java.util.Set;

/**
 *  Class that stores the list of documents and 
 *  doc frequency for a term
 *  
 * @author annuraju
 *
 */
public class DocFrequency{

	private Set<Integer> docs;
	private int df; // doc frequency
	
	public DocFrequency() {
		docs = new HashSet<Integer>();
	}
	
	public Set<Integer> getDocs() {
		return docs;
	}
	public void setDocs(Set<Integer> docs) {
		this.docs = docs;
	}
	public int getDf() {
		return df;
	}
	public void setDf(int df) {
		this.df = df;
	}
	
	public void addDocumentToSet(int docId){
		docs.add(docId);
		setDf(docs.size());
	}
	
}
