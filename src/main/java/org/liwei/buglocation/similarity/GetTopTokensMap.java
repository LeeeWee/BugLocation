package org.liwei.buglocation.similarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.liwei.buglocation.data.BugReport;
import org.liwei.buglocation.data.CodeMetrics;
import org.liwei.buglocation.similarity.SortTokens.ScoreType;
import org.liwei.buglocation.utils.DataTypeUtil.TokenScore;

public class GetTopTokensMap {

	/**
	 * get top tokens of bug reports in bug report repository 
	 * @param brRepo input bug report repository
	 * @param brSort Instance of SortTokens initialized with bug reports tfidf model
	 * @param type input score type
	 * @param top input top tokens size 
	 * @return map of all bug report top tokens
	 */
	public static HashMap<String, HashMap<String, TokenScore>> getBugReportTopTokens(HashMap<String, BugReport> brRepo,
			SortTokens brSort, ScoreType type, int top) {
		HashMap<String, HashMap<String, TokenScore>> brTopTokensMap = new HashMap<String, HashMap<String, TokenScore>>();
		for (Entry<String, BugReport> entry : brRepo.entrySet()) {
			List<TokenScore> topTokensList = brSort.getTopTokensScore(entry.getValue().getText(), type, top);
			brTopTokensMap.put(entry.getKey(), tokenScoreListToHashMap(topTokensList));
		}
		return brTopTokensMap;
	}
	
	/**
	 * get top tokens of code files in code repository 
	 * @param codeMetricsSet input code metrics set
	 * @param codeSort Instance of SortTokens initialized with code tfidf model
	 * @param type input score type
	 * @param top input top tokens size 
	 * @return map of all code file top tokens
	 */
	public static HashMap<String, HashMap<String, TokenScore>> getCodeFileTopTokens(HashMap<String, CodeMetrics> codeMetricsSet,
			SortTokens codeSort, ScoreType type, int top) {
		HashMap<String, HashMap<String, TokenScore>> codeTopTokensMap = new HashMap<String, HashMap<String, TokenScore>>();
		for (Entry<String, CodeMetrics> entry : codeMetricsSet.entrySet()) {
			List<TokenScore> topTokensList = codeSort.getTopTokensScore(entry.getValue().getText(), type, top);
			codeTopTokensMap.put(entry.getKey(), tokenScoreListToHashMap(topTokensList));
		}
		return codeTopTokensMap;
	}
	
	/**
	 * convert tokenScore list to map
	 * @param tokenScoreList input tokenScore list
	 * @return map of tokenScore
	 */
	private static HashMap<String, TokenScore> tokenScoreListToHashMap(List<TokenScore> tokenScoreList) {
		HashMap<String, TokenScore> map = new HashMap<String, TokenScore>();
		for (TokenScore tokenScore : tokenScoreList) {
			map.put(tokenScore.token, tokenScore);
		}
		return map;
	}

}
