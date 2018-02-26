package org.liwei.buglocation.tests;

import java.io.File;
import java.util.HashMap;

import org.liwei.buglocation.data.BugReportRepository;
import org.liwei.buglocation.data.CodeRepository;
import org.liwei.buglocation.genModel.SimilarityMatrixGenerator;
import org.liwei.buglocation.similarity.GetTopTokensMap;
import org.liwei.buglocation.similarity.Similarity;
import org.liwei.buglocation.similarity.SortTokens;
import org.liwei.buglocation.similarity.SortTokens.ScoreType;
import org.liwei.buglocation.utils.DataTypeUtil.TokenScore;

public class SimilarityMatrixTest {
	
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
	
	private static SimilarityMatrixGenerator generator;
	
	static String directory;
	
	public static void main(String[] args) {
		directory = "D:\\Data\\working";
		String output = "D:\\Data\\working\\similarity_matrix_test.txt"; 
		String parametersPath = "D:\\Data\\working\\parameters.txt";
		
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
		generator = new SimilarityMatrixGenerator(brRepository, codeRepository, sim);
		
		generator.generateRankingMatrix(new File(output), true);
		generator.saveParameters(new File(parametersPath));
	}
	
}
