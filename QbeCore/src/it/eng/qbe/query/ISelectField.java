/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.query;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface ISelectField {
	public final static String DATAMART_FIELD = "datamartField";
	public final static String CALCULATED_FIELD = "calculatedField";
	public final static String IN_LINE_CALCULATED_FIELD = "inLineCalculatedField";
	
	String getAlias();	
	void setAlias(String alias);
	
	String getType();	
	void setType(String type);	
	boolean isDataMartField();
	boolean isInLineCalculatedField();
	boolean isCalculatedField();
	
	boolean isVisible() ;
	void setVisible(boolean visible);

	boolean isIncluded();
	void setIncluded(boolean include) ;
	
	ISelectField copy();
}
