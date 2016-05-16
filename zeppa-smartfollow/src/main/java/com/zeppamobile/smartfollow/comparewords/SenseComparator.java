package com.zeppamobile.smartfollow.comparewords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;
import com.zeppamobile.smartfollow.relationship.RelationshipFinder;

import edu.stanford.nlp.ling.WordLemmaTag;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;

/**
 * Sense comparator finds the proper senses of a list of WordLemmaTags
 * 
 * @author PSchuette
 *
 */
public class SenseComparator {

	
	/**
	 * given a list of word lemma tags, find the proper senses
	 * 
	 * @param wordLemmaTags
	 * @return
	 */
	public static Map<IndexWord, Synset> formatSenses(List<WordLemmaTag> wordLemmaTags) {
		Map<IndexWord, Synset> senseMap = new HashMap<IndexWord, Synset>();

		// Convert word lemma tags into index words
		List<IndexWord> indexedWords = new ArrayList<IndexWord>();
		for (WordLemmaTag wordLemmaTag : wordLemmaTags) {
			IndexWord w = lookupIndexWord(wordLemmaTag);
			if (w != null) {
				indexedWords.add(w);
			}
		}
		
		/*
		 * Map the indexed words to their most similar senses 
		 */
		if(indexedWords.size()==1){
			// Only one indexed word, return it's most common sense
			senseMap.put(indexedWords.get(0), indexedWords.get(0).getSenses().get(0));
		} else if (indexedWords.size()>1){
			// If there is more than one, we will find the closest senses of them all
			IndexWord srcIndex = indexedWords.get(0);
			Synset srcSet = null;
			
			// Iterate through finding the closest sets 
			for(int i=1;i<indexedWords.size();i++){
				IndexWord trgIndex = indexedWords.get(i);
				Synset trgSet = null;
				if(srcSet==null){
					Synset[] sets = findClosestSenses(srcIndex, trgIndex);
					srcSet = sets[0];
					senseMap.put(srcIndex, sets[0]);
					trgSet = sets[1];
				} else {
					trgSet=findClosestSense(srcSet, trgIndex);
				}
				
				senseMap.put(trgIndex, trgSet);
				srcSet = trgSet;
			}
			
		}
		

		return senseMap;
	}

	/**
	 * Attempt to look up the indexed word
	 * 
	 * @param wordLemmaTag
	 * @return
	 */
	private static IndexWord lookupIndexWord(WordLemmaTag wordLemmaTag) {

		try {
			return Constants.getDictionary().getIndexWord(Utils.getPOSFromTag(wordLemmaTag.tag()),
					wordLemmaTag.lemma());
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
			// catch words without a tag: irrelevant
		}

		return null;
	}

	/**
	 * Find the closes set between two indexed words
	 * 
	 * @param src
	 * @param trg
	 * @return
	 */
	private static Synset[] findClosestSenses(IndexWord src, IndexWord trg) {
		Relationship shallowRelationship = null;
		Synset[] closestSet = new Synset[2];
		for (Synset srcSet : src.getSenses()) {
			for (Synset trgSet : trg.getSenses()) {
				Relationship r = RelationshipFinder.getShallowestRelationship(srcSet, trgSet);

				// If this relationship is more shallow, this is the closest
				// sense
				if (r == null || r.getDepth() < shallowRelationship.getDepth()) {
					shallowRelationship = r;
				}
			}
		}

		// If a relationship was identified, set the appropriate synsets
		if (shallowRelationship != null) {
			closestSet[0] = shallowRelationship.getSourceSynset();
			closestSet[1] = shallowRelationship.getTargetSynset();
		} else {
			// Otherwise, set the most common sets
			closestSet[0] = src.getSenses().get(0);
			closestSet[1] = trg.getSenses().get(0);
		}

		return closestSet;
	}

	/**
	 * Find the closes sense of a given src sense to a target word
	 * 
	 * @param src
	 * @param trg
	 * @return
	 */
	private static Synset findClosestSense(Synset srcSet, IndexWord trg) {
		Relationship shallowRelationship = null;
		for (Synset trgSet : trg.getSenses()) {
			Relationship r = RelationshipFinder.getShallowestRelationship(srcSet, trgSet);

			// If this relationship is more shallow, this is the closest
			// sense
			if (r == null || r.getDepth() < shallowRelationship.getDepth()) {
				shallowRelationship = r;
			}
		}

		// If a relationship was identified, set the appropriate synsets
		if (shallowRelationship != null) {
			return shallowRelationship.getTargetSynset();
		} else {
			return trg.getSenses().get(0);
		}
	}

}
