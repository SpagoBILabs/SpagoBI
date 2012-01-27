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

package it.eng.spagobi.tools.massiveExport.work;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ExecutionProxy;
import it.eng.spagobi.tools.massiveExport.dao.IProgressThreadDAO;
import it.eng.spagobi.tools.massiveExport.utils.Utilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import commonj.work.Work;

/** Thread of massive export; cycle on documetns to be exported calling engine for export, meanwhile keeps updated the record of the export,
 * finally create the zip and store it in temporary table
 * 
 * @author gavardi
 *
 */

public class MassiveExportWork implements Work{

	private static transient Logger logger = Logger.getLogger(MassiveExportWork.class);

	public static final String PREPARED = "PREPARED";
	public static final String STARTED = "STARTED";
	public static final String DOWNLOAD = "DOWNLOAD";
	public static final String ERROR = "ERROR";
	
	
	IEngUserProfile profile;
	List biObjects;
	LowFunctionality functionality;

	Integer progressThreadId;
	// key identifing the file
	String zipKey;

	List<File> filesToZip = null;

	boolean splittingFilter = false;

	static byte[] buf = new byte[1024]; 

	private boolean completeWithoutError = false;
	IProgressThreadDAO threadDAO;

	public MassiveExportWork(List biObjects, IEngUserProfile profile, LowFunctionality func, 
			Integer progressThreadId, String zipKey, boolean splittingFilter) {
		super();
		this.biObjects = biObjects;
		this.profile = profile;
		this.functionality = func;
		this.progressThreadId = progressThreadId;
		this.zipKey = zipKey;
		this.splittingFilter = splittingFilter;
	}





	public void run() {
		logger.debug("IN");
		IProgressThreadDAO threadDAO = null;

		String output = null;
		Thread thread = Thread.currentThread();
		Long threadId = thread.getId();

		logger.debug("Started thread Id "+threadId+" from user id: "+profile.getUserUniqueIdentifier());

		Integer totalDocs = biObjects.size();
		logger.debug("# of documents: "+totalDocs);


		try {
			threadDAO = DAOFactory.getProgressThreadDAO();
			threadDAO.setStartedProgressThread(progressThreadId);
			
		}catch (Exception e) {
			logger.error("Error setting DAO");
			deleteDBRowInCaseOfError(threadDAO, progressThreadId);
			throw new SpagoBIServiceException("Error setting DAO", e);
		}


		filesToZip = new ArrayList<File>();

		// map used to recover real name to put inside zip
		Map<String, String> randomNamesToName = new HashMap<String, String>();

		for (Iterator iterator = biObjects.iterator(); iterator.hasNext();) {
			BIObject biObj = (BIObject) iterator.next();

			File exportFile = null;

			ExecutionProxy proxy = new ExecutionProxy();
			proxy.setBiObject(biObj);
			proxy.setSplittingFilter(splittingFilter);
			byte[] returnByteArray = null;
			try{
				returnByteArray = proxy.exec(profile, SpagoBIConstants.MASSIVE_EXPORT_MODALITY, output);
			}
			catch (Throwable e) {
				logger.error("Error while executing export for object with label "+biObj.getLabel());
				exportFile = createErrorFile(biObj, e , randomNamesToName);
				returnByteArray = null;
			}




			if(returnByteArray == null){
				logger.error("execution proxy returned null document for BiObjectDocumetn: "+biObj.getLabel());
				exportFile = createErrorFile(biObj, null , randomNamesToName);
			}
			else{
				try{
					String checkerror = new String(returnByteArray);
					if(checkerror.startsWith("error")){
						logger.error("Error found in execution, make txt file");
						String fileName = "Error "+biObj.getLabel()+"-"+biObj.getName();
						exportFile = File.createTempFile(fileName, ".txt");
						randomNamesToName.put(exportFile.getName(), fileName+".txt");
					}
					else{
						logger.error("Export ok for biObj with label "+biObj.getLabel());
						String fileName = biObj.getLabel()+"-"+biObj.getName();
						exportFile = File.createTempFile(fileName, ".xls");
						randomNamesToName.put(exportFile.getName(), fileName+".xls");
					}

					FileOutputStream stream = new FileOutputStream(exportFile);
					stream.write(returnByteArray);

					logger.debug("create an export file named "+exportFile.getName());

					filesToZip.add(exportFile);

					// update progress table
					threadDAO.incrementProgressThread(progressThreadId);
					logger.debug("progress Id incremented");

				}
				catch (Exception e) {
					logger.error("Exception in  writeing export file for BiObject with label: "+biObj.getLabel()+": delete DB row",e);
					deleteDBRowInCaseOfError(threadDAO, progressThreadId);
					throw new SpagoBIServiceException("Exception in  writeing export file for BiObject with label "+biObj.getLabel()+" delete DB row", e);
				}
			}
		}
		File zipFile = null;
		try{
			zipFile = createZipFile(filesToZip, randomNamesToName);
			logger.debug("zip created");
		}
		catch (Exception e) {
			logger.error("Error in writeing the zip file: DB row will be deleted to avoid cycling problems");
			deleteDBRowInCaseOfError(threadDAO, progressThreadId);
			throw new SpagoBIServiceException("Error in writeing the zip file; DB row will be deleted to avoid cycling problems", e);
		}

		try{

			threadDAO.setDownloadProgressThread(progressThreadId);
			logger.debug("Thread row in database set as download state");
		}
		catch (EMFUserError e) {
			logger.error("Error in closing database row relative to thread "+progressThreadId+" row will be deleted");
			deleteDBRowInCaseOfError(threadDAO, progressThreadId);
			throw new SpagoBIServiceException("Error in closing database row relative to thread "+progressThreadId+" row will be deleted", e);
		}


		logger.debug("OUT");
	}

	/**
	 *  Zip file placed under resource_directory/massiveExport/functionalityCd
	 * @param filesToZip
	 * @param randomNamesToName
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 */

	public File createZipFile(List<File> filesToZip, Map<String, String> randomNamesToName) throws ZipException, IOException{
		logger.debug("IN");
		File zipFile = Utilities.createMassiveExportZip(functionality.getCode(), zipKey);
		logger.debug("zip file written "+zipFile.getAbsolutePath());
		ZipOutputStream out = null;
		FileInputStream in = null;
		try{
			out = new ZipOutputStream(new FileOutputStream(zipFile)); 
			for (Iterator iterator = filesToZip.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				in = new FileInputStream(file); 
				String fileName = file.getName();
				String realName = randomNamesToName.get(fileName);
				ZipEntry zipEntry=new ZipEntry(realName);
				out.putNextEntry(zipEntry);

				int len; 
				while ((len = in.read(buf)) > 0) 
				{ 
					out.write(buf, 0, len); 
				} 

				out.closeEntry(); 
				in.close(); 
			}
			out.flush();
			out.close();
		}
		finally{
			if(in != null) in.close();
			if(out != null) out.close();
		}

		//filesToZip
		logger.debug("OUT");
		return zipFile;
	}


	//	public File createZipFile(List<File> filesToZip, Map<String, String> randomNamesToName) throws ZipException, IOException{
	//		logger.debug("IN");
	//
	//		String dirS = System.getProperty("java.io.tmpdir");
	//		if(!dirS.endsWith(File.separator)){
	//			dirS+=File.separator;
	//		}
	//
	//		dirS+=functionality.getCode();
	//		logger.debug("write zip file in "+dirS);
	//		File dir = new File(dirS);
	//		// if not exists create directory
	//		dir.mkdir();		
	//
	//		String folde = dir.getAbsolutePath();
	//		if(!folde.endsWith(File.separator)){
	//			folde+=File.separator;
	//		}
	//
	//		//File zipFile = File.createTempFile(zipKey, ".zip");
	//		File zipFile = new File(folde+zipKey+".zip");
	//		zipFile.createNewFile();
	//		logger.debug("Zip file is "+zipFile.getAbsolutePath());
	//
	//		ZipOutputStream out = null;
	//		FileInputStream in = null;
	//		try{
	//			out = new ZipOutputStream(new FileOutputStream(zipFile)); 
	//			for (Iterator iterator = filesToZip.iterator(); iterator.hasNext();) {
	//				File file = (File) iterator.next();
	//				in = new FileInputStream(file); 
	//				String fileName = file.getName();
	//				String realName = randomNamesToName.get(fileName);
	//				ZipEntry zipEntry=new ZipEntry(realName);
	//				out.putNextEntry(zipEntry);
	//
	//				int len; 
	//				while ((len = in.read(buf)) > 0) 
	//				{ 
	//					out.write(buf, 0, len); 
	//				} 
	//
	//				out.closeEntry(); 
	//				in.close(); 
	//			}
	//			out.flush();
	//			out.close();
	//		}
	//		finally{
	//			if(in != null) in.close();
	//			if(out != null) out.close();
	//		}
	//
	//		//filesToZip
	//		logger.debug("OUT");
	//		return zipFile;
	//	}


	public File createErrorFile(BIObject biObj, Throwable error, Map randomNamesToName){
		logger.debug("IN");
		File toReturn = null;
		FileWriter fw = null;

		try{
			String fileName = "Error "+biObj.getLabel()+"-"+biObj.getName();
			toReturn = File.createTempFile(fileName, ".txt");
			randomNamesToName.put(toReturn.getName(), fileName+".txt");
			fw = new FileWriter(toReturn);
			fw.write("Error while executing biObject "+biObj.getLabel()+" - "+biObj.getName()+"\n");
			if(error != null){
				StackTraceElement[] errs = error.getStackTrace();
				for (int i = 0; i < errs.length; i++) {
					String err = errs[i].toString();
					fw.write(err+"\n");
				}
			}
			fw.flush();
		}
		catch (Exception e) {
			logger.error("Error in wirting error file for biObj "+biObj.getLabel());
			deleteDBRowInCaseOfError(threadDAO, progressThreadId);
			throw new SpagoBIServiceException("Error in wirting error file for biObj "+biObj.getLabel(), e);			
		}
		finally{
			if(fw != null ) {
				try {
					fw.flush();
					fw.close();	
				} catch (IOException e) {}
			}
		}
		logger.debug("OUT");
		return toReturn;
	}



	public boolean isDaemon() {
		return false;
	}

	public void release() {
	}





	public List getBiObjects() {
		return biObjects;
	}





	public void setBiObjects(List biObjects) {
		this.biObjects = biObjects;
	}





	/**
	 * Checks if is complete without error.
	 * 
	 * @return true, if is complete without error
	 */
	public boolean isCompleteWithoutError() {
		return completeWithoutError;
	}





	public IEngUserProfile getProfile() {
		return profile;
	}





	public void setProfile(IEngUserProfile profile) {
		this.profile = profile;
	}


	public void deleteDBRowInCaseOfError(IProgressThreadDAO threadDAO, Integer progressThreadId){
		logger.debug("IN");
		try {
			threadDAO.deleteProgressThread(progressThreadId);
		} catch (EMFUserError e1) {
			logger.error("Error in deleting the row with the progress id "+progressThreadId);
		}
		logger.debug("OUT");

	}


}
