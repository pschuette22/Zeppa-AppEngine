package com.zeppamobile.smartfollow.relationship;

import java.io.File;
import java.util.List;

import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;

import it.uniroma1.lcl.adw.ADW;
import it.uniroma1.lcl.adw.DisambiguationMethod;
import it.uniroma1.lcl.adw.ItemType;
import it.uniroma1.lcl.adw.comparison.SignatureComparison;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;



public class Testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		IndexWord w1 = null, w2 = null;
		try {
			w1 = Constants.getDictionary().lookupIndexWord(POS.NOUN, "carrot");
			w2 = Constants.getDictionary().lookupIndexWord(POS.NOUN, "broccoli");
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		
		Synset s1 = w1.getSenses().get(0);
		Synset s2 = w2.getSenses().get(0);
		
		long start = System.currentTimeMillis();
		Relationship r = RelationshipFinder.getShallowestRelationship(s1, s2);
		long finish = System.currentTimeMillis();
		
		System.out.println("Operation required " + (finish-start) + " milliseconds");
		System.out.println(r.getNodeList().toString());
		System.out.println(r.getDepth());
	*/
		
		
		
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		
		ADW pipeLine = new ADW();
	 	   
		//the two lexical items
		String text1 = "soccer#n";
		String text2 = "football#n";

		//types of the lexical items (set as auto-detect)
		ItemType text1Type = ItemType.SURFACE_TAGGED;
		ItemType text2Type = ItemType.SURFACE_TAGGED;

		//measure for comparing semantic signatures
		SignatureComparison measure = new WeightedOverlap();

		long start = System.currentTimeMillis();
		
	  	//calculate the similarity of text1 and text2
		double similarity = pipeLine.getPairSimilarity(
		    text1, text2,
		    DisambiguationMethod.ALIGNMENT_BASED, 
		    measure,
		    text1Type, text2Type);
		
		long finish = System.currentTimeMillis();
	    
		//print out the similarity
		System.out.println(similarity);
		
		System.out.println("Operation elapsed " + (finish - start) + " milliseconds");
		
	}

}
