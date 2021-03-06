/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */

package org.liwei.buglocation.utils;

//Referenced classes of package utils:
//PorterStemmer

public class Stemmer {
	
	public Stemmer() {}
	
	public static String stem(String word) {
		PorterStemmer stemmer = new PorterStemmer();
		stemmer.reset();
		stemmer.stem(word);
		return stemmer.toString();
	}
}