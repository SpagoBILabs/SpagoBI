Ext.define('Sbi.behavioural.analyticalDriver.AnalyticalDriverUseModel',{

	extend: 'Ext.data.Model',
	fields: [
	         "ID",
	         "USEID",
	         "LABEL",
	         "DESCRIPTION",
	         "NAME",
	         {name: 'LOVID',type: 'string'},
	         {name: 'SELECTIONTYPE',type: 'string'},
	         "MANUALINPUT",
	         "EXPENDABLE",
	         {name: 'DEFAULTFORMULA',type: 'string'},
	         {name: 'DEFAULTLOVID',type: 'string'},
	         "ROLESLIST",
	         "CONSTLIST"

	         ],

	         idProperty: "USEID",

	         proxy:{

	        	 type: 'rest',
	        	 url : Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'analyticalDriverUse'}),
	        	 appendId: false,
	        	 reader: {

	        		 type: "json",
	        		 root: "ADUSE"

	        	 }
	         }

});