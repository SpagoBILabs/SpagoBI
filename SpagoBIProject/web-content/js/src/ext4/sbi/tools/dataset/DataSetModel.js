/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 Ext.define('Sbi.tools.dataset.DataSetModel', {
    extend: 'Ext.data.Model',
    fields: [
             "DS_ID",
             "VERSION_NUM",
             "ACTIVE",
             "LABEL",
             "NAME",
             "DESCR", 
             "OBJECT_TYPE", 
             "DS_METADATA",
             "PARAMS", 
             "CATEGORY_ID",
             "TRANSFORMER_ID",
             "PIVOT_COLUMN", 
             "PIVOT_ROW", 
             "PIVOT_VALUE",
             "NUM_ROWS",
             "IS_PERSISTED",
             "DATA_SOURCE_PERSIST_ID",
             "IS_FLAT_DATASET",
             "FLAT_TABLE_NAME",
             "DATA_SOURCE_FLAT_ID",
             "CONFIGURATION",
             "USER"
             ]
});