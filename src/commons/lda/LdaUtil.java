/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 17:03</create-date>
 *
 * <copyright file="Corpus.java" company="">
 * Copyright (c) 2003-2014. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact to get more information.
 * </copyright>
 */
package br.ufc.great.hadoop.commons.lda;

import java.util.*;

/**
 * @author hankcs, Pedro Almir (improvements and adaptation to Hadoop)
 */
public class LdaUtil {
	/**
	 * To translate a LDA matrix to readable result
	 * 
	 * @param phi        the LDA model
	 * @param vocabulary
	 * @param limit      limit of max words in a topic
	 * @return a map array
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Double>[] translate(double[][] phi, Vocabulary vocabulary, int limit) {
		limit = Math.min(limit, phi[0].length);
		Map<String, Double>[] result = new Map[phi.length];
		for (int k = 0; k < phi.length; k++) {
			Map<Double, String> rankMap = new TreeMap<Double, String>(Collections.reverseOrder());
			for (int i = 0; i < phi[k].length; i++) {
				rankMap.put(phi[k][i], vocabulary.getWord(i));
			}
			Iterator<Map.Entry<Double, String>> iterator = rankMap.entrySet().iterator();
			result[k] = new LinkedHashMap<String, Double>();
			for (int i = 0; i < limit; ++i) {
				Map.Entry<Double, String> entry = iterator.next();
				result[k].put(entry.getValue(), entry.getKey());
			}
		}
		return result;
	}

	public static Map<String, Double> translate(double[] tp, double[][] phi, Vocabulary vocabulary, int limit) {
		Map<String, Double>[] topicMapArray = translate(phi, vocabulary, limit);
		double p = -1.0;
		int t = -1;
		for (int k = 0; k < tp.length; k++) {
			if (tp[k] > p) {
				p = tp[k];
				t = k;
			}
		}
		return topicMapArray[t];
	}

	/**
	 * To print the result in a well formatted form
	 * 
	 * @param result
	 */
	public static void explain(Map<String, Double>[] result) {
		int i = 0;
		for (Map<String, Double> topicMap : result) {
			System.out.printf("topic %d :\n", i++);
			explain(topicMap);
			System.out.println();
		}
	}

	public static void explain(Map<String, Double> topicMap) {
		for (Map.Entry<String, Double> entry : topicMap.entrySet()) {
			System.out.println(entry);
		}
	}
	
}
