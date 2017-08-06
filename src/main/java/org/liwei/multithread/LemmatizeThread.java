package org.liwei.multithread;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.tartarus.snowball.ext.EnglishStemmer;


public class LemmatizeThread extends AbstractThread {
	
	public LemmatizeThread(int threadSerial, List<String> document) {
		super(threadSerial, document);
		this.threadTag = "lemma";
	}
	
	@Override
	public String lineProcess(String line) {
		String lemmas = "";
//		Properties props = new Properties();  
//	    props.put("annotators", "tokenize,ssplit,pos,lemma");  
//		StanfordCoreNLP pipeline = new StanfordCoreNLP(props); 
//		  	      
//		Annotation document = new Annotation(line);    // 利用line创建一个空的Annotation  
//		pipeline.annotate(document);                   // 对text执行所有的Annotators（七种）
//		List<CoreMap> sentences = document.get(SentencesAnnotation.class);  
//	      
//        CoreMap sentence = sentences.get(0);
//
//	    for (CoreLabel token: sentence.get(TokensAnnotation.class)) {  
//		   String lemma = token.get(LemmaAnnotation.class);          // 获取词形还原结果  
//		   lemmas += lemma + " ";
//	    }  
		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
		Tokenizer tokenizer = tokenizerFactory.create(line);
		List<String> tokens = tokenizer.getTokens();
		for (String token : tokens) {
			EnglishStemmer stemmer = new EnglishStemmer();
			stemmer.setCurrent(token);
			if (stemmer.stem()) 
			lemmas += stemmer.getCurrent() + " ";
		}
//		System.out.println(lemmas.trim());
		return lemmas.trim();
	}

}
