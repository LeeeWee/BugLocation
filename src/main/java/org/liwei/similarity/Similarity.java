package org.liwei.similarity;

import java.util.HashMap;
import java.util.Map.Entry;

import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.liwei.data.BugReport;
import org.liwei.data.CodeMetrics;
import org.liwei.util.DataTypeUtil.TokenScore;

public class Similarity {
	protected WordVectors vec;
	protected SortTokens brSort;
	protected SortTokens codeSort;
	protected TfidfVectorizer brTfidf;
	protected TfidfVectorizer codeTfidf;
	protected HashMap<String, HashMap<String, TokenScore>> brTopTokensMap;
	protected HashMap<String, HashMap<String, TokenScore>> codeTopTokensMap;
	
	/**
	 * Similarity between bug report and code, same words' similarity equals 1, otherwise 0,
	 * get bug report and code tokens from topTokensMap respectively.
	 * @param br input bug report
	 * @param metrics input code metrics
	 * @return similarity between bug report and code
	 */
	public double similarityBySameWords(BugReport br, CodeMetrics metrics) {
		HashMap<String, TokenScore> brTokensScoreMap = brTopTokensMap.get(br.getId());
		HashMap<String, TokenScore> codeTokensScoreMap = codeTopTokensMap.get(metrics.getPath());
		return similarityBySameWords(brTokensScoreMap, codeTokensScoreMap);
	}
	
	/**
	 * Similarity between two bug report, same words' similarity equals 1, otherwise 0,
	 * get bug report and code tokens from topTokensMap respectively.
	 * @param br0 input first bug report
	 * @param br1 input second bug report
	 * @return similarity between bug report0 and bug report1
	 */
	public double similarityBySameWords(BugReport br0, BugReport br1) {
		HashMap<String, TokenScore> brTokensScoreMap0 = brTopTokensMap.get(br0.getId());
		HashMap<String, TokenScore> brTokensScoreMap1 = brTopTokensMap.get(br1.getId());
		return similarityBySameWords(brTokensScoreMap0, brTokensScoreMap1);
	}

	/**
	 * Similarity between two tokensScoreMap
	 * @param tokensScoreMap0 input first tokensScoreMap
	 * @param tokensScoreMap1 input second tokensScoreMap
	 * @return Similarity between tokensScoreMap0 and tokensScoreMap1
	 */
	public double similarityBySameWords(HashMap<String, TokenScore> tokensScoreMap0,
			HashMap<String, TokenScore> tokensScoreMap1) {
		double sim0 = directedSimilarityBySameWords(tokensScoreMap0, tokensScoreMap1);
		double sim1 = directedSimilarityBySameWords(tokensScoreMap1, tokensScoreMap0);
		return (sim0 + sim1) / 2;
	}

	/**
	 * Directed similarity from tokensScoreMap0 to tokensScoreMap1
	 * @param tokensScoreMap0 input first tokensScoreMap
	 * @param tokensScoreMap1 input second tokensScoreMap
	 * @return directed similarity from tokensScoreMap0 to tokensScoreMap1
	 */
	public double directedSimilarityBySameWords(HashMap<String, TokenScore> tokensScoreMap0,
			HashMap<String, TokenScore> tokensScoreMap1) {
		double simSum = 0.0;
		double weights = 0.0;
		for (Entry<String, TokenScore> entry0 : tokensScoreMap0.entrySet()) {
			double sim = 0.0;
			if(tokensScoreMap1.containsKey(entry0.getKey()))
				sim = 1.0;
			simSum += sim;
			weights += entry0.getValue().score;
		}
		return simSum / weights;
	}
	
	public static class Builder {
		protected WordVectors vec;
		protected SortTokens brSort;
		protected SortTokens codeSort;
		protected TfidfVectorizer brTfidf;
		protected TfidfVectorizer codeTfidf;
		protected HashMap<String, HashMap<String, TokenScore>> brTopTokensMap;
		protected HashMap<String, HashMap<String, TokenScore>> codeTopTokensMap;

		public Builder() {
		}

		public Builder setWordVectors(WordVectors vec) {
			this.vec = vec;
			return this;
		}
		
		public Builder setBrSortTokens(SortTokens brSort) {
			this.brSort = brSort;
			return this;
		}
		
		public Builder setCodeSortTokens(SortTokens codeSort) {
			this.codeSort = codeSort;
			return this;
		}

		public Builder setBrTfidf(TfidfVectorizer brTfidf) {
			this.brTfidf = brTfidf;
			return this;
		}

		public Builder setCodeTfidf(TfidfVectorizer codeTfidf) {
			this.codeTfidf = codeTfidf;
			return this;
		}

		public Builder setBrTopTokensMap(HashMap<String, HashMap<String, TokenScore>> brTopTokensMap) {
			this.brTopTokensMap = brTopTokensMap;
			return this;
		}

		public Builder setCodeTopTokensMap(HashMap<String, HashMap<String, TokenScore>> codeTopTokensMap) {
			this.codeTopTokensMap = codeTopTokensMap;
			return this;
		}

	}
}
