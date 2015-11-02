/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.bean;

import org.dom4j.Document;

public class ToolbarBean {

	private Boolean buttonCubeVisibleB = true;
	private Boolean buttonMDXVisibleB = true;
	private Boolean buttonOrderVisibleB = true;
	private Boolean buttonFatherMembVisibleB = true;
	private Boolean buttonHideSpansVisibleB = true;
	private Boolean buttonShowPropertiesVisibleB = true;
	private Boolean buttonHideEmptyVisibleB = true;
	private Boolean buttonShiftAxisVisibleB = true;
	private Boolean buttonDrillMemberVisibleB = true;
	private Boolean buttonDrillPositionVisibleB = true;
	private Boolean buttonDrillReplaceVisibleB = true;
	private Boolean buttonDrillThroughVisibleB = true;
	private Boolean buttonShowChartVisibleB = true;
	private Boolean buttonConfigureChartVisibleB = true;
	private Boolean buttonConfigurePrintVisibleB = true;
	private Boolean buttonFlushCacheVisibleB = true;
	private Boolean buttonSaveAnalysisVisibleB = true;
	
	
	
	public ToolbarBean() {
		super();
	}

	public ToolbarBean(Boolean buttonCubeVisibleB, Boolean buttonMDXVisibleB,
			Boolean buttonOrderVisibleB, Boolean buttonFatherMembVisibleB,
			Boolean buttonHideSpansVisibleB,
			Boolean buttonShowPropertiesVisibleB,
			Boolean buttonHideEmptyVisibleB, Boolean buttonShiftAxisVisibleB,
			Boolean buttonDrillMemberVisibleB,
			Boolean buttonDrillPositionVisibleB,
			Boolean buttonDrillReplaceVisibleB,
			Boolean buttonDrillThroughVisibleB,
			Boolean buttonShowChartVisibleB,
			Boolean buttonConfigureChartVisibleB,
			Boolean buttonConfigurePrintVisibleB,
			Boolean buttonFlushCacheVisibleB, Boolean buttonSaveAnalysisVisibleB) {
		super();
		this.buttonCubeVisibleB = buttonCubeVisibleB;
		this.buttonMDXVisibleB = buttonMDXVisibleB;
		this.buttonOrderVisibleB = buttonOrderVisibleB;
		this.buttonFatherMembVisibleB = buttonFatherMembVisibleB;
		this.buttonHideSpansVisibleB = buttonHideSpansVisibleB;
		this.buttonShowPropertiesVisibleB = buttonShowPropertiesVisibleB;
		this.buttonHideEmptyVisibleB = buttonHideEmptyVisibleB;
		this.buttonShiftAxisVisibleB = buttonShiftAxisVisibleB;
		this.buttonDrillMemberVisibleB = buttonDrillMemberVisibleB;
		this.buttonDrillPositionVisibleB = buttonDrillPositionVisibleB;
		this.buttonDrillReplaceVisibleB = buttonDrillReplaceVisibleB;
		this.buttonDrillThroughVisibleB = buttonDrillThroughVisibleB;
		this.buttonShowChartVisibleB = buttonShowChartVisibleB;
		this.buttonConfigureChartVisibleB = buttonConfigureChartVisibleB;
		this.buttonConfigurePrintVisibleB = buttonConfigurePrintVisibleB;
		this.buttonFlushCacheVisibleB = buttonFlushCacheVisibleB;
		this.buttonSaveAnalysisVisibleB = buttonSaveAnalysisVisibleB;
	}
	
	public Boolean getButtonCubeVisibleB() {
		return buttonCubeVisibleB;
	}
	public void setButtonCubeVisibleB(Boolean buttonCubeVisibleB) {
		this.buttonCubeVisibleB = buttonCubeVisibleB;
	}
	public Boolean getButtonMDXVisibleB() {
		return buttonMDXVisibleB;
	}
	public void setButtonMDXVisibleB(Boolean buttonMDXVisibleB) {
		this.buttonMDXVisibleB = buttonMDXVisibleB;
	}
	public Boolean getButtonOrderVisibleB() {
		return buttonOrderVisibleB;
	}
	public void setButtonOrderVisibleB(Boolean buttonOrderVisibleB) {
		this.buttonOrderVisibleB = buttonOrderVisibleB;
	}
	public Boolean getButtonFatherMembVisibleB() {
		return buttonFatherMembVisibleB;
	}
	public void setButtonFatherMembVisibleB(Boolean buttonFatherMembVisibleB) {
		this.buttonFatherMembVisibleB = buttonFatherMembVisibleB;
	}
	public Boolean getButtonHideSpansVisibleB() {
		return buttonHideSpansVisibleB;
	}
	public void setButtonHideSpansVisibleB(Boolean buttonHideSpansVisibleB) {
		this.buttonHideSpansVisibleB = buttonHideSpansVisibleB;
	}
	public Boolean getButtonShowPropertiesVisibleB() {
		return buttonShowPropertiesVisibleB;
	}
	public void setButtonShowPropertiesVisibleB(Boolean buttonShowPropertiesVisibleB) {
		this.buttonShowPropertiesVisibleB = buttonShowPropertiesVisibleB;
	}
	public Boolean getButtonHideEmptyVisibleB() {
		return buttonHideEmptyVisibleB;
	}
	public void setButtonHideEmptyVisibleB(Boolean buttonHideEmptyVisibleB) {
		this.buttonHideEmptyVisibleB = buttonHideEmptyVisibleB;
	}
	public Boolean getButtonShiftAxisVisibleB() {
		return buttonShiftAxisVisibleB;
	}
	public void setButtonShiftAxisVisibleB(Boolean buttonShiftAxisVisibleB) {
		this.buttonShiftAxisVisibleB = buttonShiftAxisVisibleB;
	}
	public Boolean getButtonDrillMemberVisibleB() {
		return buttonDrillMemberVisibleB;
	}
	public void setButtonDrillMemberVisibleB(Boolean buttonDrillMemberVisibleB) {
		this.buttonDrillMemberVisibleB = buttonDrillMemberVisibleB;
	}
	public Boolean getButtonDrillPositionVisibleB() {
		return buttonDrillPositionVisibleB;
	}
	public void setButtonDrillPositionVisibleB(Boolean buttonDrillPositionVisibleB) {
		this.buttonDrillPositionVisibleB = buttonDrillPositionVisibleB;
	}
	public Boolean getButtonDrillReplaceVisibleB() {
		return buttonDrillReplaceVisibleB;
	}
	public void setButtonDrillReplaceVisibleB(Boolean buttonDrillReplaceVisibleB) {
		this.buttonDrillReplaceVisibleB = buttonDrillReplaceVisibleB;
	}
	public Boolean getButtonDrillThroughVisibleB() {
		return buttonDrillThroughVisibleB;
	}
	public void setButtonDrillThroughVisibleB(Boolean buttonDrillThroughVisibleB) {
		this.buttonDrillThroughVisibleB = buttonDrillThroughVisibleB;
	}
	public Boolean getButtonShowChartVisibleB() {
		return buttonShowChartVisibleB;
	}
	public void setButtonShowChartVisibleB(Boolean buttonShowChartVisibleB) {
		this.buttonShowChartVisibleB = buttonShowChartVisibleB;
	}
	public Boolean getButtonConfigureChartVisibleB() {
		return buttonConfigureChartVisibleB;
	}
	public void setButtonConfigureChartVisibleB(Boolean buttonConfigureChartVisibleB) {
		this.buttonConfigureChartVisibleB = buttonConfigureChartVisibleB;
	}
	public Boolean getButtonConfigurePrintVisibleB() {
		return buttonConfigurePrintVisibleB;
	}
	public void setButtonConfigurePrintVisibleB(Boolean buttonConfigurePrintVisibleB) {
		this.buttonConfigurePrintVisibleB = buttonConfigurePrintVisibleB;
	}
	public Boolean getButtonFlushCacheVisibleB() {
		return buttonFlushCacheVisibleB;
	}
	public void setButtonFlushCacheVisibleB(Boolean buttonFlushCacheVisibleB) {
		this.buttonFlushCacheVisibleB = buttonFlushCacheVisibleB;
	}
	public Boolean getButtonSaveAnalysisVisibleB() {
		return buttonSaveAnalysisVisibleB;
	}
	public void setButtonSaveAnalysisVisibleB(Boolean buttonSaveAnalysisVisibleB) {
		this.buttonSaveAnalysisVisibleB = buttonSaveAnalysisVisibleB;
	}
	
	public void setValuesFromTemplate(Document document){
		//Check for Toolbar Configuration
		String buttonCubeVisible = document.valueOf("//olap/TOOLBAR/BUTTON_CUBE/@visible");
		if(buttonCubeVisible!=null && (buttonCubeVisible.equalsIgnoreCase("true") || buttonCubeVisible.equalsIgnoreCase("false"))){
			buttonCubeVisibleB = new Boolean(buttonCubeVisible);
		}

		String buttonMDXVisible = document.valueOf("//olap/TOOLBAR/BUTTON_MDX/@visible");
		if(buttonMDXVisible!=null && (buttonMDXVisible.equalsIgnoreCase("true") || buttonMDXVisible.equalsIgnoreCase("false"))){
			buttonMDXVisibleB = new Boolean(buttonMDXVisible);
		}
		
		String buttonOrderVisible = document.valueOf("//olap/TOOLBAR/BUTTON_ORDER/@visible");
		if(buttonOrderVisible!=null && (buttonOrderVisible.equalsIgnoreCase("true") || buttonOrderVisible.equalsIgnoreCase("false"))){
			buttonOrderVisibleB = new Boolean(buttonOrderVisible);
		}
		
		String buttonFatherMembVisible = document.valueOf("//olap/TOOLBAR/BUTTON_FATHER_MEMBERS/@visible");
		if(buttonFatherMembVisible!=null && (buttonFatherMembVisible.equalsIgnoreCase("true") || buttonFatherMembVisible.equalsIgnoreCase("false"))){
			buttonFatherMembVisibleB = new Boolean(buttonFatherMembVisible);
		}
		
		String buttonHideSpansVisible = document.valueOf("//olap/TOOLBAR/BUTTON_HIDE_SPANS/@visible");
		if(buttonHideSpansVisible!=null && (buttonHideSpansVisible.equalsIgnoreCase("true") || buttonHideSpansVisible.equalsIgnoreCase("false"))){
			buttonHideSpansVisibleB = new Boolean(buttonHideSpansVisible);
		}
		
		String buttonShowPropertiesVisible = document.valueOf("//olap/TOOLBAR/BUTTON_SHOW_PROPERTIES/@visible");
		if(buttonShowPropertiesVisible!=null && (buttonShowPropertiesVisible.equalsIgnoreCase("true") || buttonShowPropertiesVisible.equalsIgnoreCase("false"))){
			buttonShowPropertiesVisibleB = new Boolean(buttonShowPropertiesVisible);
		}
		
		String buttonHideEmptyVisible = document.valueOf("//olap/TOOLBAR/BUTTON_HIDE_EMPTY/@visible");
		if(buttonHideEmptyVisible!=null && (buttonHideEmptyVisible.equalsIgnoreCase("true") || buttonHideEmptyVisible.equalsIgnoreCase("false"))){
			buttonHideEmptyVisibleB = new Boolean(buttonHideEmptyVisible);
		}
		
		String buttonShiftAxisVisible = document.valueOf("//olap/TOOLBAR/BUTTON_SHIFT_AXIS/@visible");
		if(buttonShiftAxisVisible!=null && (buttonShiftAxisVisible.equalsIgnoreCase("true") || buttonShiftAxisVisible.equalsIgnoreCase("false"))){
			buttonShiftAxisVisibleB = new Boolean(buttonShiftAxisVisible);
		}
		
		String buttonDrillMemberVisible = document.valueOf("//olap/TOOLBAR/BUTTON_DRILL_MEMBER/@visible");
		if(buttonDrillMemberVisible!=null && (buttonDrillMemberVisible.equalsIgnoreCase("true") || buttonDrillMemberVisible.equalsIgnoreCase("false"))){
			buttonDrillMemberVisibleB = new Boolean(buttonDrillMemberVisible);
		}
		
		String buttonDrillPositionVisible = document.valueOf("//olap/TOOLBAR/BUTTON_DRILL_POSITION/@visible");
		if(buttonDrillPositionVisible!=null && (buttonDrillPositionVisible.equalsIgnoreCase("true") || buttonDrillPositionVisible.equalsIgnoreCase("false"))){
			buttonDrillPositionVisibleB = new Boolean(buttonDrillPositionVisible);
		}
		
		String buttonDrillReplaceVisible = document.valueOf("//olap/TOOLBAR/BUTTON_DRILL_REPLACE/@visible");
		if(buttonDrillReplaceVisible!=null && (buttonDrillReplaceVisible.equalsIgnoreCase("true") || buttonDrillReplaceVisible.equalsIgnoreCase("false"))){
			buttonDrillReplaceVisibleB = new Boolean(buttonDrillReplaceVisible);
		}
		
		String buttonDrillThroughVisible = document.valueOf("//olap/TOOLBAR/BUTTON_DRILL_THROUGH/@visible");
		if(buttonDrillThroughVisible!=null && (buttonDrillThroughVisible.equalsIgnoreCase("true") || buttonDrillThroughVisible.equalsIgnoreCase("false"))){
			buttonDrillThroughVisibleB = new Boolean(buttonDrillThroughVisible);
		}
		
		String buttonShowChartVisible = document.valueOf("//olap/TOOLBAR/BUTTON_SHOW_CHART/@visible");
		if(buttonShowChartVisible!=null && (buttonShowChartVisible.equalsIgnoreCase("true") || buttonShowChartVisible.equalsIgnoreCase("false"))){
			buttonShowChartVisibleB = new Boolean(buttonShowChartVisible);
		}
		
		String buttonConfigureChartVisible = document.valueOf("//olap/TOOLBAR/BUTTON_CONFIGURE_CHART/@visible");
		if(buttonConfigureChartVisible!=null && (buttonConfigureChartVisible.equalsIgnoreCase("true") || buttonConfigureChartVisible.equalsIgnoreCase("false"))){
			buttonConfigureChartVisibleB = new Boolean(buttonConfigureChartVisible);
		}
		
		String buttonConfigurePrintVisible = document.valueOf("//olap/TOOLBAR/BUTTON_CONFIGURE_PRINT/@visible");
		if(buttonConfigurePrintVisible!=null && (buttonConfigurePrintVisible.equalsIgnoreCase("true") || buttonConfigurePrintVisible.equalsIgnoreCase("false"))){
			buttonConfigurePrintVisibleB = new Boolean(buttonConfigurePrintVisible);
		}
		
		String buttonFlushCacheVisible = document.valueOf("//olap/TOOLBAR/BUTTON_FLUSH_CACHE/@visible");
		if(buttonFlushCacheVisible!=null && (buttonFlushCacheVisible.equalsIgnoreCase("true") || buttonFlushCacheVisible.equalsIgnoreCase("false"))){
			buttonFlushCacheVisibleB = new Boolean(buttonFlushCacheVisible);
		}
		
		String buttonSaveAnalysisVisible = document.valueOf("//olap/TOOLBAR/BUTTON_SAVE/@visible");
		if(buttonSaveAnalysisVisible!=null && (buttonSaveAnalysisVisible.equalsIgnoreCase("true") || buttonSaveAnalysisVisible.equalsIgnoreCase("false"))){
			buttonSaveAnalysisVisibleB = new Boolean(buttonSaveAnalysisVisible);
		}
	}
	
}
