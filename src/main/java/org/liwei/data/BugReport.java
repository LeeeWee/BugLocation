package org.liwei.data;

import java.util.Set;

import  org.liwei.util.Index;
import org.nd4j.linalg.api.ndarray.INDArray;

public class BugReport {

	/**
	 * bug report id
	 */
	protected String bugId;
	
	/**
	 * bug report corresponding vector
	 */
	protected INDArray vector;
	
	/**
	 * bug report cleaned summary and description
	 */
	protected String text;
	
	/**
	 * modified files of a bug report
	 */
	protected Set<Index> modifiedFiles;
	
	public BugReport(String bugId, INDArray vector) {
		this.bugId = bugId;
		this.vector = vector;
	}
	
	public BugReport(String bugId, INDArray vector, Set<Index> modifiedFiles) {
		this.bugId = bugId;
		this.vector = vector;
		this.modifiedFiles = modifiedFiles;
	}
	
	public String getId() {
		return this.bugId;
	}
	
	public INDArray getVector() {
		return this.vector;
	}
	
	public Set<Index> getModifiedFiles() {
		return this.modifiedFiles;
	}
	
	public void SetText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	
}
