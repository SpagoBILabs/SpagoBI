/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/

package it.eng.spagobi.commons;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 */
public class QbeEngineStaticVariables {
	
	
	// qbeEngineAnalysisState
	public static final String CATALOGUE = "CATALOGUE";
	public static final String WORKSHEET_DEFINITION = "WORKSHEET_DEFINITION";
	public static final String WORKSHEET_DEFINITION_LOWER = "worksheetdefinition";
	public static final String DATASOURCE = "DATAMART_MODEL";
	public static final String CURRENT_QUERY_VERSION = "7";
	
	
	//SaveAnalysisStateAction
	public static final String CATALOGUE_NAME = "name";	
	public static final String CATALOGUE_DESCRIPTION = "description";
	public static final String CATALOGUE_SCOPE = "scope";
	
	//LoadCrosstabAction
	public static final String CROSSTAB_DEFINITION = "crosstabDefinition";
	public static final String OPTIONAL_FILTERS = "optionalfilters";
	public static final String OPTIONAL_VISIBLE_COLUMNS = "visibleselectfields";
	
}
