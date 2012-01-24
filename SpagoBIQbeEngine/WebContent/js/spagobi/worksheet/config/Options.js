/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
Ext.ns("Sbi.worksheet.config");

Sbi.worksheet.config.options = {
	'attributes' : 
		[
		 {
			  name : 'attributePresentation'
			, description : 'An attribute can be displayed using its code or its description or both'
			, type : 'radiogroup'
			, applyTo : 'datasetdata'
			, label : LN('sbi.worksheet.config.options.attributepresentation.label')
			, items: 
				[
	                 {boxLabel: LN('sbi.worksheet.config.options.attributepresentation.code'), inputValue: "code", checked: true}
	               , {boxLabel: LN('sbi.worksheet.config.options.attributepresentation.description'), inputValue: "description"}
	               , {boxLabel: LN('sbi.worksheet.config.options.attributepresentation.both'), inputValue: "both"}
			    ]
		 }
		]
	, 'measures' :
		[
		 {
			  name : 'measureScaleFactor'
			, description : 'The scale factor of a measure'
			, type : 'radiogroup'
			, applyTo : 'datasetdata'
			, label : LN('sbi.worksheet.config.options.attributepresentation.label')
			, items: 
				[
	                 {boxLabel: LN('sbi.worksheet.config.options.measurepresentation.NONE'), inputValue: "NONE", checked: true}
	               , {boxLabel: LN('sbi.worksheet.config.options.measurepresentation.K'), inputValue: "K"}
	               , {boxLabel: LN('sbi.worksheet.config.options.measurepresentation.M'), inputValue: "M"}
	               , {boxLabel: LN('sbi.worksheet.config.options.measurepresentation.G'), inputValue: "G"}
			    ]
		 }
		]
};