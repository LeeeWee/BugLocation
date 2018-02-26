package org.liwei.buglocation.common;

import org.liwei.buglocation.utils.FileUtil;

public class MergeFile {
	public static void main(String[] args) throws Exception{
		String path1 = "D:\\Data\\working\\bug_report.s";
		String path2 = "D:\\Data\\working\\bad.s";
		String destFilePath = "D:\\Data\\working\\total.s";
		FileUtil.mergeFile(path1, path2, destFilePath);
		System.out.println("Merge sucessed!");
	}
}
