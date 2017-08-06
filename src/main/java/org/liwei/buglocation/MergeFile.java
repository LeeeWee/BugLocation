package org.liwei.buglocation;

import org.liwei.util.Util;

public class MergeFile {
	public static void main(String[] args) throws Exception{
		String path1 = "D:\\Data\\working\\bug_report1.s";
		String path2 = "D:\\Data\\working\\bad1.s";
		String destFilePath = "D:\\Data\\working\\total.s";
		Util.mergeFile(path1, path2, destFilePath);
		System.out.println("Merge sucessed!");
	}
}
