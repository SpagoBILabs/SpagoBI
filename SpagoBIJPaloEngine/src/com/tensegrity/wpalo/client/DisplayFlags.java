/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

public class DisplayFlags {
	private static final HashMap <XView, DisplayFlags> flagMap = new HashMap<XView, DisplayFlags>();
	public  static final DisplayFlags empty = new DisplayFlags();
	
	private final XUser   user;
	private final XView   view;
	private final boolean hideTitleBar;
	private final boolean hideToolBar;
	private final boolean hideSave;
	private final boolean hideSaveAs;
	private final boolean hideFilter;
	private final boolean hideStaticFilter;
	private final boolean hideHorizontalAxis;
	private final boolean hideVerticalAxis;
	private final boolean hideConnectionPicker;
	private static boolean hideViewTabs = false;
	private static boolean hideNavigator = false;
	private final boolean hidePrint;
	
	//SpagoBI modifications
	private static boolean hideConnectionAccount;
	private static boolean hideUsersRights;
		

	public static DisplayFlags getDisplayFlagsFor(XView view) {
		if (flagMap.containsKey(view)) {
			return flagMap.get(view);
		}
		return empty;
	}
	
	public static DisplayFlags createDisplayFlags(XUser user, List <Boolean> globalFlags) {
		List <Boolean> list = new ArrayList<Boolean>();
		for (int i = 0; i < 8; i++) {
			list.add(false);
		}
		list.add(true);
		hideViewTabs = globalFlags.get(0);
		hideNavigator = globalFlags.get(1);
		hideConnectionAccount = globalFlags.get(2);
		hideUsersRights = globalFlags.get(3);
		return new DisplayFlags(user, null, list);
	}
	
	public static void setDisplayFlagsFor(XView view, XUser user, List <Boolean> flags, List <Boolean> globalFlags) {
		flagMap.put(view, new DisplayFlags(user, view, flags));
		if (globalFlags != null) {
			hideViewTabs = globalFlags.get(0);
			hideNavigator = globalFlags.get(1);
			hideConnectionAccount = globalFlags.get(2);
			hideUsersRights = globalFlags.get(3);
		}
	}
	
	private DisplayFlags(XUser user, XView view, List <Boolean> displayFlags) {
		this.user          = user;
		this.view          = view;
		hideTitleBar       = displayFlags.get(0);
		hideToolBar        = displayFlags.get(1);
		hideSave           = displayFlags.get(2);
		hideSaveAs         = displayFlags.get(3);
		hideFilter         = displayFlags.get(4);
		hideStaticFilter   = displayFlags.get(5);
		hideHorizontalAxis = displayFlags.get(6);
		hideVerticalAxis   = displayFlags.get(7);
		hidePrint          = !true;
		hideConnectionPicker = displayFlags.get(8);
		//hideConnectionAccount = displayFlags.get(9);
		//hideUsersRights = displayFlags.get(10);
	}
	
	private DisplayFlags() {
		this.user          = null;
		this.view          = null;
		hideTitleBar       = false;
		hideToolBar        = false;
		hideSave           = false;
		hideSaveAs         = false;
		hideFilter         = false;
		hideStaticFilter   = false;
		hideHorizontalAxis = false;
		hideVerticalAxis   = false;
		hideViewTabs       = false;
		hideNavigator      = false;
		hidePrint          = !true;
		hideConnectionPicker = true;
		//hideConnectionAccount = false;
		//hideUsersRights = false;
	}

	public XUser getUser() {
		return user == null ? ((Workbench)Registry.get(Workbench.ID)).getUser() : user;
	}
	
	public XView getView() {
		return view;
	}
	
	public boolean isHideTitleBar() {
		return hideTitleBar;
	}

	public boolean isHideToolBar() {
		return hideToolBar;
	}

	public boolean isHideSave() {
		return hideSave;
	}

	public boolean isHideSaveAs() {
		return hideSaveAs;
	}

	public boolean isHideFilter() {
		return hideFilter;
	}

	public boolean isHideStaticFilter() {
		return hideStaticFilter;
	}

	public boolean isHideHorizontalAxis() {
		return hideHorizontalAxis;
	}

	public boolean isHideVerticalAxis() {
		return hideVerticalAxis;
	}

	public static boolean isHideViewTabs() {
		return hideViewTabs;
	}

	public static boolean isHideNavigator() {
		return hideNavigator;
	}		
	
	public boolean isHidePrint() {
		return hidePrint;
	}
	
	public boolean isHideConnectionPicker() {
		return hideConnectionPicker;
	}

	public boolean isHideConnectionAccount() {
		return hideConnectionAccount;
	}

	public boolean isHideUsersRights() {
		return hideUsersRights;
	}	
	
}
