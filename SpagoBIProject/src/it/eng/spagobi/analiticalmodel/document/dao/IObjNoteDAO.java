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
package it.eng.spagobi.analiticalmodel.document.dao;

import it.eng.spagobi.analiticalmodel.document.bo.ObjNote;
import it.eng.spagobi.commons.dao.ISpagoBIDao;

import java.util.List;



public interface IObjNoteDAO extends ISpagoBIDao{

	/**
	 * Save Notes for a specific execution of the biobject.
	 * 
	 * @param biobjId id of the biobject executed
	 * @param objNote notes to save
	 * 
	 * @throws Exception the exception
	 */
	public void saveExecutionNotes(Integer biobjId, ObjNote objNote) throws Exception;
	
	
	/**
	 * Get Notes for a specific execution of the biobject.
	 * 
	 * @param biobjId id of the biobject executed
	 * @param execIdentif the exec identif
	 * 
	 * @return ObjNote notes saved
	 * 
	 * @throws Exception the exception
	 */
	public ObjNote getExecutionNotes(Integer biobjId, String execIdentif) throws Exception;
	
	/**
	 * Get Notes for a specific execution of the biobject by owner.
	 * 
	 * @param biobjId id of the biobject executed
	 * @param execIdentif the exec identif
	 * @param owner the note's owner
	 * 
	 * @return ObjNote notes saved
	 * 
	 * @throws Exception the exception
	 */
	public ObjNote getExecutionNotesByOwner(Integer biobjId, String execIdentif, String owner) throws Exception;
	
	/**
	 * Get Notes for a specific execution of the biobject.
	 * 
	 * @param biobjId id of the biobject executed
	 * @param execIdentif the exec identif
	 * 
	 * @return ObjNote notes saved
	 * 
	 * @throws Exception the exception
	 */
	public List getListExecutionNotes(Integer biobjId, String execIdentif) throws Exception;
	
	/**
	 * Modify execution notes.
	 * 
	 * @param objNote the obj note
	 * 
	 * @throws Exception the exception
	 */
	public void modifyExecutionNotes(ObjNote objNote) throws Exception;
	
	/**
	 * Deletes all notes associated to the BIObject with the id specified in input.
	 * 
	 * @param biobjId the biobj id
	 * 
	 * @throws Exception the exception
	 */
	public void eraseNotes(Integer biobjId) throws Exception;
	
	/**
	 * Deletes all notes associated to the BIObject with the id and the owner specified in input.
	 * 
	 * @param biobjId the biobj id
	 * @param owner the user owner the note
	 * 
	 * @throws Exception the exception
	 */
	public void eraseNotesByOwner(Integer biobjId, String execIdentif, String owner) throws Exception ;
	
}
