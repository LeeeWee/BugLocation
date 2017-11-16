package org.liwei.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nd4j.linalg.api.ndarray.INDArray;

public class Index {
	
	private Integer indexNumber;
	
	private String bugId;
	
	private String path;
	
	private Date date;
	
	private INDArray vector;
	
	private String text;
	
	public Index(String line) {
		String pattern = "(\\d*)\\s(\\S*)(\\s*)(eclipse.*\\.java|0*)(\\s*)(\\d*)";
		// create regular pattern
		Pattern r = Pattern.compile(pattern);
		
		// create instance of Matcher
		Matcher m = r.matcher(line);
		
		if (m.find()) {
			this.indexNumber = Integer.parseInt(m.group(1));
			this.bugId = m.group(2);
			if (m.group(4).length() != 0) 
				this.path = m.group(4);
			if (m.group(6).length() != 0)
				this.date = new Date(new Long(m.group(6)));
		}
		
//		String[] values = line.split(" ");
//		this.indexNumber = Integer.parseInt(values[0]);
//		this.bugId = values[1];
//		int length = values.length;
//		if (length > 2)
//			this.path = values[2];
//		if (length > 3) {
//			Long time = new Long(values[3]);
//			date = new Date(time);
//		}
	}
	
	public String getBugId() {
		return bugId;
	}
	
	public String getPath() {
		return path;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setVector(INDArray vector) {
		this.vector = vector;
	}
	
	public INDArray getVector() {
		return vector;
	}
	
	public String getText() {
		return text;
	}
	
	/**
	 * get indices of file.
	 * @param file  index file
	 * @return HashMap of indices
	 */
	public static HashMap<Integer, Index> readIndices(File indexFile) {
		HashMap<Integer, Index> indices = new HashMap<Integer, Index>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(indexFile));
			String line = "";
			while ((line = reader.readLine()) != null) {
				Index index = new Index(line);
				indices.put(index.indexNumber, index);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indices;
	}
	
	/**
	 * get indices of file and attach vector
	 * @param indexFile  index File
	 * @param vectorFile  vector File
	 * @return map of indices
	 */
	public static HashMap<Integer, Index> readIndices(File indexFile, File vectorFile) {
		HashMap<Integer, Index> indices = null;
		try {
			indices = readIndices(indexFile);
			VectorFile vf = new VectorFile(vectorFile);
			for (Entry<Integer, Index> index : indices.entrySet()) {
				index.getValue().setVector(vf.get(index.getKey()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indices;
	}
	
	/**
	 * get indices of file and attach vector
	 * @param indexFile  input index File
	 * @param vectorFile  input vector File
	 * @param textFile input text file
	 * @return map of indices
	 */
	public static HashMap<Integer, Index> readIndices(File indexFile, File vectorFile, File textFile) {
		HashMap<Integer, Index> indices = null;
		try {
			indices = readIndices(indexFile);
			VectorFile vf = new VectorFile(vectorFile);
			for (Entry<Integer, Index> index : indices.entrySet()) {
				index.getValue().setVector(vf.get(index.getKey()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indices;
	}
	
}
