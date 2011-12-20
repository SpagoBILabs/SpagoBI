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
package it.eng.spagobi.tools.importexport;

import it.eng.spago.error.EMFUserError;

import java.util.List;

public interface IExportManager {

	/**
	 * Prepare the environment for export.
	 * 
	 * @param pathExpFold Path of the export folder
	 * @param nameExpFile the name to give to the exported file
	 * @param expSubObj Flag which tells if it's necessary to export subobjects
	 * @param expSnaps the exp snaps
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void prepareExport(String pathExpFold, String nameExpFile, 
			boolean expSubObj, boolean expSnaps) throws EMFUserError;
	
	/**
	 * Exports objects
	 * 
	 * @param objPaths List of path of the objects to export
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void exportObjects(List objPaths) throws EMFUserError;
	
	/**
	 * Exports resources (OLAP schemas, ETL jobs, datamarts)
	 * 
	 * @throws EMFUserError the EMF user error
	 */
//	public void exportResources() throws EMFUserError;
	
	/**
	 * Creates the archive export file
	 * 
	 * @throws EMFUserError
	 */
	public void createExportArchive() throws EMFUserError;
	
	/**
	 * Clean the export environment (close sessions and delete temporary files).
	 */
	public void cleanExportEnvironment();
}
