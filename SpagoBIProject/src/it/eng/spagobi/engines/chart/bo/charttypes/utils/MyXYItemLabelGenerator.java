package it.eng.spagobi.engines.chart.bo.charttypes.utils;


import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

public class MyXYItemLabelGenerator extends StandardXYItemLabelGenerator{

	@Override
	public String generateLabel(XYDataset dataset, int series, int item) {
		// TODO Auto-generated method stub
		return "X";
	}

	
	
}
