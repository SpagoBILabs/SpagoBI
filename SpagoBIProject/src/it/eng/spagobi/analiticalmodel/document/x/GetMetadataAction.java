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

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjMetaDataAndContent;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 * 
 * @author Zerbetto Davide
 *
 */
public class GetMetadataAction extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "GET_METADATA_ACTION";
	
	// REQUEST PARAMETERS
	public static final String OBJECT_ID = "OBJECT_ID";
	public static final String SUBOBJECT_ID = "SUBOBJECT_ID";
	
	//GENERAL METADATA NAMES
	public static final String LABEL = "metadata.docLabel";
	public static final String NAME = "metadata.docName";
	public static final String TYPE = "metadata.docType";
	public static final String ENG_NAME = "metadata.docEngine";
	public static final String RATING = "metadata.docRating";
	public static final String SUBOBJ_NAME = "metadata.subobjName";
	
	// logger component
	private static Logger logger = Logger.getLogger(GetMetadataAction.class);
	
	public void doService() {
		logger.debug("IN");
		try {
			JSONArray toReturn = new JSONArray();
			
			Integer objectId = this.getAttributeAsInteger(OBJECT_ID);
			logger.debug("Object id = " + objectId);
			Integer subObjectId = null;
			try {
				subObjectId = this.getAttributeAsInteger(SUBOBJECT_ID);
			} catch (NumberFormatException e) {}
			logger.debug("Subobject id = " + subObjectId);
			
			List metaDataAndContents = new ArrayList();
			
			HttpServletRequest httpRequest = getHttpRequest();
			MessageBuilder msgBuild = new MessageBuilder();
			Locale locale = msgBuild.getLocale(httpRequest);
			
			//START GENERAL METADATA
			if(subObjectId!=null){
				//SubObj Name
				ObjMetadata metaSubObjName = new ObjMetadata();
				String textSubName = msgBuild.getMessage(SUBOBJ_NAME, locale);	
				metaSubObjName.setName(textSubName);
				metaSubObjName.setDataTypeCode("GENERAL_META");
				ObjMetacontent metaContentSubObjName = new ObjMetacontent();
				SubObject subobj = DAOFactory.getSubObjectDAO().getSubObject(subObjectId);
				metaContentSubObjName.setContent(StringEscapeUtils.escapeHtml(subobj.getName()).getBytes());
				ObjMetaDataAndContent metaAndContentSubObjName = new ObjMetaDataAndContent();
				metaAndContentSubObjName.setMeta(metaSubObjName);
				metaAndContentSubObjName.setMetacontent(metaContentSubObjName);
				metaDataAndContents.add(metaAndContentSubObjName);
			}		
			
			BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(objectId);
			//Obj Label
			ObjMetadata metaObjLabel = new ObjMetadata();
			String textLabel = msgBuild.getMessage(LABEL, locale);	
			metaObjLabel.setName(textLabel);
			metaObjLabel.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjLabel = new ObjMetacontent();
			metaContentObjLabel.setContent(obj.getLabel().getBytes());
			ObjMetaDataAndContent metaAndContentObjLabel = new ObjMetaDataAndContent();
			metaAndContentObjLabel.setMeta(metaObjLabel);
			metaAndContentObjLabel.setMetacontent(metaContentObjLabel);
			metaDataAndContents.add(metaAndContentObjLabel);
			
			//Obj Name
			ObjMetadata metaObjName = new ObjMetadata();
			String textName = msgBuild.getMessage(NAME, locale);	
			metaObjName.setName(textName);
			metaObjName.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjName = new ObjMetacontent();
			metaContentObjName.setContent(obj.getName().getBytes());
			ObjMetaDataAndContent metaAndContentObjName = new ObjMetaDataAndContent();
			metaAndContentObjName.setMeta(metaObjName);
			metaAndContentObjName.setMetacontent(metaContentObjName);
			metaDataAndContents.add(metaAndContentObjName);

			
			//Obj Type
			ObjMetadata metaObjType = new ObjMetadata();
			String textType = msgBuild.getMessage(TYPE, locale);	
			metaObjType.setName(textType);
			metaObjType.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjType = new ObjMetacontent();
			metaContentObjType.setContent(obj.getBiObjectTypeCode().getBytes());
			ObjMetaDataAndContent metaAndContentObjType = new ObjMetaDataAndContent();
			metaAndContentObjType.setMeta(metaObjType);
			metaAndContentObjType.setMetacontent(metaContentObjType);
			metaDataAndContents.add(metaAndContentObjType);
			
			/*
			//Obj Rating
			ObjMetadata metaObjRating = new ObjMetadata();
			String textRating = msgBuild.getMessage(RATING, locale);	
			metaObjRating.setName(textRating);
			metaObjRating.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjRating = new ObjMetacontent();
			Double temp =  DAOFactory.getBIObjectRatingDAO().calculateBIObjectRating(obj);
			String docRating = ( temp != null ? temp.toString() : "" );
			metaContentObjRating.setContent(docRating.getBytes());
			ObjMetaDataAndContent metaAndContentObjRating = new ObjMetaDataAndContent();
			metaAndContentObjRating.setMeta(metaObjRating);
			metaAndContentObjRating.setMetacontent(metaContentObjRating);
			metaDataAndContents.add(metaAndContentObjRating);*/
			
			//Obj Engine Name
			ObjMetadata metaObjEngineName = new ObjMetadata();
			String textEngName = msgBuild.getMessage(ENG_NAME, locale);	
			metaObjEngineName.setName(textEngName);
			metaObjEngineName.setDataTypeCode("GENERAL_META");
			ObjMetacontent metaContentObjEngineName = new ObjMetacontent();
			metaContentObjEngineName.setContent(obj.getEngine().getName().getBytes());
			ObjMetaDataAndContent metaAndContentObjEngineName = new ObjMetaDataAndContent();
			metaAndContentObjEngineName.setMeta(metaObjEngineName);
			metaAndContentObjEngineName.setMetacontent(metaContentObjEngineName);
			metaDataAndContents.add(metaAndContentObjEngineName);
			
			//END GENERAL METADATA
			
	
			List metadata = DAOFactory.getObjMetadataDAO().loadAllObjMetadata();
			if (metadata != null && !metadata.isEmpty()) {
				Iterator it = metadata.iterator();
				while (it.hasNext()) {
					ObjMetadata objMetadata = (ObjMetadata) it.next();
					ObjMetacontent objMetacontent = (ObjMetacontent) DAOFactory.getObjMetacontentDAO().loadObjMetacontent(objMetadata.getObjMetaId(), objectId, subObjectId);
					ObjMetaDataAndContent metaAndContent = new ObjMetaDataAndContent();
					metaAndContent.setMeta(objMetadata);
					metaAndContent.setMetacontent(objMetacontent);	
					metaDataAndContents.add(metaAndContent);
				}
			}

			toReturn = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(metaDataAndContents, null);

			writeBackToClient( new JSONSuccess( toReturn ) ); 
			
		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Exception occurred while retrieving metadata", e);
		} finally {
			logger.debug("OUT");
		}
	}

}
