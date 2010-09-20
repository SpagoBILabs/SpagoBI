/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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

package it.eng.spagobi.tools.udp.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.tools.udp.bo.Udp;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

/**
 * 
 * @see it.eng.spagobi.tools.udp.bo.SbiUdp
 * @author Antonella Giachino
 */
public class UdpDAOHibImpl extends AbstractHibernateDAO implements IUdpDAO {

	private static final Logger logger = Logger.getLogger(UdpDAOHibImpl.class);


	public Integer insert(SbiUdp prop) {
		Session session = getSession();
		Transaction tx = null;
		Integer id = null;
		try {
			tx = session.beginTransaction();
			id = (Integer)session.save(prop);
			tx.commit();
		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			if(session != null){
				session.close();
			}

			return id;
		}
	}


	public void insert(Session session, SbiUdp prop) {
		session.save(prop);
	}

	public void update(SbiUdp prop) {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(prop);
			tx.commit();

		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			session.close();
		}
	}	

	public void update(Session session, SbiUdp prop) {
		session.update(prop);
	}	

	public void delete(SbiUdp prop) {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(prop);
			tx.commit();

		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			session.close();
		}
	}

	public void delete(Session session, SbiUdp item) {
		session.delete(item);
	}

	public void delete(Integer id) {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(session.load(SbiUdp.class, id));
			tx.commit();

		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			session.close();
		}
	}


	public void delete(Session session, Integer id) {
		session.delete(session.load(SbiUdp.class, id));
	}

	@SuppressWarnings("unchecked")
	public SbiUdp findById(Integer id) {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			SbiUdp prop = (SbiUdp)session.get(SbiUdp.class, id);
			tx.commit();
			return prop;

		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			session.close();
		}
	}



	public Udp loadById(Integer id) {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			SbiUdp prop = (SbiUdp)session.get(SbiUdp.class, id);
			tx.commit();
			Udp udp=toUdp(prop);
			return udp;

		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			session.close();
		}
	}


	
	/**
	 *  Load a Udp by Label
	 * @throws EMFUserError 
	 */

	public Udp loadByLabel(String label) throws EMFUserError {
		logger.debug("IN");
		Udp udp = null;
		Session tmpSession = null;
		Transaction tx = null;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("label", label);
			Criteria criteria = tmpSession.createCriteria(SbiUdp.class);
			criteria.add(labelCriterrion);	
			SbiUdp hibUDP = (SbiUdp) criteria.uniqueResult();
			if (hibUDP == null) return null;
			udp = toUdp(hibUDP);				

			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while loading the udp with label " + label, he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
		}
		logger.debug("OUT");
		return udp;		

	}
	
	

	@SuppressWarnings("unchecked")
	public List<SbiUdp> findAll() {
		Session session = getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			List<SbiUdp> list = (List<SbiUdp>)session.createQuery("from SbiUdp").list();
			tx.commit();
			return list;

		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			session.close();
		}
	}	


	@SuppressWarnings("unchecked")
	public List<Udp> loadAllByFamily(String familyCd) throws EMFUserError {
		Session session = getSession();
		List<Udp> toReturn = null;
		// get Domain id form KPI family
		Transaction tx = null;
		try {

			Integer domainId;
			SbiDomains domain = DAOFactory.getDomainDAO().loadSbiDomainByCodeAndValue("UDP_FAMILY", "KPI");
			if(domain == null){
				domain = DAOFactory.getDomainDAO().loadSbiDomainByCodeAndValue("UDP_FAMILY", "Kpi");
			}
			if(domain== null){
				logger.error("could not find domain of type UDP_FAMILY with value code KPI");
				return null;
			}
			else{
				domainId = domain.getValueId();
			}


			tx = session.beginTransaction();
			Query query = session.createQuery("from SbiUdp s where s.familyId = :idFamily");
			query.setInteger("idFamily", domainId);

			List<SbiUdp> list = (List<SbiUdp>)query.list();
			if(list != null){
				toReturn = new ArrayList<Udp>();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					SbiUdp sbiUdp = (SbiUdp) iterator.next();
					Udp udp = toUdp(sbiUdp);
					toReturn.add(udp);
				}
			}
			tx.commit();
			return toReturn;

		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}catch (EMFUserError e) {
			logger.error("error probably in getting asked UDP_FAMILY domain", e);
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;

		}finally{
			session.close();
		}
	}	


	public Udp toUdp(SbiUdp sbiUdp){
		Udp toReturn=new Udp();

		toReturn.setUdpId(sbiUdp.getUdpId());
		toReturn.setLabel(sbiUdp.getLabel());
		toReturn.setName(sbiUdp.getName());
		toReturn.setDescription(sbiUdp.getDescription());
		toReturn.setDataTypeId(sbiUdp.getTypeId());
		toReturn.setFamilyId(sbiUdp.getFamilyId());
		toReturn.setMultivalue(sbiUdp.isIsMultivalue());

		// get the type ValueCd
		if (sbiUdp.getTypeId() != null){
			Domain domain;
			try {
				domain = DAOFactory.getDomainDAO().loadDomainById(sbiUdp.getTypeId());
				toReturn.setDataTypeValeCd(domain.getValueCd());
			} catch (EMFUserError e) {
				logger.error("error in loading domain with Id "+sbiUdp.getTypeId(), e);
			}
		}
		return toReturn;
	}


}

