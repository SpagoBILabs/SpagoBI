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
package it.eng.spagobi.engines.commonj.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import commonj.work.Work;

public class SpagoBIWork implements Work{
	/** pid of the work */ 
	String pid;
	/** label of the dcument */ 
	String sbiLabel;
	/** boolean to check to see if work has been stopped, set to false by release method*/
	volatile boolean running=false;
	/** here are put allr equests parameters */ 
	Map sbiParameters=new HashMap();
	/**names of those in sbiParameters that are analytical drivers*/
	Vector<String> analyticalParameters;
	/** Logger */
	static private Logger logger = Logger.getLogger(SpagoBIWork.class);

	public boolean isDaemon() {
		// TODO Auto-generated method stub
		return false;
	}


	public void release() {

		running=false;
	}

	public void run() {
		running=true;		
	}


	public boolean isRunning() {
		return running;
	}


	public void setRunning(boolean running) {
		this.running = running;
	}


	public Map getSbiParameters() {
		return sbiParameters;
	}


	public void setSbiParameters(Map sbiParameters) {
		this.sbiParameters = sbiParameters;
	}



	public Vector<String> getAnalyticalParameters() {
		return analyticalParameters;
	}


	public void setAnalyticalParameters(Vector<String> analyticalParameters) {
		this.analyticalParameters = analyticalParameters;
	}


	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}


	public String getSbiLabel() {
		return sbiLabel;
	}


	public void setSbiLabel(String sbiLabel) {
		this.sbiLabel = sbiLabel;
	}



}
