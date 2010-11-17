/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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

**/
package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.wapp.bo.Menu;
import it.eng.spagobi.wapp.services.DetailMenuModule;
import it.eng.spagobi.wapp.util.MenuUtilities;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Chiarelli Chiara
 */
public class MenuListJSONSerializer implements Serializer {
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String TEXT = "text";
	public static final String PATH = "path";
	public static final String CLS = "cls";
	public static final String ICON = "icon";
	public static final String HREF = "href";
	public static final String ITEMS ="items";
	public String contextName = "";
	public String defaultThemePath="/themes/sbi_default";

	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		/*The result is an object of type:
		  {"items":
			[{
				"text":"Primo menu",
				"path":"Primo menu",
				"name":"menu0",
				"href":"javascript:execDirectUrl('/SpagoBI/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID=11', 'Primo menu > adsasdsa' )",
				"icon":"/SpagoBI/themes/sbi_default/img/wapp/static_page.png",
				"id":"basicMenu_0",
				"items":...
			 },...
			]
		  }
		 */
		contextName = GeneralUtilities.getSpagoBiContext();
		if( !(o instanceof List) ) {
			throw new SerializationException("MenuListJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			List filteredMenuList = (List) o;
			if(filteredMenuList!=null && !filteredMenuList.isEmpty()){
			result = new JSONObject();
			JSONArray tempFirstLevelMenuList = new JSONArray();
				for (int i=0; i<filteredMenuList.size(); i++){
					Menu menuElem = (Menu)filteredMenuList.get(i);
					String path=MenuUtilities.getMenuPath(menuElem);
					if (menuElem.getLevel().intValue() == 1){
						JSONObject temp = new JSONObject();
						temp.put(NAME, "menu"+i);
						temp.put(ID, menuElem.getMenuId());
						MessageBuilder msgBuild=new MessageBuilder();
						String text = "";
						if (!menuElem.isAdminsMenu() || !menuElem.getName().startsWith("#"))
							text = msgBuild.getUserMessage(menuElem.getName(),null, locale);
						else{							
							if (menuElem.getName().startsWith("#")){				
								String titleCode = menuElem.getName().substring(1);									
								text = msgBuild.getMessage(titleCode, locale);								
							} else {
								text = menuElem.getName();
							}
						}
						temp.put(TEXT, text);
						temp.put(PATH, path);
						String icon=DetailMenuModule.assignImage(menuElem);
				        if(menuElem.isViewIcons() && !icon.equalsIgnoreCase("")){ 				        	
				           temp.put(ICON, contextName+defaultThemePath+icon);
				        }
						
						if(menuElem.getObjId()!=null){
							temp.put(HREF, "execDirectUrl('"+contextName+"/servlet/AdapterHTTP?ACTION_NAME=MENU_BEFORE_EXEC&MENU_ID="+menuElem.getMenuId()+"', '"+path+"' )");
						}else if(menuElem.getStaticPage()!=null){
							temp.put(HREF, "execDirectUrl('"+contextName+"/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID="+menuElem.getMenuId()+"', '"+path+"' )");
						}else if(menuElem.getFunctionality()!=null){
							temp.put(HREF, "execDirectUrl('"+DetailMenuModule.findFunctionalityUrl(menuElem, contextName)+"', '"+path+"')");
						}else if(menuElem.getExternalApplicationUrl()!=null){
							temp.put(HREF, "execDirectUrl('"+StringEscapeUtils.escapeJavaScript(menuElem.getExternalApplicationUrl())+"', '"+path+"')");
						}else if (menuElem.isAdminsMenu() && menuElem.getUrl()!=null){							
							String url = "javascript:execDirectUrl('"+ menuElem.getUrl()+"'";
							url = url.replace("${SPAGOBI_CONTEXT}",contextName);
							url = url.replace("${SPAGO_ADAPTER_HTTP}", GeneralUtilities.getSpagoAdapterHttpUrl());		
							path = path.replace("#","");
							temp.put(HREF, url+", '"+path+"')");
						}
											
						if (menuElem.getHasChildren()){		
							
							List lstChildrenLev2 = menuElem.getLstChildren();
							JSONArray tempMenuList =(JSONArray)getChildren(lstChildrenLev2, 1,locale);
							temp.put(ITEMS, tempMenuList);
						}
						tempFirstLevelMenuList.put(temp);
					}
				}
				result.put(ITEMS, tempFirstLevelMenuList);
			}			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		return result;
	}

	private Object getChildren(List children, int level, Locale locale) throws JSONException{
		JSONArray tempMenuList = new JSONArray();
		for (int j=0; j<children.size(); j++){ 
			Menu childElem = (Menu)children.get(j);	
			JSONObject  temp2 = new JSONObject();

			temp2.put(ID, new Double(Math.random()).toString());
			MessageBuilder msgBuild=new MessageBuilder();
			String text = "";
			if (!childElem.isAdminsMenu() || !childElem.getName().startsWith("#"))
				text = msgBuild.getUserMessage(childElem.getName(),null, locale);
			else{							
				if (childElem.getName().startsWith("#")){				
					String titleCode = childElem.getName().substring(1);									
					text = msgBuild.getMessage(titleCode, locale);								
				} else {
					text = childElem.getName();
				}
			}
			//String text = msgBuild.getUserMessage(childElem.getName(),null, locale);
			temp2.put(TEXT, text);
			String path=MenuUtilities.getMenuPath(childElem);
			temp2.put(PATH, path);
			String icon=DetailMenuModule.assignImage(childElem);
	        if(childElem.isViewIcons() && !icon.equalsIgnoreCase("")){ 
	           temp2.put(ICON, contextName+defaultThemePath+icon);
	        }
			if(childElem.getObjId()!=null){
				temp2.put(HREF, "javascript:execDirectUrl('"+contextName+"/servlet/AdapterHTTP?ACTION_NAME=MENU_BEFORE_EXEC&MENU_ID="+childElem.getMenuId()+"', '"+path+"' )");
			}else if(childElem.getStaticPage()!=null){
				temp2.put(HREF, "javascript:execDirectUrl('"+contextName+"/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID="+childElem.getMenuId()+"', '"+path+"' )");
			}else if(childElem.getFunctionality()!=null){
				temp2.put(HREF, "javascript:execDirectUrl('"+DetailMenuModule.findFunctionalityUrl(childElem, contextName)+"', '"+path+"')");
			}else if(childElem.getExternalApplicationUrl()!=null){
				temp2.put(HREF, "javascript:execDirectUrl('"+StringEscapeUtils.escapeJavaScript(childElem.getExternalApplicationUrl())+"', '"+path+"')");
			}else if(childElem.isAdminsMenu() && childElem.getUrl()!=null){				
				String url = "javascript:execDirectUrl('"+ childElem.getUrl()+"'";
				url = url.replace("${SPAGOBI_CONTEXT}",contextName);
				url = url.replace("${SPAGO_ADAPTER_HTTP}", GeneralUtilities.getSpagoAdapterHttpUrl());		
				path = path.replace("#","");
				temp2.put(HREF, url+", '"+path+"')");
			}
			if (childElem.getHasChildren()){
				level ++;
				List childrenBis = childElem.getLstChildren();
				JSONArray tempMenuList2 =(JSONArray)getChildren(childrenBis,level, locale);
				temp2.put(ITEMS, tempMenuList2);
			}
			tempMenuList.put(temp2);
	   }	
	  return tempMenuList;
	}
	
}
