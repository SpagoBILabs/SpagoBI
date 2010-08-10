package it.eng.spagobi.kpi.model.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.kpi.config.metadata.SbiKpi;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelAttribute;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModel;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelAttr;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelAttrVal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.exception.ConstraintViolationException;

public class ModelDAOImpl extends AbstractHibernateDAO implements IModelDAO {

	static private Logger logger = Logger.getLogger(ModelDAOImpl.class);

	public Model loadModelWithoutChildrenById(Integer id) throws EMFUserError {
		logger.debug("IN");
		Model toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiKpiModel hibSbiKpiModel = (SbiKpiModel) aSession.load(
					SbiKpiModel.class, id);
			toReturn = toModelWithoutChildren(hibSbiKpiModel, aSession);

		} catch (HibernateException he) {
			logger.error("Error while loading the Model with id "
					+ ((id == null) ? "" : id.toString()), he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}

	public Model loadModelWithChildrenById(Integer id) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		Model toReturn = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiKpiModel hibSbiKpiModel = (SbiKpiModel) aSession.load(
					SbiKpiModel.class, id);
			toReturn = toModelWithChildren(aSession, hibSbiKpiModel, null);
		} catch (HibernateException he) {
			logger.error("Error while loading the Model with id "
					+ ((id == null) ? "" : id.toString()), he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}

	public void modifyModel(Model value) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Integer kpiModelId = value.getId();
			String kpiModelCd = value.getCode();
			String kpiModelDesc = value.getDescription();
			String kpiModelNm = value.getName();
			String kpiModelLbl = value.getLabel();
			List modelAttributes = value.getModelAttributes();
			Integer kpiId = value.getKpiId();

			SbiKpiModel sbiKpiModel = (SbiKpiModel) aSession.load(
					SbiKpiModel.class, kpiModelId);
			sbiKpiModel.setKpiModelCd(kpiModelCd);
			sbiKpiModel.setKpiModelDesc(kpiModelDesc);
			sbiKpiModel.setKpiModelNm(kpiModelNm);
			sbiKpiModel.setKpiModelLabel(kpiModelLbl);

			if (kpiId != null) {
				SbiKpi sbiKpi = (SbiKpi) aSession.load(SbiKpi.class, kpiId);
				sbiKpiModel.setSbiKpi(sbiKpi);
			} else {
				sbiKpiModel.setSbiKpi(null);
			}

			aSession.update(sbiKpiModel);
			for (int i = 0; i < modelAttributes.size(); i++) {
				ModelAttribute mAtt = (ModelAttribute) modelAttributes.get(i);
				// / inizio
				SbiKpiModelAttrVal modelAttrValToCreate = null;
				Criteria crit = aSession
						.createCriteria(SbiKpiModelAttrVal.class);
				List modelList = null;
				SbiKpiModelAttrVal toResult = null;
				SbiKpiModel m = (SbiKpiModel) aSession.load(SbiKpiModel.class,
						kpiModelId);
				SbiKpiModelAttr ma = (SbiKpiModelAttr) aSession.load(
						SbiKpiModelAttr.class, mAtt.getId());
				crit.add(Expression.eq("sbiKpiModelAttr", ma));
				crit.add(Expression.eq("sbiKpiModel", m));
				modelList = crit.list();

				if (modelList == null || modelList.isEmpty()) {
					// crea
					modelAttrValToCreate = new SbiKpiModelAttrVal();
					modelAttrValToCreate.setValue(mAtt.getValue());
					modelAttrValToCreate.setSbiKpiModel(m);
					modelAttrValToCreate.setSbiKpiModelAttr(ma);
				} else {
					modelAttrValToCreate = (SbiKpiModelAttrVal) modelList
							.get(0);
					modelAttrValToCreate.setValue(mAtt.getValue());
				}
				// fine

				aSession.saveOrUpdate(modelAttrValToCreate);
			}
			tx.commit();

		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
		logger.debug("OUT");
	}

	public List loadModelsRoot() throws EMFUserError {
		return loadModelsRoot(null, null);

	}

	/**
	 * Create a Model (without modelAttribute) with children from a SbiKpiModel.
	 * 
	 * @param value
	 *            the SbiKpiModel to transform to a Model.
	 * @return the Model create from the SbiKpiModel.
	 */
	private Model toModelWithChildren(Session session, SbiKpiModel value,
			Integer rootId) {
		logger.debug("IN");
		Model toReturn = new Model();
		String name = value.getKpiModelNm();
		String description = value.getKpiModelDesc();
		String code = value.getKpiModelCd();
		String label = value.getKpiModelLabel();
		Integer id = value.getKpiModelId();
		SbiKpi sbiKpi = value.getSbiKpi();
		Integer kpiId = null;
		if (sbiKpi != null) {
			kpiId = sbiKpi.getKpiId();
		}

		Integer typeId = value.getModelType().getValueId();
		String typeCd = value.getModelType().getValueCd();
		String typeName = value.getModelType().getValueNm();
		String typeDescription = value.getModelType().getValueDs();
		List childrenNodes = new ArrayList();

		// Set children = value.getSbiKpiModels();

		Criteria critt = session.createCriteria(SbiKpiModel.class);
		critt.add(Expression.eq("sbiKpiModel", value));
		critt.addOrder(Order.asc("kpiModelCd"));

		List children = critt.list();

		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			SbiKpiModel sbiKpichild = (SbiKpiModel) iterator.next();
			Model child = toModelWithChildren(session, sbiKpichild, id);
			childrenNodes.add(child);
		}

		toReturn.setId(id);
		toReturn.setName(name);
		toReturn.setDescription(description);
		toReturn.setCode(code);
		toReturn.setLabel(label);
		toReturn.setTypeName(typeName);
		toReturn.setTypeCd(typeCd);
		toReturn.setTypeId(typeId);
		toReturn.setTypeDescription(typeDescription);
		toReturn.setChildrenNodes(childrenNodes);
		toReturn.setParentId(rootId);
		toReturn.setKpiId(kpiId);

		List modelAttributes = addAttributeToModel(value, session);
		toReturn.setModelAttributes(modelAttributes);

		
		logger.debug("OUT");
		return toReturn;
	}

	
	/**
	 * Called by toModel
	 */
	static protected List addAttributeToModel(SbiKpiModel value,
			Session aSession) {
		logger.debug("IN");

		List modelAttributes = new ArrayList();

		List modelAttrList = getModelAttListByDomain(value.getModelType(),
				aSession);

		for (Iterator iterator = modelAttrList.iterator(); iterator.hasNext();) {
			ModelAttribute attribute = new ModelAttribute();
			SbiKpiModelAttr attr = (SbiKpiModelAttr) iterator.next();
			String aCode = null;
			String aName = null;
			String aDesc = null;
			String aValue = null;
			Integer idType = null;
			Integer aId = null;

			aCode = attr.getKpiModelAttrCd();
			aName = attr.getKpiModelAttrNm();
			aDesc = attr.getKpiModelAttrDescr();
			aValue = getModelAttrValue(value, attr, aSession);
			aId = attr.getKpiModelAttrId();
			idType = attr.getSbiDomains().getValueId();

			attribute.setId(aId);
			attribute.setCode(aCode);
			attribute.setName(aName);
			attribute.setDescr(aDesc);
			attribute.setValue(aValue);
			attribute.setTypeId(idType);
			modelAttributes.add(attribute);
		}

		
		logger.debug("OUT");
		return modelAttributes;
		
	}
	
	
	
	
	static protected Model toModelWithoutChildren(SbiKpiModel value,
			Session aSession) {
		logger.debug("IN");
		Model toReturn = new Model();

		String name = value.getKpiModelNm();
		String description = value.getKpiModelDesc();
		String code = value.getKpiModelCd();
		String label = value.getKpiModelLabel();
		Integer id = value.getKpiModelId();
		SbiKpi sbiKpi = value.getSbiKpi();
		Integer kpiId = null;
		if (sbiKpi != null) {
			kpiId = sbiKpi.getKpiId();
		}

		String typeCd = value.getModelType().getValueCd();
		Integer typeId = value.getModelType().getValueId();
		String typeName = value.getModelType().getValueNm();
		String typeDescription = value.getModelType().getValueDs();

//		List modelAttributes = new ArrayList();
//		//
//
//		List modelAttrList = getModelAttListByDomain(value.getModelType(),
//				aSession);
//
//		for (Iterator iterator = modelAttrList.iterator(); iterator.hasNext();) {
//			ModelAttribute attribute = new ModelAttribute();
//			SbiKpiModelAttr attr = (SbiKpiModelAttr) iterator.next();
//			String aCode = null;
//			String aName = null;
//			String aDesc = null;
//			String aValue = null;
//			Integer aId = null;
//
//			aCode = attr.getKpiModelAttrCd();
//			aName = attr.getKpiModelAttrNm();
//			aDesc = attr.getKpiModelAttrDescr();
//			aValue = getModelAttrValue(value, attr, aSession);
//			aId = attr.getKpiModelAttrId();
//
//			attribute.setId(aId);
//			attribute.setCode(aCode);
//			attribute.setName(aName);
//			attribute.setDescr(aDesc);
//			attribute.setValue(aValue);
//			modelAttributes.add(attribute);
//		}

		toReturn.setId(id);
		toReturn.setName(name);
		toReturn.setDescription(description);
		toReturn.setCode(code);
		toReturn.setLabel(label);
		toReturn.setTypeId(typeId);		
		toReturn.setTypeCd(typeCd);	
		toReturn.setTypeName(typeName);
		toReturn.setTypeDescription(typeDescription);

		List modelAttributes = addAttributeToModel(value, aSession);
		toReturn.setModelAttributes(modelAttributes);
		
		toReturn.setChildrenNodes(null);
		toReturn.setKpiId(kpiId);

		logger.debug("OUT");
		return toReturn;
	}

	static private String getModelAttrValue(SbiKpiModel model,
			SbiKpiModelAttr attr, Session session) {
		String toReturn = "";
		Criteria critt = session.createCriteria(SbiKpiModelAttrVal.class);
		critt.add(Expression.eq("sbiKpiModel", model));
		critt.add(Expression.eq("sbiKpiModelAttr", attr));
		List modelAttrValList = critt.list();
		if (!modelAttrValList.isEmpty()) {
			SbiKpiModelAttrVal attrVal = (SbiKpiModelAttrVal) modelAttrValList
					.get(0);
			toReturn = attrVal.getValue();
		}
		return toReturn;
	}

	static private List getModelAttListByDomain(SbiDomains sbiDomains,
			Session session) {
		Criteria critt = session.createCriteria(SbiKpiModelAttr.class);

		critt.add(Expression.eq("sbiDomains.valueId", sbiDomains.getValueId()));
		return critt.list();
	}

	public Integer insertModel(Model model, Integer modelTypeId)
			throws EMFUserError {
		logger.debug("IN");
		Integer idToReturn;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Integer parentId = model.getParentId();
			Integer kpiId = model.getKpiId();

			// get the domains
			SbiDomains sbiDomains = (SbiDomains) aSession.load(
					SbiDomains.class, modelTypeId);
			// set the sbiKpiModel
			SbiKpiModel sbiKpiModel = new SbiKpiModel();
			sbiKpiModel.setKpiModelNm(model.getName());
			sbiKpiModel.setKpiModelDesc(model.getDescription());
			sbiKpiModel.setKpiModelCd(model.getCode());
			sbiKpiModel.setKpiModelLabel(model.getLabel());
			sbiKpiModel.setModelType(sbiDomains);
			if (parentId != null) {
				SbiKpiModel sbiKpiParentModel = (SbiKpiModel) aSession.load(
						SbiKpiModel.class, parentId);
				sbiKpiModel.setSbiKpiModel(sbiKpiParentModel);
			}

			if (kpiId != null) {
				SbiKpi sbiKpi = (SbiKpi) aSession.load(SbiKpi.class, kpiId);
				sbiKpiModel.setSbiKpi(sbiKpi);
			}

			idToReturn = (Integer) aSession.save(sbiKpiModel);

			tx.commit();

		} catch (HibernateException he) {
			logException(he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
		logger.debug("OUT");
		return idToReturn;
	}

	public boolean deleteModel(Integer modelId) throws EMFUserError {
		Session aSession = getSession();
		Transaction tx = null;
		try {
			tx = aSession.beginTransaction();
			SbiKpiModel aModel = (SbiKpiModel) aSession.load(SbiKpiModel.class,
					modelId);
			recursiveStepDelete(aSession, aModel);
			deleteModelAndAttribute(aSession, aModel);

			tx.commit();

		} catch (ConstraintViolationException cve) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Impossible to delete a Model", cve);
			throw new EMFUserError(EMFErrorSeverity.WARNING, 10015);

		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Error while delete a Model ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);
		} finally {
			aSession.close();
		}
		return true;
	}

	private void recursiveStepDelete(Session aSession, SbiKpiModel aModel) {
		Set children = aModel.getSbiKpiModels();
		if(children != null){
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				SbiKpiModel modelChild = (SbiKpiModel) iterator.next();
				recursiveStepDelete(aSession, modelChild);
				// delete Model and Attribute
				deleteModelAndAttribute(aSession, modelChild);
			}
		}
	}

	private void deleteModelAndAttribute(Session aSession, SbiKpiModel aModel) {
		Set modelChildAttributes = aModel.getSbiKpiModelAttrVals();
		if(modelChildAttributes != null){
			for (Iterator iterator2 = modelChildAttributes.iterator(); iterator2
					.hasNext();) {
				SbiKpiModelAttrVal modelAttrVal = (SbiKpiModelAttrVal) iterator2
						.next();
				aSession.delete(modelAttrVal);
			}
		}
		aSession.delete(aModel);
	}

	public List loadModelsRoot(String fieldOrder, String typeOrder)
			throws EMFUserError {
		logger.debug("IN");
		List toReturn = null;
		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			toReturn = new ArrayList();
			
			SbiDomains sbiDomains = new SbiDomains();
			sbiDomains.setDomainCd("MODEL_ROOT");
			SbiKpiModel modelExample = new SbiKpiModel();

			Criteria crit = aSession.createCriteria(SbiKpiModel.class);
			crit.add(Example.create(modelExample)).createCriteria("modelType")
					.add(Example.create(sbiDomains));
			
			List toTransform = null;
			if (fieldOrder != null && typeOrder != null) {
				if (typeOrder.toUpperCase().trim().equals("ASC"))
					crit.addOrder(Order.asc(getModelProperty(fieldOrder)));
				if (typeOrder.toUpperCase().trim().equals("DESC"))
					crit.addOrder(Order.desc(getModelProperty(fieldOrder)));
				toTransform = crit.list();
			} else {
				toTransform = crit.list();
			}

			for (Iterator iterator = toTransform.iterator(); iterator.hasNext();) {
				SbiKpiModel sbiKpiModel = (SbiKpiModel) iterator.next();
				Model aModel = toModelWithoutChildren(sbiKpiModel, aSession);
				toReturn.add(aModel);
			}

		} catch (HibernateException he) {
			logger.error("Error while loading the list of SbiKpiModel", he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 9104);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		return toReturn;
	}

	private String getModelProperty(String property) {
		String toReturn = null;
		if(property != null && property.equals("NAME"))
				toReturn = "kpiModelNm";
		if(property != null && property.equals("CODE"))
			toReturn = "kpiModelCd";
		return toReturn;
	}

	public Integer insertModel(Model model) throws EMFUserError {
		logger.debug("IN");
		Integer idToReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Integer parentId = model.getParentId();
			Integer kpiId = model.getKpiId();

			// get the domains
			SbiDomains sbiDomains = (SbiDomains) aSession.load(
					SbiDomains.class, model.getTypeId());
			// set the sbiKpiModel
			SbiKpiModel sbiKpiModel = new SbiKpiModel();
			sbiKpiModel.setKpiModelNm(model.getName());
			sbiKpiModel.setKpiModelDesc(model.getDescription());
			sbiKpiModel.setKpiModelCd(model.getCode());
			sbiKpiModel.setKpiModelLabel(model.getLabel());
			sbiKpiModel.setModelType(sbiDomains);
			if (parentId != null) {
				SbiKpiModel sbiKpiParentModel = (SbiKpiModel) aSession.load(
						SbiKpiModel.class, parentId);
				sbiKpiModel.setSbiKpiModel(sbiKpiParentModel);
			}

			if (kpiId != null) {
				SbiKpi sbiKpi = (SbiKpi) aSession.load(SbiKpi.class, kpiId);
				sbiKpiModel.setSbiKpi(sbiKpi);
			}

			idToReturn = (Integer) aSession.save(sbiKpiModel);

			tx.commit();

		} catch (HibernateException he) {
			logException(he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
		logger.debug("OUT");
		return idToReturn;
	}

	// public boolean hasKpi(Integer modelId) throws EMFUserError {
	// Session aSession = getSession();
	// Transaction tx = null;
	// boolean toReturn = false;
	// try {
	// tx = aSession.beginTransaction();
	// SbiKpiModel aModel = (SbiKpiModel) aSession.load(SbiKpiModel.class,
	// modelId);
	// toReturn = recursiveHasKpi(aModel);
	//		
	// tx.commit();
	//
	// } catch (HibernateException e) {
	// if (tx != null && tx.isActive()) {
	// tx.rollback();
	// }
	// throw new EMFUserError(EMFErrorSeverity.ERROR, 101);
	//
	// } finally {
	// aSession.close();
	// }
	// return toReturn;
	// }
	//
	// private boolean recursiveHasKpi(SbiKpiModel model) {
	// SbiKpi sbiKpi = model.getSbiKpi();
	// boolean toReturn =(sbiKpi != null);
	// Set Children = model.getSbiKpiModels();
	// for (Iterator iterator = Children.iterator(); iterator.hasNext();) {
	// SbiKpiModel child = (SbiKpiModel) iterator.next();
	// toReturn = toReturn || recursiveHasKpi(child);
	// if(toReturn == true)
	// break;
	// }
	// return toReturn;
	// }

}
