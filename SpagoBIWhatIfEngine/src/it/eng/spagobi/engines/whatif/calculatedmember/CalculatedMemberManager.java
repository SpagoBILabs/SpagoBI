/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.calculatedmember;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.cube.CubeUtilities;
import it.eng.spagobi.engines.whatif.dimension.SbiDimension;
import it.eng.spagobi.engines.whatif.hierarchy.SbiHierarchy;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.mdx.AxisNode;
import org.olap4j.mdx.CallNode;
import org.olap4j.mdx.DimensionNode;
import org.olap4j.mdx.HierarchyNode;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.mdx.LevelNode;
import org.olap4j.mdx.MemberNode;
import org.olap4j.mdx.NameSegment;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.PropertyValueNode;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.Syntax;
import org.olap4j.mdx.WithMemberNode;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;

public class CalculatedMemberManager {
	public static transient Logger logger = Logger.getLogger(CalculatedMemberManager.class);
	private final WhatIfEngineInstance ei;

	public CalculatedMemberManager(WhatIfEngineInstance ei) {
		super();
		this.ei = ei;

	}

	/**
	 * Service to inject the calculated member in the tree
	 * 
	 * @param calculateFieldName
	 *            the name of the calculated member
	 * @param calculateFieldFormula
	 *            the formula
	 * @param parentMember
	 *            the parent member
	 * @param parentMemberAxis
	 *            the axis of the parent member
	 */
	public void injectCalculatedIntoMdxQuery(String calculateFieldName, String calculateFieldFormula, Member parentMember, Axis parentMemberAxis) throws SpagoBIEngineException {
		String currentMdx = ei.getPivotModel().getCurrentMdx();
		MdxParser p = createParser();
		SelectNode selectNode = p.parseSelect(currentMdx);
		IdentifierNode nodoCalcolato = new IdentifierNode(new NameSegment("Measures"), new NameSegment(calculateFieldName));
		ParseTreeNode expression = p.parseExpression(calculateFieldFormula); // parse
																				// the
																				// calculated
																				// member
																				// formula
		try {
			if (!parentMember.getDimension().getDimensionType().name().equalsIgnoreCase(new String("MEASURE")))
			{
				nodoCalcolato = new IdentifierNode(getParentSegments(parentMember, calculateFieldName));// build
																										// identifier
																										// node
																										// from
																										// identifier
																										// segments
			}
		} catch (OlapException olapEx) {
			throw new SpagoBIEngineException("Error building identifier node from segments for Measures", olapEx);
		}

		WithMemberNode withMemberNode = new WithMemberNode(null, nodoCalcolato, expression, Collections.<PropertyValueNode> emptyList());
		selectNode.getWithList().add(withMemberNode);

		ParseTreeNode tree = new CallNode(null, "()", Syntax.Parentheses, nodoCalcolato);

		ParseTreeNode row = selectNode.getAxisList().get(Axis.ROWS.axisOrdinal()).getExpression();
		ParseTreeNode column = selectNode.getAxisList().get(Axis.COLUMNS.axisOrdinal()).getExpression();

		selectNode.getAxisList().clear();

		if (parentMemberAxis.axisOrdinal() == (Axis.ROWS.axisOrdinal())) {
			selectNode.getAxisList().add(new AxisNode(null,
					false, Axis.COLUMNS,
					new ArrayList<IdentifierNode>(),
					new CallNode(null, "{}", Syntax.Braces, column)));

			insertCalculatedInParentNode(null, 0, row, tree, parentMember.getUniqueName());
			selectNode.getAxisList().add(new AxisNode(null,
					false, Axis.ROWS,
					new ArrayList<IdentifierNode>(),
					new CallNode(null, "{}", Syntax.Braces, row)));
		} else {

			insertCalculatedInParentNode(null, 0, column, tree, parentMember.getUniqueName());
			selectNode.getAxisList().add(new AxisNode(null,
					false, Axis.COLUMNS,
					new ArrayList<IdentifierNode>(),
					new CallNode(null, "{}", Syntax.Braces, column)));

			selectNode.getAxisList().add(new AxisNode(null,
					false, Axis.ROWS,
					new ArrayList<IdentifierNode>(),
					new CallNode(null, "{}", Syntax.Braces, row)));
		}

		String queryString = selectNode.toString();

		try {
			ei.getPivotModel().setMdx(queryString);
			ei.getPivotModel().refresh();
		} catch (Exception e) {
			ei.getPivotModel().setMdx(currentMdx);
			ei.getPivotModel().refresh();
			throw new SpagoBIEngineException("Error calculating the field", e);
		}

	}

	private List<IdentifierSegment> getParentSegments(Member parentMember, String calculateFieldName) {

		List<IdentifierSegment> parentSegments = new ArrayList<IdentifierSegment>();

		String parentMemberUniqueName = parentMember.getUniqueName();

		StringTokenizer tokenizer = new StringTokenizer(parentMemberUniqueName, "[].");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			parentSegments.add(new NameSegment(token));
		}

		parentSegments.add(new NameSegment(calculateFieldName));
		return parentSegments;

	}

	private String getIdentifierUniqueName(IdentifierNode node) {

		List<IdentifierSegment> parentSegments = node.getSegmentList();

		StringBuffer uniqueName = new StringBuffer();

		for (int i = 0; i < parentSegments.size(); i++) {
			uniqueName.append(parentSegments.get(i).toString());
			uniqueName.append(".");
		}
		if (uniqueName.length() > 0) {
			uniqueName.setLength(uniqueName.length() - 1);
		}

		return uniqueName.toString();

	}

	/**
	 * Service to find where to insert the calculated member in the tree
	 * 
	 * @param parentCallNode
	 * 
	 * @param positionInParentCallNode
	 * 
	 * @param parseNode
	 * 
	 * @param calculatedFieldTree
	 * 
	 * @param parentNodeUniqueName
	 * @return boolean true when the parent is found
	 */

	private boolean insertCalculatedInParentNode(CallNode parentCallNode, int positionInParentCallNode, ParseTreeNode parseNode, ParseTreeNode calculatedFieldTree,
			String parentNodeUniqueName) {

		if (parseNode instanceof CallNode) {
			CallNode node = (CallNode) parseNode;
			List<ParseTreeNode> args = node.getArgList();
			for (int i = 0; i < args.size(); i++) {
				ParseTreeNode aNode = args.get(i);
				if (insertCalculatedInParentNode(node, i, aNode, calculatedFieldTree, parentNodeUniqueName)) {
					return true;
				}
			}
		} else if (parseNode instanceof DimensionNode) {

		} else if (parseNode instanceof HierarchyNode) {

		} else if (parseNode instanceof IdentifierNode) {
			IdentifierNode node = (IdentifierNode) parseNode;
			String name = getIdentifierUniqueName(node);
			if (parentNodeUniqueName.equals(name)) {
				parentCallNode.getArgList().add(positionInParentCallNode + 1, calculatedFieldTree);// The
																									// new
																									// calculated
																									// member
																									// goes
																									// next
																									// its
																									// parent
																									// node
				return true;
			}
		} else if (parseNode instanceof LevelNode) {

		} else if (parseNode instanceof MemberNode) {

		}
		return false;

	}

	/**
	 * Service to get an MDX Parser
	 * 
	 * @return The MDX Parser
	 */
	private MdxParser createParser() {
		OlapConnection olapConnection = ei.getOlapConnection();
		return olapConnection.getParserFactory()
				.createMdxParser(olapConnection);
	}

	/**
	 * Service to get the dimensions
	 * 
	 * @return The SbiDimension List
	 */
	public List<SbiDimension> getDimensions(PivotModel model) throws SpagoBIEngineException {
		logger.debug("IN");
		CellSet cellSet = model.getCellSet();
		List<CellSetAxis> axis = cellSet.getAxes();
		List<Dimension> otherHDimensions;
		List<SbiDimension> dimensions = new ArrayList<SbiDimension>();
		try {
			List<Hierarchy> axisHierarchies = axis.get(0).getAxisMetaData().getHierarchies();
			axisHierarchies.addAll(axis.get(1).getAxisMetaData().getHierarchies());
			otherHDimensions = CubeUtilities.getDimensions(model.getCube().getHierarchies());
			for (int i = 0; i < otherHDimensions.size(); i++) {
				Dimension aDimension = otherHDimensions.get(i);
				SbiDimension myDimension = new SbiDimension(aDimension, -1, i);
				List<Hierarchy> dimensionHierarchies = aDimension.getHierarchies();
				String selectedHierarchyName = this.ei.getModelConfig().getDimensionHierarchyMap().get(myDimension.getUniqueName());
				if (selectedHierarchyName == null) {
					selectedHierarchyName = aDimension.getDefaultHierarchy().getUniqueName();
				}
				myDimension.setSelectedHierarchyUniqueName(selectedHierarchyName);
				for (int j = 0; j < dimensionHierarchies.size(); j++) {
					Hierarchy hierarchy = dimensionHierarchies.get(j);
					SbiHierarchy hierarchyObject = new SbiHierarchy(hierarchy, i);
					myDimension.getHierarchies().add(hierarchyObject);
					// set the position of the selected hierarchy
					if (selectedHierarchyName.equals(hierarchy.getUniqueName())) {
						myDimension.setSelectedHierarchyPosition(j);
					}
				}
				dimensions.add(myDimension);
			}
		} catch (Exception e) {
			logger.error("Error getting dimensions", e);
			throw new SpagoBIEngineException("Error getting dimensions", e);
		}
		logger.debug("OUT");
		return dimensions;
	}

}
