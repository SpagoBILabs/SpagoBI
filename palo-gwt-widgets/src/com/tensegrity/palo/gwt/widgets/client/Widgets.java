package com.tensegrity.palo.gwt.widgets.client;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.util.Theme;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

public class Widgets implements EntryPoint {
	public void onModuleLoad() {
		String theme = Window.Location.getParameter("theme");
		if (theme == null || theme.isEmpty() || theme.equalsIgnoreCase("blue")) {
			GXT.setDefaultTheme(Theme.BLUE, true);
			loadCss("blue_theme.css");			
		} else if (theme.equalsIgnoreCase("grey") || theme.equalsIgnoreCase("gray")) {
			GXT.setDefaultTheme(Theme.GRAY, true);
			loadCss("gray_theme.css");
		} else {
			GXT.setDefaultTheme(Theme.BLUE, true);
			loadCss("blue_theme.css");
		}
	}
	
	public static void loadCss(String filename) {
		Element link = DOM.createElement("link");
		DOM.setElementAttribute(link, "rel", "stylesheet");
		DOM.setElementAttribute(link, "type", "text/css");
		DOM.setElementAttribute(link, "href", filename);
		Element headElement = DOM.getElementById("head");
		DOM.appendChild(headElement, link);
	}	
}
