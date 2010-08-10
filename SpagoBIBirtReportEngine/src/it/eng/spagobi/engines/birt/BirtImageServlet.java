/**
 * 
 * LICENSE: see BIRT.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.birt;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.services.proxy.DocumentExecuteServiceProxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


public class BirtImageServlet extends HttpServlet {

	private transient Logger logger = Logger.getLogger(this.getClass());
	private static final String CHART_LABEL="chart_label"; 
	private HttpSession session = null;
	String userId = null;
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) {	
		Map allParams = request.getParameterMap();
		
		String chartLabel = request.getParameter(CHART_LABEL);
	
		ServletOutputStream ouputStream = null;
		InputStream fis = null;
		File imageFile = null;
		String completeImageFileName = "";
		response.setContentType("image");

		
		if (chartLabel == null){
			String imagePath = request.getParameter("imagePath");
			String imageDirectory = getServletContext().getRealPath(imagePath);
			String imageFileName = request.getParameter("imageID");
	
		
			if (imageDirectory == null || imageFileName == null) {
				logger.error("Image directory or image file name missing.");
				return;
			}
			//gets complete image file name:
			if (imageDirectory.endsWith("/"))
				completeImageFileName = imageDirectory + imageFileName;
			else completeImageFileName = imageDirectory + "/" + imageFileName;
	
			imageFile = new File(completeImageFileName);
			
			if (imageDirectory.endsWith("/"))
				imageFile = new File(imageDirectory + imageFileName);
			else imageFile = new File(imageDirectory + "/" + imageFileName);
			
			if (imageFile == null || !imageFile.isFile()) {
				logger.error("File [" + completeImageFileName + "] not found.");
				return;
			}
		
		
			try{		
				fis = new FileInputStream(imageFile);
			}catch (Exception e) {
				logger.error("Error writing image into file input stream", e);
			}
		}
		else{
			// USER PROFILE
			session = request.getSession();
			IEngUserProfile profile = (IEngUserProfile) session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
			userId = (String) profile.getUserUniqueIdentifier();
			logger.debug("userId=" + userId);
			
			fis = executeEngineChart(allParams);
		}
		try {
			
			ouputStream = response.getOutputStream();
			
			byte[] buffer = new byte[1024];
			int len; 
			while ((len = fis.read(buffer)) >= 0) 
				ouputStream.write(buffer, 0, len);
			
			if ( (chartLabel == null)) imageFile.delete();
			
		} catch (Exception e) {
			logger.error("Error writing image into servlet output stream", e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("Error while closing FileInputStream on file " + completeImageFileName, e);
				}
			if (ouputStream != null) {
				try {
					ouputStream.flush();
					ouputStream.close();
				} catch (IOException e) {
					logger.error("Error flushing servlet output stream", e);
				}
			}
		} 

	}

	/**
	 * This method execute the engine chart and returns its image in byte[]
	 * @param request the httpRequest 
	 * @return the chart in inputstream form
	 */
	private InputStream executeEngineChart(Map parametersMap){
		logger.debug("IN");
		InputStream is = null;

		try {
			// chart_label : indicating the label of the chart that has to be called.
			String[] arLabelValue=(String[])parametersMap.get(CHART_LABEL);
			String labelValue=arLabelValue[0];
			logger.debug("execute chart with lable "+labelValue);
			
			HashMap chartParameters=new HashMap();
			for (Iterator iterator = parametersMap.keySet().iterator(); iterator.hasNext();) {
				String namePar = (String) iterator.next();
				if(!namePar.equalsIgnoreCase(CHART_LABEL)){
					String[] value=(String[])parametersMap.get(namePar);
					chartParameters.put(namePar, value[0]);
				}
			}		
	
			DocumentExecuteServiceProxy proxy=new DocumentExecuteServiceProxy(userId,session);
			logger.debug("Calling Service");
		    byte[] image=proxy.executeChart(labelValue, chartParameters);
			logger.debug("Back from Service");
			
			is=new ByteArrayInputStream(image);
			
		}catch (Exception e) {
			logger.error("Error in execution chart engine",e);
		//s	throw new Exception(e);
		}
		return is;
	}
	
	private Map getMapParameters(Map allParams){
		Map toReturn = new HashMap();
		String[] strArParams = (String[])allParams.get("params");
		String strParams = strArParams[0];
		
		try{
			strParams = strParams.replace("{", "");
			strParams = strParams.replace("}", "");
			String[] arParamsImage= strParams.split(",");
			for (int i=0; i< arParamsImage.length; i++){
				String name = arParamsImage[i].substring(0,arParamsImage[i].indexOf("="));
				String value =  arParamsImage[i].substring(arParamsImage[i].indexOf("=")+1);
				if (value != null && !value.equals("")) toReturn.put(name.trim(), value.trim());
			}
		}catch(Exception e){
			logger.error("Error while parsing chart's parameter map. Error: " + e );
		}
		return toReturn;
	}
	
}
