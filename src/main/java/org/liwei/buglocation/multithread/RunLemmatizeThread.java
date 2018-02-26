package org.liwei.buglocation.multithread;

import java.util.List;

public class RunLemmatizeThread extends RunAbstractThread {
	public RunLemmatizeThread(String inputPath, String outputPath) {
		super(inputPath, outputPath);
	}
	
	public AbstractThread createThread(int core, List<String> document) {
		AbstractThread thread = new LemmatizeThread(core, document);
		return thread;
	}
	
	public static void main(String[] args) throws Exception {
		String inputPath = "D:\\Data\\working\\bad.txt";
		String outputPath = "D:\\Data\\working\\bad.s";
		RunLemmatizeThread rlt = new RunLemmatizeThread(inputPath, outputPath);
		rlt.runThread();
	}
	
}
