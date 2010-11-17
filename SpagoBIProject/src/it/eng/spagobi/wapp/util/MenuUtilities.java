/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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

package it.eng.spagobi.wapp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.navigation.LightNavigationManager;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.util.JavaScript;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.AdmintoolsConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.messages.IMessageBuilder;
import it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.utilities.themes.ThemesManager;
import it.eng.spagobi.wapp.bo.Menu;

public class MenuUtilities {

	Menu parent=null;

	static Logger logger = Logger.getLogger(MenuUtilities.class);

	public static final String MODULE_PAGE = "LoginPage";
	public static final String DEFAULT_LAYOUT_MODE = "ALL_TOP";
	public static final String LAYOUT_ALL_TOP = "ALL_TOP";
	public static final String LAYOUT_ALL_LEFT = "ALL_LEFT";
	public static final String LAYOUT_TOP_LEFT = "TOP_LEFT";
	public static final String LAYOUT_ADMIN_MENU = "ADMIN_MENU";
	public static final String DEFAULT_EXTRA = "NO";
	public static final String MENU_MODE = "MENU_MODE";
	public static final String MENU_EXTRA = "MENU_EXTRA";
	public static final String LIST_MENU = "LIST_MENU";

	protected static IMessageBuilder msgBuilder = MessageBuilderFactory.getMessageBuilder();
	protected static Locale locale=null;
	protected static Menu originalChild ;
	protected static int positionChild ;

	public static String getMenuPath(Menu menu) {
		try{
			if(menu.getParentId()==null){
				return menu.getName();
			}
			else{
				Menu parent=DAOFactory.getMenuDAO().loadMenuByID(menu.getParentId());		
				// can happen that parent is not found
				if(parent == null){
					return menu.getName();
				}
				else{
					return getMenuPath(parent)+" > "+menu.getName();
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception in getting menu path",e);
			return "";
		}
	}

	public static List filterListForUser(List menuList,IEngUserProfile userProfile){
		List filteredMenuList = new ArrayList();

		if(menuList!=null && !menuList.isEmpty()){
			for (int i=0; i<menuList.size(); i++){
				Menu menuElem = (Menu)menuList.get(i);
				boolean canView = false;
				if (menuElem.getCode() ==null)
					canView=MenuAccessVerifier.canView(menuElem,userProfile);
				else
					canView = true; //technical menu voice is ever visible if it's present
				if(canView){
					filteredMenuList.add(menuElem);
				}
			}		
		}
		return filteredMenuList;
	}

	/**
	 * Gets the elements of menu relative by the user logged. It reaches the role from the request and 
	 * asks to the DB all detail
	 * menu information, by calling the method <code>loadMenuByRoleId</code>.
	 *   
	 * @param request The request Source Bean
	 * @param response The response Source Bean
	 * @throws EMFUserError If an exception occurs
	 */   
	public static void getMenuItems(SourceBean request, SourceBean response, IEngUserProfile profile) throws EMFUserError {
		try {	
			List lstFinalMenu = new ArrayList();
			// get config
			SourceBean configSingleton = (SourceBean)ConfigSingleton.getInstance();
			boolean technicalMenuLoaded = false;

			Collection lstRolesForUser = ((UserProfile)profile).getRolesForUse();
			logger.debug("** Roles for user: " + lstRolesForUser.size());


			Object[] arrRoles = lstRolesForUser.toArray();
			Integer levelItem = 1;			
			for (int i=0; i< arrRoles.length; i++){
				logger.debug("*** arrRoles[i]): " + arrRoles[i]);
				Role role = (Role)DAOFactory.getRoleDAO().loadByName((String)arrRoles[i]);
				if (role != null){	
					//list final user menu
					List lstUserMenuItems  = DAOFactory.getMenuRolesDAO().loadMenuByRoleId(role.getId());
					if (lstUserMenuItems == null)
						logger.debug("Not found menu items for User Role " + (String)arrRoles[i] );
					else {
						for(int j=0; j<lstUserMenuItems.size(); j++){
							Menu tmpObj = (Menu)lstUserMenuItems.get(j);

							if (!containsMenu(lstFinalMenu, tmpObj)){						
								lstFinalMenu.add(tmpObj);	
							}
							else{
								//checks merge of children's item								
								List tmpObjChildren = tmpObj.getLstChildren();
								List tmpNewObjChildren = new ArrayList();

								for (int k=0; k<tmpObjChildren.size();k++){
									Menu tmpObjChild = (Menu)tmpObjChildren.get(k);								
									if (!containsMenuChildren(lstFinalMenu, tmpObjChild)){		
										tmpNewObjChildren.add(tmpObjChild);
									}
									else{
										//if (!tmpNewObjChildren.contains(originalChild))
										if (!containsMenuChildren(tmpNewObjChildren, originalChild))
											tmpNewObjChildren.add(originalChild);
									}
								}		

								tmpObj.setLstChildren(tmpNewObjChildren);
								if (positionChild >= 0)
									lstFinalMenu.set(positionChild, tmpObj);

							}

						}
					}

					//	List lstAdminMenuItems = new  ArrayList();
					if (!technicalMenuLoaded && (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)  // for administrators
							|| profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_DEV)  // for developers
							|| profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_TEST)  // for testers
							|| profile.isAbleToExecuteAction(SpagoBIConstants.PARAMETER_MANAGEMENT))){ 
						//list technical user menu
						technicalMenuLoaded = true;						
						List firstLevelItems = ConfigSingleton.getInstance().getAttributeAsList("TECHNICAL_USER_MENU.ITEM");
						Iterator it = firstLevelItems.iterator();
						while (it.hasNext()) {
							SourceBean itemSB = (SourceBean) it.next();
							if (isAbleToSeeItem(itemSB, profile)) {
								//lstAdminMenuItems.add(getAdminItem(itemSB, levelItem, profile));
								lstFinalMenu.add(getAdminItem(itemSB, levelItem, profile));
								levelItem++;
							}
						}						
						//lstFinalMenu = lstAdminMenuItems;
					}			      		        										
				}
				else
					logger.debug("Role " + (String)arrRoles[i] + " not found on db");
			}
			response.setAttribute(LIST_MENU, lstFinalMenu);

			logger.debug("List Menu Size " + lstFinalMenu.size());
			//String menuMode = (configSingleton.getAttribute("SPAGOBI.MENU.mode")==null)?DEFAULT_LAYOUT_MODE:(String)configSingleton.getAttribute("SPAGOBI.MENU.mode");
			//response.setAttribute(MENU_MODE, menuMode);
			response.setAttribute(MENU_MODE, DEFAULT_LAYOUT_MODE);

		} catch (Exception ex) {
			logger.error("Cannot fill response container" + ex.getLocalizedMessage());	
			HashMap params = new HashMap();
			params.put(AdmintoolsConstants.PAGE, MODULE_PAGE);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 500, new Vector(), params);
		}
	}

	/**
	 * This method checks if the single item is visible from the technical user
	 * @param itemSB the single item
	 * @param profile the profile
	 * @return boolean value
	 * @throws EMFInternalError
	 */
	private static boolean isAbleToSeeItem(SourceBean itemSB, IEngUserProfile profile) throws EMFInternalError {
		String functionality = (String) itemSB.getAttribute("functionality");
		if (functionality == null) {
			return isAbleToSeeContainedItems(itemSB, profile);
		} else {
			return profile.isAbleToExecuteAction(functionality);
		}
	}

	/**
	 * This method checks if the single item has other sub-items visible from the technical user
	 * @param itemSB the master item
	 * @param profile the profile
	 * @return boolean value
	 * @throws EMFInternalError
	 */
	private static  boolean isAbleToSeeContainedItems(SourceBean itemSB, IEngUserProfile profile) throws EMFInternalError {
		List subItems = itemSB.getAttributeAsList("ITEM");
		if (subItems == null || subItems.isEmpty()) return false;
		Iterator it = subItems.iterator();
		while (it.hasNext()) {
			SourceBean subItem = (SourceBean) it.next();
			String functionality = (String) subItem.getAttribute("functionality");
			if (profile.isAbleToExecuteAction(functionality)) return true;
		}
		return false;
	}



	/**
	 * This method return a Menu type element with the technical user item (the item is created in memory, it isn't on db)
	 * @param itemSB the technical item to add
	 * @param father
	 * @return
	 */
	private static Menu getAdminItem(SourceBean itemSB, Integer progStart, IEngUserProfile profile){
		Menu node = new Menu();
		try{
			Integer menuId = new Integer(progStart*1000);					

			String functionality = (String) itemSB.getAttribute("functionality");
			String code = (String) itemSB.getAttribute("code");
			String titleCode = (String) itemSB.getAttribute("title");
			String iconUrl = (String) itemSB.getAttribute("iconUrl");
			String url = (String) itemSB.getAttribute("url");

			node.setMenuId(menuId);			
			node.setCode(code);		
			node.setParentId(null);
			node.setProg(progStart);
			node.setName(titleCode);
			node.setLevel(new Integer(1));
			node.setDescr(titleCode);
			node.setUrl(url);
			node.setViewIcons(true);
			node.setIconPath(iconUrl);
			node.setAdminsMenu(true);

			if (functionality == null) {
				//father node
				List subItems = itemSB.getAttributeAsList("ITEM");	
				Iterator it = subItems.iterator();
				if (subItems == null || subItems.isEmpty())
					node.setHasChildren(false);
				else{
					node.setHasChildren(true);			
					List lstChildren = new ArrayList();
					while (it.hasNext()) {
						//defines children
						SourceBean subItem = (SourceBean) it.next();
						if (isAbleToSeeItem(subItem, profile)) {
							functionality = (String) subItem.getAttribute("functionality");
							code = (String) subItem.getAttribute("code");
							titleCode = (String) subItem.getAttribute("title");
							iconUrl = (String) subItem.getAttribute("iconUrl");
							url = (String) subItem.getAttribute("url");

							Menu subNode = new Menu();
							subNode.setMenuId(menuId++);
							subNode.setCode(code);									
							subNode.setParentId(progStart*1000);
							subNode.setName(titleCode);
							subNode.setProg(progStart);
							subNode.setLevel(new Integer(2));
							subNode.setDescr(titleCode);							
							subNode.setUrl(url);
							subNode.setViewIcons(true);
							subNode.setIconPath(iconUrl);	
							subNode.setAdminsMenu(true);
							lstChildren.add(subNode);
						}
					}
					node.setLstChildren(lstChildren);
				}

			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			logger.debug("OUT");
		}
		return node;
	}

	/**
	 * Check if the menu element in input is already presents into the list
	 * @param lst the list to check
	 * @param menu the element to check
	 * @return true if the element is already presents, false otherwise
	 */
	public static boolean containsMenu(List lst, Menu menu){
		if (lst == null)
			return false;
		for (int i=0; i<lst.size(); i++){
			Menu tmpMenu = (Menu)lst.get(i);

			if (tmpMenu.getMenuId().intValue() == menu.getMenuId().intValue()){			
				originalChild = tmpMenu;
				positionChild = i;				
				return true;	
			}

		}
		return false;
	}

	/**
	 * Check if the child menu element in input is already presents into the list
	 * @param lst the list to check
	 * @param menu the element to check
	 * @return true if the element is already presents, false otherwise
	 */
	public static boolean containsMenuChildren(List generalChildren, Menu menuChildren){

		if (generalChildren == null)
			return false;
		for (int i=0; i<generalChildren.size(); i++){
			Menu tmpMenu = (Menu)generalChildren.get(i);

			if (tmpMenu.getMenuId().intValue() == menuChildren.getMenuId().intValue()){				
				originalChild = tmpMenu;	
				return true;	
			}

		}
		return false;
	}

}
