/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/



Ext.ns("Sbi.settings.top.toolbar");
Ext.ns("Sbi.settings.bottom.toolbar");
Ext.ns("Sbi.settings.toolbar.html");

/**
 * Top custom toolbar settings
 */
Sbi.settings.top.toolbar = {
		buttons: ['documentbrowser',
		          'back',
		          'home', 	
		          'html_telecom_img',    
		          'prec',
		          'params',
		          'spacer',
		          'refresh',
		          'spacer',
		          'html_telecom',
		          'logout'
		          ],
		          execution:['home',
		                     'html_telecom_img',
		                     'prec',
		                     'params',
		                     'refresh',
		                     'spacer',
		                     'html_telecom',
		                     'logout'
		                     ],
		                     main:['documentbrowser',
		                           'spacer',
		                           'html_telecom',
		                           'logout'
		                           ],
		                           login:[],
		                           parameters:[]

};
Sbi.settings.bottom.toolbar = {
		buttons: [
		         'html_telecom_img',
		         'spacer',
		         'navigation',
		         'logout'
		          ],
		          execution:[
		                     'html_telecom_img',
		                     'spacer',
		                     'navigation'
		                     ],
		                     main:[
		                           'html_telecom_img',
		                           'spacer'
		                           ],
		                           login:[],
		                           parameters:[]

};

Sbi.settings.toolbar.html ={
		//code: '<div style="color: violet; border: 1px solid red; background-color: #fff;">Questo &egrave; un html di esempio</div>'
		telecom_img: '<img height="33" src="../img/custom/top.png" alt="Telecom Mobile" />',
		telecom: 'SpagoBI Mobile'
}