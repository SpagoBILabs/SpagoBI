/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.chart.bo.charttypes.XYCharts;

import it.eng.spagobi.engines.chart.utils.DatasetMap;

import java.awt.Color;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;



/**
 * @author chiarelli
 */
public class BlockChart extends XYCharts {
	
	String rootUrl=null;
	String mode="";
	String drillLabel="";
	HashMap drillParameter=null;
	String categoryUrlName="";
	String serieUrlname="";

	boolean cumulative=false;
	HashMap colorMap=null;  // keeps user selected colors
	boolean additionalLabels=false;
	boolean percentageValue=false;
	HashMap catSerLabels=null;
	
	private static transient Logger logger=Logger.getLogger(BlockChart.class);

	
    /**
     * Creates a chart for the specified dataset.
     * 
     * @param dataset  the dataset.
     * 
     * @return A chart instance.
     */
	public JFreeChart createChart(DatasetMap datasets) {
    	XYZDataset dataset=(XYZDataset)datasets.getDatasets().get("1");
    	//Creates the xAxis with its label and style
        NumberAxis xAxis = new NumberAxis(xLabel);
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        xAxis.setLabel(xLabel);
        if(addLabelsStyle!=null && addLabelsStyle.getFont()!=null){
	        xAxis.setLabelFont(addLabelsStyle.getFont());
	        xAxis.setLabelPaint(addLabelsStyle.getColor());
        }
        //Creates the yAxis with its label and style
        NumberAxis yAxis = new NumberAxis(yLabel);
       
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setInverted(false);
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        yAxis.setTickLabelsVisible(true);
        yAxis.setLabel(yLabel);
        if(addLabelsStyle!=null && addLabelsStyle.getFont()!=null){
        	yAxis.setLabelFont(addLabelsStyle.getFont());
        	yAxis.setLabelPaint(addLabelsStyle.getColor());
        }
       yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        Color outboundCol = new Color(Integer.decode(outboundColor).intValue());
        
        //Sets the graph paint scale and the legend paintscale
        LookupPaintScale paintScale = new LookupPaintScale(zvalues[0], (new Double(zrangeMax)).doubleValue(),outboundCol);
        LookupPaintScale legendPaintScale = new LookupPaintScale(0.5, 0.5+zvalues.length, outboundCol);
        
        for (int ke=0; ke<=(zvalues.length-1) ; ke++){
        	Double key =(new Double(zvalues[ke]));
        	Color temp =(Color)colorRangeMap.get(key);
        	paintScale.add(zvalues[ke],temp);
        	legendPaintScale.add(0.5+ke, temp);
        }     
        //Configures the renderer
        XYBlockRenderer renderer = new XYBlockRenderer();
        renderer.setPaintScale(paintScale);
        double blockHeight =	(new Double(blockH)).doubleValue();
        double blockWidth =	(new Double(blockW)).doubleValue();
        renderer.setBlockWidth(blockWidth);
        renderer.setBlockHeight(blockHeight);
        
        //configures the plot with title, subtitle, axis ecc.
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
        plot.setDomainCrosshairPaint(Color.black);
        
        plot.setForegroundAlpha(0.66f);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
        JFreeChart chart = new JFreeChart(plot);
        TextTitle title =setStyleTitle(name, styleTitle);
        chart.setTitle(title);
        if(subName!= null && !subName.equals("")){
			TextTitle subTitle =setStyleTitle(subName, styleSubTitle);
			chart.addSubtitle(subTitle);
		}
        chart.removeLegend();
        chart.setBackgroundPaint(Color.white);
        
        //Sets legend labels
        SymbolAxis scaleAxis = new SymbolAxis(null,legendLabels);
        scaleAxis.setRange(0.5, 0.5+zvalues.length);
        scaleAxis.setPlot(new PiePlot());
        scaleAxis.setGridBandsVisible(false);
        scaleAxis.setLabel(zLabel);
        //scaleAxis.setLabelAngle(3.14/2);
        scaleAxis.setLabelFont(addLabelsStyle.getFont());
        scaleAxis.setLabelPaint(addLabelsStyle.getColor());
      
        //draws legend as chart subtitle
        PaintScaleLegend psl = new PaintScaleLegend(legendPaintScale, scaleAxis);
        psl.setAxisOffset(2.0);
        psl.setPosition(RectangleEdge.RIGHT);
        psl.setMargin(new RectangleInsets(5, 1, 5, 1));        
        chart.addSubtitle(psl);
        
        if(yLabels!=null){
	        //Sets y legend labels
	        LookupPaintScale legendPaintScale2 = new LookupPaintScale(0, (yLabels.length-1), Color.white);
	        
	        for (int ke=0; ke<yLabels.length ; ke++){
	        	Color temp =Color.white;
	        	legendPaintScale2.add(1+ke, temp);
	        } 
	        
	        SymbolAxis scaleAxis2 = new SymbolAxis(null,yLabels);
	        scaleAxis2.setRange(0, (yLabels.length-1));
	        scaleAxis2.setPlot(new PiePlot());
	        scaleAxis2.setGridBandsVisible(false);
	      
	        //draws legend as chart subtitle
	        PaintScaleLegend psl2 = new PaintScaleLegend(legendPaintScale2, scaleAxis2);
	        psl2.setAxisOffset(5.0);
	        psl2.setPosition(RectangleEdge.LEFT);
	        psl2.setMargin(new RectangleInsets(8, 1, 40, 1));   
	        psl2.setStripWidth(0);
	        psl2.setStripOutlineVisible(false);
	        chart.addSubtitle(psl2);
        }
        
        return chart;
    }    

}
