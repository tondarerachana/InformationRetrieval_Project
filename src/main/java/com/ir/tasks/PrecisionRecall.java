package com.ir.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.util.Writer;

public class PrecisionRecall {
	
	private String system;
	
	public PrecisionRecall(String system) {
		this.system = system;
	}

	public void calculateStats(Map<Integer, List<String>> results, Map<Integer, List<String>> feedback) throws IOException{
		Map<Integer, Map<Integer, Measure>> evalResults = new HashMap<Integer, Map<Integer, Measure>>();

		int rank = 0, totalDocRelevant = 0, numRelevantSoFar = 0;
		double sumReciprocalRank = 0, sumPrecision = 0;

		for(java.util.Map.Entry<Integer, List<String>> e : results.entrySet()){
			rank = 0;
			boolean flag = false;
			double reciprocalRank = 0;
			numRelevantSoFar = 0;
			sumPrecision = 0;
			if(feedback.containsKey(e.getKey())){
				totalDocRelevant = feedback.get(e.getKey()).size();
			}
			else{
				continue;
			}
			List<String> retDocs = e.getValue();
			List<String> relDocs = feedback.get(e.getKey());
			for(int i = 0; i<retDocs.size(); i++){
				rank = i+1;
				if(relDocs.contains(retDocs.get(i))){
					numRelevantSoFar += 1;
					sumPrecision += (double) numRelevantSoFar/rank;
					if(!flag){
						reciprocalRank = (double)1/rank;
						sumReciprocalRank +=reciprocalRank; 
						flag = true;
					}				
				}
				double precision = (double) numRelevantSoFar/rank;
				double recall = (double) numRelevantSoFar/totalDocRelevant;
				Map<Integer, Measure> evalMeasures = null;
				if(!evalResults.containsKey(e.getKey())){
					evalMeasures = new HashMap<Integer, Measure>();
				}
				else{
					evalMeasures = evalResults.get(e.getKey());
				}
				evalMeasures.put(rank, new Measure(precision, recall));
				evalResults.put(e.getKey(), evalMeasures);
				
			}		
		}		
		Writer.statsToFile(evalResults, feedback,sumReciprocalRank, sumPrecision, system);
	}

}
