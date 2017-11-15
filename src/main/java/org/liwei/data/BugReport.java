package org.liwei.data;

import java.util.Date;
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
	
	/**
	 * date of commit time
	 */
	protected Date commitDate;
	
	public BugReport(String bugId, Date commitDate, INDArray vector) {
		this.bugId = bugId;
		this.commitDate = commitDate;
		this.vector = vector;
	}
	
	public BugReport(String bugId, Date commitDate, INDArray vector, Set<Index> modifiedFiles) {
		this.bugId = bugId;
		this.commitDate = commitDate;
		this.vector = vector;
		this.modifiedFiles = modifiedFiles;
	}
	
	public String getId() {
		return this.bugId;
	}
	
	public INDArray getVector() {
		return this.vector;
	}
	
	public Date getCommitDate() {
		return this.commitDate;
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
	
	public boolean isModified(String file) {
		for (Index index : modifiedFiles) {
			if (index.getPath().equals(file))
				return true;
		}
		return false;
	}
	
	
}
