package com.zeppamobile.smartfollow;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * Constants for smartfollow
 * 
 * @author Pete Schuette
 * 
 */
public final class Constants {

	/**
	 * @Constructor private constructor as this is a constants file
	 */
	private Constants() {
	}

	/**
	 * Compiler flag to check if unit testing
	 */
	public static boolean TESTING = false;

	public static void setTesting(boolean testing) {
		TESTING = testing;
	}

	/*
	 * Must determine this user is %65 interested in this tag to follow
	 */
	public static final double MIN_INTEREST_TO_FOLLOW = .65;

	/*
	 * 
	 * Weight values for final interest calculations
	 */
	// Calculated interest in this tag based on matching
	public static final double TAG_INTEREST_WEIGHT = .5;
	// Similarity of the two users tags, calculated popularity and event
	// popularity
	public static final double USER_SIMILARITY_WEIGHT = .3;
	// Calculated popularity of the given tag
	public static final double TAG_POPULARITY_WEIGHT = .2;

	/*
	 * 
	 * English dictionary used for processing words
	 */
	private static Dictionary dictionary;

	/**
	 * Get the Java Word Net Library for identifying words
	 * 
	 * @throws JWNLException
	 *             - if there is an error retrieving the dictionary
	 */
	public synchronized static Dictionary getDictionary() throws JWNLException {
		if (dictionary == null) {
			dictionary = Dictionary.getDefaultResourceInstance();
		}
		return dictionary;
	}

	/*
	 * TODO: Add another dictionary instance to be manipulated so slang words
	 * can be added NOTE: it should be considered that slang may vary from
	 * location to location
	 */

	/*
	 * TODO: create method to back up dictionary instance as words are added or
	 * removed
	 */

	/*
	 * TODO: research how Word Net handles short phrases and update
	 * appropriately
	 */
	
	/**
	 * The sum of POINTER_COUNTS for each weight regardless of POS
	 */
	public static final double POINTER_COUNTS[] =
		{
			345488391790d, // antonym
			245560743951d, // hypernym
			182638708473d, // hyponym
			55515266250d, // entailment
			56364030070d, // similar to
			3185421899d, // member holonym
			6058713950d, // substance holonym
			34275183060d, // part holonym
			5458747452d, // member meronym
			10524410760d, // substance meronym
			23318446150d, // part meronym
			169465198800d, // cause
			32070173500d, // particple of
			161377854460d, // see also
			38754434530d, // pertainym
			95228667580d, // attribute
			213832268040d, // verb group
			206505841906d, // derivation
			0d, // domain all
			0d, // member all
			71079736795d, // category domain
			2744592326d, // usage domain
			82015515480d, // region domain
			109025267620d, // member of category domain
			12897701550d, // member of usage domain
			12693971186d, // member of region domain
			18367888790d, // instance hypernym
			12152298280d // instance hyponym
		};
	
	/**
	 * Total number of pointer type count entries
	 */
	public static final double TOTAL_POINTER_COUNT = 2206599474648d;
	/**
	 * Constant array of all counts taken with pointer types.
	 * <p>
	 * First index represents the pointer type (0 - 27)
	 * </p>
	 * <p>
	 * Second index represents the source word part of speech
	 * </p>
	 * <p>
	 * Third index represents the target word part of speech
	 * </p>
	 */
	public static double POINTER_COUNTS_BY_POS[][][] = { //
	/*
	 * This is a constant array of all the
	 */
	/*
	 * Antonym // 0
	 */
	{ //
		{ // Nouns
			34880217080d,171358000d,112320500d,1248517900d
		}, { // Verbs
			9484337300d,613384426410d,6959896500d,8670132650d
		}, { // Adjectives
			25756777570d,9829876740d,47017174960d,14124713640d
		}, { // Adverbs
			27529108100d,12858321610d,20235725400d,65271487430d
		}, // end Nouns
	}, // 
	/*
	 * Hypernym // 1
	 */
	{ //
		{ // Nouns
			21676228452d,13526207820d,23495223609d,18110118170d
		}, { // Verbs
			38188883330d,34563919320d,46663274160d,49336889090d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Hyponym // 2
	 */
	{ //
		{ // Nouns
			6758116744d,12154227049d,8969516360d,11005571840d
		}, { // Verbs
			28595001040d,37954772830d,50978248720d,26223253890d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Entailment // 3
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			2444566000d,43597309250d,2487457000d,6985934000d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Similar To // 4
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			15224570570d,15595510870d,13917652100d,11626296530d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Member Holonym // 5
	 */
	{ //
		{ // Nouns
			1116808800d,725157950d,1021965670d,321489479d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Substance Holonym // 6
	 */
	{ //
		{ // Nouns
			6044214890d,9975200d,3680860d,843000d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Part Holonym // 7
	 */
	{ //
		{ // Nouns
			23389092530d,2616533030d,3051690300d,5217867200d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Member Meronym // 8
	 */
	{ //
		{ // Nouns
			2157421302d,886208080d,2081604340d,333513730d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Substance Meronym // 9
	 */
	{ //
		{ // Nouns
			9721034960d,7812000d,4271800d,791292000d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Part Meronym // 10
	 */
	{ //
		{ // Nouns
			10666500500d,6783646360d,2112500300d,3755798990d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Cause // 11
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			5130435000d,137442856800d,10495607000d,16396300000d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Participle of // 12
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,31016373500d,22300000d,1031500000d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * See Also // 13
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			10353979300d,78347943200d,10588886200d,7906934000d
		}, { // Adjectives
			6426785700d,7380802890d,27286675170d,13085848000d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Pertainym // 14
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			8226906730d,880647760d,749858250d,954578250d
		}, { // Adverbs
			4786876290d,5447776660d,7168601720d,10539188870d
		}, // end Nouns
	}, //
	/*
	 * Attribute // 15
	 */
	{ //
		{ // Nouns
			414148900d,321890000d,41376840120d,147800000d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			42234108860d,4609112100d,2717035000d,3407732600d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Verb Group // 16
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			25966061300d,130533544840d,28744093000d,28588568900d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Derivation // 17
	 */
	{ //
		{ // Nouns
			11095241270d,32581728080d,21183770040d,8802996820d
		}, { // Verbs
			21025956140d,40132706420d,10293883816d,34745891770d
		}, { // Adjectives
			10797097950d,8246684430d,7384469270d,111894400d
		}, { // Adverbs
			0d,0d,103521500d,0d
		}, // end Nouns
	}, // 
	/*
	 * Domain All // 18
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Member All // 19
	 */
	{ //
		{ // Nouns
			0d,0d,0d,0d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Category // 20
	 */
	{ //
		{ // Nouns
			13021975305d,757961800d,1021612400d,1108484300d
		}, { // Verbs
			20180247620d,801697500d,805209700d,2089664400d
		}, { // Adjectives
			10541881900d,593594100d,1189052780d,80813990d
		}, { // Adverbs
			15035930000d,1311048000d,1279257000d,1261306000d
		}, // end Nouns
	}, // 
	/*
	 * Usage // 21
	 */
	{ //
		{ // Nouns
			334356710d,13532200d,1128790d,487840d
		}, { // Verbs
			119469510d,188100d,0d,250600d,
		}, { // Adjectives
			1304244626d,363320d,7025530d,730660d
		}, { // Adverbs
			911803150d,16091100d,28004800d,6915390d
		}, // end Nouns
	}, // 
	/*
	 * Region // 22
	 */
	{ //
		{ // Nouns
			12137025260d,1561851540d,265038800d,1509834900d
		}, { // Verbs
			48298828750d,158300000d,79200000d,0d,
		}, { // Adjectives
			17116185920d,0d,694005310d,24000000d
		}, { // Adverbs
			171245000d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Category Member // 23
	 */
	{ //
		{ // Nouns
			16014138610d,25715489000d,26898644010d,40396996000d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Usage Member // 24
	 */
	{ //
		{ // Nouns
			1238744910d,678581940d,5683289900d,5297084800d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Region Member // 25
	 */
	{ //
		{ // Nouns
			2258553686d,225442000d,10125209500d,84766000d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Instance Hypernym // 26
	 */
	{ //
		{ // Nouns
			9777092480d,2799674210d,3466129690d,2324992410d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 
	/*
	 * Instance Hyponym // 27
	 */
	{ //
		{ // Nouns
			10907829910d,434760990d,539045820d,270661560d
		}, { // Verbs
			0d,0d,0d,0d
		}, { // Adjectives
			0d,0d,0d,0d
		}, { // Adverbs
			0d,0d,0d,0d
		}, // end Nouns
	}, // 

	};

}
