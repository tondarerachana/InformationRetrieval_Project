package com.ir.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Model {

	public void create() throws IOException;
	public void search(Map<Integer, String> queryMap, String indexDir) throws IOException;
	public void closeIndex() throws IOException;	
	public Map<Integer, List<String>> getResults();
	public Map<Integer, String> expandQuery(Map<Integer, String> queryMap, Map<Integer, List<String>> results);
}
