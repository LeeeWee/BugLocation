package org.liwei.buglocation.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.iterators.AbstractSequenceIterator;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.VocabConstructor;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentEncoder {
	private static Logger logger = LoggerFactory.getLogger(DocumentEncoder.class);
	
	protected static final int MIN_WORD_FREQUENCY = 3;
	
	protected File inputFile;
	protected SentenceIterator iterator;
	protected List<String> stopWords;
	protected TokenizerFactory tokenizerFactory;
	protected VocabCache<VocabWord> vocabCache;
	
	/**
	 * Constructor
	 * @param inputFile input file to vectorize
	 * @param stopWordsFile input stopWords file
	 */
	public DocumentEncoder(File inputFile, File stopWordsFile) {
		try {
			this.iterator = new BasicLineIterator(inputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.inputFile = inputFile;
		this.stopWords = FileUtil.asList(stopWordsFile);
		this.tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new StopWordsPreprocessor(stopWords));
		
		// build vocab cache
		buildVocabCache();
	}
	
	/**
	 * build vocab cache
	 */
	public void buildVocabCache() {
		 if (vocabCache == null)
	            vocabCache = new AbstractCache.Builder<VocabWord>().build();
		 
		 SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(iterator)
                 .tokenizerFactory(tokenizerFactory).build();
		 
		 AbstractSequenceIterator<VocabWord> iterator = new AbstractSequenceIterator.Builder<>(transformer).build();

		 VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>()
				 .addSource(iterator, MIN_WORD_FREQUENCY).setTargetVocabCache(vocabCache).setStopWords(stopWords)
				 .build();
		
		 constructor.buildJointVocabulary(false, true);
	}
	
	/**
	 * Get ont-hot vector of ducument, output to output path.
	 * @param output Given output path.
	 * @param length Default length of text, if text's tokens's numbers less than length, filled with 0,
	 * 				 if greater than length, cut to default length.
	 */
	public void documentVectorize(String output, int length) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "utf-8"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.inputFile), "utf-8"));
			String line = "";
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count++;
				if (count % 500 == 0) 
					logger.info(count + " lines handled.");
				INDArray vector = textVectorize(line, length);
				writer.write(vectorToString(vector));
				writer.newLine();
			}
			logger.info("Finished vectorizing!");
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get one-hot vector of text.
	 * @param text Input text to vectorize.
	 * @param length Default length of text, if text's tokens's numbers less than length, filled with 0,
	 * 				 if greater than length, cut to default length.
	 * @return vector of text
	 */
	public INDArray textVectorize(String text, int length) {
		int numWords = vocabCache.numWords();
		int columns = numWords * length;
		INDArray ret = Nd4j.zeros(1, columns);
		// tokenize text
		Tokenizer tokenizer = tokenizerFactory.create(text);
		List<String> tokens = tokenizer.getTokens();
		for (int i = 0; i < length; i++) {
			if (i < tokens.size()) {
				String word = tokens.get(i);
				int index = vocabCache.indexOf(word) + i * numWords;
				ret.putScalar(index, 1);
			}
		}
		return ret;
	}
	
	/**
	 * get one-hot vector of word
	 * @param word input word to vectorize
	 * @return one-hot vector of word
	 */
	public INDArray wordVectorize(String word) {
		INDArray ret = Nd4j.zeros(1, vocabCache.numWords());
		// get index of token in vocabCache
		int index = vocabCache.indexOf(word);
		ret.putScalar(index, 1);
		return ret;
	}
	
	/**
	 * convert vector to string
	 * @param vector input vector
	 * @return string of vector
	 */
	private static String vectorToString(INDArray vector) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < vector.columns(); i++) {
			builder.append(vector.getInt(i) + " ");
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		String inputFilePath = "/Users/liwei/Documents/defect-prediction/working/bad.s";
		String stopWordsPath = "/Users/liwei/Documents/defect-prediction/working/stopwords.txt";
		String outputPath = "/Users/liwei/Documents/defect-prediction/working/bad.vector";
		DocumentEncoder encoder = new DocumentEncoder(new File(inputFilePath), new File(stopWordsPath));
		encoder.documentVectorize(outputPath, 30);
	}

}
