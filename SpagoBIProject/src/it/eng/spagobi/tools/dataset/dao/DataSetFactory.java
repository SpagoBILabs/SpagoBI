/**
 * 
 */
package it.eng.spagobi.tools.dataset.dao;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.bo.CustomDataSet;
import it.eng.spagobi.tools.dataset.bo.CustomDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.FileDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JClassDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.QbeDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QueryDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WSDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.dataset.metadata.SbiCustomDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetHistory;
import it.eng.spagobi.tools.dataset.metadata.SbiFileDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiJClassDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQbeDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQueryDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiScriptDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiWSDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.DataSourceDAOHibImpl;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DataSetFactory {
	
	public static final String JDBC_DS_TYPE = "Query";
	public static final String FILE_DS_TYPE = "File";
	public static final String SCRIPT_DS_TYPE = "Script";
	public static final String JCLASS_DS_TYPE = "Java Class";
	public static final String WS_DS_TYPE = "Web Service";
	public static final String QBE_DS_TYPE = "Qbe";
	public static final String CUSTOM_DS_TYPE = "Custom";
	
	static private Logger logger = Logger.getLogger(DataSetFactory.class);
	
	public static GuiGenericDataSet toGuiGenericDataSet(SbiDataSetHistory sbiDataSetHistory) {		
		GuiGenericDataSet guiGenericDataSet;
		
		guiGenericDataSet = new GuiGenericDataSet();
		
		GuiDataSetDetail guiDataSetDetail = toGuiDataSetDetail(sbiDataSetHistory);
		guiGenericDataSet.setActiveDetail(guiDataSetDetail);

		if(sbiDataSetHistory.getSbiDsConfig()!=null){
			guiGenericDataSet.setDsId(sbiDataSetHistory.getSbiDsConfig().getDsId());
			guiGenericDataSet.setName(sbiDataSetHistory.getSbiDsConfig().getName());
			guiGenericDataSet.setLabel(sbiDataSetHistory.getSbiDsConfig().getLabel());
			guiGenericDataSet.setDescription(sbiDataSetHistory.getSbiDsConfig().getDescription());	
			
			guiGenericDataSet.setMetaVersion(sbiDataSetHistory.getMetaVersion());
			guiGenericDataSet.setUserIn(sbiDataSetHistory.getUserIn());
			guiGenericDataSet.setTimeIn(new Date());
			
			guiDataSetDetail.setDsId(sbiDataSetHistory.getSbiDsConfig().getDsId());

		}

		return guiGenericDataSet;
	}

	public static GuiDataSetDetail toGuiDataSetDetail(SbiDataSetHistory sbiDataSetHistory) {		
		GuiDataSetDetail guiDataSetDetail;
		
		guiDataSetDetail = null;
		
		if(sbiDataSetHistory instanceof SbiFileDataSet){		
			guiDataSetDetail = toGuiDataSetDetail( (SbiFileDataSet)sbiDataSetHistory );
		} else if(sbiDataSetHistory instanceof SbiQueryDataSet){			
			guiDataSetDetail = toGuiDataSetDetail( (SbiQueryDataSet)sbiDataSetHistory );
		} else if(sbiDataSetHistory instanceof SbiQbeDataSet){	
			guiDataSetDetail = toGuiDataSetDetail( (SbiQbeDataSet)sbiDataSetHistory );
		} else if(sbiDataSetHistory instanceof SbiWSDataSet){	
			guiDataSetDetail = toGuiDataSetDetail( (SbiWSDataSet)sbiDataSetHistory );
		} else if(sbiDataSetHistory instanceof SbiScriptDataSet){			
			guiDataSetDetail = toGuiDataSetDetail( (SbiScriptDataSet)sbiDataSetHistory );
		} else if(sbiDataSetHistory instanceof SbiJClassDataSet) {			
			guiDataSetDetail = toGuiDataSetDetail( (SbiJClassDataSet)sbiDataSetHistory );
		} else if(sbiDataSetHistory instanceof SbiCustomDataSet){			
			guiDataSetDetail = toGuiDataSetDetail( (SbiCustomDataSet)sbiDataSetHistory );
		}
		
		guiDataSetDetail.setDsHId(sbiDataSetHistory.getDsHId());
		guiDataSetDetail.setCategoryId((sbiDataSetHistory.getCategory()== null)? null:sbiDataSetHistory.getCategory().getValueId());
		guiDataSetDetail.setCategoryValueName((sbiDataSetHistory.getCategory()== null)? null:sbiDataSetHistory.getCategory().getValueNm());
		guiDataSetDetail.setTransformerId((sbiDataSetHistory.getTransformer()== null)? null:sbiDataSetHistory.getTransformer().getValueId());
		guiDataSetDetail.setTransformerCd((sbiDataSetHistory.getTransformer()== null)? null:sbiDataSetHistory.getTransformer().getValueCd());
		guiDataSetDetail.setPivotColumnName(sbiDataSetHistory.getPivotColumnName());
		guiDataSetDetail.setPivotRowName(sbiDataSetHistory.getPivotRowName());
		guiDataSetDetail.setPivotColumnValue(sbiDataSetHistory.getPivotColumnValue());
		guiDataSetDetail.setNumRows(sbiDataSetHistory.isNumRows());			
		guiDataSetDetail.setParameters(sbiDataSetHistory.getParameters());		
		guiDataSetDetail.setDsMetadata(sbiDataSetHistory.getDsMetadata());		
		guiDataSetDetail.setUserIn(sbiDataSetHistory.getUserIn());
		guiDataSetDetail.setTimeIn(sbiDataSetHistory.getTimeIn());
		guiDataSetDetail.setVersionNum(sbiDataSetHistory.getVersionNum());
		guiDataSetDetail.setSbiVersionIn(sbiDataSetHistory.getSbiVersionIn());
		guiDataSetDetail.setDsHId(sbiDataSetHistory.getDsHId());
		
		return guiDataSetDetail;
	}
	
	private static GuiDataSetDetail toGuiDataSetDetail(SbiFileDataSet sbiDataSetHistory) {
		FileDataSetDetail guiDataSetDetail = new FileDataSetDetail();
		guiDataSetDetail.setFileName(sbiDataSetHistory.getFileName());
		guiDataSetDetail.setDsType(FILE_DS_TYPE);
		return guiDataSetDetail;
	}
	
	private static GuiDataSetDetail toGuiDataSetDetail(SbiQueryDataSet sbiDataSetHistory) {
		QueryDataSetDetail guiDataSetDetail = new QueryDataSetDetail();
		guiDataSetDetail.setQuery(sbiDataSetHistory.getQuery());
		SbiDataSource sbiDataSource = sbiDataSetHistory.getDataSource();
		if(sbiDataSource!=null){
			String dataSourceLabel = sbiDataSource.getLabel();
			((QueryDataSetDetail)guiDataSetDetail).setDataSourceLabel(dataSourceLabel);
		}
		guiDataSetDetail.setDsType(JDBC_DS_TYPE);
		return guiDataSetDetail;
	}
	
	private static GuiDataSetDetail toGuiDataSetDetail(SbiQbeDataSet sbiDataSetHistory) {
		QbeDataSetDetail guiDataSetDetail = new QbeDataSetDetail();
		guiDataSetDetail.setSqlQuery(sbiDataSetHistory.getSqlQuery());
		guiDataSetDetail.setJsonQuery(sbiDataSetHistory.getJsonQuery());
		guiDataSetDetail.setDatamarts(sbiDataSetHistory.getDatamarts());
		SbiDataSource sbiDataSource = sbiDataSetHistory.getDataSource();
		if (sbiDataSource!=null){
			guiDataSetDetail.setDataSourceLabel(sbiDataSource.getLabel());
		}
		guiDataSetDetail.setDsType(QBE_DS_TYPE);
		return guiDataSetDetail;
	}
	
	private static GuiDataSetDetail toGuiDataSetDetail(SbiWSDataSet sbiDataSetHistory) {
		WSDataSetDetail guiDataSetDetail = new WSDataSetDetail();
		guiDataSetDetail.setAddress(sbiDataSetHistory.getAdress());
		((WSDataSetDetail)guiDataSetDetail).setOperation(sbiDataSetHistory.getOperation());
		guiDataSetDetail.setDsType(WS_DS_TYPE);
		return guiDataSetDetail;
	}
	
	private static GuiDataSetDetail toGuiDataSetDetail(SbiScriptDataSet sbiDataSetHistory) {
		ScriptDataSetDetail guiDataSetDetail = new ScriptDataSetDetail();
		guiDataSetDetail.setScript(sbiDataSetHistory.getScript());
		guiDataSetDetail.setLanguageScript(sbiDataSetHistory.getLanguageScript());
		guiDataSetDetail.setDsType(SCRIPT_DS_TYPE);
		return guiDataSetDetail;
	}
	
	private static GuiDataSetDetail toGuiDataSetDetail(SbiJClassDataSet sbiDataSetHistory) {
		JClassDataSetDetail guiDataSetDetail = new JClassDataSetDetail();
		guiDataSetDetail.setJavaClassName(sbiDataSetHistory.getJavaClassName());
		guiDataSetDetail.setDsType(JCLASS_DS_TYPE);
		return guiDataSetDetail;
	}
	
	private static GuiDataSetDetail toGuiDataSetDetail(SbiCustomDataSet sbiDataSetHistory) {
		CustomDataSetDetail guiDataSetDetail = new CustomDataSetDetail();
		guiDataSetDetail.setCustomData(sbiDataSetHistory.getCustomData());
		guiDataSetDetail.setJavaClassName(sbiDataSetHistory.getJavaClassName());
		guiDataSetDetail.setDsType(CUSTOM_DS_TYPE);
		return guiDataSetDetail;
	}	
	
	public static GuiGenericDataSet toGuiGenericDataSet(IDataSet dataSet) {		
		GuiGenericDataSet guiGenericDataSet = new GuiGenericDataSet();
		GuiDataSetDetail guiDataSetDetail = null;

		if(dataSet instanceof FileDataSet){		
			guiDataSetDetail = new FileDataSetDetail();
			((FileDataSetDetail)guiDataSetDetail).setFileName(((FileDataSet)dataSet).getFileName());		
			guiDataSetDetail.setDsType(FILE_DS_TYPE);
		}

		if(dataSet instanceof JDBCDataSet){			
			guiDataSetDetail=new QueryDataSetDetail();
			((QueryDataSetDetail)guiDataSetDetail).setQuery((String)(((JDBCDataSet)dataSet).getQuery()));
			IDataSource iDataSource=((JDBCDataSet)dataSet).getDataSource();
			if(iDataSource!=null){
				String dataSourceLabel = iDataSource.getLabel();
				((QueryDataSetDetail)guiDataSetDetail).setDataSourceLabel(dataSourceLabel);
			}
			guiDataSetDetail.setDsType(JDBC_DS_TYPE);
		}
		
		if(dataSet instanceof QbeDataSet){			
			guiDataSetDetail = new QbeDataSetDetail();
			QbeDataSetDetail aQbeDataSetDetail = (QbeDataSetDetail) guiDataSetDetail;
			QbeDataSet aQbeDataSet = (QbeDataSet) dataSet;
			aQbeDataSetDetail.setJsonQuery(aQbeDataSet.getJsonQuery());
			aQbeDataSetDetail.setDatamarts(aQbeDataSet.getDatamarts());
			IDataSource iDataSource = aQbeDataSet.getDataSource();
			if (iDataSource!=null){
				String dataSourceLabel = iDataSource.getLabel();
				aQbeDataSetDetail.setDataSourceLabel(dataSourceLabel);
			}
			guiDataSetDetail.setDsType(QBE_DS_TYPE);
		}

		if(dataSet instanceof WebServiceDataSet){			
			guiDataSetDetail=new WSDataSetDetail();
			((WSDataSetDetail)guiDataSetDetail).setAddress(((WebServiceDataSet)dataSet).getAddress());
			((WSDataSetDetail)guiDataSetDetail).setOperation(((WebServiceDataSet)dataSet).getOperation());
			guiDataSetDetail.setDsType(WS_DS_TYPE);
		}

		if(dataSet instanceof ScriptDataSet){			
			guiDataSetDetail=new ScriptDataSetDetail();
			((ScriptDataSetDetail)guiDataSetDetail).setScript(((ScriptDataSet)dataSet).getScript());
			((ScriptDataSetDetail)guiDataSetDetail).setLanguageScript(((ScriptDataSet)dataSet).getScriptLanguage());
			guiDataSetDetail.setDsType(SCRIPT_DS_TYPE);
		}

		if(dataSet instanceof JavaClassDataSet){			
			guiDataSetDetail=new JClassDataSetDetail();
			((JClassDataSetDetail)guiDataSetDetail).setJavaClassName(((JavaClassDataSet)dataSet).getClassName());
			guiDataSetDetail.setDsType(JCLASS_DS_TYPE);
		}
		
		if(dataSet instanceof CustomDataSet){			
			guiDataSetDetail=new CustomDataSetDetail();
			((CustomDataSetDetail)guiDataSetDetail).setCustomData(((CustomDataSet)dataSet).getCustomData());
			((CustomDataSetDetail)guiDataSetDetail).setJavaClassName(((CustomDataSet)dataSet).getJavaClassName());
			guiDataSetDetail.setDsType(CUSTOM_DS_TYPE);
		}

		guiGenericDataSet.setDsId(dataSet.getId());
		guiGenericDataSet.setName(dataSet.getName());
		guiGenericDataSet.setLabel(dataSet.getLabel());
		guiGenericDataSet.setDescription(dataSet.getDescription());	

		// set detail dataset ID
		guiDataSetDetail.setTransformerId((dataSet.getTransformerId() == null)? null:dataSet.getTransformerId());
		guiDataSetDetail.setPivotColumnName(dataSet.getPivotColumnName());
		guiDataSetDetail.setPivotRowName(dataSet.getPivotRowName());
		guiDataSetDetail.setPivotColumnValue(dataSet.getPivotColumnValue());
		guiDataSetDetail.setNumRows(dataSet.isNumRows());			
		guiDataSetDetail.setParameters(dataSet.getParameters());		
		guiDataSetDetail.setDsMetadata(dataSet.getDsMetadata());		

		guiGenericDataSet.setActiveDetail(guiDataSetDetail);

		return guiGenericDataSet;
	}
	
	public static IDataSet toDataSet(SbiDataSetHistory sbiDataSetHistory) {
		IDataSet ds = null;
		if(sbiDataSetHistory instanceof SbiFileDataSet){		
			ds = new FileDataSet();
			((FileDataSet)ds).setFileName(((SbiFileDataSet)sbiDataSetHistory).getFileName());		
			ds.setDsType(DataSetConstants.FILE);
		}

		if(sbiDataSetHistory instanceof SbiQueryDataSet){			
			ds=new JDBCDataSet();
			((JDBCDataSet)ds).setQuery(((SbiQueryDataSet)sbiDataSetHistory).getQuery());

			SbiDataSource sbids=((SbiQueryDataSet)sbiDataSetHistory).getDataSource();
			if(sbids!=null){
				DataSourceDAOHibImpl dataSourceDao=new DataSourceDAOHibImpl();
				IDataSource dataSource=dataSourceDao.toDataSource(sbids);
				((JDBCDataSet)ds).setDataSource(dataSource);
			}
			ds.setDsType(DataSetConstants.QUERY);
		}

		if(sbiDataSetHistory instanceof SbiWSDataSet){			
			ds=new WebServiceDataSet();
			((WebServiceDataSet)ds).setAddress(((SbiWSDataSet)sbiDataSetHistory).getAdress());
			((WebServiceDataSet)ds).setOperation(((SbiWSDataSet)sbiDataSetHistory).getOperation());
			ds.setDsType(DataSetConstants.WEB_SERVICE);
		}

		if(sbiDataSetHistory instanceof SbiScriptDataSet){			
			ds=new ScriptDataSet();
			((ScriptDataSet)ds).setScript(((SbiScriptDataSet)sbiDataSetHistory).getScript());
			((ScriptDataSet)ds).setScriptLanguage(((SbiScriptDataSet)sbiDataSetHistory).getLanguageScript());
			ds.setDsType(DataSetConstants.SCRIPT);
		}

		if(sbiDataSetHistory instanceof SbiJClassDataSet){			
			ds=new JavaClassDataSet();
			((JavaClassDataSet)ds).setClassName(((SbiJClassDataSet)sbiDataSetHistory).getJavaClassName());
			ds.setDsType(DataSetConstants.JAVA_CLASS);
		}
		
		if(sbiDataSetHistory instanceof SbiCustomDataSet){			
			ds=new CustomDataSet();
			((CustomDataSet)ds).setCustomData(((SbiCustomDataSet)sbiDataSetHistory).getCustomData());
			((CustomDataSet)ds).setJavaClassName(((SbiCustomDataSet)sbiDataSetHistory).getJavaClassName());
			ds.setDsType(DataSetConstants.CUSTOM_DATA);
		}
		
		if (sbiDataSetHistory instanceof SbiQbeDataSet) {			
			ds = new QbeDataSet();
			SbiQbeDataSet aSbiQbeDataSet = (SbiQbeDataSet) sbiDataSetHistory;
			QbeDataSet qbeDataset = (QbeDataSet) ds;
			qbeDataset.setJsonQuery(aSbiQbeDataSet.getJsonQuery());
			qbeDataset.setDatamarts(aSbiQbeDataSet.getDatamarts());
			
			SbiDataSource sbids = aSbiQbeDataSet.getDataSource();
			if (sbids!=null){
				DataSourceDAOHibImpl dataSourceDao = new DataSourceDAOHibImpl();
				IDataSource dataSource = dataSourceDao.toDataSource(sbids);
				qbeDataset.setDataSource(dataSource);				
			}
			ds.setDsType(DataSetConstants.QBE);
			
		}

		if(ds!=null){
			if(sbiDataSetHistory.getSbiDsConfig()!=null){
				ds.setId(sbiDataSetHistory.getSbiDsConfig().getDsId());
				ds.setName(sbiDataSetHistory.getSbiDsConfig().getName());
				ds.setLabel(sbiDataSetHistory.getSbiDsConfig().getLabel());
				ds.setDescription(sbiDataSetHistory.getSbiDsConfig().getDescription());	
			}
	
			ds.setTransformerId((sbiDataSetHistory.getTransformer()==null)?null:sbiDataSetHistory.getTransformer().getValueId());
			ds.setPivotColumnName(sbiDataSetHistory.getPivotColumnName());
			ds.setPivotRowName(sbiDataSetHistory.getPivotRowName());
			ds.setPivotColumnValue(sbiDataSetHistory.getPivotColumnValue());
			ds.setNumRows(sbiDataSetHistory.isNumRows());
	
			ds.setParameters(sbiDataSetHistory.getParameters());		
			ds.setDsMetadata(sbiDataSetHistory.getDsMetadata());		
	
			if(ds.getPivotColumnName() != null 
					&& ds.getPivotColumnValue() != null
					&& ds.getPivotRowName() != null){
				ds.setDataStoreTransformer(
						new PivotDataSetTransformer(ds.getPivotColumnName(), ds.getPivotColumnValue(), ds.getPivotRowName(), ds.isNumRows()));
			}
		}
		return ds;
	}
	
	//SpagoBiDataSet toSpagoBiDataSet();
	

}
