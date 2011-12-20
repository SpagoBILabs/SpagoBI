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
