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
	//public static final String OPTIONAL_FILTERS = "optionalfilters";
	public static final String FILTERS = "FILTERS";
	public static final String OPTIONAL_VISIBLE_COLUMNS = "visibleselectfields";
	
}
