/*
*
* @file UserAgent.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: UserAgent.java,v 1.9 2010/02/12 13:50:48 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.util;

public class UserAgent {
	private static final UserAgent instance = new UserAgent();
	
	public final boolean isOpera;
	public final boolean isChrome;
	public final boolean isChrome1;
	public final boolean isChrome2;
	public final boolean isChrome3;
	public final boolean isChrome4;
	public final boolean isIron;
	public final boolean isIron1;
	public final boolean isIron2;
	public final boolean isIron3;
	public final boolean isIron4;
	public final boolean isWebKit;
	public final boolean isSafari;
	public final boolean isSafari2;
	public final boolean isSafari3;
	public final boolean isSafari4;
	public final boolean isIE;
	public final boolean isIE6;
	public final boolean isIE7;
	public final boolean isIE8;
	public final boolean isGecko;
	public final boolean isGecko2;
	public final boolean isGecko3;
	public final boolean isGecko35;
	public final boolean isGecko36;
	
	private UserAgent() {
	    String ua = getUserAgent();

	    isOpera = ua.indexOf("opera") != -1;
	    isChrome = ua.indexOf("chrome") != -1;
	    isChrome1 = isChrome && ua.indexOf("chrome/1.") != -1;
	    isChrome2 = isChrome && ua.indexOf("chrome/2.") != -1;
	    isChrome3 = isChrome && ua.indexOf("chrome/3.") != -1;
	    isChrome4 = isChrome && ua.indexOf("chrome/4.") != -1;
	    isIron = ua.indexOf("iron") != -1;
	    isIron1 = isIron && ua.indexOf("iron/1.") != -1;
	    isIron2 = isIron && ua.indexOf("iron/2.") != -1;
	    isIron3 = isIron && ua.indexOf("iron/3.") != -1;	    
	    isIron4 = isIron && ua.indexOf("iron/4.") != -1;
	    isWebKit = ua.indexOf("webkit") != -1;
	    isSafari = !isChrome && !isIron && ua.indexOf("safari") != -1;
	    isSafari3 = isSafari && ua.indexOf("version/3") != -1;
	    isSafari4 = isSafari && ua.indexOf("version/4") != -1;
	    isSafari2 = isSafari && (ua.indexOf("419.3") != -1 || ua.indexOf("418.9") != -1 || ua.indexOf("version/2") != -1);
	    isIE = !isOpera && ua.indexOf("msie") != -1;
	    isIE7 = !isOpera && ua.indexOf("msie 7") != -1;
	    isIE8 = !isOpera && ua.indexOf("msie 8") != -1;
	    isIE6 = !isOpera && ua.indexOf("msie 6") != -1;
	    isGecko = !isWebKit && ua.indexOf("gecko") != -1;
	    isGecko3 = isGecko && ua.indexOf("rv:1.9.0") != -1;
	    isGecko35 = isGecko && ua.indexOf("rv:1.9.1") != -1;
	    isGecko36 = isGecko && ua.indexOf("rv:1.9.2") != -1;
	    isGecko2 = isGecko && ua.indexOf("rv:1.8.1") != -1;
	}		
	
	public static final UserAgent getInstance() {
		return instance;
	}
	
	private static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;
}
