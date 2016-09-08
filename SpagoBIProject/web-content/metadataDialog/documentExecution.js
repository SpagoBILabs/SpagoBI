(function() {

	

	var stringStartsWith = function (string, prefix) {
		return string.toLowerCase().slice(0, prefix.length) == prefix.toLowerCase();
	};

	var documentExecutionApp = angular.module('documentExecutionModule',['ngMaterial', 'ui.tree', 'sbiModule', 'document_tree', 'componentTreeModule', 'angular_table', 'ngSanitize', 'expander-box', 'ngAnimate', 'ngWYSIWYG','angular_list','file_upload']);
	
	documentExecutionApp.config(['$mdThemingProvider', function($mdThemingProvider) {
		$mdThemingProvider.theme('knowage')
		$mdThemingProvider.setDefaultTheme('knowage');
	}]);

	
	
	angular.module('documentExecutionModule').service('multipartForm',['$http',function($http){
		
		this.put = function(uploadUrl,data){
			
			var formData = new FormData();
			
			formData.append("file",data.file);

			return	$http.put(uploadUrl,formData,{
					transformRequest:angular.identity,
					headers:{'Content-Type': undefined}
				})
		}
		
	}]);
	
	
	
	
//	documentExecutionApp.controller( 'documentExecutionController', ['$scope', '$http', '$mdSidenav', '$mdDialog', '$mdToast',  documentExecutionControllerFn]);
//
//	function documentExecutionControllerFn($scope, $http, $mdSidenav, $mdDialog,$mdToast) 
//	{



////		documentExecutionApp.controller( 'documentExecutionController', 
////				['$scope', '$http', '$mdSidenav', '$mdDialog', '$mdToast', 'sbiModule_translate', 'sbiModule_restServices', 'sbiModule_user', 
////				 'sbiModule_config', 'sbiModule_messaging', 'execProperties', 'documentExecuteFactories', 'sbiModule_helpOnLine',
////				 'documentExecuteServices', 'docExecute_urlViewPointService', 'docExecute_paramRolePanelService', 'infoMetadataService', 'sbiModule_download', '$crossNavigationScope',
////				 'docExecute_dependencyService', '$timeout', 'docExecute_exportService', '$filter', 'sbiModule_dateServices', 'cockpitEditing',
////				 documentExecutionControllerFn]);

////		function documentExecutionControllerFn(
////				$scope, $http, $mdSidenav, $mdDialog,$mdToast, sbiModule_translate, sbiModule_restServices,sbiModule_user, sbiModule_config,
////				sbiModule_messaging, execProperties, documentExecuteFactories, sbiModule_helpOnLine, documentExecuteServices,
////				docExecute_urlViewPointService, docExecute_paramRolePanelService, infoMetadataService, sbiModule_download, $crossNavigationScope,
////				docExecute_dependencyService, $timeout, docExecute_exportService, $filter, sbiModule_dateServices, cockpitEditing) {
	
	
documentExecutionApp.controller( 'documentExecutionController', 
['$scope', '$http', '$mdSidenav', '$mdDialog', '$mdToast','sbiModule_translate','sbiModule_restServices','sbiModule_download','multipartForm',  documentExecutionControllerFn]);

function documentExecutionControllerFn(
$scope, $http, $mdSidenav, $mdDialog,$mdToast,sbiModule_translate,sbiModule_restServices,sbiModule_download,multipartForm) {

			console.log("documentExecutionControllerFn IN ");
			
					
			$scope.test="test";
				
			
			var stringStartsWith = function (string, prefix) {
				return string.toLowerCase().slice(0, prefix.length) == prefix.toLowerCase();
			};

			var documentExecutionApp = angular.module('documentExecutionModule');
			
			documentExecutionApp.config(['$mdThemingProvider', function($mdThemingProvider) {
				$mdThemingProvider.theme('knowage')
				$mdThemingProvider.setDefaultTheme('knowage');
			}]);
			
			
			
			
			
			//--start controller Ex InfoMetadataService--
			
			
			
    		var metadataDlgCtrl = $scope;
    		var lblTitle = sbiModule_translate.load('sbi.execution.executionpage.toolbar.metadata');
    		var lblCancel = sbiModule_translate.load('sbi.general.cancel');
    		var lblClose = sbiModule_translate.load('sbi.general.close');
    		var lblSave = sbiModule_translate.load('sbi.generic.update');
    		var lblGeneralMeta = sbiModule_translate.load('sbi.execution.metadata.generalmetadata');
    		var lblShortMeta = sbiModule_translate.load('sbi.execution.metadata.shorttextmetadata');
    		var lblLongMeta = sbiModule_translate.load('sbi.execution.metadata.longtextmetadata');
    		var lblAttachments = sbiModule_translate.load('sbi.execution.metadata.attachments');
    		
    		metadataDlgCtrl.selectedTab={'tab':0};
    		metadataDlgCtrl.lblTitle = lblTitle;
    		metadataDlgCtrl.lblCancel = lblCancel;
    		metadataDlgCtrl.lblClose = lblClose;
    		metadataDlgCtrl.lblSave = lblSave;
    		metadataDlgCtrl.lblGeneralMeta = lblGeneralMeta;
    		metadataDlgCtrl.lblShortMeta = lblShortMeta;
    		metadataDlgCtrl.lblLongMeta = lblLongMeta;
    		metadataDlgCtrl.lblAttachments = lblAttachments;
    		
    		metadataDlgCtrl.generalMetadata = [];
    		metadataDlgCtrl.shortText = [];
    		metadataDlgCtrl.longText = [];
    		metadataDlgCtrl.file = [];
    		metadataDlgCtrl.importedFile={}; 
    		    		
    		
    		metadataDlgCtrl.linkToHiddenInputTypeFile=function () {		    			
	  			  var input   = document.getElementById('fileInput')
	  			  var button = document.getElementById('uploadButton');
	  			  button.click(input.click()); 	  
    		}
    		 
    		metadataDlgCtrl.uploadFile=function (fileToSave) {		  
    			if(fileToSave.file!=undefined && fileToSave.file!="" && fileToSave.file!=null)
    			{	
    			
					//Upload file to local directory
					multipartForm.put("/SpagoBI/restful-services/1.0/documentexecution/"+"upload",fileToSave).success(   
							
							function(data,status,headers,config){
								if(data.hasOwnProperty("errors")){						
									console.log("[UPLOAD]: DATA HAS ERRORS PROPERTY!");	
				    				
									 $mdDialog.show(
											 $mdDialog.alert()
											 	.parent(angular.element(document.querySelector('#popupContainer')))
											 	.clickOutsideToClose(true)
											 	.title('Error during uploading')
											 	.ok('OK')
										    	);
									
									
									

								}else{
				    				//documentExecuteServices.showToast("Upload successfull", 3);  
									
									 $mdDialog.show(
											 $mdDialog.alert()
											 	.parent(angular.element(document.querySelector('#popupContainer')))
											 	.clickOutsideToClose(true)
											 	.title('Upload successfull')
											 	.ok('OK')
										    	);
									
									console.log("[UPLOAD]: SUCCESS!");
								}

							}).error(function(data, status, headers, config) {
								console.log("[UPLOAD]: FAIL!"+status);
			    				//documentExecuteServices.showToast("Upload error", 3);  
								 $mdDialog.show(
										 $mdDialog.alert()
										 	.parent(angular.element(document.querySelector('#popupContainer')))
										 	.clickOutsideToClose(true)
										 	.title('Upload error')
										 	.ok('OK')
									    	);
								
								
							});
    			}
    			else
    			{
    				//documentExecuteServices.showToast("Select a file to Upload!", 3); 
					 $mdDialog.show(
							 $mdDialog.alert()
							 	.parent(angular.element(document.querySelector('#popupContainer')))
							 	.clickOutsideToClose(true)
							 	.title("Select a file to Upload!")
							 	.ok('OK')
						    	);
    				
					console.log("[UPLOAD]: SELECT A FILE TO UPLOAD!");
    			}	
    			
    		}
    		
    		var params = null;
    		
    		metadataDlgCtrl.setTab = function(Tab){
    			metadataDlgCtrl.selectedTab.tab = Tab;
    		}
    		metadataDlgCtrl.isSelectedTab = function(Tab){
    			return (Tab == metadataDlgCtrl.selectedTab.tab) ;
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		metadataDlgCtrl.getDocumentMetadataFunction=function(){	
	    		sbiModule_restServices.promiseGet('1.0/documentexecution/' + objectId, 'documentMetadata', params)
	    		.then(function(response){
	    			metadataDlgCtrl.generalMetadata = response.data.GENERAL_META;
	    			metadataDlgCtrl.shortText = response.data.SHORT_TEXT;
		    		metadataDlgCtrl.longText = response.data.LONG_TEXT;
		    		metadataDlgCtrl.file = response.data.FILE;
		    		console.log("RECEIVED FILES: ",metadataDlgCtrl.file)
		    		for(var i=0;i<metadataDlgCtrl.file.length;i++) 
		    		{
		    			if(metadataDlgCtrl.file[i].fileToSave==undefined || metadataDlgCtrl.file[i].fileToSave==null||metadataDlgCtrl.file[i].fileToSave=='')
		    			{	
		    				metadataDlgCtrl.file[i].fileToSave={};	// fileToUpload instead of uploadedFile
		    			}
		    			//When there are saved files make its fileNames, saveDates and fileLabel visible for user
		    			if(metadataDlgCtrl.file[i].savedFile!=undefined && metadataDlgCtrl.file[i].savedFile!=null && metadataDlgCtrl.file[i].savedFile!='')
		    			{	
			    			var fileItem=JSON.parse(metadataDlgCtrl.file[i].savedFile);
			    			metadataDlgCtrl.file[i].fileName=fileItem.fileName;
			    			metadataDlgCtrl.file[i].saveDate=fileItem.saveDate;
		    			}
		    		}
		    		
		    		
		    		if(metadataDlgCtrl.shortText.length>0)
		    		{
		    			for(var j=0;j<metadataDlgCtrl.shortText.length;j++)
		    			{
		    				if(metadataDlgCtrl.shortText[j].value!="" && metadataDlgCtrl.shortText[j].value!=null && metadataDlgCtrl.shortText[j].value!=undefined)
		    				{
		    		    		metadataDlgCtrl.shortExpanderOpened = true;
		    				}	
		    			}	
		    		}
		    		if(metadataDlgCtrl.longText.length>0)
		    		{
		    			for(var j=0;j<metadataDlgCtrl.longText.length;j++)
		    			{
		    				if(metadataDlgCtrl.longText[j].value!="" && metadataDlgCtrl.longText[j].value!=null && metadataDlgCtrl.longText[j].value!=undefined)
		    				{
		    		    		metadataDlgCtrl.longExpanderOpened = true;
		    				}	
		    			}
		    		}	
		    		if(metadataDlgCtrl.file.length>0)
		    		{	
		    			for(var j=0;j<metadataDlgCtrl.file.length;j++)
		    			{
		    				if(metadataDlgCtrl.file[j].savedFile!="" && metadataDlgCtrl.file[j].savedFile!=null && metadataDlgCtrl.file[j].savedFile!=undefined && metadataDlgCtrl.file[j].savedFile!="{}")
		    				{
		    		    		metadataDlgCtrl.fileExpanderOpened = true;
		    				}	
		    			}
		    			
		    		}	
		    		
		    		
		    		
	    		},function(response){
	    			//documentExecuteServices.showToast(response.data.errors[0].message, 5000);
	    			
					$mdDialog.show(
							 $mdDialog.alert()
							 	.parent(angular.element(document.querySelector('#popupContainer')))
							 	.clickOutsideToClose(true)
							 	.title(response.data.errors[0].message)
							 	.ok('OK')
					);
	    			
	    			
	    			
	    		});
    		};	
    		
    		
    		
    		metadataDlgCtrl.save = function(){

    			if(metadataDlgCtrl.shortText==null || metadataDlgCtrl.shortText==undefined || metadataDlgCtrl.shortText=='')
    			{ 
    				metadataDlgCtrl.shortText=[];
    			}	
    			if(metadataDlgCtrl.longText==null || metadataDlgCtrl.longText==undefined || metadataDlgCtrl.longText=='')
    			{ 
    				metadataDlgCtrl.longText=[];
    			}	
    			var saveObj = {
    				id:objectId,
    				subobjectId: null, 
    				jsonMeta: metadataDlgCtrl.shortText.concat(metadataDlgCtrl.longText).concat(metadataDlgCtrl.file) //added last concat
    			};
    			sbiModule_restServices.promisePut('1.0/documentexecution', 'saveDocumentMetadata', saveObj)
    			.then(function(response){
    				//documentExecuteServices.showToast(sbiModule_translate.load("sbi.execution.viewpoints.msg.saved"), 3000);
    				//documentExecuteServices.showToast("Salvataggio OK", 3);
					$mdDialog.show(
							 $mdDialog.alert()
							 	.parent(angular.element(document.querySelector('#popupContainer')))
							 	.clickOutsideToClose(true)
							 	.title("Save operation successfull")
							 	.ok('OK')
					);
    				
    				    				
    				metadataDlgCtrl.getDocumentMetadataFunction(); 

    				//metadataDlgCtrl.setTab(0); //Added

    			},function(response){
    				//documentExecuteServices.showToast(response.data.errors[0].message, 5);
					 $mdDialog.alert()
					 	.parent(angular.element(document.querySelector('#popupContainer')))
					 	.clickOutsideToClose(true)
					 	.title(response.data.errors[0].message)
					 	.ok('OK')
    			});

    		}
    		
    		metadataDlgCtrl.cleanFile= function(metadataId){
    			//remove file from temp directory (if present) and from db
    			
    			objId=objectId;
    			var subobjId="null";
//	    		if(executionInstance.SUBOBJECT_ID){ 			//TODO: bring subobjectId management logic from knowage to SpagoBI 5.1
//	    			subobjId=executionInstance.SUBOBJECT_ID;
//	    		}

	    				    		
	    		sbiModule_restServices.promiseGet('1.0/documentexecution/'+objId+"/"+metadataId, 'deletefilemetadata').then(function(response){
    				//documentExecuteServices.showToast(sbiModule_translate.load("sbi.execution.viewpoints.msg.saved"), 3000);
    				//documentExecuteServices.showToast("Rimozione effettuata", 3);
					$mdDialog.show(
							 $mdDialog.alert()
							 	.parent(angular.element(document.querySelector('#popupContainer')))
							 	.clickOutsideToClose(true)
							 	.title("Item successfully deleted")
							 	.ok('OK')
					);
	    			
    				metadataDlgCtrl.getDocumentMetadataFunction(); 
    					
    			},function(response){
    				//documentExecuteServices.showToast(response.data.errors[0].message, 5);
					 $mdDialog.alert()
					 	.parent(angular.element(document.querySelector('#popupContainer')))
					 	.clickOutsideToClose(true)
					 	.title(response.data.errors[0].message)
					 	.ok('OK')
    			});
	    		
	    		
    		}
    		
    		metadataDlgCtrl.download = function(metadataId,savedFile){
    			if(savedFile!="" && savedFile!=null && savedFile!=undefined)
    			{	
	    			objId=objectId;
	    			var subobjId="null";
//		    		if(executionInstance.SUBOBJECT_ID){ 		TODO: bring subobjid logic from knowage to spagobi 5.1
//		    			subobjId=executionInstance.SUBOBJECT_ID;
//		    		}

		    				    		
		    		sbiModule_download.getLink('/restful-services/1.0/documentexecution/'+objId+"/"+metadataId+"/"+"documentfilemetadata"); 	
    			}
    			else
    			{
    				//documentExecuteServices.showToast("No saved file to download!", 3);  
					$mdDialog.show(
							 $mdDialog.alert()
							 	.parent(angular.element(document.querySelector('#popupContainer')))
							 	.clickOutsideToClose(true)
							 	.title("No saved file to download!")
							 	.ok('OK')
					);
    				
					console.log("[DOWNLOAD]: NO FILE TO DOWNLOAD!");
    			}	
    		}
    		
    		
    		
    		
    		
    		
    		
    		metadataDlgCtrl.getDocumentMetadataFunction();
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
};

		documentExecutionApp.directive('iframeSetDimensionsOnload', ['docExecute_urlViewPointService',function(docExecute_urlViewPointService) {
			return {
				scope: {
					iframeOnload: '&?'
				},
				restrict: 'A',
				link: function(scope, element, attrs) {
					element.on('load', function() {
						var iFrameHeight = element[0].parentElement.scrollHeight + 'px';
						element.css('height', iFrameHeight);				
						element.css('width', '100%');
						if(scope.iframeOnload)
							scope.iframeOnload();
					});
				}
			};
		}]);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//	};
})()