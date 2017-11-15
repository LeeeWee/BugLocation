package org.liwei.similarity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.MathUtils;
import org.deeplearning4j.util.SerializationUtils;
import org.liwei.util.DataTypeUtil.TokenScore;
import org.liwei.util.DataTypeUtil.TokenScoreComparator;
import org.liwei.util.FileUtil;
import org.liwei.util.StopWordsPreprocessor;

/**
 * Sort tokens of text by tokens score. 
 * @author Liwei
 */
public class SortTokens {
	
	private TfidfVectorizer tfidf;
	private List<String> stopWords;
	private TokenizerFactory tokenizerFactory;
	
	public enum ScoreType {
		TF_IDF, IDF
	}
	
	/**
	 * Constructor
	 * @param tfidfModelFile input tfidf model file
	 * @param stopWordsFile input stopWords file
	 */
	public SortTokens(File tfidfModelFile, File stopWordsFile) {
		this.tfidf = loadTfidfModel(tfidfModelFile);
		this.stopWords = FileUtil.asList(stopWordsFile);
		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new StopWordsPreprocessor(stopWords));
	}
	
	/**
	 * Constructor
	 * @param tfidf input tfidf vectorizer
	 * @param stopWords input stopwords list
	 */
	public SortTokens(TfidfVectorizer tfidf, List<String> stopWords) {
		this.tfidf = tfidf;
		this.stopWords = stopWords;
		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new StopWordsPreprocessor(stopWords));
	}
	
	public static TfidfVectorizer loadTfidfModel(File tfidfModelFile) {
		TfidfVectorizer tfidf = SerializationUtils.readObject(tfidfModelFile);
		return tfidf;
	}
	
	/**
	 * convert tokensScoreList to string
	 * @param tokensScoreList input tokenScore
	 * @return string of tokensScore list
	 */
	public String outputTokensScore(List<TokenScore> tokensScoreList) {
		String output = "";
		for (TokenScore token : tokensScoreList) {
			output += token + " , ";
		}
		return output;
	} 
	
	/**
	 * get top tokens score of given text
	 * @param text input text
	 * @param type input score type
	 * @param top input top score number.
	 * @return list of top tokens score
	 */
	public List<TokenScore> getTopTokensScore(String text, ScoreType type, int top) {
		List<TokenScore> tokensScoreList = getTokensScore(text, type);
		if (top > 0 && top <= tokensScoreList.size())
			return tokensScoreList.subList(0, top);
		else 
			return tokensScoreList;
	}
	
	/**
	 * Given a text, get tokens score sorted by score type
	 * @param text input text
	 * @param type input score type
	 * @return list of sorted tokens score
	 */
	public List<TokenScore> getTokensScore(String text, ScoreType type) {
		List<TokenScore> result = new ArrayList<TokenScore>();
		Tokenizer tokenizer = tokenizerFactory.create(text);
		List<String> tokens = tokenizer.getTokens();
		HashMap<String, AtomicInteger> counts = new HashMap<String, AtomicInteger>();
		for (String token : tokens) {
			if (!counts.containsKey(token))
				counts.put(token, new AtomicInteger(0));
			counts.get(token).getAndIncrement();
		}
		// store tokenScore with PriorityQueue
		PriorityQueue<TokenScore> heap = new PriorityQueue<TokenScore>(counts.size(), new TokenScoreComparator());
		for (Entry<String, AtomicInteger> entry : counts.entrySet()) {
			double idf = idfForWord(entry.getKey());
			Double score;
			switch(type) {
				case IDF:
					score = idf;
				case TF_IDF:
					score = getTfidfWord(entry.getKey(), entry.getValue().get(), tokens.size());
				default:
					score = idf;
			}
			if (!score.isNaN()) {
				TokenScore tokenScore = new TokenScore(entry.getKey(), score, idf);
				heap.add(tokenScore);
			}
		}
		
		// store tokenScore to list
		while (!heap.isEmpty()) {
			result.add(heap.poll());
		}
		return result;
	}
	
	private double getTfidfWord(String word, long wordCount, long documentLength) {
		return MathUtils.tfidf(tfForWord(wordCount, documentLength), idfForWord(word));
	}
	
	private double tfForWord(long wordCount, long documentLength) {
		return (double) wordCount / (double) documentLength;
	}
	
	private double idfForWord(String word) {
		return MathUtils.idf(tfidf.getVocabCache().totalNumberOfDocs(), tfidf.getVocabCache().docAppearedIn(word));
	}
	
}
