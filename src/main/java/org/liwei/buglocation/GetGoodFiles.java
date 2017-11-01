package org.liwei.buglocation;

import java.io.File;
import java.util.List;

import org.liwei.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetGoodFiles {
	
	private static Logger logger = LoggerFactory.getLogger(GetGoodFiles.class);
	
	public static void main(String[] args) {
		String repoPath = "D:\\data\\working\\eclipse.jdt.ui";
		String goodPath = "D:\\data\\working\\good\\";
		
		getGoodFiles(repoPath, goodPath);
	}
	
	private static void getGoodFiles(String repoPath, String goodPath) {
		if (!goodPath.endsWith(File.separator))
			goodPath += File.separator;
		String directory = repoPath.substring(0, repoPath.lastIndexOf("\\"));
		// get all java file in repository
		List<String> javaFiles = FileUtil.getAllFiles(repoPath, "java");
		
		// write java files to good path
		int count = 0;
		for (String javaFile : javaFiles) {
			count++;
			if (count % 500 == 0) 
				logger.info("Copying " + count + " files to good path.");
			String fileName = javaFile.substring(directory.length() + 1);
			String destPath = goodPath + fileName.replace(File.separatorChar, '#');
//			logger.info("Copying " + fileName + " to good path.");
			FileUtil.copyFile(javaFile, destPath, true);
		}
		System.out.println("Finished copying! Total copy " + javaFiles.size() + " files");
	}
	

}
