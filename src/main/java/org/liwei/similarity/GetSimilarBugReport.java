package org.liwei.similarity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.liwei.data.BugReport;
import org.liwei.data.BugReportRepository;
import org.liwei.data.SimilarBugReport;
import org.liwei.data.SimilarBugReport.SimilarityComparator;
import org.liwei.similarity.SortTokens.ScoreType;
import org.liwei.util.DataTypeUtil.TokenScore;

public class GetSimilarBugReport {

	private Similarity sim;
	private BugReportRepository brRepo;
	
	/**
	 * Constructor
	 * @param sim instance of Similarity
	 * @param brRepo input BugReportRepository
	 */
	public GetSimilarBugReport(Similarity sim, BugReportRepository brRepo) {
		this.sim = sim;
		this.brRepo = brRepo;
	}
	
	/**
	 * get top similar bug report for input br, if top <= 0, get all bug reports 
	 * @param br input bug report
	 * @param top input similar bug reports' size
	 * @return list of similarBugReport.
	 */
	public List<SimilarBugReport> getSimilarBugReport(BugReport br, int top) {
		List<SimilarBugReport> similarBugReports = new ArrayList<SimilarBugReport>();
		HashMap<String, BugReport> bugReports = brRepo.getBugReports();
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
	
	
	public static void main(String[] args) {
		String directory = "D:\\Data\\working";
		String testBrId = "424772";
		
		// initializing
		BugReportRepository brRepo = BugReportRepository.readBugReportRepository(directory);
		SortTokens brSort = new SortTokens(new File(directory, "bug_report.tfidf"),
				new File(directory, "stopwords.txt"));
		HashMap<String, HashMap<String, TokenScore>> brTopTokensMap = GetTopTokensMap
				.getBugReportTopTokens(brRepo.getBugReports(), brSort, ScoreType.IDF, 0);
		Similarity sim = new Similarity.Builder().setBrSortTokens(brSort).setBrTopTokensMap(brTopTokensMap).build();
		GetSimilarBugReport getSimilarBugReport = new GetSimilarBugReport(sim, brRepo);
		BugReport testBr = brRepo.get(testBrId);
		
		//get similar bug reports
		List<SimilarBugReport> similarBugReports = getSimilarBugReport.getSimilarBugReport(testBr, 10);
		System.out.println(testBrId + ": " + testBr.getText());
		for (SimilarBugReport similarBugReport : similarBugReports) {
			System.out.println(similarBugReport.similarity + " " + similarBugReport.getId() + ": " + similarBugReport.getText());
		}
	}
}
