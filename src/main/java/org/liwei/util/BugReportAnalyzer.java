package org.liwei.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;

import org.liwei.data.BugReport;
import org.liwei.data.BugReportRepository;
import org.liwei.data.CodeMetrics;
import org.liwei.data.CodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BugReportAnalyzer {
	private static Logger logger = LoggerFactory.getLogger(BugReportAnalyzer.class);
	
	/**
	 * bug report repository
	 */
	protected BugReportRepository brRepo;
	
	/**
	 * code report repository
	 */
	protected CodeRepository codeRepo;
	
	/**
	 * Constructor
	 * @param directory input directory to initialize bugRepo 
	 */
	public BugReportAnalyzer(String directory) {
		this.brRepo = BugReportRepository.readBugReportRepository(directory, true);
		this.codeRepo = CodeRepository.readCodeRepository(directory);
	}
	
	/**
	 * Input a bug report id and get it's summary-description and modified files' information
	 * @param output path to save output message
	 */
	public void brModifiedFilesAnalyzer(String output) {
		try {
			BufferedWriter writer;
			Scanner scanner = new Scanner(System.in, "utf-8");
			while (true) {
				System.out.println("Input BugReport id to query information: (input 'q' to quit)");
				String input = scanner.nextLine();
				if (input.equals("q")) 
					break;
				StringBuffer outputStrBuffer = new StringBuffer();
				String[] bugReportIds = input.split(" ");
				for (String bugReportId : bugReportIds) {
					BugReport bugReport = brRepo.get(bugReportId);
					if (bugReport == null ) {
						System.out.println("Bug repository doesn't contain input bug: " + input);
						continue;
					}
					System.out.println("Getting information for " + bugReportId + "...");
					// get bug report 
					outputStrBuffer.append("BugReport " + bugReport.getId() + ":\n\tText: " + bugReport.getText() + "\n\n\tModified Files:\n");
					// get modified files text
					for (Index badIndex : bugReport.getModifiedFiles()) {
						outputStrBuffer.append("\t\t" + badIndex.getPath() + ":\n\t\t\tText: " + badIndex.getText() + "\n");
						CodeMetrics codeMetrics = codeRepo.getCodeMetrics(badIndex.getPath());
						if (codeMetrics == null) 
							outputStrBuffer.append("\t\t\tExist in temp code repo: false\n");
						else 
							outputStrBuffer.append("\t\t\tExist in temp code repo: true\n");
					}
					outputStrBuffer.append("\n");
				}
				writer = new BufferedWriter(new FileWriter(output));
				writer.write(outputStrBuffer.toString());
				writer.close();
			}
			scanner.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		String directory = "D:\\Data\\working";
		String output = "D:\\Data\\working\\output\\brModifiedFilesAnalyze.txt";
		BugReportAnalyzer brAnalyzer = new BugReportAnalyzer(directory);
		
		brAnalyzer.brModifiedFilesAnalyzer(output);
	}
}
