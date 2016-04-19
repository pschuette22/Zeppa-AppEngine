package com.zeppamobile.smartfollow.relationship;

import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;

/**
 * @author Eric Most
 * 
 * WIP - may not be needed
 * 
 * This class contains different measures of similarity between two synsets
 */
public class SimilarityMeasure {

	/**
	 * WordNet::Similarity::path.pm version 2.04 (Last updated $Id:
	 * path.pm,v1.19 2008/03/27 06:21:17 sidz1979 Exp $)
	 * 
	 * Semantic Similarity Measure package implementing a simple path-length
	 * (node-counting) semantic relatedness measure.
	 * 
	 * Computes the relatedness of two word senses using a node counting scheme.
	 * The relatedness score is inversely proportional to the number of nodes
	 * along the shortest path between the two word senses.
	 * 
	 * Returns: Unless a problem occurs, the return value is the relatedness
	 * score, which belongs to the interval [0, 1]. If no path exists between
	 * the two word senses, then 0 is returned.
	 * 
	 * @param s1
	 *            synset
	 * @param s2
	 *            synset
	 * @return similarity
	 * @throws CloneNotSupportedException 
	 */
	public static double path(Synset s1, Synset s2) {
		Relationship shortestPath = RelationshipFinder.getShallowestRelationship(s1, s2);
		if (shortestPath == null) {
			return 0;
		} else {
			return (1 / shortestPath.getDepth());
		}
	}

	/**
	 * WordNet::Similarity::lin.pm version 2.04 (Last updated $Id: lin.pm,v 1.22
	 * 2008/03/27 06:21:17 sidz1979 Exp $) Semantic Similarity Measure package
	 * implementing the measure described by Lin (1998).
	 * 
	 * Computes the relatedness of two word senses using an information content
	 * scheme. The relatedness is equal to twice the information content of the
	 * LCS divided by the sum of the information content of each input synset.
	 * 
	 * @param s1
	 *            synset
	 * @param s2
	 *            synset
	 * @return similarity [0,1]
	 */
	public static double lin(Synset s1, Synset s2) {

		return 0.0;
	}
}
