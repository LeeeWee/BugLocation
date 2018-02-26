package org.liwei.buglocation.data;

import java.util.ArrayList;
import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;

public class CodeMetrics {
	
	/**
	 * code file path
	 */
	private String path;
	
	/**
	 * code related changed points
	 */
	private long[] changeHistory;
	
	/**
	 * a list of bug report related to this code file
	 */
	private List<BugReport> relatedBugReportList;
	
	/**
	 * code text
	 */
	private String text;
	
	/**
	 * code vector generated from tfidf and word2vec model
	 */
	private INDArray literalFeatures;
	
	public CodeMetrics(String path, INDArray literalFeatures) {
		this.path = path;
		this.literalFeatures = literalFeatures;
		this.relatedBugReportList = new ArrayList<BugReport>();
	}
	
	public CodeMetrics(String path, INDArray literalFeatures, String text) {
		this.path = path;
		this.literalFeatures = literalFeatures;
		this.text = text;
		this.relatedBugReportList = new ArrayList<BugReport>();
	}
	
	/**
	 * Getter of path.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Setter of text.
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Getter of text. 
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Getter of literalFeatures
	 */
	public INDArray getLiteralFeatures() {
		return literalFeatures;
	}
	
	/**
	 * Getter of relatedBugReportList
	 */
	public List<BugReport> getRelatedBugReportList() {
		return relatedBugReportList;
	}
	
	/**
	 * Setter of changeHistory
	 * @param changeHistory
	 */
	public void setChangeHistory(long[] changeHistory) {
		this.changeHistory = changeHistory;
	}
	
	/**
	 * Get the index of the latest time of given time.
	 * @param time Input time.
	 * @return The index of the latest time of given time.
	 */
	public int locateChangePoint(long time) {
		if (changeHistory == null || changeHistory[0] > time) 
			return -1;
		int i = 0;
		for (int index = 0; index < changeHistory.length; index++) {
			if (changeHistory[index] > time) {
				i = index - 1;
				break;
			}
		}
		return i;
	}
	
	/**
	 * Given a index of change history, get change frequency.
	 * @param index Input index of change history.
	 */
	public int countChangeFrequency(int index) {
		return index + 1;
	}
	
	/**
	 * Get change point of given index of change history.
	 * @param index Input index of change history.
	 * @return The change point of given index of change history.
	 */
	public long getChangePoint(int index) {
		return changeHistory[index];
	}
	
}
