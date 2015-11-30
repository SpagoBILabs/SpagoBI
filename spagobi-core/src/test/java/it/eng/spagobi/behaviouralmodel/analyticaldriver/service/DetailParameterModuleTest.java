package it.eng.spagobi.behaviouralmodel.analyticaldriver.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DetailParameterModuleTest {

	@Test
	public void testToJson() {
		List<String[]> opts = new ArrayList<String[]>();
		opts.add(new String[] { "e", "2" });
		opts.add(new String[] { "r", "3" });
		String json = DetailParameterModule.toJson(opts);
		Assert.assertEquals("{\"options\":[{\"type\":\"e\",\"quantity\":\"2\"},{\"type\":\"r\",\"quantity\":\"3\"}]}", json);

		opts.remove(0);
		json = DetailParameterModule.toJson(opts);
		Assert.assertEquals("{\"options\":[{\"type\":\"r\",\"quantity\":\"3\"}]}", json);
	}

}
