package org.liwei.genModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.liwei.data.BugReport;
import org.liwei.data.BugReportRepository;
import org.liwei.data.CodeRepository;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimilarityMatrixGenerator {
	private static Logger logger = LoggerFactory.getLogger(SimilarityMatrixGenerator.class);
	
	public static class FileResult {
		private final static int KEY_INDEX = 0;
		private final static double MODIFIED_SCORE = 1000000.0;
		
		public String path;
		public boolean isModified;
		public double[] features;
		
		public Integer qid;
		public BugReport br;
		public double rank;
		
		public FileResult(String path, boolean isModified, double[] features) {
			this.path = path;
			this.isModified = isModified;
			this.features = features;
		}
		
		public double getScore() {
			if (isModified)
				return MODIFIED_SCORE + features[KEY_INDEX];
			else
				return features[KEY_INDEX];
		}
	}

	private static class FileComparator implements Comparator<FileResult> {
		public int compare(FileResult r1, FileResult r2) {
			if (r1.getScore() > r2.getScore()) 
				return -1;
			else if (r1.getScore() == r2.getScore()) 
				return 0;
			else 
				return 1;
		}
	}
	
	
	public static final int TOP_SIMILAR_CODE = 300;
	
	/**
	 * Specify the maximal rate of negative samples against the positive samples.
	 * For example, if we have retrieved ONE positive sample, the number of negative
	 * sample is no more than MAX_GOOD_BAD_RATE.
	 */
	public static final double MAX_GOOD_BAD_RATE = 500;
	
	public static final String LOGGER = "D:\\Data\\working\\logger.txt";
	
	/**
	 * A set of bug reports.
	 */
	private BugReportRepository brRepository;
	
	/**
	 * A set of code source files.
	 */
	private CodeRepository codeRepository;

	/**
	 * The maximal negative-positive rate for this instance.
	 */
	private double maxGoodBadRate;
	
	/**
	 * Used to normalize the voted score. 
	 */
	private double minVote;
	private double maxVote;
	
	/**
	 * Used to normalize the frequency.
	 */
	private double minFrequency;
	private double maxFrequency;

	/**
	 * Used to normalize the recency.
	 */
	private double minRecency;
	private double maxRecency;

	private HashMap<String, Double> votedScore;
	
	/**
	 * Constructor
	 * @param brRepository The given bug report repository.
	 * @param codeRepository The given code souce files repository.
	 */
	public SimilarityMatrixGenerator(BugReportRepository brRepository, CodeRepository codeRepository) {
		this.brRepository = brRepository;
		this.codeRepository = codeRepository;
		if ((codeRepository != null) && (brRepository != null)) 
			codeRepository.attachRelatedDTS(brRepository);
		this.maxGoodBadRate = MAX_GOOD_BAD_RATE;
		minVote = Double.MAX_VALUE;
		maxVote = Double.MIN_VALUE;
		minFrequency = Double.MAX_VALUE;
		maxFrequency = Double.MIN_VALUE;
		minRecency = Double.MAX_VALUE;
		maxRecency = Double.MIN_VALUE;
	}
	
	public void saveParameters(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write("minVote:" + minVote + "\n");
			writer.write("maxVote:" + maxVote + "\n");
			writer.write("minFrequency:" + minFrequency + "\n");
			writer.write("maxFrequency:" + maxFrequency + "\n");
			writer.write("minRecency:" + minRecency + "\n");
			writer.write("maxRecency:" + maxRecency + "\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadParameters(File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = line.split(":");
				double value = Double.parseDouble(words[1]);
				switch (words[0]) {
				case "minVote":
					minVote = value;
					break;
				case "maxVote":
					maxVote = value;
					break;
				case "minFrequency":
					minFrequency = value;
					break;
				case "maxFrequency":
					maxFrequency = value;
					break;
				case "minRecency":
					minRecency = value;
					break;
				case "maxRecency":
					maxRecency = value;
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void mergeParameters(SimilarityMatrixGenerator g) {
		minVote = Double.min(minVote, g.minVote);
		maxVote = Double.max(maxVote, g.maxVote);
		minFrequency = Double.min(minFrequency, g.minFrequency);
		maxFrequency = Double.max(maxFrequency, g.maxFrequency);
		minRecency = Double.min(minRecency, g.minRecency);
		maxRecency = Double.max(maxRecency, g.maxRecency);
	}
	
	public void setMaxGoodBadRate(double maxGoodBadRate) {
		this.maxGoodBadRate = maxGoodBadRate;
	}
	
	/**
	 * Given a set of bug reports, generate the traning set.
	 * @param outputFile The output file to write.
	 * @throws Exception 
	 */
	public void generate(File outputFile) throws Exception {
		List<INDArray> result = new LinkedList<INDArray>();
		// generate vectors into a list
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
		Integer count = 0;
		for (BugReport br : brRepository.getBugReports().values()) {
			count++;
			if (count % 500 == 0) 
				logger.info(count + " bug reports handled.");
			if (br.getModifiedFiles().size() == 0)
				continue;
			
		}
	}
	
	/**
	 * generate training data for svm-learn-to-rank.
	 * @param outputFile outputFile to write.
	 * @param isTraining
	 */
	public void generateRankingMatrix(File outputFile, boolean isTraining) {
		
	}
	
	/**
	 * Write a line to the vector file.
	 * @param writer The writer on the file.
	 * @param rank The rank of the vector.
	 * @param result The vector to write.
	 */
	public static void writeRankingFeatures(BufferedWriter writer, Integer rank, FileResult result) {
		
	}
	
	/**
	 * Normalize an array of features.
	 * @param features The feature array to normalize.
	 */
	public void normalize(double[] features) {
		
	}
	
	public void normalize(Collection<INDArray> set) {
		for (INDArray vector : set) {
			
		}
	}
	
	public static String vector2String(INDArray vector) {
		StringBuilder result = new StringBuilder();
		if (vector.data().getDouble(0) < 0.999999)
			result.append("0");
		else 
			result.append("1");
		for (int i = 1; i < vector.columns(); i++) {
			result.append(",");
			result.append(Double.toString(vector.data().getDouble(i)));
		}
		result.append("\n");
		return result.toString();
	}
	
	/**
	 * Min-max normalization.
	 * @param max The max value.
	 * @param min The min value.
	 * @param value The value to normalize.
	 * @return The normalized value.
	 */
	private double maxMinNormalize(double max, double min, double value) {
		if (max == min) 
			return 0.0;
		return (value - min) / (max - min);
	}
}
