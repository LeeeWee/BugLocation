package org.liwei.multithread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.liwei.util.Util;

public abstract class RunAbstractThread {
	private String inputPath;
	private String outputPath;
	private List<List<String>> documents;
	
	public RunAbstractThread(String inputPath, String outputPath) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
	}
	
	public abstract AbstractThread createThread(int core, List<String> document);
	
	public List<String> runThread() throws Exception {
		List<String> result = new ArrayList<String>();
		int cores = Runtime.getRuntime().availableProcessors();
		AbstractThread[] threads = new AbstractThread[cores];
		documents = spiltDocuments(inputPath, cores);
		for (int i = 0; i < cores; i++) {
			List<String> document = documents.get(i);
			threads[i] = createThread(i, document);
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
				for (AbstractThread thread : threads) {
					result.addAll(thread.getResult());
				}
				System.out.println("processed fineshed!");
				break;
			}
			else {
				finishedThreadNum = 0;
				Thread.sleep(3000);
			}
			
		}
		Util.writeListToFile(result, outputPath);
		return result;
	} 
	
	
	private List<List<String>> spiltDocuments(String inputPath, int cores) throws IOException {
		List<String> document = Util.readFileToList(inputPath);
		int averN = document.size() / cores;
		List<List<String>> splitDocument = new ArrayList<List<String>>();
		int n = 0;
		for (int i = 0; i < cores; i++) {
			List<String> texts = new ArrayList<String>();
			if (i != cores - 1) {
				for (int j = 0; j < averN; j++) {
					texts.add(document.get(n));
					n++;
				}
			}
			else {
				for (; n < document.size(); n++) {
					texts.add(document.get(n));
				}
			}
			splitDocument.add(texts);
		}
		return splitDocument;
	}
}
