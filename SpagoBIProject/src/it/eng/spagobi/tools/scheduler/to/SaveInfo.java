/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.scheduler.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SaveInfo implements Serializable{

	private boolean saveAsSnapshot = false;
	private boolean saveAsDocument = false;

	private boolean sendMail = false;
	private boolean sendToDl = false;
	private boolean addToDl = false;
	private boolean sendToJavaClass = false;
	

	private String snapshotName = "";
	private String snapshotDescription = "";
	private String snapshotHistoryLength = "";
	private String documentName = "";
	private String documentDescription = "";
	private String documentHistoryLength = "";
	private boolean useFixedFolder = false;
	private String foldersTo = "";
	private boolean useFolderDataSet = false;
	private String dataSetFolderLabel = null;
	private String dataSetFolderParameterLabel = null;
	/**
	 * @return the useFixedFolder
	 */
	public boolean isUseFixedFolder() {
		return useFixedFolder;
	}

	/**
	 * @param useFixedFolder the useFixedFolder to set
	 */
	public void setUseFixedFolder(boolean useFixedFolder) {
		this.useFixedFolder = useFixedFolder;
	}

	/**
	 * @return the foldersTo
	 */
	public String getFoldersTo() {
		return foldersTo;
	}

	/**
	 * @param foldersTo the foldersTo to set
	 */
	public void setFoldersTo(String foldersTo) {
		this.foldersTo = foldersTo;
	}

	/**
	 * @return the useFolderDataSet
	 */
	public boolean isUseFolderDataSet() {
		return useFolderDataSet;
	}

	/**
	 * @param useFolderDataSet the useFolderDataSet to set
	 */
	public void setUseFolderDataSet(boolean useFolderDataSet) {
		this.useFolderDataSet = useFolderDataSet;
	}

	/**
	 * @return the dataSetFolderLabel
	 */
	public String getDataSetFolderLabel() {
		return dataSetFolderLabel;
	}

	/**
	 * @param dataSetFolderLabel the dataSetFolderLabel to set
	 */
	public void setDataSetFolderLabel(String dataSetFolderLabel) {
		this.dataSetFolderLabel = dataSetFolderLabel;
	}

	/**
	 * @return the dataSetFolderParameterLabel
	 */
	public String getDataSetFolderParameterLabel() {
		return dataSetFolderParameterLabel;
	}

	/**
	 * @param dataSetFolderParameterLabel the dataSetFolderParameterLabel to set
	 */
	public void setDataSetFolderParameterLabel(String dataSetFolderParameterLabel) {
		this.dataSetFolderParameterLabel = dataSetFolderParameterLabel;
	}

	private boolean useFixedRecipients = false;
	private String mailTos = "";
	private boolean useDataSet = false;
	private String dataSetLabel = null;
	private String dataSetParameterLabel = null;
	private boolean useExpression = false;
	private String expression = "";
	private String functionalityIds = "";
	private String mailSubj = "";
	private String mailTxt = "";
	private String javaClassPath = "";	
	private int biobjId = 0;
	private List dlIds = new ArrayList();
	
	/**
	 * Removes the dl id.
	 * 
	 * @param dlId the dl id
	 */
	public void removeDlId(Integer dlId) {
		this.dlIds.remove(dlId);
	}
	
	/**
	 * Adds the dl id.
	 * 
	 * @param dlId the dl id
	 */
	public void addDlId(Integer dlId) {
		this.dlIds.add(dlId);
	}
	
	/**
	 * Gets the document description.
	 * 
	 * @return the document description
	 */
	public String getDocumentDescription() {
		return documentDescription;
	}
	
	/**
	 * Sets the document description.
	 * 
	 * @param documentDescription the new document description
	 */
	public void setDocumentDescription(String documentDescription) {
		this.documentDescription = documentDescription;
	}
	
	/**
	 * Gets the document history length.
	 * 
	 * @return the document history length
	 */
	public String getDocumentHistoryLength() {
		return documentHistoryLength;
	}
	
	/**
	 * Sets the document history length.
	 * 
	 * @param documentHistoryLength the new document history length
	 */
	public void setDocumentHistoryLength(String documentHistoryLength) {
		this.documentHistoryLength = documentHistoryLength;
	}
	
	/**
	 * Gets the document name.
	 * 
	 * @return the document name
	 */
	public String getDocumentName() {
		return documentName;
	}
	
	/**
	 * Sets the document name.
	 * 
	 * @param documentName the new document name
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	
	/**
	 * Gets the mail tos.
	 * 
	 * @return the mail tos
	 */
	public String getMailTos() {
		return mailTos;
	}
	
	/**
	 * Sets the mail tos.
	 * 
	 * @param mailTos the new mail tos
	 */
	public void setMailTos(String mailTos) {
		this.mailTos = mailTos;
	}
	
	/**
	 * Gets the mail subj.
	 * 
	 * @return the mail subj
	 */
	public String getMailSubj() {
		return mailSubj;
	}
	
	/**
	 * Sets the mail subj.
	 * 
	 * @param mailSubj the new mail subj
	 */
	public void setMailSubj(String mailSubj) {
		this.mailSubj = mailSubj;
	}
	
	/**
	 * Gets the mail txt.
	 * 
	 * @return the mail txt
	 */
	public String getMailTxt() {
		return mailTxt;
	}
	
	/**
	 * Sets the mail txt.
	 * 
	 * @param mailTxt the new mail txt
	 */
	public void setMailTxt(String mailTxt) {
		this.mailTxt = mailTxt;
	}
	
	/**
	 * Checks if is save as document.
	 * 
	 * @return true, if is save as document
	 */
	public boolean isSaveAsDocument() {
		return saveAsDocument;
	}
	
	/**
	 * Sets the save as document.
	 * 
	 * @param saveAsDocument the new save as document
	 */
	public void setSaveAsDocument(boolean saveAsDocument) {
		this.saveAsDocument = saveAsDocument;
	}
	
	/**
	 * Checks if is save as snapshot.
	 * 
	 * @return true, if is save as snapshot
	 */
	public boolean isSaveAsSnapshot() {
		return saveAsSnapshot;
	}
	
	/**
	 * Sets the save as snapshot.
	 * 
	 * @param saveAsSnapshot the new save as snapshot
	 */
	public void setSaveAsSnapshot(boolean saveAsSnapshot) {
		this.saveAsSnapshot = saveAsSnapshot;
	}
	
	/**
	 * Checks if is send mail.
	 * 
	 * @return true, if is send mail
	 */
	public boolean isSendMail() {
		return sendMail;
	}
	
	/**
	 * Sets the send mail.
	 * 
	 * @param sendMail the new send mail
	 */
	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}
	
	/**
	 * Gets the snapshot description.
	 * 
	 * @return the snapshot description
	 */
	public String getSnapshotDescription() {
		return snapshotDescription;
	}
	
	/**
	 * Sets the snapshot description.
	 * 
	 * @param snapshotDescription the new snapshot description
	 */
	public void setSnapshotDescription(String snapshotDescription) {
		this.snapshotDescription = snapshotDescription;
	}
	
	/**
	 * Gets the snapshot history length.
	 * 
	 * @return the snapshot history length
	 */
	public String getSnapshotHistoryLength() {
		return snapshotHistoryLength;
	}
	
	/**
	 * Sets the snapshot history length.
	 * 
	 * @param snapshotHistoryLength the new snapshot history length
	 */
	public void setSnapshotHistoryLength(String snapshotHistoryLength) {
		this.snapshotHistoryLength = snapshotHistoryLength;
	}
	
	/**
	 * Gets the snapshot name.
	 * 
	 * @return the snapshot name
	 */
	public String getSnapshotName() {
		return snapshotName;
	}
	
	/**
	 * Sets the snapshot name.
	 * 
	 * @param snapshotName the new snapshot name
	 */
	public void setSnapshotName(String snapshotName) {
		this.snapshotName = snapshotName;
	}
	
	/**
	 * Gets the functionality ids.
	 * 
	 * @return the functionality ids
	 */
	public String getFunctionalityIds() {
		return functionalityIds;
	}
	
	/**
	 * Sets the functionality ids.
	 * 
	 * @param functionalityIds the new functionality ids
	 */
	public void setFunctionalityIds(String functionalityIds) {
		this.functionalityIds = functionalityIds;
	}
	
	/**
	 * Gets the dl ids.
	 * 
	 * @return the dl ids
	 */
	public List getDlIds() {
		return dlIds;
	}
	
	/**
	 * Sets the dl ids.
	 * 
	 * @param dlIds the new dl ids
	 */
	public void setDlIds(List dlIds) {
		this.dlIds = dlIds;
	}

	/**
	 * Checks if is send to dl.
	 * 
	 * @return true, if is send to dl
	 */
	public boolean isSendToDl() {
		return sendToDl;
	}

	/**
	 * Sets the send to dl.
	 * 
	 * @param sendToDl the new send to dl
	 */
	public void setSendToDl(boolean sendToDl) {
		this.sendToDl = sendToDl;
	}

	/**
	 * Checks if is adds the to dl.
	 * 
	 * @return true, if is adds the to dl
	 */
	public boolean isAddToDl() {
		return addToDl;
	}

	/**
	 * Sets the adds the to dl.
	 * 
	 * @param addToDl the new adds the to dl
	 */
	public void setAddToDl(boolean addToDl) {
		this.addToDl = addToDl;
	}

	/**
	 * Gets the biobj id.
	 * 
	 * @return the biobj id
	 */
	public int getBiobjId() {
		return biobjId;
	}

	/**
	 * Sets the biobj id.
	 * 
	 * @param biobjId the new biobj id
	 */
	public void setBiobjId(int biobjId) {
		this.biobjId = biobjId;
	}

	public boolean isUseDataSet() {
		return useDataSet;
	}

	public void setUseDataSet(boolean useDataSet) {
		this.useDataSet = useDataSet;
	}

	public String getDataSetLabel() {
		return dataSetLabel;
	}

	public void setDataSetLabel(String dataSetLabel) {
		this.dataSetLabel = dataSetLabel;
	}

	public boolean isUseExpression() {
		return useExpression;
	}

	public void setUseExpression(boolean useExpression) {
		this.useExpression = useExpression;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getDataSetParameterLabel() {
		return dataSetParameterLabel;
	}

	public void setDataSetParameterLabel(String dataSetParameterLabel) {
		this.dataSetParameterLabel = dataSetParameterLabel;
	}

	public boolean isUseFixedRecipients() {
		return useFixedRecipients;
	}

	public void setUseFixedRecipients(boolean useFixedRecipients) {
		this.useFixedRecipients = useFixedRecipients;
	}

	public boolean isSendToJavaClass() {
		return sendToJavaClass;
	}

	public void setSendToJavaClass(boolean sendToJavaClass) {
		this.sendToJavaClass = sendToJavaClass;
	}

	public String getJavaClassPath() {
		return javaClassPath;
	}

	public void setJavaClassPath(String javaClassPath) {
		this.javaClassPath = javaClassPath;
	}
	
	
}
