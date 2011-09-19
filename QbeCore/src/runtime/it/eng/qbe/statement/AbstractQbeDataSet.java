package it.eng.qbe.statement;

import it.eng.qbe.query.CalculatedSelectField;
import it.eng.qbe.query.DataMartSelectField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.spagobi.tools.dataset.bo.AbstractDataSet;
import it.eng.spagobi.tools.dataset.bo.DataSetVariable;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.MetaData;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public abstract class AbstractQbeDataSet extends AbstractDataSet {


	protected IStatement statement;
	protected IDataStore dataStore;
	protected boolean abortOnOverflow;	
	protected Map bindings;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(AbstractQbeDataSet.class);
    
	
	public AbstractQbeDataSet(IStatement statement) {
		setStatement(statement);
		bindings = new HashMap();
	}

	public IDataStore getDataStore() {
		return dataStore;
	}

	private MetaData getDataStoreMeta(Query query) {
		MetaData dataStoreMeta;
		ISelectField queryFiled;
		FieldMetadata dataStoreFieldMeta;
		
		dataStoreMeta = new MetaData();
		
		Iterator fieldsIterator = query.getSelectFields(true).iterator();
		while(fieldsIterator.hasNext()) {
			queryFiled = (ISelectField)fieldsIterator.next();
			
			dataStoreFieldMeta = new FieldMetadata();
			dataStoreFieldMeta.setAlias( queryFiled.getAlias() );
			if(queryFiled.isDataMartField()) {
				DataMartSelectField dataMartSelectField = (DataMartSelectField) queryFiled;
				dataStoreFieldMeta.setName( ((DataMartSelectField)queryFiled).getUniqueName() );
				dataStoreFieldMeta.setProperty("calculated", new Boolean(false));
				dataStoreFieldMeta.setProperty("uniqueName", dataMartSelectField.getUniqueName());
				dataStoreFieldMeta.setType(Object.class);
				String format = dataMartSelectField.getPattern();
				if (format != null && !format.trim().equals("")) {
					dataStoreFieldMeta.setProperty("format", format);
				}
			} else if(queryFiled.isCalculatedField()){
				CalculatedSelectField claculatedQueryField = (CalculatedSelectField)queryFiled;
				dataStoreFieldMeta.setName(claculatedQueryField.getAlias());
				dataStoreFieldMeta.setProperty("calculated", new Boolean(true));	
				// FIXME also calculated field must have uniquename for uniformity
				dataStoreFieldMeta.setProperty("uniqueName", claculatedQueryField.getAlias());
				DataSetVariable variable = new DataSetVariable(claculatedQueryField.getAlias(), claculatedQueryField.getType(), claculatedQueryField.getExpression());
				dataStoreFieldMeta.setProperty("variable", variable);	
				dataStoreFieldMeta.setType( variable.getTypeClass() );	
				
			} else if(queryFiled.isInLineCalculatedField()){
				InLineCalculatedSelectField claculatedQueryField = (InLineCalculatedSelectField)queryFiled;
				dataStoreFieldMeta.setName(claculatedQueryField.getAlias());
				dataStoreFieldMeta.setProperty("calculated", new Boolean(false));	
				// FIXME also calculated field must have uniquename for uniformity
				dataStoreFieldMeta.setProperty("uniqueName", claculatedQueryField.getAlias());
				DataSetVariable variable = new DataSetVariable(claculatedQueryField.getAlias(), claculatedQueryField.getType(), claculatedQueryField.getExpression());
				dataStoreFieldMeta.setProperty("variable", variable);	
				dataStoreFieldMeta.setType( variable.getTypeClass() );	
				
			}
			dataStoreFieldMeta.setProperty("visible", new Boolean(queryFiled.isVisible()));	
			
			dataStoreMeta.addFiedMeta(dataStoreFieldMeta);
		}
		
		return dataStoreMeta;
	}
	
	
	
	protected DataStore toDataStore(List result) {
		DataStore dataStore;
		MetaData dataStoreMeta;
		Object[] row;
	
		dataStore = new DataStore();
		dataStoreMeta = getDataStoreMeta( statement.getQuery() );
		dataStore.setMetaData(dataStoreMeta);
		
		Iterator it = result.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			
		    if (!(o instanceof Object[])){
		    	row = new Object[1];
		    	row[0] = o == null? "": o;
		    }else{
		    	row = (Object[])o;
		    }
		    
		    
		    IRecord record = new Record(dataStore);
		    for(int i = 0,  j = 0; i < dataStoreMeta.getFieldCount(); i++) {
				IFieldMetaData fieldMeta = dataStoreMeta.getFieldMeta(i);
				Boolean calculated = (Boolean)fieldMeta.getProperty("calculated");
				if(calculated.booleanValue() == false) {
					Assert.assertTrue(j < row.length, "Impossible to read field [" + fieldMeta.getName() + "] from resultset");
					record.appendField( new Field( row[j] ) );
					if(row[j] != null) fieldMeta.setType(row[j].getClass());
					j++;					
				} else {
					DataSetVariable variable = (DataSetVariable)fieldMeta.getProperty("variable");
					if(variable.getResetType() == DataSetVariable.RESET_TYPE_RECORD) {
						variable.reset();
					}
					
					record.appendField( new Field( variable.getValue()) );
					if(variable.getValue() != null)  fieldMeta.setType(variable.getValue().getClass());
				}
			}
		    
		    processCalculatedFields(record, dataStore);
		    dataStore.appendRecord(record);
		}
		
		return dataStore;
	}
	
	private void processCalculatedFields(IRecord record, IDataStore dataStore) {
		IMetaData dataStoreMeta;
		List calculatedFieldsMeta;
		
		dataStoreMeta = dataStore.getMetaData();
		calculatedFieldsMeta = dataStoreMeta.findFieldMeta("calculated", Boolean.TRUE);
		for(int i = 0; i < calculatedFieldsMeta.size(); i++) {
			IFieldMetaData fieldMeta = (IFieldMetaData)calculatedFieldsMeta.get(i);
			DataSetVariable variable = (DataSetVariable)fieldMeta.getProperty("variable");
			
			ScriptEngineManager scriptManager = new ScriptEngineManager();
			ScriptEngine groovyScriptEngine = scriptManager.getEngineByName("groovy");
			
			
			// handle bindings 
			// ... static bindings first
			Iterator it = bindings.keySet().iterator();
			while(it.hasNext()) {
				String bindingName = (String)it.next();
				Object bindingValue = bindings.get(bindingName);
				groovyScriptEngine.put(bindingName, bindingValue);
			}
			
			// ... then runtime bindings
			Map qFields = new HashMap();
			Map dmFields = new HashMap();
			Object[] columns = new Object[dataStoreMeta.getFieldCount()];
			for(int j = 0; j < dataStoreMeta.getFieldCount(); j++) {
				qFields.put(dataStoreMeta.getFieldMeta(j).getAlias(), record.getFieldAt(j).getValue());
				dmFields.put(dataStoreMeta.getFieldMeta(j).getProperty("uniqueName"), record.getFieldAt(j).getValue());
				columns[j] = record.getFieldAt(j).getValue();
			}
			
			groovyScriptEngine.put("qFields", qFields); // key = alias
			groovyScriptEngine.put("dmFields", dmFields); // key = id
			groovyScriptEngine.put("fields", qFields); // default key = alias
			groovyScriptEngine.put("columns", columns); // key = col-index
			
			// show time
			Object calculatedValue = null;
			try {
				calculatedValue = groovyScriptEngine.eval(variable.getExpression());
				
			} catch (ScriptException ex) {
				calculatedValue = "NA";
			    ex.printStackTrace();
			}	
			
			//logger.debug("Field [" + fieldMeta.getName()+ "] is equals to [" + calculatedValue + "]");
			variable.setValue(calculatedValue);
			
			record.getFieldAt(dataStoreMeta.getFieldIndex(fieldMeta.getName())).setValue(variable.getValue());
		}
	}
	
	
	public void printInfo() {
		ScriptEngineManager mgr = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = mgr.getEngineFactories();
		
		for (ScriptEngineFactory factory: factories) {
		    System.out.println("ScriptEngineFactory Info");
		    String engName = factory.getEngineName();
		    String engVersion = factory.getEngineVersion();
		    String langName = factory.getLanguageName();
		    String langVersion = factory.getLanguageVersion();
		    System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
		    List<String> engNames = factory.getNames();
		    for(String name: engNames) {
		      System.out.printf("\tEngine Alias: %s\n", name);
		    }
		    System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);
		  }   
	}
	
	public IStatement getStatement() {
		return statement;
	}


	public void setStatement(IStatement statement) {
		this.statement = statement;
	}
	
	public boolean isAbortOnOverflow() {
		return abortOnOverflow;
	}


	public void setAbortOnOverflow(boolean abortOnOverflow) {
		this.abortOnOverflow = abortOnOverflow;
	}
	
	public void addBinding(String bindingName, Object bindingValue) {
		bindings.put(bindingName, bindingValue);
	}

	
}
