/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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

package it.eng.spagobi.engines.documentcomposition.configuration;

/**
 * Contains constant definitions
 */
public class Constants {
	
	// TAGS
	
	public static final String HIERARCHIES_TAG = "HIERARCHIES";
	
	public static final String HIERARCHY_TAG = "HIERARCHY";
	
	public static final String HIERARCHY_LEVEL_TAG = "LEVEL";
	
	public static final String DRILL_TAG = "DRILL";
	
	public static final String PARAM_TAG = "PARAM";
	
	
	// ATTRIBUTES
	public static final String DP_CLASS_NAME_ATTR = "CLASS_NAME";
	
	public static final String DP_CONNECTION_NAME_ATTR = "CONNECTION_NAME";
	
	public static final String DP_QUERY_ATTR = "QUERY";
	
	public static final String DP_COLUMN_ID_ATRR = "COLUMN_ID";
	
	public static final String DP_HIERARCHY_NAME_ATRR = "HIERARCHY_NAME";
	
	public static final String DP_HIERARCHY_BASE_LEVEL_ATRR = "HIERARCHY_BASE_LEVEL";
	
	public static final String DP_HIERARCHY_LEVEL_ATRR = "HIERARCHY_LEVEL";
	
	public static final String DP_KPI_COLUMN_NAMES_ATRR = "COLUMN_VALUES";
	
	public static final String DP_KPI_AGG_FUNCS_ATRR = "AGG_TYPE";
	
	public static final String HIERARCHY_NAME_ATRR = "NAME";
	
	public static final String HIERARCHY_TYPE_ATRR = "TYPE";
	
	public static final String HIERARCHY_TABLE_ATRR = "TABLE_NAME";
	
	public static final String HIERARCHY_LEVEL_NAME_ATRR = "NAME";
	
	public static final String HIERARCHY_LEVEL_COLUMN_ID_ATRR = "COLUMN_ID";
	
	public static final String HIERARCHY_LEVEL_COLUMN_DESC_ATRR = "COLUMN_DESC";
	
	public static final String HIERARCHY_LEVEL_FEATURE_NAME_ATRR = "FEATURE_NAME";
	
	public static final String DRILL_DOCUMENT_ATRR = "DOCUMENT";
	
	public static final String PARAM_TYPE_ATRR = "TYPE";
	
	public static final String PARAM_NAME_ATRR = "NAME";
	
	public static final String PARAM_VALUE_ATRR = "VALUE";
	
	
	

	
	
	
	
	

	public static final String LOG_NAME = "SPAGOBI_GEO_ENGINE";
	
	public static final String TEMPLATE_PARAMETER = "Template";
	
	public static final String OUTPUT_FORMAT_PARAMETER = "OutputFormat";
	
    public static final String CONFIGURATION = "CONFIGURATION";

    public static final String LEGEND = "LEGEND";
    
    public static final String LEVELS = "LEVELS";

    public static final String LEVEL = "LEVEL";

    public static final String NAME = "NAME";

    public static final String MAP = "MAP";
    
    public static final String MAP_NAME = "MAP_NAME";

    public static final String DATAMART_PROVIDER = "DATAMART_PROVIDER";

    public static final String MAP_PROVIDER = "MAP_PROVIDER";
    
    public static final String MAP_RENDERER = "MAP_RENDERER";

    public static final String ENABLED = "ENABLED";

	public static final String X = "x";

	public static final String Y = "y";

	public static final String WIDTH = "width";

	public static final String HEIGHT = "height";

	public static final String THRESHOLD = "threshold";

	public static final String DESCRIPTION = "description";

	public static final String STYLE = "style";

	public static final String TITLE = "TITLE";

	public static final String TEXT = "TEXT";

	public static final String CLASS_NAME = "CLASS_NAME";

	public static final String TYPE = "type";


	// OUTPUT FORMAT TYPES AND MIME TYPES
	
	public static final String SVG = "svg";

	public static final String SVG_MIME_TYPE = "image/svg+xml";

	public static final String JPEG = "jpeg";

	public static final String JPEG_MIME_TYPE = "image/jpeg";

	public static final String PDF = "pdf";
	
	public static final String PDF_MIME_TYPE = "application/pdf";

	public static final String GIF = "gif";
	
	public static final String GIF_MIME_TYPE = "image/gif";

	public static final String BMP = "bmp";
	
	public static final String BMP_MIME_TYPE = "image/bmp";

	public static final String X_PNG = "x-png";
	
	public static final String X_PNG_MIME_TYPE = "image/x-png";

	public static final String XML = "xml";
	
	public static final String XML_MIME_TYPE = "image/xml";

	public static final String HTML = "html";
	
	public static final String HTML_MIME_TYPE = "image/html";
	
	public static final String TEXT_MIME_TYPE = "text/plan";
	
	public static final String TYPE_FILTER = "typeFilter";
	public static final String VALUE_FILTER = "valueFilter";
	public static final String COLUMN_FILTER = "columnFilter";
	public static final String TYPE_VALUE_FILTER = "typeValueFilter";
	public static final String START_FILTER = "start";
	public static final String END_FILTER = "end";
	public static final String EQUAL_FILTER = "equal";
	public static final String CONTAIN_FILTER= "contain";
	public static final String LESS_FILTER= "<";
	public static final String LESS_OR_EQUAL_FILTER= "<=";
	public static final String GREATER_FILTER= ">";
	public static final String GREATER_OR_EQUAL_FILTER= ">=";
	public static final String NUMBER_TYPE_FILTER= "NUM";
	public static final String STRING_TYPE_FILTER= "STRING";
	public static final String DATE_TYPE_FILTER= "DATE";
	public static final String DETAIL_SELECT = "DETAIL_SELECT";
	public static final String ACTION = "ACTION";
	
	//gestione anagrafica
	public static final String DETAIL_NEW = "DETAIL_NEW";
	public static final String DETAIL_MOD = "DETAIL_MOD";
	public static final String DETAIL_INS = "DETAIL_INS";
	public static final String DETAIL_DEL = "DETAIL_DEL";
	public static final String MODALITY = "MODALITY";
}