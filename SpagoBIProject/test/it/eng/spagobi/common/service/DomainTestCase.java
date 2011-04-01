package it.eng.spagobi.common.service;

import java.util.Iterator;
import java.util.List;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DomainDAOHibImpl;
import junit.framework.TestCase;

public class DomainTestCase extends TestCase {

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
