package org.liwei.buglocation.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.liwei.buglocation.utils.FileParser;
import org.liwei.buglocation.utils.FileUtil;

/**
 * clean comment files which contain only methods' names and comments. 
 * @author Liwei
 *
 */
public class GetFileMethodsAndComments {

	private static Set<String> stopWords = new HashSet<String>();

	public static void main(String[] args) {
		String dirPath = "D:\\Data\\working\\bad\\";
		String destPath = "D:\\Data\\working\\bad.txt";
		mergeBadCommentFiles(dirPath, destPath);
	}
	
	/**
	 * generate comment files for java file in given dirPath
	 */
	public static void genCommentFileInDirectory(String dirPath) {
		List<String> files = FileUtil.getAllFiles(dirPath, ".java");
		int fileCount = files.size();
		for (int i = 0; i < fileCount; i++) {
			getMethodsAndComments(files.get(i));
			if(i % 500 == 0) {
				System.out.println("genCommentFiles:" + i + "/" + fileCount);
			}
		}
		System.out.println("genCommentFiles finished!");
	}
	
	/**
	 * get methods and comments in sourceFile and write into comment file, 
	 * @param sourceFilePath
	 */
	public static void getMethodsAndComments(String sourceFilePath) {
		try {
			String destFilePath = sourceFilePath + ".comment";
			FileParser parser = new FileParser(new File(sourceFilePath));
			FileUtil.writeStringToFile(parser.getMethodsAndComments(), destFilePath); 
		} catch (Exception e) {
			System.out.println("file:" + sourceFilePath + " genCommentFile failed!");
		}
	}
	
	public static void mergeBadCommentFiles(String dirPath, String destPath) {
		initializeStopWords();
		List<String> commentFiles = FileUtil.getAllFiles(dirPath, ".comment");
		String allComments = new String();
		String infos = new String();
		for (int i = 0; i < commentFiles.size(); i++) {
			String commentFile = commentFiles.get(i);
			allComments = allComments + textCleaning(commentFile) + "\n";
			String fileName = commentFile.substring(commentFile.lastIndexOf(File.separator) + 1);
			String file = fileName.substring(0, fileName.lastIndexOf("#")).replace("#", "/") + ".java";
			String commit = fileName.substring(fileName.lastIndexOf("#") + 1, fileName.indexOf(".java.comment"));
			String info = String.valueOf(i) + " " + commit + " " + file + "\n";
			infos = infos + info;
			if (i % 500 == 0) {
				System.out.println("Text cleaning processing:" + i + "/" + commentFiles.size());
			}
		}
		System.out.println("Finished text cleaning!");
		try {
			FileUtil.writeStringToFile(allComments, destPath);
			FileUtil.writeStringToFile(infos, destPath.substring(0, destPath.lastIndexOf(".")) + ".i");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mergeGoodCommentFiles(String dirPath, String destPath) {
		initializeStopWords();
		List<String> commentFiles = FileUtil.getAllFiles(dirPath, ".comment");
		String allComments = new String();
		String infos = new String();
		for (int i = 0; i < commentFiles.size(); i++) {
			String commentFile = commentFiles.get(i);
			allComments = allComments + textCleaning(commentFile) + "\n";
			String fileName = commentFile.substring(commentFile.lastIndexOf(File.separator) + 1, commentFile.lastIndexOf(".")).replace("#", "/");
			String info = String.valueOf(i) + " 000000 " + fileName + "\n";
			infos = infos + info;
			if (i % 500 == 0) {
				System.out.println("Text cleaning processing:" + i + "/" + commentFiles.size());
			}
		}
		try {
			FileUtil.writeStringToFile(allComments, destPath);
			FileUtil.writeStringToFile(infos, destPath.substring(0, destPath.lastIndexOf(".")) + ".i");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * remove digits, symbols and stopwords of text
	 * 
	 * @param commentFile
	 *            input text string to be cleaned
	 * @return
	 */
	public static String textCleaning(String commentFile) {
		String resultStr = new String();
		String tempStr = new String();
		try {
			FileReader fr = new FileReader(commentFile);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				for (String stopWord : stopWords) {
					line = line.replace(stopWord, " ");
				}
				line = line.replaceAll("[\\pP\\pS\\pZ\\pN]", " ");
				line = line.trim() + " ";
				tempStr = tempStr + line;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] words = tempStr.split("\\s+");
		for (String word : words) {
			resultStr = resultStr + splitName(word) + " ";
		}
		resultStr = resultStr.replaceAll("\\s+", " ").toLowerCase().trim();
		return resultStr;
	}

	/**
	 * initial the stopwords
	 */
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
	 * 
	 * @param 输入的单词
	 * @return 返回分割的单词
	 */
	public static String splitName(String word) {
		int k = 0;
		String splitWord = new String();
		for (int i = 1; i < word.length() - 1; i++) {
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