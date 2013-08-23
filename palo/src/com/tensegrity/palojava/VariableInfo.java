/*
*
* @file VariableInfo.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author PhilippBouillon
*
* @version $Id: VariableInfo.java,v 1.3 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava;

/**
 * 
 * @author PhilippBouillon
 * @deprecated Do not use. Interface is subject to change.
 */
public interface VariableInfo extends PaloInfo {
	public final static int VAR_TYPE_UNKNOWN   = 0;
	public final static int VAR_TYPE_MEMBER    = 1;
	public final static int VAR_TYPE_NUMERIC   = 2;
	public final static int VAR_TYPE_HIERARCHY = 3;
	
	public final static int VAR_PROC_TYPE_UNKNOWN    = 0;
	public final static int VAR_PROC_TYPE_USER_INPUT = 1;
	
	public final static int VAR_SELECTION_TYPE_UNKNOWN  = 0;
	public final static int VAR_SELECTION_TYPE_VALUE    = 1;
	public final static int VAR_SELECTION_TYPE_INTERVAL = 2;
	public final static int VAR_SELECTION_TYPE_COMPLEX  = 3;
		
	public final static int VAR_INPUT_TYPE_OPTIONAL              = 0;
	public final static int VAR_INPUT_TYPE_MANDATORY             = 1;
	public final static int VAR_INPUT_TYPE_MANDATORY_NOT_INITIAL = 2;
	public final static int VAR_INPUT_TYPE_UNKNOWN               = 3;

	String getName();
	DimensionInfo getElementDimension();
	int getSelectionType();
	int getInputType();
	String getDataType();	
	void setValue(ElementInfo element);
	void setValue(String elementId);
	void setInterval(ElementInfo from, ElementInfo to);
	void setInterval(String fromId, String toId);
	String getText();
	void setText(String newText);
	ElementInfo getValue();
	ElementInfo [] getInterval();
	ElementInfo [] getSelectedElements();
	void setSelectedElements(String [] elementIds);
}
