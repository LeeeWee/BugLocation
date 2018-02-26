package org.liwei.buglocation.tests;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.liwei.buglocation.data.BugReport;
import org.liwei.buglocation.data.BugReportRepository;
import org.liwei.buglocation.data.CodeRepository;
import org.liwei.buglocation.similarity.GetTopTokensMap;
import org.liwei.buglocation.similarity.Similarity;
import org.liwei.buglocation.similarity.SimilarityCalculator;
import org.liwei.buglocation.similarity.SortTokens;
import org.liwei.buglocation.similarity.SortTokens.ScoreType;
import org.liwei.buglocation.utils.DataTypeUtil.FileScore;
import org.liwei.buglocation.utils.DataTypeUtil.TokenScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimilarCodeFileTest {
	
	private static Logger logger = LoggerFactory.getLogger(SimilarBugReportTest.class);
	
	private static final String BR_TFIDF_PATH = "bug_report.tfidf";
	private static final String GOOD_TFIDF_PATH = "good.tfidf";
	private static final String STOPWORDS_PATH = "stopwords.txt";
	
	private static final ScoreType SCORE_TYPE = ScoreType.TF_IDF;
	
	private static BugReportRepository brRepository;
	private static CodeRepository codeRepository;
	private static SortTokens brSort;
	private static SortTokens codeSort;
	private static HashMap<String, HashMap<String, TokenScore>> brTopTokensMap;
	private static HashMap<String, HashMap<String, TokenScore>> codeTopTokensMap;
	private static Similarity sim;
	public static SimilarityCalculator simGenerator;

	public static void main(String[] args) {
		initialize();
		allBugReportsTest();
	}
	
	/**
	 * initialize all variables
	 */
	public static void initialize() {
		String directory = "D:\\Data\\working";
		
		brRepository = BugReportRepository.readBugReportRepository(directory);
		codeRepository = CodeRepository.readCodeRepository(directory);
		brSort = new SortTokens(new File(directory, BR_TFIDF_PATH), new File(directory, STOPWORDS_PATH));
		codeSort = new SortTokens(new File(directory, GOOD_TFIDF_PATH), new File(directory, STOPWORDS_PATH));
		brTopTokensMap = GetTopTokensMap.getBugReportTopTokens(brRepository.getBugReports(), brSort, SCORE_TYPE, 0);
		codeTopTokensMap = GetTopTokensMap.getCodeFileTopTokens(codeRepository.getCodeMetricsSet(), codeSort, SCORE_TYPE, 0);
		sim = new Similarity.Builder()
				.setBrTopTokensMap(brTopTokensMap)
				.setCodeTopTokensMap(codeTopTokensMap)
				.build();
		simGenerator = new SimilarityCalculator(sim).setBugReports(brRepository.getBugReports()).setCodeMetricsSet(codeRepository.getCodeMetricsSet());
	}
	
	public static void allBugReportsTest() {
		HashMap<String, BugReport> bugReports = brRepository.getBugReports();
		int hit5 = 0, hit10 = 0, hit15 = 0, hit20 = 0, hit100 = 0;
		int count = 0;
		for (BugReport bugReport : bugReports.values()) {
			count++;
			if (count % 200 == 0)
				logger.info(count + "bug reports handled.");
			List<FileScore> similarCodeFile = simGenerator.getSimilarCodeFile(bugReport, 100);
			int i = 0;
			for (FileScore file : similarCodeFile) {
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
