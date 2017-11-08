package org.liwei.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;

public class StopWordsPreprocessor extends CommonPreprocessor {
	
	private Set<String> stopWords;
	
	public StopWordsPreprocessor(Collection<String> stopWords) {
		Set<String> stopWordsSet = new HashSet<String>();
		for (String stopWord : stopWords) {
			stopWordsSet.add(stopWord.toLowerCase());
		}
		this.stopWords = stopWordsSet;
	}
	
	public String preProcess(String token) {
       String result = super.preProcess(token);
       if (stopWords.contains(result))
    	   return "";
       else 
    	   return result;
    }
	
}
