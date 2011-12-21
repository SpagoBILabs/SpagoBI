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

import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

public class LabelGenerator extends AbstractCategoryItemLabelGenerator 
implements CategoryItemLabelGenerator  {

	public LabelGenerator(String labelFormat, DateFormat formatter) {
		super(labelFormat, formatter);
		// TODO Auto-generated constructor stub
	}

	public LabelGenerator(String labelFormat, NumberFormat formatter,
			NumberFormat percentFormatter) {
		super(labelFormat, formatter, percentFormatter);
		// TODO Auto-generated constructor stub
	}

	public LabelGenerator(String labelFormat, NumberFormat formatter) {
		super(labelFormat, formatter);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */

	public String generateLabel(CategoryDataset arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

    public LabelGenerator() {
        super("", NumberFormat.getInstance());
    }
	
}
