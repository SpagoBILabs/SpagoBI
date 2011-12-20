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
package it.eng.spagobi.tools.objmetadata.bo;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubObjects;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiBinContents;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
* @author Antonella Giachino (antonella.giachino@eng.it)
*/

public class ObjMetacontent implements Serializable {
	
    private Integer objMetacontentId=null;
    private Integer objmetaId=null;
    private Integer biobjId=null;
    private Integer subobjId=null;
	private Date lastChangeDate=null;
	private Date creationDate=null;
	private byte[] content=null;
	private Integer binaryContentId=null;
	/**
	 * @return the objMetacontentId
	 */
	public Integer getObjMetacontentId() {
		return objMetacontentId;
	}
	/**
	 * @param objMetacontentId the objMetacontentId to set
	 */
	public void setObjMetacontentId(Integer objMetacontentId) {
		this.objMetacontentId = objMetacontentId;
	}
	
	/**
	 * @return the biobjmetaId
	 */
	public Integer getObjmetaId() {
		return objmetaId;
	}
	/**
	 * @param biobjmetaId the biobjmetaId to set
	 */
	public void setObjmetaId(Integer objmetaId) {
		this.objmetaId = objmetaId;
	}
	/**
	 * @return the biobj_id
	 */
	public Integer getBiobjId() {
		return biobjId;
	}
	/**
	 * @param biobj_id the biobj_id to set
	 */
	public void setBiobjId(Integer biobjId) {
		this.biobjId = biobjId;
	}
	/**
	 * @return the subobj_id
	 */
	public Integer getSubobjId() {
		return subobjId;
	}
	/**
	 * @param subobj_id the subobj_id to set
	 */
	public void setSubobjId(Integer subobjId) {
		this.subobjId = subobjId;
	}
	/**
	 * @return the lastChangeDate
	 */
	public Date getLastChangeDate() {
		return lastChangeDate;
	}
	/**
	 * @param lastChangeDate the lastChangeDate to set
	 */
	public void setLastChangeDate(Date lastChangeDate) {
		this.lastChangeDate = lastChangeDate;
	}
	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}
	
	
	
	/**
	 * @param content the content to set
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}
	/**
	 * @return the binaryContentId
	 */
	public Integer getBinaryContentId() {
		return binaryContentId;
	}
	
	
	/**
	 * @param binaryContentId the binaryContentId to set
	 */
	public void setBinaryContentId(Integer binaryContentId) {
		this.binaryContentId = binaryContentId;
	}
	
	
	
	
    
}
