package com.zeppamobile.smartfollow.task;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;

import it.uniroma1.lcl.adw.ADW;
import it.uniroma1.lcl.adw.ADWConfiguration;
import it.uniroma1.lcl.adw.DisambiguationMethod;
import it.uniroma1.lcl.adw.ItemType;
import it.uniroma1.lcl.adw.comparison.SignatureComparison;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;
import it.uniroma1.lcl.jlt.Configuration;

import com.zeppamobile.smartfollow.AppConfig;
import com.zeppamobile.smartfollow.StorageUtils;

import com.zeppamobile.smartfollow.comparewords.WordInfo;

/**
 * This object is used to compare tags using the ADW (Align-Disambiguate-Walk)
 * library. Refer to
 * http://www.pilevar.com/taher/pubs/ACL_2013_Pilehvar_Jurgens_Navigli.pdf for
 * more information.
 * 
 * @author Eric Most
 * 
 */
public class CompareTagsTask extends SmartFollowTask {
	// APPENGINE
	private static String configDir = "zeppa-smartfollow-1.war/WEB-INF/config/";
	// LOCAL
	// private static String configDir = "src/main/webapp/WEB-INF/config/";

	// JLT
	File jltConfig = new File(configDir, "jlt.properties");

	// Each tag represented as a list of word-parts (POS tagged)
	private List<WordInfo> tag1, tag2;

	// Value contained in [0,1] where 0 represents no similarity and 1 indicates
	// synonyms - defaults to 0
	private double similarity = 0;

	/**
	 * Create a task to compare tags
	 * 
	 * @param tag1
	 *            parsed list of words
	 * @param tag2
	 *            parsed list of words
	 */
	public CompareTagsTask(ServletContext context, List<WordInfo> tag1, List<WordInfo> tag2) {
		super(context, "CompareTagsTask");
		this.tag1 = tag1;
		this.tag2 = tag2;
	}

	/**
	 * Compares tag1 and tag2 and sets similarity accordingly. Similarity is -1
	 * if ADW causes an exception.
	 */
	public void execute() {
		try {
			if (!AppConfig.isTesting()) {
				if (!Configuration.CONFIG_FILE.equals(jltConfig)) {
					Configuration.getInstance().setConfigurationFile(jltConfig);
				}
				if (!ADWConfiguration.getConfigDir().equalsIgnoreCase(configDir)) {
					ADWConfiguration.setConfigDir(configDir);
				}
				if (!StorageUtils.getInstance().getCredentialsPath()
						.equals(configDir + "serviceAccountCredentials.json")) {
					StorageUtils.setCredentialsPath(configDir + "serviceAccountCredentials.json");
				}
			}

			ADW pipeline = new ADW();

			// The two lexical items
			String text1 = buildADWInput(tag1);
			String text2 = buildADWInput(tag2);

			// Type of input (formatting)
			ItemType text1Type = ItemType.SURFACE_TAGGED;
			ItemType text2Type = ItemType.SURFACE_TAGGED;

			// Measure for comparing semantic signatures
			// See other methods in it.uniroma1.lcl.adw.comparison
			SignatureComparison measure = new WeightedOverlap();

			// Calculate the similarity of text1 and text2
			similarity = pipeline.getPairSimilarity(text1, text2, DisambiguationMethod.ALIGNMENT_BASED, measure,
					text1Type, text2Type);
			System.out.println("Calculated similarity: " + similarity);
		} catch (Exception e) {
			System.err.println("Exception in ADW library similarity comparison");
			e.printStackTrace();
			similarity = -1;
		}

	}

	/**
	 * Construct properly formatted input for ADW library
	 * 
	 * @param tagParts
	 * @return formatted string
	 */
	public static String buildADWInput(List<WordInfo> tagParts) {
		StringBuilder sb = new StringBuilder();
		for (WordInfo word : tagParts) {
			sb.append(word.getWord());
			sb.append("#");
			switch (word.getPos()) {
			case NOUN:
				sb.append("n");
				break;
			case VERB:
				sb.append("v");
				break;
			case ADJECTIVE:
				sb.append("a");
				break;
			case ADVERB:
				sb.append("r");
				break;
			}
			sb.append(" ");
		}

		return sb.toString().trim();
	}

	/**
	 * Returns similarity [0,1] between tags, -1 on ADW error
	 */
	public double getSimilarity() {
		return similarity;
	}

	@Override
	public void finalize() {
		// TODO Auto-generated method stub

	}

	@Override
	public String abort(boolean doResume) {
		// TODO Auto-generated method stub
		return null;
	}

}
