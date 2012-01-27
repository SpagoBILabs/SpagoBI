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
package it.eng.spagobi.tools.massiveExport.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.tools.massiveExport.bo.ProgressThread;
import it.eng.spagobi.tools.massiveExport.metadata.SbiProgressThread;
import it.eng.spagobi.tools.massiveExport.work.MassiveExportWork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ProgressThreadDAOImpl extends AbstractHibernateDAO implements IProgressThreadDAO {

	// logger component
	private static Logger logger = Logger.getLogger(ProgressThreadDAOImpl.class);

	public ProgressThread loadProgressThreadById(Integer progressThreadId) throws EMFUserError {
		logger.debug("IN");
		ProgressThread toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.progressThreadId = ?" );
			hibPT.setInteger(0, progressThreadId);
			SbiProgressThread sbiProgressThread =(SbiProgressThread)hibPT.uniqueResult();
			if(sbiProgressThread!=null){
				toReturn = toProgressThread(sbiProgressThread);
			}	
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Thread with progresThreadId", he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return null;
	}





	public List<ProgressThread> loadActiveProgressThreadsByUserId(
			String userId) throws EMFUserError {
		logger.debug("IN");
		List<ProgressThread> toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.userId = ? AND (h.message = '"+MassiveExportWork.STARTED+"' OR h.message = '"+MassiveExportWork.PREPARED+"')" );
			hibPT.setString(0, userId);

			List sbiProgressThreadList = hibPT.list();
			if(sbiProgressThreadList!=null){
				toReturn = new ArrayList<ProgressThread>();
				for (Iterator iterator = sbiProgressThreadList.iterator(); iterator.hasNext();) {
					SbiProgressThread sbiPT = (SbiProgressThread) iterator.next();
					ProgressThread pT = toProgressThread(sbiPT);
					toReturn.add(pT);
				}
			}

			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Threads with userId"+userId + " and message STARTED or prepared", he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}

	public List<ProgressThread> loadNotClosedProgressThreadsByUserId(
			String userId) throws EMFUserError {
		logger.debug("IN");
		List<ProgressThread> toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.userId = ? AND h.message != 'CLOSED'" );
			hibPT.setString(0, userId);

			List sbiProgressThreadList = hibPT.list();
			if(sbiProgressThreadList!=null){
				toReturn = new ArrayList<ProgressThread>();
				for (Iterator iterator = sbiProgressThreadList.iterator(); iterator.hasNext();) {
					SbiProgressThread sbiPT = (SbiProgressThread) iterator.next();
					ProgressThread pT = toProgressThread(sbiPT);
					toReturn.add(pT);
				}
			}

			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Threads with userId"+userId + " and message NOT CLOSED", he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}







	public ProgressThread loadActiveProgressThreadByUserIdAndFuncCd(String userId, String functCd) throws EMFUserError {
		logger.debug("IN");
		ProgressThread toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.userId = ? AND h.functionCd = ? AND (h.message = '"+MassiveExportWork.STARTED+"' OR h.message ='"+MassiveExportWork.PREPARED+"') " );
			hibPT.setString(0, userId);
			hibPT.setString(1, functCd);

			SbiProgressThread sbiProgressThread =(SbiProgressThread)hibPT.uniqueResult();
			if(sbiProgressThread!=null){
				toReturn = toProgressThread(sbiProgressThread);
			}	
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Thread with progresThreadId", he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}




	public boolean incrementProgressThread(Integer progressThreadId) throws EMFUserError{
		logger.debug("IN");
		ProgressThread toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.progressThreadId = ?" );
			hibPT.setInteger(0, progressThreadId);
			SbiProgressThread sbiProgressThread =(SbiProgressThread)hibPT.uniqueResult();

			Integer partial = sbiProgressThread.getPartial();
			sbiProgressThread.setPartial(partial+1);


			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Thread with progressThreadId = "+progressThreadId, he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return true;

	}

	public Integer insertProgressThread(ProgressThread progThread) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		SbiProgressThread sbiPT = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			sbiPT = new SbiProgressThread();
			sbiPT.setFunctionCd(progThread.getFunctionCd());
			sbiPT.setUserId(progThread.getUserId());

			sbiPT.setTotal(progThread.getTotal());
			sbiPT.setPartial(0);
			sbiPT.setMessage(MassiveExportWork.PREPARED);
			sbiPT.setRandomKey(progThread.getRandomKey());

			aSession.save(sbiPT);
			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while inserting the progress thread with user id " + progThread.getUserId() + " and on functionality "+progThread.getFunctionCd(), he);

			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return sbiPT.getProgressThreadId();
	}




	public ProgressThread toProgressThread(SbiProgressThread sbiPT){
		logger.debug("IN");
		ProgressThread toReturn = new ProgressThread();

		toReturn.setUserId(sbiPT.getUserId());
		toReturn.setFunctionCd(sbiPT.getFunctionCd());
		toReturn.setMessage(sbiPT.getMessage());
		toReturn.setProgressThreadId(sbiPT.getProgressThreadId());
		toReturn.setRandomKey(sbiPT.getRandomKey());

		toReturn.setTotal(sbiPT.getTotal());
		toReturn.setPartial(sbiPT.getPartial());
		logger.debug("OUT");
		return toReturn;
	}


	public void setStartedProgressThread(Integer progressThreadId) throws EMFUserError{
		logger.debug("IN");
		ProgressThread toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.progressThreadId = ?" );
			hibPT.setInteger(0, progressThreadId);
			SbiProgressThread sbiProgressThread =(SbiProgressThread)hibPT.uniqueResult();
			sbiProgressThread.setMessage(MassiveExportWork.STARTED);
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Thread with progressThreadId = "+progressThreadId, he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");

	}
	

	public void setDownloadProgressThread(Integer progressThreadId) throws EMFUserError{
		logger.debug("IN");
		ProgressThread toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.progressThreadId = ?" );
			hibPT.setInteger(0, progressThreadId);
			SbiProgressThread sbiProgressThread =(SbiProgressThread)hibPT.uniqueResult();
			sbiProgressThread.setMessage(MassiveExportWork.DOWNLOAD);
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Thread with progressThreadId = "+progressThreadId, he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");

	}

	public void closeProgressThread(Integer progressThreadId) throws EMFUserError{
		logger.debug("IN");
		ProgressThread toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.progressThreadId = ?" );
			hibPT.setInteger(0, progressThreadId);
			SbiProgressThread sbiProgressThread =(SbiProgressThread)hibPT.uniqueResult();
			sbiProgressThread.setMessage("CLOSED");
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Thread with progressThreadId = "+progressThreadId, he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");

	}





	public boolean deleteProgressThread(Integer progressThreadId) throws EMFUserError{
		logger.debug("IN");

		boolean found = false;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.progressThreadId = ?" );
			hibPT.setInteger(0, progressThreadId);
			Object sbiProgressThreadO =hibPT.uniqueResult();
			
			if(sbiProgressThreadO  != null) {
				SbiProgressThread pT = (SbiProgressThread)sbiProgressThreadO;
				found=true;
				aSession.delete(pT);
				tx.commit();
			}

		} catch (HibernateException he) {
			logger.error("Error while deletering Progress Thread with progressThreadId = "+progressThreadId, he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return found;
	}






	public void setErrorProgressThread(Integer progressThreadId) throws EMFUserError{
		logger.debug("IN");
		ProgressThread toReturn = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibPT = aSession.createQuery("from SbiProgressThread h where h.progressThreadId = ?" );
			hibPT.setInteger(0, progressThreadId);
			SbiProgressThread sbiProgressThread =(SbiProgressThread)hibPT.uniqueResult();
			sbiProgressThread.setMessage("ERROR");
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading Progress Thread with progressThreadId = "+progressThreadId, he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");

	}




}



