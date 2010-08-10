package it.eng.spagobi.kpi.model.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelAttribute;
import it.eng.spagobi.kpi.model.bo.ModelAttributeValue;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelAttr;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelAttrVal;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ModelAttrValDAOImpl extends AbstractHibernateDAO implements IModelAttrValDAO {

	static private Logger logger = Logger.getLogger(ModelAttrValDAOImpl.class);

	public ModelAttributeValue loadModelAttrValByAttrIdAndModelId(Integer attrId,
			Integer modelId) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		ModelAttributeValue toReturn = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			//Query hibQuery = aSession.createQuery(" from SbiEngines engines where engines.biobjType.valueCd = '" + biobjectType + "'");
			Query hibQuery = aSession.createQuery(" from SbiKpiModelAttrVal vals where vals.sbiKpiModel.kpiModelId = ? AND vals.sbiKpiModelAttr.kpiModelAttrId = ?" );
			hibQuery.setInteger(0, modelId);
			hibQuery.setInteger(1, attrId);
			List hibList = hibQuery.list();
			if(hibList.isEmpty()) return null;

			Iterator it = hibList.iterator();

			toReturn = toModelAttributeVal((SbiKpiModelAttrVal)it.next());

			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		logger.debug("OUT");

		return toReturn;

	}


	/**
	 * From the hibernate ModelAttributeVal at input, gives
	 * the corrispondent <code>ModelAttributeValue</code> object.
	 * 
	 * @param hibEngine The hybernate engine
	 * 
	 * @return The corrispondent <code>Engine</code> object
	 */
	public ModelAttributeValue toModelAttributeVal(SbiKpiModelAttrVal hibModelAttrVal){
		ModelAttributeValue eng = new ModelAttributeValue();

		eng.setId(hibModelAttrVal.getKpiModelAttrValId());
		eng.setAttrId(hibModelAttrVal.getSbiKpiModelAttr().getKpiModelAttrId());

		eng.setModelId(hibModelAttrVal.getSbiKpiModel().getKpiModelId());
		eng.setValue(hibModelAttrVal.getValue());
		return eng;
	}



	public List allModelsIdWithAttribute() throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		List realResult = new ArrayList();

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiKpiModelAttrVal");

			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			SbiKpiModelAttrVal kpiModelAttrVal = null;			
			while (it.hasNext()) {
				kpiModelAttrVal = ((SbiKpiModelAttrVal) it.next());
				if(!realResult.contains(kpiModelAttrVal.getSbiKpiModel().getKpiModelId())){
					realResult.add(kpiModelAttrVal.getSbiKpiModel().getKpiModelId());
				}
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






}
