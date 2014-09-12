package it.eng.spagobi.engines.whatif.calculatedmember;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.olap4j.Axis;
import org.olap4j.OlapConnection;
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
import org.olap4j.metadata.Member;

public class CalculatedMemberManager {
	public static transient Logger logger = Logger.getLogger(CalculatedMemberManager.class);
	private final WhatIfEngineInstance ei;

	public CalculatedMemberManager(WhatIfEngineInstance ei) {
		super();
		this.ei = ei;

	}

	public void injectCalculatedIntoMdxQuery(String calculateFieldName, String calculateFieldFormula, Member parentMember, Axis parentMemberAxis) throws SpagoBIEngineException {
		String currentMdx = ei.getPivotModel().getCurrentMdx();
		MdxParser p = createParser();
		SelectNode selectNode = p.parseSelect(currentMdx);

		ParseTreeNode expression = p.parseExpression(calculateFieldFormula);

		IdentifierNode nodoCalcolato = new IdentifierNode(getParentSegments(parentMember, calculateFieldName));

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

			getDimensionNode(null, 0, row, tree, parentMember.getUniqueName());
			selectNode.getAxisList().add(new AxisNode(null,
					false, Axis.ROWS,
					new ArrayList<IdentifierNode>(),
					new CallNode(null, "{}", Syntax.Braces, row)));
		} else {

			getDimensionNode(null, 0, column, tree, parentMember.getUniqueName());
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

	private boolean getDimensionNode(CallNode parentCallNode, int positionInParentCallNode, ParseTreeNode parseNode, ParseTreeNode calculatedFieldTree, String patrentNodeUniqueName) {

		if (parseNode instanceof CallNode) {
			CallNode node = (CallNode) parseNode;
			List<ParseTreeNode> args = node.getArgList();
			for (int i = 0; i < args.size(); i++) {
				ParseTreeNode aNode = args.get(i);
				if (getDimensionNode(node, i, aNode, calculatedFieldTree, patrentNodeUniqueName)) {
					return true;
				}
			}
		} else if (parseNode instanceof DimensionNode) {

		} else if (parseNode instanceof HierarchyNode) {

		} else if (parseNode instanceof IdentifierNode) {
			IdentifierNode node = (IdentifierNode) parseNode;
			String name = getIdentifierUniqueName(node);
			if (patrentNodeUniqueName.equals(name)) {
				parentCallNode.getArgList().add(positionInParentCallNode + 1, calculatedFieldTree);
				return true;
			}
		} else if (parseNode instanceof LevelNode) {

		} else if (parseNode instanceof MemberNode) {

		}
		return false;

	}

	private MdxParser createParser() {
		OlapConnection olapConnection = ei.getOlapConnection();
		return olapConnection.getParserFactory()
				.createMdxParser(olapConnection);
	}

}
