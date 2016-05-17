package com.zeppamobile.smartfollow.comparewords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;
import com.zeppamobile.smartfollow.relationship.RelationshipFinder;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.stanford.nlp.ling.WordLemmaTag;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;
import it.uniroma1.lcl.adw.semsig.LKB;
import it.uniroma1.lcl.adw.semsig.SemSig;
import it.uniroma1.lcl.adw.textual.similarity.TextualSimilarity;
import it.uniroma1.lcl.adw.utils.GeneralUtils;
import it.uniroma1.lcl.adw.utils.SentenceProcessor;
import it.uniroma1.lcl.adw.utils.WordNetUtils;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
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
	 * Index and map words from a raw sentence
	 * 
	 * @param uncookedSentence
	 *            - raw sentence of space-separated words
	 * 
	 * @return indexedSynsMap - map of indexed word ids to a list of synonym
	 *         indexed word ids. This is the bread and butter of dynamic
	 *         similarity metrics
	 */
	public static Map<String, List<String>> doWordMapping(String uncookedSentence) {

		// Map word offsets to their synonyms
		Map<String, List<String>> indexedSynsMap = new HashMap<String, List<String>>();

		/*
		 * Let's bake up a batch of Semsigs!
		 */
		List<WordLemmaTag> taggedWords = SentenceProcessor.getInstance().processSentence(uncookedSentence, false);
		List<String> cookedSentence = new ArrayList<String>();
		for(WordLemmaTag w:taggedWords){
			cookedSentence.add(w.lemma()+"#"+w.tag());
		}
		
		// Determine what the appropriate senses are from cooked sentence
		List<List<SemSig>> vectors = TextualSimilarity.getInstance().getSenseVectorsFromCookedSentence(cookedSentence,
				LKB.WordNetGloss, 0);
		
		// Remove vectors that are empty
		List<List<SemSig>> remove = new ArrayList<List<SemSig>>();
		for(List<SemSig> l: vectors){
			if(l.isEmpty()){
				remove.add(l);
			}
		}
		vectors.removeAll(remove);

		if (!vectors.isEmpty()) {
			SemSig[] semSigs = new SemSig[vectors.size()];

			if (vectors.size() == 1) {
				// If there is only one word, assume it is the most common sense
				semSigs[0] = vectors.get(0).get(0);

			} else {

				WeightedOverlap overlap = new WeightedOverlap();

				SemSig[] initialSigs = findHighestSimilaritySemSigs(overlap, vectors.get(0), vectors.get(1));
				semSigs[0] = initialSigs[0];
				semSigs[1] = initialSigs[1];

				// If there are more than 2 index words, find their sense
				if (vectors.size() > 2) {
					for (int i = 2; i < vectors.size(); i++) {
						// Find the highest similarity semantic signature to the
						// word before it
						semSigs[i] = findHighestSimilaritySemSig(overlap, semSigs[i - 1], vectors.get(i));
					}
				}

			}
			// Create a map to words with semantic similarity
			for (int i = 0; i < semSigs.length; i++) {
				try {
					indexedSynsMap.put(semSigs[i].getOffset(), getSemSigSynonymOffsets(semSigs[i]));
				} catch (NullPointerException e) {
					System.out.println(i + " sense map null pointer");
				}
			}
		}

		return indexedSynsMap;
	}
	
	

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
		if (indexedWords.size() == 1) {
			// Only one indexed word, return it's most common sense
			senseMap.put(indexedWords.get(0), indexedWords.get(0).getSenses().get(0));
		} else if (indexedWords.size() > 1) {
			// If there is more than one, we will find the closest senses of
			// them all
			IndexWord srcIndex = indexedWords.get(0);
			Synset srcSet = null;

			// Iterate through finding the closest sets
			for (int i = 1; i < indexedWords.size(); i++) {
				IndexWord trgIndex = indexedWords.get(i);
				Synset trgSet = null;
				if (srcSet == null) {
					Synset[] sets = findClosestSenses(srcIndex, trgIndex);
					srcSet = sets[0];
					senseMap.put(srcIndex, sets[0]);
					trgSet = sets[1];
				} else {
					trgSet = findClosestSense(srcSet, trgIndex);
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
		} catch (NullPointerException e) {
			// catch words without a tag: irrelevant
		}

		return null;
	}

	/**
	 * Determine what two semantic signatures have the highest similarity
	 * 
	 * @param overlap
	 * @param srcSigs
	 * @param trgSigs
	 * @return highSimilaritySigs- primative array of two semantic signatures
	 *         where the first is source signature and the second is target
	 *         signature
	 */
	private static SemSig[] findHighestSimilaritySemSigs(WeightedOverlap overlap, List<SemSig> srcSigs,
			List<SemSig> trgSigs) {

		// Resulting high similarity signatures
		SemSig[] highSimilaritySigs = { srcSigs.get(0), trgSigs.get(0) };
		double highSim = -1;

		/*
		 * Compare all signatures
		 */
		for (SemSig srcSig : srcSigs) {
			for (SemSig trgSig : trgSigs) {
				double sim = overlap.compare(srcSig, trgSig, true); // sortedNormalized
																	// unused
				if (sim > highSim) {
					// update highest similarity signatures as they come
					highSim = sim;
					highSimilaritySigs[0] = srcSig;
					highSimilaritySigs[1] = trgSig;
				}
			}
		}

		// Quick check to see if

		return highSimilaritySigs;
	}

	/**
	 * Determine which target signature is the most similar to the src signature
	 * 
	 * @param overlap
	 * @param srcSig
	 * @param trgSigs
	 * @return highSimilaritySig - target semantic signature with the highest
	 *         similarity to the src signature
	 */
	private static SemSig findHighestSimilaritySemSig(WeightedOverlap overlap, SemSig srcSig, List<SemSig> trgSigs) {
		SemSig highSimilaritySig = null;
		double highSim = -1;

		// Iterate through the target signatures finding the one with the
		// highest similarity
		for (SemSig trgSig : trgSigs) {
			double sim = overlap.compare(srcSig, trgSig, true);
			if (sim > highSim) {
				highSim = sim;
				highSimilaritySig = trgSig;
			}

		}
		return highSimilaritySig;
	}

	/**
	 * Get the offsets of all the indicies for this sig
	 * 
	 * @param sig
	 * @return
	 */
	private static List<String> getSemSigSynonymOffsets(SemSig sig) {
		List<String> result = new ArrayList<String>();
		
		ISynset synset = WordNetUtils.getInstance().getSynsetFromOffset(sig.getOffset());
		
		for(ISynsetID iset: synset.getRelatedSynsets()){
			result.add(GeneralUtils.fixOffset(iset.getOffset(), iset.getPOS()));
		}
		
		return result;
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
