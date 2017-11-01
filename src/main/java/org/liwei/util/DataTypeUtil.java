package org.liwei.util;

import java.util.Comparator;

public class DataTypeUtil {

	public class TokenScore {
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
	
}
