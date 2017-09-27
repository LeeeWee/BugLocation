package org.liwei.data;

import java.io.File;
import java.util.HashMap;

import org.nd4j.linalg.api.ndarray.INDArray;

public class CodeRepository {

	/**
	 * Relate each source file with a vector of features.
	 */
	private static HashMap<String, INDArray> codeVectors;
	/**
	 * Map each source file(path) to a metric in vector format.
	 */
	private static HashMap<String, CodeMetrics> codeMetricsSet;
	
	public CodeRepository(File indexFile, File vectorFile, File historyFile) {
		
	}
	
	/**
	 * Read code change history from a file, and attach it to the metrics set.
	 * @param historyFile The input history file
	 */
	private void attachHistory(File historyFile) {
		
	}
	
	/**
	 * For each source file(i.e. CodeMetrics object), attach related bugReport.
	 * @param brRepository
	 */
	public void attachRelatedDTS(BugReportRepository brRepository) {
		
	} 
}
