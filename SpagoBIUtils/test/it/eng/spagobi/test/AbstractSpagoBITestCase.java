/**
 * 
 */
package it.eng.spagobi.test;

import junit.framework.TestCase;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class AbstractSpagoBITestCase extends TestCase {
	
	protected boolean performTearDown = false;
	
	public AbstractSpagoBITestCase() {
		super();
	}

	public void setUp() throws Exception {
		try {
			performTearDown = false;
		} catch(Exception t) {
			System.err.println("An unespected error occurred during setUp: ");
			t.printStackTrace();
			throw t;
		}
	}
	
	public void tearDown() throws Exception {
		if(performTearDown) {
			doTearDown();
		}
	}

	protected void doTearDown() {
		
	}
}
