package integration.calculatedfield;

import integration.agorithms.AbstractWhatIfInMemoryTestCase;
import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.calculatedmember.CalculatedMemberManager;
import it.eng.spagobi.engines.whatif.cube.CubeUtilities;

import org.olap4j.Axis;
import org.olap4j.metadata.Member;

public class CalculatedFieldsTestCase extends AbstractWhatIfInMemoryTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInjectCalculatedIntoMdxQuery() throws Exception {

		WhatIfEngineInstance ei = getWhatifengineiEngineInstance(getCatalogue());

		CalculatedMemberManager cm = new CalculatedMemberManager(ei);

		Member parentMember = CubeUtilities.getMember(ei.getPivotModel().getCube(), "[Product].[Food]");

		String cc = "[Product].[Food].[Dairy]";
		cm.injectCalculatedIntoMdxQuery("name", cc, parentMember, Axis.ROWS);

		String s = ei.getPivotModel().getCurrentMdx();
		System.out.println(s);
	}

}