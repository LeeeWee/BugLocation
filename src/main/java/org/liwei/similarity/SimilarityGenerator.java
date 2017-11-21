package org.liwei.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import org.liwei.data.BugReport;
import org.liwei.data.CodeMetrics;
import org.liwei.data.SimilarBugReport;
import org.liwei.data.SimilarBugReport.SimilarityComparator;
import org.liwei.util.DataTypeUtil.FileScore;
import org.liwei.util.DataTypeUtil.FileScoreComparator;

public class SimilarityGenerator {
	
	/**
	 * Instance of Similarity used to calculated similarity.
	 */
	protected Similarity sim;
	
	/**
	 * Bug reports in bug report repository.
	 */
	protected HashMap<String, BugReport> bugReports;
	
	/**
	 * Code metrics in code repository.
	 */
	protected HashMap<String, CodeMetrics> codeMetricsSet;
	
	public SimilarityGenerator(Similarity sim) {
		this.sim = sim;
	}
	
	public SimilarityGenerator setBugReports(HashMap<String, BugReport> bugReports) {
		this.bugReports = bugReports;
		return this;
	}
	
	public SimilarityGenerator setCodeMetricsSet(HashMap<String, CodeMetrics> codeMetricsSet) {
		this.codeMetricsSet = codeMetricsSet;
		return this;
	}
	
	/**
	 * get top similar bug report for input br, if top <= 0, get all bug reports 
	 * @param br input bug report
	 * @param top input similar bug reports' size
	 * @return list of similarBugReport.
	 */
	public List<SimilarBugReport> getSimilarBugReports(BugReport br, int top) {
		List<SimilarBugReport> similarBugReports = new ArrayList<SimilarBugReport>();
		PriorityQueue<SimilarBugReport> heap = new PriorityQueue<SimilarBugReport>(bugReports.size(), new SimilarityComparator());
		for (Entry<String, BugReport> entry : bugReports.entrySet()) {
			if (entry.getKey().equals(br.getId()))
				continue;
			double similarity = sim.similarityBySameWords(br, entry.getValue());
			heap.add(new SimilarBugReport(entry.getValue(), similarity));
		}
		if (top <= 0) {
			while (!heap.isEmpty()) 
				similarBugReports.add(heap.poll());
		} else {
			int left = top;
			while (left >= 0 && (!heap.isEmpty())) {
				similarBugReports.add(heap.poll());
				left--;
			}
		}
		return similarBugReports;
	}
	
	/**
	 * get top similar code file for input br, if top <= 0, get all code file
	 * @param br input bug report
	 * @param top input similar code file size
	 * @return list of similar code file.
	 */
	public List<FileScore> getSimilarCodeFile(BugReport br, int top) {
		List<FileScore> similarCodeFile = new ArrayList<FileScore>();
		PriorityQueue<FileScore> heap = new PriorityQueue<FileScore>(codeMetricsSet.size(), new FileScoreComparator());
		for (Entry<String, CodeMetrics> entry : codeMetricsSet.entrySet()) {
			double similarity = sim.similarityBySameWords(br, entry.getValue());
			heap.add(new FileScore(entry.getKey(), similarity));
		}
		if (top <= 0) {
			while (!heap.isEmpty())
				similarCodeFile.add(heap.poll());
		} else {
			int left = top;
			while (left >= 0 && (!heap.isEmpty())) {
				similarCodeFile.add(heap.poll());
				left--;
			}
		}
		return similarCodeFile;
	}
	
	
	
}
