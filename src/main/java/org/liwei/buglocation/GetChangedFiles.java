package org.liwei.buglocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.liwei.util.FileManage;;



public class GetChangedFiles {
	public static List<BugInfo> bugInfoList = new ArrayList<BugInfo>();
	public static List<String> commitList = new ArrayList<String>();
	public static void main(String[] args) {
		String repoPath = "/Users/liwei/Documents/defect-prediction/open-source/eclipse.jdt.ui";
		String logPath = repoPath + ".gitlog";
		String brExcelPath = "/Users/liwei/Documents/defect-prediction/bug-report/dataset/JDT.xlsx";
		String destPath = "/Users/liwei/Documents/defect-prediction/working/";
		bugInfoList = BugInfo.getBugInfoFromExcel(brExcelPath);
		getCommitList(logPath);
		genchangedFiles(repoPath, destPath);		
	}
	
	//获得commitId
	private static void getCommitList(String filePath) {
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				// 正则表达式规则
				String regEx = "^[0-9a-z]+\\s";
				// 编译正则表达式
			    Pattern pattern = Pattern.compile(regEx);
			    Matcher matcher = pattern.matcher(line);
			    while (matcher.find()) {
			    	commitList.add(matcher.group());
			    }
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void genchangedFiles(String repoPath, String destPath) {
		//为每个bug找到修改前对应的commitId
		int temp = 0;
		for (int i = 0; i < bugInfoList.size(); i++) {
			String commit = bugInfoList.get(i).commit;
			String regEx = "^" + commit;
			Pattern pattern = Pattern.compile(regEx);
			for (int j = temp; j < commitList.size(); j++) {
				String tempCommit = commitList.get(j);
				Matcher matcher = pattern.matcher(tempCommit);
				if (matcher.find()) {
					String preCommit = commitList.get(j + 1);
					bugInfoList.get(i).preCommit = preCommit;
					temp = j;
					break;
				}
			}
		}
		//将文件仓库复制到目标文件夹下
		if (repoPath.endsWith(File.separator))
			repoPath = repoPath.substring(0, repoPath.length() - 1);
		String repoName = repoPath.substring(repoPath.lastIndexOf(File.separator) + 1, repoPath.length()); 
		if (!destPath.endsWith(File.separator)) 
			destPath = destPath + File.separator;
		FileManage.copyDirectory(repoPath, destPath + "work" + File.separator + repoName, true);
		
		//将每个bug修改的文件存储到destPath的bad目录下
		for (int i = 0; i < bugInfoList.size(); i++) {
			BugInfo bugInfo = bugInfoList.get(i);
			//回到前一个版本的仓库
			String cmd = "git reset --hard " + bugInfo.preCommit;
			Runtime run = Runtime.getRuntime();
			try {  
				File file = new File(destPath + "work" + File.separator + repoName);
		        Process p = run.exec(cmd, null, file);// 启动另一个进程来执行命令  
		        //检查命令是否执行失败。  
		        if (p.waitFor() != 0) {  
		        	 if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束  
		        		 System.err.println("命令执行失败!");  
		        }   
		     } catch (Exception e) {  
		           e.printStackTrace();  
		     }
			for (String file : bugInfo.files) {
				String changedFileName = file.replace(File.separatorChar, '#').substring(0, file.lastIndexOf("."));
				String destFileName = destPath + "bad" + File.separator + changedFileName + "#" + bugInfo.commit + ".java";
				String sourceFilePath = destPath + "work" + File.separator + repoName + File.separator + file;
				FileManage.copyFile(sourceFilePath, destFileName, true);
			}
			if (i % 500 == 0) {
				System.out.println("Processed bugs:" + i + "/" + bugInfoList.size());	
			}
		}
		System.out.println("Processed finished");
	}
}
