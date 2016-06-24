/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.mapcatalogue.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.mapcatalogue.bo.GeoLayer;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoLayers;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoLayersRoles;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SbiGeoLayersDAOHibImpl extends AbstractHibernateDAO implements ISbiGeoLayersDAO{

	/**
	 * Load layer by id.
	 * 
	 * @param layerID the layer id
	 * 
	 * @return the geo layer
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.mapcatalogue.dao.geo.bo.dao.ISbiGeoLayersDAO#loadLayerByID(integer)
	 */
	public GeoLayer loadLayerByID(Integer layerID) throws EMFUserError {
		GeoLayer toReturn = null;
		Session tmpSession = null;
		Transaction tx = null;

		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			SbiGeoLayers hibLayer = (SbiGeoLayers)tmpSession.load(SbiGeoLayers.class,  layerID);
			toReturn = hibLayer.toGeoLayer();
			tx.commit();
			
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
				
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Load layer by name.
	 * 
	 * @param name the name
	 * 
	 * @return the geo layer
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.mapcatalogue.dao.geo.bo.dao.ISbiGeoLayersDAO#loadLayerByName(string)
	 */
	public GeoLayer loadLayerByLabel(String label) throws EMFUserError {
		GeoLayer biLayer = null;
		Session tmpSession = null;
		Transaction tx = null;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("label", label);
			Criteria criteria = tmpSession.createCriteria(SbiGeoLayers.class);
			criteria.add(labelCriterrion);	
			SbiGeoLayers hibLayer = (SbiGeoLayers) criteria.uniqueResult();
			if (hibLayer == null) return null;
			biLayer = hibLayer.toGeoLayer();
			
			tx.commit();
		} catch (HibernateException he) {
			logException(he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
			
		}
		return biLayer;		
	}

	
	/**
	 * Modify layer.
	 * 
	 * @param aLayer the a layer
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.geo.bo.dao.IEngineDAO#modifyEngine(it.eng.spagobi.bo.Engine)
	 */
	public void modifyLayer(GeoLayer aLayer) throws EMFUserError {
		
		Session tmpSession = null;
		Transaction tx = null;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();

			SbiGeoLayers hibLayer = (SbiGeoLayers) tmpSession.load(SbiGeoLayers.class, new Integer(aLayer.getLayerId()));
			hibLayer.setName(aLayer.getName());
			hibLayer.setDescr(aLayer.getDescr());
			hibLayer.setType(aLayer.getType());	
			hibLayer.setLabel(aLayer.getLabel());
			hibLayer.setBaseLayer(aLayer.isBaseLayer());
			hibLayer.setLayerDef(aLayer.getLayerDef());
			updateSbiCommonInfo4Update(hibLayer);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
		}

	}

	/**
	 * Insert layer.
	 * 
	 * @param aLayer the a layer
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.geo.bo.dao.IEngineDAO#insertEngine(it.eng.spagobi.bo.Engine)
	 */
	public Integer insertLayer(GeoLayer aLayer) throws EMFUserError {		
		Session tmpSession = null;
		Transaction tx = null;
		Integer id;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			SbiGeoLayers hibLayer = new SbiGeoLayers();
			hibLayer.setName(aLayer.getName());
			hibLayer.setDescr(aLayer.getDescr());
			hibLayer.setType(aLayer.getType());
			hibLayer.setLabel(aLayer.getLabel());
			hibLayer.setBaseLayer(aLayer.isBaseLayer());
			hibLayer.setLayerDef(aLayer.getLayerDef());
			updateSbiCommonInfo4Insert(hibLayer);
			id = (Integer) tmpSession.save(hibLayer);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
			
		}
		return id;
	}

	/**
	 * Erase layer.
	 * 
	 * @param aLayer the a layer
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.geo.bo.dao.IEngineDAO#eraseEngine(it.eng.spagobi.bo.Engine)
	 */
	public void eraseLayer(Integer layerId) throws EMFUserError {
		
		Session tmpSession = null;
		Transaction tx = null;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			SbiGeoLayers hibLayer = (SbiGeoLayers) tmpSession.load(SbiGeoLayers.class,layerId);

			tmpSession.delete(hibLayer);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
			
		}
	}
	
	/**
	 * Load all layers.
	 * 
	 * @return the list
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.geo.bo.dao.IEngineDAO#loadAllEngines()
	 */
	public List<GeoLayer> loadAllLayers() throws EMFUserError {
		Session tmpSession = null;
		Transaction tx = null;
		List<GeoLayer> realResult = new ArrayList<GeoLayer>();
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();

			Query hibQuery = tmpSession.createQuery(" from SbiGeoLayers");
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();			
			while (it.hasNext()) {			
				SbiGeoLayers hibLayer = (SbiGeoLayers) it.next();	
				if (hibLayer != null) {
					GeoLayer bilayer = hibLayer.toGeoLayer();
					realResult.add(bilayer);
				}
			}

			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
			
		}
		return realResult;
	}

	/**
	 * Load all layers.
	 *
	 * @return the list
	 *
	 * @throws EMFUserError
	 *             the EMF user error
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 *
	 * @see it.eng.spagobi.geo.bo.dao.IEngineDAO#loadAllEngines()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<GeoLayer> loadAllLayers(String[] listLabel, IEngUserProfile profile) throws EMFUserError, JSONException, UnsupportedEncodingException {
		Session tmpSession = null;
		Transaction tx = null;
		List<GeoLayer> realResult = new ArrayList<GeoLayer>();
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();

			String inList = "";
			if (listLabel != null) {
				inList += " where label in (:listLabel)";
			}
			Query hibQuery = tmpSession.createQuery(" from SbiGeoLayers" + inList);

			if (listLabel != null) {
				hibQuery.setParameterList("listLabel", listLabel);
			}
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();
			SbiGeoLayers hibLayer;
			while (it.hasNext()) {
				hibLayer = (SbiGeoLayers) it.next();
				if (hibLayer != null) {
					final GeoLayer bilayer = hibLayer.toGeoLayer();
					List<SbiGeoLayersRoles> roles = getListRolesById(hibLayer.getLayerId());
					if (!userIsAbilited(roles, profile)) {
						continue;
					}
					String str = new String(hibLayer.getLayerDef(), "UTF-8");
					JSONObject layerDef = new JSONObject(str);

					bilayer.setLayerIdentify(layerDef.getString("layerId"));
					bilayer.setLayerLabel(layerDef.getString("layerLabel"));
					bilayer.setLayerName(layerDef.getString("layerName"));
					if (!layerDef.getString("properties").isEmpty()) {
						List<String> prop = new ArrayList<>();
						JSONArray obj = layerDef.getJSONArray("properties");

						for (int j = 0; j < obj.length(); j++) {

							prop.add(obj.getString(j));
						}

						bilayer.setProperties(prop);
					}
					if (!layerDef.getString("layer_file").equals("null")) {

						String resourcePath = SpagoBIUtilities.getResourcePath();
						// TODO delete this after all layer are saved with new path file
						if (layerDef.getString("layer_file").startsWith(resourcePath)) {
							bilayer.setPathFile(layerDef.getString("layer_file"));
						} else {
							bilayer.setPathFile(resourcePath + File.separator + layerDef.getString("layer_file"));
						}

					}
					if (!layerDef.getString("layer_url").equals("null")) {
						bilayer.setLayerURL(layerDef.getString("layer_url"));

					}
					if (!layerDef.getString("layer_params").equals("null")) {
						bilayer.setLayerParams(layerDef.getString("layer_params"));
					}
					if (!layerDef.getString("layer_options").equals("null")) {
						bilayer.setLayerOptions(layerDef.getString("layer_options"));

					}
					if (!layerDef.getString("layer_order").equals("null")) {
						bilayer.setLayerOrder(new Integer(layerDef.getString("layer_order")));

					}
					realResult.add(bilayer);
				}
			}

			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {

			if (tmpSession != null) {
				if (tmpSession.isOpen())
					tmpSession.close();
			}

		}
		return realResult;
	}
	
	
	
	@Override
	public List<SbiGeoLayersRoles> getListRolesById(Integer id) {
		Session tmpSession = getSession();
		List<SbiGeoLayersRoles> roles = new ArrayList<>();

		String hql = " from SbiGeoLayersRoles WHERE layer.layerId =? ";
		Query q = tmpSession.createQuery(hql);
		q.setInteger(0, id);
		roles = q.list();
		if (roles.size() == 0) {
			return null;
		}
		return roles;
	}
	
	private boolean userIsAbilited(List<SbiGeoLayersRoles> roles, IEngUserProfile profile) {
		if (UserUtilities.isAdministrator(profile) || roles == null) {
			return true;
		}
		for (SbiGeoLayersRoles r : roles) {
			Collection<String> rolesProfile;
			try {
				rolesProfile = profile.getRoles();

				Iterator it = rolesProfile.iterator();
				while (it.hasNext()) {
					String roleName = (String) it.next();
					if (roleName.equals(r.getRole().getName())) {
						return true;
					}
				}

			} catch (EMFInternalError e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
}