package it.eng.spagobi.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	@Test
	public void testDetDateRangeEndDate() throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Date startDate = df.parse("20-07-2016");
		Date dr = DateRangeUtils.getDateRangeEndDate(startDate, "3M");
		assertEquals("20-10-2016", df.format(dr));

		dr = DateRangeUtils.getDateRangeEndDate(startDate, "3_months");
		assertEquals("20-10-2016", df.format(dr));
	}

	@Test
	public void testGetDuration() {
		assertEquals("6W", DateRangeUtils.getDuration("6W"));
		assertEquals("6W", DateRangeUtils.getDuration("6_weeks"));

		assertEquals("3M", DateRangeUtils.getDuration("3M"));
		assertEquals("3M", DateRangeUtils.getDuration("3_months"));
	}

}
