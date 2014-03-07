/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.tools.scheduler.SchedulerListDetailPanel', {
	extend: 'Sbi.widgets.compositepannel.ListDetailPanel'

		, config: {
			stripeRows: true,
			modelName: "Sbi.tools.scheduler.SchedulerModel",
	        contextName: ''
		}

		, constructor: function(config) {
		
			this.initConfig(config);

			var isSuperadmin= config.isSuperadmin;
						
			var thisPanel = this;
		
			this.services =[];
			//this.initServices();
			this.detailPanel =  Ext.create('Sbi.tools.scheduler.SchedulerDetailPanel',{services: this.services, isSuperadmin: isSuperadmin });
			this.columns = [{dataIndex:"jobName", header:LN('sbi.generic.label')}, {dataIndex:"jobDescription", header:LN('sbi.generic.descr')}];
			this.fields = ["jobName","jobDescription"];
	
			this.filteredProperties = ["jobName","jobDescription"];
			this.buttonToolbarConfig = {
					newButton: true
			};
			this.buttonColumnsConfig ={
					deletebutton:true
			};
			
			 Ext.tip.QuickTipManager.init();
			
			//custom buttons for scheduler operations
			Sbi.widget.grid.StaticGridDecorator.addCustomBottonColumn(this.columns, 'button-detail', 'Detail of Activity',function(grid, rowIndex, colIndex) {
				var record = grid.getStore().getAt(rowIndex);
				var jobName = record.get('jobName');
				var jobGroup = record.get('jobGroup');
				window.location.assign(thisPanel.contextName + '/servlet/AdapterHTTP?JOBGROUPNAME='+jobGroup+'&PAGE=JobManagementPage&TYPE_LIST=TYPE_LIST&MESSAGEDET=MESSAGE_GET_JOB_DETAIL&JOBNAME='+jobName);

			})
			Sbi.widget.grid.StaticGridDecorator.addCustomBottonColumn(this.columns, 'button-schedule', 'Schedule List',function(grid, rowIndex, colIndex) {
				var record = grid.getStore().getAt(rowIndex);
				var jobName = record.get('jobName');
				var jobGroup = record.get('jobGroup');
				window.location.assign(thisPanel.contextName + '/servlet/AdapterHTTP?JOBGROUPNAME='+jobGroup+'&PAGE=TriggerManagementPage&TYPE_LIST=TYPE_LIST&MESSAGEDET=MESSAGE_GET_JOB_SCHEDULES&JOBNAME='+jobName);

			})
		
			this.callParent(arguments);
		}
		
		, onDeleteRow: function(record){
			alert('TODO: Delete schedule activity');
		}
		
		//overwrite parent method
		, onAddNewRow: function(){
			window.location.assign(this.contextName + '/servlet/AdapterHTTP?PAGE=JobManagementPage&TYPE_LIST=TYPE_LIST&MESSAGEDET=MESSAGE_NEW_JOB');
		}
		
});		