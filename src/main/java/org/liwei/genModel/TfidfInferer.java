package org.liwei.genModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.MathUtils;
import org.liwei.util.FileUtil;
import org.liwei.util.StopWordsPreprocessor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TfidfInferer {
	private static Logger log = LoggerFactory.getLogger(TfidfInferer.class);
	/**
	 * default value:top n largest tfidf words 
	 */
	public static final Integer TOP_TFIDF_SCORES = 20;
	
	/**
	 * word2vec vector size
	 */
	private int numOutput;
	private WordVectors vec;
	private TfidfVectorizer tfidf;
	private TokenizerFactory tokenizerFactory;
	private List<String> stopWords;
	
	public class TfidfScore {
		private String word;
		private Double score;
		
		public TfidfScore(String word, Double score) {
			this.word = word;
			this.score = score;
		}
		
		public String toString() {
			return word + ":" + score;
		}
	} 
	
	static class TfdifComparator implements Comparator<TfidfScore> {
		public int compare(TfidfScore tfidf1, TfidfScore tfidf2) {
			if (tfidf1.score < tfidf2.score) 
				return 1;
			else if (tfidf1.score > tfidf2.score)
				return -1;
			else 
				return 0;
		}
	}
	
	/**
	 * Constructor
	 * @param tfidf tfidf vectorizer
	 * @param word2VecModelFile  word2vec model file
	 */
	public TfidfInferer(TfidfVectorizer tfidf, File word2VecModelFile, File stopWordsFile, int numOutput) throws Exception{
		this.tfidf = tfidf;
		this.vec = WordVectorSerializer.loadTxtVectors(word2VecModelFile);
		this.stopWords = FileUtil.asList(stopWordsFile);
		this.numOutput = numOutput;
		this.tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new StopWordsPreprocessor(stopWords));
	}
	
	public TfidfInferer(TfidfVectorizer tfidf, WordVectors vec, List<String> stopWords, int numOutput) {
		this.tfidf = tfidf;
		this.vec = vec;
		this.stopWords = stopWords;
		this.numOutput = numOutput;
		this.tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new StopWordsPreprocessor(stopWords));
	}
	
	public INDArray vectorize(String text) {
		return vectorize(text, TOP_TFIDF_SCORES);
	}
	
	/**
	 * get text vector
	 * @param text  given text
	 * @param topN  using top N tfidf score words to calculate
	 * @return vector of text 
	 */
	public INDArray vectorize(String text, int topN) {
		INDArray result = Nd4j.create(1, numOutput);
		Tokenizer tokenizer = tokenizerFactory.create(text); 
		List<String> tokens = tokenizer.getTokens();
		HashMap<String, AtomicInteger> counts = new HashMap<String, AtomicInteger>();
		for (String token : tokens) {
			if (!counts.containsKey(token))
				counts.put(token, new AtomicInteger(0));
			counts.get(token).getAndIncrement();
		}
		PriorityQueue<TfidfScore> heap = new PriorityQueue<TfidfScore>(100, new TfdifComparator());
		for (Entry<String, AtomicInteger> entry : counts.entrySet()) {
			TfidfScore score = new TfidfScore(entry.getKey(), getTfidfWord(entry.getKey(), entry.getValue().get(), tokens.size()));
			if (!score.score.isNaN()) 
				heap.add(score);
		}
		int left = topN;
		double weights = 0;
		while (left > 0 && !heap.isEmpty()) {
			TfidfScore score = heap.poll();
			INDArray v = vec.getWordVectorMatrixNormalized(score.word);
			if (v != null) {
				v.muli(score.score);
				result.addi(v);
				weights += score.score;
			}
			left--;
		}
		if (weights > 0)
			result.divi(weights);
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
	
	/**
	 * get topN tfidf score word
	 * @param topN top N
	 * @return list of words
	 */
	public List<String> getTopWords(String text, int topN) {
		List<String> topWords = new ArrayList<String>();
		Tokenizer tokenizer = tokenizerFactory.create(text); 
		List<String> tokens = tokenizer.getTokens();
		HashMap<String, AtomicInteger> counts = new HashMap<String, AtomicInteger>();
		for (String token : tokens) {
			if (!counts.containsKey(token))
				counts.put(token, new AtomicInteger(0));
			else 
				counts.get(token).getAndIncrement();
		}
		PriorityQueue<TfidfScore> heap = new PriorityQueue<TfidfScore>(100, new TfdifComparator());
		for (Entry<String, AtomicInteger> entry : counts.entrySet()) {
			TfidfScore score = new TfidfScore(entry.getKey(), getTfidfWord(entry.getKey(), entry.getValue().get(), tokens.size()));
			heap.add(score);
		}
		int left = topN;
		while (left >= 0 && (!heap.isEmpty())) {
			TfidfScore score = heap.poll();
			topWords.add(score.word);
			left--;
		}
		return topWords;
	}
}
