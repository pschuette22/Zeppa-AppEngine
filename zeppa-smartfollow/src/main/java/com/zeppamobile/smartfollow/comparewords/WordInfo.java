package com.zeppamobile.smartfollow.comparewords;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;

import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;

/**
 * 
 * @author Pete Schuette
 * 
 *         This class represents a word in a tag
 * 
 */
public class WordInfo {

	private String word;
	private POS pos;
	// Relatively high probability this value is null
	private IndexWord indexWord = null;
	private IndexWordSet indexWordSet = null;

	public WordInfo(String word, POS pos) {

		// Make non-noun words lowercase for library readability
		if (Character.isLetter(word.charAt(0))
				&& (pos == null || pos != POS.NOUN)) {
			word = Utils.toLowercase(word);
		}

		this.word = word;
		this.pos = pos;
		
		// If there is an identified part of speech, lookup the indexed word
		if (pos != null) {
			try {
				// If there is a defined part of speech, try to get the synset

				// Synset synset = null;
				// if (pos == POS.ADJECTIVE) {
				// synset = new AdjectiveSynset(Constants.getDictionary());
				// } else if (pos == POS.VERB) {
				// synset = new VerbSynset(Constants.getDictionary());
				// } else {
				//
				// synset = new Synset(Constants.getDictionary(), pos);
				// }

				indexWord = Constants.getDictionary()
						.lookupIndexWord(pos, word);
				
			} catch (JWNLException e) {
				e.printStackTrace();
				// all good, synset is null;
				// TODO: Log word and reason

			}
		}

	}
	
	
	/**
	 * Get the readable of the word
	 * @return
	 */
	public String getWord() {
		return word;
	}



	/**
	 * Get this word's part of speech
	 * @return
	 */
	public POS getPos() {
		return pos;
	}



	/**
	 * Get this words dictionary index
	 * @return
	 */
	public IndexWord getIndexWord() {
		return indexWord;
	}


	/**
	 * Get this words index word set
	 * @return
	 * @throws JWNLException 
	 */
	public IndexWordSet getIndexWordSet() throws JWNLException {
		if(indexWordSet==null){
			indexWordSet = Constants.getDictionary().lookupAllIndexWords(indexWord.getLemma());
		}
		return indexWordSet;
	}



}