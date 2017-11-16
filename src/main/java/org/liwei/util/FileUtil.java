package org.liwei.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	/** 
     * 复制单个文件 
     * @param srcFileName 待复制的文件名 
     * @param descFileName 目标文件名 
     * @param overlay 如果目标文件存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {  
        String message = "";  
        File srcFile = new File(srcFileName);  
        // 判断源文件是否存在  
        if (!srcFile.exists()) {  
        	message = "源文件：" + srcFileName + "不存在！";  
        	//System.out.print(message);
            return false;  
        } else if (!srcFile.isFile()) {  
        	message = "复制文件失败，源文件：" + srcFileName + "不是一个文件！";  
        	System.out.print(message);
            return false;  
        }  
        // 判断目标文件是否存在  
        File destFile = new File(destFileName);  
        if (destFile.exists()) {  
            // 如果目标文件存在并允许覆盖  
            if (overlay) {  
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
                new File(destFileName).delete();  
            }  
        } else {  
            // 如果目标文件所在目录不存在，则创建目录  
            if (!destFile.getParentFile().exists()) {  
                // 目标文件所在目录不存在  
                if (!destFile.getParentFile().mkdirs()) {  
                    // 复制文件失败：创建目标文件所在目录失败  
                    return false;  
                }  
            }  
        }  
        // 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (FileNotFoundException e) {  
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    } 
    
	/** 
    * 复制整个目录的内容 
    * @param srcDirName 待复制目录的目录名 
    * @param destDirName 目标目录名 
    * @param overlay 如果目标目录存在，是否覆盖 
    * @return 如果复制成功返回true，否则返回false 
    */  
    public static boolean copyDirectory(String srcDirName, String destDirName, boolean overlay) {  
    	String message = "";  
        // 判断源目录是否存在  
        File srcDir = new File(srcDirName);  
        if (!srcDir.exists()) {  
            message = "复制目录失败：源目录" + srcDirName + "不存在！";  
            //JOptionPane.showMessageDialog(null, MESSAGE);  
        	System.out.print(message);
            return false;  
        } else if (!srcDir.isDirectory()) {  
            message = "复制目录失败：" + srcDirName + "不是目录！";  
            //JOptionPane.showMessageDialog(null, MESSAGE);  
        	System.out.print(message);
            return false;  
        }  
        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        File destDir = new File(destDirName);  
        // 如果目标文件夹存在  
        if (destDir.exists()) {  
            // 如果允许覆盖则删除已存在的目标目录  
            if (overlay) {  
                new File(destDirName).delete();  
            } else {  
                message = "复制目录失败：目的目录" + destDirName + "已存在！";  
                //JOptionPane.showMessageDialog(null, MESSAGE);  
            	System.out.print(message);
                return false;  
            }  
        } else {  
            // 创建目的目录  
            //System.out.println("目的目录不存在，准备创建。。。");  
            if (!destDir.mkdirs()) {  
                System.out.println("复制目录失败：创建目的目录失败！");  
                return false;  
            }  
        }  
        boolean flag = true;  
        File[] files = srcDir.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            // 复制文件  
            if (files[i].isFile()) {  
                flag = FileUtil.copyFile(files[i].getAbsolutePath(),  
                        destDirName + files[i].getName(), overlay);  
                if (!flag)  
                    break;  
            } else if (files[i].isDirectory()) {  
                flag = FileUtil.copyDirectory(files[i].getAbsolutePath(),  
                        destDirName + files[i].getName(), overlay);  
                if (!flag)  
                    break;  
            }  
        }  
        if (!flag) {  
            message = "复制目录" + srcDirName + "至" + destDirName + "失败！";  
            //JOptionPane.showMessageDialog(null, MESSAGE);  
        	System.out.print(message);
            return false;  
        } else {  
            return true;  
        }  
    }
    
    // 创建目录，如果存在相应目录或者文件返回错误
	public static boolean make_dir(String dirPath) {
		
		File file = new File(dirPath);
		if (file.exists()) {
			if (file.isDirectory()) {
				System.out.println("file exists");
				return true;
			} else {
				System.out.println("the same name file exists, can not create file");
				return false;
			} 
		}else {
			//System.out.println("directory not exists, create it ...");
			file.mkdir();
			return true;
		}	
	}
	
	/** 
     * get all files under a specific directory 
     * @param path 文件夹路径，绝对地址           
     * @return 如果读取成功返回类型List<String> 包含文件路径
     */
    public static List<String> getAllFiles(String path) {
    	List<String> files = new ArrayList<String>();
    	if(files != null)
    		files.clear();
    	return getFile(path, null, files);
    }
	
	/** 
     * get all specific types files under a specific directory 
     * @param path 文件夹路径，绝对地址 
     * @param suffix 文件后缀名，如“.java”             
     * @return 如果读取成功返回类型List<String> 包含文件路径
     */
    public static List<String> getAllFiles(String path, String suffix) {
    	List<String> files = new ArrayList<String>();
    	if(files != null)
    		files.clear();
    	return getFile(path, suffix, files);
    }
    
    private static List<String> getFile(String path, String suffix, List<String> files) {
    	if (null != path) {
            File file = new File(path);
            if (file.exists()) {
                File[] list = file.listFiles();
                if(null != list){
                    for (File child : list) {
                        if (child.isFile()) {
                            //fileList.add(child.getAbsolutePath());
                            String temp = child.getAbsolutePath();
                            if (suffix != null) {
                            	if(temp.substring(temp.length() - suffix.length()).equals(suffix))
                            		files.add(child.getAbsolutePath());
                            } else {
                            	files.add(child.getAbsolutePath());
                            }
                            //System.out.println(child.getAbsolutePath());
                        } else if (child.isDirectory()) {
                        	getFile(child.getAbsolutePath(), suffix, files);
                        }
                    }
                }
            }
        }
        return files;
	}
    
    /** 
     * 将文件转化为字符串
     * @param filePath 文件路径，绝对地址           
     * @return 如果读取成功返回类型String
     */
    public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return  fileData.toString();	
	}
    
    
    /** 
     * 将字符串写入目标路径下
     * @param str 要写入的字符串
     * @param path 文件夹路径，绝对地址           
     */
	public static void writeStringToFile(String str, String filePath) throws IOException {
		File file = new File(filePath);
		// if file doesn't exists, then create it
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(str);
		bw.close();
	}
	
	public static List<String> readFileToList(String filePath) throws IOException {
		List<String> stringList = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringList.add(line);
		}
		reader.close();
		return stringList;
	}
	
	public static void writeListToFile(List<String> stringList, String filePath) throws IOException {
		File file = new File(filePath);
		// if file doesn't exists, then create it
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for (String str : stringList) {
			bw.write(str);
			bw.newLine();
		}
		bw.close();
	}
	
	/**
	 * merge two file into a new file
	 * @param filePath1  the input first file path
	 * @param filePath2  the input second file path
	 * @param destFilePath  the destination file path
	 */
	public static void mergeFile(String filePath1, String filePath2, String destFilePath) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(destFilePath));
		BufferedReader reader = new BufferedReader(new FileReader(filePath1));
		String line;
		while ((line = reader.readLine()) != null) {
			writer.write(line);
			writer.newLine();
		}
		reader.close();
		reader = new BufferedReader(new FileReader(filePath2));
		while ((line = reader.readLine()) != null) {
			writer.write(line);
			writer.newLine();
		}
		reader.close();
		writer.close();
	}
	
	/**
	 * store each line of file into an list
	 * @param file   given file
	 * @return  a list of file's all lines
	 */
	public static List<String> asList(File file) {
		List<String> result = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * split documents
	 * @param inputPath input file path
	 * @param cores  num of documents to split 
	 * @return a list of list 
	 */
	public static List<List<String>> spiltDocuments(String inputPath, int cores) {
		List<List<String>> splitDocument = new ArrayList<List<String>>();
		try {
			List<String> document = FileUtil.readFileToList(inputPath);

			int averN = document.size() / cores;
			for (int i = 0; i < cores; i++) {
				List<String> texts = new ArrayList<String>();
				splitDocument.add(texts);
			}
			int n = 0;
			int core = 0;
			List<String> texts = splitDocument.get(core);
			for (int i = 0; i < document.size(); i++) {
				texts.add(document.get(i));
				n++;
				if (n > averN && core < cores) {
					n = 0;
					core++;
					texts = splitDocument.get(core);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return splitDocument;
	}
	
}
