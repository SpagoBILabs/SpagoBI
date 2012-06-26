/* Copyright 2012 Engineering Ingegneria Informatica S.p.A. – SpagoBI Competency Center
 * The original code of this file is part of Spago java framework, Copyright 2004-2007.

 * This Source Code Form is subject to the term of the Mozilla Public Licence, v. 2.0. If a copy of the MPL was not distributed with this file, 
 * you can obtain one at http://Mozilla.org/MPL/2.0/.

 * Alternatively, the contents of this file may be used under the terms of the LGPL License (the “GNU Lesser General Public License”), in which 
 * case the  provisions of LGPL are applicable instead of those above. If you wish to  allow use of your version of this file only under the 
 * terms of the LGPL  License and not to allow others to use your version of this file under  the MPL, indicate your decision by deleting the 
 * provisions above and  replace them with the notice and other provisions required by the LGPL.  If you do not delete the provisions above, 
 * a recipient may use your version  of this file under either the MPL or the GNU Lesser General Public License. 

 * Spago is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or any later version. Spago is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License 
 * for more details.
 * You should have received a copy of the GNU Lesser General Public License along with Spago. If not, see: http://www.gnu.org/licenses/. The complete text of 
 * Spago license is included in the  COPYING.LESSER file of Spago java framework.
 */

package it.eng.spagobi.engines.geo;

import it.eng.spagobi.engines.geo.datamart.provider.DataMartProvider;
import it.eng.spagobi.engines.geo.map.provider.SOMapProvider;
import it.eng.spagobi.engines.geo.map.renderer.InteractiveMapRenderer;

// TODO: Auto-generated Javadoc
/**
 * Contains constant definitions.
 */
public class GeoEngineConstants {
	
	
	// DEFAULTS
	
	public static final String DEFAULT_DATAMART_PROVIDER = DataMartProvider.class.getName();
	public static final String DEFAULT_MAP_PROVIDER = SOMapProvider.class.getName();
	public static final String DEFAULT_MAP_RENDERER = InteractiveMapRenderer.class.getName();
	
	// TAGS	
	/** The Constant MAP_PROVIDER_TAG. */
	public static final String MAP_PROVIDER_TAG = "MAP_PROVIDER";
	
	/** The Constant DATAMART_PROVIDER_TAG. */
	public static final String DATAMART_PROVIDER_TAG = "DATAMART_PROVIDER";
	
	/** The Constant MAP_RENDERER_TAG. */
	public static final String MAP_RENDERER_TAG = "MAP_RENDERER";
	
	/** The Constant METADATA_TAG. */
	public static final String METADATA_TAG = "METADATA";
	
	/** The Constant COLUMN_TAG. */
	public static final String COLUMN_TAG = "COLUMN";
	
	/** The Constant HIERARCHIES_TAG. */
	public static final String HIERARCHIES_TAG = "HIERARCHIES";
	
	/** The Constant HIERARCHY_TAG. */
	public static final String HIERARCHY_TAG = "HIERARCHY";
	
	/** The Constant HIERARCHY_LEVEL_TAG. */
	public static final String HIERARCHY_LEVEL_TAG = "LEVEL";
	
	/** The Constant DATASET_TAG. */
	public static final String DATASET_TAG = "DATASET";
	
	/** The Constant DATASOURCE_TAG. */
	public static final String DATASOURCE_TAG = "DATASOURCE";
	
	/** The Constant QUERY_TAG. */
	public static final String QUERY_TAG = "QUERY";
	
	/** The Constant MAP_NAME_TAG. */
	public static final String MAP_NAME_TAG = "MAP_NAME";
	
		
	
	// ATTRIBUTES
	
	/** The Constant CLASS_NAME_ATTRIBUTE. */
	public static final String CLASS_NAME_ATTRIBUTE = "CLASS_NAME";
	
	/** The Constant COLUMN_NAME_ATTRIBUTE. */
	public static final String COLUMN_NAME_ATTRIBUTE = "COLUMN_ID";
	
	/** The Constant COLUMN_TYPE_ATTRIBUTE. */
	public static final String COLUMN_TYPE_ATTRIBUTE = "TYPE";
	
	/** The Constant COLUMN_HIERARCHY_REF_ATTRIBUTE. */
	public static final String COLUMN_HIERARCHY_REF_ATTRIBUTE = "HIERARCHY";
	
	/** The Constant COLUMN_LEVEL_REF_ATTRIBUTE. */
	public static final String COLUMN_LEVEL_REF_ATTRIBUTE = "LEVEL";
	
	/** The Constant COLUMN_AFUNC_REF_ATTRIBUTE. */
	public static final String COLUMN_AFUNC_REF_ATTRIBUTE = "AGG_FUNC";
	
	/** The Constant HIERARCHY_NAME_ATTRIBUTE. */
	public static final String HIERARCHY_NAME_ATTRIBUTE = "NAME";
	
	/** The Constant HIERARCHY_TYPE_ATTRIBUTE. */
	public static final String HIERARCHY_TYPE_ATTRIBUTE = "TYPE";
	
	/** The Constant HIERARCHY_TABLE_ATRRIBUTE. */
	public static final String HIERARCHY_TABLE_ATRRIBUTE = "TABLE_NAME";
	
	/** The Constant HIERARCHY_LEVEL_NAME_ATRRIBUTE. */
	public static final String HIERARCHY_LEVEL_NAME_ATRRIBUTE = "NAME";
	
	/** The Constant HIERARCHY_LEVEL_COLUMN_ID_ATRRIBUTE. */
	public static final String HIERARCHY_LEVEL_COLUMN_ID_ATRRIBUTE = "COLUMN_ID";
	
	/** The Constant HIERARCHY_LEVEL_COLUMN_DESC_ATRRIBUTE. */
	public static final String HIERARCHY_LEVEL_COLUMN_DESC_ATRRIBUTE = "COLUMN_DESC";
	
	/** The Constant HIERARCHY_LEVEL_FEATURE_NAME_ATRRIBUTE. */
	public static final String HIERARCHY_LEVEL_FEATURE_NAME_ATRRIBUTE = "FEATURE_NAME";
	
	/** The Constant DATASET_TYPE_ATTRIBUTE. */
	public static final String DATASET_TYPE_ATTRIBUTE = "TYPE";
	
	/** The Constant DATASET_NAME_ATTRIBUTE. */
	public static final String DATASET_NAME_ATTRIBUTE = "NAME";
	
	/** The Constant DATASET_DRIVER_ATTRIBUTER. */
	public static final String DATASET_DRIVER_ATTRIBUTER = "DRIVER";
	
	/** The Constant DATASET_PWD_ATTRIBUTE. */
	public static final String DATASET_PWD_ATTRIBUTE = "PASSWORD";
	
	/** The Constant DATASET_USER_ATTRIBUTE. */
	public static final String DATASET_USER_ATTRIBUTE = "USER";
	
	/** The Constant DATASET_URL_ATTRIBUTE. */
	public static final String DATASET_URL_ATTRIBUTE = "URL";
	
	// ENV PROPERTIES
	
	
	/** The Constant ENV_MAPCATALOGUE_SERVICE_PROXY. */
	public static final String ENV_MAPCATALOGUE_SERVICE_PROXY = "MAPCATALOGUE_SERVICE_PROXY";
	
	/** The Constant ENV_CONTEXT_URL. */
	public static final String ENV_CONTEXT_URL = "CONTEXT_URL";
	
	/** The Constant ENV_ABSOLUTE_CONTEXT_URL. */
	public static final String ENV_ABSOLUTE_CONTEXT_URL = "ABSOLUTE_CONTEXT_URL";
	
	/** The Constant ENV_STD_HIERARCHY. */
	public static final String ENV_STD_HIERARCHY = "STD_HIERARCHY";
	
	/** The Constant ENV_IS_DAFAULT_DRILL_NAV. */
	public static final String ENV_IS_DAFAULT_DRILL_NAV = "ENV_IS_DAFAULT_DRILL_NAV";
	
	/** The Constant ENV_IS_WINDOWS_ACTIVE. */
	public static final String ENV_IS_WINDOWS_ACTIVE = "ENV_IS_WINDOWS_ACTIVE";
	
	/** The Constant ENV_EXEC_IFRAME_ID. */
	public static final String ENV_EXEC_IFRAME_ID = "ENV_EXEC_IFRAME_ID";
	
	public static final String ENV_OUTPUT_TYPE = "OUTPUT_TYPE";
	
	
	
	
	// OUTPUT FORMAT TYPES AND MIME TYPES
	
	/** The Constant XDSVG. */
	public static final String XDSVG = "xdsvg";
	
	/** The Constant XDSVG_MIME_TYPE. */
	public static final String XDSVG_MIME_TYPE = "image/svg+xml";
	
	/** The Constant DSVG. */
	public static final String DSVG = "dsvg";
	
	/** The Constant DSVG_MIME_TYPE. */
	public static final String DSVG_MIME_TYPE = "image/svg+xml";
	
	/** The Constant SVG. */
	public static final String SVG = "svg";
	
	/** The Constant SVG_MIME_TYPE. */
	public static final String SVG_MIME_TYPE = "image/svg+xml";

	/** The Constant JPEG. */
	public static final String JPEG = "jpeg";
	
	/** The Constant JPEG_MIME_TYPE. */
	public static final String JPEG_MIME_TYPE = "image/jpeg";

	/** The Constant PDF. */
	public static final String PDF = "pdf";	
	
	/** The Constant PDF_MIME_TYPE. */
	public static final String PDF_MIME_TYPE = "application/pdf";

	/** The Constant GIF. */
	public static final String GIF = "gif";	
	
	/** The Constant GIF_MIME_TYPE. */
	public static final String GIF_MIME_TYPE = "image/gif";

	/** The Constant BMP. */
	public static final String BMP = "bmp";	
	
	/** The Constant BMP_MIME_TYPE. */
	public static final String BMP_MIME_TYPE = "image/bmp";

	/** The Constant X_PNG. */
	public static final String X_PNG = "x-png";	
	
	/** The Constant X_PNG_MIME_TYPE. */
	public static final String X_PNG_MIME_TYPE = "image/x-png";

	/** The Constant XML. */
	public static final String XML = "xml";	
	
	/** The Constant XML_MIME_TYPE. */
	public static final String XML_MIME_TYPE = "image/xml";

	/** The Constant HTML. */
	public static final String HTML = "html";	
	
	/** The Constant HTML_MIME_TYPE. */
	public static final String HTML_MIME_TYPE = "image/html";
	
	/** The Constant TEXT_MIME_TYPE. */
	public static final String TEXT_MIME_TYPE = "text/plan";


	
	
		
}