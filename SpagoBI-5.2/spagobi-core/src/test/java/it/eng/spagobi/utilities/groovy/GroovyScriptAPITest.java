package it.eng.spagobi.utilities.groovy;

import it.eng.qbe.runtime.script.groovy.GroovyScriptAPI;
import junit.framework.TestCase;

public class GroovyScriptAPITest extends TestCase {

	private static final String SCRIPT = "import it.eng.qbe.script.groovy.GroovyScriptAPI; a=new GroovyScriptAPI();c=a.getImage();";

	public void testGetLink() {
		GroovySandboxTest.expectException(true, SCRIPT);
		GroovySandboxTest.expectException(false, SCRIPT, GroovyScriptAPI.class);
	}

}
