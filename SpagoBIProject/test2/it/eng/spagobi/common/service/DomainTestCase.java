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
import it.eng.spagobi.commons.metadata.SbiDomains;
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

	public void testSaveDomain() throws Exception {
		
		DomainDAOHibImpl dao=new DomainDAOHibImpl();

		String domCod = "Test";
		String valueCD = "01Test";
		dao.deleteTest(domCod,valueCD);
		System.out.println("Il domain con id 01 è stato eliminato");		
		

		Domain domain = new Domain();
		domain.setDomainCode("Test");
		domain.setDomainName("Test");
		domain.setValueCd("01Test");
		domain.setValueDescription("Primo Test");
		domain.setValueName("uno");
		dao.saveDomain(domain);
		List<Domain> lista=dao.loadListDomains();
		Iterator<Domain> iter=lista.iterator();
		boolean found=false;
		while(iter.hasNext()){
			if(iter.next().getValueCd().equals("01Test"))
				found=true;
							
		}
		if (!found)	fail("Inserimento Ko");
	}

	public void testUpdateDomain() throws Exception {
		DomainDAOHibImpl dao=new DomainDAOHibImpl();
		SbiDomains sbiDomain = dao.loadSbiDomainByCodeAndValue("Test","01Test");
		
		Domain domain = new Domain();
		domain.setDomainCode(sbiDomain.getDomainCd());
		domain.setDomainName(sbiDomain.getDomainNm());
		domain.setValueCd(sbiDomain.getValueCd());
		domain.setValueDescription("Primo Test 2");
		domain.setValueName(sbiDomain.getValueNm());
		domain.setValueId(sbiDomain.getValueId());
		dao.updateDomain(domain);

	}

}
