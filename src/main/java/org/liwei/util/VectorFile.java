package org.liwei.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class VectorFile {
	
	protected HashMap<Integer, INDArray> vectors;
	
	public VectorFile(File file) {
		vectors = new HashMap<Integer, INDArray>();
		try {
			int lineNumber = 0;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] nums = line.split(" ");
				double[] values = new double[nums.length];
				for (int i = 0; i < nums.length; i++) {
					values[i] = Double.parseDouble(nums[i]);
				}
				INDArray vector = Nd4j.create(values);
				vectors.put(lineNumber, vector);
				lineNumber++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, INDArray> get() {
		return vectors;
	}
	
	public INDArray get(Integer i) {
		return vectors.get(i);
	}
	
	public int size() {
		return vectors.size();
	}

}
