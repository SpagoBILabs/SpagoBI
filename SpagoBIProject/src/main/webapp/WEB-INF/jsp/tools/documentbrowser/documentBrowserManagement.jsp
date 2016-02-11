<%@ page language="java" pageEncoding="utf-8" session="true"%>

<%@ include file="/WEB-INF/jsp/commons/angular/angularResource.jspf"%>

<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%
	String contextName = ChannelUtilities.getSpagoBIContextName(request);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@include file="/WEB-INF/jsp/commons/angular/angularImport.jsp"%>
	
	<!-- Styles -->
	<link rel="stylesheet" type="text/css" href="/SpagoBI/themes/glossary/css/generalStyle.css">
	<link rel="stylesheet" type="text/css" href="/SpagoBI/themes/fiware/css/documentbrowser/md-data-table.min.css">
	<link rel="stylesheet" type="text/css" href="/SpagoBI/themes/fiware/css/documentbrowser/documentBrowser.css">
	<link rel="stylesheet" type="text/css" href="/SpagoBI/themes/fiware/css/documentbrowser/documentBrowserFiware.css">
	
	<script type="text/javascript" src="/SpagoBI/js/src/angular_1.4/tools/documentbrowser/md-data-table.min.js"></script>
	<script type="text/javascript" src="/SpagoBI/js/src/angular_1.4/tools/commons/document-tree/DocumentTree.js"></script>
	<script type="text/javascript" src="/SpagoBI/js/src/angular_1.4/tools/documentbrowser/documentBrowser.js"></script>
	
	<title>Document Browser</title>
</head>

<body class="bodyStyle" ng-app="documentBrowserModule" id="ng-app">
	<div ng-controller="documentBrowserController as ctrl" ng-cloak>
		
		<!-- 
		<div>searchDocuments.length = {{searchDocuments.length}}</div>
		<div>searchDocuments = {{searchDocuments}}</div>
		<div>folderDocuments.length = {{folderDocuments.length}}</div>
		<div>showSearchResultHeader() = {{showSearchResultHeader()}}</div>
		<div>showDefaultHeader() = {{showDefaultHeader()}}</div>
		<div>showBreadcrumbHeader() = {{showBreadcrumbHeader()}}</div>
		<div>selectedFolder = {{selectedFolder.name}}</div>
		-->
		
		<!-- Toolbar -->
		<md-toolbar class="header" layout="row">
			<div class="md-toolbar-tools">
				<!-- Folders button -->
				<md-button class="toolbar-button-custom" title="Folders" aria-label="Folders" style="min-width: 40px;" hide-gt-md ng-hide="showSearchView" ng-click="toggleFolders()">
					<i class="fa fa-bars" style="color:white"></i>
				</md-button>
				
				<!-- Title -->
				<div class="header" ng-hide="showSearchView">Document Browser</div>
				
				<!-- Search back -->
				<md-button class="md-icon-button" title="Back" aria-label="Back" style="margin-right:0px;" ng-show="showSearchView" ng-click="toggleSearchView()">
					<i class="fa fa-arrow-left" style="color:white"></i>
				</md-button>
				
				<!-- Search input -->
				<md-input-container ng-show="showSearchView">
					<label class="searchheader" for="searchInput">Search documents</label>
					<input class="searchheader" type="text" id="searchInput" ng-model="searchInput" ng-change="setSearchInput(searchInput)" focus-on="searchInput">
				</md-input-container>
				
				<!-- Search clear -->
				<md-button class="md-icon-button" title="Clear" aria-label="Clear" ng-show="showSearchView" ng-click="setSearchInput('')">
					<i class="fa fa-times" style="color:white"></i>
				</md-button>
				
				<!-- Search similar words checkbox -->
				<md-checkbox class="dotted" aria-label="Search similar words" ng-model="searchSimilarWords" ng-show="showSearchView" ng-change="setSearchInput(searchInput)">
					Search similar words
				</md-checkbox>
				
				<span flex=""></span>
				
				<!-- Document view button -->
				<md-button title="{{showDocumentGridView?'List view':'Grid view'}}" aria-label="{{showDocumentGridView?'List view':'Grid view'}}" class="toolbar-button-custom" ng-hide="showSearchView" ng-click="toggleDocumentView()">
					<i class="fa fa-th-list" style="color:white" ng-show="showDocumentGridView"></i>
					<i class="fa fa-th" style="color:white" ng-hide="showDocumentGridView"></i>
				</md-button>
				
				<!-- Search button -->
				<md-button class="toolbar-button-custom" title="Search" aria-label="Search" ng-class="{'md-raised':showSearchView}" ng-click="toggleSearchView()">
					<i class="fa fa-search" style="color:white"></i>
				</md-button>
				
				<!-- Document Detail button-->
				<md-button class="toolbar-button-custom" title="Details" aria-label="Details" ng-class="{'md-raised':showDocumentDetail}" ng-click="setDetailOpen(!showDocumentDetail)" ng-disabled="!isSelectedDocumentValid()">
					<i class="fa fa-info-circle" style="color:white"></i>
				</md-button>
				
				<!-- Settings button-->
				<!-- <md-button class="toolbar-button-custom" title="Settings" aria-label="Settings" ng-click="alert('Settings')">
					<i class="fa fa-cog header"></i>
				</md-button> -->
				
			</div>
		</md-toolbar>
		
		<section layout="row" flex>
				
			<md-sidenav layout="column" class="md-sidenav-left md-whiteframe-z1" md-component-id="left" md-is-locked-open="$mdMedia('gt-md')" ng-hide="showSearchView">
				<md-toolbar class="md-toolbar-tools subheader">Folders</md-toolbar>
				<document-tree ng-model="folders" create-tree="true" drag-enabled="false" click-function="setSelectedFolder(item)" multi-select="false" selected-item="selectedFolder"></document-tree>
			</md-sidenav>
			
			<md-content layout="column" flex>
				<div ng-include="'search_result_header.html'" ng-show="showSearchResultHeader()"></div>
				<div layout="row" ng-hide="showSearchView" ng-include="'breadcrumbs_header.html'" ></div>
				<div ng-include="'no_documents.html'" ng-show="showDefaultHeader()"></div>
				
				<!-- Document List View -->
				<div ng-hide="showDocumentGridView || showSearchView" ng-class="{'doc-list-border': folderDocuments.length>0}"> 
					<md-data-table-container ng-show="folderDocuments.length>0">
						<table md-data-table>
							<thead md-order="documentsOrderProperty" style="height: 75px;">
								<tr>
									<th name="Type" order-by="typeCode"></th>
									<th name="Name" order-by="name"></th>
									<th name="Author" order-by="creationUser"></th>
									<th name="Date" order-by="creationDate"></th>
								</tr>
							</thead>
							<tbody>
								<tr md-auto-select ng-repeat="document in folderDocuments | orderBy: documentsOrderProperty" ng-click="selectDocument(document)" ng-class="{'selected-doc':wasSelected(document)}">
									<td>{{document.typeCode}}</td>
									<td>{{document.name}}</td>
									<td>{{document.creationUser}}</td>
									<td>{{document.creationDate | limitTo: 10}}</td>
								</tr>
							</tbody>
						</table>
					</md-data-table-container>
				</div>
				
				<!-- Document Grid View -->
				<div ng-hide="!showDocumentGridView || showSearchView">
					<div layout="row" layout-align="start center" layout-padding layout-wrap layout-fill style="padding-bottom: 32px;">
						<md-whiteframe flex="33" layout layout-align="center center" ng-repeat="document in folderDocuments">
							<!--<md-card ng-click="selectDocument(document)" ng-dblclick="alert(document.name)" ng-class="{'selected-doc':wasSelected(document)}">
								<img src="/SpagoBI/js/lib/ext-2.0.1/resources/images/default/s.gif" class="md-card-image {{document.typeCode}}-icon" style="width: 178px; height: 150px;"></img>
								<md-card-content style="padding:0px; padding-left:2px; text-align:center;">
									<div style="font-weight: bold">{{document.name | limitEllipses:20}}</div>
									<div>Author: {{document.creationUser | limitEllipses:20}}</div>
									<div>Created: {{document.creationDate | limitTo: 10}}</div>
								</md-card-content>
							</md-card>-->
							<md-card ng-click="selectDocument(document)" ng-dblclick="alert(document.name)" style="width: 220px;">
								<div class="md-card-title" layout="column">
									<span class="md-card-title-headline">{{document.name | limitEllipses:24}}</span>
									<span class="md-card-title-subheadline">{{document.creationUser | limitEllipses:24}}</span>
									<span class="md-card-title-subheadline">{{document.creationDate | limitTo:10}}</span>
								</div>
								<div layout="row" layout-align="start center">
									<img src="/SpagoBI/js/lib/ext-2.0.1/resources/images/default/s.gif" class="md-card-image {{document.typeCode}}-icon" ng-class="{'selected-doc':wasSelected(document)}" style="width:89px; height:75px;"></img>
									<div>{{document.description}}</div>
								</div>
							</md-card>
						</md-whiteframe>
					</div>
				</div>
			
				<!-- Document Search View -->
				<div ng-show="showSearchView" ng-class="{'doc-list-border': searchDocuments.length>0}">
					<p></p> 
					<md-data-table-container ng-show="searchDocuments.length > 0">
						<table md-data-table>
						<thead></thead>
							<tbody>
								<tr md-auto-select ng-repeat="document in searchDocuments" ng-click="selectDocument(document)" ng-class="{'selected-doc':wasSelected(document)}">
									<td>
										<div style="font-weight:bold">{{document.name}}</div>
										<div>Type: {{document.typeCode}}</div>
										<div>Author: {{document.creationUser}}</div>
										<div>Created: {{document.creationDate | limitTo: 10}}</div>
										<div style="font-style: italic" ng-bind-html="document.summary | unsafe"></div>
									</td>
								</tr>
							</tbody>
						</table>
					</md-data-table-container>
				</div>
			</md-content>
		
			<md-sidenav class="md-sidenav-right md-whiteframe-z1 selected-doc" md-component-id="right" md-is-locked-open="$mdMedia('gt-md')" ng-show="showDocumentDetails()">
				<md-toolbar class="subheader" style="height: 75px;">
					<div class="md-toolbar-tools subheader" style="text-align:center; display:inline;">{{selectedDocument.name | limitEllipses:28}}</div>
					<div layout="row" layout-align="center center">
						<md-button title="Execute Document" aria-label="Execute Document" class="toolbar-button-custom" ng-click="executeDocument('<%=contextName%>')">
							<i class="fa fa-play-circle" style="color:white"></i>
						</md-button>
						
						<md-button title="Edit Document" aria-label="Edit Document" class="toolbar-button-custom">
							<i class="fa fa-pencil" style="color:white"></i>
						</md-button>
						
						<md-button title="Clone Document" aria-label="Clone Document" class="toolbar-button-custom">
							<i class="fa fa-clone" style="color:white"></i>
						</md-button>
						
						<md-button title="Delete Document" aria-label="Delete Document" class="toolbar-button-custom">
							<i class="fa fa-trash-o" style="color:white"></i>
						</md-button>
					</div>
				</md-toolbar>
				
				<md-list>
					<md-list-item layout="row">
						<span flex="35"><b>Description:</b></span>
						<span flex="65">{{selectedDocument.description}}</span>
					</md-list-item>
					<md-list-item layout="row">
						<span flex="35"><b>Label:</b></span>
						<span flex="65">{{selectedDocument.label}}</span>
					</md-list-item>
					<md-list-item layout="row">
						<span flex="35"><b>State:</b></span>
						<span flex="65">{{selectedDocument.stateCode}}</span>
					</md-list-item>
					<md-list-item layout="row">
						<span flex="35"><b>Type:</b></span>
						<span flex="65">{{selectedDocument.typeCode}}</span>
					</md-list-item>
					<md-list-item layout="row">
						<span flex="35"><b>Author:</b></span>
						<span flex="65">{{selectedDocument.creationUser}}</span>
					</md-list-item>
					<md-list-item layout="row">
						<span flex="35"><b>Created:</b></span>
						<span flex="65">{{selectedDocument.creationDate}}</span>
					</md-list-item>
				</md-list>
			</md-sidenav>
				
		</section>
	</div>
	
	<script type="text/ng-template" id="folders_renderer.html">
		<div ng-click="setSelectedFolder(folder)" class="customTreeNode" ui-tree-handle>
			<md-button ng-class="{'md-primary':folder.selected,'md-raised':folder.selected}">
			<md-icon>{{folder.subfolders.length>0?(folder.showSubfolders?"&#xE313;":"&#xE315;"):""}}</md-icon>
			<md-icon>&#xE2C7;</md-icon>&nbsp;{{folder.NAME}}
			</md-button>
		</div>
		<ol ui-tree-nodes="" ng-model="folder.subfolders" ng-show="folder.showSubfolders">
			<li ng-repeat="folder in folder.subfolders" ui-tree-node ng-include="'folders_renderer.html'"></li>
		</ol>
	</script>
	
	<script type="text/ng-template" id="search_result_header.html">
		<p style="font-style: italic">&nbsp;{{searchDocuments.length || 0}} documents found.</p>
	</script>

	<script type="text/ng-template" id="no_documents.html">
		<p style="font-style: italic">&nbsp;No documents to display.</p>
	</script>

	<!-- Should be included in a div with layout row-->
	<script type="text/ng-template" id="breadcrumbs_header.html">
		<div ng-repeat="folderChild in getFolderAncestors(selectedFolder, folders)">
			<span ng-hide="$first">
				<i class="fa fa-angle-right"></i>
			</span>
			<md-button ng-click="setSelectedFolder(folderChild)">
				{{folderChild.name}}
			</md-button>
		</div>
 	</script>
</body>
</html>