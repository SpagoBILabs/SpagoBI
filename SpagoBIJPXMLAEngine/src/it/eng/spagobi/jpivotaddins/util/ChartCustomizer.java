/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.util;

import it.eng.spagobi.jpivotaddins.bean.AnalysisBean;

import com.tonbeller.jpivot.chart.ChartComponent;

public class ChartCustomizer {

	public static void customizeChart(AnalysisBean analysis, ChartComponent chart) {
		
		chart.setChartHeight(analysis.getChartHeight());
		chart.setChartWidth(analysis.getChartWidth());
				
		// use persisted chart size is set to true, set chart size from restored
		// values.
//		if (analysis.isUseChartSize()) {
//			chart.setChartHeight(analysis.getChartHeight());
//			chart.setChartWidth(analysis.getChartWidth());
//
//			// otherwise set chart size based on screen size.
//		} else {
//
//			// set chart height and width as per client window height and width.
//			if (session.getAttribute("clientWindowWidth") != null) {
//				chart.setChartWidth(Integer.parseInt(session.getAttribute(
//						"clientWindowWidth").toString()) - 250);
//			}
//
//			if (session.getAttribute("clientWindowHeight") != null) {
//				chart.setChartHeight(Integer.parseInt(session.getAttribute(
//						"clientWindowHeight").toString()) / 2 + 150);
//			}
//
//		}

		// set color palette
		// List palette = (List) projectContext.getProject().getColorPalette(
		// projectContext.getProject().getDefaultPaletteName());
		// chart.setColorPalette(palette);

		// chart.setShowPareto(analysis.getShowPareto());
		// chart.setForegroundAlpha(analysis.getForegroundAlpha());

		chart.setChartTitle(analysis.getChartTitle());
		chart.setChartType(analysis.getChartType());
		chart.setFontName(analysis.getFontName());
		chart.setFontStyle(analysis.getFontStyle());
		chart.setFontSize(analysis.getFontSize());

		// legend
		chart.setShowLegend(analysis.isShowLegend());
		// if legend is visible, set properties
		if (analysis.isShowLegend() == true) {
			chart.setLegendFontName(analysis.getLegendFontName());
			chart.setLegendFontStyle(analysis.getLegendFontStyle());
			chart.setLegendFontSize(analysis.getLegendFontSize());
			chart.setLegendPosition(analysis.getLegendPosition());
		}

		// slicer
		chart.setShowSlicer(analysis.isShowSlicer());
		// if slicer is visible, set properties
		if (analysis.isShowSlicer() == true) {
			chart.setSlicerPosition(analysis.getSlicerPosition());
			chart.setSlicerAlignment(analysis.getSlicerAlignment());
			chart.setSlicerFontName(analysis.getSlicerFontName());
			chart.setSlicerFontStyle(analysis.getSlicerFontStyle());
			chart.setSlicerFontSize(analysis.getSlicerFontSize());
		}

		// axes
		chart.setAxisFontName(analysis.getAxisFontName());
		chart.setAxisFontStyle(analysis.getAxisFontStyle());
		chart.setAxisFontSize(analysis.getAxisFontSize());
		chart.setHorizAxisLabel(analysis.getHorizAxisLabel());
		chart.setVertAxisLabel(analysis.getVertAxisLabel());
		chart.setAxisTickFontName(analysis.getAxisTickFontName());
		chart.setAxisTickFontStyle(analysis.getAxisTickFontStyle());
		chart.setAxisTickFontSize(analysis.getAxisTickFontSize());

		chart.setDrillThroughEnabled(analysis.isDrillThroughEnabled());
		chart.setTickLabelRotate(analysis.getTickLabelRotate());

		// chart.setUseChartSize(analysis.isUseChartSize());

		// set chart visible status
		chart.setVisible(analysis.isShowChart());

		// background color
		chart.setBgColorB(analysis.getBgColorB());
		chart.setBgColorG(analysis.getBgColorG());
		chart.setBgColorR(analysis.getBgColorR());
	

	}
	
}
