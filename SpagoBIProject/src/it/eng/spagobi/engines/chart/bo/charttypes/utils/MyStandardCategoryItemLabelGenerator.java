/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.chart.bo.charttypes.utils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

/**
 * 
 * @author gavardi
 * This class is used to generate additiona label over bars
 *
 */

public class MyStandardCategoryItemLabelGenerator extends StandardCategoryItemLabelGenerator {

	HashMap catSerLabel=null;
	private static transient Logger logger=Logger.getLogger(MyStandardCategoryItemLabelGenerator.class);


	public MyStandardCategoryItemLabelGenerator() {
		super();
		catSerLabel=new HashMap();
	}

	public MyStandardCategoryItemLabelGenerator(HashMap catSerMap) {
		super();

		catSerLabel=catSerMap;
	}




	public MyStandardCategoryItemLabelGenerator(HashMap catSerMap,String labelFormat,
			DateFormat formatter) {
		super(labelFormat, formatter);
		catSerLabel=catSerMap;
	}

	public MyStandardCategoryItemLabelGenerator(HashMap catSerMap,String labelFormat,
			NumberFormat formatter, NumberFormat percentFormatter) {
		super(labelFormat, formatter, percentFormatter);
		catSerLabel=catSerMap;	}

	public MyStandardCategoryItemLabelGenerator(HashMap catSerMap,String labelFormat,
			NumberFormat formatter) {
		super(labelFormat, formatter);
		catSerLabel=catSerMap;	}

	public String generateLabel(CategoryDataset dataset, int row, int column) {
		logger.debug("IN");
		String category=(String)dataset.getColumnKey(column);
		String serie=(String)dataset.getRowKey(row);

		String index=category+"-"+serie;

		String value="";
		if(catSerLabel.get(index)!=null && !catSerLabel.get(index).equals("")) 
		{
			logger.debug("set label");
			value=(String)catSerLabel.get(index);
		}

		logger.debug("OUT");

		return value;
	}

}
