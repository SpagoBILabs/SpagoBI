package test;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.tools.dataset.bo.AbstractCustomDataSet;
import it.eng.spagobi.tools.dataset.bo.AbstractDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.IRecordMatcher;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyFakeDataset extends AbstractCustomDataSet {

	public void addBinding(String bindingName, Object bindingValue) {
		// TODO Auto-generated method stub

	}

	public IDataStore getDataStore() {
		// TODO Auto-generated method stub
		return null;
	}

	// scorro colonne dataset, se sono misure lascio così, se sono attributi e sono di tipo stringa appendo descirption

	public Map<String, List<String>> getDomainDescriptions(
			Map<String, List<String>> codes) {
		Map map = new HashMap<String, List<String>>();
		for (Iterator iterator = codes.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			List valuesList = codes.get(type);

			List l = new ArrayList<String>();
			for (Iterator iterator2 = l.iterator(); iterator2.hasNext();) {
				String s = (String) iterator2.next();
				l.add(s+ " description");
			}
			map.put(type, l);
		}

		return map;
	}

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

	public static void main(String[] args) {
		createStore();	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataSetTableDescriptor persist(String tableName,
			Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataStore decode(IDataStore datastore) {
		// TODO Auto-generated method stub
		return null;
	}











}
