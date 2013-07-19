<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
%>
    
<!-- Include Ext stylesheets here: -->
<link id="extall"     rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" />
<link id="theme-gray" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all-gray.css" type="text/css" />
<script type="text/javascript" src="/SpagoBI/js/lib/ext-4.1.1a/overrides/overrides.js"></script>

<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/overrides/resources/css/spagobi.css" type="text/css" />
<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/themes/sbi_default/css/home40/layout.css" type="text/css" />
<script type="text/javascript">
    Ext.BLANK_IMAGE_URL = '/SpagoBI/js/lib/ext-4.1.1a/resources/themes/images/default/tree/s.gif';
</script>

<%-- Javascript object useful for session expired management (see also sessionExpired.jsp) --%>
<script>
sessionExpiredSpagoBIJS = 'sessionExpiredSpagoBIJS';
/*
Ext.onReady(function () {
	
	var firstUrl =  '<%= StringEscapeUtils.escapeJavaScript(firstUrlToCall) %>';  
	firstUrlTocallvar = firstUrl;
    Ext.tip.QuickTipManager.init();
    this.mainframe = Ext.create('Ext.ux.IFrame', 
    			{ xtype: 'uxiframe'
  	  			, src: firstUrl
  	  			, height: '100%'
  	  			});
    
    this.titlePath = Ext.create("Ext.Panel",{title :'Home'});
    var itemsM = <%=jsonMenuList%>;
	for(i=0; i< itemsM.length; i++){
		var menuItem = itemsM[i];
		if(menuItem.itemLabel != null && menuItem.itemLabel == "LANG"){
	 		var languagesMenuItems = [];
	 		for (var j = 0; j < Sbi.config.supportedLocales.length ; j++) {
	 			var aLocale = Sbi.config.supportedLocales[j];
 				var aLanguagesMenuItem = new Ext.menu.Item({
					id: '',
					text: aLocale.language,
					iconCls:'icon-' + aLocale.language,
					href: this.getLanguageUrl(aLocale)
				})
 				languagesMenuItems.push(aLanguagesMenuItem);
	 		}
	 		menuItem.menu= languagesMenuItems;
		}else if(menuItem.itemLabel != null && menuItem.itemLabel == "ROLE"){
			if(Sbi.user.roles && Sbi.user.roles.length == 1){
				menuItem.hidden=true;
			}
		}else if(menuItem.itemLabel != null && menuItem.itemLabel == "HOME"){
			menuItem.tooltip = '<p style="color: blue; ">'+LN('sbi.home.Welcome')+'<b>'+ 
			'<p style="color: white; font-weight: bold;">'+Sbi.user.userName+'</p>'
								+'<b></p>'
		}
		
	}
	function hideItem( menu, e, eOpts){
        console.log('bye bye ');
        menu.hide();
    }
    this.mainpanel =  Ext.create("Ext.panel.Panel",{
    	autoScroll: true,
    	height: '100%',
    	items: [
			//this.titlePath	,		
    	    mainframe]
    	, dockedItems: [{
	   	    xtype: 'toolbar',
	   	    dock: 'top',
	   	    items: itemsM
    	}]
    });
    
    Ext.create('Ext.Viewport', {
    	
        layout: 'fit',
        items: [this.mainpanel]
    });
    
    
});

	*/
</script>

<html dir="ltr" lang="en-US">
    <head>
        <meta charset="utf-8" />
        <title>GeoBI</title>
    </head>
   
        <body>

    	<header class="header" id="header">
        	<div class="aux">
        		<a href="#" id="logo">GeoBI - Geographic Business Intelligence</a>
                <nav class="main-buttons">
                	<ul>
                    	<li class="btn-maps active"><a href="#">Mappe<span></span></a></li>
                        <li class="btn-datasets"><a href="#">Dataset<span></span></a></li>
                    </ul>
                </nav>
            </div>
            <div class="top-bar" id="top-bar">
                <nav class="aux">
                    <ul class="top-menu" id="top-menu">
                        <li class="first"><a href="#">GeoBI Project</a></li>
                        <li><a href="#">Tutorial</a></li>
                        <li><a href="#">Termini e condizioni</a></li>
                        <li class="user last"><a href="#"><span class="name">Emma Hofer</span> - <span class="company">ASTAT</span></a></li>
                    </ul>
                    <ul class="language-switcher">
                        <li class="active"><a href="#">IT</a></li>
                        <li><a href="#">DE</a></li>
                        <li><a href="#">EN</a></li>
                    </ul>
                </nav>
            </div>
        </header>
        <main class="main main-maps-list main-list" id="main">
        	<div class="aux">
            	<div class="list-actions-container">
                	<ul class="list-tab">
                    	<li class="active first"><a href="#">Tutte</a></li>
                        <li><a href="#">ASTAT</a></li>
                        <li class="favourite last"><a href="#">Favoriti</a></li>
                	</ul>
                    <div class="list-actions">
                        <a href="#" class="btn-add"><span class="highlighted">Carica</span> mappa<span class="plus">+</span></a>
                        <form action="#" method="get" class="search-form">
                            <fieldset>
                                <div class="field">
                                    <label for="search">Cerca fra i dataset</label>
                                    <input type="text" name="search" id="search" value="Cerca per parola chiave..." />
                                </div>
                                <div class="submit">
                                    <input type="submit" value="Cerca" />
                                </div>
                            </fieldset>
                        </form>
                        <ul class="order">
                            <li class="active"><a href="#">Recenti<span class="arrow"></span></a></li>
                            <li><a href="#">Valore 2</a></li>
                            <li><a href="#">Altro valore</a></li>
                        </ul>
                    </div>
                </div>
                <div class="list-container">
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box box-fav">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box box-fav">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                    	<div class="box-container">
                            <div class="box-figure">
                                <img src="images/placeholders/img-map.jpg" alt=" " />
                                <span class="shadow"></span>
                                <div class="hover">
                                	<div class="box-actions-container">
                                        <ul class="box-actions">
                                            <li class="view"><a href="#">Visualizza mappa</a></li>
                                        </ul>
                                    </div>
                                    <a href="#" class="delete">Cancella</a>
                                </div>
                            </div>
                            <div class="box-text">
                                <h2>Titolo della mappa</h2>
                                <p>Cras augue justo, tempor in varius ut, ornare id turpis, lorem ipsum sit, amet ...</p>
                                <p class="modified">Modificata il 20 settembre 2013</p>
                            </div>
                            <div class="fav-container">
                                <div class="fav">
                                    <span class="icon"></span>
                                    <span class="counter">12</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <footer class="footer" id="footer">
        	<div class="aux">
            	<div class="left">
                	<p>Geobi.info geographic business intelligence | TIS innovation park | Siemensstr.19 - 39100 Bozen</p>
                	<p><a href="mailto:info@geobi.info">info@geobi.info</a> | +39 0471 068000 | MwSt.Nr. IT 01677580217</p>
                	<p><a href="#">Impressum & Privacy</a> | <a href="#">Forum & FAQs</a></p>
                </div>
                <ul class="logos">
                	<li class="tis"><a href="#">TIS - Innovation Park</a></li>
                    <li class="pab"><a href="#">Provincia Autonoma di Bolzano Alto Adige - Autonome Provinz Bozen Sudtirol</a></li>
                    <li class="ue"><a href="#">Unione Europea - Provincia Autonoma di Bolzano - Alto Adige</a></li>
                </ul>
            </div>
        </footer>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"> </script>
		<script src="scripts/utility.js" type="text/javascript"></script>

    </body>
</html>
 