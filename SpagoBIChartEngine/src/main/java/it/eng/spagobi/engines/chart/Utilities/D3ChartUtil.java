package it.eng.spagobi.engines.chart.Utilities;

import java.util.Iterator;

import it.eng.spagobi.engines.chart.ChartEngineInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;

public class D3ChartUtil {

	public static String buildTreeMapDataSource(ChartEngineInstance ce)
	{
		IDataSet dataSet = ce.getDataSet();
		dataSet.loadData();
		Iterator it = dataSet.getDataStore().iterator();
		while (it.hasNext()) {
			IRecord record = (IRecord) it.next();
			record.getFields().get(0).getValue();
		}
		
		return null;
	}
}
