package org.liwei.buglocation.utils;

import java.util.Comparator;

public class DataTypeUtil {

	public static class TokenScore {
		public String token;
		public double score;
		public double idf;

		public TokenScore(String token) {
			this.token = token;
		}
		
		public TokenScore(String token, double score) {
			this.token = token;
			this.score = score;
		}
		
		public TokenScore(String token, double score, double idf) {
			this.token = token;
			this.score = score;
			this.idf = idf;
		}
		
		public String toString() {
			return token + " " + String.format("%.3f", score) + " " + String.format("%.3f", idf);
		}
	}
	
	public static class TokenScoreComparator implements Comparator<TokenScore> {
		public int compare(TokenScore tokenScore0, TokenScore tokenScore1) {
			if (tokenScore0.score < tokenScore1.score)
				return 1;
			else if (tokenScore0.score > tokenScore1.score) 
				return -1;
			else 
				return 0;
		}
		
	}
	
	public static class FileScore {
		public String file;
		public double score;
		
		public FileScore(String file, double score) {
			this.file = file;
			this.score = score;
		}
	}
	
	public static class FileScoreComparator implements Comparator<FileScore> {
		public int compare(FileScore fileScore0, FileScore fileScore1) {
			if (fileScore0.score < fileScore1.score)
				return 1;
			else if (fileScore0.score > fileScore1.score) 
				return -1;
			else 
				return 0;
		}
		
	}
}
