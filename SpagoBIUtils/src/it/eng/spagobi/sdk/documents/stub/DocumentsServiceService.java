/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.sdk.documents.stub;

public interface DocumentsServiceService extends javax.xml.rpc.Service {
    public java.lang.String getDocumentsServiceAddress();

    public it.eng.spagobi.sdk.documents.stub.DocumentsService getDocumentsService() throws javax.xml.rpc.ServiceException;

    public it.eng.spagobi.sdk.documents.stub.DocumentsService getDocumentsService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
