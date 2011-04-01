package it.eng.spagobi.common.service;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.configuration.IConfigurationCreator;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DomainDAOHibImpl;
import junit.framework.TestCase;

public class DomainTestCase extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		
		ConfigSingleton.setConfigurationCreation(new IConfigurationCreator() {

			public SourceBean createConfiguration(String arg0) throws SourceBeanException {
				//System.err.println("create: " + arg0);
				return new SourceBean("MASTER");
			}

			public InputStream getInputStream(String arg0) throws Exception {
				System.err.println("getInputStream");
				return null;
			}
			
		});
		
		//System.setProperty("AF_ROOT_PATH", "D:\\Documenti\\Sviluppo\\workspaces\\helios\\spagobi\\server\\SpagoBIProject\\web-content");
	}
	public void testLoadListDomains() throws EMFUserError {
		DomainDAOHibImpl dao=new DomainDAOHibImpl();
		List<Domain> lista=dao.loadListDomains();
		Iterator<Domain> iter=lista.iterator();
		while(iter.hasNext()){
			System.out.println("Elemento="+iter.next().getValueCd());
		}
		
	}

	public void testSaveDomain() {
		fail("Not yet implemented");
	}

	public void testUpdateDomain() {
		fail("Not yet implemented");
	}

}
