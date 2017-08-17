package org.liwei.multithread;

import java.util.List;

public class RunTextCleaningThread extends RunAbstractThread{
	public RunTextCleaningThread(String inputPath, String outputPath) {
		super(inputPath, outputPath);
	}
	
	public AbstractThread createThread(int core, List<String> document) {
		AbstractThread thread = new TextCleaningThread(core, document);
		return thread;
	}
	
	public static void main(String[] args) throws Exception {
		String inputPath = "D:\\data\\working\\bug_report.txt";
		String outputPath = "D:\\data\\working\\bug_report.s";
		RunTextCleaningThread rlt = new RunTextCleaningThread(inputPath, outputPath);
		rlt.runThread();
	}
}
