package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import com.google.gwt.user.client.rpc.IsSerializable;

public class XPrintConfiguration implements IsSerializable {
	public static final int PORTRAIT  = 0;
	public static final int LANDSCAPE = 1;
	
	public static final int FORMAT_A0 = 0;
	public static final int FORMAT_A1 = 1;
	public static final int FORMAT_A2 = 2;
	public static final int FORMAT_A3 = 3;
	public static final int FORMAT_A4 = 4;
	public static final int FORMAT_A5 = 5;
	public static final int FORMAT_A6 = 6;
	public static final int FORMAT_B0 = 7;
	public static final int FORMAT_B1 = 8;
	public static final int FORMAT_B2 = 9;
	public static final int FORMAT_B3 = 10;
	public static final int FORMAT_B4 = 11;
	public static final int FORMAT_B5 = 12;
	public static final int FORMAT_B6 = 13;
	public static final int FORMAT_EXECUTIVE = 14;
	public static final int FORMAT_LEGAL = 15;
	public static final int FORMAT_LETTER = 16;

	private int paperFormat;
	private int paperOrientation;
	private String title;
	private boolean showTitle;
	private boolean showPOV;
	private boolean showExpansionStateIcons;
	private boolean indent;
	private boolean printPageNumbers;
	private String maxColString;
	private String maxRowsHeaderString;
	private String cellReplaceString;
	
	public XPrintConfiguration() {		
	}
	
	public void setPaperFormat(int format) {
		paperFormat = format;
	}
	
	public int getPaperFormat() {
		return paperFormat;
	}
	
	public void setPaperOrientation(int orientation) {
		paperOrientation = orientation;
	}
	
	public int getPaperOrientation() {
		return paperOrientation;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setShowTitle(boolean show) {
		showTitle = show;
	}
	
	public boolean isShowTitle() {
		return showTitle;
	}
	
	public void setShowPOV(boolean show) {
		showPOV = show;
	}
	
	public boolean isShowPOV() {
		return showPOV;
	}
	
	public void setShowExpansionStateIcons(boolean show) {
		showExpansionStateIcons = show;
	}
	
	public boolean isShowExpansionStateIcons() {
		return showExpansionStateIcons;
	}
	
	public void setIndent(boolean indent) {
		this.indent = indent;
	}
	
	public boolean isIndent() {
		return indent;
	}
	
	public void setPrintPageNumbers(boolean print) {
		printPageNumbers = print;
	}
	
	public boolean isPrintPageNumbers() {
		return printPageNumbers;
	}
	
	public void setMaxWidths(String maxColString, String maxRowsHeaderString, String cellReplaceString) {
		this.maxColString = maxColString;
		this.maxRowsHeaderString = maxRowsHeaderString;
		this.cellReplaceString = cellReplaceString;
	}
	
	public String getMaxColString() {
		return maxColString;
	}
	
	public String getMaxRowsHeaderString() {
		return maxRowsHeaderString;
	}
	
	public String getCellReplaceString() {
		return cellReplaceString;
	}
}
