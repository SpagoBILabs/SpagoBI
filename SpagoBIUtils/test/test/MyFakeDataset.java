package test;

import it.eng.spagobi.tools.dataset.bo.AbstractCustomDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.SelectableFieldsBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.dataset.persist.temporarytable.DatasetTemporaryTableUtils;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class MyFakeDataset extends AbstractCustomDataSet {
	
	 public static transient Logger logger = Logger.getLogger(MyFakeDataset.class);

	public IDataStore getDomainValues(String fieldName, Integer start,
			Integer limit, IDataStoreFilter filter) {
		IDataStore ds = createStore();

		int intex = ds.getMetaData().getFieldIndex(fieldName);
		List list =ds.getFieldValues(intex);
		Class cc = ds.getMetaData().getFieldType(intex);
		
		
		
		IDataStore dsS = new DataStore();
		dsS.getMetaData().addFiedMeta(new FieldMetadata(fieldName, cc));
		int intexSS = dsS.getMetaData().getFieldIndex(fieldName);
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			IRecord rec = new Record();
			IField f = new Field(object);
			rec.appendField(f);
			dsS.appendRecord(rec);
		}
		
		return dsS;
	}


	public IDataStore test() {


		return createStore();
	}




	public IDataStore test(int offset, int fetchSize, int maxResults) {
		// TODO Auto-generated method stub
		return createStore();
	}


	static public IDataStore createStore(){
		IDataStore dataStore = new DataStore();


		String[] enti = new String[]{"Ente1", "Ente2", "Ente3", "Ente4", "Ente5", "Ente6", "Ente7", "Ente8"}; 
		Integer[] anni = new Integer[]{2000, 2000, 2000, 2000, 2001, 2001, 2001, 2001}; 

		Integer[] spese = new Integer[]{107, 201, 0, 20, 241, 301, 20, 21};
		Integer[]  guadagni= new Integer[]{407, 301, 50, 60, 261, 331, 420, 621};


		IMetaData meta = dataStore.getMetaData();
		meta.addFiedMeta(new FieldMetadata("anno", Integer.class));
		meta.addFiedMeta(new FieldMetadata("ente", String.class));
		meta.addFiedMeta(new FieldMetadata("spesa", Integer.class));
		meta.addFiedMeta(new FieldMetadata("guadagno", Integer.class));

		IField f = null;
		IRecord rec = null;
		List listFields = null;

		for (int i = 0; i < enti.length; i++) {
			listFields = new ArrayList();
			String ente = enti[i];
			Integer anno = anni[i];
			Integer spesa = spese[i];
			Integer guadagno = guadagni[i];

			f = new Field(); f.setValue(anno); listFields.add(f);
			f = new Field(); f.setValue(ente); listFields.add(f);
			f = new Field(); f.setValue(spesa); listFields.add(f);
			f = new Field(); f.setValue(guadagno); listFields.add(f);

			rec = new Record();
			rec.setFields(listFields);
			dataStore.appendRecord(rec);
		}

		print(dataStore);

		return dataStore;
	}


	static void print(IDataStore dstore){


		for(int i = 0; i<dstore.getRecordsCount(); i++){
			IRecord rec = dstore.getRecordAt(i);
			String print = "";

			List fields = rec.getFields();
			for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
				IField f = (IField) iterator.next();
				print+=f.getValue().toString();
				print += " ";
			}
			System.out.println(print);			
		}

	}

	@Override
	public String getSignature() {
		logger.debug("IN");
		
		StringBuffer buffer = new StringBuffer();
		
		// considero i SelectableFieldsBehaviour
		SelectableFieldsBehaviour sfb = (SelectableFieldsBehaviour) this.getBehaviour(SelectableFieldsBehaviour.ID);
		List<String> fields = sfb.getSelectedFields();
		Collections.sort(fields); // li ordino in modo che l'ordine con cui vengono impostati sia irrilevante
		String result = join(fields, ",");
		buffer.append("SelectedFields:" + result + ";");
		
		// considero i FilteringBehaviour
		FilteringBehaviour fb = (FilteringBehaviour) this.getBehaviour(FilteringBehaviour.ID);
		Map<String, List<String>> filters = fb.getFilters();
		Set<String> keys = filters.keySet();
		List<String> keysList = new ArrayList<String>();
		keysList.addAll(keys);
		Collections.sort(keysList); // li ordino in modo che l'ordine con cui vengono impostati sia irrilevante
		Iterator<String> it = keysList.iterator();
		buffer.append("Filters:");
		while (it.hasNext()) {
			String aKey = it.next();
			List<String> filterValues = filters.get(aKey);
			Collections.sort(filterValues);  // li ordino in modo che l'ordine con cui vengono impostati sia irrilevante
			buffer.append(aKey + "=" + join(filterValues, ",") + ";");
		}
		
		// considero i driver analitici anno ed ente
		buffer.append("Analytical drivers:");
		String yearStr = (String) this.getParamsMap().get("anno");
		String[] years = yearStr.split(","); // i valori sono separati da virgola
		Arrays.sort(years); // li ordino in modo che l'ordine con cui vengono impostati sia irrilevante
		buffer.append("anno=" + years + ";");
		
		String enteStr = (String) this.getParamsMap().get("ente");
		String[] ente = enteStr.split(","); // i valori sono separati da virgola
		Arrays.sort(ente); // li ordino in modo che l'ordine con cui vengono impostati sia irrilevante
		buffer.append("ente=" + ente + ";");
		
		String toReturn = buffer.toString();
		
		logger.debug("OUT: " + toReturn);
		
		return toReturn;
	}
	
	static public String join(Collection<String> list, String conjunction) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String item : list) {
			if (first)
				first = false;
			else
				sb.append(conjunction);
			sb.append(item);
		}
		return sb.toString();
	}

	@Override
	public IDataSetTableDescriptor persist(String tableName,
			Connection connection) {
		IDataSetTableDescriptor toReturn = DatasetTemporaryTableUtils.createTemporaryTable(connection, this.getMetadata(), tableName);
		this.populateTable(connection, this.getMetadata(), tableName);
		return toReturn;
	}

	private void populateTable(Connection conn, IMetaData meta, String tableName) {
		logger.debug("IN");

		Statement st = null;
		String query = null;

		try {
			InsertCommand insertCommand = new InsertCommand(meta, tableName);

			IDataStore datastore = createStore();
			
			Iterator it = datastore.iterator();
			while (it.hasNext()) {
				IRecord record = (IRecord) it.next();
				
				insertCommand.setRecord(record);
				
				// after built columns create SQL Query
				query = insertCommand.createSQLQuery();
				System.out.println(query);
				// execute 
				st = conn.createStatement();
				st.execute(query);

			}
		} catch (SQLException e) {
			logger.error("Error in excuting statement " + query, e);
			throw new SpagoBIRuntimeException("Error creating temporary table", e);
		}
		finally {
			try {
				if ( st != null ) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("could not free resources ", e);
			}
		}
		logger.debug("OUT");
		
	}


	public Map<String, List<String>> getDomainDescriptions(
			Map<String, List<String>> codes) {
		Map map = new HashMap<String, List<String>>();
		for (Iterator iterator = codes.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			List valuesList = codes.get(type);

			List l = new ArrayList<String>();
			for (Iterator iterator2 = valuesList.iterator(); iterator2.hasNext();) {
				String s = (String) iterator2.next();
				l.add(s+ " description");
			}
			map.put(type, l);
		}

		return map;
	}


}
