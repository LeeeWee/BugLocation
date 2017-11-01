package org.liwei.buglocation;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.liwei.util.FileUtil;

public class BugInfo {
	public String bugId;
	public String summary;
	public String description;
	public String commit;
	public String status;
	public Date reportDate;
	public Date commitDate;
	public List<String> files;
	public String preCommit;
	
	/**
	 * transform excel cell value to string
	 * @param cell  input Cell value
	 * @return  string transformed from cell
	 */
	private static String cellValue2String(Cell cell) {
		if (cell == null) 
			return "";
		else {
			switch (cell.getCellTypeEnum()) {   //根据cell中的类型来输出数据  
			case STRING:
				return cell.getStringCellValue();
	        case NUMERIC:  
	        	DecimalFormat df = new DecimalFormat("0");
	            return df.format(cell.getNumericCellValue()).toString();  
	        case BOOLEAN:  
	            return cell.getBooleanCellValue() ? "TRUE" : "FALSE"; 
	        case FORMULA:  
	            return cell.getCellFormula(); 
	        default:  
	            return " ";  
	        }  
		}
	}
	
	/**
	 * split string to get files
	 * @param filesString  string of file names' collection
	 * @return  a list of modified files
	 */
	private static List<String> extractFilesFromString(String filesString) {
		List<String> filesList = new ArrayList<String>();
		String[] files = filesString.split("\\.java ");
		for (int i = 0; i < files.length; i++) {
				filesList.add(files[i].trim() + ".java");
		}
		return filesList;
	}
	
	/**
	 * get bug information from a given excel
	 * @param filePath  given excel path
	 * @return  A list of bug information
	 */
	public static List<BugInfo> getBugInfoFromExcel(String filePath) {
		List<BugInfo> bugInfoList = new ArrayList<BugInfo>();
		//创建输入流
		try {
			InputStream stream = new FileInputStream(filePath);
			Workbook wb = null;
			if (filePath.endsWith(".xlsx")) 
				wb = new XSSFWorkbook(stream);
			else
				wb = new HSSFWorkbook(stream);
			//获取文件的指定工作表 
	        Sheet sheet = wb.getSheetAt(0);
	        int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
	        for (int r = 1; r < rowCount; r++) {
	        	if (r % 500 == 0)
	        		System.out.println("Read " + r + " records.");
	        	Row row = sheet.getRow(r);
	        	BugInfo bugInfo = new BugInfo();
	        	bugInfo.bugId = cellValue2String(row.getCell(1));
	        	bugInfo.summary = cellValue2String(row.getCell(2));
	        	bugInfo.description = cellValue2String(row.getCell(3));
	        	bugInfo.status = cellValue2String(row.getCell(6));
	        	bugInfo.commit = cellValue2String(row.getCell(7));
	        	//string转date
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	String rds = cellValue2String(row.getCell(4));
	        	bugInfo.reportDate = sdf.parse(rds);
	        	//时间戳转date
	        	Long ct = Long.parseLong(cellValue2String(row.getCell(8))) * 1000;
	        	bugInfo.commitDate = new Date(ct);
	        	//提取修改文件
	        	String filesString = cellValue2String(row.getCell(9));
	        	bugInfo.files = extractFilesFromString(filesString);
	        	bugInfoList.add(bugInfo);
	        }
	        System.out.println("Total " + bugInfoList.size() + " bug info.");
	        wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		

		return bugInfoList;
	}
	
	/**
	 * set BugInfo output format
	 */
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String rt = sdf.format(this.reportDate);
		String ct = sdf.format(this.commitDate);
		String returnString = "bugId:" + this.bugId + " summary:" + this.summary
				+ " description:" + this.description + "commit:" +this.commit 
				+ " status:" + this.status + " reportDate:" + rt + " commitDate:" + ct
				+ " files:" + this.files;
		return returnString;
	}
	
	public static void main(String[] args) {
		List<BugInfo> bugInfoList = getBugInfoFromExcel("D:\\Data\\dataset\\JDT.xlsx");
		String destPath = "D:\\Data\\working\\bug_report.txt";
		String IndexPath = "D:\\Data\\working\\bug_report.i";
		String summ_Desc = new String();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(IndexPath));
			int lineNumber = 0;
			for (BugInfo bugInfo : bugInfoList) {
				writer.write(lineNumber + " " + bugInfo.bugId + "\n");
				String summary = bugInfo.summary.replaceAll("^Bug \\d+ ", "");
				summ_Desc = summ_Desc + summary + " " + bugInfo.description + "\n";
				lineNumber++;
			}
			writer.close();
			FileUtil.writeStringToFile(summ_Desc, destPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("BugInfo output finished!");
	}
}
