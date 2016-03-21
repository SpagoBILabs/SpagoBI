/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Danilo Ristovski (danilo.ristovski@mht.net)
 */

Ext.define
(
		"Sbi.behavioural.lov.LOVJavaClassBottomPanel", 
		
		{
			create: function()
			{		
				Sbi.debug("[IN] Creating LOVJavaClassBottomPanel");
				
				this.javaClassName = Ext.create
				(
					"Ext.form.field.Text",
					
					{
						fieldLabel: LN('sbi.behavioural.lov.details.javaClassName'), 
						name: 'JAVA_CLASS_NAME',
						id: "JAVA_CLASS_NAME",
						width: 500,
						padding: '10 0 10 0',
						allowBlank: true					
						//name: "LOV_LABEL"		    		
					}
				);	
				
				Sbi.debug("[OUT] Creating LOVJavaClassBottomPanel");
			}			
		}
);