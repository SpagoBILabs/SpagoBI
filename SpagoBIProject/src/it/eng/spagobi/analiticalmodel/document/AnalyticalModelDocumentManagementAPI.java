/**
 * 
 */
package it.eng.spagobi.analiticalmodel.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IBIObjectParameterDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
import it.eng.spagobi.tools.dataset.bo.DataSetParameterItem;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 * 
 * This class considers all objects with the id property set and not equal to 0 as object that already exist 
 * in the database (see method isAnExistingDocument)
 *
 */
public class AnalyticalModelDocumentManagementAPI {
	
	private IBIObjectDAO biObjectDAO;
	IBIObjectParameterDAO biObjParameterDAO;
	
	// default for document parameters
	public static final Integer REQUIRED = 0;
	public static final Integer MODIFIABLE = 1;
	public static final Integer MULTIVALUE = 0;
	public static final Integer VISIBLE = 1;
	
	private static Logger logger = Logger.getLogger(AnalyticalModelDocumentManagementAPI.class);
	
	public AnalyticalModelDocumentManagementAPI(IEngUserProfile userProfile) {
		try {
			biObjectDAO = DAOFactory.getBIObjectDAO();
			biObjectDAO.setUserProfile(userProfile);
			
			biObjParameterDAO = DAOFactory.getBIObjectParameterDAO();
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
					document = biObjectDAO.loadBIObjectById((Integer)docDescriptor);
				} catch (Throwable t) {
					throw new SpagoBIRuntimeException("Impossible to load document whose id is equal to [" + docDescriptor + "]", t);
				}
			} else if(docDescriptor instanceof String) {
				try {
					document = biObjectDAO.loadBIObjectByLabel((String)docDescriptor);
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
				throw new SpagoBIRuntimeException("Unable to manage a analytical driver descriptor of type [" + analyticalDriverDescriptor.getClass().getName() + "]");
			}
		} catch (SpagoBIRuntimeException t) {
			throw t; // nothing to add just re-throw
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unsespected error occured while loading object [" + analyticalDriverDescriptor + "]", t);
		}
		
		return analyticalDriver;
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
				biObjectDAO.modifyBIObject(document, template);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to update object [" + document.getLabel() + "]", t);
			}
			overwrite = true;
			logger.debug("Document [" + document.getLabel() + "] succesfully updated");
		} else {
			try {
				biObjectDAO.insertBIObject(document, template);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to insert object [" + document.getLabel() + "]", t);
			}
			
			overwrite = false;
			logger.debug("Document with [" + document.getLabel() + "] succesfully inserted with id [" + document.getId() + "]");
		}
		
		return overwrite;
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
				biObjParameterDAO.insertBIObjectParameter(documentParameter);
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
