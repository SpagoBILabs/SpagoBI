package it.eng.spagobi;

import it.eng.spagobi.tools.dataset.bo.RESTDataSetTest;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetTest;
import it.eng.spagobi.tools.dataset.common.dataproxy.RESTDataProxyTest;
import it.eng.spagobi.tools.dataset.common.datareader.JSONPathDataReaderTest;
import it.eng.spagobi.utilities.rest.RestUtilitiesTest;
import it.eng.spagobi.utilities.scripting.SpagoBIScriptManagerTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		suite.addTestSuite(RESTDataProxyTest.class);
		suite.addTestSuite(JSONPathDataReaderTest.class);
		suite.addTestSuite(RESTDataSetTest.class);
		suite.addTestSuite(RestUtilitiesTest.class);
		suite.addTestSuite(SpagoBIScriptManagerTest.class);
		suite.addTestSuite(ScriptDataSetTest.class);
		return suite;
	}

}
