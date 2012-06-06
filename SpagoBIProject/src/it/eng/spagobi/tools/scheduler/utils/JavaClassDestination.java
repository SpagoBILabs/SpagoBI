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

package it.eng.spagobi.tools.scheduler.utils;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;

public abstract class JavaClassDestination implements IJavaClassDestination {

	BIObject biObj=null;
	byte[] documentByte=null;
	
	public abstract void execute();
	
	public byte[] getDocumentByte() {
		return documentByte;
	}

	public void setDocumentByte(byte[] documentByte) {
		this.documentByte = documentByte;
	}

	public BIObject getBiObj() {
		return biObj;
	}

	public void setBiObj(BIObject biObj) {
		this.biObj = biObj;
	}
}
