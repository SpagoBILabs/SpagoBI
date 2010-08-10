/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.analiticalmodel.document.x;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.ObjNote;
import it.eng.spagobi.analiticalmodel.document.dao.IObjNoteDAO;
import it.eng.spagobi.analiticalmodel.document.handlers.BIObjectNotesManager;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.commons.dao.DAOFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;
import org.xml.sax.InputSource;

/**
 * 
 * @author Gavardi Giulio
 *
 */
public class PrintNotesAction extends AbstractSpagoBIAction {

	public static final String SERVICE_NAME = "PRINT_NOTES_ACTION";

	public static final String SBI_OUTPUT_TYPE = "SBI_OUTPUT_TYPE";

	private static final String TEMPLATE_NAME="notesPrintedTemplate.jrxml";
	private static final String TEMPLATE_PATH="/WEB-INF/classes/it/eng/spagobi/analiticalmodel/document/resources/";

	// logger component
	private static Logger logger = Logger.getLogger(PrintNotesAction.class);
	InputSource inputSource;

	public void doService() {
		logger.debug("IN");
		IEngUserProfile profile = (IEngUserProfile) this.getUserProfile();
		ExecutionInstance executionInstance;
		executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
		String executionIdentifier=new BIObjectNotesManager().getExecutionIdentifier(executionInstance.getBIObject());
		Integer biobjectId = executionInstance.getBIObject().getId();
		List objNoteList=null;
		try {
			objNoteList=DAOFactory.getObjNoteDAO().getListExecutionNotes(biobjectId, executionIdentifier);
		} catch (EMFUserError e1) {
			logger.error("Error in retrieving obj notes",e1);
			return;
		} catch (Exception e1) {
			logger.error("Error in retrieving obj notes",e1);
			return;
		}

		String outputType = "PDF";
		RequestContainer requestContainer=getRequestContainer();
		SourceBean sb=requestContainer.getServiceRequest();
		outputType=(String)sb.getAttribute(SBI_OUTPUT_TYPE);
		if(outputType==null)outputType="PDF";

		String templateStr = getTemplateTemplate();


		//JREmptyDataSource conn=new JREmptyDataSource(1);
		//Connection conn = getConnection("SpagoBI",getHttpSession(),profile,obj.getId().toString());		
		JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(objNoteList);            

		HashedMap parameters=new HashedMap();
		parameters.put("PARAM_OUTPUT_FORMAT", outputType);
		parameters.put("TITLE", executionInstance.getBIObject().getLabel());

		UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
		UUID uuid_local = uuidGen.generateTimeBasedUUID();
		String executionId = uuid_local.toString();
		executionId = executionId.replaceAll("-", "");
		//Creta etemp file
		String dirS = System.getProperty("java.io.tmpdir");
		File dir = new File(dirS);
		dir.mkdirs();
		String fileName="notes"+executionId;
		OutputStream out=null;
		File tmpFile=null; 
		try {								
			tmpFile = File.createTempFile(fileName, "." + outputType, dir);
			out = new FileOutputStream(tmpFile);
			StringBufferInputStream sbis=new StringBufferInputStream(templateStr);
			logger.debug("compiling report");
			JasperReport report  = JasperCompileManager.compileReport(sbis);
			//report.setProperty("", )
			logger.debug("filling report");
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, datasource);
			JRExporter exporter=null;
			if(outputType.equalsIgnoreCase("PDF")){
				exporter = (JRExporter)Class.forName("net.sf.jasperreports.engine.export.JRPdfExporter").newInstance();
				if(exporter == null) exporter = new JRPdfExporter(); 	
			}
			else{
				exporter = (JRExporter)Class.forName("net.sf.jasperreports.engine.export.JRRtfExporter").newInstance();
				if(exporter == null) exporter = new JRRtfExporter(); 					
			}

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			logger.debug("exporting report");
			exporter.exportReport();

		} catch(Throwable e) {
			logger.error("An exception has occured", e);
			return;		
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				logger.error("Error closing output", e);
			}
		}

		String mimeType;
		if(outputType.equalsIgnoreCase("RTF")){
			mimeType = "application/rtf";
		}
		else{
			mimeType = "application/pdf";
		}


		HttpServletResponse response = getHttpResponse();
		response.setContentType(mimeType);							
		response.setHeader("Content-Disposition", "filename=\"report." + outputType + "\";");
		response.setContentLength((int) tmpFile.length());
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(tmpFile));
			int b = -1;
			while ((b = in.read()) != -1) {
				response.getOutputStream().write(b);
			}
			response.getOutputStream().flush();
			in.close();
		}
		catch (Exception e) {
			logger.error("Error while writing the content output stream", e);			
		}
		finally {
			tmpFile.delete();
		}

		logger.debug("OUT");


	}

	private void saveNotes(String execIdentifier, Integer objectId, String notes, ObjNote objnote) throws Exception {
		logger.debug("IN");
		try {
			IObjNoteDAO objNoteDAO = DAOFactory.getObjNoteDAO();
			if (objnote != null) {
				objnote.setContent(notes.getBytes());
				objNoteDAO.modifyExecutionNotes(objnote);
			} else {
				objnote = new ObjNote();
				objnote.setContent(notes.getBytes());
				objnote.setExecReq(execIdentifier);
				objNoteDAO.saveExecutionNotes(objectId, objnote);
			}
		} finally {
			logger.debug("OUT");
		}
	}




	/**
	 * Gets the template template.
	 * 
	 * @return the template template
	 */
	public String getTemplateTemplate() {
		StringBuffer buffer = new StringBuffer();
		logger.debug("IN");
		try{

			String rootPath=ConfigSingleton.getRootPath();
			logger.debug("rootPath: "+rootPath!=null ? rootPath : "");
			String templateDirPath=rootPath+TEMPLATE_PATH;
			logger.debug("templateDirPath: "+templateDirPath!=null ? templateDirPath : "");
			templateDirPath+=TEMPLATE_NAME;
			logger.debug("templatePath: "+templateDirPath!=null ? templateDirPath : "");
			if (templateDirPath!=null){
				File file=new File(templateDirPath);
				if(file!=null){
					logger.debug("File found ");
				}
				FileInputStream fis=new FileInputStream(file);
				if(fis!=null){
					logger.debug("File Input Stream created");
				}
				inputSource=new InputSource(fis);
				if(inputSource!=null){
					logger.debug("Input Source created");
				}
				BufferedReader reader = new BufferedReader( new InputStreamReader(fis) );
				if(reader!=null){
					logger.debug("Buffer Reader created");
				}
				String line = null;
				try {
					while( (line = reader.readLine()) != null) {
						buffer.append(line + "\n");
					}
				} catch (IOException e) {
					logger.error("error in appending lines to the buffer",e);
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			logger.error("error in retrieving the template",e);
			e.printStackTrace();
			return null;
		}
		logger.debug("OUT");
		return buffer.toString();
	}

	/**
	 * This method, based on the data sources table, gets a database connection
	 * and return it
	 * 
	 * @return the database connection
	 */
//	private Connection getConnection(String requestConnectionName,HttpSession session,IEngUserProfile profile,String documentId) {
//	logger.debug("IN.documentId:"+documentId);
//	DataSourceServiceProxy proxyDS = new DataSourceServiceProxy((String)profile.getUserUniqueIdentifier(),session);
//	IDataSource ds =null;
//	if (requestConnectionName!=null){
//	ds = proxyDS.getDataSourceByLabel(requestConnectionName);
//	}else{
//	ds = proxyDS.getDataSource(documentId);
//	}

//	String schema=null;
//	try {
//	if (ds.checkIsMultiSchema()){
//	String attrname=ds.getSchemaAttribute();
//	if (attrname!=null) schema = (String)profile.getUserAttribute(attrname);
//	}
//	} catch (EMFInternalError e) {
//	logger.error("Cannot retrive ENTE", e);
//	}

//	if (ds==null) {
//	logger.warn("Data Source IS NULL. There are problems reading DataSource informations");
//	return null;
//	}
//	// get connection
//	Connection conn = null;

//	try {
//	conn = ds.toSpagoBiDataSource().readConnection(schema);
//	} catch (Exception e) {
//	logger.error("Cannot retrive connection", e);
//	} 

//	return conn;

//	}










}
