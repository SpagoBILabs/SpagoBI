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

public class LanguageBox extends EnhancedSimpleComboBox <LanguageData> {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	private SimpleComboValue <LanguageData> currentLanguage;
	
	public LanguageBox() {
		super();
		setEditable(false);
		setFieldLabel(constants.language());  
		fillLanguages();
		String locale = Window.Location.getParameter("locale");
		if (locale == null || locale.isEmpty()) {
			locale = "en_US";
		}		
		SimpleComboValue <LanguageData> val = findModel(new LanguageData("n", locale));
		currentLanguage = val;
		setValue(val);
	}	
	
	public SimpleComboValue <LanguageData> getCurrentLanguage() {
		return currentLanguage;
	}
	
	private final void fillLanguages() {
		String languages = constants.languages();
				
		for (String language: languages.split(",")) {
			int index = language.indexOf("=");
			if (index == -1) {
				continue;
			}
			String id = language.substring(0, index);
			String name = language.substring(index + 1);
			add(new LanguageData(name, id));
		}
	}	
}
