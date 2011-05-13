package it.eng.spagobi.utilities.themes;
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

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.SpagoBIConstants;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class ThemesManager {

	private static transient Logger logger = Logger.getLogger(ThemesManager.class);


	/**
	 * Gets the elements of menu relative by the user logged. It reaches the role from the request and 
	 * asks to the DB all detail
	 * menu information, by calling the method <code>loadMenuByRoleId</code>.
	 *   
	 * @param request The request Source Bean
	 * @param response The response Source Bean
	 * @throws EMFUserError If an exception occurs
	 */   

	public static String getDefaultTheme(){
		SingletonConfig spagoconfig = SingletonConfig.getInstance(); 
		String toRet=null;
		String themeSB = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.default");
		if("true".equals(themeSB)){
			toRet = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.name");
		}
		else 
		{
			toRet="sbi_default";
		}
		return toRet;	
	}

	public static String getCurrentThemeName(String  currTheme){
		SingletonConfig spagoconfig = SingletonConfig.getInstance(); 
		String toRet=null;
		String themeSB = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.name");	
		if(themeSB.equals(currTheme)){
			toRet = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.view_name");
		}
		else 
		{
			toRet="default";
		}
		return toRet;	
	}
	

	public static String getCurrentTheme(RequestContainer reqCont){
		SessionContainer sessCont = reqCont.getSessionContainer();
		SessionContainer permSess = sessCont.getPermanentContainer();		
		String currTheme=(String)permSess.getAttribute(SpagoBIConstants.THEME);

		if(currTheme!=null){
			return currTheme;
		}
		else{
			return getDefaultTheme();
		}
	}

	public static boolean drawSelectTheme(List themes){
		boolean drawSelect=false;
		if(themes==null || themes.size()<=1) { // if no theme is defined only default will be used
			drawSelect=false;
		}
		else drawSelect=true;
		return drawSelect;

	}

	/**
	 * Check if a resource exists in the current team;
	 * @param currTheme  the current theme
	 * @param resource addres of the resource ro be verified
	 */   

	public static boolean resourceExists(String currTheme, String resource){
		logger.debug("IN");
		ConfigSingleton config = ConfigSingleton.getInstance();
		String rootPath=config.getRootPath();
		String urlByTheme=resource;
		resource.trim();
		if(resource.startsWith("/"))
			resource=resource.substring(1);

		if(currTheme!=null)
		{
			urlByTheme="/themes/"+currTheme+"/"+resource;
		}

		String urlComplete=rootPath+urlByTheme;
		// check if object exists
		File check=new File(urlComplete);
		// if file
		logger.debug("IN");
		if(!check.exists())
		{
			return false;
		}
		else return true;

	}

	/**
	 * Check if a resource exists;  if the name of the resource contains the spagoBICOntext removes it
	 *   
	 * @param resource addres of the resource ro be verified
	 * @param spagoBIContext   the name of the context
	 */   

	public static boolean resourceExistsInTheme(String resource, String spagoBIContext){
		logger.debug("IN");
		ConfigSingleton config = ConfigSingleton.getInstance();
		String rootPath=config.getRootPath();
		String urlByTheme=resource;
		resource.trim();

		if(spagoBIContext!=null && resource.startsWith(spagoBIContext)){
			int sizeToRemove=spagoBIContext.length();
			resource=resource.substring(sizeToRemove);			
		}

		String urlComplete=rootPath+resource;
		// check if object exists
		File check=new File(urlComplete);
		// if file
		logger.debug("IN");
		if(!check.exists())
		{
			return false;
		}
		else return true;
	}



	public static String getTheExtTheme(String currTheme){
		SingletonConfig spagoconfig = SingletonConfig.getInstance(); 
		String toRet=null;
		String themeSB = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.name");
		if(themeSB.equals(currTheme)){
			toRet = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.ext_theme");
		}
		if(toRet==null){
			String themeSB2 = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.name");
					if("sbi_default".equals(themeSB2)){
						toRet = spagoconfig.getConfigValue("SPAGOBI.THEMES.THEME.ext_theme");
					}
		}
		// gets a default one if still not specified
		//if(toRet==null)toRet="xtheme-gray.css";
		return toRet;


		}




	}






