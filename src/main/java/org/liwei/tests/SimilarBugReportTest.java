package org.liwei.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.liwei.data.BugReport;
import org.liwei.data.BugReportRepository;
import org.liwei.data.SimilarBugReport;
import org.liwei.similarity.GetTopTokensMap;
import org.liwei.similarity.Similarity;
import org.liwei.similarity.SimilarityCalculator;
import org.liwei.similarity.SortTokens;
import org.liwei.similarity.SortTokens.ScoreType;
import org.liwei.util.DataTypeUtil.FileScore;
import org.liwei.util.DataTypeUtil.FileScoreComparator;
import org.liwei.util.DataTypeUtil.TokenScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.liwei.util.Index;

public class SimilarBugReportTest {
	private static Logger logger = LoggerFactory.getLogger(SimilarBugReportTest.class);

	private static final String BR_TFIDF_PATH = "bug_report.tfidf";
	private static final String GOOD_TFIDF_PATH = "good.tfidf";
	private static final String STOPWORDS_PATH = "stopwords.txt";
	private static final ScoreType SCORE_TYPE = ScoreType.TF_IDF;
	private static final int TOP_SIMILAR_BUG_REPORTS = 100;
	
	public static BugReportRepository brRepo;
	public static SortTokens brSort;
	public static HashMap<String, HashMap<String, TokenScore>> brTopTokensMap;
	public static Similarity sim;
	public static SimilarityCalculator simGenerator;
	
	public static void main(String[] args) {
		initialize();
		allBugReportsTest();
	}
	
	/**
	 * initialize all variables
	 */
	public static void initialize() {
		String directory = "D:\\data\\working";
		brRepo = BugReportRepository.readBugReportRepository(directory);
		brSort = new SortTokens(new File(directory, BR_TFIDF_PATH), new File(directory, STOPWORDS_PATH));
		brTopTokensMap = GetTopTokensMap.getBugReportTopTokens(brRepo.getBugReports(), brSort, SCORE_TYPE, 0);
		sim = new Similarity.Builder()
				.setBrTopTokensMap(brTopTokensMap)
				.build();
		simGenerator = new SimilarityCalculator(sim).setBugReports(brRepo.getBugReports());

	}
	
	/**
	 * get top candidate files for given bug report
	 * @param br input bug report
	 * @param top candidate files size
	 * @return top candidate files for given bug report
	 */
	public static List<FileScore> getCandidateFiles(BugReport br, int top) {
		List<FileScore> candidateFiles = new ArrayList<FileScore>();
		PriorityQueue<FileScore> heap = new PriorityQueue<FileScore>(300, new FileScoreComparator());
		HashMap<String, Double> fileScoreMap = new HashMap<String, Double>();
		// update file socre map
		List<SimilarBugReport> similarBugReports = simGenerator.getSimilarBugReports(br, TOP_SIMILAR_BUG_REPORTS);
		for (SimilarBugReport similarBugReport : similarBugReports) {
			for (Index index : similarBugReport.getModifiedFiles()) {
				String path = index.getPath();
				if (!fileScoreMap.containsKey(path)) 
					fileScoreMap.put(path, 0.0);
				fileScoreMap.put(path, fileScoreMap.get(path) + similarBugReport.similarity);
			}
		}
		// sort files
		for (Entry<String, Double> entry : fileScoreMap.entrySet()) {
			heap.add(new FileScore(entry.getKey(), entry.getValue()));
		}
		int left = top;
		while ((!heap.isEmpty()) && left > 0) {
			candidateFiles.add(heap.poll());
			left--;
		}
		return candidateFiles;  
	}
	
	public static void allBugReportsTest() {
		HashMap<String, BugReport> bugReports = brRepo.getBugReports();
		int hit5 = 0, hit10 = 0, hit15 = 0, hit20 = 0, hit100 = 0;
		int count = 0;
		for (BugReport bugReport : bugReports.values()) {
			count++;
			if (count % 200 == 0)
				logger.info(count + "bug reports handled.");
			List<FileScore> candidateFiles = getCandidateFiles(bugReport, 100);
			int i = 0;
			for (FileScore file : candidateFiles) {
				if (bugReport.isModified(file.file)) {
					if (i < 5) 
						hit5++;
					if (i < 10)
						hit10++;
					if (i < 15)
						hit15++;
					if (i < 20)
						hit20++;
					hit100++;
				}
				i++;
			}
		}
		int total = bugReports.size();
		System.out.println("hit5: " + hit5 + "/" + total + " = " + (double)hit5/total);
		System.out.println("hit5: " + hit10 + "/" + total + " = " + (double)hit10/total);
		System.out.println("hit5: " + hit15 + "/" + total + " = " + (double)hit15/total);
		System.out.println("hit5: " + hit20 + "/" + total + " = " + (double)hit20/total);
		System.out.println("hit5: " + hit100 + "/" + total + " = " + (double)hit100/total);
	}
}
