package org.liwei.buglocation.genModel;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;

public class TfidfThread extends TfidfInferer implements Runnable{

	private Thread thread;
	private int threadSerial;
	private String threadName;
	private List<String> document;
	private List<INDArray> result;
	private boolean isFinished;

	public TfidfThread(int threadSerial, List<String> document, TfidfVectorizer tfidf, WordVectors wordVec,
			List<String> stopWords, int numOutput) throws Exception {
		super(tfidf, wordVec, stopWords, numOutput);
		this.threadSerial = threadSerial;
		this.document = document;
	}
	
	public void run() {
		result = new ArrayList<INDArray>();
		for (int n = 0; n < document.size(); n++) {
			String text = document.get(n);
			INDArray textVec = vectorize(text);
			result.add(textVec);
			if (n % 200 == 0) 
				System.out.println(threadName + ":prcessed " + n + "lines.");
		}
		System.out.println(threadName + ":prcessed finished!");
		this.isFinished = true;
	}
	
	public List<INDArray> getResult() {
		return this.result;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public void start() {
		threadName = "Vectorize@" + String.valueOf(threadSerial);
		thread = new Thread(this, threadName);
		thread.start();
	}
}
