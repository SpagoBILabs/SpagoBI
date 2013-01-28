/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.sdk.documents.stub;

public interface DocumentsService extends java.rmi.Remote {
    public it.eng.spagobi.sdk.documents.bo.SDKDocument[] getDocumentsAsList(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public it.eng.spagobi.sdk.documents.bo.SDKDocument getDocumentById(java.lang.Integer in0) throws java.rmi.RemoteException;
    public it.eng.spagobi.sdk.documents.bo.SDKDocument getDocumentByLabel(java.lang.String in0) throws java.rmi.RemoteException;
    public it.eng.spagobi.sdk.documents.bo.SDKFunctionality getDocumentsAsTree(java.lang.String in0) throws java.rmi.RemoteException;
    public java.lang.String[] getCorrectRolesForExecution(java.lang.Integer in0) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NonExecutableDocumentException;
    public it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter[] getDocumentParameters(java.lang.Integer in0, java.lang.String in1) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NonExecutableDocumentException;
    public java.util.HashMap getAdmissibleValues(java.lang.Integer in0, java.lang.String in1) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NonExecutableDocumentException;
    public it.eng.spagobi.sdk.documents.bo.SDKTemplate downloadTemplate(java.lang.Integer in0) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
    public void uploadTemplate(java.lang.Integer in0, it.eng.spagobi.sdk.documents.bo.SDKTemplate in1) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
    public java.lang.Integer saveNewDocument(it.eng.spagobi.sdk.documents.bo.SDKDocument in0, it.eng.spagobi.sdk.documents.bo.SDKTemplate in1, java.lang.Integer in2) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
    public it.eng.spagobi.sdk.documents.bo.SDKExecutedDocumentContent executeDocument(it.eng.spagobi.sdk.documents.bo.SDKDocument in0, it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter[] in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NonExecutableDocumentException, it.eng.spagobi.sdk.exceptions.InvalidParameterValue, it.eng.spagobi.sdk.exceptions.MissingParameterValue, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
    public void uploadDatamartTemplate(it.eng.spagobi.sdk.documents.bo.SDKTemplate in0, it.eng.spagobi.sdk.documents.bo.SDKTemplate in1, java.lang.String in2) throws java.rmi.RemoteException;
    public void uploadDatamartModel(it.eng.spagobi.sdk.documents.bo.SDKTemplate in0) throws java.rmi.RemoteException;
    public it.eng.spagobi.sdk.documents.bo.SDKTemplate downloadDatamartFile(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
    public it.eng.spagobi.sdk.documents.bo.SDKTemplate downloadDatamartModelFiles(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public java.util.HashMap getAllDatamartModels() throws java.rmi.RemoteException;
    public void uploadMondrianSchema(it.eng.spagobi.sdk.documents.bo.SDKSchema in0) throws java.rmi.RemoteException;
}
