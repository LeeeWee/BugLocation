package org.liwei.buglocation;

import java.util.List;

import org.liwei.astparser.AstParser;
import org.liwei.util.FileManage;;

public class getCommentsAndMethods {
	public static void main(String[] args) {
		String dirPath = "/Users/liwei/Documents/defect-prediction/working/bad/";
		List<String> files = FileManage.getAllFiles(dirPath, ".java");
		int fileCount = files.size();
		for (int i = 0; i < fileCount; i++) {
			genCommentFile(files.get(i));
			if(i % 500 == 0) {
				System.out.println("genCommentFiles:" + i + "/" + fileCount);
			}
		}
		System.out.println("gencCommentFiles finished!");
	}
	
	
	public static void genCommentFile(String sourceFilePath) {
		try {
			String destFilePath = sourceFilePath + ".comment";
			FileManage.writeStringToFile(AstParser.parse(FileManage.readFileToString(sourceFilePath)), destFilePath); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("file:" + sourceFilePath + " genCommentFile failed!");
		}
	}
}
