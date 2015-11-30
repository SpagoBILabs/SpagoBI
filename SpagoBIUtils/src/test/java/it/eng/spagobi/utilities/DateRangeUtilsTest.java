package it.eng.spagobi.utilities;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import it.eng.spagobi.commons.constants.SpagoBIConstants;

public class DateRangeUtilsTest {

	@BeforeClass
	public static void setUpBeforeAll() throws Exception {
		UtilitiesForTest.setUpTestJNDI();
		UtilitiesForTest.setUpMasterConfiguration();
		DateRangeUtils.setDATE_FORMAT_SERVER("dd/MM/yyyy");
	}
	
	@Test
	public void testIsInDateRangeFilter() {
		String valuefilter = "24-12-2018_3M";
		String typeFilter = SpagoBIConstants.IN_RANGE_FILTER;
		String value = "24/03/2019";
		boolean isInRange = DateRangeUtils.isInDateRangeFilter(valuefilter, typeFilter, value);
		assertTrue(isInRange);
	}
	
	@Test
	public void testIsNotInDateRangeFilter() {
		String valuefilter = "24-12-2018_3Y";
		String typeFilter = SpagoBIConstants.IN_RANGE_FILTER;
		String value = "24/03/2015";
		boolean isInRange = DateRangeUtils.isInDateRangeFilter(valuefilter, typeFilter, value);
		assertFalse(isInRange);
	}

}
