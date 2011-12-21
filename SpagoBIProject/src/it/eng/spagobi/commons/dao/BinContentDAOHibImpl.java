/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
/*
 * Created on 22-giu-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.commons.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spagobi.commons.metadata.SbiBinContents;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BinContentDAOHibImpl extends AbstractHibernateDAO implements IBinContentDAO {

    private static transient Logger logger = Logger.getLogger(BinContentDAOHibImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see it.eng.spagobi.commons.dao.IBinContentDAO#getBinContent(java.lang.Integer)
     */
    public byte[] getBinContent(Integer binId) throws EMFInternalError {
	logger.debug("IN");
	if (binId != null)
	    logger.debug("binId=" + binId.toString());
	byte[] content = new byte[0];
	Session aSession = null;
	Transaction tx = null;
	try {
	    aSession = getSession();
	    tx = aSession.beginTransaction();
	    SbiBinContents hibBinCont = (SbiBinContents) aSession.load(SbiBinContents.class, binId);
	    content = hibBinCont.getContent();
	    tx.commit();
	} catch (HibernateException he) {
	    logger.error("HibernateException",he);
	    if (tx != null)
		tx.rollback();
	    throw new EMFInternalError(EMFErrorSeverity.ERROR, "100");
	} finally {
	    if (aSession != null) {
		if (aSession.isOpen())
		    aSession.close();
	    }
	    logger.debug("OUT");
	}
	return content;
    }

}
