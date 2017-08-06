package org.liwei.buglocation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class INDArrayTest {
	public static void main(String[] args) {
		INDArray nd = Nd4j.create(new float[]{1,2,3,4}, new int[]{2,2});
		System.out.println(nd);
		INDArray nd2 = Nd4j.create(new float[]{5,6},new int[]{2,1}); //vector as column
	    INDArray nd3 = Nd4j.create(new float[]{5,6},new int[]{2}); //vector as row
		nd.addiColumnVector(nd2);
		System.out.println(nd);
		nd.addiRowVector(nd3);
		System.out.println(nd);
	}
}
