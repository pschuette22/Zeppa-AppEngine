package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipFinder;

import com.zeppamobile.smartfollow.Constants;

/**
 *
 * @author Pete Schuette
 * 
 *         Servlet to serve as a touch point for the relationship similarity
 *         calculator. I wrote it here temporarily because I am on the plane and
 *         cannot download the library to be added to a local project.
 *
 */
public class RelationshipSimilarityServlet extends HttpServlet {

	/**
	 * Count the number of times relationships between parts of speech are made
	 */
	private long[][][] counts;

	/**
	 * Depths of the relationships in words
	 */
	private long[][][] depths;

	/**
	 * Size of the relationships between words
	 */
	private long[][][] sizes;

	/**
	 * Start the calculation
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().println("Starting similarity calculation");

		// Initialize the counts
		counts = new long[4][4][PointerType.getAllPointerTypes().size()];
		depths = new long[4][4][PointerType.getAllPointerTypes().size()];
		sizes = new long[4][4][PointerType.getAllPointerTypes().size()];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < PointerType.getAllPointerTypes().size(); k++) {
					counts[i][j][k] = 0;
					depths[i][j][k] = 0;
					sizes[i][j][k] = 0;
				}
			}
		}

		try {
			List<POS> pos1 = POS.getAllPOS();
			List<POS> pos2 = POS.getAllPOS();

			/**
			 * Iterate through each pos and count relationships
			 */
			for (POS p1 : pos1) {
				for (POS p2 : pos2) {

					/*
					 * Find the relevant pointer types for these POS
					 */

					List<PointerType> temp1 = PointerType
							.getAllPointerTypesForPOS(p1);
					List<PointerType> temp2 = PointerType
							.getAllPointerTypesForPOS(p2);
					List<PointerType> types = new ArrayList<PointerType>();
					for (PointerType type : temp1) {
						if (temp2.contains(type)) {
							types.add(type);
						}
					}
					// If there are no relevant types
					// Note it but move on
					if (types.isEmpty()) {
						continue;
					}

					// Iterate through all the words of this part of speech
					Iterator<IndexWord> i1 = Constants.getDictionary()
							.getIndexWordIterator(p1);
					while (i1.hasNext()) {
						IndexWord w1 = i1.next();
						Iterator<IndexWord> i2 = Constants.getDictionary()
								.getIndexWordIterator(p2);
						while (i2.hasNext()) {
							IndexWord w2 = i2.next();
							// Make sure we are not comparing the same word
							if (!w1.equals(w2)) {
								List<Synset> set1 = w1.getSenses();
								List<Synset> set2 = w2.getSenses();

								// Iterate through all the synsets
								for (Synset s1 : set1) {
									for (Synset s2 : set2) {

										for (PointerType type : types) {
											try {

												Iterator<Relationship> relationshipIterator = RelationshipFinder
														.findRelationships(s1,
																s2, type)
														.iterator();
												while (relationshipIterator
														.hasNext()) {
													Relationship r = relationshipIterator
															.next();
													// Add to the overall
													// calculation
													addToCalculation(p1, p2, r);
												}

											} catch (CloneNotSupportedException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}

									}
								}

							}
						}
					}

				}
			}

		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Add to the overall calculation
	 * 
	 * @param pos1
	 *            - first word part of speech
	 * @param pos2
	 *            - second word part of speech
	 * @param r
	 *            - relationship between the two
	 */
	private void addToCalculation(POS pos1, POS pos2, Relationship r) {

		int i = pos1.getId() - 1;
		int j = pos2.getId() - 1;
		int k = PointerType.getAllPointerTypes().indexOf(r.getType());
		// Increment the number of calculations done between these pointer types
		counts[i][j][k]++;

		// Increment the overall depth count
		depths[i][j][k] += r.getDepth();

		// Increment the overall size count
		sizes[i][j][k] += r.getSize();
		
	}

	/**
	 * Print the counts of the relationships
	 */
	private void printCounts(PrintWriter w) {

		/*
		 * 
		 */
		List<PointerType> types = PointerType.getAllPointerTypes();
		for (int i = 0; i < 4; i++) {
			String label1 = POS.getPOSForId(i+1).getLabel();
			for (int j = 0; j < 4; j++) {
				String label2 = POS.getPOSForId(j+1).getLabel();
				
				w.println(label1 + " to " + label2 + " relationship counts");
				
				// Totals
				long totalCount = 0;
				long totalDepth = 0;
				long totalSize = 0;
				
				for(int k = 0; k < types.size(); k++){
					totalCount+= counts[i][j][k];
					totalDepth+= depths[i][j][k];
					totalSize+= sizes[i][j][k];
					
					w.println(types.get(k).getLabel() + " pointer type");
					w.println("Count Sum - " + counts[i][j][k]);
					w.println("Depth Sum - " + depths[i][j][k]);
					w.println("Size Sum  - " + sizes[i][j][k]);
				}
				
				w.println("Total relationship counts");
				w.println("Total Count Sum - " + totalCount);
				w.println("Total Depth Sum - " + totalDepth);
				w.println("Total Size Sum  - " + totalSize);
				
			}
		}

	}

}
