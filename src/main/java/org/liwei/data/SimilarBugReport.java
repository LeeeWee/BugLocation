package org.liwei.data;

import java.util.Comparator;

public class SimilarBugReport extends BugReport{
	
	public Double similarity;
	
	public SimilarBugReport(BugReport bugReport, Double similarity) {
		super(bugReport.getId(), bugReport.getVector(), bugReport.getModifiedFiles());
		this.similarity = similarity;
		this.SetText(bugReport.getText());
	}
	
	public static class SimilarityComparator implements Comparator<SimilarBugReport> {

		public int compare(SimilarBugReport br1, SimilarBugReport br2) {
			if (br1.similarity < br2.similarity) 
				return 1;
			else if (br1.similarity > br2.similarity)
				return -1;
			else 
				return 0;
		}
		
	} 
	
}
