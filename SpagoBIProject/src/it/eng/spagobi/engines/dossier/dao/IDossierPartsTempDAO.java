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
package it.eng.spagobi.engines.dossier.dao;

import it.eng.spago.error.EMFInternalError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;

import java.util.Map;

/**
 * 
 * @author Zerbetto (davide.zerbetto@eng.it)
 *
 */
public interface IDossierPartsTempDAO extends ISpagoBIDao{
	
	/**
	 * Store image.
	 * 
	 * @param dossierId the dossier id
	 * @param image the image
	 * @param docLogicalName the doc logical name
	 * @param pageNum the page num
	 * @param workflowProcessId the workflow process id
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public void storeImage(Integer dossierId, byte[] image, String docLogicalName, int pageNum, Long workflowProcessId) throws EMFInternalError;
	
	/**
	 * Gets the images of dossier part.
	 * 
	 * @param dossierId the dossier id
	 * @param pageNum the page num
	 * @param workflowProcessId the workflow process id
	 * 
	 * @return the images of dossier part
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public Map getImagesOfDossierPart(Integer dossierId, int pageNum, Long workflowProcessId) throws EMFInternalError;
	
	/**
	 * Gets the notes of dossier part.
	 * 
	 * @param dossierId the dossier id
	 * @param pageNum the page num
	 * @param workflowProcessId the workflow process id
	 * 
	 * @return the notes of dossier part
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public byte[] getNotesOfDossierPart(Integer dossierId, int pageNum, Long workflowProcessId) throws EMFInternalError;
	
	/**
	 * Store note.
	 * 
	 * @param dossierId the dossier id
	 * @param pageNum the page num
	 * @param noteContent the note content
	 * @param workflowProcessId the workflow process id
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public void storeNote(Integer dossierId, int pageNum, byte[] noteContent, Long workflowProcessId) throws EMFInternalError;
	
	/**
	 * Erases the dossier temporary parts for the process specified at input.
	 * 
	 * @param dossierId The id of the dossier
	 * @param workflowProcessId The id of the process
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public void cleanDossierParts(Integer dossierId, Long workflowProcessId) throws EMFInternalError;
	
	/**
	 * Erases the dossier temporary parts for all the processes that involve the dossier specified at input.
	 * 
	 * @param dossierId The dossier id
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public void eraseDossierParts(Integer dossierId) throws EMFInternalError;

}
