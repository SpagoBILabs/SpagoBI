package it.eng.spagobi.analiticalmodel.document.handlers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.eng.spagobi.utilities.UtilitiesForTest;

public class ExecutionInstanceTest {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	@BeforeClass
	public static void setUpBeforeAll() throws Exception {
		UtilitiesForTest.setUpTestJNDI();
		UtilitiesForTest.setUpMasterConfiguration();
	}

	@Test
	public void testManageDateRangeParameter() throws ParseException {
		Calendar c = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		c.setTime(df.parse("31/12/2018"));
		StringBuffer sb = new StringBuffer();
		String start = DATE_FORMAT.format(c.getTime());
		ExecutionInstance.manageDateRangeParameter(start + "_3D", sb, "param1");
		Assert.assertEquals("&param1_begin=31/12/2018&param1_duration=3D&param1_end=03/01/2019".replace("/", "%2F"), sb.toString());

		c.setTime(df.parse("01/11/2018"));
		sb = new StringBuffer();
		start = DATE_FORMAT.format(c.getTime());
		ExecutionInstance.manageDateRangeParameter(start + "_3M", sb, "param1");
		Assert.assertEquals("&param1_begin=01/11/2018&param1_duration=3M&param1_end=01/02/2019".replace("/", "%2F"), sb.toString());

		c.setTime(df.parse("04/01/2018"));
		sb = new StringBuffer();
		start = DATE_FORMAT.format(c.getTime());
		ExecutionInstance.manageDateRangeParameter(start + "_2Y", sb, "param1");
		Assert.assertEquals("&param1_begin=04/01/2018&param1_duration=2Y&param1_end=04/01/2020".replace("/", "%2F"), sb.toString());

		c.setTime(df.parse("31/12/2018"));
		sb = new StringBuffer();
		start = DATE_FORMAT.format(c.getTime());
		ExecutionInstance.manageDateRangeParameter(start + "_2W", sb, "param1");
		Assert.assertEquals("&param1_begin=31/12/2018&param1_duration=2W&param1_end=14/01/2019".replace("/", "%2F"), sb.toString());
	}

}
