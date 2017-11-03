package org.liwei.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.liwei.util.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeRepository {

	private static Logger logger = LoggerFactory.getLogger(CodeRepository.class);
	
	private static final String GOOD_INDEX_PATH = "good.i";
	private static final String GOOD_TEXT_PATH = "good.s";
	private static final String GOOD_VECTOR_PATH = "good.v";
	private static final String FILE_HISTORY_PATH = "file.history";
	
	/**
	 * Map each source file(path) to a metric in vector format.
	 */
	private static HashMap<String, CodeMetrics> codeMetricsSet;
	
	public CodeRepository(File indexFile, File vectorFile, File textFile, File historyFile) {
		codeMetricsSet = new HashMap<String, CodeMetrics>();
		HashMap<Integer, Index> indices = Index.readIndices(indexFile, vectorFile, textFile);
		for (Index index : indices.values()) {
			CodeMetrics codeMetrics = new CodeMetrics(index.getPath(), index.getVector(), index.getText());
			codeMetricsSet.put(index.getPath(), codeMetrics);
		}
		attachHistory(historyFile);
	}
	
	/**
	 * Read code change history from a file, and attach it to the metrics set.
	 * @param historyFile The input history file
	 */
	private void attachHistory(File historyFile) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(historyFile), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				int firstComma = line.indexOf(",");
				String path = line.substring(0, firstComma);
				String[] times = line.substring(firstComma + 1).split(",");
				long[] values = new long[times.length];
				for (int i = 0; i < times.length; i++) {
					values[i] = Long.parseLong(times[i].trim());
					CodeMetrics metrics = codeMetricsSet.get(path);
					if (metrics != null) 
						metrics.setChangeHistory(values);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * For each source file(i.e. CodeMetrics object), attach related bugReport.
	 * @param brRepository
	 */
	public void attachRelatedDTS(BugReportRepository brRepository) {
		for (BugReport br : brRepository.getBugReports().values()) {
			for (Index index : br.getModifiedFiles()) {
				CodeMetrics metrics = getCodeMetrics(index.getPath());
				if (metrics != null) {
					metrics.getRelatedBugReportList().add(br);
				}
			}
		}
	} 
	
	/**
	 * Get code metrics according to given path.
	 * @param path Input path.
	 * @return Code metrics to input path.
	 */
	public CodeMetrics getCodeMetrics(String path) {
		return codeMetricsSet.get(path);
	}
	
	/**
	 * Get code metrics set.
	 */
	public HashMap<String, CodeMetrics> getCodeMetricsSet() {
		return codeMetricsSet;
	}
	
	/**
	 * read code report repository in the given directory
	 * @param directory the directory contains good code index, vector, text file and history file.
	 * @return codeRepository
	 */
	public static CodeRepository readCodeRepository(String directory) {
		logger.info("Reading code repository...");
		if (!directory.endsWith("\\"))
			directory += "\\";
		File indexFile = new File(directory + GOOD_INDEX_PATH); 
		File vectorFile = new File(directory + GOOD_VECTOR_PATH);
		File textFile = new File(directory + GOOD_TEXT_PATH);
		File historyFile = new File(directory + FILE_HISTORY_PATH);
		CodeRepository codeRepository = new CodeRepository(indexFile, vectorFile, textFile, historyFile);
		logger.info("Reading successed!");
		return codeRepository;
	}
	
}
