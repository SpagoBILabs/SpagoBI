/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.chart.bo.charttypes.XYCharts;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spagobi.engines.chart.bo.charttypes.utils.ZRange;
import it.eng.spagobi.engines.chart.utils.DataSetAccessFunctions;
import it.eng.spagobi.engines.chart.utils.DatasetMap;

import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;



/**
 * @author gavardi
 */
public class SimpleBlockChart extends XYCharts {



	private static transient Logger logger=Logger.getLogger(SimpleBlockChart.class);

	Double xLowerBound = null;
	Double xUpperBound = null;
	Double yLowerBound = null;
	Double yUpperBound = null;
	Double scaleLowerBound = null;
	Double scaleUpperBound = null;
	
	Double minScaleValue;
	Double maxScaleValue;
	
	Double blockDimension;

	ZRange[] zRangeArray;
	
	boolean grayPaintScale = true;
	
	public static final String X_LOWER_BOUND = "x_lower_bound";
	public static final String X_UPPER_BOUND = "x_upper_bound";
	public static final String Y_LOWER_BOUND = "y_lower_bound";
	public static final String Y_UPPER_BOUND = "y_upper_bound";
	public static final String SCALE_LOWER_BOUND = "scale_lower_bound";
	public static final String SCALE_UPPER_BOUND = "scale_upper_bound";
	public static final String BLOCK_DIMENSION = "block_dimension";

	public void configureChart(SourceBean content) {
		logger.debug("IN");
		super.configureChart(content);

		try{
			xLowerBound = confParameters.get(X_LOWER_BOUND) != null ? 
				Double.valueOf(confParameters.get(X_LOWER_BOUND).toString()) : null;
			xUpperBound = confParameters.get(X_UPPER_BOUND) != null ? 
				Double.valueOf(confParameters.get(X_UPPER_BOUND).toString()) : null;
			yLowerBound = confParameters.get(Y_LOWER_BOUND) != null ? 
				Double.valueOf(confParameters.get(Y_LOWER_BOUND).toString()) : null;
			yUpperBound = confParameters.get(Y_UPPER_BOUND) != null ? 
				Double.valueOf(confParameters.get(Y_UPPER_BOUND).toString()) : null;
			scaleLowerBound = confParameters.get(SCALE_LOWER_BOUND) != null ? 
				Double.valueOf(confParameters.get(SCALE_LOWER_BOUND).toString()) : null;
			scaleUpperBound = confParameters.get(SCALE_UPPER_BOUND) != null ? 
				Double.valueOf(confParameters.get(SCALE_UPPER_BOUND).toString()) : null;
				
			logger.debug("Configuration found: xLowerBound = "+xLowerBound+
							"xUpperBound = "+xUpperBound+
							"yLowerBound = "+yLowerBound+
							"yUpperBound = "+yUpperBound+
							"scaleLowerBound = "+scaleLowerBound+
							"scaleUpperBound = "+scaleUpperBound);
		}
		catch (Exception e) {
			logger.error("Error in converting lower and upepr Bounds: use automatic Bounds ",e);
			xLowerBound = null;
			xUpperBound = null;
			yLowerBound = null;
			yUpperBound = null;
			scaleLowerBound = null;
			scaleUpperBound = null;
		}
		
		Object blocDimensionOb = confParameters.get(BLOCK_DIMENSION);
		if(blocDimensionOb != null){
			blockW = blocDimensionOb.toString();
			try{
				blockDimension = Double.valueOf(blocDimensionOb.toString());
			}
			catch (Exception e) {
				logger.error("Error in converting block_dimension parameter into a double",e);
				throw new RuntimeException("Error in converting block_dimension parameter into a double", e);
			}
		}
		logger.debug("BLock dimension by template is "+blockDimension);
		

		
		SourceBean zranges = (SourceBean)content.getAttribute("ZRANGES");

		if(zranges!=null && zranges.getAttributeAsList("RANGE") != null) {
			logger.debug("Z Ranges defined in template");
			List ranges = zranges.getAttributeAsList("RANGE");
			int rangesNum = ranges.size();
			zRangeArray= new ZRange[rangesNum];

			Iterator rangesIter = ranges.iterator();
			int num = 0;
			for (Iterator iterator = ranges.iterator(); iterator.hasNext();) {
				SourceBean range = (SourceBean) iterator.next();

				String label = (String)range.getAttribute("label");
				String colour = (String)range.getAttribute("colour");
				Color col = null;
				try{
					col=new Color(Integer.decode(colour).intValue());
				}
				catch (Exception e) {
					logger.error("Could not convert "+colour+" into a color",e);
					throw new RuntimeException("Could not convert "+colour+" into a color", e);
				}
				String value = (String)range.getAttribute("value");

				Double valueD = null;
				try{
					valueD = Double.valueOf(value); 					}
				catch (Exception e) {
					logger.error("Could not convert "+value+" into a double",e);
					throw new RuntimeException("Could not convert "+value+" into a double",e);
				}

				if(minScaleValue == null ) minScaleValue = valueD;
				if(maxScaleValue == null ) maxScaleValue = valueD;
				if(valueD<minScaleValue){
					minScaleValue = valueD;
				}
				if(valueD>maxScaleValue){
					maxScaleValue = valueD;
				}
				
				ZRange zRange = new ZRange(label, valueD, col);
				zRangeArray[num]= zRange;
				num++;
			}
			grayPaintScale = false;
		}
		else {
			logger.debug("Z Ranges not defined in template, use gray scale as default");
			grayPaintScale = true;
		}
				
		logger.debug("OUT");
	}





	public DatasetMap calculateValue() throws Exception {
		logger.debug("IN");
		String res=DataSetAccessFunctions.getDataSetResultFromId(profile, getData(),parametersObject);

		SourceBean sbRows=SourceBean.fromXMLString(res);
		List listAtts=sbRows.getAttributeAsList("ROW");


		DefaultXYZDataset dataset = new DefaultXYZDataset();
		int length = listAtts != null ? listAtts.size() : 0;

		double[] xvalues = new double[length];
		double[] yvalues = new double[length];        
		double[] zvalues = new double[length];

		double[][] data = new double[][] {xvalues, yvalues, zvalues};

		int xVal = 0;
		int yVal = 0;
		double zVal = 0;

		boolean first=true;
		int cont = 0;

		for (Iterator iterator = listAtts.iterator(); iterator.hasNext();) {
			SourceBean category = (SourceBean) iterator.next();
			List atts=category.getContainedAttributes();

			String nameP="";
			String value="";
			xVal = 0;
			yVal = 0;
			zVal = 0;
			for (Iterator iterator2 = atts.iterator(); iterator2.hasNext();) {
				SourceBeanAttribute object = (SourceBeanAttribute) iterator2.next();

				nameP=new String(object.getKey());
				value=new String((String)object.getValue());
				if(nameP.equalsIgnoreCase("x"))
				{						
					xVal = new Double(value).intValue();	
					xvalues[cont] = xVal;
				}
				if(nameP.equalsIgnoreCase("y"))
				{
					yVal = new Double(value).intValue();	
					yvalues[cont] = yVal;
				}
				if(nameP.equalsIgnoreCase("z"))
				{
					zVal = new Double(value).doubleValue();				
					zvalues[cont] = zVal;

					if(minScaleValue == null ) minScaleValue = zVal;
					if(maxScaleValue == null ) maxScaleValue = zVal;
					if(zVal<minScaleValue){
						minScaleValue = zVal;
					}
					if(zVal>maxScaleValue){
						maxScaleValue = zVal;
					}				
				}			    
			}
			cont++;	
		}


		dataset.addSeries("Series 1", data);

		//XYZDataset dataset = createDataset();

		DatasetMap datasets=new DatasetMap();
		datasets.addDataset("1",dataset);
		logger.debug("OUT");
		return datasets;
	}




	/**
	 * Creates a chart for the specified dataset.
	 * 
	 * @param dataset  the dataset.
	 * 
	 * @return A chart instance.
	 */
	public JFreeChart createChart(DatasetMap datasets) {
		logger.debug("IN");
		XYZDataset dataset=(XYZDataset)datasets.getDatasets().get("1");
		
		JFreeChart chart = null;
		
		NumberAxis xAxis = new NumberAxis(xLabel);
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		if(xLowerBound != null && xUpperBound != null){
			xAxis.setLowerBound(xLowerBound);
			xAxis.setUpperBound(xUpperBound);
		}
		else{
			xAxis.setAutoRange(true);
		}
		xAxis.setAxisLinePaint(Color.white);
		xAxis.setTickMarkPaint(Color.white);
		
        if(addLabelsStyle!=null && addLabelsStyle.getFont()!=null){
	        xAxis.setLabelFont(addLabelsStyle.getFont());
	        xAxis.setLabelPaint(addLabelsStyle.getColor());
        }

		NumberAxis yAxis = new NumberAxis(yLabel);
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		if(yLowerBound != null && yUpperBound != null){
			yAxis.setLowerBound(yLowerBound);
			yAxis.setUpperBound(yUpperBound);
		}
		else
			yAxis.setAutoRange(true);
		
		yAxis.setAxisLinePaint(Color.white);
		yAxis.setTickMarkPaint(Color.white);
		
        if(addLabelsStyle!=null && addLabelsStyle.getFont()!=null){
        	yAxis.setLabelFont(addLabelsStyle.getFont());
        	yAxis.setLabelPaint(addLabelsStyle.getColor());
        }
        
		
		XYBlockRenderer renderer = new XYBlockRenderer();
		
		PaintScale paintScale = null;

		if(grayPaintScale){
			paintScale = new GrayPaintScale(-2.0, 1.0);
		}
		else{
			if(scaleLowerBound!= null &&  scaleUpperBound != null)	
			{
				paintScale = new LookupPaintScale(scaleLowerBound, scaleUpperBound, Color.gray);  
			}
			else{
				paintScale = new LookupPaintScale(minScaleValue, maxScaleValue, Color.gray);  				
			}
				for (int i = 0; i < zRangeArray.length; i++) {
				ZRange zRange = zRangeArray[i];
				((LookupPaintScale)paintScale).add(zRange.getValue().doubleValue(), zRange.getColor());

			}
		}
		
		renderer.setPaintScale(paintScale);
		
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
        plot.setForegroundAlpha(0.66f);
		
		chart = new JFreeChart(plot);
        TextTitle title =setStyleTitle(name, styleTitle);
        chart.setTitle(title);
        if(subName!= null && !subName.equals("")){
			TextTitle subTitle =setStyleTitle(subName, styleSubTitle);
			chart.addSubtitle(subTitle);
		}
        
		chart.removeLegend();
		
		NumberAxis scaleAxis = new NumberAxis(zLabel);
		scaleAxis.setAxisLinePaint(Color.white);
		scaleAxis.setTickMarkPaint(Color.white);
		scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 7));
		if(scaleLowerBound != null && scaleUpperBound != null){
			scaleAxis.setLowerBound(scaleLowerBound);
			scaleAxis.setUpperBound(scaleUpperBound);
		}
		else
			scaleAxis.setAutoRange(true);
	
        if(addLabelsStyle!=null && addLabelsStyle.getFont()!=null){
        	scaleAxis.setLabelFont(addLabelsStyle.getFont());
        	scaleAxis.setLabelPaint(addLabelsStyle.getColor());
        }

        if(blockDimension != null){
        	renderer.setBlockWidth(blockDimension.doubleValue());
        	renderer.setBlockHeight(blockDimension.doubleValue());
        }
        
		PaintScaleLegend legend = new PaintScaleLegend(paintScale, 
				scaleAxis);
		legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		legend.setAxisOffset(5.0);
		legend.setMargin(new RectangleInsets(5, 5, 5, 5));
		legend.setFrame(new BlockBorder(Color.black));
		legend.setPadding(new RectangleInsets(10, 10, 10, 10));
		legend.setStripWidth(10);
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setBackgroundPaint(color);
		
		chart.addSubtitle(legend);
		
//		chart.setBackgroundPaint(new Color(180, 180, 250));	
		chart.setBackgroundPaint(color);	

		logger.debug("OUT");
		return chart;
	}    

}
