package org.liwei.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.liwei.util.Index;
import org.liwei.util.VectorFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BugReportRepository {
	
	private static Logger logger = LoggerFactory.getLogger(BugReportRepository.class);
	
	private static final String BUG_REPORT_INDEX_PATH = "bug_report.i";
	private static final String BUG_REPORT_TEXT_PATH = "bug_report.s";
	private static final String BUG_REPORT_VECTOR_PATH = "bug_report.v";
	private static final String BAD_INDEX_PATH = "bad.i";
	private static final String BAD_VECTOR_PATH = "bad.v";
	private static final String BAD_TEXT_PATH = "bad.txt";
	
	/**
	 * all bug reports in bug repository
	 */
	protected HashMap<String, BugReport> bugReports;

	/**
	 * constructor
	 * 
	 * @param indexFile
	 *            path to the bug report index file
	 * @param vectorFile
	 *            path to the bug report vector file
	 */
	public BugReportRepository(File indexFile, File vectorFile) {
		bugReports = new HashMap<String, BugReport>();
		VectorFile vectors = new VectorFile(vectorFile);
		HashMap<Integer, Index> indices = Index.readIndices(indexFile);
		for (Integer i = 0; i < vectors.size(); i++) {
			if (indices.containsKey(i)) {
				Index index = indices.get(i);
				BugReport bugReport = new BugReport(index.getBugId(), index.getDate(), vectors.get(i));
				bugReports.put(index.getBugId(), bugReport);
			}
		}
	}

	/**
	 * constructor
	 * 
	 * @param indexFile
	 *            path to the bug report index file
	 * @param vectorFile
	 *            path to the bug report vector file
	 * @param textFile
	 *            path to the bug report text file
	 */
	public BugReportRepository(File indexFile, File vectorFile, File textFile) {
		bugReports = new HashMap<String, BugReport>();
		VectorFile vectors = new VectorFile(vectorFile);
		HashMap<Integer, Index> indices = Index.readIndices(indexFile);
		for (Integer i = 0; i < vectors.size(); i++) {
			if (indices.containsKey(i)) {
				Index index = indices.get(i);
				BugReport bugReport = new BugReport(index.getBugId(), index.getDate(), vectors.get(i));
				bugReports.put(index.getBugId(), bugReport);
			}
		}

		// Attach each text line to it's corresponding bug report
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), "UTF-8"));
			Integer lineNumber = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				if (indices.containsKey(lineNumber)) {
					Index index = indices.get(lineNumber);
					if (bugReports.containsKey(index.getBugId()))
						bugReports.get(index.getBugId()).SetText(line);
				}
				lineNumber++;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * attach modified files information to bug reports
	 * 
	 * @param badIndices
	 *            A set of modified files
	 */
	public void attachModifiedFiles(HashMap<Integer, Index> badIndices) {
		for (Index index : badIndices.values()) {
			String bugId = index.getBugId();
			BugReport bugReport = this.get(bugId);
			if (bugReport != null) {
				bugReport.getModifiedFiles().add(index);
			}
		}
	}

	/**
	 * get bug reports in repository
	 * 
	 * @return
	 */
	public HashMap<String, BugReport> getBugReports() {
		return bugReports;
	}

	/**
	 * get bug report by bugId, if bugReports don't contain given bug, return null
	 */
	public BugReport get(String bugId) {
		if (bugReports.containsKey(bugId))
			return bugReports.get(bugId);
		else 
			return null;
	}
	
	/**
	 * read bug report repository in the given directory
	 * @param directory  the directory contains bug report index, vector, text file and bad file index, vector file.
	 * @return bugReportRepository
	 */
	public static BugReportRepository readBugReportRepository(String directory) {
		return readBugReportRepository(directory, false);
	}
	
	/**
	 * read bug report repository in the given directory
	 * @param directory  the directory contains bug report index, vector, text file and bad file index, vector file.
	 * @param attachBadText if ture, attach bad text to bad indices
	 * @return
	 */
	public static BugReportRepository readBugReportRepository(String directory, boolean attachBadText) {
		logger.info("Reading bug report repository...");
		File indexFile = new File(directory, BUG_REPORT_INDEX_PATH); 
		File vectorFile = new File(directory, BUG_REPORT_VECTOR_PATH);
		File textFile = new File(directory, BUG_REPORT_TEXT_PATH);
		File badIndexFile = new File(directory, BAD_INDEX_PATH);
		File badVectorFile = new File(directory, BAD_VECTOR_PATH);
		HashMap<Integer, Index> badIndices;
		if (attachBadText) {
			File badTextFile = new File(directory, BAD_TEXT_PATH);
			badIndices = Index.readIndices(badIndexFile, badVectorFile, badTextFile);
		} else {
			badIndices = Index.readIndices(badIndexFile, badVectorFile);
		}
		BugReportRepository repository = new BugReportRepository(indexFile, vectorFile, textFile);
		repository.attachModifiedFiles(badIndices);
		logger.info("Reading successed!");
		return repository;
	}
	
	public static void main(String[] args) {
		BugReportRepository repository = readBugReportRepository("D:\\data\\working");
		System.out.println(repository.getBugReports().size());
	}

}
