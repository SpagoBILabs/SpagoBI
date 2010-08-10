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

import it.eng.spagobi.engines.commonj.exception.WorkExecutionException;
import it.eng.spagobi.engines.commonj.exception.WorkNotFoundException;
import it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class WorksRepository {
	private File rootDir;

	private static transient Logger logger = Logger.getLogger(WorksRepository.class);

	/**
	 * Instantiates a new runtime repository.
	 * 
	 * @param rootDir the root dir
	 */
	public WorksRepository(File rootDir) {
		this.rootDir = rootDir;
	}






	/**
	 * Gets the root dir.
	 * 
	 * @return the root dir
	 */
	public File getRootDir() {
		return rootDir;
	}

	/**
	 * Sets the root dir.
	 * 
	 * @param rootDir the new root dir
	 */
	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}

	/**
	 * Gets the executable work project dir.
	 * 
	 * @param work the work
	 * 
	 * @return the executable work project dir
	 */

	public File getExecutableWorkProjectDir(CommonjWork work) {
		logger.debug("IN");
		File worksDir = new File(rootDir, work.getWorkName());
		logger.debug("OUT");
		return worksDir;
	}


	/**
	 * Gets the executable work dir.
	 * 
	 * @param work the work
	 * 
	 * @return the executable work dir
	 */
	public File getExecutableWorkDir(CommonjWork work) {
		logger.debug("IN");
		File workDir = new File(rootDir, work.getWorkName());
		logger.debug("OUT");
		return workDir;
	}


	/**
	 * Gets the executable work file.
	 * 
	 * @param work the work
	 * 
	 * @return the executable work file
	 */
	public File getExecutableWorkFile(CommonjWork work) {
		File workExecutableFile = new File(getExecutableWorkDir(work), work.getWorkName());	
		return workExecutableFile;
	}

	/**
	 * Contains work.
	 * 
	 * @param work the work
	 * 
	 * @return true, if successful
	 */
	public boolean containsWork(CommonjWork work) {

		File workFolder=new File(rootDir, work.getWorkName());	
		boolean exists=workFolder.exists();
		return exists;
	}



}
