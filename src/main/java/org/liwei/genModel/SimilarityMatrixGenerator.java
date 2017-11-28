package org.liwei.genModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import org.liwei.data.BugReport;
import org.liwei.data.BugReportRepository;
import org.liwei.data.CodeMetrics;
import org.liwei.data.CodeRepository;
import org.liwei.data.SimilarBugReport;
import org.liwei.similarity.GetSimilarBugReport;
import org.liwei.similarity.Similarity;
import org.liwei.similarity.SimilarityGenerator;
import org.liwei.util.DataTypeUtil.FileScore;
import org.liwei.util.DataTypeUtil.FileScoreComparator;
import org.liwei.util.Index;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
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
	
	public static final int TOP_SIMILAR_DTS = 100;
	public static final int TOP_FILES_FROM_VOTES = 10000;
	
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

	private HashMap<String, Double> votedScores;
	
	/**
	 * Used to calculate similarity between dts and dts or dts and code file.
	 */
	private Similarity sim;
	
	private SimilarityGenerator simGenerator;
	
	/**
	 * Constructor
	 * @param brRepository The given bug report repository.
	 * @param codeRepository The given code souce files repository.
	 */
	public SimilarityMatrixGenerator(BugReportRepository brRepository, CodeRepository codeRepository, Similarity sim) {
		this.brRepository = brRepository;
		this.codeRepository = codeRepository;
		if ((codeRepository != null) && (brRepository != null)) 
			codeRepository.attachRelatedDTS(brRepository);
		this.sim = sim;
		
		this.simGenerator = new SimilarityGenerator(sim).setBugReports(brRepository.getBugReports());
		this.maxGoodBadRate = MAX_GOOD_BAD_RATE;
		minVote = Double.MAX_VALUE;
		maxVote = Double.MIN_VALUE;
		minFrequency = Double.MAX_VALUE;
		maxFrequency = Double.MIN_VALUE;
		minRecency = Double.MAX_VALUE;
		maxRecency = Double.MIN_VALUE;
	}
	
	/**
	 * set sim to calculate similarity
	 * @param sim input instance of Similarity
	 */
	public void setSim(Similarity sim) {
		this.sim = sim;
	}
	
	public void setMaxGoodBadRate(double maxGoodBadRate) {
		this.maxGoodBadRate = maxGoodBadRate;
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
			HashMap<String, INDArray> generated = generate(br);
			result.addAll(generated.values());
		}
		// Normalize and write to the file.
		logger.info("Normalizing vectors...");
		normalize(result);
		logger.info("Writing vectors...");
		for (INDArray vector : result) {
			// write the array
			if (vector.data().getDouble(0) < 0.999999)
				writer.write("0");
			else 
				writer.write("1");
			for (int i = 1; i < vector.columns(); i++) {
				writer.write(",");
				writer.write(Double.toString(vector.data().getDouble(i)));
			}
			writer.newLine();
		}
		writer.close();
	}
	
	//TODO
	public HashMap<String, INDArray> generate(BugReport br) {
		// ignore bug report with no related modified files
		if (br.getModifiedFiles().size() == 0) 
			return new HashMap<String, INDArray>();
		
		List<SimilarBugReport> similarBrs = simGenerator.getSimilarBugReports(br, TOP_SIMILAR_DTS);
		
		// vote for candidate files.
		HashMap<String, Double> voteScores = new HashMap<String, Double>();
		for (SimilarBugReport similarBr : similarBrs) {
			if (br.getId().equals(similarBr.getId()))
				continue;
			updateFileScores(voteScores, similarBr.getModifiedFiles(), similarBr.similarity);
		}
		if (voteScores.isEmpty())
			return new HashMap<String, INDArray>();
		
		// pick up topN candidate files.
		PriorityQueue<FileScore> heap = new PriorityQueue<FileScore>(voteScores.size(), new FileScoreComparator());
		for (Entry<String, Double> score : voteScores.entrySet()) 
			heap.add(new FileScore(score.getKey(), score.getValue()));
		List<FileScore> candidates = new ArrayList<FileScore>();
		int goodCount = (int)Math.round(br.getModifiedFiles().size() * maxGoodBadRate + 0.5);
		while (!heap.isEmpty()) {
			FileScore score = heap.poll();
			if (br.isModified(score.file))
				candidates.add(score);
			else {
				if (goodCount > 0) {
					candidates.add(score);
					goodCount--;
				}
			}
		}
		
		return generateVectors(br, candidates);
	}
	
	/**
	 * Generate the training vectors, given a set of candidate source files.
	 * @param br the related bug report 
	 * @param candidates a list of candidate source files
	 * @return a list of generated vectors
	 */
	private HashMap<String, INDArray> generateVectors(BugReport br, List<FileScore> candidates) {
		HashMap<String, INDArray> result = new HashMap<String, INDArray>();
		
		for (FileScore fileScore : candidates) {
			String path = fileScore.file;
			// Verfications.
			CodeMetrics codeMetrics = codeRepository.getCodeMetrics(path);
			if (codeMetrics == null) 
				continue;
			// get features
			double votedScore = fileScore.score;
			long brTime = br.getCommitDate().getTime();
			double frequency = 0.0;
			double recency = 1.0 / (1.0 + 15.0 * 12.0); // 15 years ago by default
			int index = codeMetrics.locateChangePoint(brTime);
			if (index > 0) {
				frequency = (double)codeMetrics.countChangeFrequency(index);
				long lastModifiedTime = codeMetrics.getChangePoint(index);
				double monthDurationTime = ((double)(brTime - lastModifiedTime)) / 1000.0 / 3600.0 / 24.0 / 30.0;
				recency = 1.0 / (1.0 + monthDurationTime);
			}
			
			// Update max-min to prepare normalization.
			if (votedScore > maxVote) 
				maxVote = votedScore;
			if (votedScore < minVote)
				minVote = votedScore;
			if (frequency > maxFrequency) 
				maxFrequency = frequency;
			if (frequency < minFrequency)
				minFrequency = frequency;
			if (recency > maxRecency)
				maxRecency = recency;
			if (recency < minRecency)
				minRecency = recency;
			
			// Merge vectors
			Double fileSimilarity = sim.similarityBySameWords(br, codeMetrics);
			double label;
			if (br.isModified(path))
				label = 1.0;
			else 
				label = 0.0;
			
			double[] values = new double[] {label, votedScore, fileSimilarity, frequency, recency};
			result.put(path, Nd4j.create(values));
		}
		
		return result;
	}
	
	/**
	 * Pre-calculate some features(e.g.voted scores)
	 * This method must be invoked before feature generated for each bug report.
	 * For each bug report, it calculates the voted scores and stores them in.
	 * 'votedScores'
	 * @param br The bugReport to preprocess
	 */
	public void prepareGenerationForBugReport(BugReport br) {
		votedScores = new HashMap<String, Double>();
		for (BugReport r : brRepository.getBugReports().values()) {
			if (r.getId().equals(br.getId())) // Exclude self
				continue;
			
			Double similarity = sim.similarityBySameWords(br, r);
			for (Index index : r.getModifiedFiles()) {
				String path = index.getPath();
				if (!votedScores.containsKey(path))
					votedScores.put(path, 0.0);
				else 
					votedScores.put(path, votedScores.get(path) + similarity);
			}
		}
	}
	
	/**
	 * Generate an unnormalized vectors of scores for (br, code).
	 * @param br The given bug report.
	 * @param code The given code metrics
	 * @return the generate vectors
	 */
	public double[] generate(BugReport br, CodeMetrics code) {
		return generate(br, code, false);
	}
	
	/**
	 * Given a bug report and a source file, generate a vector of scores
	 * 		The result may be normalize. determined by the parameter "normalize"
	 * @param br The given bug report.
	 * @param code The given code metrics.
	 * @param normalize Whether to normalize the generated vector
	 * @return The generate vectors.
	 */
	public double[] generate(BugReport br, CodeMetrics codeMetrics, boolean normalize) {
		// Calculate voted score.
		double votedScore = 0.0;
		if (votedScores.containsKey(codeMetrics.getPath()))
			votedScore = votedScores.get(codeMetrics.getPath());
		
		// Calculate file similarity
		double fileSimilarity = sim.similarityBySameWords(br, codeMetrics);
		
		// Calculate extra features.
		long brTime = br.getCommitDate().getTime();
		double frequency = 0.0;
		double recency = 1.0 / (1.0 + 15.0 * 12.0); // 15 years ago by default
		int index = codeMetrics.locateChangePoint(brTime);
		if (index > 0) {
			frequency = (double)codeMetrics.countChangeFrequency(index);
			long lastModifiedTime = codeMetrics.getChangePoint(index);
			double monthDurationTime = ((double)(brTime - lastModifiedTime)) / 1000.0 / 3600.0 / 24.0 / 30.0;
			recency = 1.0 / (1.0 + monthDurationTime);
		}
		
		// Update max-min to prepare normalization.
		if (votedScore > maxVote) 
			maxVote = votedScore;
		if (votedScore < minVote)
			minVote = votedScore;
		if (frequency > maxFrequency) 
			maxFrequency = frequency;
		if (frequency < minFrequency)
			minFrequency = frequency;
		if (recency > maxRecency)
			maxRecency = recency;
		if (recency < minRecency)
			minRecency = recency;
		
		double[] features = new double[] {votedScore, fileSimilarity, frequency, recency};
		if (normalize)
			normalize(features);
		return features;
	}
	
	public List<FileResult> generateRankingMatrix(BugReport br, Integer qid, boolean isTraining) {
		List<FileResult> finals = new LinkedList<FileResult>();
		
		// Do some preprocessing to avoid redundant calculation.
		prepareGenerationForBugReport(br);
		
		// Enum code files.
		List<FileResult> vectors = new ArrayList<FileResult>();
		for (CodeMetrics codeMetrics : codeRepository.getCodeMetricsSet().values()) {
			double[] v = generate(br, codeMetrics, !isTraining);
			if (v == null)
				continue;
			boolean isModified;
			if (isTraining) {
				if (br.isModified(codeMetrics.getPath()))
					isModified = true;
				else 
					isModified = false;
				vectors.add(new FileResult(codeMetrics.getPath(), isModified, v));
			}
			
			// Sort and add to the final results.
			vectors.sort(new FileComparator());
			Integer count = 0;
			for (FileResult result : vectors) {
				result.qid = qid;
				finals.add(result);
				count++;
				if (count >= TOP_SIMILAR_CODE)
					break;
			}
		}
		
		return finals;
	}
	
	/**
	 * generate training data for svm-learn-to-rank.
	 * @param outputFile outputFile to write.
	 * @param isTraining
	 */
	public void generateRankingMatrix(File outputFile, boolean isTraining) {
		List<FileResult> finals = new LinkedList<FileResult>();
		
		// Iterate bug report.
		Integer qid = 1;
		for (BugReport br : brRepository.getBugReports().values()) {
			if ((qid % 20) == 0)
				logger.info(qid.toString() + " records handled.");
			qid++;
			if (br.getModifiedFiles().size() == 0)
				continue;
			
			// Enumerate code files.
			List<FileResult> vectors = generateRankingMatrix(br, qid, isTraining);
			finals.addAll(vectors);
		}
		
		// Normalize and output.
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
			for (FileResult result : finals) {
				normalize(result.features);
				
				Integer rank;
				if (result.isModified)
					rank = TOP_SIMILAR_CODE;
				else 
					rank = 1;
				writeRankingFeatures(writer, rank, result);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * generate unnormalized training data for svm-learn-to-rank.
	 * @param outputFile outputFile to write.
	 * @param isTraining
	 */
	public void generateUnnormalizedRankingMatrix(File outputFile, boolean isTraining) {
		List<FileResult> finals = new LinkedList<FileResult>();
		
		// Iterate bug report.
		Integer qid = 1;
		for (BugReport br : brRepository.getBugReports().values()) {
			if ((qid % 2) == 0)
				logger.info(qid.toString() + " records handled.");
			if ((qid % 2 == 0)) {
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));
					for (FileResult result : finals) {
						Integer rank;
						if (result.isModified)
							rank = TOP_SIMILAR_CODE;
						else 
							rank = 1;
						writeRankingFeatures(writer, rank, result);
					}
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finals.clear();
			}
			qid++;
			if (br.getModifiedFiles().size() == 0)
				continue;
			
			// Enumerate code files.
			List<FileResult> vectors = generateRankingMatrix(br, qid, isTraining);
			finals.addAll(vectors);
		}
		
	}
	
	/**
	 * Write a line to the vector file.
	 * @param writer The writer on the file.
	 * @param rank The rank of the vector.
	 * @param result The vector to write.
	 */
	public static void writeRankingFeatures(BufferedWriter writer, Integer rank, FileResult result) {
		try {
			writer.write(rank.toString() + " qid:" + result.qid.toString());
			for (Integer column = 1; column <= result.features.length; column++) {
				writer.write(" " + column.toString() + ":" + result.features[column - 1]);
			}
			writer.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * when a similar bug report comes, update the votes.
	 * @param fileScores the voted scores to update
	 * @param files the list of modified files related the bug report
	 * @param similarity similarity of the bug report
	 */
	private void updateFileScores(HashMap<String, Double> fileScores, Set<Index> files, Double similarity) {
		if (files.isEmpty())
			return;
		
		for (Index index : files) {
			String path = index.getPath();
			if (!fileScores.containsKey(path))
				fileScores.put(path, 0.0);
			fileScores.put(path, fileScores.get(path) + similarity);
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
	
	public void normalize(Collection<INDArray> set) {
		for (INDArray vector : set) {
			double value = maxMinNormalize(maxVote, minVote, vector.data().getDouble(1));
			vector.data().put(1, value);
			value = maxMinNormalize(maxFrequency, minFrequency, vector.data().getDouble(3));
			vector.data().put(3, value);
			value = maxMinNormalize(maxRecency, minRecency, vector.data().getDouble(4));
			vector.data().put(4, value);
		}
	}
	
	/**
	 * Normalize an array of features.
	 * @param features The feature array to normalize.
	 */
	public void normalize(double[] features) {
		features[0] = this.maxMinNormalize(maxVote, minVote, features[0]);
		features[2] = this.maxMinNormalize(maxFrequency, minFrequency, features[2]);
		features[3] = this.maxMinNormalize(maxRecency, minRecency, features[3]);
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
