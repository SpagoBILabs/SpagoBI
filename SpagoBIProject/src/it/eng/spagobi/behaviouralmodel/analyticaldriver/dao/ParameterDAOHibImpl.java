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
package it.eng.spagobi.behaviouralmodel.analyticaldriver.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjects;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParameters;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseDet;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.behaviouralmodel.lov.metadata.SbiLov;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiDomains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

/**
 * Defines the Hibernate implementations for all DAO methods,
 * for a parameter
 * 
 * @author zoppello
 */
public class ParameterDAOHibImpl extends AbstractHibernateDAO implements
		IParameterDAO {
	static private Logger logger = Logger.getLogger(ParameterDAOHibImpl.class);
	/**
	 * Load for detail by parameter id.
	 * 
	 * @param parameterID the parameter id
	 * 
	 * @return the parameter
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO#loadForDetailByParameterID(java.lang.Integer)
	 */
	public Parameter loadForDetailByParameterID(Integer parameterID)throws EMFUserError{
		Parameter toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
	
			SbiParameters hibParameters = (SbiParameters)aSession.load(SbiParameters.class,  parameterID);
		
			toReturn = toParameter(hibParameters);
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
		return toReturn;
	}

	
	public Parameter loadForDetailByParameterLabel(String label) throws EMFUserError {
		logger.debug("IN");
		Parameter parameter = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("label",
					label);
			Criteria criteria = aSession.createCriteria(SbiParameters.class);
			criteria.add(labelCriterrion);
			SbiParameters hibPar = (SbiParameters) criteria.uniqueResult();
			if (hibPar == null) return null;
			parameter = toParameter(hibPar);
			tx.commit();
		} catch (HibernateException he) {
			logger.error(he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		logger.debug("OUT");
		return parameter;
	}

	
	
	
	
	
	/**
	 * Load for execution by parameter i dand role name.
	 * 
	 * @param parameterID the parameter id
	 * @param roleName the role name
	 * 
	 * @return the parameter
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO#loadForExecutionByParameterIDandRoleName(java.lang.Integer, java.lang.String)
	 */
	public Parameter loadForExecutionByParameterIDandRoleName(
			Integer parameterID, String roleName) throws EMFUserError {
		
		Query hqlQuery = null;
		String hql = null;
		Session aSession = null;
		Transaction tx = null;
		Parameter parameter = null;
		
		try{
		
			parameter = loadForDetailByParameterID(parameterID);
			Role role = DAOFactory.getRoleDAO().loadByName(roleName);
			
			aSession = getSession();
			tx = aSession.beginTransaction();
			Criterion domainCdCriterrion = null;
			Criteria criteria = null;
			
			// load all the paruse with the given parmaeter id
			domainCdCriterrion = Expression.eq("sbiParameters.parId", parameter.getId());
			criteria = aSession.createCriteria(SbiParuse.class);
			criteria.add(domainCdCriterrion);
			List paruses = criteria.list();
			
			
			List parUseAssociated = new ArrayList();
			Iterator parusesIter = paruses.iterator();
			while(parusesIter.hasNext()) {
				SbiParuse hibParuse = (SbiParuse)parusesIter.next();
				Iterator paruseDetsIter = hibParuse.getSbiParuseDets().iterator();
				
				
				while(paruseDetsIter.hasNext()){
					SbiParuseDet hibParuseDet = (SbiParuseDet)paruseDetsIter.next();
					if (hibParuseDet.getId().getSbiExtRoles().getExtRoleId().equals(role.getId())){
						parUseAssociated.add(hibParuse);
					}
				}
			}
		
			
			if(parUseAssociated.size() == 1) {
				SbiParuse hibParuse = (SbiParuse)parUseAssociated.get(0);
				SbiLov sbiLov = hibParuse.getSbiLov();
				
				//if modval is null, then the parameter always has a man_in modality
				//force the man_in modality to the parameter
				Integer man_in = hibParuse.getManualInput();
				//Integer sbiLovId = sbiLov.getLovId();
				if(man_in.intValue() == 1){
					ModalitiesValue manInModVal = new ModalitiesValue();
					manInModVal.setITypeCd("MAN_IN");
					manInModVal.setITypeId("37");
					parameter.setModalityValue(manInModVal);
					
				}else{
				ModalitiesValue modVal  = DAOFactory.getModalitiesValueDAO().loadModalitiesValueByID(hibParuse.getSbiLov().getLovId());
				modVal.setSelectionType(hibParuse.getSelectionType());
				modVal.setMultivalue(hibParuse.getMultivalue() != null && hibParuse.getMultivalue().intValue() > 0);
				parameter.setModalityValue(modVal);
				}
				ParameterUse aParameterUse = DAOFactory.getParameterUseDAO().loadByUseID(hibParuse.getUseId());
				parameter.setChecks(aParameterUse.getAssociatedChecks());  
			} else {
				// this part of code wouldn't never be executed because one role can have only one parameteruse
				// for each parameter. The control is executed before the load of the object so 
				// the list would have to contain only one element but if the list contains more than one
				// object it's an error
				logger.error("the parameter with id "+parameterID+" has more than one parameteruse for the role "+roleName);
				throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
			}
			tx.commit();
			return parameter;
			
			
			
		} catch (HibernateException he) {
			logException(he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} catch (EMFUserError emfue) {
			if (tx != null)
				tx.rollback();
			throw emfue;
		} finally {
			if(aSession!=null) {
				if (aSession.isOpen()) aSession.close();
			}
		}
		
		
		
		
		
		
		
	}

	
	
	
	
	/**
	 * Load all parameters.
	 * 
	 * @return the list
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO#loadAllParameters()
	 */
	public List loadAllParameters() throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		List realResult = new ArrayList();
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiParameters");
			List hibList = hibQuery.list();

			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				realResult.add(toParameter((SbiParameters) it.next()));
			}
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
		return realResult;
	}

	/**
	 * Modify parameter.
	 * 
	 * @param aParameter the a parameter
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO#modifyParameter(it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter)
	 */
	public void modifyParameter(Parameter aParameter) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String info = aParameter.getModality();
			StringTokenizer st; 
			String token = null;
			st = new StringTokenizer (info, ",", false);
			String input_type_cd = st.nextToken();
			String input_type_id = st.nextToken();
			
			Integer typeId = Integer.valueOf(input_type_id);
			SbiDomains parameterType = (SbiDomains)aSession.load(SbiDomains.class, typeId); 
			
			SbiParameters hibParameters = (SbiParameters)aSession.load(SbiParameters.class,  aParameter.getId());
			updateSbiCommonInfo4Update(hibParameters);
			hibParameters.setDescr(aParameter.getDescription());
			hibParameters.setLength(new Short(aParameter.getLength().shortValue()));
			hibParameters.setLabel(aParameter.getLabel());
			
			hibParameters.setName(aParameter.getName());
			
			hibParameters.setParameterTypeCode(input_type_cd);
			hibParameters.setMask(aParameter.getMask());
			hibParameters.setParameterType(parameterType);
			
			if (aParameter.isFunctional()) hibParameters.setFunctionalFlag(new Short((short) 1));
			else hibParameters.setFunctionalFlag(new Short((short) 0));
			
			if (aParameter.isTemporal()) hibParameters.setTemporalFlag(new Short((short) 1));
			else hibParameters.setTemporalFlag(new Short((short) 0));
			
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
	
	}
		
	/**
	 * Insert parameter.
	 * 
	 * @param aParameter the a parameter
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO#insertParameter(it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter)
	 */
	public void insertParameter(Parameter aParameter) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String info = aParameter.getModality();
			StringTokenizer st;
			String token = null;
			st = new StringTokenizer(info, ",", false);
			String input_type_cd = st.nextToken();
			String input_type_id = st.nextToken();

			Integer typeId = Integer.valueOf(input_type_id);
			SbiDomains parameterType = (SbiDomains) aSession.load(
					SbiDomains.class, typeId);

			SbiParameters hibParameters = new SbiParameters();
			hibParameters.setDescr(aParameter.getDescription());
			hibParameters.setLength(new Short(aParameter.getLength()
					.shortValue()));
			hibParameters.setLabel(aParameter.getLabel());
			hibParameters.setName(aParameter.getName());
			hibParameters.setParameterTypeCode(input_type_cd);
			hibParameters.setMask(aParameter.getMask());
			hibParameters.setParameterType(parameterType);
			if (aParameter.isFunctional()) hibParameters.setFunctionalFlag(new Short((short) 1));
			else hibParameters.setFunctionalFlag(new Short((short) 0));
			if (aParameter.isTemporal()) hibParameters.setTemporalFlag(new Short((short) 1));
			else hibParameters.setTemporalFlag(new Short((short) 0));
			updateSbiCommonInfo4Insert(hibParameters);
			aSession.save(hibParameters);
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
	}

	/**
	 * Erase parameter.
	 * 
	 * @param aParameter the a parameter
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO#eraseParameter(it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter)
	 */
	public void eraseParameter(Parameter aParameter) throws EMFUserError {
		
		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
				
				SbiParameters hibParameters = (SbiParameters)aSession.load(SbiParameters.class,  aParameter.getId());
				aSession.delete(hibParameters);
				
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
	}
	
	/**
	 * From the hibernate parametes at input, gives
	 * the corrispondent <code>Parameter</code> object.
	 * 
	 * @param hibParameters The hybernate parameter
	 * 
	 * @return The corrispondent <code>Parameter</code> object
	 */
	public Parameter toParameter(SbiParameters hibParameters){
		Parameter aParameter = new Parameter();
		aParameter.setDescription(hibParameters.getDescr());
		aParameter.setId(hibParameters.getParId());
		aParameter.setLabel(hibParameters.getLabel());
		aParameter.setName(hibParameters.getName());
		aParameter.setLength(new Integer(hibParameters.getLength().intValue()));
		aParameter.setMask(hibParameters.getMask());
		aParameter.setType(hibParameters.getParameterTypeCode());
		aParameter.setTypeId(hibParameters.getParameterType().getValueId());
		if (hibParameters.getFunctionalFlag().intValue() == 0) aParameter.setIsFunctional(false);
		else aParameter.setIsFunctional(true);
		if (hibParameters.getTemporalFlag().intValue() == 0) aParameter.setIsTemporal(false);
		else aParameter.setIsTemporal(true);
		return aParameter;
	}
}
