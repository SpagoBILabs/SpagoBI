/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */
package com.tensegrity.wpalo.client.ui.dialog;

import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.google.gwt.user.client.Window;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.EnhancedSimpleComboBox;

public class ThemeBox extends EnhancedSimpleComboBox <ThemeData> {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	private SimpleComboValue <ThemeData> currentTheme;
	
	public ThemeBox() {
		super();
		setEditable(false);
		setFieldLabel(constants.theme());  		
		fillThemes();
		String theme = Window.Location.getParameter("theme");
		if (theme == null || theme.isEmpty()) {
			theme = "blue";
		}		
		SimpleComboValue <ThemeData> val = findModel(new ThemeData("n", theme));
		currentTheme = val;
		setValue(val);
	}	
	
	public SimpleComboValue <ThemeData> getCurrentTheme() {
		return currentTheme;
	}
	
	private final void fillThemes() {
		String themes = constants.themes();
				
		for (String theme: themes.split(",")) {
			int index = theme.indexOf("=");
			if (index == -1) {
				continue;
			}
			String id = theme.substring(0, index);
			String name = theme.substring(index + 1);
			add(new ThemeData(name, id));
		}
	}	
}
