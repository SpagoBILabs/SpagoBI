/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.i18n.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.i18n.metadata.SbiI18NMessages;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class I18NMessagesDAOHibImpl extends AbstractHibernateDAO implements
I18NMessagesDAO {

	static private Logger logger = Logger
	.getLogger(I18NMessagesDAOHibImpl.class);

	public String getI18NMessages(Locale locale, String code) throws EMFUserError {
		logger.debug("IN");
		String toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		if(locale == null){
			logger.warn("No I18n conversion because locale passed as parameter is null");
			return code;
		}

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			String qDom = "from SbiDomains dom where dom.valueCd = :valueCd AND dom.domainCd = 'LANG'";
			Query queryDom = aSession.createQuery(qDom);
			String localeId = locale.getISO3Language();
			queryDom.setString("valueCd", localeId);
			Object objDom = queryDom.uniqueResult();
			if(objDom == null){
				logger.warn("Could not find language domain for code "+code);				
			}
			Integer domId = ((SbiDomains)objDom).getValueId();
			
			String q = "from SbiI18NMessages att where att.id.languageCd = :languageCd AND att.id.label = :label";
			Query query = aSession.createQuery(q);
	
			query.setInteger("languageCd", domId);
			query.setString("label", code);

			Object obj = query.uniqueResult();
			if(obj != null){
				SbiI18NMessages SbiI18NMessages = (SbiI18NMessages)obj;
				toReturn = SbiI18NMessages.getMessage();
			}

			tx.commit();
		}
		catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
		logger.debug("OUT");
		return toReturn;
	}




}
