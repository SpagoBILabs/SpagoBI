package it.eng.spagobi.kpi.model.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelAttribute;
import it.eng.spagobi.kpi.model.bo.ModelAttributeValue;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelAttr;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ModelAttrDAOImpl extends AbstractHibernateDAO implements IModelAttrDAO {

	static private Logger logger = Logger.getLogger(ModelAttrDAOImpl.class);



	public Model loadModelAttrByDomainId(Integer id) throws EMFUserError {
		// TODO Auto-generated method stub
		return null;
	}


	public ModelAttribute loadModelAttrById(Integer id) throws EMFUserError {
		// TODO Auto-generated method stub
		return null;
	}

	public List loadAllModelAttrs() throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		List realResult = new ArrayList();
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiKpiModelAttr");

			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				realResult.add(toModelAttribute((SbiKpiModelAttr) it.next()));
			}
			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while loading all metadata ", he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();				
			}
		}
		logger.debug("OUT");
		return realResult;	
	}



	/**
	 * From the hibernate ModelAttribute at input, gives
	 * the corrispondent <code>ModelAttribute</code> object.
	 * 
	 * @param hibEngine The hybernate engine
	 * 
	 * @return The corrispondent <code>Engine</code> object
	 */
	public ModelAttribute toModelAttribute(SbiKpiModelAttr hibModelAttr){
		ModelAttribute eng = new ModelAttribute();

		eng.setId(hibModelAttr.getKpiModelAttrId());
		eng.setCode(hibModelAttr.getKpiModelAttrCd());
		eng.setName(hibModelAttr.getKpiModelAttrNm());
		eng.setDescr(hibModelAttr.getKpiModelAttrDescr());
		eng.setTypeId(hibModelAttr.getSbiDomains().getValueId());

		return eng;
	}



}
