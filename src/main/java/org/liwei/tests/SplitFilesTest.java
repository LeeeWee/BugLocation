package org.liwei.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SplitFilesTest {

	public static void main(String[] args) {
		String historyFilePath = "D:\\data\\working\\file.history";
		List<String> errorFiles = getErrorFiles(historyFilePath);
		for (String errorFile : errorFiles) {
			System.out.println(errorFile);
		}
//		String filesString = "org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/java/FilledArgumentNamesMethodProposal.java org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/java/LazyGenericTypeProposal.java org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/java/ParameterGuessingProposal.java org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/javadoc/JavadocInlineTagCompletionProposal.java org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/javadoc/JavadocLinkTypeCompletionProposal.java";
//		List<String> files = extractFilesFromString(filesString);
//		for (String file : files)
//			System.out.println(file);
	}
	
	private static List<String> getErrorFiles(String historyFilePath) {
		List<String> errorFiles = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(historyFilePath)));
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("org.eclipse."))
					errorFiles.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return errorFiles;
	}
	
	private static List<String> extractFilesFromString(String filesString) {
		List<String> filesList = new ArrayList<String>();
		String[] files = filesString.split("\\.java");
		for (int i = 0; i < files.length; i++) {
				filesList.add(files[i].trim() + ".java");
		}
		return filesList;
	}
}
