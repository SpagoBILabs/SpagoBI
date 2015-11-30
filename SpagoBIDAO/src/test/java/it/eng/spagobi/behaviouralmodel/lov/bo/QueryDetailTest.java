package it.eng.spagobi.behaviouralmodel.lov.bo;

import static it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail.DIALECT_HSQL;
import static it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail.DIALECT_POSTGRES;
import static it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail.TRUE_CONDITION;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.commons.constants.SpagoBIConstants;

public class QueryDetailTest {

	@Test
	public void testGetDateRangeClauseTrueCondition() {
		QueryDetail qd = new QueryDetail();
		BIObjectParameter fp = new BIObjectParameter();
		List<String> pvs = new ArrayList<String>();
		fp.setParameterValues(pvs);
		ObjParuse dep = new ObjParuse();
		String drc = qd.getDateRangeClause(dep, fp);
		assertEquals(TRUE_CONDITION, drc);
	}

	@Test
	public void testGetDateRangeClauseInFilter() {
		assertDateRange(SpagoBIConstants.IN_RANGE_FILTER, DIALECT_POSTGRES, "17-11-2020_4M",
				" ( columnA>= TO_TIMESTAMP('17/11/2020 00:00:00','DD/MM/YYYY HH24:MI:SS.FF')  AND columnA< TO_TIMESTAMP('18/03/2021 00:00:00','DD/MM/YYYY HH24:MI:SS.FF') ) ");
	}

	@Test
	public void testGetDateRangeClauseNotInFilter() {
		assertDateRange(SpagoBIConstants.NOT_IN_RANGE_FILTER, DIALECT_POSTGRES, "17-11-2020_4M",
				" ( columnA< TO_TIMESTAMP('17/11/2020 00:00:00','DD/MM/YYYY HH24:MI:SS.FF')  AND columnA>= TO_TIMESTAMP('18/03/2021 00:00:00','DD/MM/YYYY HH24:MI:SS.FF') ) ");
	}

	@Test
	public void testGetDateRangeClauseInFilterHSQL() {
		assertDateRange(SpagoBIConstants.IN_RANGE_FILTER, DIALECT_HSQL, "17-11-2020_4M", " ( columnA>='17/11/2020' AND columnA<='17/03/2021') ");
	}

	@Test
	public void testGetDateRangeClauseNotInFilterHSQL() {
		assertDateRange(SpagoBIConstants.NOT_IN_RANGE_FILTER, DIALECT_HSQL, "17-11-2020_4M", " ( columnA<'17/11/2020' AND columnA>'17/03/2021') ");
	}

	private void assertDateRange(String filter, String dialect, String value, String expected) {
		QueryDetail qd = new QueryDetail();
		qd.setDataSourceDialect(dialect);
		BIObjectParameter fp = new BIObjectParameter();
		List<String> pvs = new ArrayList<String>();
		pvs.add(value);
		fp.setParameterValues(pvs);
		ObjParuse dep = new ObjParuse();
		dep.setFilterOperation(filter);
		dep.setFilterColumn("columnA");
		String drc = qd.getDateRangeClause(dep, fp);
		assertEquals(expected, drc);
	}

}
