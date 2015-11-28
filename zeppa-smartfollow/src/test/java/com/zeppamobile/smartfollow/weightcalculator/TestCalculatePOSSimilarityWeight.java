package com.zeppamobile.smartfollow.weightcalculator;

import org.junit.Test;

public class TestCalculatePOSSimilarityWeight {

	@Test
	public void testPrintFormatting() {
		CalculatePOSSimilarityWeight calculation = new CalculatePOSSimilarityWeight(1);
		
		calculation.printFormattedResults(System.out);
		
	}
	
}
