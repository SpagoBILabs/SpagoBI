package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import com.google.gwt.user.client.rpc.IsSerializable;

public class XPrintResult implements IsSerializable {
	private String fileName;
	private XViewModel view;
	
	public XPrintResult() {		
	}
	
	public XPrintResult(String fName, XViewModel view) {
		this.fileName = fName;
		this.view = view;
	}
	
	public XViewModel getView() {
		return view;
	}
	
	public String getFilename() {
		return fileName;
	}
}
