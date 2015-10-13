package it.eng.spagobi.tools.dataset.listener;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.IDataSetBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class DataStoreListenerOperatorTest extends TestCase {

	private DummyDataSet curr = new DummyDataSet();
	private DataStoreListenerOperator op = new DataStoreListenerOperator();

	private void assertEvent(DataStore prevStore, DataStore currStore, List<IRecord> added, List<IRecord> updated, List<IRecord> deleted, int idField) {

		MetaData metadata = new MetaData();
		metadata.setIdField(idField);
		if (prevStore != null) {
			prevStore.setMetaData(metadata);
		}
		currStore.setMetaData(metadata);

		DataStoreChangedEvent event = op.createEvent(prevStore, currStore, curr);
		assertRecords(added, event.getAdded());
		assertRecords(updated, event.getUpdated());
		assertRecords(deleted, event.getDeleted());
	}

	public void testCreateEvent() {
		//1
		DataStore store = new DataStore();
		List<IRecord> records = getRecords(new String[] { "1", "2", "3" }, new String[] { "3", "2", "3" }, new String[] { "2", "1", "3" });
		store.setRecords(records);

		assertEvent(null, store, records, new ArrayList<IRecord>(0),new ArrayList<IRecord>(0), -1);
		
		//2
		DataStore store2=new DataStore();
		List<IRecord> records2 = getRecords(new String[] { "1", "10", "15" }, new String[] { "3", "2", "3" }, new String[] { "2", "A", "3" });
		store2.setRecords(records2);
		List<IRecord> updated=getRecords(new String[] { "1", "10", "15" },new String[] { "2", "A", "3" });
		assertEvent(store, store2, new ArrayList<IRecord>(0),updated,new ArrayList<IRecord>(0),0);
		
		//3
		DataStore store3=new DataStore();
		records2 = getRecords( new String[] { "3", "2", "3" }, new String[] { "2", "A", "4" },new String[]{"Q","4","7"});
		store3.setRecords(records2);
		updated=getRecords(new String[] { "2", "A", "4" });
		List<IRecord> deleted=getRecords(new String[]{"1", "10", "15"});
		List<IRecord> added=getRecords(new String[]{"Q","4","7"});
		assertEvent(store2, store3, added,updated,deleted,0);
	}

	private void assertRecords(List<IRecord> exps, List<IRecord> acts) {
		
		assertEquals(exps.size(), acts.size());
		for (int i = 0; i < exps.size(); i++) {
			IRecord exp = exps.get(i);
			IRecord act = acts.get(i);
			assertEquals(exp.getFields().size(), act.getFields().size());

			for (int j = 0; j < exp.getFields().size(); j++) {
				IField expField = exp.getFieldAt(j);
				IField actField = act.getFieldAt(j);

				assertEquals(expField.getValue(), actField.getValue());
			}
		}
	}

	private static List<IRecord> getRecords(String[]... recs) {
		List<IRecord> res = new ArrayList<IRecord>(recs.length);
		for (String[] recV : recs) {
			Record rec = new Record();
			rec.setFields(getFields(recV));
			res.add(rec);
		}

		assert res.size() == recs.length;
		return res;
	}

	private static List<IField> getFields(String... values) {
		List<IField> res = new ArrayList<IField>(values.length);
		for (String v : values) {
			Field f = new Field();
			f.setValue(v);
			res.add(f);
		}

		assert res.size() == values.length;
		return res;
	}

	private class DummyDataSet implements IDataSet {

		private IDataStore store;

		public String getDsMetadata() {

			return null;
		}

		public void setDsMetadata(String dsMetadata) {

		}

		public IMetaData getMetadata() {

			return null;
		}

		public void setMetadata(IMetaData metadata) {

		}

		public int getId() {

			return 0;
		}

		public void setId(int id) {

		}

		public String getName() {

			return null;
		}

		public void setName(String name) {

		}

		public String getDescription() {

			return null;
		}

		public void setDescription(String description) {

		}

		public String getLabel() {

			return null;
		}

		public void setLabel(String label) {

		}

		public Integer getCategoryId() {

			return null;
		}

		public void setCategoryId(Integer categoryId) {

		}

		public String getCategoryCd() {

			return null;
		}

		public void setCategoryCd(String categoryCd) {

		}

		public String getDsType() {

			return null;
		}

		public void setDsType(String dsType) {

		}

		public String getConfiguration() {

			return null;
		}

		public void setConfiguration(String configuration) {

		}

		@SuppressWarnings("rawtypes")
		public Map getProperties() {

			return null;
		}

		@SuppressWarnings("rawtypes")
		public void setProperties(Map map) {

		}

		public String getOwner() {

			return null;
		}

		public void setOwner(String owner) {

		}

		public boolean isPublic() {

			return false;
		}

		public void setPublic(boolean isPublic) {

		}

		public String getUserIn() {

			return null;
		}

		public void setUserIn(String userIn) {

		}

		public Date getDateIn() {

			return null;
		}

		public void setDateIn(Date dateIn) {

		}

		public Integer getScopeId() {

			return null;
		}

		public void setScopeId(Integer scopeId) {

		}

		public String getScopeCd() {

			return null;
		}

		public void setScopeCd(String scopeCd) {

		}

		public String getParameters() {

			return null;
		}

		public void setParameters(String parameters) {

		}

		@SuppressWarnings("rawtypes")
		public Map getParamsMap() {

			return null;
		}

		@SuppressWarnings("rawtypes")
		public void setParamsMap(Map params) {

		}

		@SuppressWarnings("rawtypes")
		public Map getUserProfileAttributes() {

			return null;
		}

		public void setUserProfileAttributes(Map<String, Object> attributes) {

		}

		public void loadData() {

		}

		public void loadData(int offset, int fetchSize, int maxResults) {

		}

		public IDataStore getDataStore() {
			return this.store;
		}

		public boolean hasBehaviour(String behaviourId) {

			return false;
		}

		public Object getBehaviour(String behaviourId) {

			return null;
		}

		public void addBehaviour(IDataSetBehaviour behaviour) {

		}

		public Integer getTransformerId() {

			return null;
		}

		public void setTransformerId(Integer transformerId) {

		}

		public String getTransformerCd() {

			return null;
		}

		public void setTransformerCd(String transfomerCd) {

		}

		public String getPivotColumnName() {

			return null;
		}

		public void setPivotColumnName(String pivotColumnName) {

		}

		public String getPivotRowName() {

			return null;
		}

		public void setPivotRowName(String pivotRowName) {

		}

		public boolean isNumRows() {

			return false;
		}

		public void setNumRows(boolean numRows) {

		}

		public String getPivotColumnValue() {

			return null;
		}

		public void setPivotColumnValue(String pivotColumnValue) {

		}

		public boolean hasDataStoreTransformer() {

			return false;
		}

		public void removeDataStoreTransformer() {

		}

		public void setAbortOnOverflow(boolean abortOnOverflow) {

		}

		public void addBinding(String bindingName, Object bindingValue) {

		}

		public void setDataStoreTransformer(IDataStoreTransformer transformer) {

		}

		public IDataStoreTransformer getDataStoreTransformer() {

			return null;
		}

		public boolean isPersisted() {

			return false;
		}

		public void setPersisted(boolean persisted) {

		}

		public boolean isScheduled() {

			return false;
		}

		public void setScheduled(boolean scheduled) {

		}

		public boolean isFlatDataset() {

			return false;
		}

		public String getFlatTableName() {

			return null;
		}

		@SuppressWarnings("rawtypes")
		public List getNoActiveVersions() {

			return null;
		}

		@SuppressWarnings("rawtypes")
		public void setNoActiveVersions(List noActiveVersions) {

		}

		public String getPersistTableName() {

			return null;
		}

		public void setPersistTableName(String persistTableName) {

		}

		public SpagoBiDataSet toSpagoBiDataSet() {

			return null;
		}

		public IDataStore test() {

			return null;
		}

		public IDataStore test(int offset, int fetchSize, int maxResults) {

			return null;
		}

		public String getSignature() {

			return null;
		}

		public IDataSetTableDescriptor persist(String tableName, IDataSource dataSource) {

			return null;
		}

		public IDataStore getDomainValues(String fieldName, Integer start, Integer limit, IDataStoreFilter filter) {

			return null;
		}

		public IDataStore decode(IDataStore datastore) {

			return null;
		}

		public boolean isCalculateResultNumberOnLoadEnabled() {

			return false;
		}

		public void setCalculateResultNumberOnLoad(boolean enabled) {

		}

		public void setDataSource(IDataSource dataSource) {

		}

		public IDataSource getDataSource() {

			return null;
		}

		public String getTableNameForReading() {

			return null;
		}

		public IDataSource getDataSourceForReading() {

			return null;
		}

		public void setDataSourceForReading(IDataSource dataSource) {

		}

		public String getOrganization() {

			return null;
		}

		public void setOrganization(String organization) {

		}

		public IDataSource getDataSourceForWriting() {

			return null;
		}

		public void setDataSourceForWriting(IDataSource dataSource) {

		}

	}

}
