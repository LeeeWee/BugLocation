package org.liwei.buglocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.liwei.util.FileManage;

public class TextCleaning {
	private static List<String> stopWords = new ArrayList<String>();
	public static void main(String[] args) {
		String path = "/Users/liwei/Documents/defect-prediction/working/test/";
		String destPath =  "/Users/liwei/Documents/defect-prediction/working/allComments.txt";
		initializeStopWords();
		List<String> commentFiles = FileManage.getAllFiles(path, ".comment");
		String allComments = new String();
		for (String commentFile : commentFiles) {
			allComments = allComments + textCleaning(commentFile) + "\n";
		}
		try {
			FileManage.writeStringToFile(allComments, destPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	public static String textCleaning(String commentFile) {
		String resultStr = new String();
		try {
			FileReader fr = new FileReader(commentFile);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				for (String stopWord : stopWords) {
					line = line.replaceAll(stopWord, " ");
				}
				line = line.replaceAll("[\\pP\\pS\\pZ\\pN]", " ");
				line = line.trim() + " ";
				resultStr = resultStr + line;
			}
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		resultStr = resultStr.replaceAll("\\s+", " ").trim();
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
}