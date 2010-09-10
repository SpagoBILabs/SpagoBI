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

import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.tools.udp.bo.UdpValue;
import it.eng.spagobi.tools.udp.metadata.SbiUdpValue;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * 
 * @see it.eng.spagobi.tools.udp.bo.SbiUdp
 * @author Antonella Giachino
 */
public class UdpValueDAOHibImpl extends AbstractHibernateDAO implements IUdpValueDAO {

	private static final Logger logger = Logger.getLogger(UdpValueDAOHibImpl.class);

	
    public Integer insert(SbiUdpValue propValue) {
        Session session = getSession();
        Transaction tx = null;
        Integer id = null;
        try {
        	tx = session.beginTransaction();
			id = (Integer)session.save(propValue);
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
    
    
    public void insert(Session session, SbiUdpValue propValue) {
        session.save(propValue);
    }

    public void update(SbiUdpValue propValue) {
        Session session = getSession();
        Transaction tx = null;
        try {
        	tx = session.beginTransaction();
			session.update(propValue);
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
    
    public void update(Session session, SbiUdpValue propValue) {
        session.update(propValue);
    }	
	
    public void delete(SbiUdpValue propValue) {
        Session session = getSession();
        Transaction tx = null;
        try {
        	tx = session.beginTransaction();
			session.delete(propValue);
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
    
    public void delete(Session session, SbiUdpValue item) {
       session.delete(item);
    }

    public void delete(Integer id) {
        Session session = getSession();
        Transaction tx = null;
        try {
        	tx = session.beginTransaction();
        	session.delete(session.load(SbiUdpValue.class, id));
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
       	session.delete(session.load(SbiUdpValue.class, id));
    }
	
    @SuppressWarnings("unchecked")
    public SbiUdpValue findById(Integer id) {
        Session session = getSession();
        Transaction tx = null;
        try {
        	tx = session.beginTransaction();
        	SbiUdpValue propValue = (SbiUdpValue)session.get(SbiUdpValue.class, id);
			tx.commit();
			return propValue;
			
		} catch (HibernateException e) {
			if( tx != null && tx.isActive() ){
				tx.rollback();
			}
			throw e;
			
		}finally{
			session.close();
		}
    }
    
    
    
    public UdpValue loadById(Integer id) {
        Session session = getSession();
        Transaction tx = null;
        try {
        	tx = session.beginTransaction();
        	SbiUdpValue prop = (SbiUdpValue)session.get(SbiUdpValue.class, id);
			tx.commit();
			UdpValue udpValue=toUdpValue(prop);
			return udpValue;
			
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
	public List<SbiUdpValue> findAll() {
        Session session = getSession();
        Transaction tx = null;
        try {
        	tx = session.beginTransaction();
			
			List<SbiUdpValue> list = (List<SbiUdpValue>)session.createQuery("from SbiUdpValue").list();
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

    
	public UdpValue toUdpValue(SbiUdpValue sbiUdpValue){
		UdpValue toReturn=new UdpValue();
		
		toReturn.setUdpValueId(sbiUdpValue.getUdpValueId());
		toReturn.setUdpId(sbiUdpValue.getSbiUdp().getUdpId());
		toReturn.setReferenceId(sbiUdpValue.getReferenceId());
		toReturn.setLabel(sbiUdpValue.getSbiUdp().getLabel()); //denormilized
		toReturn.setName(sbiUdpValue.getSbiUdp().getName());	//denormilized		
		  
		try{
			IDomainDAO aDomainDAO = DAOFactory.getDomainDAO();
			Domain familyDomain = aDomainDAO.loadDomainById(sbiUdpValue.getSbiUdp().getFamilyId());
			toReturn.setFamily(familyDomain.getValueCd()); //denormilized
		} catch (Exception he) {
			logger.error(he);
		} 
		
		toReturn.setValue(sbiUdpValue.getValue());
		toReturn.setProg(sbiUdpValue.getProg());
		toReturn.setBeginTs(sbiUdpValue.getBeginTs());
		toReturn.setEndTs(sbiUdpValue.getEndTs());
		
		return toReturn;
	}
    

}

