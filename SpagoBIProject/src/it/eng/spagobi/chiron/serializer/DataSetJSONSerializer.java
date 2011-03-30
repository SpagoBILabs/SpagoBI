package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.bo.FileDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.JClassDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QueryDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WSDataSetDetail;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

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
		
		if( !(o instanceof GuiGenericDataSet) ) {
			throw new SerializationException("DataSetJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			GuiGenericDataSet ds = (GuiGenericDataSet)o;
			result = new JSONObject();
			
			result.put(ID, ds.getDsId());
			result.put(LABEL, ds.getLabel() );	
			result.put(NAME, ds.getName() );
			result.put(DESCRIPTION, ds.getDescription() );
			Integer numObjAssociated = DAOFactory.getDataSetDAO().countBIObjAssociated(new Integer(ds.getDsId()));
			if(numObjAssociated!=null){
				result.put(USED_BY_N_DOCS, numObjAssociated );
			}
			
			GuiDataSetDetail dsDetail = ds.getActiveDetail();
			
			result.put(CATEGORY_TYPE_CD, dsDetail.getCategoryCd());
			
			result.put(PARS, dsDetail.getParameters());	
			result.put(METADATA, dsDetail.getDsMetadata());	
			
			result.put(DS_TYPE_CD, dsDetail.getDsType());		

			if(dsDetail instanceof FileDataSetDetail){
				String fileName = ((FileDataSetDetail)dsDetail).getFileName();
				if(fileName!=null){
					result.put(FILE_NAME, fileName);				
				}
			}

			else if(dsDetail instanceof QueryDataSetDetail){
				String query = ((QueryDataSetDetail)dsDetail).getQuery().toString();
				if(query!=null){
					result.put(QUERY, query);
				}
				String dataSourceLabel = ((QueryDataSetDetail)dsDetail).getDataSourceLabel();
				if(dataSourceLabel!=null){
					result.put(DATA_SOURCE, dataSourceLabel);
				}				
			}

			else if(dsDetail instanceof WSDataSetDetail){
				String ws_address = ((WSDataSetDetail)dsDetail).getAddress();
				if(ws_address!=null){
					result.put(WS_ADDRESS, ws_address);
				}
				String ws_operation = ((WSDataSetDetail)dsDetail).getOperation();
				if(ws_operation!=null){
					result.put(WS_OPERATION, ws_operation);
				}	
			}

			else if(dsDetail instanceof ScriptDataSetDetail){
				String script = ((ScriptDataSetDetail)dsDetail).getScript();
				if(script!=null){					
					result.put(SCRIPT, script);
				}
				String script_language = ((ScriptDataSetDetail)dsDetail).getLanguageScript();
				if(script_language!=null){
					result.put(SCRIPT_LANGUAGE, script_language);
				}
			}

			else if(dsDetail instanceof JClassDataSetDetail){
				String jClass = ((JClassDataSetDetail)dsDetail).getJavaClassName();
				if(jClass!=null){
					result.put(JCLASS_NAME, jClass);
				}
			}
				
			result.put(TRASFORMER_TYPE_CD, dsDetail.getTransformerCd());
			result.put(PIVOT_COL_NAME, dsDetail.getPivotColumnName());	
			result.put(PIVOT_COL_VALUE, dsDetail.getPivotColumnValue());	
			result.put(PIVOT_ROW_NAME,dsDetail.getPivotRowName());	
			result.put(PIVOT_IS_NUM_ROWS,dsDetail.isNumRows());	
	
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}		
	  return result;
	}
}