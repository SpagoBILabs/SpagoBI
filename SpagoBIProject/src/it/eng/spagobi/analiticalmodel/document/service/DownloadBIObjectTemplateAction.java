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
package it.eng.spagobi.analiticalmodel.document.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.action.AbstractHttpAction;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IObjTemplateDAO;
import it.eng.spagobi.commons.bo.Subreport;
import it.eng.spagobi.commons.dao.DAOFactory;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

public class DownloadBIObjectTemplateAction extends AbstractHttpAction {

	/* (non-Javadoc)
	 * @see it.eng.spagobi.commons.services.BaseProfileAction#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		
		String operation = (String)request.getAttribute("operation");
		
	     if (operation != null && operation.equalsIgnoreCase("downloadAll")) {
	    	 	    	 
	    	 freezeHttpResponse();
			 HttpServletResponse httpResp = getHttpResponse();
			 OutputStream out = httpResp.getOutputStream();
			 ZipOutputStream zipOut = new ZipOutputStream(out);
			 
			 
	         String idStr = (String)request.getAttribute("biobjectId");
	         Integer id = new Integer (idStr);         
	          
	          BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(id);
	          ObjTemplate templ1 = obj.getActiveTemplate();
        	  byte[] jcrContent1 = templ1.getContent();	  
        	  String templateFileName = templ1.getName();
        	  ZipEntry entry = new ZipEntry(templateFileName);
        	  String label = obj.getLabel();
        	  zipOut.putNextEntry(entry);
        	  zipOut.write(jcrContent1);	  
	          
        	  List subReports = DAOFactory.getSubreportDAO().loadSubreportsByMasterRptId(id);
              Iterator subReportsIt = subReports.iterator();
              while (subReportsIt.hasNext()) {
                  Subreport subRpt = (Subreport) subReportsIt.next();
                  BIObject aSubRptObj = DAOFactory.getBIObjectDAO().loadBIObjectById(subRpt.getSub_rpt_id());
                  // load the subreport template
                  ObjTemplate templ = aSubRptObj.getActiveTemplate();
                  byte[] jcrContent = templ.getContent();
                    templateFileName = templ.getName();
                    label = aSubRptObj.getLabel();
                  // put the subreport template in a folder which name is the subreport label
                  entry = new ZipEntry(label + "/" + templateFileName);
                  zipOut.putNextEntry(entry);
                  zipOut.write(jcrContent);
              }
	         
             String templateFileZIP = (String)request.getAttribute("fileName");
  			 if (templateFileName == null){
  				 templateFileName = "template.zip";
  			 }
	         httpResp.setHeader("Content-Disposition","attachment; filename=\"" + templateFileZIP + "\";");
	         zipOut.flush();
	         zipOut.close();
	         out.flush();
	         
	        }else{
		
			freezeHttpResponse();
			HttpServletResponse httpResp = getHttpResponse();
			String idTemplateStr = (String)request.getAttribute("TEMP_ID");
			Integer idTemplate = new Integer(idTemplateStr);
			IObjTemplateDAO objtempdao = DAOFactory.getObjTemplateDAO();
			ObjTemplate objTemp = objtempdao.loadBIObjectTemplate(idTemplate);	
			byte[] content = objTemp.getContent(); 
			httpResp.setHeader("Content-Disposition","attachment; filename=\"" + objTemp.getName() + "\";");
			httpResp.setContentLength(content.length);
			httpResp.getOutputStream().write(content);
			httpResp.getOutputStream().flush();
	        }
	}

}
