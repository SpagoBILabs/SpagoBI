/**
Copyright (c) 2005-2010, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.

 * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 **/

package it.eng.spagobi.engines.commonj.runtime;

import it.eng.spagobi.utilities.threadmanager.WorkManager;

import javax.servlet.http.HttpSession;

import commonj.work.Work;
import commonj.work.WorkItem;

import de.myfoo.commonj.work.FooRemoteWorkItem;

/**
 * stato del work, contenuto nella mappa
 * @author bernabei
 *
 */
public class CommonjWorkContainer {

	String pid=null;
	
	Work work=null;

	CommonjWorkListener listener;

	String name=null;

	WorkManager wm=null;

	FooRemoteWorkItem fooRemoteWorkItem=null;
	WorkItem workItem = null;	

	public FooRemoteWorkItem getFooRemoteWorkItem() {
		return fooRemoteWorkItem;
	}

	public void setFooRemoteWorkItem(FooRemoteWorkItem fooRemoteWorkItem) {
		this.fooRemoteWorkItem = fooRemoteWorkItem;
	}

	public WorkManager getWm() {
		return wm;
	}

	public void setWm(WorkManager wm) {
		this.wm = wm;
	}


	public WorkItem getWorkItem() {
		return workItem;
	}

	public void setWorkItem(WorkItem workItem) {
		this.workItem = workItem;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	public CommonjWorkListener getListener() {
		return listener;
	}

	public void setListener(CommonjWorkListener listener) {
		this.listener = listener;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/**
	 *  No more used
	 * @param documentId
	 * @param session
	 */
	
	public void setInSession(String documentId,HttpSession session){
		session.setAttribute("SBI_PROCESS_"+documentId, this);
	}

	
	
	public boolean isInSession(String documentId,HttpSession session){
		Object o=session.getAttribute("SBI_PROCESS_"+documentId);
		if(o!=null){
			return true;
		}
		else return false;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	
	

}
