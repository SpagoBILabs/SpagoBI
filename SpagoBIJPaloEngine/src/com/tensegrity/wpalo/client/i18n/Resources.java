package com.tensegrity.wpalo.client.i18n;

import com.google.gwt.core.client.GWT;

public class Resources {
	private static final Resources instance = new Resources();
	
	private final ILocalConstants constants;
	private final ILocalMessages  messages;
	
	public static Resources getInstance() {
		return instance;
	}
	
	private Resources() {
		if (GWT.isClient()) {
			constants = (ILocalConstants) GWT.create(ILocalConstants.class);
			messages  = (ILocalMessages)  GWT.create(ILocalMessages.class);
		} else {
			constants = null;
			messages  = null;
		}
	}
	
	public ILocalConstants getConstants() {
		return constants;
	}
	
	public ILocalMessages getMessages() {
		return messages;
	}	
}
