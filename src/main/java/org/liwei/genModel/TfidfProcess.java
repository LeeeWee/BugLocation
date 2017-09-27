package org.liwei.genModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;
import org.liwei.util.Util;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gengerate document vectors
 * @author Liwei
 *
 */
public class TfidfProcess {

	private Logger log = LoggerFactory.getLogger(TfidfProcess.class);
	
	/**
	 * path to the input text file to be vectorize
	 */
	private String input;
	
	/**
	 * output path of the vectors file
	 */
	private String output;

	/**
	 * words vectors dimensions
	 */
	private int numOutput;

	/**
	 * path to the stopwords file
	 */
	private String stopWordsFilePath;

	/**
	 * path to the word2vec model file
	 */
	private String word2VecModelPath;

	private TfidfVectorizer tfidf;

	/**
	 * constructor
	 */
	public TfidfProcess(int numOutput, String input, String output, String stopWordsFilePath,
			String word2VecModelPath) {
		this.numOutput = numOutput;
		this.input = input;
		this.output = output;
		this.stopWordsFilePath = stopWordsFilePath;
		this.word2VecModelPath = word2VecModelPath;
	}

	/**
	 * train tfidf model
	 */
	public void train(String tfidfPath) throws Exception {
		List<String> stopWords = Util.asList(new File(stopWordsFilePath));
		SentenceIterator iter = new BasicLineIterator(input);
		// Split on white spaces in the line to get words
		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());
		log.info("Building model...");
		tfidf = new TfidfVectorizer.Builder()
				.setMinWordFrequency(5)
				.setIterator(iter)
				.setTokenizerFactory(t)
				.setStopWords(stopWords)
				.build();
		log.info("Fitting Tfidf model...");
		tfidf.fit();

		log.info("Writing tfidf vectors to text file....");
		SerializationUtils.saveObject(tfidf, new File(tfidfPath));
	}
	
	/**
	 * 
	 * @param tfidfModel
	 * @return
	 */
	public TfidfVectorizer loadTfidfModel(File tfidfModel) {
		TfidfVectorizer tfidf = SerializationUtils.readObject(tfidfModel);
		return tfidf;
	}

	public TfidfVectorizer getTfidfModel() {
		return tfidf;
	}

	/**
	 * generate vector for each line of file
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<INDArray> vectorize() throws Exception {
		List<INDArray> result = new ArrayList<INDArray>();
		// Set tfidf model path, train tfidf model.
		String tfidfPath = input.subSequence(0, input.lastIndexOf(".")) + ".tfidf";
		train(tfidfPath);
		
		// Creat multi threads to vectorize documents.
		int cores = Runtime.getRuntime().availableProcessors();
		TfidfThread[] threads = new TfidfThread[cores];
		List<List<String>> documents = Util.spiltDocuments(input, cores);
		for (int core = 0; core < cores; core++) {
			TfidfThread thread = new TfidfThread(core, documents.get(core), tfidf, new File(word2VecModelPath),
					numOutput);
			threads[core] = thread;
			thread.start();
		}
		while (true) {
			int finishedThreadNum = 0;
			for (int i = 0; i < cores; i++) {
				if (threads[i].isFinished())
					finishedThreadNum++;
				else
					break;
			}
			if (finishedThreadNum == cores) {
				for (TfidfThread thread : threads) {
					result.addAll(thread.getResult());
				}
				System.out.println("processed fineshed!");
				System.out.println("Writing into " + output);
				// write vectors into outputFile
				BufferedWriter writer = new BufferedWriter(new FileWriter(output));
				for (INDArray vec : result) {
					for (int i = 0; i < vec.columns(); i++) {
						writer.write(Double.toString(vec.getDouble(i)));
						writer.write(" ");
					}
					writer.newLine();
				}
				writer.close();
				System.out.println("Finished!");
				break;
			} else {
				finishedThreadNum = 0;
				Thread.sleep(1000);
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		int numOutput = 200;
		String input = "D:\\Data\\working\\bug_report.s";
		String output = "D:\\Data\\working\\bug_report.v";
		String stopWordsFilePath = "D:\\Data\\stopwords.txt";
		String word2VecModelPath = "D:\\Data\\working\\wordvec_d200e60.v";

		TfidfProcess process = new TfidfProcess(numOutput, input, output, stopWordsFilePath, word2VecModelPath);
		process.vectorize();
	}

}
