/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.utilities.threadmanager;


import java.util.UUID;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import commonj.work.Work;
import commonj.work.WorkItem;
import commonj.work.WorkListener;

import de.myfoo.commonj.work.FooRemoteWorkItem;
import de.myfoo.commonj.work.FooWorkManager;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.utilities.assertion.Assert;



/**
 * @author Angelo Bernabei angelo.bernabei@eng.it
 */
public class WorkManager {

	private FooWorkManager wm = null;
	private static transient Logger logger = Logger.getLogger(WorkManager.class);

	/**
	 * Instantiates a new work manager.
	 * 
	 * @throws NamingException the naming exception
	 */
	public WorkManager() throws NamingException {
		init();
	}
	
	
	public WorkManager(String jndiServerManager) throws NamingException {
		init(jndiServerManager);
	}

	/**
	 * Run.
	 * 
	 * @param job the job
	 * @param listener the listener
	 * 
	 * @throws Exception the exception
	 */
	public void run(Work job, WorkListener listener) throws Exception {
		logger.debug("IN");
		try {
			WorkItem wi = wm.schedule(job, listener);

		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		} finally {
			logger.debug("OUT");
		}

	}

	/**
	 * Run.
	 * 
	 * @param job the job
	 * @param listener the listener
	 * 
	 * @throws Exception the exception
	 */
	public FooRemoteWorkItem runWithReturn(Work job, WorkListener listener) throws Exception {
		logger.debug("IN");
		FooRemoteWorkItem fooRemoteWorkItem=null;
		try {
			fooRemoteWorkItem=new FooRemoteWorkItem(job, listener, wm);
			WorkItem wi = wm.schedule(job, listener);

		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		} finally {
			logger.debug("OUT");
		}
		return fooRemoteWorkItem;
	}


	public FooRemoteWorkItem buildFooRemoteWorkItem(Work job, WorkListener listener) throws Exception{
		FooRemoteWorkItem fooRemoteWorkItem=null;
		fooRemoteWorkItem=new FooRemoteWorkItem(job, listener, wm);
		return fooRemoteWorkItem;
	}

	
	public WorkItem runWithReturnWI(Work job, WorkListener listener) throws Exception {
		logger.debug("IN");
		WorkItem workItem=null;
		try {
			workItem  = wm.schedule(job, listener);

		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		} finally {
			logger.debug("OUT");
		}
		return workItem;
	}


	/** No more used, now set in singleton
	 * 
	 * @param session
	 * @param job
	 * @param listener
	 * @param name
	 */

//	public void setInSession(HttpSession session, Work job, WorkListener listener, String name){
//		String id="SBI_PROCESS_"+name;
//		FooRemoteWorkItem fooRemoteWorkItem=new FooRemoteWorkItem(job, listener, wm);
//		session.setAttribute(id, fooRemoteWorkItem);
//	}




	/**
	 * Inits the.
	 * 
	 * @throws NamingException the naming exception
	 */
	public void init() throws NamingException {

		SourceBean jndiSB;
		String jndi;
		Context ctx;

		logger.debug("IN");

		try {
			jndiSB = (SourceBean)EnginConf.getInstance().getConfig().getAttribute("JNDI_THREAD_MANAGER");
			Assert.assertNotNull(jndiSB, "Impossible to find block [<JNDI_THREAD_MANAGER>] into configuration");
			jndi = (String) jndiSB.getCharacters();
			Assert.assertNotNull(jndiSB, "Block [<JNDI_THREAD_MANAGER>] foud in configuration is empty");

			logger.debug("WorkManager jndi name is [" + jndi + "]");

			logger.debug("Looking up for JNDI resource [" + jndi + "] ...");
			ctx = new InitialContext();
			wm = (FooWorkManager) ctx.lookup(jndi);
			logger.debug("JNDI resource lookup successfully terminated");
		} catch (NamingException e) {
			throw e;
		} catch (Throwable t) {
			throw new RuntimeException("An error occurred while initializing the WorkManager", t);
		} finally {
			logger.debug("OUT");
		}

	}

	
	
/**
 *  JNDI string passed as parameter	
 * @param jndi
 * @throws NamingException
 */
	
	public void init(String jndi) throws NamingException {

		Context ctx;
		logger.debug("IN");

		try {
			Assert.assertNotNull(jndi, "jndi is empty");

			logger.debug("WorkManager jndi name is [" + jndi + "]");

			logger.debug("Looking up for JNDI resource [" + jndi + "] ...");
			ctx = new InitialContext();
			Object o = ctx.lookup(jndi);
			
			wm = (FooWorkManager) o;
			logger.debug("JNDI resource lookup successfully terminated");
		} catch (Throwable t) {
			throw new RuntimeException("An error occurred while initializing the WorkManager", t);
		} finally {
			logger.debug("OUT");
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void shutdown(){
		wm.shutdown();
	}

}
