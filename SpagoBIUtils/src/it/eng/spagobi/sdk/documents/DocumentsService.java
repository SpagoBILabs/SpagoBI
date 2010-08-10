/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.sdk.documents;

import it.eng.spagobi.sdk.documents.bo.SDKDocument;
import it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter;
import it.eng.spagobi.sdk.documents.bo.SDKExecutedDocumentContent;
import it.eng.spagobi.sdk.documents.bo.SDKFunctionality;
import it.eng.spagobi.sdk.documents.bo.SDKTemplate;
import it.eng.spagobi.sdk.exceptions.InvalidParameterValue;
import it.eng.spagobi.sdk.exceptions.MissingParameterValue;
import it.eng.spagobi.sdk.exceptions.NonExecutableDocumentException;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;

import java.util.HashMap;

public interface DocumentsService {
	
	SDKDocument[] getDocumentsAsList(String type, String state, String folderPath);

	SDKDocument getDocumentById(Integer id);

	SDKDocument getDocumentByLabel(String label);
	
	SDKFunctionality getDocumentsAsTree(String initialPath);
	
	String[] getCorrectRolesForExecution(Integer documentId) throws NonExecutableDocumentException;
	
	SDKDocumentParameter[] getDocumentParameters(Integer documentId, String roleName) throws NonExecutableDocumentException;
	
	HashMap<String, String> getAdmissibleValues(Integer documentParameterId, String roleName) throws NonExecutableDocumentException;
	
	SDKTemplate downloadTemplate(Integer documentId) throws NotAllowedOperationException;
	
	void uploadTemplate(Integer documentId, SDKTemplate template) throws NotAllowedOperationException;
	
	Integer saveNewDocument(SDKDocument document, SDKTemplate template, Integer functionalityId) throws NotAllowedOperationException;

    SDKExecutedDocumentContent executeDocument(SDKDocument document, SDKDocumentParameter[] parameters, String roleName, String outputType) throws NonExecutableDocumentException, NotAllowedOperationException, InvalidParameterValue, MissingParameterValue;

}
