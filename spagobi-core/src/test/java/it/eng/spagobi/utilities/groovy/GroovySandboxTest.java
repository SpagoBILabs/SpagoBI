package it.eng.spagobi.utilities.groovy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class GroovySandboxTest {

	@Test
	public void testSecure() {
		// code execution
		expectException(false, "a=1+2");
		expectException(false, "a='1'+2");
		expectException(false, "Date a=new Date()\nb=a.getTime()");
		expectException(false, "a=new ArrayList()");
		expectException(false, "a=new ArrayList();a.add(3);a.add('q');");
		expectException(false, "List a=new ArrayList()");
		expectException(false, "a=new HashSet()");
		expectException(false, "a=[new Date(10)]");
		expectException(false, "b=['q':new Date(10),'r':new ArrayList()]");
		expectException(false, "a=new Integer(2)");
		expectException(false, "Date d=new java.text.SimpleDateFormat('yyyy').parse('2015')");

		expectException(true, "println('a')");
		expectException(true, "a=1+query;println(a)");
		expectException(true, "fileContents = new File('resource-test/a.txt')");
		expectException(true, "String fileContents = new File('resource-test/a.txt').text;print(fileContents)");
		expectException(true, "System.exit()");
		expectException(true, "a=new File()");
		expectException(true, "Scanner b=new Scanner(new File('')); b.next()");
		expectException(true, "b=['q':new File('q.txt'),'r':new ArrayList()]");
		expectException(true, "b=['q':new File('q.txt'),'r':new ArrayList()]");
		expectException(true, "Formatter fr = new Formatter('/tmp/test.jsp');fr.format('<html></html>');fr.close();");
	}

	@Test
	public void testReflection() {
		expectException(true, "a=String.class.forName('java.io.File')");
		expectException(true, "a=String.class.forName('java.io.File').getConstructor(String.class).newInstance('a');");
		// no reflection at all
		expectException(true, "a=String.class.forName('java.lang.String')");

	}

	@Test
	public void testAddClass() {
		expectException(true, "a=new File('b.txt')");
		expectException(false, "a=new File('a.txt'); a.exists()", File.class);
		expectException(true, "a=new File('a.txt');System.exit(0);", File.class);
		expectException(true, "a=new File('a.txt');c=new Scanner('q');", File.class);
		expectException(true, "a=new File('a.txt');println('error');", File.class);
	}

	@Test
	public void testVariables() throws IOException {
		Map<String, Object> bindings = new HashMap<String, Object>();
		bindings.put("q", 3);
		GroovySandbox gs = new GroovySandbox();
		gs.setBindings(bindings);
		Object res = gs.evaluate("a=1+q");
		Assert.assertEquals(4, res);

		bindings.put("q", "3");
		gs = new GroovySandbox();
		gs.setBindings(bindings);
		res = gs.evaluate("a=1+q");
		Assert.assertEquals("13", res);

		bindings.put("q", new A());
		gs = new GroovySandbox(new Class[] { A.class });
		gs.setBindings(bindings);
		res = gs.evaluate("return q.calc()");
		Assert.assertEquals(5, res);
	}

	@Test
	public void testLovsScript() throws IOException {
		String script = getScript();
		script += "returnValue('a'+'b');";
		expectException(false, script);
	}

	private String getScript() throws IOException {
		return IOUtils.toString(getClass().getResourceAsStream("script-lovs.groovy"));
	}

	/**
	 * For testing in script
	 */
	public int doSome() {
		return 5;
	}

	@SuppressWarnings("rawtypes")
	public static void expectException(boolean exp, String script, Class... added) {
		try {
			new GroovySandbox(added).evaluate(script);
		} catch (Exception e) {
			if (!exp) {
				Assert.fail(e.getMessage());
			}
			return;
		}
		if (exp) {
			Assert.fail();
		}

	}

}
