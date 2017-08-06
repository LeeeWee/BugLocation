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
import org.liwei.util.Util;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TfidfProcess {

	private Logger log = LoggerFactory.getLogger(TfidfProcess.class);

	private int numOutput;

	private String input;
	private String output;
	private String stopWordsFilePath;
	private String word2VecModelPath;

	private TfidfVectorizer tfidf;

	public TfidfProcess(int numOutput, String input, String output, String stopWordsFilePath,
			String word2VecModelPath) {
		this.numOutput = numOutput;
		this.input = input;
		this.output = output;
		this.stopWordsFilePath = stopWordsFilePath;
		this.word2VecModelPath = word2VecModelPath;
	}

	public void train() throws Exception {
		String tfidfPath = input.subSequence(0, input.lastIndexOf(".")) + ".tfidf";
		List<String> stopWords = Util.asList(new File(stopWordsFilePath));
		SentenceIterator iter = new BasicLineIterator(input);
		// Split on white spaces in the line to get words
		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());
		log.info("Building model...");
		tfidf = new TfidfVectorizer.Builder().setMinWordFrequency(5).setIterator(iter)
				.setTokenizerFactory(t).setStopWords(stopWords).build();
		log.info("Fitting Tfidf model...");
		tfidf.fit();

		// log.info("Writing tfidf vectors to text file....");

	}

	public TfidfVectorizer getTfidfModel() {
		return tfidf;
	}

	public List<INDArray> vectorize() throws Exception {
		List<INDArray> result = new ArrayList<INDArray>();
		train();
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

	public static void main(String[] args) throws Exception{
		int numOutput = 200;
		String input = "D:\\Data\\working\\total.s";
		String output = "D:\\Data\\working\\total.v";
		String stopWordsFilePath = "D:\\Data\\stopwords.txt";
		String word2VecModelPath = "D:\\Data\\working\\wordvec_d200e60.v";

		TfidfProcess process = new TfidfProcess(numOutput, input, output, stopWordsFilePath, word2VecModelPath);
		process.vectorize();
	}

}
