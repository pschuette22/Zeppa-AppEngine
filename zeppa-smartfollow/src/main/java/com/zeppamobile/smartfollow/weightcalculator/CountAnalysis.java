package com.zeppamobile.smartfollow.weightcalculator;

import java.util.List;

import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;

import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;

/**
 * 
 * @author PSchuette Quick program to count review the counts we just looked at
 */
public class CountAnalysis {

	public static void main(String[] args) {

		System.out.println("Part of speech source counts:");
		List<POS> sourceParts = POS.getAllPOS();
		for (POS source : sourceParts) {
			long sourceCount = 0;
			List<POS> targetParts = POS.getAllPOS();
			for (POS target : targetParts) {
				List<PointerType> types = PointerType.getAllPointerTypes();
				for (PointerType type : types) {
					sourceCount += Constants.POINTER_COUNTS[Utils
							.getPointerTypeIndex(type)][Utils
							.getPOSIndex(source)][Utils.getPOSIndex(target)];
				}
			}
			System.out.println(source.getLabel() + " source counts = "
					+ sourceCount);
			System.out.println(source.getLabel() + " percent of total = "
					+ (float) sourceCount / Constants.TOTAL_POINTER_COUNT
					+ "\n");
		}

		System.out.println("\n --------------------- \n");

		System.out.println("Part of speech Target counts:");
		List<POS> targetParts1 = POS.getAllPOS();
		for (POS target : targetParts1) {
			long sourceCount = 0;
			List<POS> sourceParts1 = POS.getAllPOS();
			for (POS source : sourceParts1) {
				List<PointerType> types = PointerType.getAllPointerTypes();
				for (PointerType type : types) {
					sourceCount += Constants.POINTER_COUNTS[Utils
							.getPointerTypeIndex(type)][Utils
							.getPOSIndex(source)][Utils.getPOSIndex(target)];
				}
			}
			System.out.println(target.getLabel() + " target counts = "
					+ sourceCount);
			System.out.println(target.getLabel() + " percent of total = "
					+ (float) sourceCount / Constants.TOTAL_POINTER_COUNT
					+ "\n");
		}
		
		System.out.println("\n --------------------- \n");

		System.out.println("Pointer Weights:");
		for (PointerType type : PointerType.getAllPointerTypes()) {
			long pointerCount = 0;
			for (POS source : POS.getAllPOS()) {
				for (POS target : POS.getAllPOS()) {
					pointerCount += Constants.POINTER_COUNTS[Utils
												.getPointerTypeIndex(type)][Utils
												.getPOSIndex(source)][Utils.getPOSIndex(target)];
				}
			}
			System.out.println(type.getLabel() + " type count = "
					+ pointerCount);
			System.out.println(type.getLabel() + " percent of total = "
					+ (float) pointerCount / Constants.TOTAL_POINTER_COUNT
					+ "\n");
		}

		System.out.println("\n --------------------- \n");

		System.out.println("Pointer Weights by POS, (source weight):");
		List<PointerType> types = PointerType.getAllPointerTypes();
		for (PointerType type : types) {
			for (POS source : POS.getAllPOS()) {
				for (POS target : POS.getAllPOS()) {
					System.out.println(source.getLabel()
							+ " - "
							+ type.getLabel()
							+ " -> "
							+ target.getLabel()
							+ " = "
							+ (float) Constants.POINTER_COUNTS[Utils
									.getPointerTypeIndex(type)][Utils
									.getPOSIndex(source)][Utils
									.getPOSIndex(target)]
							/ Constants.TOTAL_POINTER_COUNT);
				}
			}
		}

		System.out.println("\n --------------------- \n");
		System.out.println("Pointer Weights by POS, (target weight):");
		List<PointerType> types1 = PointerType.getAllPointerTypes();
		for (PointerType type : types1) {
			for (POS target : POS.getAllPOS()) {
				for (POS source : POS.getAllPOS()) {
					System.out.println(source.getLabel()
							+ " - "
							+ type.getLabel()
							+ " -> "
							+ target.getLabel()
							+ " = "
							+ (float) Constants.POINTER_COUNTS[Utils
									.getPointerTypeIndex(type)][Utils
									.getPOSIndex(source)][Utils
									.getPOSIndex(target)]
							/ Constants.TOTAL_POINTER_COUNT);
				}
			}
		}

		System.out.println("\n --------------------- \n");
		System.out.println("Pointer Weights by POS, (Bi-directional weight):");
		List<PointerType> types2 = PointerType.getAllPointerTypes();
		for (PointerType type : types2) {
			for (POS target : POS.getAllPOS()) {
				for (POS source : POS.getAllPOS()) {
					System.out
							.println(source.getLabel()
									+ " - "
									+ type.getLabel()
									+ " -> "
									+ target.getLabel()
									+ " = "
									+ ((float) Constants.POINTER_COUNTS[Utils
											.getPointerTypeIndex(type)][Utils
											.getPOSIndex(source)][Utils
											.getPOSIndex(target)] + (float) Constants.POINTER_COUNTS[Utils
											.getPointerTypeIndex(type)][Utils
											.getPOSIndex(target)][Utils
											.getPOSIndex(source)])
									/ Constants.TOTAL_POINTER_COUNT);
				}
			}
		}
	}
}
