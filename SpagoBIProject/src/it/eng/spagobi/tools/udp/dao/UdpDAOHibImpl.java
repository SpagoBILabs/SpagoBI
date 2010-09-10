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

import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.tools.udp.bo.Udp;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;

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

    
	public Udp toUdp(SbiUdp sbiUdp){
		Udp toReturn=new Udp();
		
		toReturn.setUdpId(sbiUdp.getUdpId());
		toReturn.setLabel(sbiUdp.getLabel());
		toReturn.setName(sbiUdp.getName());
		toReturn.setDescription(sbiUdp.getDescription());
		toReturn.setDataTypeId(sbiUdp.getTypeId());
		toReturn.setFamilyId(sbiUdp.getFamilyId());
		toReturn.setMultivalue(sbiUdp.isIsMultivalue());
		
		return toReturn;
	}
    

}

