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
