package com.zeppamobile.common.report;

import java.io.IOException;
import java.io.Writer;

/**
 * 
 * @author Pete Schuette
 * 
 * Report object so logs can be saved and displayed when calculating the similarity of two tags
 *
 */
public class TagComparisonReport extends SmartfollowReport {

	/**
	 * Text of tags to be compared
	 */
	private String tagText1,tagText2;
	
	/**
	 * Final Calculated Similarity of the Report
	 */
	private double calculatedSimilarity = 0.0;
	
	
	/**
	 * Writer to output logs as the come if desired
	 */
	private Writer writer;
	
	
	/**
	 * Constructor with text of tags to be compared
	 * 
	 * @param tagText1
	 * @param tagText2
	 */
	public TagComparisonReport(String tagText1, String tagText2, Writer writer){
		this.tagText1 = tagText1;
		this.tagText2 = tagText2;
		this.writer = writer;
	}
	
	/**
	 * Set the final calculation of percent similarity
	 * 
	 * @param similarity
	 */
	public void setSimilarity(double similarity){
		this.calculatedSimilarity = similarity;
	}
	
	/**
	 * Get the final calculation of percent similarity expressed as a decimal less than 1
	 * 
	 * @return calculateSimilarity
	 */
	public double getSimilarity(){
		return calculatedSimilarity;
	}
	
	/**
	 * Get the text of the tags being compared
	 * 
	 * @return text of tags being compared
	 */
	public String[] getTagTexts(){
		String[] tags = new String[2];
		tags[0] = tagText1;
		tags[1] = tagText2;
		return tags;
	}

	
	/**
	 * Log text for the report
	 */
	@Override
	public void log(String text) {
		super.log(text);
		
		// If writer was specified, write log
		if(writer!=null){
			try {
				writer.write(text+" \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
