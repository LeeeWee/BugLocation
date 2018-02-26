package org.liwei.buglocation.multithread;


import java.util.ArrayList;
import java.util.List;


public class TextCleaningThread extends AbstractThread{
	private static List<String> stopWords = new ArrayList<String>();

	public TextCleaningThread(int threadSerial, List<String> document) {
		super(threadSerial, document);
		this.threadTag = "textCleaning";
		initializeStopWords();
	}
	
	@Override
	public String lineProcess(String line) {
		String resultStr = new String();

		for (String stopWord : stopWords) {
			line = line.replace(stopWord, " ");
		}
		line = line.replaceAll("[\\pP\\pS\\pZ\\pN]", " ");
		String[] words = line.split("\\s+");
		for (String word : words) {
			resultStr = resultStr + splitName(word) + " ";
		}
		resultStr = resultStr.replaceAll("\\s+", " ").toLowerCase().trim();
		return resultStr;
	}
	
	public static void initializeStopWords() {
		stopWords.add("@param");
		stopWords.add("@return");
		stopWords.add("@category");
		stopWords.add("@see");
		stopWords.add("@since");
		stopWords.add("@throws");
		stopWords.add("@serialData");
		stopWords.add("@deprecated");
		stopWords.add("@author");
		stopWords.add("@exception");
		stopWords.add("{@code}");
		stopWords.add("{@docRoot}");
		stopWords.add("{@inheritDoc}");
		stopWords.add("{@linkplain}");
		stopWords.add("{@link}");
		stopWords.add("{@literal}");
		stopWords.add("{@value}");

		stopWords.add("@\n");
	}
	
	/**
	 * 分割单词，将多个单词组成的组合单词分成对应的单个单词
	 * @param 输入的单词
	 * @return 返回分割的单词
	 */
	public static String splitName(String word) {
		int k = 0;
		String splitWord = new String();
		for (int i = 1; i < word.length()-1; i++) {
			char preC = word.charAt(i - 1);
			char tempC = word.charAt(i);
			char nextC = word.charAt(i + 1);
			if ((tempC <= 'Z' && tempC >= 'A' && nextC <= 'z' && nextC >= 'a')
					|| (tempC <= 'Z' && tempC >= 'A' && preC <= 'z' && preC >= 'a')) {
				splitWord = splitWord + word.substring(k, i) + " ";
				k = i;
			}
		}
		splitWord = splitWord + word.substring(k);
		return splitWord;
	}

}
