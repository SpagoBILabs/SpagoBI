<%@include file="/WEB-INF/jsp/commons/angular/angularResource.jspf"%>
<%@include file="/WEB-INF/jsp/commons/angular/angularImport.jsp"%>

<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.BIObject"%>
<%@page import="it.eng.spagobi.commons.utilities.ObjectsAccessVerifier"%>
<%@page import="it.eng.spagobi.engines.config.bo.Engine"%>
<%@page import="it.eng.spagobi.utilities.engines.rest.ExecutionSession"%>



<%
/*
	These two variables are needed for checking if the "Add to workspace" should be available for the current user. This option is available when 
	the document is executed and it serves to add link to that particular document in the Organizer (Documents view) in the Workspace (for that 
	particular user). Variables are at disposal for using for other purposes as well.
	@author Danilo Ristovski (danristo, danilo.ristovski@mht.net)
*/
boolean isAdmin = UserUtilities.isAdministrator(userProfile);
boolean isSuperAdmin = (Boolean)((UserProfile)userProfile).getIsSuperadmin();
BIObject obj = (BIObject) aServiceResponse.getAttribute(SpagoBIConstants.OBJECT);
			
						
			
%>














<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
		
		<!-- Styles -->
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/themes/commons/css/customStyle.css"> 
		<script type="text/javascript" src="<%=urlBuilder.getResourceLink(request, "js/src/angular_1.4/tools/commons/component-tree/componentTree.js")%>"></script>
		<script type="text/javascript" src="<%=urlBuilder.getResourceLink(request, "js/src/angular_1.4/tools/commons/document-tree/DocumentTree.js")%>"></script>
		<script type="text/javascript" src="<%=urlBuilder.getResourceLink(request, "js/lib/angular/ngWYSIWYG/wysiwyg.min.js")%>"></script>	
		<link rel="stylesheet" type="text/css" href="<%=urlBuilder.getResourceLink(request, "js/lib/angular/ngWYSIWYG/editor.min.css")%>">
		
		
		<script type="text/javascript">
			var objectId=<%=obj.getId()%>;
		</script>
		

		<script type="text/javascript" 
				src="<%=urlBuilder.getResourceLink(request, "metadataDialog/documentExecution.js")%>"></script>
		<script type="text/javascript" 
				src="<%=urlBuilder.getResourceLink(request, "metadataDialog/documentExecutionServices.js")%>"></script>		
		
		
	</head>
	<body ng-app="documentExecutionModule" ng-controller="documentExecutionController">
		<div id="metadataDlg" aria-label="{{lblTitle}}" layout="column" flex class="metadataDialog">
								
				
		<!--  <md-dialog>-->
		
		<% boolean canModify = false;
		  if (userProfile.isAbleToExecuteAction(SpagoBIConstants.SAVE_METADATA_FUNCTIONALITY)) {
		 			canModify = true;
		  
		  } %>
		
	
		<md-toolbar >
			<div class="md-toolbar-tools">
		    	<h2>{{lblTitle}}</h2>
	       	</div>
	  	</md-toolbar>
 		<div class="md-dialog-content">
 		
 		
 		
			<expander-box id="generalMetadata" expanded="true" title="lblGeneralMeta" toolbar-class="secondaryToolbar">
				<md-list flex>
		     		<md-list-item ng-repeat="item in generalMetadata">
		        		<span flex="20"><b>{{ ::item.name }}</b></span><span flex>{{ ::item.value }}</span>
		        	</md-list-item>
		     	</md-list>
			</expander-box>
			<expander-box id="shortMetadata" color="white" background-color="rgb(63,81,181)" expanded="shortExpanderOpened" title="lblShortMeta" toolbar-class="secondaryToolbar">
				<md-list-item ng-repeat="item in shortText">
					<div flex>
		     			<md-input-container>
		     				<label>{{ ::item.name }}</label><input ng-model="item.value"  <%= canModify? "":"readonly" %> >
		     			</md-input-container>
					</div>
	        	</md-list-item>
			</expander-box>
			<!-- <expander-box  id="longMetadata" color="white" background-color="rgb(63,81,181)" expanded="longExpanderOpened" title="lblLongMeta" toolbar-class="secondaryToolbar"> -->
			<expander-box  id="longMetadata" color="white" background-color="rgb(63,81,181)" expanded="true" title="lblLongMeta" toolbar-class="secondaryToolbar">
				<md-tabs class="removeTransition" layout="column" md-border-bottom md-dynamic-height >
					<md-tab flex=200  ng-repeat="item in longText" label="{{::item.name}}" md-on-select="setTab($index)" > <!-- md-active="selectedTab.tab==0" -->
					 <md-tab-body>
					 <!-- workaround to disable wysiwyg if user haven't authorization -->
					 <div  <%= canModify? "style='display:none'":"" %> style="position:absolute; z-index:1000;background:transparent;" layout-fill>
					 
					 </div>
					<wysiwyg-edit content="item.value"></wysiwyg-edit> <!-- There was a ng-if-->
					</md-tab-body>
						
					</md-tab>
				</md-tabs>
			</expander-box>
			
			<expander-box  id="attachments" color="white" background-color="rgb(63,81,181)" expanded="fileExpanderOpened" title="lblAttachments" toolbar-class="secondaryToolbar"> 
				<!--  
				<div ng-repeat="fileMeta in file" layout="column">
					<div layout="row" >
						<div flex="15" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
							{{fileMeta.fileName}}
						</div>
						<div flex="15" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
							{{fileMeta.saveDate}}
						</div>
						<div flex="40">
							<file-upload id="id_file_upload-{{$index}}" ng-model="fileMeta.fileToSave" ng-disabled=false ng-if=<%= canModify %>></file-upload>
						</div>
						<div flex="10">
							<md-button class="md-ExtraMini md-raised " ng-click="uploadFile(fileMeta.fileToSave)" ng-if=<%= canModify %>>Upload</md-button>
						</div>
						<div flex="10">
							<md-button class="md-ExtraMini md-raised " ng-click="download(fileMeta.id,fileMeta.savedFile)" ng-if="fileMeta.fileName" >Download</md-button>
						</div>
						<div flex="10">
							<md-button class="md-ExtraMini md-raised " ng-click="cleanFile(fileMeta.id)" ng-if=<%= canModify %>>Clean</md-button>
						</div>
					</div>
				</div>	
				-->
				
				<table>
				
					  <tr ng-repeat="fileMeta in file" layout-fill>
					    <td style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-weight: bold;" flex>{{fileMeta.fileName}}&nbsp; &nbsp;</td> 
					    <td style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" flex>{{fileMeta.saveDate}}&nbsp; &nbsp;</td>
					    <td><file-upload id="id_file_upload-{{$index}}" ng-model="fileMeta.fileToSave" ng-disabled=false ng-if=<%= canModify %>></file-upload></td>
						<td><md-button class="md-ExtraMini md-raised " ng-click="uploadFile(fileMeta.fileToSave)" ng-if=<%= canModify %>>Upload</md-button></td>		
						<td><md-button class="md-ExtraMini md-raised " ng-click="download(fileMeta.id,fileMeta.savedFile)" ng-if="fileMeta.fileName">Download</md-button></td>
						<td><md-button class="md-ExtraMini md-raised " ng-click="cleanFile(fileMeta.id)" ng-if=<%= canModify %>>Clean</md-button></td>						 	
					  </tr>
				</table> 

				

			</expander-box>
			
			
			
		</div>


	
	  	<div class="md-actions" layout="row">
		  	<span flex></span>
	    		  <% if (userProfile.isAbleToExecuteAction(SpagoBIConstants.SAVE_METADATA_FUNCTIONALITY)) { %>
			      	<md-button aria-label="{{lblSave}}" class="md-primary md-raised" 
						ng-click="save()">
						{{lblSave}}
				 	</md-button>
				 	<%} %>
   		 </div>
		
		
		
		
		
		
		
		</div>		
	</body>
</html>	