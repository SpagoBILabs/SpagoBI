package it.eng.spagobi.chiron.serializer;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.bo.FileDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.JClassDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QbeDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QueryDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WSDataSetDetail;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataSetJSONSerializer implements Serializer {

	public static final String DS_ID = "dsId";
	public static final String VERSION_ID = "versId";
	public static final String VERSION_NUM = "versNum";	
	public static final String USER_IN = "userIn";
	public static final String TYPE = "type";
	public static final String DATE_IN = "dateIn";
	public static final String DS_OLD_VERSIONS = "dsVersions";
	
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
	
	private static final String QBE_DATA_SOURCE = "qbeDataSource";
	private static final String QBE_DATAMARTS = "qbeDatamarts";
	private static final String QBE_JSON_QUERY = "qbeJSONQuery";
	private static final String QBE_SQL_QUERY = "qbeSQLQuery";
	
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
			Integer dsId = ds.getDsId();
			result.put(ID, dsId);
			result.put(LABEL, ds.getLabel() );	
			result.put(NAME, ds.getName() );
			result.put(DESCRIPTION, ds.getDescription() );
			Integer numObjAssociated = DAOFactory.getDataSetDAO().countBIObjAssociated(new Integer(ds.getDsId()));
			if(numObjAssociated!=null){
				result.put(USED_BY_N_DOCS, numObjAssociated );
			}
			
			GuiDataSetDetail dsDetail = ds.getActiveDetail();
			
			result.put(CATEGORY_TYPE_CD, dsDetail.getCategoryCd());

			JSONArray parsListJSON = new JSONArray();
			String pars = dsDetail.getParameters();
			if(pars!=null && !pars.equals("")){
				SourceBean source = SourceBean.fromXMLString(pars);
				if(source!=null && source.getName().equals("PARAMETERSLIST")){
					List<SourceBean> rows = source.getAttributeAsList("ROWS.ROW");
					for(int i=0; i< rows.size(); i++){
						SourceBean row = rows.get(i);
						String name = (String)row.getAttribute("NAME");
						String type = (String)row.getAttribute("TYPE");
						JSONObject jsonPar = new JSONObject();
						jsonPar.put("name", name);
						jsonPar.put("type", type);
						parsListJSON.put(jsonPar);
					}				
				}
			}
			result.put(PARS, parsListJSON);	
			
			JSONArray metaListJSON = new JSONArray();
			String meta = dsDetail.getDsMetadata();
			if(meta!=null && !meta.equals("")){
				SourceBean source = SourceBean.fromXMLString(meta);
				if(source!=null && source.getName().equals("METADATALIST")){
					List<SourceBean> rows = source.getAttributeAsList("ROWS.ROW");
					for(int i=0; i< rows.size(); i++){
						SourceBean row = rows.get(i);
						String name = (String)row.getAttribute("NAME");
						String type = (String)row.getAttribute("TYPE");
						JSONObject jsonMeta = new JSONObject();
						jsonMeta.put("name", name);
						jsonMeta.put("type", type);
						metaListJSON.put(jsonMeta);
					}				
				}
			}
			result.put(METADATA, metaListJSON);	
			
			JSONArray versionsListJSON = new JSONArray();
			List<GuiDataSetDetail> nonActiveDetails = ds.getNonActiveDetails();
			if(nonActiveDetails!=null && !nonActiveDetails.isEmpty()){
				Iterator it = nonActiveDetails.iterator();
				while(it.hasNext()){
					GuiDataSetDetail tempDetail = (GuiDataSetDetail)it.next();
					String dsType = tempDetail.getDsType();
					String userIn = tempDetail.getUserIn();
					Integer dsVersionNum = tempDetail.getVersionNum();
					Integer dsVersionId = tempDetail.getDsHId();
					Date timeIn = tempDetail.getTimeIn();
					JSONObject jsonOldVersion = new JSONObject();
					jsonOldVersion.put(TYPE, dsType);
					jsonOldVersion.put(USER_IN, userIn);
					jsonOldVersion.put(VERSION_NUM, dsVersionNum);
					jsonOldVersion.put(VERSION_ID, dsVersionId);
					jsonOldVersion.put(DATE_IN, timeIn);
					jsonOldVersion.put(DS_ID, dsId);
					versionsListJSON.put(jsonOldVersion);
				}
			}
			result.put(DS_OLD_VERSIONS, versionsListJSON);	
			
			result.put(DS_TYPE_CD, dsDetail.getDsType());	
			
			result.put(USER_IN, dsDetail.getUserIn());
			result.put(VERSION_NUM, dsDetail.getVersionNum());
			result.put(VERSION_ID, dsDetail.getDsHId());
			result.put(DATE_IN, dsDetail.getTimeIn());

			if(dsDetail instanceof FileDataSetDetail){
				String fileName = ((FileDataSetDetail)dsDetail).getFileName();
				if(fileName!=null){
					result.put(FILE_NAME, fileName);				
				}
			}

			else if(dsDetail instanceof QueryDataSetDetail){
				if(((QueryDataSetDetail)dsDetail).getQuery()!=null){
					String query = ((QueryDataSetDetail)dsDetail).getQuery().toString();
					if(query!=null){
						result.put(QUERY, query);
					}
				}
				String dataSourceLabel = ((QueryDataSetDetail)dsDetail).getDataSourceLabel();
				if(dataSourceLabel!=null){
					result.put(DATA_SOURCE, dataSourceLabel);
				}				
			}
			
			else if(dsDetail instanceof QbeDataSetDetail) {
				QbeDataSetDetail aQbeDataSetDetail = (QbeDataSetDetail) dsDetail;
				result.put(QBE_SQL_QUERY, aQbeDataSetDetail.getSqlQuery());
				result.put(QBE_JSON_QUERY, aQbeDataSetDetail.getJsonQuery());
				result.put(QBE_DATA_SOURCE, aQbeDataSetDetail.getDataSourceLabel());
				result.put(QBE_DATAMARTS, aQbeDataSetDetail.getDatamarts());			
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