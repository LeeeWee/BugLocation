package org.liwei.multithread;

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
		String inputPath = "D:\\Data\\working\\good.txt";
		String outputPath = "D:\\Data\\working\\good.s";
		RunLemmatizeThread rlt = new RunLemmatizeThread(inputPath, outputPath);
		rlt.runThread();
	}
	
}
