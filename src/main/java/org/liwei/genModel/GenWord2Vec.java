package org.liwei.genModel;

import org.liwei.util.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

/**
 * generate word2vector model
 * @author Liwei
 */
public class GenWord2Vec {
	private static Logger log = LoggerFactory.getLogger(GenWord2Vec.class);
	
	public static void main(String[] args) throws Exception{
		String filePath = "D:\\Data\\working\\total.s";
		String vecPath = "D:\\Data\\working\\total.v";
		String stopWordsFilePath = "D:\\data\\stopwords.txt";
		List<String> stopWords = FileUtil.asList(new File(stopWordsFilePath)); 
		log.info("Load & Vectorize Sentences....");
		SentenceIterator iter = new BasicLineIterator(filePath);
		// Split on white spaces in the line to get words
		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());
		log.info("Building model...");
		Word2Vec vec = new Word2Vec.Builder()
				.iterations(1)
				.layerSize(200)
				.seed(42)
				.windowSize(5)
				.iterate(iter)
				.tokenizerFactory(t)
				.stopWords(stopWords)
				.minWordFrequency(3)
				.epochs(60)
				.build();
		log.info("Fitting Word2Vec model...");
		vec.fit();
		
        log.info("Writing word vectors to text file....");  
        WordVectorSerializer.writeWordVectors(vec, vecPath);
	}
}
