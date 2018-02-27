package org.liwei.buglocation.common;

import java.io.File;
import java.util.List;

import org.liwei.buglocation.astparser.ASTCreator;
import org.liwei.buglocation.multithread.AbstractThread;
import org.liwei.buglocation.utils.FileParser;
import org.liwei.buglocation.utils.FileUtil;;

public class GetFileContent {
	
	public static void main(String[] args) {
		String dirPath = "D:\\Data\\working\\bad";
		genContentFileInDirectoryWithMultiThread(dirPath);
	}
	
	/**
	 * Thread used to generate content file for code files
	 * @author Liwei
	 */
	public static class GenContentFileThread extends Thread {
		private Thread thread;
		private int threadSerial;
		private String threadName;
		private List<String> files;
		private boolean isFinished;
		
		public GenContentFileThread(int threadSerial, List<String> files) {
			this.threadSerial = threadSerial;
			this.files = files;
			this.isFinished = false;
		}
		
		public void run() {
			for (int n = 0; n < files.size(); n++) {
				getFileContent(files.get(n));
				if(n % 200 == 0) {
					System.out.println(threadName + ": processed:" + n + " files");
				}
			}
			System.out.println(threadName + ":prcessed finished!");
			this.isFinished = true;
		}
		
		public boolean isFinished() {
			return isFinished;
		}
		
		public void start() {
			threadName = "Thread" + "@" + String.valueOf(threadSerial);
			thread = new Thread(this, threadName);
			thread.start();
		}
	}
	
	/**
	 * generate content files for java file in given dirParh using multi thread
	 */
	public static void genContentFileInDirectoryWithMultiThread(String dirPath) {
		int cores = Runtime.getRuntime().availableProcessors();
		GenContentFileThread[] threads = new GenContentFileThread[cores];
		List<String> javaFiles = FileUtil.getAllFiles(dirPath, ".java");
		List<List<String>> splitFiles = FileUtil.splitStringList(javaFiles, cores);
		for (int i = 0; i < cores; i++) {
			List<String> files = splitFiles.get(i);
			threads[i] = new GenContentFileThread(i, files);
			threads[i].start();
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
				System.out.println("processed fineshed!");
				break;
			}
			else {
				finishedThreadNum = 0;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	/**
	 * generate content files for java file in given dirParh
	 */
	public static void genContentFileInDirectory(String dirPath) {
		List<String> files = FileUtil.getAllFiles(dirPath, ".java");
		int fileCount = files.size();
		for (int i = 0; i < fileCount; i++) {
			getFileContent(files.get(i));
			if(i % 500 == 0) {
				System.out.println("genContentFiles:" + i + "/" + fileCount);
			}
		}
		System.out.println("genContentFiles finished!");
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
	 * get splitted content in code file and write into content file
	 * @param sourceFilePath 
	 */
	public static void getFileContent(String sourceFilePath) {
		try {
			String desrFilePath = sourceFilePath + ".content";
			FileParser parser = new FileParser(new File(sourceFilePath));
			FileUtil.writeStringToFile(parser.getContent(), desrFilePath);
		} catch (Exception e) {
			System.out.println("file:" + sourceFilePath + " genCommentFile failed!");
		}
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
}
