/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.analiticalmodel.document;

import it.eng.spago.base.SourceBean;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IBIObjectParameterDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.MetadataJSONSerializer;
import it.eng.spagobi.commons.utilities.indexing.LuceneIndexer;
import it.eng.spagobi.tools.dataset.bo.DataSetParameterItem;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetacontentDAO;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetadataDAO;
import it.eng.spagobi.tools.objmetadata.dao.ObjMetadataDAOHibImpl;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 * 
 * This class considers all objects with the id property set and not equal to 0 as object that already exist 
 * in the database (see method isAnExistingDocument)
 *
 */
public class AnalyticalModelDocumentManagementAPI {
	
	private IBIObjectDAO documentDAO;
	private IBIObjectParameterDAO documentParameterDAO;
	private IObjMetacontentDAO documentMetadataPropertyDAO;
	private IObjMetadataDAO metadataPropertyDAO;
	
	// default for document parameters
	public static final Integer REQUIRED = 0;
	public static final Integer MODIFIABLE = 1;
	public static final Integer MULTIVALUE = 0;
	public static final Integer VISIBLE = 1;
	
	private static Logger logger = Logger.getLogger(AnalyticalModelDocumentManagementAPI.class);
	
	public AnalyticalModelDocumentManagementAPI(IEngUserProfile userProfile) {
		try {
			documentDAO = DAOFactory.getBIObjectDAO();
			documentDAO.setUserProfile(userProfile);
			
			documentParameterDAO = DAOFactory.getBIObjectParameterDAO();
			documentParameterDAO.setUserProfile(userProfile);
			
			documentMetadataPropertyDAO = DAOFactory.getObjMetacontentDAO();
			documentMetadataPropertyDAO.setUserProfile(userProfile);
			
			metadataPropertyDAO = new ObjMetadataDAOHibImpl();
			metadataPropertyDAO.setUserProfile(userProfile);
			
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to instatiate BIObjectDAO", t);
		}
	}
	
	
	/**
	 * Utility method. Returns the document associated to the descriptor object.
	 * 
	 * @param docDescriptor Could be the document itself (an object of type BIObject), its id (an object
	 * of type Integer) or its label (an object of type String)
	 * 
	 * @return the document associated to the descriptor object if it exist, null otherwise.
	 */
	public BIObject getDocument(Object docDescriptor) {
		BIObject document;
		
		document = null;
		
		try {
			Assert.assertNotNull(docDescriptor, "Input parameter [docDescriptor] cannot be null");
			
			if(docDescriptor instanceof BIObject) {
				document = (BIObject)docDescriptor;
				if( !isAnExistingDocument(document) ) document = null;
			} else if(docDescriptor instanceof Integer) {
				try {
					document = documentDAO.loadBIObjectById((Integer)docDescriptor);
				} catch (Throwable t) {
					throw new SpagoBIRuntimeException("Impossible to load document whose id is equal to [" + docDescriptor + "]", t);
				}
			} else if(docDescriptor instanceof String) {
				try {
					document = documentDAO.loadBIObjectByLabel((String)docDescriptor);
				} catch (Throwable t) {
					throw new SpagoBIRuntimeException("Impossible to load document whose label is equal to [" + docDescriptor + "]", t);
				}
			} else {
				throw new SpagoBIRuntimeException("Unable to manage a document descriptor of type [" + docDescriptor.getClass().getName() + "]");
			}
		} catch (SpagoBIRuntimeException t) {
			throw t; // nothing to add just re-throw
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unsespected error occured while loading object [" + docDescriptor + "]", t);
		}
		
		return document;
	}
	
	/**
	 * Utility method. Returns the analytical driver associated to the descriptor object.
	 * 
	 * @param analyticalDriverDescriptor Could be the analytical driver itself (an object of type Parameter)
 	 * or its label (an object of type String)
	 * 
	 * @return the analytical driver associated to the descriptor object if it exist, null otherwise.
	 */
	public Parameter getAnalyticalDriver(Object analyticalDriverDescriptor) {
		Parameter analyticalDriver;
		
		try {
			analyticalDriver = null;
			if(analyticalDriverDescriptor instanceof Parameter) {
				analyticalDriver = (Parameter)analyticalDriverDescriptor;
			} else if(analyticalDriverDescriptor instanceof String) {
				try {
					String analyticalDriverLabel = (String)analyticalDriverDescriptor;
					analyticalDriver = DAOFactory.getParameterDAO().loadForDetailByParameterLabel(analyticalDriverLabel);
				} catch (Throwable t) {
					throw new SpagoBIRuntimeException("Analytical driver " + analyticalDriver + " cannot be loaded", t);
				}
			} else {
				throw new SpagoBIRuntimeException("Unable to manage an analytical driver descriptor of type [" + analyticalDriverDescriptor.getClass().getName() + "]");
			}
		} catch (SpagoBIRuntimeException t) {
			throw t; // nothing to add just re-throw
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unsespected error occured while loading object [" + analyticalDriverDescriptor + "]", t);
		}
		
		return analyticalDriver;
	}
	
	/**
	 * Utility method. Returns the metadata property associated to the descriptor object.
	 * 
	 * @param matadataPropertyDescriptor Could be the metadata property's id (an object of type Integer)
 	 * or its label (an object of type String)
	 * 
	 * @return the analytical driver associated to the descriptor object if it exist, null otherwise.
	 */
	public ObjMetadata getMetadataProperty(Object matadataPropertyDescriptor) {
		
		ObjMetadata metadataProperty;
		
		logger.debug("IN");
		
		metadataProperty = null;
		try {
			if(matadataPropertyDescriptor instanceof Integer) {
				Integer id = (Integer)matadataPropertyDescriptor;
				metadataProperty = metadataPropertyDAO.loadObjMetaDataByID(id);
			} else if (matadataPropertyDescriptor instanceof String){
				String label = (String)matadataPropertyDescriptor;
				metadataProperty = metadataPropertyDAO.loadObjMetadataByLabel(label);
			} else {
				throw new SpagoBIRuntimeException("Unable to manage a metadata descriptor of type [" + matadataPropertyDescriptor.getClass().getName() + "]");
			}
		} catch (SpagoBIRuntimeException t) {
			throw t; // nothing to add just re-throw
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unsespected error occured while loading metadata [" + matadataPropertyDescriptor + "]", t);
		}
		
		return metadataProperty;
	}
	
	/**
	 * 
	 * @param document The document
	 * @return return true if the doocument's id property is set and not equal to 0. This method do not perform a real check
	 * on the database.
	 */
	public boolean isAnExistingDocument(BIObject document) {
		Integer documentId;
		
		Assert.assertNotNull(document, "Input parameter [document] cannot be null");
		
		documentId = document.getId();
		return (documentId != null && documentId.intValue() != 0);
	}

	/**
	 * 
	 * @param document The document to save (insert or modify)
	 * @param template The new template of the document
	 * 
	 * @return true if the save operation perform an overwrite ( = modify an existing document ), false
	 * otherwise ( = insert a new document )
	 * 
	 */
	public boolean saveDocument( BIObject document, ObjTemplate template ) {
		
		Boolean overwrite;
		
		Assert.assertNotNull(document, "Input parameter [document] cannot be null");
		
		if( isAnExistingDocument(document) ){										
			try {
				documentDAO.modifyBIObject(document, template);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to update object [" + document.getLabel() + "]", t);
			}
			overwrite = true;
			logger.debug("Document [" + document.getLabel() + "] succesfully updated");
		} else {
			try {
				documentDAO.insertBIObject(document, template);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to insert object [" + document.getLabel() + "]", t);
			}
			
			overwrite = false;
			logger.debug("Document with [" + document.getLabel() + "] succesfully inserted with id [" + document.getId() + "]");
		}
		
		return overwrite;
	}
	

	/**
	 * 
	 * @param documentDescriptor The descriptor of the target document
	 * @param subObjectId The id of the target subobject (optional). If it is nos specified the metadata properties 
	 * will be applied to the main object
	 * @param metadataJSON The metadata properties to add. They are encoded as an array of object like the following one
	 * <code>
	 * {
	 * 	meta_id: NUMBER
	 * , meta_name: STRING
	 * , meta_content: STRING
	 * }
	 * </code> 
	 * at least one between attributes meta_id and meta_name must be set.
	 * 
	 * TODO use this method to refactor class SaveMetadataAction
	 * 
	 */
	public void saveDocumentMetadataProperties(Object documentDescriptor, Integer subObjectId, JSONArray metadataJSON) {
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(documentDescriptor, "Input parameter [documentDescriptor] cannot be null");
			Assert.assertNotNull(metadataJSON, "Input parameter [metadataJSON] cannot be null");
			
			
			BIObject document = getDocument(documentDescriptor);
			if(document == null) {
				throw new SpagoBIRuntimeException("Impossible to resolve document [" + documentDescriptor + "]");
			}
			
					
			for (int i = 0; i < metadataJSON.length(); i++) {					
				JSONObject documentMatadataPropertyJSON = metadataJSON.getJSONObject(i);
				
				Integer metadataPropertyId = null;
				if(documentMatadataPropertyJSON.has( MetadataJSONSerializer.METADATA_ID )) {
					metadataPropertyId = documentMatadataPropertyJSON.optInt(MetadataJSONSerializer.METADATA_ID);
				}
				String metadataPropertyName = documentMatadataPropertyJSON.optString(MetadataJSONSerializer.NAME);
				if(metadataPropertyId == null && metadataPropertyName == null) {
					throw new SpagoBIRuntimeException("Attributes [" + MetadataJSONSerializer.METADATA_ID + "] and [" + MetadataJSONSerializer.NAME + "] cannot be both null");
				}
				
				if(metadataPropertyId == null) {
					ObjMetadata metadataProperty = getMetadataProperty(metadataPropertyName);
					if(metadataProperty != null){
						metadataPropertyId = metadataProperty.getObjMetaId();
					}
					
					if(metadataPropertyId == null) {
						logger.warn("Impossible to resolve metadata property [" + metadataPropertyName+ "]");
						continue;
					}
				}
				
				
				String documentMetadataPropertyValue = documentMatadataPropertyJSON.getString(MetadataJSONSerializer.TEXT);
				if(documentMetadataPropertyValue == null) {
					throw new SpagoBIRuntimeException("Attributes [" + MetadataJSONSerializer.TEXT + "] of metadata property cannot [" + metadataPropertyId + "] be null");
				}
				
		
				ObjMetacontent documentMatadataProperty = 
					documentMetadataPropertyDAO.loadObjMetacontent(metadataPropertyId, document.getId(), subObjectId); // TODO manage subobjects
				
				if (documentMatadataProperty == null) {
					logger.debug("ObjMetacontent for metadata id = " + metadataPropertyId + ", biobject id = " + document.getId() + 
							", subobject id = " + subObjectId + " was not found, creating a new one...");
					documentMatadataProperty = new ObjMetacontent();
					documentMatadataProperty.setObjmetaId(metadataPropertyId);
					documentMatadataProperty.setBiobjId( document.getId() );
					documentMatadataProperty.setSubobjId( subObjectId );
					documentMatadataProperty.setContent(documentMetadataPropertyValue.getBytes("UTF-8"));
					documentMatadataProperty.setCreationDate(new Date());
					documentMatadataProperty.setLastChangeDate(new Date());
					
					documentMetadataPropertyDAO.insertObjMetacontent(documentMatadataProperty);
				} else {
					logger.debug("ObjMetacontent for metadata id = " + metadataPropertyId + ", biobject id = " + document.getId() + 
							", subobject id = " + subObjectId + " was found, it will be modified...");
					documentMatadataProperty.setContent(documentMetadataPropertyValue.getBytes("UTF-8"));
					documentMatadataProperty.setLastChangeDate(new Date());
					
					documentMetadataPropertyDAO.modifyObjMetacontent(documentMatadataProperty);
				}

			}	
	
			BIObject biObjToIndex = DAOFactory.getBIObjectDAO().loadBIObjectById( document.getId() );
			LuceneIndexer.updateBiobjInIndex(biObjToIndex, false);
		
		} catch (Throwable e) {
			throw new SpagoBIRuntimeException("Exception occurred while saving metadata", e);
		} finally {
			logger.debug("OUT");
		}
	}
	

	
	/**
	 * Copy all the parameters associated with sourceDocument to destinationDocument
	 * 
	 * @param sourceDocument can be an object of type BIObject or an Integer 
	 * representing the id of the source document
	 * @param destinationDocument can be an object of type BIObject or an Integer 
	 * representing the id of the destination document
	 */
	public void copyParameters(Object sourceDocument, Object destinationDocument) {
		copyParameters(getDocument(sourceDocument), getDocument(destinationDocument));
	}
	
	private void copyParameters(BIObject sourceDocument, BIObject destinationDocument) {
		
		String sourceDocumentLabel = null;
		String destinationDocumentLabel = null;
		
		try {
			Assert.assertNotNull(sourceDocument, "Input parameter [sourceDocument] cannot be null");
			Assert.assertNotNull(destinationDocument, "Input parameter [destinationDocument] cannot be null");
			
			sourceDocumentLabel = sourceDocument.getLabel();
			destinationDocumentLabel = destinationDocument.getLabel();
			
			List<BIObjectParameter> parameters = sourceDocument.getBiObjectParameters();
			
			if (parameters != null && !parameters.isEmpty() ) {
				for(BIObjectParameter parameter : parameters) {
					parameter.setBiObjectID( destinationDocument.getId() );
					parameter.setId(null);
					try {
						DAOFactory.getBIObjectParameterDAO().insertBIObjectParameter(parameter);
					} catch(Throwable t) {
						throw new SpagoBIRuntimeException("Impossible to copy parameter [" + parameter.getLabel() + "] from document [" + sourceDocumentLabel + "] to document [" + destinationDocumentLabel + "]",t);
					}
				}
			} else {
				logger.warn("Document [" + sourceDocumentLabel +"] have no parameters");
			}
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occured while copying parameters from document [" + sourceDocumentLabel + "] to document [" + destinationDocumentLabel + "]", t);
		}
	}
	

	/**
	 * This method add a parameter to the document for each parameter associated with the dataset. The added parameters
	 * will point to analytical drivers whose label match with the corresponding dataset parameter's name. If for one
	 * dataset parameter does not exist an analytical driver whose label match with the name of the parameter an exception will
	 * be thrown
	 * 
	 * @param dataset the datset
	 * @param document the document
	 */
	public void propagateDatasetParameters(GuiGenericDataSet dataset, BIObject document) {

		List<DataSetParameterItem> datasetParameterItems = getDatasetParameters(dataset);
		
		int priority = 0;
		for (DataSetParameterItem datasetParameters : datasetParameterItems) {
			addParameter(document, datasetParameters.getName(), priority++);		
		}
	}
	
	
	/**
	 * Add the analytical driver associated to the analyticalDriverDescriptor to the document associated
	 * to the documentDescriptor. The document must be already present on the database. The name and the 
	 * url of the added parameters are both equal to the analytical driver label. This method do not
	 * check if the document already have a parameter with this name.
	 * 
	 * @param documentDescriptor can be the document itself(BIObject), the document id(Integer) or the document label(String)
	 * @param analyticalDriverDescriptor can be the analytical driver(Parameter) itself or its label (String)
	 * @param priority
	 */
	public void addParameter(BIObject documentDescriptor, Object analyticalDriverDescriptor, int priority) {
		BIObject document;
		BIObjectParameter documentParameter;
		Parameter analyticalDriver;
		
		try {
			Assert.assertNotNull(documentDescriptor, "Input parameter [documentDescriptor] cannot be null");
			Assert.assertNotNull(documentDescriptor, "Input parameter [analyticalDriverDescriptor] cannot be null");
			
			document = getDocument(documentDescriptor);
			if(document == null){
				throw new SpagoBIRuntimeException("Analytical driver with " + documentDescriptor + " does not exist");
			}
			
			analyticalDriver = getAnalyticalDriver(analyticalDriverDescriptor);		
			if(analyticalDriver == null){
				throw new SpagoBIRuntimeException("Analytical driver " + analyticalDriverDescriptor + " does not exist");
			}
			
			documentParameter = new BIObjectParameter();
			documentParameter.setBiObjectID(document.getId());
			documentParameter.setParID(analyticalDriver.getId());
			documentParameter.setParameter(analyticalDriver);
			documentParameter.setParameterUrlName(analyticalDriver.getLabel());
			documentParameter.setLabel(analyticalDriver.getName());
			documentParameter.setRequired(REQUIRED);
			documentParameter.setMultivalue(MULTIVALUE);
			documentParameter.setModifiable(MODIFIABLE);
			documentParameter.setVisible(VISIBLE);
			documentParameter.setPriority( priority );
			
			try {
				documentParameterDAO.insertBIObjectParameter(documentParameter);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to save parameter whose label is equal to [" + analyticalDriverDescriptor + "] to document [" + document + "]", t);
			}
			
			if(document.getBiObjectParameters() == null) {
				document.setBiObjectParameters(new ArrayList<BIObjectParameter>());
			}
			document.getBiObjectParameters().add(documentParameter);
			
		} catch (SpagoBIRuntimeException t) {
			throw t; // nothing to add just re-throw
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unsespected error occured while adding parameter [" + analyticalDriverDescriptor + "] to document [" + documentDescriptor + "]", t);
		}
	}
	
	// TODO move this in DataSetManagementAPI
	private List<DataSetParameterItem> getDatasetParameters (GuiGenericDataSet dataset) {
		
		GuiDataSetDetail datasetDetail;
		String parametersRawData;
		List<DataSetParameterItem> datasetParameters;
		
		logger.debug("IN");
		
		datasetParameters = new ArrayList<DataSetParameterItem>();
		
		try {
			datasetDetail = dataset.getActiveDetail();
			parametersRawData = datasetDetail.getParameters();
			parametersRawData = parametersRawData.trim();
			
			SourceBean parametersSourceBean;
			try {
				parametersSourceBean = SourceBean.fromXMLString(parametersRawData);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("Parameters' rowdata are not encoded in a valid XML format [" + parametersRawData + "].", t);
			}
			
			List<SourceBean> rows = parametersSourceBean.getAttributeAsList("ROWS.ROW");
			for(SourceBean row : rows){
				String name = (String)row.getAttribute("NAME");
				String type = (String)row.getAttribute("TYPE");
				
				DataSetParameterItem datasetParameter = new DataSetParameterItem();
				datasetParameter.setName(name);
				datasetParameter.setType(type);
				
				datasetParameters.add(datasetParameter);
			}
			
		} catch(SpagoBIRuntimeException t) {
			throw t;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occured while loading dataset parameters", t);
		} finally {
			logger.debug("OUT");
		}
		
		return datasetParameters;
	}
	
}
