/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.serializer;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.utilities.themes.ThemesManager;
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
 * @author Monica Franceschini
 */
public class MenuListJSONSerializer implements Serializer {

	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String TITLE_ALIGN = "titleAlign";
	public static final String COLUMNS = "columns";
	public static final String ICON_CLS = "iconCls";
	public static final String ICON_ALIGN = "iconAlign";
	public static final String SCALE = "scale";
	public static final String TOOLTIP ="tooltip";
	public static final String SRC ="src";
	public static final String XTYPE ="xtype";
	public static final String PATH ="path";
	public static final String HREF ="href";
	
	public static final String MENU ="menu";

	public static final String NAME = "name";
	public static final String TEXT = "text";
	public static final String ITEMS ="items";
	public static final String LABEL ="itemLabel";
	public static final String INFO ="INFO";
	public static final String ROLE ="ROLE";
	public static final String LANG ="LANG";
	public static final String HOME ="HOME";
	public static final String TARGET ="hrefTarget";
	
	public String contextName = "";
	public String defaultThemePath="/themes/sbi_default";

	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONArray  result = null;

		contextName = GeneralUtilities.getSpagoBiContext();
		if( !(o instanceof List) ) {
			throw new SerializationException("MenuListJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}

		try {
			List filteredMenuList = (List) o;
			if(filteredMenuList!=null && !filteredMenuList.isEmpty()){
				result = new JSONArray();
				JSONArray tempFirstLevelMenuList = new JSONArray();
				JSONArray menuUserList = new JSONArray();
				MessageBuilder msgBuild=new MessageBuilder();
				//build home
				JSONObject home = new JSONObject();
				JSONObject personal = new JSONObject();

				home.put(ICON_CLS, "home");
				home.put(TOOLTIP, "Home");
				home.put(ICON_ALIGN, "top");
				home.put(SCALE, "large");
				home.put(PATH, "Home");
				home.put(LABEL, HOME);
				home.put(TARGET, "_self");
				home.put(HREF, "javascript:goHome('/html/home.html', 'spagobi');");
				//home.put(HREF, "javascript:alert('ciao');");
				
				String userMenu = msgBuild.getI18nMessage(locale, "menu.UserMenu");
				personal.put(ICON_CLS, "spagobi");
				personal.put(TOOLTIP, userMenu);
				personal.put(ICON_ALIGN, "top");
				personal.put(SCALE, "large");
				personal.put(PATH, userMenu);
				personal.put(TARGET, "_self");

				
				tempFirstLevelMenuList.put(home);
				tempFirstLevelMenuList.put(personal);
				boolean isAdmin= false;
				for (int i=0; i<filteredMenuList.size(); i++){
					Menu menuElem = (Menu)filteredMenuList.get(i);
					String path=MenuUtilities.getMenuPath(menuElem, locale);
					
					if (menuElem.getLevel().intValue() == 1){

						JSONObject temp = new JSONObject();
						
						if(!menuElem.isAdminsMenu()){

							menuUserList = createUserMenuElement(menuElem, locale, 1, menuUserList);
							personal.put(MENU, menuUserList);
							
							if (menuElem.getHasChildren()){		
								
								List lstChildrenLev2 = menuElem.getLstChildren();
								JSONArray tempMenuList2 =(JSONArray)getChildren(lstChildrenLev2, 1,locale);
								temp.put(MENU, tempMenuList2);
							}
						}else{
							isAdmin= true;

							temp.put(ICON_CLS, menuElem.getIconCls());
	
							
							String text = "";
							if (!menuElem.isAdminsMenu() || !menuElem.getName().startsWith("#"))
	
								text = msgBuild.getI18nMessage(locale, menuElem.getName());
							else{							
								if (menuElem.getName().startsWith("#")){				
									String titleCode = menuElem.getName().substring(1);									
									text = msgBuild.getMessage(titleCode, locale);								
								} else {
									text = menuElem.getName();
								}
							}
							temp.put(TOOLTIP, text);
							temp.put(ICON_ALIGN, "top");
							temp.put(SCALE, "large");
							temp.put(PATH, path);
							temp.put(TARGET, "_self");
							
							if (menuElem.getHasChildren()){		
	
								List lstChildrenLev2 = menuElem.getLstChildren();
								JSONArray tempMenuList =(JSONArray)getChildren(lstChildrenLev2, 1,locale);
								temp.put(MENU, tempMenuList);
							}
							tempFirstLevelMenuList.put(temp);
						}

					}
				}
				if(!isAdmin){
					tempFirstLevelMenuList= createEndUserMenu(locale, 1, tempFirstLevelMenuList);
				}
				tempFirstLevelMenuList= createFixedMenu(locale, 1, tempFirstLevelMenuList);
				result = tempFirstLevelMenuList;
			}			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {

		}
		return result;
	}
	
	private JSONArray createEndUserMenu(Locale locale, int level, JSONArray tempMenuList) throws JSONException, EMFUserError{

		 // JSONObject charts = createMenuItem("charts","/servlet/AdapterHTTP?PAGE=DetailBIObjectPage&MESSAGEDET=DETAIL_NEW","Analytical model", true, null);
		JSONObject browser = createMenuItem("folder_open","/servlet/AdapterHTTP?ACTION_NAME=DOCUMENT_USER_BROWSER_START_ACTION&LIGHT_NAVIGATOR_RESET_INSERT=TRUE","Documents browser", true, null);
		JSONObject favourites = createMenuItem("bookmark","/servlet/AdapterHTTP?PAGE=HOT_LINK_PAGE&OPERATION=GET_HOT_LINK_LIST&LIGHT_NAVIGATOR_RESET_INSERT=TRUE","My favorites", true, null);

		JSONObject createDoc = createMenuItem("pencil","/servlet/AdapterHTTP?ACTION_NAME=START_CREATING_WORKSHEET_FROM_DATASET_ACTION&LIGHT_NAVIGATOR_RESET_INSERT=TRUE","Create document", true, null);
		JSONObject subscription = createMenuItem("edit","/servlet/AdapterHTTP?PAGE=ListDistributionListUserPage&LIGHT_NAVIGATOR_RESET_INSERT=TRUE","Subscriptions", true, null);
		JSONObject toDoList = createMenuItem("list","/servlet/AdapterHTTP?PAGE=WorkflowToDoListPage&WEBMODE=TRUE&LIGHT_NAVIGATOR_RESET_INSERT=TRUE","To do list", true, null);

		// tempMenuList.put(charts);
		tempMenuList.put(browser);
		tempMenuList.put(favourites);
		LowFunctionality personalFolder = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByCode("USER_FUNCT", false);
		JSONObject myFolder = new JSONObject();
		if(personalFolder != null){
			Integer persFoldId = personalFolder.getId();
			myFolder =  createMenuItem("my_folder","/servlet/AdapterHTTP?ACTION_NAME=DOCUMENT_USER_BROWSER_START_ACTION&node="+persFoldId,"My folder", true, null);
			tempMenuList.put(myFolder);
		}
		
		
		tempMenuList.put(createDoc);
		tempMenuList.put(subscription);
		tempMenuList.put(toDoList);
		
		return tempMenuList;
	}
	private JSONObject createMenuItem(String icon, String href, String tooltip, boolean idDirectLink, String label) throws JSONException{
		JSONObject menuItem = new JSONObject();
		menuItem.put(ICON_ALIGN, "top");
		menuItem.put(SCALE, "large");
		menuItem.put(TOOLTIP, "Info");
		menuItem.put(ICON_CLS, icon);
		menuItem.put(TOOLTIP, tooltip);
		menuItem.put(TARGET, "_self");
		if(label != null){
			menuItem.put(LABEL, label);
		}
		if(idDirectLink){
			menuItem.put(HREF, "javascript:javascript:execDirectUrl('"+contextName+href+"', '"+tooltip+"')");
		}else{
			if(label != null && label.equals(INFO)){
				menuItem.put(HREF, "javascript:info()");
			}else if(label != null && label.equals(ROLE)){
				menuItem.put(HREF, "javascript:role()");
			}else{
				menuItem.put(HREF, "javascript:execUrl('"+contextName+href+"')");
			}
		}
		
		return menuItem;
	}
	
	private JSONArray createFixedMenu(Locale locale, int level, JSONArray tempMenuList) throws JSONException{

		JSONObject spacer = new JSONObject();
		JSONObject lang = createMenuItem("flag","","Languages", true, "LANG");

		JSONObject roles = createMenuItem("roles","","Roles", false, "ROLE");
		
		JSONObject info = createMenuItem("info","","Info", false, "INFO");
		JSONObject power = createMenuItem("power","/servlet/AdapterHTTP?ACTION_NAME=LOGOUT_ACTION&LIGHT_NAVIGATOR_DISABLED=TRUE","Quit", false, null);
		
		spacer.put("xtype", "spacer");
		tempMenuList.put("->");		

		tempMenuList.put(roles);
		
		tempMenuList.put(lang);

		tempMenuList.put(info);

		tempMenuList.put(power);
		
		return tempMenuList;
	}

	private Object getChildren(List children, int level, Locale locale) throws JSONException{
		JSONArray tempMenuList = new JSONArray();
		for (int j=0; j<children.size(); j++){ 
			Menu childElem = (Menu)children.get(j);	
			tempMenuList = createUserMenuElement(childElem, locale, level, tempMenuList);
		}	
		return tempMenuList;
	}
	
	private JSONArray createUserMenuElement(Menu childElem, Locale locale, int level, JSONArray tempMenuList) throws JSONException{

		JSONObject  temp2 = new JSONObject();

		String path=MenuUtilities.getMenuPath(childElem, locale);

		MessageBuilder msgBuild=new MessageBuilder();
		String text = "";
		if (!childElem.isAdminsMenu() || !childElem.getName().startsWith("#"))
			text = msgBuild.getI18nMessage(locale, childElem.getName());
		else{							
			if (childElem.getName().startsWith("#")){				
				String titleCode = childElem.getName().substring(1);									
				text = msgBuild.getMessage(titleCode, locale);								
			} else {
				text = childElem.getName();
			}
		}
		temp2.put(ID, new Double(Math.random()).toString());
		
		if (childElem.getHasChildren()){
			level ++;
			temp2.put(TITLE, text);
			temp2.put(TITLE_ALIGN, "left");
			temp2.put(COLUMNS, 1);
			temp2.put(XTYPE, "buttongroup");
			temp2.put(TARGET, "_self");
			
			List childrenBis = childElem.getLstChildren();
			JSONArray tempMenuList2 =(JSONArray)getChildren(childrenBis,level, locale);
			temp2.put(ITEMS, tempMenuList2);

		}else{
			if(childElem.getGroupingMenu() != null && childElem.getGroupingMenu().equals("true")){
				temp2.put(TITLE, text);
				temp2.put(TITLE_ALIGN, "left");
				temp2.put(COLUMNS, 1);
				temp2.put(XTYPE, "buttongroup");
			}else{
				temp2.put(TEXT, text);
				temp2.put("style", "text-align: left;");
				temp2.put(SRC, childElem.getUrl());
				temp2.put(TARGET, "_self");
				
				if(childElem.getObjId()!=null){
					temp2.put(HREF, "javascript:execDirectUrl('"+contextName+"/servlet/AdapterHTTP?ACTION_NAME=MENU_BEFORE_EXEC&MENU_ID="+childElem.getMenuId()+"', '"+path+"' )");
				}else if(childElem.getStaticPage()!=null){
					temp2.put(HREF, "javascript:execDirectUrl('"+contextName+"/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID="+childElem.getMenuId()+"', '"+path+"' )");
				}else if(childElem.getFunctionality()!=null){					
					String finalUrl = "javascript:execDirectUrl('"+DetailMenuModule.findFunctionalityUrl(childElem, contextName)+"', '"+path+"')";
					temp2.put(HREF, finalUrl);

				}else if(childElem.getExternalApplicationUrl()!=null){
					temp2.put(HREF, "javascript:callExternalApp('"+StringEscapeUtils.escapeJavaScript(childElem.getExternalApplicationUrl())+"', '"+path+"')");
				}else if (childElem.isAdminsMenu() && childElem.getUrl()!=null){							
					String url = "javascript:execDirectUrl('"+ childElem.getUrl()+"'";
					url = url.replace("${SPAGOBI_CONTEXT}",contextName);
					url = url.replace("${SPAGO_ADAPTER_HTTP}", GeneralUtilities.getSpagoAdapterHttpUrl());		
					path = path.replace("#","");

					temp2.put(HREF, url+", '"+path+"')");
				}
			}
		}
		tempMenuList.put(temp2);
		return tempMenuList;
	}


}
