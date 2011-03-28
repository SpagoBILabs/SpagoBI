package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.Locale;

import org.json.JSONObject;

public class DataSetJSONSerializer implements Serializer {

	public static final String ID = "id";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String LABEL = "label";
	private static final String USED_BY_N_DOCS = "usedByNDocs";
	
	private static final String CATEGORY_TYPE_CD = "catTypeCd";
		
	private static final String PARS = "pars";
	private static final String METADATA = "meta";
	
	private static final String DS_TYPE_CD = "dsTypeCd";
	private static final String FILE_NAME = "fileName";
	private static final String QUERY = "query";
	private static final String DATA_SOURCE = "dataSource";
	private static final String WS_ADDRESS = "wsAddress";
	private static final String WS_OPERATION = "wsOperation";
	private static final String SCRIPT = "script";
	private static final String SCRIPT_LANGUAGE = "scriptLanguage";
	private static final String JCLASS_NAME = "jclassName";
	
	private static final String TRASFORMER_TYPE_CD = "trasfTypeCd";
	private static final String PIVOT_COL_NAME = "pivotColName";
	private static final String PIVOT_COL_VALUE = "pivotColValue";
	private static final String PIVOT_ROW_NAME = "pivotRowName";
	private static final String PIVOT_IS_NUM_ROWS = "pivotIsNumRows";
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof IDataSet) ) {
			throw new SerializationException("DataSetJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			IDataSet ds = (IDataSet)o;
			result = new JSONObject();
			
			result.put(ID, ds.getId());
			result.put(LABEL, ds.getLabel() );	
			result.put(NAME, ds.getName() );
			result.put(DESCRIPTION, ds.getDescription() );
			Integer numObjAssociated = DAOFactory.getDataSetDAO().countBIObjAssociated(new Integer(ds.getId()));
			if(numObjAssociated!=null){
				result.put(USED_BY_N_DOCS, numObjAssociated );
			}
			
			result.put(CATEGORY_TYPE_CD, ds.getCategoryCd());
			
			result.put(PARS, ds.getParameters());	
			result.put(METADATA, ds.getDsMetadata());	
			
			result.put(DS_TYPE_CD, ds.getDsType());		

			if(ds instanceof FileDataSet){
				String fileName = ((FileDataSet)ds).getFileName();
				if(fileName!=null){
					result.put(FILE_NAME, fileName);				
				}
			}

			else if(ds instanceof JDBCDataSet){
				String query = ((JDBCDataSet)ds).getQuery().toString();
				if(query!=null){
					result.put(QUERY, query);
				}
				IDataSource dataSource = ((JDBCDataSet)ds).getDataSource();
				if(dataSource!=null){
					result.put(DATA_SOURCE, dataSource.getLabel());
				}				
			}

			else if(ds instanceof WebServiceDataSet){
				String ws_address = ((WebServiceDataSet)ds).getAddress();
				if(ws_address!=null){
					result.put(WS_ADDRESS, ws_address);
				}
				String ws_operation = ((WebServiceDataSet)ds).getOperation();
				if(ws_operation!=null){
					result.put(WS_OPERATION, ws_operation);
				}	
			}

			else if(ds instanceof ScriptDataSet){
				String script = ((ScriptDataSet)ds).getScript();
				if(script!=null){					
					result.put(SCRIPT, script);
				}
				String script_language = ((ScriptDataSet)ds).getLanguageScript();
				if(script_language!=null){
					result.put(SCRIPT_LANGUAGE, script_language);
				}
			}

			else if(ds instanceof JavaClassDataSet){
				String jClass = ((JavaClassDataSet)ds).getClassName();
				if(jClass!=null){
					result.put(JCLASS_NAME, jClass);
				}
			}
				
			result.put(TRASFORMER_TYPE_CD, ds.getTransformerCd());
			result.put(PIVOT_COL_NAME, ds.getPivotColumnName());	
			result.put(PIVOT_COL_VALUE, ds.getPivotColumnValue());	
			result.put(PIVOT_ROW_NAME,ds.getPivotRowName());	
			result.put(PIVOT_IS_NUM_ROWS,ds.isNumRows());	
	
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}