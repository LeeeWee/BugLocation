package org.liwei.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;

import org.nd4j.linalg.api.ndarray.INDArray;

public class Index {
	
	private Integer indexNumber;
	
	private String bugId;
	
	private String path;
	
	private INDArray vector;
	
	public Index(String line) {
		String[] values = line.split(" ");
		this.indexNumber = Integer.parseInt(values[0]);
		this.bugId = values[1];
		int length = values.length;
		if (length > 2)
			this.path = values[2];
	}
	
	public String getBugId() {
		return this.bugId;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setVector(INDArray vector) {
		this.vector = vector;
	}
	
	public INDArray getVector() {
		return this.vector;
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
	 * @return
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
	
	
}
