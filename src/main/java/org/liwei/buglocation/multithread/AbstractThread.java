package org.liwei.buglocation.multithread;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractThread extends Thread{
	private Thread thread;
	private int threadSerial;
	protected String threadTag;
	private String threadName;
	private List<String> document;
	private List<String> result;
	private boolean isFinished;
	
	public AbstractThread(int threadSerial, List<String> document) {
		this.document = document;
		this.threadSerial = threadSerial;
		this.isFinished = false;
	}
	
	public abstract String lineProcess(String line);
	
	public void run() {
		result = new ArrayList<String>();
		for (int n = 0; n < document.size(); n++) {
			String line = document.get(n);
			result.add(lineProcess(line));
			if (n % 200 == 0) {
				System.out.println(threadName + ":prcessed " + n + "lines.");
			}
		}
		System.out.println(threadName + ":prcessed finished!");
		this.isFinished = true;
	}
	
	public List<String> getResult() {
		return this.result;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public void start() {
		threadName = threadTag + "@" + String.valueOf(threadSerial);
		thread = new Thread(this, threadName);
		thread.start();
	}
}
