/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
    
    void uploadDatamartTemplate(SDKTemplate template, SDKTemplate calculatedFields, String dataSourceLabel);
    
    void uploadDatamartModel(SDKTemplate template);

    SDKTemplate downloadDatamartFile(String folderName, String fileName);
    
    SDKTemplate downloadDatamartModelFiles(String folderName, String fileDatamartName , String fileModelName);
    
    HashMap<String, String> getAllDatamartModels();
}
