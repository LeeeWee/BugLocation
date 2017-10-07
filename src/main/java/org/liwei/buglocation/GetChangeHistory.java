package org.liwei.buglocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetChangeHistory {
	private static Logger logger = LoggerFactory.getLogger(GetChangeHistory.class);

	/**
	 * Get file change history in bugInfo list.
	 * @param bugInfoList Given bugInfo list.
	 * @return Map of changeFile(path) to it's changed date.
	 */
	public static HashMap<String, List<Date>> filesChangeHistory(List<BugInfo> bugInfoList) {
		HashMap<String, List<Date>> history = new HashMap<String, List<Date>>();
		for (BugInfo bugInfo : bugInfoList) {
			List<String> changedFiles = bugInfo.files;
			for (String file : changedFiles) {
				if (!history.containsKey(file)) {
					List<Date> dateList = new ArrayList<Date>();
					history.put(file, dateList);
				}
				List<Date> dateList = history.get(file);
				dateList.add(bugInfo.commitDate);
			}
		}
		return history;
	}
	
	/**
	 * Write files path and related change date to file.
	 * @param filesChangeHistory Map file path to related changed date.
	 * @param filePath Given path to write history.
	 */
	public static void writeHistoryToFile(HashMap<String, List<Date>> filesChangeHistory, String filePath) {
		logger.info("Begin writing history to file.");
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
			for (Entry<String, List<Date>> entry : filesChangeHistory.entrySet()) {
				writer.write("eclipse.jdt.ui/" + entry.getKey());
				for (Date date : entry.getValue()) {
					writer.write(", " + date.getTime());
				}
				writer.newLine();
			}
			writer.close();
			logger.info("Finshed Writed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		List<BugInfo> bugInfoList = BugInfo.getBugInfoFromExcel("D:\\Data\\dataset\\JDT.xlsx");
		String filePath = "D:\\Data\\working\\file.history";
		HashMap<String, List<Date>> filesChangeHistory = filesChangeHistory(bugInfoList);
		writeHistoryToFile(filesChangeHistory, filePath);
	}
	
}
