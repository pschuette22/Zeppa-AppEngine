package com.zeppamobile.smartfollow.relationship;

import java.util.Iterator;
import java.util.List;

import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.data.list.PointerTargetTreeNode;
import net.sf.extjwnl.data.list.PointerTargetTreeNodeList;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.data.relationship.AsymmetricRelationship;
import net.sf.extjwnl.data.relationship.SymmetricRelationship;

/**
 * @author Eric Most
 * 
 * WIP - may not be needed
 * 
 * The purpose of this class is to create our own implementations of
 * methods in the extJWNL RelationshipFinder class so we can search for
 * relationships more efficiently and conduct customized searches.
 */
public class RelationshipFinder {

	private static final int DEFAULT_ASYMMETRIC_SEARCH_DEPTH = Integer.MAX_VALUE;// Integer.MAX_VALUE;
	private static final int DEFAULT_SYMMETRIC_SEARCH_DEPTH = 2;

	/**
	 * Feeds into the the getShortestPath(Synset s1, Synset s2, PointerType[]
	 * pointerTypes) method
	 * 
	 * @param s1
	 *            synset
	 * @param s2
	 *            synset
	 * @return relationship
	 */
	public static Relationship getShallowestRelationship(Synset s1, Synset s2) {
		List<PointerType> types = PointerType.getAllPointerTypes();
		return getShallowestRelationship(s1, s2, types.toArray(new PointerType[types.size()]));
	}

	/**
	 * Helper method to efficiently return the shortest path between two
	 * synsets. The goal is to identify this relationship as efficiently as
	 * possible. Returns null if no path between the synsets is found.
	 * 
	 * @param s1
	 *            synset
	 * @param s2
	 *            synset
	 * @return relationship
	 * @throws CloneNotSupportedException 
	 */
	public static Relationship getShallowestRelationship(Synset s1, Synset s2,
			PointerType[] pointerTypes) {
		// Sort PointerTypes by descending weight according to the
		// lookup table in com.zeppa.smartfollow.Constants.POINTER_COUNTS
		// Leave the original array alone
		PointerType[] clonedTypes = pointerTypes.clone();
		Utils.sortDescending(clonedTypes);

		Relationship path = null;
		int pathLength = DEFAULT_ASYMMETRIC_SEARCH_DEPTH;
		
		for (PointerType type : clonedTypes) {
			try {
				// Building relationship tree node lists is a little different
				// depending on whether or not type is symmetric
				Relationship r = (type.isSymmetric() ? findSymmetricRelationship(
						s1, s2, type, pathLength)
						: findAsymmetricRelationship(s1, s2, type, pathLength));
				
				// Update
				if (r != null && r.getDepth() < pathLength) {
					path = r;
					pathLength = r.getDepth();
				}
			} catch (JWNLException e) {
				System.err.println("Error finding relationships");
				e.printStackTrace();
			} catch (CloneNotSupportedException e) {
				System.err.println("Error cloning PointerType array");
				e.printStackTrace();
			}	
		}
		
		return path;
	}

	/**
	 * Adapted from net.sf.extjwnl.relationship.RelationshipFinder This method
	 * builds pointer target trees for both the source and target synsets up to
	 * the given depth. These trees are analyzed to find the shortest
	 * relationship s1 -> -> -> s2.
	 * 
	 * @param sourceSynset
	 *            synset
	 * @param targetSynset
	 *            synset
	 * @param type
	 *            pointer type
	 * @param depth
	 *            search depth
	 * @return the shallowest relationship found less than search depth, or null
	 * @throws CloneNotSupportedException
	 * @throws JWNLException
	 */
	private static Relationship findAsymmetricRelationship(Synset sourceSynset,
			Synset targetSynset, PointerType type, int depth)
			throws CloneNotSupportedException, JWNLException {
		// Build tree for source synset
		List<PointerTargetNodeList> sourceRelations = new PointerTargetTree(
				sourceSynset, PointerUtils.makePointerTargetTreeList(
						sourceSynset, type, depth)).reverse();

		// Build tree for target synset
		List<PointerTargetNodeList> targetRelations = new PointerTargetTree(
				targetSynset, PointerUtils.makePointerTargetTreeList(
						targetSynset, type, depth)).reverse();

		PointerTargetNodeList sourceRelation;
		Relationship shallowest = null;

		for (Iterator<PointerTargetNodeList> iter = sourceRelations.iterator(); iter
				.hasNext();) {
			sourceRelation = (PointerTargetNodeList) iter.next();
			for (PointerTargetNodeList targetRelation : targetRelations) {
				Relationship relationship = findAsymmetricRelationship(
						sourceRelation, targetRelation, type, sourceSynset,
						targetSynset);
				// Process result
				if (relationship != null) {
					if (shallowest == null) {
						shallowest = relationship;
					} else {
						// Check for an improvement
						if (relationship.getDepth() < shallowest.getDepth()) {
							shallowest = relationship;
						}
					}
				}
			}
		}

		return shallowest;
	}

	private static Relationship findSymmetricRelationship(Synset sourceSynset,
			Synset targetSynset, PointerType type, int depth) {
			 
		return null;
	}

	/**
	 * This is directly copied from extJWNL's RelationshipFinder class. It
	 * iterates over the list of source nodes and checks to see if any of the
	 * target nodes share a common index. If a node is found, its added to the
	 * pointer target node list and returned as a Relationship.
	 * 
	 * @param sourceNodes
	 * @param targetNodes
	 * @param type
	 * @param sourceSynset
	 * @param targetSynset
	 * @return
	 * @throws CloneNotSupportedException
	 */
	private static Relationship findAsymmetricRelationship(
			PointerTargetNodeList sourceNodes,
			PointerTargetNodeList targetNodes, PointerType type,
			Synset sourceSynset, Synset targetSynset)
			throws CloneNotSupportedException {
		PointerTargetNode sourceRoot = (PointerTargetNode) sourceNodes.get(0);
		PointerTargetNode targetRoot = (PointerTargetNode) targetNodes.get(0);

		// If the deepest ancestor of both trees is not common,
		// there is no relationship between them
		if (!sourceRoot.getSynset().equals(targetRoot.getSynset())) {
			return null;
		}

		PointerTargetNodeList relationship = new PointerTargetNodeList();
		int targetStart = 0;
		int commonParentIndex = 0;

		for (int i = sourceNodes.size() - 1; i >= 0; i--) {
			PointerTargetNode testNode = (PointerTargetNode) sourceNodes.get(i);
			int idx = targetNodes.indexOf(testNode);
			if (idx >= 0) {
				targetStart = idx;
				break;
			}
			relationship.add(testNode.clone());
			commonParentIndex++;
		}

		for (int i = targetStart; i < targetNodes.size(); i++) {
			PointerTargetNode node = ((PointerTargetNode) targetNodes.get(i))
					.clone();
			node.setType(type.getSymmetricType());
			relationship.add(node);
		}

		return new AsymmetricRelationship(type, relationship,
				commonParentIndex, sourceSynset, targetSynset);
	}
}
