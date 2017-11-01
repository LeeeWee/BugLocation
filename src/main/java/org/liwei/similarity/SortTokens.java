package org.liwei.similarity;

import java.io.File;
import java.util.List;

import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;
import org.liwei.util.DataTypeUtil.TokenScore;
import org.liwei.util.FileUtil;
import org.liwei.util.StopWordsPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sort tokens of text by tokens score. 
 * @author Liwei
 */
public class SortTokens {
	private static Logger log = LoggerFactory.getLogger(SortTokens.class);
	
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
	}
	
	public static TfidfVectorizer loadTfidfModel(File tfidfModelFile) {
		TfidfVectorizer tfidf = SerializationUtils.readObject(tfidfModelFile);
		return tfidf;
	}
	
//	public List<TokenScore> getTokensScore(String text) {
//		Tokenizer tokenizer = tokenizerFactory.create(text);
//	}
	
	
}
