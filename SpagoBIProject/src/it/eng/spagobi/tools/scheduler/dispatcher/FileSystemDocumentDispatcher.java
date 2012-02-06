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
package it.eng.spagobi.tools.scheduler.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DocumentMetadataProperty;
import it.eng.spagobi.analiticalmodel.document.bo.Snapshot;
import it.eng.spagobi.analiticalmodel.document.dao.ISnapshotDAO;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ExecutionProxy;
import it.eng.spagobi.tools.massiveExport.bo.ProgressThread;
import it.eng.spagobi.tools.massiveExport.dao.IProgressThreadDAO;
import it.eng.spagobi.tools.massiveExport.services.StartMassiveScheduleAction;
import it.eng.spagobi.tools.massiveExport.utils.Utilities;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetacontentDAO;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetadataDAO;
import it.eng.spagobi.tools.scheduler.to.DispatchContext;
import it.eng.spagobi.tools.scheduler.utils.JavaClassDestination;
import it.eng.spagobi.tools.scheduler.utils.SchedulerUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class FileSystemDocumentDispatcher implements IDocumentDispatchChannel {
	
	private DispatchContext dispatchContext;
	List<File> filesToZip;
	String zipFileName;
	Map<String, String> randomNamesToName;
	ProgressThread progressThread;
	Integer progressThreadId;
	IProgressThreadDAO progressThreadDAO;

	
	// logger component
	private static Logger logger = Logger.getLogger(FileSystemDocumentDispatcher.class); 
	
	public FileSystemDocumentDispatcher(DispatchContext dispatchContext) {
		this.dispatchContext = dispatchContext;
		this.filesToZip = new ArrayList<File>();
		this.zipFileName = generateZipFileName();
		this.randomNamesToName = new HashMap<String, String>();
	}
	
	public boolean canDispatch(BIObject document)  {
		return true;
	}
	
	public boolean dispatch(BIObject document, byte[] executionOutput) {
		File exportFile;

		String fileExtension = dispatchContext.getFileExtension();

		try {
			progressThreadDAO = DAOFactory.getProgressThreadDAO();
			
//			ProgressThread runningProgressThread = progressThreadDAO.loadActiveProgressThreadByUserIdAndFuncCd(
//					dispatchContext.getOwner(), dispatchContext.getFunctionalityTreeFolderLabel());
			
			if(progressThread == null) {
				progressThread = new ProgressThread(
						dispatchContext.getOwner(), 
						dispatchContext.getTotalNumberOfDocumentsToDispatch(), 
						dispatchContext.getFunctionalityTreeFolderLabel(), null, zipFileName, ProgressThread.TYPE_MASSIVE_SCHEDULE);
				
				progressThreadId = progressThreadDAO.insertProgressThread(progressThread);
				progressThreadDAO.setStartedProgressThread(progressThreadId);
			}
			
		
			if(executionOutput == null) {
				logger.error("execution proxy returned null document for BiObjectDocumetn: " + document.getLabel());
				exportFile = createErrorFile(document, null , randomNamesToName);
			} else{
				String checkError = new String(executionOutput);
				if(checkError.startsWith("error") || checkError.startsWith("{\"errors\":")){
					logger.error("Error found in execution, make txt file");
					String fileName = "Error " + document.getLabel() + "-" + document.getName();
					exportFile = File.createTempFile(fileName, ".txt");
					randomNamesToName.put(exportFile.getName(), fileName+".txt");
				} else{
					logger.error("Export ok for biObj with label "+document.getLabel());
					String fileName = document.getLabel() + "-" + document.getName();
					exportFile = File.createTempFile(fileName, fileExtension); 
					randomNamesToName.put(exportFile.getName(), fileName + fileExtension);
				}

				FileOutputStream stream = new FileOutputStream(exportFile);
				stream.write(executionOutput);

				logger.debug("create an export file named " + exportFile.getName());

				filesToZip.add(exportFile);
				progressThreadDAO.incrementProgressThread(progressThreadId);
			}
		} catch (Exception e) {
			throw new SpagoBIServiceException("Exception in  writeing export file for BiObject with label "+document.getLabel()+" delete DB row", e);
		}
		
		return true;
	}

	public void close() {
		
		byte[] buffer = new byte[1024]; 
		ZipOutputStream out = null;
		FileInputStream in = null;
		
		logger.debug("IN");
		
		try {
			
			File destinationFolder = new File(dispatchContext.getDestinationFolder());
			if(!destinationFolder.exists()) {
				destinationFolder.mkdirs();
			}
			
			File zipFile = new File(destinationFolder, zipFileName + ".zip");
	
			logger.debug("zip file written "+zipFile.getAbsolutePath());
	
			out = new ZipOutputStream(new FileOutputStream(zipFile)); 
			for (Iterator iterator = filesToZip.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				in = new FileInputStream(file); 
				String fileName = file.getName();
				String realName = randomNamesToName.get(fileName);
				ZipEntry zipEntry=new ZipEntry(realName);
				out.putNextEntry(zipEntry);

				int len; 
				while ((len = in.read(buffer)) > 0) 
				{ 
					out.write(buffer, 0, len); 
				} 

				out.closeEntry(); 
				in.close(); 
			}
			out.flush();
			out.close();
			
			progressThreadDAO.setDownloadProgressThread(progressThreadId);
		} catch(Throwable t) {
			throw new DispatchException("An unexpected error occured while closing dipatcher", t);
		} finally{
			if(in != null)
			try {
				in.close();
			} catch (IOException e) {
				throw new DispatchException("An unexpected error occured while closing input stream");
			}
			if(out != null)
			try {
				out.close();
			} catch (IOException e) {
				throw new DispatchException("An unexpected error occured while closing output stream");
			}
			
			logger.debug("OUT");
		}
	}
	
	private String generateZipFileName(){
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss.SSS");
		String randomName = formatter.format(new Date());			
		randomName=randomName.replaceAll(" ", "_");
		randomName=randomName.replaceAll(":", "-");
		return randomName;

	}

	
	


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
	
	public void setDispatchContext(DispatchContext dispatchContext) {
		this.dispatchContext = dispatchContext;
	}

	
}
