package it.eng.spagobi.engines.chart.bo.charttypes.utils;

import org.apache.log4j.Logger;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

public class FilterZeroStandardCategoryItemLabelGenerator  extends StandardCategoryItemLabelGenerator {

	/**
	 * This class is used to generate value labels over bars and lines, while filtering 0 values.
	 * 
	 */
	
	private static transient Logger logger=Logger.getLogger(FilterZeroStandardCategoryItemLabelGenerator.class);

	@Override
	public String generateLabel(CategoryDataset dataset, int row, int column) {
		String result=super.generateLabel(dataset, row, column);
		// filter 0 or 0.0 values
		if(result.equalsIgnoreCase("0") || result.equalsIgnoreCase("0.0")){
			return null;
		}
		else return result;
		
	}

	

}
