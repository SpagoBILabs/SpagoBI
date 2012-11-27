/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.businness;

import java.util.List;

import it.eng.spagobi.engines.network.bean.Edge;
import it.eng.spagobi.engines.network.bean.INetwork;
import it.eng.spagobi.engines.network.bean.JSONNetwork;
import it.eng.spagobi.engines.network.bean.Node;
import it.eng.spagobi.engines.network.bean.XMLNetwork;
import it.eng.spagobi.engines.network.template.NetworkTemplate;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class NetworkBuilder {
	
	public static INetwork buildNetwork(IDataSet dataSet, NetworkTemplate template){
		INetwork net;
		String XMLNet = template.getNetworkDefinition().getNetworkXML();
		if(XMLNet==null){
			net = buildNetworkFromDataset(dataSet, template);
		}else{
			net = new XMLNetwork(XMLNet);
			
		}
		return net;
	}


	public static JSONNetwork buildNetworkFromDataset(IDataSet dataSet, NetworkTemplate template){
		dataSet.loadData();
		IDataStore dataStore = dataSet.getDataStore();
		
		IMetaData metaData = dataStore.getMetaData();
		IRecord record=null;
		Node src;
		Node dest;
		Edge edge;
		List<IField> fields;
		IField field;
		JSONNetwork net = new JSONNetwork(template.getNetworkOptions());
		for(int index = 0; index<dataStore.getRecordsCount() ; index++){
			record = dataStore.getRecordAt(index);
			fields = record.getFields();
			//for(int fieldIndex = 0; fieldIndex<fields.size() ; fieldIndex++){
			//	IFieldMetaData fieldMetaData = metaData.getFieldMeta(fieldIndex);
			//	field =  fields.get(fieldIndex);
				src = new Node((String)(fields.get(0)).getValue());
				dest = new Node((String)(fields.get(1)).getValue());
				edge = new Edge((String)(fields.get(2)).getValue(), src, dest);
			//}
				net.addEdge(edge);
				net.addNode(src);
				net.addNode(dest);
		}
		
		return net;
	}
	
}
