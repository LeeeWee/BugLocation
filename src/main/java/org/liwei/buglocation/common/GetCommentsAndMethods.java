package org.liwei.buglocation.common;

import java.io.File;
import java.util.List;

import org.liwei.buglocation.astparser.ASTCreator;
import org.liwei.buglocation.utils.FileParser;
import org.liwei.buglocation.utils.FileUtil;;

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
			FileParser parser = new FileParser(new File(sourceFilePath));
			FileUtil.writeStringToFile(parser.getMethodsAndComments(), destFilePath); 
		} catch (Exception e) {
			System.out.println("file:" + sourceFilePath + " genCommentFile failed!");
		}
	}
}
