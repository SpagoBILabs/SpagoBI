<%-- 
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
--%>

<%-- 
author: Monica Franceschini

--%>
<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spago.configuration.*"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.engine.mobile.*"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.services.common.EnginConf"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<html>

	<head>
		<link rel="stylesheet" href="../css/sencha-touch-debug.css" type="text/css">

		<link rel="stylesheet" href="../css/Ext.ux.TouchGridPanel.css" type="text/css">

		<script type="text/javascript" src="../js/sencha/sencha-touch-debug.js"></script>
		
		<script type="text/javascript" src="../js/sencha/Ext.ux.touch.PagingToolbar.js"></script>
		<script type="text/javascript" src="../js/spagobi/service/ServiceRegistry.js"></script>
		
	</head>

	<body>

		
		        <script>
        Ext.setup({
            icon: 'icon.png',
            tabletStartupScreen: 'tablet_startup.png',
            phoneStartupScreen: 'phone_startup.png',
            glossOnIcon: false,

            onReady: function() {

                var form;

				Sbi.config = {};
				
				var url = {
			    	host: 'localhost'
			    	, port: '8080'
			    	    
			    };
			    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
			    	baseUrl: url
			    });
				var loginUrl = Sbi.config.serviceRegistry.getServiceUrl({
					serviceName: 'LOGIN_SUBMIT_ACTION'
				});	

                var formBase = {
                    scroll : 'vertical',
                    url    : loginUrl,
                    standardSubmit : false,
                    items: [
                        {
                            xtype: 'fieldset',
                            title: 'SpagoBI Mobile Login',
                            instructions: 'Please login.',
                            defaults: {
                                required: true,
                                labelAlign: 'left',
                                labelWidth: '45%'
                            },
                            items: [
                            {
                                xtype: 'textfield',
                                name : 'username',
                                label: 'Username',
                                useClearIcon: true
                            }, {
                                xtype: 'passwordfield',
                                name : 'password',
                                label: 'Password',
                                useClearIcon: false
                            }]
                        }],
                    listeners : {
                        submit : function(form, result){
                            console.log('success', Ext.toArray(arguments));

                        },
                        exception : function(form, result){
                            console.log('failure', result);
                            //this.form.close();

                        }
                    },

                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            items: [
                                {
                                    text: 'Login',
                                    ui: 'confirm',
                                    handler: function() {
                                        form.submit({
                                            waitMsg : {message:'Logging in ...', cls : 'demos-loading'}
                                        });
                                      console.log('submitting to ', formBase.url);
                                      console.log('submitting ', formBase.items);
                                      Ext.Ajax.request({
                                          url: formBase.url,
                                          method: 'post',
                                          params: formBase.getValues, //{username: formBase.getValues().username, password : formBase.getValues().password},
                                          failure : function(response){
                                               //data = Ext.decode(response.responseText);
                                               //Ext.Msg.alert('Login Error', data.errorMessage, Ext.emptyFn);
                                                console.log('Login Error! ' + response.responseText);
                                          },
                                          success: function(response, opts) {
                                              console.log('Login success!');
                                              data = Ext.decode(response.responseText);
                                              if (data.errorMessage != null)
                                              {
                                                  console.log('Login Error after success!');
                                                  //Ext.Msg.alert('Login Error', data.errorMessage, Ext.emptyFn);
                                                  //mainPanel.setLoading(false);
                                              } else {
                                                  //hide the Loading mask
                                                  //mainPanel.setLoading(false);
                                                  //show the next screen
                                                  //mainPanel.setActiveItem(dashboard, 'slide');
                                                  //Ext.Msg.alert('Login success!');
                                                  console.log('Login success after data check!');
                                              }
                                          }
                                      });

                                    }
                                }
                            ]
                        }
                    ]
                };

                if (Ext.is.Phone) {
                    formBase.fullscreen = true;
                } else {
                    Ext.apply(formBase, {
                        autoRender: true,
                        floating: true,
                        modal: true,
                        centered: true,
                        hideOnMaskTap: false,
                        height: 385,
                        width: 480
                    });
                }

                form = new Ext.form.FormPanel(formBase);
                form.show();
            }
        });
    </script>
		
		 
	</body>
 
</html>