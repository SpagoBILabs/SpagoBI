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
package it.eng.spagobi.tools.massiveExport.utils;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class Utilities {

	private static Logger logger = Logger.getLogger(Utilities.class);


	public static List getContainedObjFilteredbyType(LowFunctionality funct, String docType){
		logger.debug("IN");
		List objList = funct.getBiObjects();
		// filteronly selected type
		List<BIObject> selectedObjects = new ArrayList<BIObject>();
		if(docType == null){
			selectedObjects = objList;
		}
		else {
			for (Iterator iterator = objList.iterator(); iterator.hasNext();) {
				BIObject biObject = (BIObject) iterator.next();
				if(biObject.getBiObjectTypeCode().equals(docType)){
					selectedObjects.add(biObject);
				}

			}
		}
		logger.debug("OUT");
		return selectedObjects;
	}



	public static File getZipFile(String randomKey, String functionalityCd) throws SpagoBIServiceException{
		logger.debug("IN");
		// get Zip file
		String dirS = System.getProperty("java.io.tmpdir");
		if(!dirS.endsWith(File.separator)){
			dirS+=File.separator;
		}
		dirS += functionalityCd;
		if(!dirS.endsWith(File.separator)){
			dirS+=File.separator;
		}
		String filePath = dirS+randomKey+".zip";

		logger.debug("directory with zip is "+dirS);

		if(!(new File(dirS)).exists()){
			logger.error("not existing directory "+dirS);
			throw new SpagoBIRuntimeException("not existing directory "+dirS, null);
		}

		File zip = new File(filePath);
		if(!(zip.exists())){
			logger.error("not existing zip file "+filePath);
			throw new SpagoBIRuntimeException("not existing zip file "+filePath, null);
		}
		
		logger.debug("OUT");
		return zip;
	}

	
	
}
