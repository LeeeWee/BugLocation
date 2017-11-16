package org.liwei.buglocation;

import java.util.List;

import org.liwei.astparser.AstParser;
import org.liwei.util.FileUtil;;

public class GetCommentsAndMethods {
	public static void main(String[] args) {
		String dirPath = "D:\\Data\\working\\bad";
		List<String> files = FileUtil.getAllFiles(dirPath, ".java");
		int fileCount = files.size();
		for (int i = 0; i < fileCount; i++) {
			genCommentFile(files.get(i));
			if(i % 500 == 0) {
				System.out.println("genCommentFiles:" + i + "/" + fileCount);
			}
		}
		System.out.println("genCommentFiles finished!");
	}
	
	
	public static void genCommentFile(String sourceFilePath) {
		try {
			String destFilePath = sourceFilePath + ".comment";
			FileUtil.writeStringToFile(AstParser.parse(FileUtil.readFileToString(sourceFilePath)), destFilePath); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("file:" + sourceFilePath + " genCommentFile failed!");
		}
	}
}
