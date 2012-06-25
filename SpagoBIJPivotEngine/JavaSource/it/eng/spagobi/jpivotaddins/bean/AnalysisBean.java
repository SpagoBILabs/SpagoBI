/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.bean;

import java.io.Serializable;

public class AnalysisBean implements Serializable {
	
	/*
    Although default values are already defined in EnhancedCharComponent,
      default values for properties are required while creating new analysis.
    */
   private String catalogUri = null;
   private String mdxQuery = null;
   private String connectionName = null;
   private String colorPaletteName = null;

   /*As there is no float type formatter in com.tonbeller.wcf.format.BasicTypes class, making
      foregroundAlpha as double type. But in library, this field of float type. So need to cast into
      float type while setting foreground alpha for chart*/
   private double foregroundAlpha = 1.0; //default to no transparency
   private boolean showPareto = false;
   private boolean showChart = true;
   private boolean showTable = true;

   //chart
   private String chartTitle = "";
   private int chartType = 1; //vertical bar
   private int chartHeight = 300;
   private int chartWidth = 500;
   private String fontName = "SansSerif";
   private int fontStyle = 1; //bold
   private int fontSize = 18;
   private boolean useChartSize = false; // chart size based on saved width and height.

   //legend
   private boolean showLegend = true;
   private String legendFontName = "SansSerif";
   private int legendFontStyle = 0; //plain
   private int legendFontSize = 10;
   private int legendPosition = 2; //right side of chart

   //slicer
   private boolean showSlicer = true;
   private int slicerPosition = 1; //top
   private int slicerAlignment = 4; //center
   private String slicerFontName = "SansSerif";
   private int slicerFontStyle = 0; //plain
   private int slicerFontSize = 12;

   //axes
   private String axisFontName = "SansSerif";
   private int axisFontStyle = 0; //plain
   private int axisFontSize = 12;
   private String horizAxisLabel = "";
   private String vertAxisLabel = "";
   private String axisTickFontName = "SansSerif";
   private int axisTickFontStyle = 0; //plain
   private int axisTickFontSize = 10;

   private boolean drillThroughEnabled = true;
   private int tickLabelRotate = 30; //30 degree

   private int bgColorR = 255;
   private int bgColorG = 255;
   private int bgColorB = 255;

   // show parents
   private boolean levelStyle = false;

   // hide spans
   private boolean hideSpans = false;

   public AnalysisBean() {

   }

   /**
    *
    * @return String
    */
   public String getCatalogUri() {
       return catalogUri;
   }

   /**
    *
    * @return String
    */
   public String getAxisFontName() {
       return axisFontName;
   }

   /**
    *
    * @return int
    */
   public int getAxisFontSize() {
       return axisFontSize;
   }

   /**
    *
    * @return int
    */
   public int getAxisFontStyle() {
       return axisFontStyle;
   }

   /*
     public String getCatalog() {
       return catalog;
     }
    */
   /**
    *
    * @return int
    */
   public int getChartHeight() {
       return chartHeight;
   }

   /**
    *
    * @return String
    */
   public String getChartTitle() {
       return chartTitle;
   }

   /**
    *
    * @return int
    */
   public int getChartType() {
       return chartType;
   }

   /**
    *
    * @return int
    */
   public int getChartWidth() {
       return chartWidth;
   }

   /**
    *
    * @return boolean
    */
   public boolean isDrillThroughEnabled() {
       return drillThroughEnabled;
   }

   /**
    *
    * @return String
    */
   public String getFontName() {
       return fontName;
   }

   /**
    *
    * @return int
    */
   public int getFontSize() {
       return fontSize;
   }

   /**
    *
    * @return int
    */
   public int getFontStyle() {
       return fontStyle;
   }

   /**
    *
    * @return String
    */
   public String getHorizAxisLabel() {
       return horizAxisLabel;
   }

   /**
    *
    * @return String
    */
   public String getLegendFontName() {
       return legendFontName;
   }

   /**
    *
    * @return int
    */
   public int getLegendFontSize() {
       return legendFontSize;
   }

   /**
    *
    * @return int
    */
   public int getLegendFontStyle() {
       return legendFontStyle;
   }

   /**
    *
    * @return int
    */
   public int getLegendPosition() {
       return legendPosition;
   }

   /**
    *
    * @return String
    */
   public String getMdxQuery() {
       return mdxQuery;
   }

   /**
    *
    * @return boolean
    */
   public boolean isShowLegend() {
       return showLegend;
   }

   /**
    *
    * @return boolean
    */
   public boolean isShowSlicer() {
       return showSlicer;
   }

   /**
    *
    * @return int
    */
   public int getSlicerAlignment() {
       return slicerAlignment;
   }

   /**
    *
    * @return String
    */
   public String getSlicerFontName() {
       return slicerFontName;
   }

   /**
    *
    * @return int
    */
   public int getSlicerFontSize() {
       return slicerFontSize;
   }

   /**
    *
    * @return int
    */
   public int getSlicerFontStyle() {
       return slicerFontStyle;
   }

   /**
    *
    * @return int
    */
   public int getSlicerPosition() {
       return slicerPosition;
   }

   /**
    *
    * @return int
    */
   public int getTickLabelRotate() {
       return tickLabelRotate;
   }

   /**
    *
    * @return String
    */
   public String getVertAxisLabel() {
       return vertAxisLabel;
   }

   /**
    *
    * @return String
    */
   public String getConnectionName() {
       return connectionName;
   }

   /*
        public String getXmlaUri() {
     return xmlaUri;
        }
    */
   /**
    *
    * @param analysisTitle String
    */
   public void setCatalogUri(String catalogUri) {
       this.catalogUri = catalogUri;
   }

   /**
    *
    * @param axisFontName String
    */
   public void setAxisFontName(String axisFontName) {
       this.axisFontName = axisFontName;
   }

   /**
    *
    * @param axisFontSize int
    */
   public void setAxisFontSize(int axisFontSize) {
       this.axisFontSize = axisFontSize;
   }

   /**
    *
    * @param axisFontStyle int
    */
   public void setAxisFontStyle(int axisFontStyle) {
       this.axisFontStyle = axisFontStyle;
   }

   /*
     public void setCatalog(String catalog) {
       this.catalog = catalog;
     }
    */
   /**
    *
    * @param chartHeight int
    */
   public void setChartHeight(int chartHeight) {
       this.chartHeight = chartHeight;
   }

   /**
    *
    * @param chartTitle String
    */
   public void setChartTitle(String chartTitle) {
       this.chartTitle = chartTitle;
   }

   /**
    *
    * @param chartType int
    */
   public void setChartType(int chartType) {
       this.chartType = chartType;
   }

   /**
    *
    * @param chartWidth int
    */
   public void setChartWidth(int chartWidth) {
       this.chartWidth = chartWidth;
   }

   /**
    *
    * @param drillThroughEnabled boolean
    */
   public void setDrillThroughEnabled(boolean drillThroughEnabled) {
       this.drillThroughEnabled = drillThroughEnabled;
   }

   /**
    *
    * @param fontName String
    */
   public void setFontName(String fontName) {
       this.fontName = fontName;
   }

   /**
    *
    * @param fontSize int
    */
   public void setFontSize(int fontSize) {
       this.fontSize = fontSize;
   }

   /**
    *
    * @param fontStyle int
    */
   public void setFontStyle(int fontStyle) {
       this.fontStyle = fontStyle;
   }

   /**
    *
    * @param horizAxisLabel String
    */
   public void setHorizAxisLabel(String horizAxisLabel) {
       this.horizAxisLabel = horizAxisLabel;
   }

   /**
    *
    * @param legendFontName String
    */
   public void setLegendFontName(String legendFontName) {
       this.legendFontName = legendFontName;
   }

   /**
    *
    * @param legendFontSize int
    */
   public void setLegendFontSize(int legendFontSize) {
       this.legendFontSize = legendFontSize;
   }

   /**
    *
    * @param legendFontStyle int
    */
   public void setLegendFontStyle(int legendFontStyle) {
       this.legendFontStyle = legendFontStyle;
   }

   /**
    *
    * @param legendPosition int
    */
   public void setLegendPosition(int legendPosition) {
       this.legendPosition = legendPosition;
   }

   /**
    *
    * @param mdxQuery String
    */
   public void setMdxQuery(String mdxQuery) {
       this.mdxQuery = mdxQuery;
   }

   /**
    *
    * @param showLegend boolean
    */
   public void setShowLegend(boolean showLegend) {
       this.showLegend = showLegend;
   }

   /**
    *
    * @param showSlicer boolean
    */
   public void setShowSlicer(boolean showSlicer) {
       this.showSlicer = showSlicer;
   }

   /**
    *
    * @param slicerAlignment int
    */
   public void setSlicerAlignment(int slicerAlignment) {
       this.slicerAlignment = slicerAlignment;
   }

   /**
    *
    * @param slicerFontName String
    */
   public void setSlicerFontName(String slicerFontName) {
       this.slicerFontName = slicerFontName;
   }

   /**
    *
    * @param slicerFontSize int
    */
   public void setSlicerFontSize(int slicerFontSize) {
       this.slicerFontSize = slicerFontSize;
   }

   /**
    *
    * @param slicerFontStyle int
    */
   public void setSlicerFontStyle(int slicerFontStyle) {
       this.slicerFontStyle = slicerFontStyle;
   }

   /**
    *
    * @param slicerPosition int
    */
   public void setSlicerPosition(int slicerPosition) {
       this.slicerPosition = slicerPosition;
   }

   /**
    *
    * @param tickLabelRotate int
    */
   public void setTickLabelRotate(int tickLabelRotate) {
       this.tickLabelRotate = tickLabelRotate;
   }

   /**
    *
    * @param vertAxisLabel String
    */
   public void setVertAxisLabel(String vertAxisLabel) {
       this.vertAxisLabel = vertAxisLabel;
   }

   /**
    *
    * @param dataSource String
    */
   public void setConnectionName(String connectionName) {
       this.connectionName = connectionName;
   }

   /*
        public void setXmlaUri(String xmlaUri) {
     this.xmlaUri = xmlaUri;
        }
    */

   /**
    * @return Returns the colorPaletteName.
    */
   public String getColorPaletteName() {
       return colorPaletteName;
   }

   /**
    * @param colorPaletteName The colorPaletteName to set.
    */
   public void setColorPaletteName(String colorPaletteName) {
       this.colorPaletteName = colorPaletteName;
   }

   /**
    * @return Returns the showPareto.
    */
   public boolean getShowPareto() {
       return showPareto;
   }

   /**
    *
    * @return String
    */
   public String getAxisTickFontName() {
       return axisTickFontName;
   }

   /**
    *
    * @return int
    */
   public int getAxisTickFontSize() {
       return axisTickFontSize;
   }

   /**
    *
    * @return int
    */
   public int getAxisTickFontStyle() {
       return axisTickFontStyle;
   }

   /**
    *
    * @return boolean
    */
   public boolean isShowChart() {
       return showChart;
   }

   /**
    *
    * @return boolean
    */
   public boolean isShowTable() {
       return showTable;
   }

   /**
    *
    * @return double
    */
   public double getForegroundAlpha() {
       return foregroundAlpha;
   }

   /**
    *
    * @return boolean
    */
   public boolean isUseChartSize() {
       return useChartSize;
   }

   /**
    *
    * @return int
    */
   public int getBgColorB() {
       return bgColorB;
   }

   /**
    *
    * @return int
    */
   public int getBgColorG() {
       return bgColorG;
   }

   /**
    *
    * @return int
    */
   public int getBgColorR() {
       return bgColorR;
   }

   /**
    *
    * @return boolean
    */
   public boolean isHideSpans() {
       return hideSpans;
   }

   /**
    *
    * @return boolean
    */
   public boolean isLevelStyle() {
       return levelStyle;
   }

   /**
    * @param showPareto The showPareto to set.
    */
   public void setShowPareto(boolean showPareto) {
       this.showPareto = showPareto;
   }

   /**
    *
    * @param axisTickFontName String
    */
   public void setAxisTickFontName(String axisTickFontName) {
       this.axisTickFontName = axisTickFontName;
   }

   /**
    *
    * @param axisTickFontSize int
    */
   public void setAxisTickFontSize(int axisTickFontSize) {
       this.axisTickFontSize = axisTickFontSize;
   }

   /**
    *
    * @param axisTickFontStyle int
    */
   public void setAxisTickFontStyle(int axisTickFontStyle) {
       this.axisTickFontStyle = axisTickFontStyle;
   }

   /**
    *
    * @param showChart boolean
    */
   public void setShowChart(boolean showChart) {
       this.showChart = showChart;
   }

   /**
    *
    * @param showTable boolean
    */
   public void setShowTable(boolean showTable) {
       this.showTable = showTable;
   }

   /**
    *
    * @param foregroundAlpha double
    */
   public void setForegroundAlpha(double foregroundAlpha) {
       this.foregroundAlpha = foregroundAlpha;
   }

   /**
    *
    * @param useChartSize boolean
    */
   public void setUseChartSize(boolean useChartSize) {
       this.useChartSize = useChartSize;
   }

   /**
    *
    * @param bgColorB int
    */
   public void setBgColorB(int bgColorB) {
       this.bgColorB = bgColorB;
   }

   /**
    *
    * @param bgColorG int
    */
   public void setBgColorG(int bgColorG) {
       this.bgColorG = bgColorG;
   }

   /**
    *
    * @param bgColorR int
    */
   public void setBgColorR(int bgColorR) {
       this.bgColorR = bgColorR;
   }

   /**
    *
    * @param hideSpans boolean
    */
   public void setHideSpans(boolean hideSpans) {
       this.hideSpans = hideSpans;
   }

   /**
    *
    * @param levelStyle boolean
    */
   public void setLevelStyle(boolean levelStyle) {
       this.levelStyle = levelStyle;
   }
   
}
