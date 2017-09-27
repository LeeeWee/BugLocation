package org.liwei.buglocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetChangeHistory {

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
	
}
