/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.tools.importexport;

import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.dao.BIObjectDAOHibImpl;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjFunc;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjFuncId;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjPar;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjTemplates;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSnapshots;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubreports;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubreportsId;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFuncRole;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFunctions;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterUseDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParuseId;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParameters;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseCk;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseCkId;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseDet;
import it.eng.spagobi.behaviouralmodel.check.metadata.SbiChecks;
import it.eng.spagobi.behaviouralmodel.lov.metadata.SbiLov;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.commons.metadata.SbiBinContents;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.utilities.FileUtilities;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.commons.utilities.indexing.LuceneIndexer;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.config.dao.IEngineDAO;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmContact;
import it.eng.spagobi.kpi.config.metadata.SbiKpi;
import it.eng.spagobi.kpi.config.metadata.SbiKpiDocument;
import it.eng.spagobi.kpi.config.metadata.SbiKpiInstPeriod;
import it.eng.spagobi.kpi.config.metadata.SbiKpiInstance;
import it.eng.spagobi.kpi.config.metadata.SbiKpiPeriodicity;
import it.eng.spagobi.kpi.config.metadata.SbiKpiRel;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModel;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelInst;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelResources;
import it.eng.spagobi.kpi.model.metadata.SbiResources;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnit;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrant;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrantNodes;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrantNodesId;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitHierarchies;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitNodes;
import it.eng.spagobi.kpi.threshold.metadata.SbiThreshold;
import it.eng.spagobi.kpi.threshold.metadata.SbiThresholdValue;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMapFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMapFeaturesId;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMaps;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;
import it.eng.spagobi.tools.importexport.bo.AssociationFile;
import it.eng.spagobi.tools.importexport.transformers.TransformersUtilities;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetacontents;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetadata;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;
import it.eng.spagobi.tools.udp.metadata.SbiUdpValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

/**
 * Implements the interface which defines methods for managing the import
 * requests
 */
public class ImportManager implements IImportManager, Serializable {

	static private Logger logger = Logger.getLogger(ImportManager.class);

	private String pathImportTmpFolder = "";
	private String pathBaseFolder = "";
	private String pathDBFolder = "";
	private ImporterMetadata importer = null;
	private Properties props = null;
	private SessionFactory sessionFactoryExpDB = null;
	private Session sessionExpDB = null;
	private Transaction txExpDB = null;
	private Session sessionCurrDB = null;
	private Transaction txCurrDB = null;
	private MetadataAssociations metaAss = null;
	private MetadataLogger metaLog = null;
	private UserAssociationsKeeper usrAss = null;
	private String exportedFileName = "";
	private AssociationFile associationFile = null;
	private String impAssMode = IMPORT_ASS_DEFAULT_MODE;


	/**
	 * Prepare the environment for the import procedure.
	 * 
	 * @param pathImpTmpFold The path of the temporary import folder
	 * @param archiveName the name of the compress exported file
	 * @param archiveContent the bytes of the compress exported file
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void init(String pathImpTmpFold, String archiveName, byte[] archiveContent) throws EMFUserError {
		logger.debug("IN");
		// create directories of the tmp import folder
		File impTmpFold = new File(pathImpTmpFold);
		impTmpFold.mkdirs();
		// write content uploaded into a tmp archive
		String pathArchiveFile = pathImpTmpFold + "/" + archiveName;
		File archive = new File(pathArchiveFile);
		exportedFileName = archiveName.substring(0, archiveName.indexOf(".zip"));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(archive);
			fos.write(archiveContent);
			fos.flush();
		} catch (Exception ioe) {
			logger.error("Error while writing archive content into a tmp file ", ioe);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("Error while closing stream", e);
				}
			}
		}
		// decompress archive
		ImportUtilities.decompressArchive(pathImpTmpFold, pathArchiveFile);
		// erase archive file
		archive.delete();
		int lastDotPos = archiveName.lastIndexOf(".");
		if (lastDotPos != -1)
			archiveName = archiveName.substring(0, lastDotPos);
		pathImportTmpFolder = pathImpTmpFold;
		pathBaseFolder = pathImportTmpFolder + "/" + archiveName;
		pathDBFolder = pathBaseFolder + "/metadata";
		String propFilePath = pathBaseFolder + "/export.properties";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propFilePath);
			props = new Properties();
			props.load(fis);
		} catch (Exception e) {
			logger.error("Error while reading properties file ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("Error while closing stream", e);
				}
		}
		importer = new ImporterMetadata();
		sessionFactoryExpDB = ImportUtilities.getHibSessionExportDB(pathDBFolder);
		metaAss = new MetadataAssociations();
		metaLog = new MetadataLogger();
		usrAss = new UserAssociationsKeeper();
		logger.debug("OUT");
	}

	public void openSession() throws EMFUserError {
		logger.debug("IN");
		try  {
			sessionExpDB = sessionFactoryExpDB.openSession();
			txExpDB = sessionExpDB.beginTransaction();
			sessionCurrDB = HibernateUtil.currentSession();
			txCurrDB = sessionCurrDB.beginTransaction();
		} catch (Exception e) {
			logger.error("Error while opening session. May be the import manager was not correctly initialized.", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		}
		logger.debug("OUT");
	}

	public void closeSession() {
		logger.debug("IN");
		if (txExpDB != null && txExpDB.isActive()) {
			txExpDB.rollback();
		}
		if (sessionExpDB != null) {
			if (sessionExpDB.isOpen()) {
				sessionExpDB.close();
			}
		}
		if (txCurrDB != null && txCurrDB.isActive()) {
			txCurrDB.commit();
		}
		if (sessionCurrDB != null) {
			if (sessionCurrDB.isOpen()) {
				sessionCurrDB.close();
			}
		}
		logger.debug("OUT");
	}

	/**
	 * Imports the exported objects.
	 * 
	 * @param overwrite the overwrite
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void importObjects(boolean overwrite, SessionContainer session) throws EMFUserError {
		logger.debug("IN");
		metaLog.log("                                             ");
		metaLog.log("-+-+-+-+-+-+-Begin import objects-+-+-+-+-+-");
		metaLog.log("                                             ");
		updateDataSourceReferences(metaAss.getDataSourceIDAssociation());
		metaLog.log("-------ObjMetadata-----");
		importObjMetadata(overwrite);
		metaLog.log("-------Data Source-----");
		importDataSource(overwrite);
		metaLog.log("-------Data Set-----");
		importDataSet(overwrite, session);
		metaLog.log("-------Roles-----");
		importRoles();
		metaLog.log("-------Engines-----");
		importEngines();
		metaLog.log("-------Functionalities-----");
		importFunctionalities();
		metaLog.log("-------Checks-----");
		importChecks();
		metaLog.log("-------Lov-----");
		importLovs(overwrite);
		metaLog.log("-------Parameters-----");
		importParameters(overwrite);
		metaLog.log("-------Paruse-----");
		//importParuse();
		metaLog.log("-------Paruse Det-----");
		//importParuseDet();
		metaLog.log("-------Paruse check-----");
		//importParuseCheck();
		metaLog.log("-------Funct Roles-----");
		importFunctRoles();
		metaLog.log("-------BiObject-----");
		importBIObjects(overwrite);
		metaLog.log("-------Bi Object Link-----");
		importBIObjectLinks();
		metaLog.log("-------Map Catalogue-----");
		importMapCatalogue(overwrite);
		metaLog.log("-------Threshold-----");
		importThreshold(overwrite);	
		metaLog.log("-------Threshold Value-----");
		importThresholdValue(overwrite);
		metaLog.log("-------Kpi-----");
		importKpi(overwrite);
		metaLog.log("-------Kpi Instance-----");
		importKpiInstance(overwrite);
		metaLog.log("-------Model-----");
		importModel(overwrite);
		metaLog.log("-------Model Instance-----");
		importModelInstance(overwrite);
		metaLog.log("-------Resources-----");
		importResources(overwrite);
		metaLog.log("-------Model Resources-----");
		importModelResources(overwrite);
		metaLog.log("-------Periodicity-----");
		importPeriodicity(overwrite);
		metaLog.log("-------Kpi Instance Periodicity-----");
		importKpiInstPeriod(overwrite);
		metaLog.log("-------Alarm Contact-----");
		importAlarmContact(overwrite);
		metaLog.log("-------Alarm-----");
		importAlarm(overwrite);
		metaLog.log("-------SbiKpiModel Attr-----");
		importKpiModelAttr(overwrite);
		metaLog.log("-------SbiKpiModel Attr Value-----");
		importKpiModelAttrVal(overwrite);
		metaLog.log("-------SbiObjMetacontents -----");
		importObjMetacontent(overwrite);
/*		metaLog.log("-------UDP -----");
		importUdp(overwrite);
		metaLog.log("-------UDP values -----");
		importUdpValues(overwrite);
		metaLog.log("-------OU grants -----");
		importOuGrants(overwrite);
		metaLog.log("-------OU grant nodes -----");
		importOuGrantNodes(overwrite);*/

		logger.debug("OUT");
	}


	/**
	 * Gets the SpagoBI version of the exported file.
	 * 
	 * @return The SpagoBI version of the exported file
	 */
	public String getExportVersion() {
		return props.getProperty("spagobi-version");
	}

	/**
	 * Gets the current SpagobI version.
	 * 
	 * @return The current SpagoBI version
	 */
	public String getCurrentVersion() {
		logger.debug("IN");
		ConfigSingleton conf = ConfigSingleton.getInstance();
		SourceBean curVerSB = (SourceBean) conf.getAttribute("IMPORTEXPORT.CURRENTVERSION");
		String curVer = (String) curVerSB.getAttribute("version");
		logger.debug("OUT");
		return curVer;
	}

	/**
	 * Gets the list of all exported roles.
	 * 
	 * @return The list of exported roles
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List getExportedRoles() throws EMFUserError {
		List exportedRoles = null;
		exportedRoles = importer.getAllExportedRoles(sessionExpDB);
		return exportedRoles;
	}

	/**
	 * Gets the list of all exported engines.
	 * 
	 * @return The list of exported engines
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List getExportedEngines() throws EMFUserError {
		List exportedEngines = null;
		exportedEngines = importer.getAllExportedEngines(sessionExpDB);
		return exportedEngines;
	}

	/**
	 * checks if two or more exported roles are associate to the same current
	 * role.
	 * 
	 * @param roleAssociations Map of assocaition between exported roles and roles of the
	 * portal in use
	 * 
	 * @throws EMFUserError if two ore more exported roles are associate to the same
	 * current role
	 */
	public void checkRoleReferences(Map roleAssociations) throws EMFUserError {
		logger.debug("IN");
		// each exported role should be associate only to one system role
		Set rolesAssKeys = roleAssociations.keySet();
		Iterator iterRoleAssKeys1 = rolesAssKeys.iterator();
		while (iterRoleAssKeys1.hasNext()) {
			Integer roleExpId = (Integer) iterRoleAssKeys1.next();
			Integer roleAssId = (Integer) roleAssociations.get(roleExpId);
			Iterator iterRoleAssKeys2 = rolesAssKeys.iterator();
			while (iterRoleAssKeys2.hasNext()) {
				Integer otherRoleExpId = (Integer) iterRoleAssKeys2.next();
				if (otherRoleExpId.compareTo(roleExpId) != 0) {
					Integer otherRoleAssId = (Integer) roleAssociations.get(otherRoleExpId);
					if (otherRoleAssId.compareTo(roleAssId) == 0) {
						logger.debug("OUT. The checkRoleReferences method doesn't work ");
						throw new EMFUserError(EMFErrorSeverity.ERROR, "8001", "component_impexp_messages");
					}
				}
			}
		}
		logger.debug("OUT");
	}

	/**
	 * Update the data source name for each list of values of type query based on
	 * association between exported data sources and current system data sources.
	 * 
	 * @param mapDataSources Map of the associations between exported data sources and
	 * current system data sources
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void updateDataSourceReferences(Map mapDataSources) throws EMFUserError {
		/*
		 * The key of the map are the name of the exported data sources Each key
		 * value is the name of the current system data source associate
		 */
		importer.updateDSRefs(mapDataSources, sessionExpDB, metaLog);
	}

	/**
	 * Closes Hibernate session factory for the exported database
	 */
	private void closeSessionFactory() {
		if (sessionFactoryExpDB != null) {
			sessionFactoryExpDB.close();
		}
	}

	/**
	 * Rollbacks each previous changes made on exported and current databases
	 */
	private void rollback() {
		if (txExpDB != null && txExpDB.isActive())
			txExpDB.rollback();
		if (txCurrDB != null && txCurrDB.isActive())
			txCurrDB.rollback();
		closeSession();
		closeSessionFactory();
	}

	/**
	 * Commits changes done till now and open a new transaction
	 */
	private void commit() {
		if (txCurrDB != null && txCurrDB.isActive()) {
			txCurrDB.commit();
			txCurrDB = sessionCurrDB.beginTransaction();
		}
	}

	/**
	 * Commits all changes made on exported and current databases.
	 * 
	 * @return the import result info
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public ImportResultInfo commitAllChanges() throws EMFUserError {
		logger.debug("IN");
		// commit all database changes and close hibernate connection
		txCurrDB.commit();
		closeSession();
		closeSessionFactory();
		// clear metadata association
		metaAss.clear();
		// create the import info bean
		ImportResultInfo iri = new ImportResultInfo();
		// create the folder which contains the import result files
		Date now = new Date();
		String folderImportOutcomeName = "import" + now.getTime();
		String pathFolderImportOutcome = pathImportTmpFolder + "/" + folderImportOutcomeName;
		File fileFolderImportOutcome = new File(pathFolderImportOutcome);
		fileFolderImportOutcome.mkdirs();
		// fill the result bean with eventual manual task info
		String pathManualTaskFolder = pathBaseFolder + "/" + ImportExportConstants.MANUALTASK_FOLDER_NAME;
		File fileManualTaskFolder = new File(pathManualTaskFolder);
		if (fileManualTaskFolder.exists()) {
			String[] manualTaskFolders = fileManualTaskFolder.list();
			Map manualTaskMap = new HashMap();
			String nameTask = "";
			for (int i = 0; i < manualTaskFolders.length; i++) {
				try {
					String pathMTFolder = pathManualTaskFolder + "/" + manualTaskFolders[i];
					File fileMTFolder = new File(pathMTFolder);
					if (!fileMTFolder.isDirectory())
						continue;
					String pathFilePropMT = pathManualTaskFolder + "/" + manualTaskFolders[i] + ".properties";
					File filePropMT = new File(pathFilePropMT);
					FileInputStream fisProp = new FileInputStream(filePropMT);
					Properties props = new Properties();
					props.load(fisProp);
					nameTask = props.getProperty("name");
					fisProp.close();
					// copy the properties
					FileOutputStream fosProp = new FileOutputStream(pathFolderImportOutcome + "/"
							+ manualTaskFolders[i] + ".properties");
					props.store(fosProp, "");
					// GeneralUtilities.flushFromInputStreamToOutputStream(fisProp,
					// fosProp, true);
					// create zip of the task folder
					String manualTaskZipFilePath = pathFolderImportOutcome + "/" + manualTaskFolders[i] + ".zip";
					FileOutputStream fosMT = new FileOutputStream(manualTaskZipFilePath);
					ZipOutputStream zipoutMT = new ZipOutputStream(fosMT);
					TransformersUtilities.compressFolder(pathMTFolder, pathMTFolder, zipoutMT);
					zipoutMT.flush();
					zipoutMT.close();
					fosMT.close();
					// put task into the manual task map
					manualTaskMap.put(nameTask, manualTaskZipFilePath);
				} catch (Exception e) {
					logger.error("Error while generatin zip file for manual task " + nameTask, e);
				}
			}
			iri.setManualTasks(manualTaskMap);
		}
		// delete the tmp directory of the current import operation
		FileUtilities.deleteDir(new File(pathBaseFolder));
		// generate the log file
		File logFile = new File(pathFolderImportOutcome + "/" + exportedFileName + ".log");
		if (logFile.exists())
			logFile.delete();
		try {
			FileOutputStream fos = new FileOutputStream(logFile);
			fos.write(metaLog.getLogBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			logger.error("Error while writing log file ", e);
		}
		// generate the association file
		File assFile = new File(pathFolderImportOutcome + "/" + exportedFileName + ".xml");
		if (assFile.exists())
			assFile.delete();
		try {
			FileOutputStream fos = new FileOutputStream(assFile);
			fos.write(usrAss.toXml().getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			logger.error("Error while writing the associations file ", e);
		}
		iri.setFolderName(folderImportOutcomeName);
		// set into the result bean the log file path
		iri.setLogFileName(exportedFileName);
		// set into the result bean the associations file path
		iri.setAssociationsFileName(exportedFileName);
		// return the result info bean
		logger.debug("OUT");
		return iri;
	}


	/**
	 * Import exported roles
	 * 
	 * @throws EMFUserError
	 */
	private void importRoles() throws EMFUserError {
		logger.debug("IN");
		SbiExtRoles role = null;
		try {
			List exportedRoles = importer.getAllExportedSbiObjects(sessionExpDB, "SbiExtRoles", null);
			Iterator iterSbiRoles = exportedRoles.iterator();
			while (iterSbiRoles.hasNext()) {
				role = (SbiExtRoles) iterSbiRoles.next();
				Integer oldId = role.getExtRoleId();
				Map roleIdAss = metaAss.getRoleIDAssociation();
				Set roleIdAssSet = roleIdAss.keySet();
				if (roleIdAssSet.contains(oldId)) {
					metaLog.log("Exported role " + role.getName() + " not inserted"
							+ " because it has been associated to an existing role or it has the same name "
							+ " of an existing role");
					continue;
				}
				SbiExtRoles newRole = ImportUtilities.makeNewSbiExtRole(role);
				String roleCd = role.getRoleTypeCode();
				Map unique = new HashMap();
				unique.put("valuecd", roleCd);
				unique.put("domaincd", "ROLE_TYPE");
				SbiDomains existDom = (SbiDomains) importer.checkExistence(unique, sessionCurrDB, new SbiDomains());
				if (existDom != null) {
					newRole.setRoleType(existDom);
					newRole.setRoleTypeCode(existDom.getValueCd());
				}
				sessionCurrDB.save(newRole);
				metaLog.log("Inserted new role " + newRole.getName());
				Integer newId = newRole.getExtRoleId();
				metaAss.insertCoupleRole(oldId, newId);
			}
		} catch (Exception e) {
			if (role != null) {
				logger.error("Error while importing exported role with name [" + role.getName() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Imports exported engines
	 * 
	 * @throws EMFUserError
	 */
	private void importEngines() throws EMFUserError {
		logger.debug("IN");
		SbiEngines engine = null;
		try {
			List exportedEngines = importer.getAllExportedSbiObjects(sessionExpDB, "SbiEngines", null);
			Iterator iterSbiEngines = exportedEngines.iterator();
			while (iterSbiEngines.hasNext()) {
				engine = (SbiEngines) iterSbiEngines.next();
				Integer oldId = engine.getEngineId();
				Map engIdAss = metaAss.getEngineIDAssociation();
				Set engIdAssSet = engIdAss.keySet();
				if (engIdAssSet.contains(oldId)) {
					metaLog.log("Exported engine " + engine.getName() + " not inserted"
							+ " because it has been associated to an existing engine or it has the same label "
							+ " of an existing engine");
					continue;
				}
				SbiEngines newEng = ImportUtilities.makeNewSbiEngine(engine);
				SbiDomains engineTypeDomain = engine.getEngineType();
				Map uniqueEngineType = new HashMap();
				uniqueEngineType.put("valuecd", engineTypeDomain.getValueCd());
				uniqueEngineType.put("domaincd", "ENGINE_TYPE");
				SbiDomains existEngineTypeDomain = (SbiDomains) importer.checkExistence(uniqueEngineType,
						sessionCurrDB, new SbiDomains());
				if (existEngineTypeDomain != null) {
					newEng.setEngineType(existEngineTypeDomain);
				}
				SbiDomains biobjectTypeDomain = engine.getBiobjType();
				Map uniqueBiobjectType = new HashMap();
				uniqueBiobjectType.put("valuecd", biobjectTypeDomain.getValueCd());
				uniqueBiobjectType.put("domaincd", "BIOBJ_TYPE");
				SbiDomains existBiobjectTypeDomain = (SbiDomains) importer.checkExistence(uniqueBiobjectType,
						sessionCurrDB, new SbiDomains());
				if (existBiobjectTypeDomain != null) {
					newEng.setBiobjType(existBiobjectTypeDomain);
				}
				// check datasource link
				SbiDataSource expDs = engine.getDataSource();
				if (expDs != null) {
					Integer dsId = (Integer) metaAss.getDataSourceIDAssociation().get(new Integer(expDs.getDsId()));
					SbiDataSource localDS = (SbiDataSource) sessionCurrDB.load(SbiDataSource.class, dsId);
					newEng.setDataSource(localDS);
				}
				sessionCurrDB.save(newEng);
				metaLog.log("Inserted new engine " + engine.getName());
				Integer newId = newEng.getEngineId();
				metaAss.insertCoupleEngine(oldId, newId);
			}
		} catch (Exception e) {
			if (engine != null) {
				logger.error("Error while importing exported engine with label [" + engine.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}





	/**
	 *  function to import ObjMetada
	 * @param overwrite
	 * @throws EMFUserError
	 */

	private void importObjMetadata(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiObjMetadata exportedObjMetadata = null;
		try {
			List exportedDS = importer.getAllExportedSbiObjects(sessionExpDB, "SbiObjMetadata", null);
			Iterator iterSbiObjMetadata = exportedDS.iterator();

			while (iterSbiObjMetadata.hasNext()) {
				exportedObjMetadata = (SbiObjMetadata) iterSbiObjMetadata.next();
				Integer oldId = new Integer(exportedObjMetadata.getObjMetaId());
				Integer existingMetadataId= null;
				Map metadataIdAss = metaAss.getObjMetadataIDAssociation();
				Set metadataIdAssSet = metadataIdAss.keySet();
				if (metadataIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported objMetadata " + exportedObjMetadata.getLabel() + " not inserted"
							+ " because exist objMetadata with the same label ");
					continue;
				} else {
					existingMetadataId = (Integer) metadataIdAss.get(oldId);
				}

				if (existingMetadataId != null) {
					logger.info("The objMetadata with label:[" + exportedObjMetadata.getLabel() + "] is just present. It will be updated.");
					metaLog.log("The objMetadata with label = [" + exportedObjMetadata.getLabel() + "] will be updated.");
					SbiObjMetadata existingObjMetadata = ImportUtilities.modifyExistingSbiObjMetadata(exportedObjMetadata, sessionCurrDB, existingMetadataId, metaAss,importer);

					sessionCurrDB.update(existingObjMetadata);
				} else {
					SbiObjMetadata newObjM= ImportUtilities.makeNewSbiObjMetadata(exportedObjMetadata, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newObjM);
					metaLog.log("Inserted new ObjectMetadata " + newObjM.getLabel());
					Integer newId = new Integer(newObjM.getObjMetaId());
					metaAss.insertCoupleObjMetadataIDAssociation(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedObjMetadata != null) {
				logger.error("Error while importing exported ObjectMetadata with label [" + exportedObjMetadata.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}








	private void importDataSource(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiDataSource dataSource = null;
		try {
			List exportedDS = importer.getAllExportedSbiObjects(sessionExpDB, "SbiDataSource", null);
			Iterator iterSbiDataSource = exportedDS.iterator();

			while (iterSbiDataSource.hasNext()) {
				dataSource = (SbiDataSource) iterSbiDataSource.next();
				Integer oldId = new Integer(dataSource.getDsId());
				Integer existingDatasourceId = null;
				Map dsIdAss = metaAss.getDataSourceIDAssociation();
				Set engIdAssSet = dsIdAss.keySet();
				if (engIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported dataSource " + dataSource.getLabel() + " not inserted"
							+ " because exist dataSource with the same label ");
					continue;
				} else {
					existingDatasourceId = (Integer) dsIdAss.get(oldId);
				}

				// if association made by user do not update!
				if(!getUserAssociation().isDataSourceAssociated(oldId)){
					if (existingDatasourceId != null) {
						logger.info("The data source with label:[" + dataSource.getLabel() + "] is just present. It will be updated.");
						metaLog.log("The data source with label = [" + dataSource.getLabel() + "] will be updated.");
						SbiDataSource existingDs = ImportUtilities.modifyExistingSbiDataSource(dataSource, sessionCurrDB, existingDatasourceId);
						ImportUtilities.associateWithExistingEntities(existingDs, dataSource, sessionCurrDB, importer, metaAss);
						sessionCurrDB.update(existingDs);
					} else {
						SbiDataSource newDS = ImportUtilities.makeNewSbiDataSource(dataSource);
						ImportUtilities.associateWithExistingEntities(newDS, dataSource, sessionCurrDB, importer, metaAss);
						Integer newId = (Integer) sessionCurrDB.save(newDS);
						metaLog.log("Inserted new datasource " + newDS.getLabel());
						metaAss.insertCoupleDataSources(oldId, newId);
					}
				}
				else{
					metaLog.log("Not inserted data source with ID " + oldId);					
				}
			}
		} catch (Exception e) {
			if (dataSource != null) {
				logger.error("Error while importing exported datasource with label [" + dataSource.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	private void importDataSet(boolean overwrite, SessionContainer session) throws EMFUserError {
		logger.debug("IN");
		SbiDataSetConfig exportedDataSet = null;
		try {				

			List exportedDatasets = importer.getAllExportedSbiObjects(sessionExpDB, "SbiDataSetConfig", null);
			Iterator iterSbiDataSet = exportedDatasets.iterator();

			while (iterSbiDataSet.hasNext()) {
				exportedDataSet = (SbiDataSetConfig) iterSbiDataSet.next();
				logger.debug("Importing exported dataset with id "+exportedDataSet.getDsId() + " and label "+exportedDataSet.getLabel());
				Integer oldId = new Integer(exportedDataSet.getDsId());
				Integer existingDatasetId = null;
				Map datasetAss = metaAss.getDataSetIDAssociation();
				Set datasetAssSet = datasetAss.keySet();
				if (datasetAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported dataset " + exportedDataSet.getLabel() + " not inserted"
							+ " because exist dataset with the same label ");
					continue;
				} else {
					existingDatasetId = (Integer) datasetAss.get(oldId);
				}
				if (existingDatasetId != null) {
					logger.info("The dataset with label:[" + exportedDataSet.getLabel() + "] is just present. It will be updated. Existing one has id "+existingDatasetId);
					metaLog.log("The dataset with label = [" + exportedDataSet.getLabel() + "] will be updated.");
					SbiDataSetConfig existingDataset = ImportUtilities.modifyExistingSbiDataSet(exportedDataSet, sessionCurrDB, existingDatasetId, sessionExpDB, session);
					ImportUtilities.associateNewSbiDataSethistory(existingDataset, exportedDataSet, sessionCurrDB, sessionExpDB, importer, metaAss, session);
					sessionCurrDB.update(existingDataset);
				} else {
					SbiDataSetConfig newDataset = ImportUtilities.makeNewSbiDataSet(exportedDataSet, session);
					sessionCurrDB.save(newDataset);
					ImportUtilities.associateNewSbiDataSethistory(newDataset, exportedDataSet, sessionCurrDB, sessionExpDB, importer, metaAss, session);

					metaLog.log("Inserted new dataset " + newDataset.getName());
					Integer newId = new Integer(newDataset.getDsId());
					metaAss.insertCoupleDataSets(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedDataSet != null) {
				logger.error("Error while importing exported dataset with label [" + exportedDataSet.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Imports exported functionalities
	 * 
	 * @throws EMFUserError
	 */
	private void importFunctionalities() throws EMFUserError {
		logger.debug("IN");
		SbiFunctions functToInsert = null;
		try {
			List exportedFuncts = importer.getAllExportedSbiObjects(sessionExpDB, "SbiFunctions", null);

			while (exportedFuncts.size() != 0) {
				Iterator iterSbiFuncts = exportedFuncts.iterator();
				int minEl = 1000;

				SbiFunctions funct = null;

				// search the functionality for insert
				while (iterSbiFuncts.hasNext()) {
					funct = (SbiFunctions) iterSbiFuncts.next();
					String path = funct.getPath();
					int numEl = path.split("/").length; // the number of levels
					if (numEl < minEl) {
						minEl = numEl;
						functToInsert = funct;
					}
				}

				// remove function from list
				exportedFuncts = removeFromList(exportedFuncts, functToInsert);

				logger.info("Insert the Funtionality (Path):" + functToInsert.getPath());

				// insert function
				Integer expId = functToInsert.getFunctId();
				Map functIdAss = metaAss.getFunctIDAssociation();
				Set functIdAssSet = functIdAss.keySet();
				// if the functionality is present skip the insert
				if (functIdAssSet.contains(expId)) {
					logger.info("Exported functionality " + functToInsert.getName() + " not inserted"
							+ " because it has the same label (and the same path) of an existing functionality");
					metaLog.log("Exported functionality " + functToInsert.getName() + " not inserted"
							+ " because it has the same label (and the same path) of an existing functionality");
					continue;
				}
				SbiFunctions newFunct = ImportUtilities.makeNewSbiFunction(functToInsert);
				String functCd = functToInsert.getFunctTypeCd();
				Map unique = new HashMap();
				unique.put("valuecd", functCd);
				unique.put("domaincd", "FUNCT_TYPE");
				SbiDomains existDom = (SbiDomains) importer.checkExistence(unique, sessionCurrDB, new SbiDomains());
				if (existDom != null) {
					newFunct.setFunctType(existDom);
					newFunct.setFunctTypeCd(existDom.getValueCd());
				}
				String path = newFunct.getPath();
				String parentPath = path.substring(0, path.lastIndexOf('/'));
				Query hibQuery = sessionCurrDB.createQuery(" from SbiFunctions where path = '" + parentPath + "'");
				SbiFunctions functParent = (SbiFunctions) hibQuery.uniqueResult();
				if (functParent != null) {
					newFunct.setParentFunct(functParent);
				}
				// manages prog column that determines the folders order
				if (functParent == null)
					newFunct.setProg(new Integer(1));
				else {
					// loads sub functionalities
					Query query = sessionCurrDB
					.createQuery("select max(s.prog) from SbiFunctions s where s.parentFunct.functId = "
							+ functParent.getFunctId());
					Integer maxProg = (Integer) query.uniqueResult();
					if (maxProg != null)
						newFunct.setProg(new Integer(maxProg.intValue() + 1));
					else
						newFunct.setProg(new Integer(1));
				}
				sessionCurrDB.save(newFunct);
				metaLog.log("Inserted new functionality " + newFunct.getName() + " with path " + newFunct.getPath());
				Integer newId = newFunct.getFunctId();
				metaAss.insertCoupleFunct(expId, newId);

			}
		} catch (Exception e) {
			if (functToInsert != null) {
				logger.error("Error while importing exported functionality with path [" + functToInsert.getPath() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}

	}

	private List removeFromList(List complete, SbiFunctions funct) {
		logger.debug("IN");
		List toReturn = new ArrayList();
		Iterator iterList = complete.iterator();
		while (iterList.hasNext()) {
			SbiFunctions listFunct = (SbiFunctions) iterList.next();
			if (!listFunct.getPath().equals(funct.getPath())) {
				toReturn.add(listFunct);
			}
		}
		logger.debug("OUT");
		return toReturn;
	}

	/**
	 * Import exported lovs
	 * 
	 * @throws EMFUserError
	 */
	private void importLovs(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiLov exportedLov = null;
		try {
			List exportedLovs = importer.getAllExportedSbiObjects(sessionExpDB, "SbiLov", null);
			Iterator iterSbiLovs = exportedLovs.iterator();
			while (iterSbiLovs.hasNext()) {
				exportedLov = (SbiLov) iterSbiLovs.next();
				Integer oldId = exportedLov.getLovId();
				Integer existingLovId = null; 
				Map lovIdAss = metaAss.getLovIDAssociation();
				Set lovIdAssSet = lovIdAss.keySet();
				if (lovIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported lov " + exportedLov.getName() + " not inserted"
							+ " because it has the same label of an existing lov");
					continue;
				} else {
					existingLovId = (Integer) lovIdAss.get(oldId);
				}
				if (existingLovId != null) {
					logger.info("The lov with label:[" + exportedLov.getLabel() + "] is just present. It will be updated.");
					metaLog.log("The lov with label = [" + exportedLov.getLabel() + "] will be updated.");
					SbiLov existinglov = ImportUtilities.modifyExistingSbiLov(exportedLov, sessionCurrDB, existingLovId, getUserAssociation().getDsExportedToUserLabel());
					ImportUtilities.associateWithExistingEntities(existinglov, exportedLov, sessionCurrDB, importer, metaAss);
					sessionCurrDB.update(existinglov);
				} else {
					SbiLov newlov = ImportUtilities.makeNewSbiLov(exportedLov, getUserAssociation().getDsExportedToUserLabel());
					ImportUtilities.associateWithExistingEntities(newlov, exportedLov, sessionCurrDB, importer, metaAss);
					sessionCurrDB.save(newlov); 
					metaLog.log("Inserted new lov " + newlov.getName());
					Integer newId = newlov.getLovId();
					metaAss.insertCoupleLov(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedLov != null) {
				logger.error("Error while importing exported lov with label [" + exportedLov.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Import exported checks
	 * 
	 * @throws EMFUserError
	 */
	private void importChecks() throws EMFUserError {
		logger.debug("IN");
		SbiChecks check = null;
		try {
			List exportedChecks = importer.getAllExportedSbiObjects(sessionExpDB, "SbiChecks", null);
			Iterator iterSbiChecks = exportedChecks.iterator();
			while (iterSbiChecks.hasNext()) {
				check = (SbiChecks) iterSbiChecks.next();
				Integer oldId = check.getCheckId();
				Map checkIdAss = metaAss.getCheckIDAssociation();
				Set checkIdAssSet = checkIdAss.keySet();
				if (checkIdAssSet.contains(oldId)) {
					metaLog.log("Exported check " + check.getName() + " not inserted"
							+ " because it has the same label of an existing check");
					continue;
				}
				SbiChecks newck = ImportUtilities.makeNewSbiCheck(check);
				String valueCd = check.getValueTypeCd();
				Map unique = new HashMap();
				unique.put("valuecd", valueCd);
				unique.put("domaincd", "CHECK");
				SbiDomains existDom = (SbiDomains) importer.checkExistence(unique, sessionCurrDB, new SbiDomains());
				if (existDom != null) {
					newck.setCheckType(existDom);
					newck.setValueTypeCd(existDom.getValueCd());
				}
				sessionCurrDB.save(newck);

				metaLog.log("Inserted new check " + newck.getName());
				Integer newId = newck.getCheckId();
				metaAss.insertCoupleCheck(oldId, newId);
			}
		} catch (Exception e) {
			if (check != null) {
				logger.error("Error while importing exported check with label [" + check.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Import exported parameters
	 * 
	 * @throws EMFUserError
	 */
	private void importParameters(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiParameters exportedParameter = null;
		try {
			List exportedParams = importer.getAllExportedSbiObjects(sessionExpDB, "SbiParameters", null);
			Iterator iterSbiParams = exportedParams.iterator();
			while (iterSbiParams.hasNext()) {
				exportedParameter = (SbiParameters) iterSbiParams.next();
				Integer oldId = exportedParameter.getParId();
				Integer existingParId = null;
				Map paramIdAss = metaAss.getParameterIDAssociation();
				Set paramIdAssSet = paramIdAss.keySet();
				if (paramIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported parameter " + exportedParameter.getName() + " not inserted"
							+ " because it has the same label of an existing parameter");
					continue;
				} else {
					existingParId = (Integer) paramIdAss.get(oldId);
				}
				Integer newIdPar=null;
				// parameter is already present and overwrite==true b(exstingParId)
				if (existingParId != null) {
					logger.info("The parameter with label:[" + exportedParameter.getLabel() + "] is just present. It will be updated.");
					metaLog.log("The parameter with label = [" + exportedParameter.getLabel() + "] will be updated.");
					SbiParameters existingParameter = ImportUtilities.modifyExistingSbiParameter(exportedParameter, sessionCurrDB, existingParId);
					ImportUtilities.associateWithExistingEntities(existingParameter, exportedParameter, sessionCurrDB, importer, metaAss);
					sessionCurrDB.update(existingParameter);
					newIdPar=existingParId;

					// delete the paruse associated to the previous!
					//					List exportedParuses = importer.getAllExportedSbiObjects(sessionCurrDB, "SbiParuse", null);
					//					Iterator iterSbiParuses = exportedParuses.iterator();
					//					while (iterSbiParuses.hasNext()) {
					//					SbiParuse paruse = (SbiParuse) iterSbiParuses.next();
					//					SbiParameters param = paruse.getSbiParameters();

					// Delete the UseParameters Object

					IParameterUseDAO iParameterUseDAO=DAOFactory.getParameterUseDAO();
					//					iParameterUseDAO.eraseParameterUseByParIdSameSession(newIdPar, sessionCurrDB, txCurrDB);
					//deleteParameterUseByParId(newIdPar);
					iParameterUseDAO.eraseParameterUseByParIdSameSession(newIdPar, sessionCurrDB);

					//					List exportedParuses = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiParuse", "sbiParameters", existingParId);					
					//					Iterator iterSbiParuses = exportedParuses.iterator();
					//					while (iterSbiParuses.hasNext()) {
					//					SbiParuse paruse = (SbiParuse) iterSbiParuses.next();
					//					sessionCurrDB.delete(paruse);
					//					}
				} else {
					// parameter is new (new Id)
					SbiParameters newPar = ImportUtilities.makeNewSbiParameter(exportedParameter);
					ImportUtilities.associateWithExistingEntities(newPar, exportedParameter, sessionCurrDB, importer, metaAss);
					sessionCurrDB.save(newPar);
					metaLog.log("Inserted new parameter " + newPar.getName());
					Integer newId = newPar.getParId();
					metaAss.insertCoupleParameter(oldId, newId);
					newIdPar=newId;
				}
				importParuse(oldId);
			}
		} catch (Exception e) {
			if (exportedParameter != null) {
				logger.error("Error while importing exported parameter with label [" + exportedParameter.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	private void deleteParameterUseByParId(Integer idPar) throws EMFUserError{
		List parUseList = null;

		IParameterUseDAO parUseDAO = DAOFactory.getParameterUseDAO();
		parUseList = parUseDAO.loadParametersUseByParId(idPar);

		for (Iterator iterator = parUseList.iterator(); iterator.hasNext();) {
			Object o =  iterator.next();
			ParameterUse parameterUse = (ParameterUse) o;
			SbiParuse sbiParuse = parUseDAO.loadById(parameterUse.getId());

			Set checks =	sbiParuse.getSbiParuseCks();
			Set dets =	sbiParuse.getSbiParuseDets();

			for (Iterator iterator2 = dets.iterator(); iterator2.hasNext();) {
				SbiParuseDet det = (SbiParuseDet) iterator2.next();
				sessionCurrDB.delete(det);
			}
			for (Iterator iterator2 = checks.iterator(); iterator2.hasNext();) {
				SbiParuseCk check = (SbiParuseCk) iterator2.next();
				sessionCurrDB.delete(check);
			}

			sessionCurrDB.delete(sbiParuse);

		}

	}

	/**
	 * import exported biobjects
	 * 
	 * @throws EMFUserError
	 */
	private void importBIObjects(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiObjects exportedObj = null;
		try {
			List exportedBIObjs = importer.getAllExportedSbiObjects(sessionExpDB, "SbiObjects", "label");
			Iterator iterSbiObjs = exportedBIObjs.iterator();
			
		    LuceneIndexer indexer = new LuceneIndexer();
		    
			while (iterSbiObjs.hasNext()) {
				exportedObj = (SbiObjects) iterSbiObjs.next();
				Integer expId = exportedObj.getBiobjId();
				Integer existingObjId = null;
				Map objIdAss = metaAss.getBIobjIDAssociation();
				Set objIdAssSet = objIdAss.keySet();
				if (objIdAssSet.contains(expId) && !overwrite) {
					metaLog.log("Exported biobject "+exportedObj.getName()+" not inserted" +
					" because it has the same label of an existing biobject");
					continue;
				} else {
					existingObjId = (Integer) objIdAss.get(expId);
				}

				SbiObjects obj = null;
				if (existingObjId != null) {
					logger.info("The document with label:[" + exportedObj.getLabel() + "] is just present. It will be updated.");
					metaLog.log("The document with label = [" + exportedObj.getLabel() + "] will be updated.");
					obj = ImportUtilities.modifyExistingSbiObject(exportedObj, sessionCurrDB, existingObjId);
					ImportUtilities.associateWithExistingEntities(obj, exportedObj, sessionCurrDB, importer, metaAss);
					sessionCurrDB.update(obj);
				} else {
					obj = ImportUtilities.makeNewSbiObject(exportedObj);
					ImportUtilities.associateWithExistingEntities(obj, exportedObj, sessionCurrDB, importer, metaAss);
					// insert document
					Integer newId = (Integer) sessionCurrDB.save(obj);
					metaLog.log("Inserted new biobject " + obj.getName());
					metaAss.insertCoupleBIObj(expId, newId);
				}
				// manage object template
				insertObjectTemplate(obj, exportedObj.getBiobjId());
				// manage sub_object here
				insertSubObject(obj, exportedObj);
				// manage snapshot here
				insertSnapshot(obj, exportedObj);
				// insert object into folders tree
				importFunctObject(exportedObj.getBiobjId());
				// puts parameters into object
				importBIObjPar(exportedObj.getBiobjId());
				// puts dependencies into object
				importObjParUse(exportedObj.getBiobjId());

				commit();

				//updates lucene index
		    	BIObjectDAOHibImpl daoObj = (BIObjectDAOHibImpl)DAOFactory.getBIObjectDAO();
		    	BIObject biObj = daoObj.toBIObject(obj);
				indexer.addBiobjToIndex(biObj);

				// TODO controllare che fa questo e se serve!!!
				//updateSubObject(obj, exportedObj.getBiobjId());
			}
		} catch (Exception e) {
			if (exportedObj != null) {
				logger.error("Error while importing exported biobject with label [" + exportedObj.getLabel() + "]", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	private void insertSnapshot(SbiObjects obj, SbiObjects exportedObj) throws EMFUserError {
		logger.debug("IN");
		List exportedSnapshotsList = null;
		List currentSnapshotsList = null;
		SbiSnapshots expSbiSnapshots = null;
		try {
			Query hibQuery = sessionExpDB.createQuery(" from SbiSnapshots ot where ot.sbiObject.biobjId = " + exportedObj.getBiobjId());
			exportedSnapshotsList = hibQuery.list();
			if (exportedSnapshotsList.isEmpty()) {
				logger.debug("Exported document with label = [" + exportedObj.getLabel() + "] has no snapshots");
				return;
			}
			hibQuery = sessionCurrDB
			.createQuery(" from SbiSnapshots ot where ot.sbiObject.biobjId = " + obj.getBiobjId());
			currentSnapshotsList = hibQuery.list();
			Iterator exportedSnapshotsListIt = exportedSnapshotsList.iterator();
			while (exportedSnapshotsListIt.hasNext()) {
				expSbiSnapshots = (SbiSnapshots) exportedSnapshotsListIt.next();
				if (isAlreadyExisting(expSbiSnapshots, currentSnapshotsList)) {
					logger.info("Exported snaphost with name = [" + expSbiSnapshots.getName() + "] and creation date = [" + expSbiSnapshots.getCreationDate() + "] (of document with name = [" + exportedObj.getName() + "] and label = [" + exportedObj.getLabel() + "]) is already existing, most likely it is the same snapshot, so it will not be inserted.");
					metaLog.log("Exported snaphost with name = [" + expSbiSnapshots.getName() + "] and creation date = [" + expSbiSnapshots.getCreationDate() + "] (of document with name = [" + exportedObj.getName() + "] and label = [" + exportedObj.getLabel() + "]) is already existing, most likely it is the same snapshot, so it will not be inserted.");
					continue;
				} else {
					SbiSnapshots newSnapshots = ImportUtilities.makeNewSbiSnapshots(expSbiSnapshots);
					newSnapshots.setSbiObject(obj);
					SbiBinContents binary = insertBinaryContent(expSbiSnapshots.getSbiBinContents());
					newSnapshots.setSbiBinContents(binary);
					sessionCurrDB.save(newSnapshots);
				}
			}
		} catch (Exception e) {
			if (expSbiSnapshots != null) {
				logger.error("Error while importing exported snapshot with name [" + expSbiSnapshots.getName() + "] " +
						"of biobject with label [" + obj.getLabel() + "]", e);
			}
			logger.error("Error while getting exported template objects ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	private boolean isAlreadyExisting(SbiSnapshots expSbiSnapshots,
			List currentSnapshotsList) {
		Iterator currentSnapshotsListIt = currentSnapshotsList.iterator();
		while (currentSnapshotsListIt.hasNext()) {
			SbiSnapshots currentSnapshot = (SbiSnapshots) currentSnapshotsListIt.next();
			if (((currentSnapshot.getName() == null && expSbiSnapshots.getName() == null) || 
					(currentSnapshot.getName() != null && currentSnapshot.getName().equals(expSbiSnapshots.getName()))) 
					&& ((currentSnapshot.getDescription() == null && expSbiSnapshots.getDescription() == null) || 
							(currentSnapshot.getDescription() != null && currentSnapshot.getDescription().equals(expSbiSnapshots.getDescription())))
							&& currentSnapshot.getCreationDate().equals(expSbiSnapshots.getCreationDate())) {
				return true;
			}
		}
		return false;
	}


	private void updateSnapshot(SbiObjects obj, Integer objIdExp) throws EMFUserError {
		logger.debug("IN");
		List subObjList = null;
		SbiSnapshots expSbiSnapshots = null;
		try {
			Query hibQuery = sessionCurrDB.createQuery(" from SbiSnapshots ot where ot.sbiObject.biobjId = " + obj.getBiobjId());
			subObjList = hibQuery.list();
			if (subObjList.isEmpty()) {
				logger.warn(" Error during reading of Existing SbiSnapshots");
			}
			SbiSnapshots existingSbiSnapshots = (SbiSnapshots) subObjList.get(0);

			hibQuery = sessionExpDB.createQuery(" from SbiSnapshots ot where ot.sbiObject.biobjId = " + objIdExp);
			subObjList = hibQuery.list();
			if (subObjList.isEmpty()) {
				logger.warn(" SbiSnapshots is not present");
				return;
			}

			expSbiSnapshots = (SbiSnapshots) subObjList.get(0);

			existingSbiSnapshots.setCreationDate(expSbiSnapshots.getCreationDate());
			existingSbiSnapshots.setDescription(expSbiSnapshots.getDescription());
			existingSbiSnapshots.setName(expSbiSnapshots.getName());
			//existingSbiSnapshots.setSbiObject(obj);
			SbiBinContents existingBinaryContent=existingSbiSnapshots.getSbiBinContents();
			sessionCurrDB.delete(existingBinaryContent);
			SbiBinContents binary = insertBinaryContent(expSbiSnapshots.getSbiBinContents());
			existingSbiSnapshots.setSbiBinContents(binary);
			sessionCurrDB.update(existingSbiSnapshots);

		} catch (Exception e) {
			if (expSbiSnapshots != null) {
				logger.error("Error while updating exported snapshot with name [" + expSbiSnapshots.getName() + "] " +
						"of biobject with label [" + obj.getLabel() + "]", e);
			}
			logger.error("Error while getting exported template objects ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}    

	private void updateSubObject(SbiObjects obj, Integer objIdExp) throws EMFUserError {
		logger.debug("IN");
		List subObjList = null;
		try {
			// read the existing sub object
			Query hibQuery = sessionCurrDB
			.createQuery(" from SbiSubObjects ot where ot.sbiObject.biobjId = " + obj.getBiobjId());
			subObjList = hibQuery.list();
			if (subObjList.isEmpty()) {
				logger.warn(" Existing Sub Object is not present");
			}	
			SbiSubObjects existingSubObject = (SbiSubObjects) subObjList.get(0);
			if (existingSubObject==null){
				logger.warn("Don't read the Existing SubObject ... ERROR");
				return;
			}
			// read the import sub object
			hibQuery = sessionExpDB
			.createQuery(" from SbiSubObjects ot where ot.sbiObject.biobjId = " + objIdExp);
			subObjList = hibQuery.list();
			if (subObjList.isEmpty()) {
				logger.warn(" Sub Object is not present");
				return;
			}
			SbiSubObjects expSubObject = (SbiSubObjects) subObjList.get(0);
			existingSubObject.setCreationDate(expSubObject.getCreationDate());
			existingSubObject.setDescription(expSubObject.getDescription());
			existingSubObject.setLastChangeDate(expSubObject.getLastChangeDate());
			existingSubObject.setIsPublic(expSubObject.getIsPublic());
			existingSubObject.setName(expSubObject.getName());
			existingSubObject.setOwner(expSubObject.getOwner());
			//existingSubObject.setSbiObject(obj);
			SbiBinContents existingBinaryContent=existingSubObject.getSbiBinContents();
			sessionCurrDB.delete(existingBinaryContent);
			SbiBinContents binary = insertBinaryContent(expSubObject.getSbiBinContents());
			existingSubObject.setSbiBinContents(binary);
			sessionCurrDB.update(existingSubObject);

		} catch (HibernateException he) {
			logger.error("Error while getting exported template objects ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	private void insertSubObject(SbiObjects obj, SbiObjects exportedObj) throws EMFUserError {
		logger.debug("IN");
		List exportedSubObjList = null;
		List currentSubObjList = null;
		SbiSubObjects expSubObject = null;
		try {
			Query hibQuery = sessionExpDB
			.createQuery(" from SbiSubObjects ot where ot.sbiObject.biobjId = " + exportedObj.getBiobjId());
			exportedSubObjList = hibQuery.list();
			if (exportedSubObjList.isEmpty()) {
				logger.debug("Exported document with label=[" + exportedObj.getLabel() + "] has no subobjects");
				return;
			}
			hibQuery = sessionCurrDB
			.createQuery(" from SbiSubObjects ot where ot.sbiObject.biobjId = " + obj.getBiobjId());
			currentSubObjList = hibQuery.list();
			Iterator exportedSubObjListIt = exportedSubObjList.iterator();
			Map idAssociation = metaAss.getObjSubObjectIDAssociation();
			while (exportedSubObjListIt.hasNext()) {
				expSubObject = (SbiSubObjects) exportedSubObjListIt.next();
				SbiSubObjects current = isAlreadyExisting(expSubObject, currentSubObjList) ;
				if (current != null) {
					logger.info("Exported subobject with name = [" + expSubObject.getName() + "] and owner = [" + expSubObject.getOwner() + "] and visibility = [" + expSubObject.getIsPublic() + "] and creation date = [" + expSubObject.getCreationDate() + "] (of document with name = [" + exportedObj.getName() + "] and label = [" + exportedObj.getLabel() + "]) is already existing, so it will not be inserted.");
					metaLog.log("Exported subobject with name = [" + expSubObject.getName() + "] and owner = [" + expSubObject.getOwner() + "] and visibility = [" + expSubObject.getIsPublic() + "] and creation date = [" + expSubObject.getCreationDate() + "] (of document with name = [" + exportedObj.getName() + "] and label = [" + exportedObj.getLabel() + "]) is already existing, most likely it is the same subobject, so it will not be inserted.");
					// if already present don't modify the subObject so don't map the ID!
					//idAssociation.put(expSubObject.getSubObjId(), current.getSubObjId());
					continue;
				} else {
					SbiSubObjects newSubObj = ImportUtilities.makeNewSbiSubObjects(expSubObject);
					newSubObj.setSbiObject(obj);
					SbiBinContents binary = insertBinaryContent(expSubObject.getSbiBinContents());
					newSubObj.setSbiBinContents(binary);
					sessionCurrDB.save(newSubObj);
					idAssociation.put(expSubObject.getSubObjId(), newSubObj.getSubObjId());
				}
			}
		} catch (Exception e) {
			if (expSubObject != null) {
				logger.error("Error while importing exported subobject with name [" + expSubObject.getName() + "] " +
						"of biobject with label [" + exportedObj.getLabel() + "]", e);
			}
			logger.error("Error while getting exported template objects ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Controls if a subobject is already existing (i.e. they have the same name, owner, visibility, 
	 * creation date and last modification date)
	 * @param expSubObject
	 * @param currentSubObjList
	 * @return the subobject if is already existing, null otherwise
	 */
	private SbiSubObjects isAlreadyExisting(SbiSubObjects expSubObject,
			List currentSubObjList) {
		Iterator currentSubObjListIt = currentSubObjList.iterator();
		while (currentSubObjListIt.hasNext()) {
			SbiSubObjects currentSubObject = (SbiSubObjects) currentSubObjListIt.next();
			if (((currentSubObject.getName() == null && expSubObject.getName() == null) ||
					(currentSubObject.getName() != null && currentSubObject.getName().equals(expSubObject.getName())))  
					&& ((currentSubObject.getOwner() == null && expSubObject.getOwner() == null) ||
							(currentSubObject.getOwner() != null && currentSubObject.getOwner().equals(expSubObject.getOwner())))
							&& currentSubObject.getIsPublic().equals(expSubObject.getIsPublic())
							&& currentSubObject.getCreationDate().equals(expSubObject.getCreationDate())
							&& currentSubObject.getLastChangeDate().equals(expSubObject.getLastChangeDate())) {
				return currentSubObject;
			}
		}
		return null;
	} 


	private void insertObjectTemplate(SbiObjects obj, Integer objIdExp) throws EMFUserError {
		logger.debug("IN");
		List templateList = null;
		try {
			Query hibQuery = sessionExpDB.createQuery(" from SbiObjTemplates ot where ot.sbiObject.biobjId = "
					+ objIdExp);
			templateList = hibQuery.list();
			if (templateList.isEmpty()) {
				logger.warn("WARN: exported document with id = " + objIdExp + " has no template");
				return;
			}
			// finds the next prog value
			Integer nextProg = getNextProg(obj.getBiobjId());

			SbiObjTemplates expTemplate = (SbiObjTemplates) templateList.get(0);
			SbiObjTemplates newObj = ImportUtilities.makeNewSbiObjTemplates(expTemplate);
			newObj.setProg(nextProg);
			if (nextProg.intValue() > 1) {
				// old current template is no more active
				logger.debug("Update template...");
				SbiObjTemplates existingObjTemplate = getCurrentActiveTemplate(obj.getBiobjId());
				existingObjTemplate.setActive(new Boolean(false));
				sessionCurrDB.save(existingObjTemplate);
			}
			newObj.setSbiObject(obj);
			SbiBinContents binary = insertBinaryContent(expTemplate.getSbiBinContents());
			newObj.setSbiBinContents(binary);
			sessionCurrDB.save(newObj);
		} catch (Exception he) {
			logger.error("Error while getting exported template objects ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	private SbiObjTemplates getCurrentActiveTemplate(Integer biobjId) {
		logger.debug("IN");
		String hql = "from SbiObjTemplates sot where sot.active=true and sot.sbiObject.biobjId=" + biobjId;
		Query query = sessionCurrDB.createQuery(hql);
		SbiObjTemplates hibObjTemp = (SbiObjTemplates)query.uniqueResult();
		logger.debug("OUT");
		return hibObjTemp;
	}

	private Integer getNextProg(Integer objId) {
		logger.debug("IN");
		String hql = "select max(sot.prog) as maxprog from SbiObjTemplates sot where sot.sbiObject.biobjId = " + objId;
		Query query = sessionCurrDB.createQuery(hql);
		Integer maxProg = (Integer) query.uniqueResult();
		if (maxProg == null) {
			maxProg = new Integer(1);
		} else {
			maxProg = new Integer(maxProg.intValue() + 1);
		}
		logger.debug("OUT");
		return maxProg;
	}

	private SbiBinContents insertBinaryContent(SbiBinContents binaryContent) throws EMFUserError {
		logger.debug("IN");
		List templateList = null;
		SbiBinContents newObj = null;
		try {
			Query hibQuery = sessionExpDB.createQuery(" from SbiBinContents where id = " + binaryContent.getId());
			templateList = hibQuery.list();
			if (templateList.isEmpty()) {
				logger.warn(" Binary Content is not present");
				return null;
			}
			SbiBinContents expBinaryContent = (SbiBinContents) templateList.get(0);
			newObj = ImportUtilities.makeNewSbiBinContents(expBinaryContent);
			// save new binary content
			sessionCurrDB.save(newObj);
			return newObj;

		} catch (HibernateException he) {
			logger.error("Error while getting exported binary content objects ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Imports exported paruses
	 * 
	 * @throws EMFUserError
	 */
	// overwrite will be surely true or the paramete is new
	private void importParuse(Integer oldParameterId) throws EMFUserError {
		logger.debug("IN");
		// delete previous paruse!
		SbiParuse paruse = null;
		try {
			//List exportedParuses = importer.getAllExportedSbi(sessionExpDB, "SbiParuse", null);
			List exportedParuses = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiParuse", "sbiParameters", oldParameterId);
			Iterator iterSbiParuses = exportedParuses.iterator();
			while (iterSbiParuses.hasNext()) {
				paruse = (SbiParuse) iterSbiParuses.next();

				SbiParameters param = paruse.getSbiParameters();
				// recover param and lov to insert into relationship
				Integer oldParamId = param.getParId();
				Map assParams = metaAss.getParameterIDAssociation();
				Integer newParamId = (Integer) assParams.get(oldParamId);
				if (newParamId != null) {
					SbiParameters newParam = ImportUtilities.makeNewSbiParameter(param, newParamId);
					paruse.setSbiParameters(newParam);
				}

				SbiLov lov = paruse.getSbiLov();
				if (lov != null) {
					Integer oldLovId = lov.getLovId();
					Map assLovs = metaAss.getLovIDAssociation();
					Integer newLovId = (Integer) assLovs.get(oldLovId);
					if (newLovId != null) {
						SbiLov newlov = ImportUtilities.makeNewSbiLov(lov, newLovId, null);
						paruse.setSbiLov(newlov);
					}
				}

				Integer oldId = paruse.getUseId();
				Integer existingParUseId = null;
				Map paruseIdAss = metaAss.getParuseIDAssociation();
				Set paruseIdAssSet = paruseIdAss.keySet();
				// should not contain
				//				if (paruseIdAssSet.contains(oldId)) {
				//				metaLog.log("Exported parameter use " + paruse.getName() + " not inserted"
				//				+ " because it has the same label of an existing parameter use");
				//				continue;
				//				}
				//				else{
				existingParUseId = (Integer) paruseIdAss.get(oldId);
				//				}

				SbiParuse newParuse = ImportUtilities.makeNewSbiParuse(paruse);
				sessionCurrDB.save(newParuse);
				metaLog.log("Inserted new parameter use " + newParuse.getName() + " for param " + param.getName());
				Integer newId = newParuse.getUseId();
				sessionExpDB.evict(paruse);
				metaAss.insertCoupleParuse(oldId, newId);
				importParuseDet(oldId);
				importParuseCheck(oldId);
			}
		} catch (Exception e) {
			if (paruse != null) {
				logger.error("Error while importing exported parameter use with label [" + paruse.getLabel() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}




	/**
	 * Import exported paruses association with roles
	 * 
	 * @throws EMFUserError
	 */
	//	private void importParuseDet() throws EMFUserError {
	//	logger.debug("IN");
	//	SbiParuseDet parusedet = null;
	//	try {
	//	List exportedParuseDets = importer.getAllExportedSbiObjects(sessionExpDB, "SbiParuseDet", null);
	//	Iterator iterSbiParuseDets = exportedParuseDets.iterator();
	//	while (iterSbiParuseDets.hasNext()) {
	//	parusedet = (SbiParuseDet) iterSbiParuseDets.next();
	//	// get ids of exported role and paruse associzted
	//	Integer paruseid = parusedet.getId().getSbiParuse().getUseId();
	//	Integer roleid = parusedet.getId().getSbiExtRoles().getExtRoleId();
	//	// get association of roles and paruses
	//	Map paruseIdAss = metaAss.getParuseIDAssociation();
	//	Map roleIdAss = metaAss.getRoleIDAssociation();
	//	// try to get from association the id associate to the exported
	//	// metadata
	//	Integer newParuseid = (Integer) paruseIdAss.get(paruseid);
	//	Integer newRoleid = (Integer) roleIdAss.get(roleid);
	//	// build a new SbiParuseDet
	//	SbiParuseDet newParuseDet = ImportUtilities.makeNewSbiParuseDet(parusedet, newParuseid, newRoleid);
	//	// check if the association between metadata already exist
	//	Map unique = new HashMap();
	//	unique.put("paruseid", newParuseid);
	//	unique.put("roleid", newRoleid);
	//	Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiParuseDet());
	//	if (existObj == null) {
	//	sessionCurrDB.save(newParuseDet);
	//	metaLog.log("Inserted new association between paruse " + parusedet.getId().getSbiParuse().getName()
	//	+ " and role " + parusedet.getId().getSbiExtRoles().getName());
	//	}

	//	}
	//	} catch (Exception e) {
	//	if (parusedet != null) {
	//	logger.error("Error while importing association between exported parameter use with label [" + parusedet.getId().getSbiParuse().getLabel()
	//	+ "] and exported role with name [" + parusedet.getId().getSbiExtRoles().getName() + "]", e);
	//	}
	//	logger.error("Error while inserting object ", e);
	//	throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
	//	} finally {
	//	logger.debug("OUT");
	//	}
	//	}

	private void importParuseDet(Integer parUseOldId) throws EMFUserError {
		logger.debug("IN");
		SbiParuseDet parusedet = null;
		try {
			//List exportedParuseDets2 = importer.getAllExportedSbiObjects(sessionExpDB, "SbiParuseDet", null);
			List exportedParuseDets = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiParuseDet", "id.sbiParuse",parUseOldId);

			Iterator iterSbiParuseDets = exportedParuseDets.iterator();
			while (iterSbiParuseDets.hasNext()) {
				parusedet = (SbiParuseDet) iterSbiParuseDets.next();
				// get ids of exported role and paruse associzted
				Integer paruseid = parusedet.getId().getSbiParuse().getUseId();
				Integer roleid = parusedet.getId().getSbiExtRoles().getExtRoleId();
				// get association of roles and paruses
				Map paruseIdAss = metaAss.getParuseIDAssociation();
				Map roleIdAss = metaAss.getRoleIDAssociation();
				// try to get from association the id associate to the exported
				// metadata
				Integer newParuseid = (Integer) paruseIdAss.get(paruseid);
				Integer newRoleid = (Integer) roleIdAss.get(roleid);
				// build a new SbiParuseDet
				SbiParuseDet newParuseDet = ImportUtilities.makeNewSbiParuseDet(parusedet, newParuseid, newRoleid);
				// check if the association between metadata already exist
				Map unique = new HashMap();
				unique.put("paruseid", newParuseid);
				unique.put("roleid", newRoleid);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiParuseDet());
				if (existObj == null) {
					sessionCurrDB.save(newParuseDet);
					metaLog.log("Inserted new association between paruse " + parusedet.getId().getSbiParuse().getName()
							+ " and role " + parusedet.getId().getSbiExtRoles().getName());
				}

			}
		} catch (Exception e) {
			if (parusedet != null) {
				logger.error("Error while importing association between exported parameter use with label [" + parusedet.getId().getSbiParuse().getLabel()
						+ "] and exported role with name [" + parusedet.getId().getSbiExtRoles().getName() + "]", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}



	/**
	 * Imports associations between parameter uses and checks
	 * 
	 * @throws EMFUserError
	 */
	private void importParuseCheck(Integer paruseOldId) throws EMFUserError {
		logger.debug("IN");
		SbiParuseCk paruseck = null;
		try {
			List exportedParuseChecks = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiParuseCk", "id.sbiParuse", paruseOldId);
			Iterator iterSbiParuseChecks = exportedParuseChecks.iterator();
			while (iterSbiParuseChecks.hasNext()) {
				paruseck = (SbiParuseCk) iterSbiParuseChecks.next();
				// get ids of exported paruse and check associzted
				Integer paruseid = paruseck.getId().getSbiParuse().getUseId();
				Integer checkid = paruseck.getId().getSbiChecks().getCheckId();
				// get association of checks and paruses
				Map paruseIdAss = metaAss.getParuseIDAssociation();
				Map checkIdAss = metaAss.getCheckIDAssociation();
				// try to get from association the id associate to the exported
				// metadata
				Integer newParuseid = (Integer) paruseIdAss.get(paruseid);
				Integer newCheckid = (Integer) checkIdAss.get(checkid);
				// build a new paruse check
				SbiParuseCk newParuseCk = ImportUtilities.makeNewSbiParuseCk(paruseck, newParuseid, newCheckid);
				// check if the association between metadata already exist
				Map unique = new HashMap();
				unique.put("paruseid", newParuseid);
				unique.put("checkid", newCheckid);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiParuseCk());
				if (existObj == null) {
					sessionCurrDB.save(newParuseCk);
					metaLog.log("Inserted new association between paruse " + paruseck.getId().getSbiParuse().getName()
							+ " and check " + paruseck.getId().getSbiChecks().getName());
				}

				// build a new id for the SbiParuseCheck
				SbiParuseCkId parusecheckid = paruseck.getId();
				if (newParuseid != null) {
					SbiParuse sbiparuse = parusecheckid.getSbiParuse();
					SbiParuse newParuse = ImportUtilities.makeNewSbiParuse(sbiparuse, newParuseid);
					parusecheckid.setSbiParuse(newParuse);
					paruseid = newParuseid;
				}
				if (newCheckid != null) {
					SbiChecks sbicheck = parusecheckid.getSbiChecks();
					SbiChecks newCheck = ImportUtilities.makeNewSbiCheck(sbicheck, newCheckid);
					parusecheckid.setSbiChecks(newCheck);
					checkid = newCheckid;
				}
				paruseck.setId(parusecheckid);

			}
		} catch (Exception e) {
			if (paruseck != null) {
				logger.error("Error while importing association between exported parameter use with label [" + paruseck.getId().getSbiParuse().getLabel()
						+ "] and exported check with label [" + paruseck.getId().getSbiChecks().getLabel() + "]", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Imports biobject links
	 * 
	 * @throws EMFUserError
	 */
	private void importBIObjectLinks() throws EMFUserError {
		logger.debug("IN");
		SbiSubreports objlink = null;
		try {
			List exportedBIObjectsLinks = importer.getAllExportedSbiObjects(sessionExpDB, "SbiSubreports", null);
			Iterator iterSbiObjLinks = exportedBIObjectsLinks.iterator();
			while (iterSbiObjLinks.hasNext()) {
				objlink = (SbiSubreports) iterSbiObjLinks.next();

				// get biobjects
				SbiObjects masterBIObj = objlink.getId().getMasterReport();
				SbiObjects subBIObj = objlink.getId().getSubReport();
				Integer masterid = masterBIObj.getBiobjId();
				Integer subid = subBIObj.getBiobjId();
				// get association of object
				Map biobjIdAss = metaAss.getBIobjIDAssociation();
				// try to get from association the id associate to the exported
				// metadata
				Integer newMasterId = (Integer) biobjIdAss.get(masterid);
				Integer newSubId = (Integer) biobjIdAss.get(subid);

				// build a new SbiSubreport
				// build a new id for the SbiSubreport
				SbiSubreportsId sbiSubReportsId = objlink.getId();
				SbiSubreportsId newSubReportsId = new SbiSubreportsId();
				if (sbiSubReportsId != null) {
					SbiObjects master = sbiSubReportsId.getMasterReport();
					SbiObjects sub = sbiSubReportsId.getMasterReport();
					SbiObjects newMaster = ImportUtilities.makeNewSbiObject(master, newMasterId);
					SbiObjects newSub = ImportUtilities.makeNewSbiObject(sub, newSubId);
					newSubReportsId.setMasterReport(newMaster);
					newSubReportsId.setSubReport(newSub);
				}
				SbiSubreports newSubReport = new SbiSubreports();
				newSubReport.setId(newSubReportsId);
				// check if the association between metadata already exist
				Map unique = new HashMap();
				unique.put("masterid", newMasterId);
				unique.put("subid", newSubId);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiSubreports());
				if (existObj == null) {
					sessionCurrDB.save(newSubReport);
					metaLog.log("Inserted new link between master object " + masterBIObj.getLabel()
							+ " and sub object " + subBIObj.getLabel());
				}
			}
		} catch (Exception e) {
			if (objlink != null) {
				logger.error("Error while importing association between exported master biobject with label [" + objlink.getId().getMasterReport().getLabel()
						+ "] and exported sub biobject with label [" + objlink.getId().getSubReport().getLabel() + "]", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Imports associations between functionalities and current object
	 * 
	 * @param exportedBIObjectId The id of the current exported object
	 * @throws EMFUserError
	 */
	private void importFunctObject(Integer exportedBIObjectId) throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedFunctObjects = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiObjFunc", "id.sbiObjects.biobjId", exportedBIObjectId);
			Iterator iterSbiFunctObjects = exportedFunctObjects.iterator();
			while (iterSbiFunctObjects.hasNext()) {
				SbiObjFunc objfunct = (SbiObjFunc) iterSbiFunctObjects.next();
				// get ids of exported role, function and state associzted
				Integer functid = objfunct.getId().getSbiFunctions().getFunctId();
				Integer objid = objfunct.getId().getSbiObjects().getBiobjId();
				Integer prog = objfunct.getProg();
				// get association of roles and paruses
				Map functIdAss = metaAss.getFunctIDAssociation();
				Map biobjIdAss = metaAss.getBIobjIDAssociation();
				// try to get from association the id associate to the exported
				// metadata
				Integer newFunctid = (Integer) functIdAss.get(functid);
				Integer newObjectid = (Integer) biobjIdAss.get(objid);
				// build a new id for the SbiObjFunct
				SbiObjFuncId objfunctid = objfunct.getId();
				if (objfunctid != null) {
					SbiFunctions sbifunct = objfunctid.getSbiFunctions();
					SbiFunctions newFunct = ImportUtilities.makeNewSbiFunction(sbifunct, newFunctid);
					objfunctid.setSbiFunctions(newFunct);
					functid = newFunctid;
				}
				if (newObjectid != null) {
					SbiObjects sbiobj = objfunctid.getSbiObjects();
					SbiObjects newObj = ImportUtilities.makeNewSbiObject(sbiobj, newObjectid);
					objfunctid.setSbiObjects(newObj);
					objid = newObjectid;
				}
				objfunct.setId(objfunctid);
				// check if the association between metadata already exist
				Map unique = new HashMap();
				unique.put("objectid", objid);
				unique.put("functionid", functid);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiObjFunc());
				if (existObj == null) {
					sessionCurrDB.save(objfunct);
					metaLog.log("Inserted new association between function "
							+ objfunct.getId().getSbiFunctions().getName() + " and object "
							+ objfunct.getId().getSbiObjects().getName());
				}
			}
		} catch (HibernateException he) {
			logger.error("Error while inserting object ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} catch (Exception e) {
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Imports associations between functionalities and roles
	 * 
	 * @throws EMFUserError
	 */
	private void importFunctRoles() throws EMFUserError {
		logger.debug("IN");
		SbiFuncRole functrole = null;
		try {
			List exportedFunctRoles = importer.getAllExportedSbiObjects(sessionExpDB, "SbiFuncRole", null);
			Iterator iterSbiFunctRoles = exportedFunctRoles.iterator();
			while (iterSbiFunctRoles.hasNext()) {
				functrole = (SbiFuncRole) iterSbiFunctRoles.next();
				// get ids of exported role, function and state associzted
				Integer functid = functrole.getId().getFunction().getFunctId();
				Integer roleid = functrole.getId().getRole().getExtRoleId();
				Integer stateid = functrole.getId().getState().getValueId();
				// get association of roles and paruses
				Map functIdAss = metaAss.getFunctIDAssociation();
				Map roleIdAss = metaAss.getRoleIDAssociation();
				// try to get from association the id associate to the exported
				// metadata
				Integer newFunctid = (Integer) functIdAss.get(functid);
				Integer newRoleid = (Integer) roleIdAss.get(roleid);
				// build a new SbiFuncRole
				SbiFuncRole newFunctRole = ImportUtilities.makeNewSbiFunctRole(functrole, newFunctid, newRoleid);
				// get sbidomain of the current system
				String stateCd = functrole.getStateCd();
				Map uniqueDom = new HashMap();
				uniqueDom.put("valuecd", stateCd);
				uniqueDom.put("domaincd", SpagoBIConstants.PERMISSION_ON_FOLDER);
				SbiDomains existDom = (SbiDomains) importer.checkExistence(uniqueDom, sessionCurrDB, new SbiDomains());
				if (existDom != null) {
					newFunctRole.getId().setState(existDom);
					newFunctRole.setStateCd(existDom.getValueCd());
				}
				// check if the association between metadata already exist
				Map unique = new HashMap();
				unique.put("stateid", existDom.getValueId());
				unique.put("roleid", newRoleid);
				unique.put("functionid", newFunctid);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiFuncRole());
				if (existObj == null) {
					sessionCurrDB.save(newFunctRole);
					metaLog.log("Inserted new association between function "
							+ functrole.getId().getFunction().getName() + " and role "
							+ functrole.getId().getRole().getName());
				}
			}
		} catch (Exception e) {
			if (functrole != null) {
				logger.error("Error while importing association between exported function with path [" + functrole.getId().getFunction().getPath()
						+ "] and exported role with name [" + functrole.getId().getRole().getName() + "]", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Imports associations between parameters and current exported biobject
	 * 
	 * @param exportedBIObjectId The id of the current exported object
	 * @throws EMFUserError
	 */
	private void importBIObjPar(Integer exportedBIObjectId) throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedObjPars = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiObjPar", "sbiObject.biobjId", exportedBIObjectId);
			Iterator iterSbiObjPar = exportedObjPars.iterator();
			while (iterSbiObjPar.hasNext()) {
				SbiObjPar objpar = (SbiObjPar) iterSbiObjPar.next();
				SbiParameters param = objpar.getSbiParameter();
				SbiObjects biobj = objpar.getSbiObject();
				Integer oldParamId = param.getParId();
				Integer oldBIObjId = biobj.getBiobjId();
				Map assBIObj = metaAss.getBIobjIDAssociation();
				Map assParams = metaAss.getParameterIDAssociation();
				Integer newParamId = (Integer) assParams.get(oldParamId);
				Integer newBIObjId = (Integer) assBIObj.get(oldBIObjId);
				if (newParamId != null) {
					SbiParameters newParam = ImportUtilities.makeNewSbiParameter(param, newParamId);
					objpar.setSbiParameter(newParam);
				}
				if (newBIObjId != null) {
					SbiObjects newObj = ImportUtilities.makeNewSbiObject(biobj, newBIObjId);
					objpar.setSbiObject(newObj);
				}
				Integer oldId = objpar.getObjParId();

				// check if the association already exist
				Map uniqueMap = new HashMap();
				uniqueMap.put("biobjid", newBIObjId);
				uniqueMap.put("paramid", newParamId);
				uniqueMap.put("urlname", objpar.getParurlNm());
				Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiObjPar());
				if (existObj != null) {
					metaLog.log("Exported association between object " + objpar.getSbiObject().getName() + " "
							+ " and parameter " + objpar.getSbiParameter().getName() + " with url name "
							+ objpar.getParurlNm() + " not inserted"
							+ " because already existing into the current database");
					continue;
				}

				SbiObjPar newObjpar = ImportUtilities.makeNewSbiObjpar(objpar);
				sessionCurrDB.save(newObjpar);
				metaLog.log("Inserted new biobject parameter with " + newObjpar.getParurlNm() + " for biobject "
						+ newObjpar.getSbiObject().getName());
				Integer newId = newObjpar.getObjParId();
				sessionExpDB.evict(objpar);
				metaAss.insertCoupleObjpar(oldId, newId);
			}
		} catch (HibernateException he) {
			logger.error("Error while inserting object ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} catch (Exception e) {
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Imports biparameter dependencies for current exported biobject
	 * 
	 * @param exportedBIObjectId The id of the current exported biobject
	 * @throws EMFUserError
	 */
	private void importObjParUse(Integer exportedBIObjectId) throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedParDepends = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiObjParuse", "id.sbiObjPar.sbiObject.biobjId", exportedBIObjectId);
			Iterator iterParDep = exportedParDepends.iterator();
			while (iterParDep.hasNext()) {
				SbiObjParuse pardep = (SbiObjParuse) iterParDep.next();
				// get ids of objpar and paruse associated
				Integer objparId = pardep.getId().getSbiObjPar().getObjParId();
				Integer paruseId = pardep.getId().getSbiParuse().getUseId();
				Integer objparfathId = pardep.getId().getSbiObjParFather().getObjParId();
				String filterOp = pardep.getId().getFilterOperation();
				// get association of objpar and paruses
				Map objparIdAss = metaAss.getObjparIDAssociation();
				Map paruseIdAss = metaAss.getParuseIDAssociation();
				// try to get from association the id associate to the exported
				// metadata
				Integer newObjparId = (Integer) objparIdAss.get(objparId);
				Integer newParuseId = (Integer) paruseIdAss.get(paruseId);
				Integer newObjParFathId = (Integer) objparIdAss.get(objparfathId);
				// build a new id for the SbiObjParuse
				SbiObjParuseId objparuseid = pardep.getId();
				objparuseid.setFilterOperation(filterOp);
				if (newParuseId != null) {
					SbiParuse sbiparuse = objparuseid.getSbiParuse();
					SbiParuse newParuse = ImportUtilities.makeNewSbiParuse(sbiparuse, newParuseId);
					objparuseid.setSbiParuse(newParuse);
					paruseId = newParuseId;
				}
				if (newObjparId != null) {
					SbiObjPar sbiobjpar = objparuseid.getSbiObjPar();
					SbiObjPar newObjPar = ImportUtilities.makeNewSbiObjpar(sbiobjpar, newObjparId);
					objparuseid.setSbiObjPar(newObjPar);
					objparId = newObjparId;
				}
				if (newObjParFathId != null) {
					SbiObjPar sbiobjparfath = objparuseid.getSbiObjParFather();
					SbiObjPar newObjParFath = ImportUtilities.makeNewSbiObjpar(sbiobjparfath, newObjParFathId);
					objparuseid.setSbiObjParFather(newObjParFath);
					objparfathId = newObjParFathId;
				}

				pardep.setId(objparuseid);

				Map unique = new HashMap();
				unique.put("objparid", objparId);
				unique.put("paruseid", paruseId);
				unique.put("objparfathid", objparfathId);
				unique.put("filterop", filterOp);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiObjParuse());
				if (existObj == null) {
					sessionCurrDB.save(pardep);
					metaLog.log("Inserted new dependecies between biparameter "
							+ pardep.getId().getSbiObjPar().getLabel() + " of the biobject "
							+ pardep.getId().getSbiObjPar().getSbiObject().getLabel() + " and paruse "
							+ pardep.getId().getSbiParuse().getLabel());
				}
			}
		} catch (HibernateException he) {
			logger.error("Error while inserting object ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} catch (Exception e) {
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Ends the import procedure.
	 */
	public void stopImport() {
		logger.debug("IN");
		metaAss.clear();
		rollback();
		FileUtilities.deleteDir(new File(pathBaseFolder));
		logger.debug("OUT");
	}

	/**
	 * Gets the list of exported data sources.
	 * 
	 * @return List of the exported data sources
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List getExportedDataSources() throws EMFUserError {
		logger.debug("IN");
		List datasources = new ArrayList();
		try {
			List exportedDS = importer.getAllExportedSbiObjects(sessionExpDB, "SbiDataSource", null);
			Iterator iterSbiDataSource = exportedDS.iterator();
			while (iterSbiDataSource.hasNext()) {
				SbiDataSource dataSource = (SbiDataSource) iterSbiDataSource.next();
				DataSource ds = new DataSource();
				ds.setDsId(dataSource.getDsId());
				ds.setLabel(dataSource.getLabel());
				ds.setDescr(dataSource.getDescr());
				ds.setUser(dataSource.getUser());
				ds.setPwd(dataSource.getPwd());
				ds.setDriver(dataSource.getDriver());
				ds.setUrlConnection(dataSource.getUrl_connection());
				ds.setJndi(dataSource.getJndi());
				ds.setDialectId(dataSource.getDialect().getValueId());
				datasources.add(ds);
			}
		} finally {
			logger.debug("OUT");
		}

		logger.debug("OUT");
		return datasources;
	}

	/**
	 * Check the existance of the exported metadata into the current system
	 * metadata and insert their associations into the association object
	 * MeatadataAssociation.
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void checkExistingMetadata() throws EMFUserError {
		logger.debug("IN");

		logger.debug("ObjMetadata check existence");
		List exportedMeta = importer.getAllExportedSbiObjects(sessionExpDB, "SbiObjMetadata", null);
		// use this data structure to save Id-Label, will be used later to have labels
		HashMap<String, String> exportedMetadatasMap = new HashMap<String, String>();
		Iterator iterSbiMeta = exportedMeta.iterator();
		while (iterSbiMeta.hasNext()) {
			SbiObjMetadata metaExp = (SbiObjMetadata) iterSbiMeta.next();
			String labelMeta = metaExp.getLabel();
			exportedMetadatasMap.put(metaExp.getObjMetaId().toString(), metaExp.getLabel());
			Object existObj = importer.checkExistence(labelMeta, sessionCurrDB, new SbiObjMetadata());
			if (existObj != null) {
				SbiObjMetadata metaCurr = (SbiObjMetadata) existObj;
				metaAss.insertCoupleObjMetadataIDAssociation(metaExp.getObjMetaId(), metaCurr.getObjMetaId());
				metaAss.insertCoupleObjMetadataAssociation(metaExp, metaCurr);
				metaLog.log("Found an existing ObjMetadata " + metaCurr.getName() + " with "
						+ "the same label of the exported ObjMetadata " + metaExp.getName());
			}
		}


		List exportedParams = importer.getAllExportedSbiObjects(sessionExpDB, "SbiParameters", null);
		Iterator iterSbiParams = exportedParams.iterator();
		while (iterSbiParams.hasNext()) {
			SbiParameters paramExp = (SbiParameters) iterSbiParams.next();
			String labelPar = paramExp.getLabel();
			Object existObj = importer.checkExistence(labelPar, sessionCurrDB, new SbiParameters());
			if (existObj != null) {
				SbiParameters paramCurr = (SbiParameters) existObj;
				metaAss.insertCoupleParameter(paramExp.getParId(), paramCurr.getParId());
				metaAss.insertCoupleParameter(paramExp, paramCurr);
				metaLog.log("Found an existing Parameter " + paramCurr.getName() + " with "
						+ "the same label of the exported parameter " + paramExp.getName());
			}
		}

		List exportedRoles = importer.getAllExportedSbiObjects(sessionExpDB, "SbiExtRoles", null);
		Iterator iterSbiRoles = exportedRoles.iterator();
		while (iterSbiRoles.hasNext()) {
			SbiExtRoles roleExp = (SbiExtRoles) iterSbiRoles.next();
			String roleName = roleExp.getName();
			Integer expRoleId = roleExp.getExtRoleId();
			Map rolesAss = metaAss.getRoleIDAssociation();
			Set keysExpRoleAss = rolesAss.keySet();
			if (keysExpRoleAss.contains(expRoleId))
				continue;
			Object existObj = importer.checkExistence(roleName, sessionCurrDB, new SbiExtRoles());
			if (existObj != null) {
				SbiExtRoles roleCurr = (SbiExtRoles) existObj;
				metaAss.insertCoupleRole(roleExp.getExtRoleId(), roleCurr.getExtRoleId());
				metaAss.insertCoupleRole(roleExp, roleCurr);
				metaLog.log("Found an existing Role " + roleCurr.getName() + " with "
						+ "the same name of the exported role " + roleExp.getName());
			}
		}
		List exportedParuse = importer.getAllExportedSbiObjects(sessionExpDB, "SbiParuse", null);
		Iterator iterSbiParuse = exportedParuse.iterator();
		while (iterSbiParuse.hasNext()) {
			SbiParuse paruseExp = (SbiParuse) iterSbiParuse.next();
			String label = paruseExp.getLabel();
			SbiParameters par = paruseExp.getSbiParameters();
			Integer idPar = par.getParId();
			// check if the parameter has been associated to a current system
			// parameter
			Map paramsAss = metaAss.getParameterIDAssociation();
			Integer idParAss = (Integer) paramsAss.get(idPar);
			if (idParAss != null) {
				// only if parameter has already been associated there could be association between its modalities
				Map unique = new HashMap();
				unique.put("label", label);
				unique.put("idpar", idParAss);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiParuse());
				if (existObj != null) {
					SbiParuse paruseCurr = (SbiParuse) existObj;
					metaAss.insertCoupleParuse(paruseExp.getUseId(), paruseCurr.getUseId());
					metaAss.insertCoupleParuse(paruseExp, paruseCurr);
					metaLog.log("Found an existing Parameter use " + paruseCurr.getName() + " with "
							+ "the same label of the exported parameter use " + paruseExp.getName());
				}
			}
		}
		List exportedBiobj = importer.getAllExportedSbiObjects(sessionExpDB, "SbiObjects", null);
		Iterator iterSbiBiobj = exportedBiobj.iterator();
		while (iterSbiBiobj.hasNext()) {
			SbiObjects objExp = (SbiObjects) iterSbiBiobj.next();
			String label = objExp.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiObjects());
			if (existObj != null) {
				SbiObjects objCurr = (SbiObjects) existObj;
				metaAss.insertCoupleBIObj(objExp.getBiobjId(), objCurr.getBiobjId());
				metaAss.insertCoupleBIObj(objExp, objCurr);
				metaLog.log("Found an existing BIObject " + objCurr.getName() + " with "
						+ "the same label and path of the exported BIObject " + objExp.getName());
			}
		}
		List exportedLov = importer.getAllExportedSbiObjects(sessionExpDB, "SbiLov", null);
		Iterator iterSbiLov = exportedLov.iterator();
		while (iterSbiLov.hasNext()) {
			SbiLov lovExp = (SbiLov) iterSbiLov.next();
			String label = lovExp.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiLov());
			if (existObj != null) {
				SbiLov lovCurr = (SbiLov) existObj;
				metaAss.insertCoupleLov(lovExp.getLovId(), lovCurr.getLovId());
				metaAss.insertCoupleLov(lovExp, lovCurr);
				metaLog.log("Found an existing Lov " + lovCurr.getName() + " with "
						+ "the same label of the exported lov " + lovExp.getName());
			}
		}
		List exportedFunct = importer.getAllExportedSbiObjects(sessionExpDB, "SbiFunctions", null);
		Iterator iterSbiFunct = exportedFunct.iterator();
		while (iterSbiFunct.hasNext()) {
			SbiFunctions functExp = (SbiFunctions) iterSbiFunct.next();
			String code = functExp.getCode();
			Object existObj = importer.checkExistence(code, sessionCurrDB, new SbiFunctions());
			if (existObj != null) {
				SbiFunctions functCurr = (SbiFunctions) existObj;
				metaAss.insertCoupleFunct(functExp.getFunctId(), functCurr.getFunctId());
				metaAss.insertCoupleFunct(functExp, functCurr);
				metaLog.log("Found an existing Functionality " + functCurr.getName() + " with "
						+ "the same CODE of the exported functionality " + functExp.getName());
			}
		}
		List exportedEngine = importer.getAllExportedSbiObjects(sessionExpDB, "SbiEngines", null);
		Iterator iterSbiEng = exportedEngine.iterator();
		while (iterSbiEng.hasNext()) {
			SbiEngines engExp = (SbiEngines) iterSbiEng.next();
			String label = engExp.getLabel();
			Integer expEngineId = engExp.getEngineId();
			Map engAss = metaAss.getEngineIDAssociation();
			Set keysExpEngAss = engAss.keySet();
			if (keysExpEngAss.contains(expEngineId))
				continue;
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiEngines());
			if (existObj != null) {
				SbiEngines engCurr = (SbiEngines) existObj;
				metaAss.insertCoupleEngine(engExp.getEngineId(), engCurr.getEngineId());
				metaAss.insertCoupleEngine(engExp, engCurr);
				metaLog.log("Found an existing Engine " + engCurr.getName() + " with "
						+ "the same label of the exported engine " + engExp.getName());
			}
		}
		List exportedCheck = importer.getAllExportedSbiObjects(sessionExpDB, "SbiChecks", null);
		Iterator iterSbiCheck = exportedCheck.iterator();
		while (iterSbiCheck.hasNext()) {
			SbiChecks checkExp = (SbiChecks) iterSbiCheck.next();
			String label = checkExp.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiChecks());
			if (existObj != null) {
				SbiChecks checkCurr = (SbiChecks) existObj;
				metaAss.insertCoupleCheck(checkExp.getCheckId(), checkCurr.getCheckId());
				metaAss.insertCoupleCheck(checkExp, checkCurr);
				metaLog.log("Found an existing check " + checkCurr.getName() + " with "
						+ "the same label of the exported check " + checkExp.getName());
			}
		}
		List exportedObjPar = importer.getAllExportedSbiObjects(sessionExpDB, "SbiObjPar", null);
		Iterator iterSbiObjPar = exportedObjPar.iterator();
		while (iterSbiObjPar.hasNext()) {
			SbiObjPar objparExp = (SbiObjPar) iterSbiObjPar.next();
			String urlName = objparExp.getParurlNm();

			Integer objid = objparExp.getSbiObject().getBiobjId();
			Map objIdAss = metaAss.getBIobjIDAssociation();
			Integer newObjid = (Integer) objIdAss.get(objid);
			// only if biobject has already been associated there could be association between its biparameters
			if (newObjid == null)
				continue;

			Integer parid = objparExp.getSbiParameter().getParId();
			Map parIdAss = metaAss.getParameterIDAssociation();
			Integer newParid = (Integer) parIdAss.get(parid);
			// only if parameter has already been associated there could be association between its biparameters
			if (newParid == null)
				continue;

			Map uniqueMap = new HashMap();
			uniqueMap.put("biobjid", newObjid);
			uniqueMap.put("paramid", newParid);
			uniqueMap.put("urlname", urlName);
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiObjPar());

			if (existObj != null) {
				SbiObjPar objParCurr = (SbiObjPar) existObj;
				metaAss.insertCoupleObjpar(objparExp.getObjParId(), objParCurr.getObjParId());
				metaAss.insertCoupleObjpar(objparExp, objParCurr);
				metaLog.log("Found an existing association between object " + objparExp.getSbiObject().getName()
						+ " and parameter " + objparExp.getSbiParameter().getName() + " with " + " the same url "
						+ objparExp.getParurlNm() + " name of the exported objpar ");
			}
		}
		List exportedDs = importer.getAllExportedSbiObjects(sessionExpDB, "SbiDataSource", null);
		Iterator iterSbiDs = exportedDs.iterator();
		while (iterSbiDs.hasNext()) {
			SbiDataSource dsExp = (SbiDataSource) iterSbiDs.next();
			String label = dsExp.getLabel();
			if(metaAss.getDataSourceIDAssociation()!=null && metaAss.getDataSourceIDAssociation().get(dsExp.getDsId())==null){
				Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiDataSource());
				if (existObj != null) {
					SbiDataSource dsCurr = (SbiDataSource) existObj;
					metaAss.insertCoupleDataSources(new Integer(dsExp.getDsId()), new Integer(dsCurr.getDsId()));
					metaLog.log("Found an existing data source " + dsCurr.getLabel() + " with "
							+ "the same label of one exported data source");
				}
			}
			else{
				metaLog.log("User already defined association  for datasource with label" + dsExp.getLabel());				
			}			
		}
		List exportedDataset = importer.getAllExportedSbiObjects(sessionExpDB, "SbiDataSetConfig", null);
		Iterator iterSbiDataset = exportedDataset.iterator();
		while (iterSbiDataset.hasNext()) {
			SbiDataSetConfig dsExp = (SbiDataSetConfig) iterSbiDataset.next();
			String label = dsExp.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiDataSetConfig());
			if (existObj != null) {
				SbiDataSetConfig dsCurr = (SbiDataSetConfig) existObj;
				metaAss.insertCoupleDataSets(new Integer(dsExp.getDsId()), new Integer(dsCurr.getDsId()));
				metaLog.log("Found an existing dataset " + dsCurr.getLabel() + " with "
						+ "the same label of one exported dataset");
			}
		}


		List exportedThreshold = importer.getAllExportedSbiObjects(sessionExpDB, "SbiThreshold", null);
		Iterator iterSbiTh = exportedThreshold.iterator();
		while (iterSbiTh.hasNext()) {
			SbiThreshold dsExp = (SbiThreshold) iterSbiTh.next();
			String code = dsExp.getCode();
			Object existObj = importer.checkExistence(code, sessionCurrDB, new SbiThreshold());
			if (existObj != null) {
				SbiThreshold dsCurr = (SbiThreshold) existObj;
				metaAss.insertCoupleThreshold(new Integer(dsExp.getThresholdId()), new Integer(dsCurr.getThresholdId()));
				metaLog.log("Found an existing threshold " + dsCurr.getCode() + " with "
						+ "the same label of one exported Threshold");
			}
		}



		List exportedThresholdValues = importer.getAllExportedSbiObjects(sessionExpDB, "SbiThresholdValue", null);
		Iterator iterSbiThValue = exportedThresholdValues.iterator();
		while (iterSbiThValue.hasNext()) {
			SbiThresholdValue dsExp = (SbiThresholdValue) iterSbiThValue.next();
			String label = dsExp.getLabel();
			Integer oldThresholdId=dsExp.getSbiThreshold().getThresholdId();
			Map map=metaAss.getTresholdIDAssociation();
			Object newThresholdIdOb=(Object)map.get(oldThresholdId);
			String newThresholdId=null;
			if(newThresholdIdOb!=null){
				newThresholdId=newThresholdIdOb.toString();
			}
			else{
				newThresholdId=oldThresholdId.toString();
			}

			Object existObj = importer.checkExistenceThresholdValue(label, newThresholdId ,sessionCurrDB, new SbiThresholdValue());
			if (existObj != null) {
				SbiThresholdValue dsCurr = (SbiThresholdValue) existObj;
				metaAss.insertCoupleThresholdValue(new Integer(dsExp.getIdThresholdValue()), new Integer(dsCurr.getIdThresholdValue()));
				metaLog.log("Found an existing thresholdValue " + dsCurr.getLabel() + " with "
						+ "the same label of one exported ThresholdValue");
			}
		}






		List exportedKpi = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpi", null);
		Iterator iterSbiKpi = exportedKpi.iterator();
		while (iterSbiKpi.hasNext()) {
			SbiKpi dsExp = (SbiKpi) iterSbiKpi.next();
			String label = dsExp.getCode();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiKpi());
			if (existObj != null) {
				SbiKpi dsCurr = (SbiKpi) existObj;
				metaAss.insertCoupleKpi(new Integer(dsExp.getKpiId()), new Integer(dsCurr.getKpiId()));
				metaLog.log("Found an existing kpi " + dsCurr.getCode() + " with "
						+ "the same label of one exported kpi");
			}
		}


		List exportedModel = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModel", null);
		Iterator iterSbiModel = exportedModel.iterator();
		while (iterSbiModel.hasNext()) {
			SbiKpiModel dsExp = (SbiKpiModel) iterSbiModel.next();
			String label = dsExp.getKpiModelLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiKpiModel());
			if (existObj != null) {
				SbiKpiModel dsCurr = (SbiKpiModel) existObj;
				metaAss.insertCoupleModel(new Integer(dsExp.getKpiModelId()), new Integer(dsCurr.getKpiModelId()));
				metaLog.log("Found an existing model " + dsCurr.getKpiModelLabel() + " with "
						+ "the same label of one exported model");
			}
		}

		List exportedModelInst = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelInst", null);
		Iterator iterSbiModelInst = exportedModelInst.iterator();
		while (iterSbiModelInst.hasNext()) {
			SbiKpiModelInst dsExp = (SbiKpiModelInst) iterSbiModelInst.next();
			String label = dsExp.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiKpiModelInst());
			if (existObj != null) {
				SbiKpiModelInst dsCurr = (SbiKpiModelInst) existObj;
				metaAss.insertCoupleModelInstance(new Integer(dsExp.getKpiModelInst()), new Integer(dsCurr.getKpiModelInst()));
				metaLog.log("Found an existing model instance" + dsCurr.getLabel() + " with "
						+ "the same label of one exported model instance");
			}
		}



		// Kpi Instance  
		// for each model instance get the kpi instance id; then take the kpiInstance of the corresponding model instance; map them

		List exportedModelInst2 = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelInst", null);
		Iterator iterSbiModelInst2 = exportedModelInst.iterator();
		while (iterSbiModelInst2.hasNext()) {
			SbiKpiModelInst dsExp = (SbiKpiModelInst) iterSbiModelInst2.next();

			if(dsExp.getSbiKpiInstance()!=null){

				Integer idKpiInstance = dsExp.getSbiKpiInstance().getIdKpiInstance();
				String label = dsExp.getLabel();

				Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiKpiModelInst());
				if (existObj != null) {
					SbiKpiModelInst dsCurr = (SbiKpiModelInst) existObj;
					if(dsCurr.getSbiKpiInstance()!=null){
						Integer correspondingIdKpiInstance=dsCurr.getSbiKpiInstance().getIdKpiInstance();
						metaAss.insertCoupleKpiInstance(idKpiInstance, correspondingIdKpiInstance);
						metaLog.log("Found an existing kpi instance that, as one of the exported kpi instances, is referred by model instance " + dsCurr.getLabel());
					}
				}
			}
		}

		// Resource

		List exportedResource = importer.getAllExportedSbiObjects(sessionExpDB, "SbiResources", null);
		Iterator iterSbiResources = exportedResource.iterator();
		while (iterSbiResources.hasNext()) {
			SbiResources dsExp = (SbiResources) iterSbiResources.next();
			String code = dsExp.getResourceCode();
			Object existObj = importer.checkExistence(code, sessionCurrDB, new SbiResources());
			if (existObj != null) {
				SbiResources dsCurr = (SbiResources) existObj;
				metaAss.insertCoupleResources(dsExp.getResourceId(), dsCurr.getResourceId());
				metaLog.log("Found an existing resource code " + dsCurr.getResourceCode() + " with "
						+ "the same code of one exported resource");
			}
		}


		// Model Resources

		List exportedModResource = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelResources", null);
		Iterator iterSbiModResources = exportedModResource.iterator();
		while (iterSbiModResources.hasNext()) {
			SbiKpiModelResources dsExp = (SbiKpiModelResources) iterSbiModResources.next();
			String resourceCode = dsExp.getSbiResources().getResourceCode();
			String modelInstLabel=dsExp.getSbiKpiModelInst().getLabel();
			Object existObj = importer.checkExistenceModelResource(modelInstLabel,resourceCode, sessionCurrDB, new SbiKpiModelResources());
			if (existObj != null) {
				SbiKpiModelResources dsCurr = (SbiKpiModelResources) existObj;
				metaAss.insertCoupleModelResources(dsExp.getKpiModelResourcesId(), dsCurr.getKpiModelResourcesId());
				metaLog.log("Found an existing model resource, with id "+dsCurr.getKpiModelResourcesId()+", referring to resource with name "+ dsCurr.getSbiResources().getResourceName() + " and model instance with label "+dsCurr.getSbiKpiModelInst().getLabel() +" ");
			}
		}



		// Periodicity

		List exportedPeriodicity = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiPeriodicity", null);
		Iterator iterSbiPeriodicity = exportedPeriodicity.iterator();
		while (iterSbiPeriodicity.hasNext()) {
			SbiKpiPeriodicity dsPer = (SbiKpiPeriodicity) iterSbiPeriodicity.next();
			String name = dsPer.getName();
			Object existObj = importer.checkExistence(name, sessionCurrDB, new SbiKpiPeriodicity());
			if (existObj != null) {
				SbiKpiPeriodicity dsCurr = (SbiKpiPeriodicity) existObj;
				metaAss.insertCouplePeriodicity(dsPer.getIdKpiPeriodicity(), dsCurr.getIdKpiPeriodicity());
				metaLog.log("Found an existing periodicity " + dsCurr.getName() + " with "
						+ "the same label of one exported periodicity");
			}
		}


		// kpi Instance Period

		List exportedKpiInstPeriod = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiInstPeriod", null);
		Iterator iterSbiKpiInstPeriod = exportedKpiInstPeriod.iterator();
		while (iterSbiKpiInstPeriod.hasNext()) {
			SbiKpiInstPeriod dsInstPer = (SbiKpiInstPeriod) iterSbiKpiInstPeriod.next();
			String periodicityName=dsInstPer.getSbiKpiPeriodicity().getName();
			SbiKpiInstance kpiInst=dsInstPer.getSbiKpiInstance();
			Map kpiInstanceIDMap=metaAss.getKpiInstanceIDAssociation();

			Integer newKpiInstanceId=null;
			Object newKpiInstanceIdO=kpiInstanceIDMap.get(kpiInst.getIdKpiInstance());
			if(newKpiInstanceIdO!=null){
				newKpiInstanceId=(Integer)newKpiInstanceIdO;
			}
			Object existObj = null;
			if(newKpiInstanceId!=null){
				existObj=importer.checkExistenceKpiInstPeriod(newKpiInstanceId,periodicityName, sessionCurrDB, new SbiKpiModelResources());
			}

			if (existObj != null) {
				SbiKpiInstPeriod dsCurr = (SbiKpiInstPeriod) existObj;
				metaAss.insertCoupleKpiInstPeriod(dsInstPer.getKpiInstPeriodId(), dsCurr.getKpiInstPeriodId());
				metaLog.log("Found a kpiInstPeriod, with id "+dsCurr.getKpiInstPeriodId()+" referring to periodicity " + dsCurr.getSbiKpiPeriodicity().getName() + " and kpi instance with id "+dsCurr.getSbiKpiInstance().getIdKpiInstance());			
			}
		}



		List exportedDomains = importer.getAllExportedSbiObjects(sessionExpDB, "SbiDomains", null);
		Iterator iterSbiDomains = exportedDomains.iterator();
		while (iterSbiDomains.hasNext()) {
			SbiDomains dsExp = (SbiDomains) iterSbiDomains.next();
			String label = dsExp.getValueCd();
			Object existObj = importer.checkExistenceDomain(label, dsExp.getDomainCd(), sessionCurrDB, new SbiDomains());
			if (existObj != null) {
				SbiDomains dsCurr = (SbiDomains) existObj;
				metaAss.insertCoupleDomain(new Integer(dsExp.getValueId()), new Integer(dsCurr.getValueId()));
				metaLog.log("Found an existing domain" + dsCurr.getValueCd() + " with "
						+ "the same label of one exported domain");
			}
		}


		// Alarm

		List exportedAlarms = importer.getAllExportedSbiObjects(sessionExpDB, "SbiAlarm", null);
		Iterator iterSbiAlarm = exportedAlarms.iterator();
		while (iterSbiAlarm.hasNext()) {
			SbiAlarm dsExp = (SbiAlarm) iterSbiAlarm.next();
			String label = dsExp.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiAlarm());
			if (existObj != null) {
				SbiAlarm dsCurr = (SbiAlarm) existObj;
				metaAss.insertCoupleAlarm(dsExp.getId(), dsCurr.getId());
				metaLog.log("Found an existing alarm " + dsCurr.getLabel() + " with "
						+ "the same label of one exported alarm");
			}
		}

		// Alarm Contact

		List exportedAlarmContacts = importer.getAllExportedSbiObjects(sessionExpDB, "SbiAlarmContact", null);
		Iterator iterSbiAlarmContacts = exportedAlarmContacts.iterator();
		while (iterSbiAlarmContacts.hasNext()) {
			SbiAlarmContact dsExp = (SbiAlarmContact) iterSbiAlarmContacts.next();
			String name = dsExp.getName();
			Object existObj = importer.checkExistence(name, sessionCurrDB, new SbiAlarmContact());
			if (existObj != null) {
				SbiAlarmContact dsCurr = (SbiAlarmContact) existObj;
				metaAss.insertCoupleAlarmContact(dsExp.getId(), dsCurr.getId());
				metaLog.log("Found an existing alarm contact " + dsCurr.getName() + " with "
						+ "the same name of one exported alarm contact");
			}
		}

		// TODO cambiare con i nuovi UDP VAlues
/*
		List exportedKpiModelAttrs = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelAttr", null);
		Iterator iterSbiKpiModelAttr = exportedKpiModelAttrs.iterator();
		while (iterSbiKpiModelAttr.hasNext()) {
			SbiKpiModelAttr attrExp = (SbiKpiModelAttr) iterSbiKpiModelAttr.next();
			SbiDomains sbiDomain  = attrExp.getSbiDomains();
			String kpiModelAttrCd  = attrExp.getKpiModelAttrCd();
			// get new sbi Domain ID
			Integer newIdDomain = (Integer)metaAss.getDomainIDAssociation().get(sbiDomain.getValueId());
			Object existObj = importer.checkExistenceKpiModelAttr(newIdDomain, kpiModelAttrCd, sessionCurrDB, new SbiKpiModelAttr());
			if (existObj != null) {
				SbiKpiModelAttr attrCurr = (SbiKpiModelAttr) existObj;
				metaAss.insertCoupleSbiKpiModelAttrID(attrExp.getKpiModelAttrId(), attrCurr.getKpiModelAttrId());
				metaLog.log("Found an existing model attr with code " + attrCurr.getKpiModelAttrCd() + " " +
						" and referring to domain "+ sbiDomain.getDomainCd()+" - "+ sbiDomain.getValueCd() +" with "
						+ "the same name of one exported kpi model attr");
			}
		}


		// Model Attr Val

		List exportedKpiModelAttrVals = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelAttrVal", null);
		Iterator iterSbiKpiModelAttrVal = exportedKpiModelAttrVals.iterator();
		while (iterSbiKpiModelAttrVal.hasNext()) {
			SbiKpiModelAttrVal attrValExp = (SbiKpiModelAttrVal) iterSbiKpiModelAttrVal.next();
			Integer kpiModelId = attrValExp.getSbiKpiModel().getKpiModelId();
			Integer kpiModelAttrId = attrValExp.getSbiKpiModelAttr().getKpiModelAttrId();

			// get the new Ids
			Integer newModelId = (Integer)metaAss.getModelIDAssociation().get(kpiModelId);
			Integer newModelAttrId = (Integer)metaAss.getSbiKpiModelAttrIDAssociation().get(kpiModelAttrId);

			// get new sbi Domain ID
			Object existObj = importer.checkExistenceKpiModelAttrVal(newModelAttrId, newModelId, sessionCurrDB, new SbiKpiModelAttrVal());
			if (existObj != null) {
				SbiKpiModelAttrVal attrValCurr = (SbiKpiModelAttrVal) existObj;
				metaAss.insertCoupleSbiKpiModelAttrValID(attrValExp.getKpiModelAttrValId(), attrValCurr.getKpiModelAttrValId());
				metaLog.log("Found an existing model attribute value referring to model" + attrValCurr.getSbiKpiModel().getKpiModelNm()+ " " +
						" and referring to attribute "+ attrValCurr.getSbiKpiModelAttr().getKpiModelAttrCd()+" with "
						+ "the same name of one exported kpi model attr");
			}
		}
*/

		logger.debug("check existence of Object MetaContent, only for Objects!");
		List exportedMetaContent = importer.getAllExportedSbiObjects(sessionExpDB, "SbiObjMetacontents", null);
		Iterator iterSbiModMetaContent = exportedMetaContent.iterator();
		while (iterSbiModMetaContent.hasNext()) {
			SbiObjMetacontents contExp = (SbiObjMetacontents) iterSbiModMetaContent.next();
			String objectLabel = contExp.getSbiObjects().getLabel();
			// metacontent referring to subobjects not referred here because of a previous structure
			if( contExp.getSbiSubObjects() != null ){
				continue;
				//				SbiSubObjects sub = contExp.getSbiSubObjects();
				//				subObjectName = sub.getName();
			}
			Integer objMetaId = contExp.getObjmetaId();

			// I want metadata label
			String metaLabel = exportedMetadatasMap.get(objMetaId.toString());

			Object existObj = importer.checkExistenceObjMetacontent(objectLabel,metaLabel, sessionCurrDB, new SbiObjMetacontents());
			if (existObj != null) {
				SbiObjMetacontents contCurr = (SbiObjMetacontents) existObj;
				//metaAss.insertCoupleObjMeIDAssociation(metaExp.getKpiModelResourcesId(), metaCurr.getKpiModelResourcesId());				
				metaAss.insertCoupleObjMetacontentsIDAssociation(contExp.getObjMetacontentId(), contCurr.getObjMetacontentId());
				metaLog.log("Found an existing metacontents with id " + contCurr.getObjMetacontentId()+ "" +
						"referring to the same object label "+contCurr.getSbiObjects().getLabel()+", " +
						"referring to meta with id "+ contCurr.getObjmetaId()
				);
			}
		}
		// Kpi Relations
		List exportedKpiRelList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiRel", null);
		Iterator iterKpiRel = exportedKpiRelList.iterator();
		while (iterKpiRel.hasNext()) {
			SbiKpiRel kpirel = (SbiKpiRel) iterKpiRel.next();
			// check if the association already exist
			Map uniqueMap = new HashMap();
			Map kpiAss = metaAss.getKpiIDAssociation();
			if(kpirel.getSbiKpiByKpiFatherId() != null){
				Integer newFatherId = (Integer)kpiAss.get(kpirel.getSbiKpiByKpiFatherId().getKpiId());
				uniqueMap.put("fatherId", newFatherId);
				if(kpirel.getSbiKpiByKpiChildId()!= null){
					Integer newChildId = (Integer)kpiAss.get(kpirel.getSbiKpiByKpiChildId().getKpiId());
					uniqueMap.put("childId", newChildId);
					uniqueMap.put("parameter", kpirel.getParameter());
				}
			}
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiKpiRel());
			if (existObj != null) {
				SbiKpiRel dsCurr = (SbiKpiRel) existObj;
				metaAss.insertCoupleKpiRelAssociation(kpirel.getKpiRelId(), dsCurr.getKpiRelId());
				metaLog.log("Found an existing kpi Relation");
			}
		}
		// Udp

		List exportedUdpList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiUdp", null);
		Iterator iterUdp = exportedUdpList.iterator();
		while (iterUdp.hasNext()) {
			SbiUdp udp = (SbiUdp) iterUdp.next();

			//logical unique key but table just looks for label
/*			Map uniqueMap = new HashMap();
			Map doaminAss = metaAss.getDomainIDAssociation();
			Integer newTypeId = (Integer)doaminAss.get(udp.getTypeId());
			uniqueMap.put("typeId", newTypeId);
			Integer newFamilyId = (Integer)doaminAss.get(udp.getFamilyId());
			uniqueMap.put("familyId", newFamilyId);
			uniqueMap.put("label", udp.getLabel());
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiUdp());*/
			String label = udp.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiUdp());
			if (existObj != null) {
				SbiUdp dsCurr = (SbiUdp) existObj;
				metaAss.insertCoupleUdpAssociation(udp.getUdpId(), dsCurr.getUdpId());
				metaLog.log("Exported association between type id " + udp.getTypeId() + " "
						+ " and family id " + udp.getFamilyId() + " with label "
						+ udp.getLabel() + " not inserted"
						+ " because already existing into the current database");
			}
		}
		// Udp Value

		List exportedUdpValList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiUdpValue", null);
		Iterator iterUdpVal = exportedUdpValList.iterator();
		while (iterUdpVal.hasNext()) {
			SbiUdpValue udpVal = (SbiUdpValue) iterUdpVal.next();
			// check if the association already exist
			Map uniqueMap = new HashMap();
			Map kpiAss = metaAss.getKpiIDAssociation();
			Map modelAss = metaAss.getModelIDAssociation();
			Map udpAss = metaAss.getUdpAssociation();
			
			if(udpVal.getSbiUdp() != null){
				Integer newUdpId = (Integer)udpAss.get(udpVal.getSbiUdp().getUdpId());
				uniqueMap.put("udpId", newUdpId);
				Integer newRefId = null;
				if(udpVal.getFamily().equalsIgnoreCase("Kpi")){
					newRefId = (Integer)kpiAss.get(udpVal.getReferenceId());
				}else{
					newRefId = (Integer)modelAss.get(udpVal.getReferenceId());
				}
				uniqueMap.put("referenceId", newRefId);
				uniqueMap.put("family", udpVal.getFamily());
			}
	
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiUdpValue());
			if (existObj != null) {
				SbiUdpValue dsCurr = (SbiUdpValue) existObj;
				metaAss.insertCoupleUdpValueAssociation(udpVal.getUdpValueId(), dsCurr.getUdpValueId());
				metaLog.log("Exported association udp value between udp with label " + udpVal.getSbiUdp().getLabel() + " "
						+ " and family " + udpVal.getFamily() + " with reference id "
						+  udpVal.getReferenceId() + " not inserted"
						+ " because already existing into the current database");
			}
		}
		// OU  SbiOrgUnit
		List exportedSbiOrgUnitList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiOrgUnit", null);
		Iterator iterOUVal = exportedSbiOrgUnitList.iterator();
		while (iterOUVal.hasNext()) {
			SbiOrgUnit ouVal = (SbiOrgUnit) iterOUVal.next();
			Map uniqueMap = new HashMap();
			String label = ouVal.getLabel();
			String name = ouVal.getName();
			uniqueMap.put("label", label);
			uniqueMap.put("name", name);
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiOrgUnit());
			if (existObj != null) {
				SbiOrgUnit dsCurr = (SbiOrgUnit) existObj;
				metaAss.insertCoupleIdOuAssociation(ouVal.getId(), dsCurr.getId());
				metaLog.log("Found an existing ou " + dsCurr.getName() + " with "
						+ "the same label of one exported ou");
			}
		}// OU hierarchy SbiOrgUnitHierarchies 
		List exportedSbiOuHierList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiOrgUnitHierarchies", null);
		Iterator iterOUHierVal = exportedSbiOuHierList.iterator();
		while (iterOUHierVal.hasNext()) {
			SbiOrgUnitHierarchies ouHierVal = (SbiOrgUnitHierarchies) iterOUHierVal.next();
			// check if the association already exist
			Map uniqueMap = new HashMap();
			String label = ouHierVal.getLabel();
			String company = ouHierVal.getCompany();
			uniqueMap.put("label", label);
			uniqueMap.put("company", company);
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiOrgUnitHierarchies());
			if (existObj != null) {
				SbiOrgUnitHierarchies dsCurr = (SbiOrgUnitHierarchies) existObj;
				metaAss.insertCoupleIdOuHierarchyAssociation(ouHierVal.getId(), dsCurr.getId());
				metaLog.log("Found an existing ou hierarchy " + dsCurr.getName() + " with "
						+ "the same label of one exported ou hierarchy");
			}
			/*
			String label = ouHierVal.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiOrgUnitHierarchies());
			if (existObj != null) {
				SbiOrgUnitHierarchies dsCurr = (SbiOrgUnitHierarchies) existObj;
				metaAss.insertCoupleIdOuHierarchyAssociation(ouHierVal.getId(), dsCurr.getId());
				metaLog.log("Found an existing ou hierarchy " + dsCurr.getName() + " with "
						+ "the same label of one exported ou hierarchy");
			}
			*/
		}// OU node  SbiOrgUnitNodes
		List exportedSbiOrgUnitNodeList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiOrgUnitNodes", null);
		Iterator iterOUNodeVal = exportedSbiOrgUnitNodeList.iterator();
		while (iterOUNodeVal.hasNext()) {
			SbiOrgUnitNodes ouVal = (SbiOrgUnitNodes) iterOUNodeVal.next();
			// check if the association already exist
			Map uniqueMap = new HashMap();
			Map ouAss = metaAss.getOuAssociation();
			Map hierAss = metaAss.getOuHierarchiesAssociation();
			if(ouVal.getSbiOrgUnit() != null){
				Integer newOuId = (Integer)ouAss.get(ouVal.getSbiOrgUnit().getId());
				uniqueMap.put("ouId", newOuId);
				Integer newHierId = (Integer)hierAss.get(ouVal.getSbiOrgUnitHierarchies().getId());
				uniqueMap.put("hierarchyId", newHierId);
			}
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiOrgUnitNodes());
			if (existObj != null) {
				SbiOrgUnitNodes dsCurr = (SbiOrgUnitNodes) existObj;
				metaAss.insertCoupleIdOuNodeAssociation(ouVal.getNodeId(), dsCurr.getNodeId());
				metaLog.log("Found an existing ou node " + dsCurr.getNodeId() + " with "
						+ "the same organizational unit and hierarchy of one exported ou node");
			}
		}// OU grants  SbiOrgUnitGrant
		List exportedSbiOUGrantList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiOrgUnitGrant", null);
		Iterator iterOUGrantVal = exportedSbiOUGrantList.iterator();
		while (iterOUGrantVal.hasNext()) {
			SbiOrgUnitGrant ouGrantVal = (SbiOrgUnitGrant) iterOUGrantVal.next();
			String label = ouGrantVal.getLabel();
			Object existObj = importer.checkExistence(label, sessionCurrDB, new SbiOrgUnitGrant());
			if (existObj != null) {
				SbiOrgUnitGrant dsCurr = (SbiOrgUnitGrant) existObj;
				metaAss.insertCoupleIdOuGrantAssociation(ouGrantVal.getId(), dsCurr.getId());
				metaLog.log("Found an existing ou grant " + dsCurr.getId() + " with "
						+ "the same label of one exported ou grant");
			}
		}// OU grant nodes  SbiOrgUnitGrantNodes
		List exportedSbiOUGrantNodeList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiOrgUnitGrantNodes", null);
		Iterator iterOUGrantNodesVal = exportedSbiOUGrantNodeList.iterator();
		while (iterOUGrantNodesVal.hasNext()) {
			SbiOrgUnitGrantNodes ouGrantNode = (SbiOrgUnitGrantNodes) iterOUGrantNodesVal.next();
			Map uniqueMap = new HashMap();		
			Map nodeAss = metaAss.getOuNodeAssociation();
			Map miAss = metaAss.getModelInstanceIDAssociation();
			Map grantAss = metaAss.getOuGrantAssociation();
			if(ouGrantNode.getId() != null){
				Integer newGrantId = (Integer)grantAss.get(ouGrantNode.getId().getGrantId());
				uniqueMap.put("grantId", newGrantId );
				Integer newNodeId = (Integer)nodeAss.get(ouGrantNode.getId().getNodeId());
				uniqueMap.put("nodeId", newNodeId);
				Integer newMiId = (Integer)miAss.get(ouGrantNode.getId().getKpiModelInstNodeId());
				uniqueMap.put("modelInstId", newMiId);
			}
			Object existObj = importer.checkExistence(uniqueMap, sessionCurrDB, new SbiOrgUnitGrantNodes());
			if (existObj != null) {
				SbiOrgUnitGrantNodes dsCurr = (SbiOrgUnitGrantNodes) existObj;
				metaAss.insertCoupleIdOuGrantNodesAssociation(ouGrantNode.getId(), dsCurr.getId());
				metaLog.log("Found an existing ou grant node with grant id " + dsCurr.getId().getGrantId() + " with "
						+ "the same id of one exported ou grant node");
			}
		}
		logger.debug("OUT");
	}

	/**
	 * Gets the object which contains the association between exported metadata
	 * and the current system metadata.
	 * 
	 * @return MetadataAssociation the object which contains the association
	 * between exported metadata and the current system metadata
	 */
	public MetadataAssociations getMetadataAssociation() {
		return metaAss;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#getExistingObject(java.lang.Integer, java.lang.Class)
	 */
	public Object getExistingObject(Integer id, Class objClass) {
		return importer.getObject(id, objClass, txCurrDB, sessionCurrDB);
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#getExportedObject(java.lang.Integer, java.lang.Class)
	 */
	public Object getExportedObject(Integer id, Class objClass) {
		return importer.getObject(id, objClass, txExpDB, sessionExpDB);
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#getUserAssociation()
	 */
	public UserAssociationsKeeper getUserAssociation() {
		return usrAss;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#getImpAssMode()
	 */
	public String getImpAssMode() {
		return impAssMode;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#setImpAssMode(java.lang.String)
	 */
	public void setImpAssMode(String impAssMode) {
		this.impAssMode = impAssMode;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#getAssociationFile()
	 */
	public AssociationFile getAssociationFile() {
		return associationFile;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#setAssociationFile(it.eng.spagobi.tools.importexport.bo.AssociationFile)
	 */
	public void setAssociationFile(AssociationFile associationFile) {
		this.associationFile = associationFile;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#associateAllExportedRolesByUserAssociation()
	 */
	public boolean associateAllExportedRolesByUserAssociation() throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedRoles = this.getExportedRoles();
			IRoleDAO roleDAO = DAOFactory.getRoleDAO();
			List currentRoles = roleDAO.loadAllRoles();
			Iterator exportedRolesIt = exportedRoles.iterator();
			while (exportedRolesIt.hasNext()) {
				Role exportedRole = (Role) exportedRolesIt.next();
				String associatedRoleName = this.getUserAssociation().getAssociatedRole(exportedRole.getName());
				if (associatedRoleName == null || associatedRoleName.trim().equals("")) return true;
				Iterator currentRolesIt = currentRoles.iterator();
				boolean associatedRoleNameExists = false;
				while (currentRolesIt.hasNext()) {
					Role currentRole = (Role) currentRolesIt.next();
					if (currentRole.getName().equals(associatedRoleName)) {
						associatedRoleNameExists = true;
						metaAss.insertCoupleRole(exportedRole.getId(), currentRole.getId());
						break;
					}
				}
				if (!associatedRoleNameExists) return true;
			}
			return false;
		} finally {
			logger.debug("OUT");
		}
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#associateAllExportedEnginesByUserAssociation()
	 */
	public boolean associateAllExportedEnginesByUserAssociation() throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedEngines = this.getExportedEngines();
			IEngineDAO engineDAO = DAOFactory.getEngineDAO();
			List currentEngines = engineDAO.loadAllEngines();
			Iterator exportedEnginesIt = exportedEngines.iterator();
			while (exportedEnginesIt.hasNext()) {
				Engine exportedEngine = (Engine) exportedEnginesIt.next();
				String associatedEngineLabel = this.getUserAssociation().getAssociatedEngine(exportedEngine.getLabel());
				if (associatedEngineLabel == null || associatedEngineLabel.trim().equals("")) return true;
				Iterator currentEngineIt = currentEngines.iterator();
				boolean associatedEngineLabelExists = false;
				while (currentEngineIt.hasNext()) {
					Engine currentEngine = (Engine) currentEngineIt.next();
					if (currentEngine.getLabel().equals(associatedEngineLabel)) {
						associatedEngineLabelExists = true;
						metaAss.insertCoupleEngine(exportedEngine.getId(), currentEngine.getId());
						break;
					}
				}
				if (!associatedEngineLabelExists) return true;
			}
			return false;
		} finally {
			logger.debug("OUT");
		}
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.importexport.IImportManager#associateAllExportedDataSourcesByUserAssociation()
	 */
	public boolean associateAllExportedDataSourcesByUserAssociation() throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedDataSources = this.getExportedDataSources();
			if (exportedDataSources == null || exportedDataSources.size() == 0) return false;
			IDataSourceDAO datasourceDAO = DAOFactory.getDataSourceDAO();
			List currentDataSources = datasourceDAO.loadAllDataSources();
			Iterator exportedDataSourcesIt = exportedDataSources.iterator();
			while (exportedDataSourcesIt.hasNext()) {
				IDataSource exportedDataSource = (IDataSource) exportedDataSourcesIt.next();
				String associatedDataSourceLabel = this.getUserAssociation().getAssociatedDataSource(exportedDataSource.getLabel());
				if (associatedDataSourceLabel == null || associatedDataSourceLabel.trim().equals("")) return true;
				Iterator currentDataSourcesIt = currentDataSources.iterator();
				boolean associatedDataSourceLabelExists = false;
				while (currentDataSourcesIt.hasNext()) {
					IDataSource currentDataSource = (IDataSource) currentDataSourcesIt.next();
					if (currentDataSource.getLabel().equals(associatedDataSourceLabel)) {
						associatedDataSourceLabelExists = true;
						metaAss.insertCoupleDataSources(new Integer(exportedDataSource.getDsId()), new Integer(currentDataSource.getDsId()));
						break;
					}
				}
				if (!associatedDataSourceLabelExists) return true;
			}
			return false;
		} finally {
			logger.debug("OUT");
		}
	}

	private void importMapCatalogue(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		try {
			// import maps
			List exportedMaps = importer.getAllExportedSbiObjects(sessionExpDB, "SbiGeoMaps", null);
			Iterator iterMaps = exportedMaps.iterator();
			while (iterMaps.hasNext()) {
				SbiGeoMaps expMap = (SbiGeoMaps) iterMaps.next();
				String name = expMap.getName();
				Object existObj = importer.checkExistence(name, sessionCurrDB, new SbiGeoMaps());
				SbiGeoMaps newMap = null;
				if (existObj != null) {
					if (!overwrite) {
						metaLog.log("Found an existing map '" + name + "' with "
								+ "the same name of the exported map. It will be not overwritten.");
						continue;
					} else {
						metaLog.log("Found an existing map '" + name + "' with "
								+ "the same name of the exported map. It will be overwritten.");
						newMap = (SbiGeoMaps) existObj;
					}
				} else {
					newMap = new SbiGeoMaps();
				}
				newMap.setName(expMap.getName());
				newMap.setDescr(expMap.getDescr());
				newMap.setFormat(expMap.getFormat());
				newMap.setUrl(expMap.getUrl());

				if (expMap.getBinContents() != null) {
					SbiBinContents binary = insertBinaryContent(expMap.getBinContents());
					newMap.setBinContents(binary);
				} else {
					metaLog.log("WARN: exported map with name '" + expMap.getName() + "' has no content!!");
					newMap.setBinContents(null);
				}

				sessionCurrDB.save(newMap);
				metaAss.insertCoupleMaps(new Integer(expMap.getMapId()), new Integer(newMap.getMapId()));
			}

			// import features
			List exportedFeatures = importer.getAllExportedSbiObjects(sessionExpDB, "SbiGeoFeatures", null);
			Iterator iterFeatures = exportedFeatures.iterator();
			while (iterFeatures.hasNext()) {
				SbiGeoFeatures expFeature = (SbiGeoFeatures) iterFeatures.next();
				String name = expFeature.getName();
				Object existObj = importer.checkExistence(name, sessionCurrDB, new SbiGeoFeatures());
				SbiGeoFeatures newFeature = null;
				if (existObj != null) {
					if (!overwrite) {
						metaLog.log("Found an existing feature '" + name + "' with "
								+ "the same name of the exported feature. It will be not overwritten.");
						continue;
					} else {
						metaLog.log("Found an existing feature '" + name + "' with "
								+ "the same name of the exported feature. It will be overwritten.");
						newFeature = (SbiGeoFeatures) existObj;
					}
				} else {
					newFeature = new SbiGeoFeatures();
				}
				newFeature.setName(expFeature.getName());
				newFeature.setDescr(expFeature.getDescr());
				newFeature.setType(expFeature.getType());
				sessionCurrDB.save(newFeature);
				metaAss.insertCoupleFeatures(new Integer(expFeature.getFeatureId()), new Integer(newFeature.getFeatureId()));
			}

			// import association between maps and features
			List exportedMapFeatures = importer.getAllExportedSbiObjects(sessionExpDB, "SbiGeoMapFeatures", null);
			Iterator iterMapFeatures = exportedMapFeatures.iterator();
			while (iterMapFeatures.hasNext()) {
				SbiGeoMapFeatures expMapFeature = (SbiGeoMapFeatures) iterMapFeatures.next();
				Integer expMapId = new Integer(expMapFeature.getId().getMapId());
				Integer expFeatureId = new Integer(expMapFeature.getId().getFeatureId());
				Integer existingMapId = null;
				Integer existingFeatureId = null;
				// find associated map id
				Map mapsIDAssociations = metaAss.getMapIDAssociation();
				Set mapsIDAssociationsKeySet = mapsIDAssociations.keySet();
				if (!mapsIDAssociationsKeySet.contains(expMapId)) {
					metaLog.log("Association between exported map with id = " + expMapId + " and exported feature with id = " + expFeatureId + 
					" will not be imported: the map was not imported.");
					continue;
				} else {
					existingMapId = (Integer) mapsIDAssociations.get(expMapId);
				}
				// find associated feature id
				Map featuresIDAssociations = metaAss.getFeaturesIDAssociation();
				Set featuresIDAssociationsKeySet = featuresIDAssociations.keySet();
				if (!featuresIDAssociationsKeySet.contains(expFeatureId)) {
					metaLog.log("Association between exported map with id = " + expMapId + " and exported feature with id = " + expFeatureId + 
					" will not be imported: the feature was not imported.");
					continue;
				} else {
					existingFeatureId = (Integer) featuresIDAssociations.get(expFeatureId);
				}

				Map unique = new HashMap();
				unique.put("mapId", existingMapId);
				unique.put("featureId", existingFeatureId);
				Object existObj = importer.checkExistence(unique, sessionCurrDB, new SbiGeoMapFeatures());
				SbiGeoMapFeatures newMapFeature = null;
				if (existObj != null) {
					if (!overwrite) {
						metaLog.log("Found an existing association between map " + existingMapId + " and feature " + existingFeatureId + ". " +
						"It will be not overwritten.");
						continue;
					} else {
						metaLog.log("Found an existing association between map " + existingMapId + " and feature " + existingFeatureId + ". " +
						"It will be overwritten.");
						newMapFeature = (SbiGeoMapFeatures) existObj;
					}
				} else {
					newMapFeature = new SbiGeoMapFeatures();
					SbiGeoMapFeaturesId hibMapFeatureId = new SbiGeoMapFeaturesId();			
					hibMapFeatureId.setMapId(existingMapId.intValue());
					hibMapFeatureId.setFeatureId(existingFeatureId.intValue());
					newMapFeature.setId(hibMapFeatureId);
				}
				newMapFeature.setSvgGroup(expMapFeature.getSvgGroup());
				newMapFeature.setVisibleFlag(expMapFeature.getVisibleFlag());
				sessionCurrDB.save(newMapFeature);
			}

			/*
			// copy all exported map files
			File mapsDir = new File(ConfigSingleton.getRootPath() + "/components/mapcatalogue/maps");
			if (!mapsDir.exists()) mapsDir.mkdirs();
			File exportedmapsDir = new File(pathBaseFolder + "/components/mapcatalogue/maps");
			if (exportedmapsDir.exists() && exportedmapsDir.isDirectory()) {
				File[] exportedMapsFiles = exportedmapsDir.listFiles();
				for (int i = 0; i < exportedMapsFiles.length; i++) {
					File exportedMapFile = exportedMapsFiles[i];
					if (exportedMapFile.isFile()) {
						FileOutputStream fos = null;
						InputStream is = null;
						try {
							File copy = new File(mapsDir.getAbsolutePath() + "/" + exportedMapFile.getName());
							if (copy.exists()) {
								if (!overwrite) continue;
								if (!copy.delete()) {
									logger.warn("Could not delete file [" + copy.getAbsolutePath() + "]. Map cannot be updated.");
									metaLog.log("Could not delete file [" + copy.getAbsolutePath() + "]. Map cannot be updated.");
									continue;
								}
							}
					        fos = new FileOutputStream(copy);
					        is = new FileInputStream(exportedMapFile);
					        int read = 0;
					        while ((read = is.read()) != -1) {
					        	fos.write(read);
					        }
					        fos.flush();
						} catch (Exception e) {
						    logger.error("Error while coping map catalogue files ", e);
						    throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
						} finally {
				        	try {
					        	if (fos != null) {
					        		fos.close();
					        	}
					        	if (is != null) {
					        		is.close();
					        	}
				        	} catch (Exception e) {
				        	    logger.error("Error while closing streams " , e);
				        	}
						}
					}
				}
			}
			 */

		} finally {
			logger.debug("OUT");
		}
	}



	/**
	 * Import exported model
	 * 
	 * @throws EMFUserError
	 */
	private void importModel(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiKpiModel exportedModel = null;
		try {
			List exportedModels = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModel", null);
			Iterator iterSbiModels = exportedModels.iterator();
			while (iterSbiModels.hasNext()) {
				exportedModel = (SbiKpiModel) iterSbiModels.next();
				Integer oldId = exportedModel.getKpiModelId();
				Integer existingModelId = null;
				Map modelIdAss = metaAss.getModelIDAssociation();
				Set modelIdAssSet = modelIdAss.keySet();
				if (modelIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported model " + exportedModel.getKpiModelCd() + " not inserted"
							+ " because it has the same code of an existing model");
					continue;
				} else {
					existingModelId = (Integer) modelIdAss.get(oldId);
				}
				if (existingModelId != null) {
					logger.info("The model with id:[" + exportedModel.getKpiModelId() + "] is just present. It will be updated.");
					metaLog.log("The model with code = [" + exportedModel.getKpiModelCd() + "] will be updated.");
					SbiKpiModel existingModel = ImportUtilities.modifyExistingSbiModel(exportedModel, sessionCurrDB, existingModelId, metaAss);
					sessionCurrDB.update(existingModel);
				} else {
					SbiKpiModel newModel = ImportUtilities.makeNewSbiModel(exportedModel, sessionCurrDB, metaAss);
					// TODO manca da associare il kpi con le entita
					//ImportUtilities.associateWithExistingEntities(newPar, exportedParameter, sessionCurrDB, importer, metaAss);
					sessionCurrDB.save(newModel);
					metaLog.log("Inserted new model " + newModel.getKpiModelCd());
					Integer newId = newModel.getKpiModelId();
					metaAss.insertCoupleModel(oldId, newId);
				}
				importUdpValues(oldId, "Model", overwrite);
			}
		} catch (Exception e) {
			if (exportedModel != null) {
				logger.error("Error while importing exported model with code [" + exportedModel.getKpiModelCd() + "].", e);
			}
			else{
				logger.error("Error while inserting model ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Import exported model instance
	 * 
	 * @throws EMFUserError
	 */
	private void importModelInstance(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiKpiModelInst exportedModelInst = null;
		try {
			List exportedModelsInsts = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelInst", null);
			Iterator iterSbiModelsInsts = exportedModelsInsts.iterator();
			while (iterSbiModelsInsts.hasNext()) {
				exportedModelInst = (SbiKpiModelInst) iterSbiModelsInsts.next();
				Integer oldId = exportedModelInst.getKpiModelInst();
				Integer existingModelInstId = null;
				Map modelInstIdAss = metaAss.getModelInstanceIDAssociation();
				Set modelInstIdAssSet = modelInstIdAss.keySet();
				if (modelInstIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported model instance" + exportedModelInst.getLabel() + " not inserted"
							+ " because it has the same label of an existing model instance");
					continue;
				} else {
					existingModelInstId = (Integer) modelInstIdAss.get(oldId);
				}
				if (existingModelInstId != null) {
					logger.info("The model instance with id:[" + exportedModelInst.getKpiModelInst() + "] is just present. It will be updated.");
					metaLog.log("The model instance with code = [" + exportedModelInst.getLabel() + "] will be updated.");
					SbiKpiModelInst existingModelInst = ImportUtilities.modifyExistingSbiModelInstance(exportedModelInst, sessionCurrDB, existingModelInstId, metaAss);
					sessionCurrDB.update(existingModelInst);
				} else {
					SbiKpiModelInst newModelInst = ImportUtilities.makeNewSbiModelInstance(exportedModelInst, sessionCurrDB, metaAss);
					sessionCurrDB.save(newModelInst);
					metaLog.log("Inserted new model instance " + newModelInst.getLabel());
					Integer newId = newModelInst.getKpiModelInst();
					metaAss.insertCoupleModelInstance(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedModelInst != null) {
				logger.error("Error while importing exported kpi with code [" + exportedModelInst.getLabel() + "].", e);
			}
			else{
				logger.error("Error while inserting model instance ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Import exported kpi
	 * 
	 * @throws EMFUserError
	 */
	private void importKpi(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiKpi exportedKpi = null;
		try {
			List exportedKpis = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpi", null);
			Iterator iterSbiKpis = exportedKpis.iterator();
			while (iterSbiKpis.hasNext()) {
				exportedKpi = (SbiKpi) iterSbiKpis.next();
				Integer oldId = exportedKpi.getKpiId();
				Integer existingKpiId = null;
				Map kpiIdAss = metaAss.getKpiIDAssociation();
				Set kpiIdAssSet = kpiIdAss.keySet();
				if (kpiIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported kpi " + exportedKpi.getName() + " not inserted"
							+ " because it has the same label of an existing kpi");
					continue;
				} else {
					existingKpiId = (Integer) kpiIdAss.get(oldId);
				}
				SbiKpi referenceKpi = null;
				if (existingKpiId != null) {
					logger.info("The kpi with id:[" + exportedKpi.getKpiId() + "] is just present. It will be updated.");
					metaLog.log("The kpi with code = [" + exportedKpi.getCode() + "] will be updated.");
					SbiKpi existingKpi = ImportUtilities.modifyExistingSbiKpi(exportedKpi, sessionCurrDB, existingKpiId, metaAss);
					existingKpi.setSbiKpiDocumentses(new HashSet(0));
					// TODO manca da associare il kpi alle nuove realtà
					//ImportUtilities.associateWithExistingEntities(existingParameter, exportedParameter, sessionCurrDB, importer, metaAss);
					sessionCurrDB.update(existingKpi);
					referenceKpi = existingKpi;
				} else {
					SbiKpi newKpi = ImportUtilities.makeNewSbiKpi(exportedKpi, sessionCurrDB, metaAss);
					newKpi.setSbiKpiDocumentses(new HashSet(0));
					// TODO manca da associare il kpi con le entita
					//ImportUtilities.associateWithExistingEntities(newPar, exportedParameter, sessionCurrDB, importer, metaAss);
					sessionCurrDB.save(newKpi);
					metaLog.log("Inserted new kpi " + newKpi.getName());
					Integer newId = newKpi.getKpiId();
					metaAss.insertCoupleKpi(oldId, newId);
					referenceKpi = newKpi;
				}
				
				Set kpiDocsList = exportedKpi.getSbiKpiDocumentses();
				Iterator i = kpiDocsList.iterator();
				while (i.hasNext()) {
					SbiKpiDocument doc = (SbiKpiDocument) i.next();
					if(doc!=null){
						String label = doc.getSbiObjects().getLabel();
						
						if(label!=null && referenceKpi!=null){		
							Criterion labelCriterrion = Expression.eq("label",label);
							Criteria criteria = sessionCurrDB.createCriteria(SbiObjects.class);
							criteria.add(labelCriterrion);
							SbiObjects hibObject = (SbiObjects) criteria.uniqueResult();
							SbiKpiDocument docToBeSaved = new SbiKpiDocument();
							docToBeSaved.setSbiKpi(referenceKpi);
							docToBeSaved.setSbiObjects(hibObject);
							sessionCurrDB.save(docToBeSaved);
						}
					}
				}
				importUdpValues(oldId, "Kpi", overwrite);

			}
			//loop again to get relations (after all kpi are imported)
			Iterator iterSbiKpisForRel = exportedKpis.iterator();
			while (iterSbiKpisForRel.hasNext()) {
				exportedKpi = (SbiKpi) iterSbiKpisForRel.next();
				importKpiRel(exportedKpi.getKpiId(), overwrite);
			}	
		} catch (Exception e) {
			if (exportedKpi != null) {
				logger.error("Error while importing exported kpi with code [" + exportedKpi.getCode() + "].", e);
			}
			else{
				logger.error("Error while inserting kpi ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}





	/**
	 * Import exported kpi inst
	 * 
	 * @throws EMFUserError
	 */
	private void importKpiInstance(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiKpiInstance exportedKpiInst = null;
		try {
			List exportedKpisInsts = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiInstance", null);
			Iterator iterSbiKpisInsts = exportedKpisInsts.iterator();
			while (iterSbiKpisInsts.hasNext()) {
				exportedKpiInst = (SbiKpiInstance) iterSbiKpisInsts.next();
				Integer oldId = exportedKpiInst.getIdKpiInstance();
				Integer existingKpiInstId = null;
				Map kpiInstIdAss = metaAss.getKpiInstanceIDAssociation();
				Set kpiInstIdAssSet = kpiInstIdAss.keySet();
				if (kpiInstIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported kpi instance with id" + exportedKpiInst.getIdKpiInstance() + " not inserted"
							+ " because it has the same relations of an existing kpi instance");
					continue;
				} else {
					existingKpiInstId = (Integer) kpiInstIdAss.get(oldId);
				}
				if (existingKpiInstId != null) {
					logger.info("The kpi instance with id:[" + exportedKpiInst.getIdKpiInstance() + "] is just present. It will be updated.");
					metaLog.log("The kpi instance with id = [" + exportedKpiInst.getIdKpiInstance() + "] will be updated.");
					SbiKpiInstance existingKpiInst = ImportUtilities.modifyExistingSbiKpiInstance(exportedKpiInst, sessionCurrDB, existingKpiInstId, metaAss);
					sessionCurrDB.update(existingKpiInst);
				} else {
					SbiKpiInstance newKpiInst = ImportUtilities.makeNewSbiKpiInstance(exportedKpiInst, sessionCurrDB, metaAss);
					sessionCurrDB.save(newKpiInst);
					metaLog.log("Inserted new kpi instance with id " + newKpiInst.getIdKpiInstance());
					Integer newId = newKpiInst.getIdKpiInstance();
					metaAss.insertCoupleKpiInstance(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedKpiInst != null) {
				logger.error("Error while importing exported kpi instance with id [" + exportedKpiInst.getIdKpiInstance() + "].", e);
			}
			else{
				logger.error("Error while inserting kpi instance ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}






	/**
	 * Import exported threshold Value
	 * 
	 * @throws EMFUserError
	 */
	private void importThresholdValue(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiThresholdValue exportedThValue = null;
		try {
			List exportedThValues = importer.getAllExportedSbiObjects(sessionExpDB, "SbiThresholdValue", null);
			Iterator iterSbiThValues = exportedThValues.iterator();
			while (iterSbiThValues.hasNext()) {
				exportedThValue = (SbiThresholdValue) iterSbiThValues.next();
				Integer oldId = exportedThValue.getIdThresholdValue();
				Integer existingThValueId = null;
				Map thValuesIdAss = metaAss.getTresholdValueIDAssociation();
				Set thValuesIdAssSet = thValuesIdAss.keySet();
				if (thValuesIdAssSet.contains(oldId) && !overwrite) {        // it could have been already inserted
					metaLog.log("Exported threshold values " + exportedThValue.getLabel() + " not inserted"
							+ " because it has the same label of an existing threshold value");
					continue;
				} else {
					existingThValueId = (Integer) thValuesIdAss.get(oldId);
				}
				if (existingThValueId != null) {
					logger.info("The threshold value with id:[" + exportedThValue.getIdThresholdValue() + "] is just present. It will be updated.");
					metaLog.log("The threshold value with code = [" + exportedThValue.getLabel() + "] will be updated.");
					SbiThresholdValue existingThValue = ImportUtilities.modifyExistingSbiThresholdValue(exportedThValue, sessionCurrDB, existingThValueId, metaAss,importer);
					sessionCurrDB.update(existingThValue);
				} else {
					SbiThresholdValue newThresholdValue = ImportUtilities.makeNewSbiThresholdValue(exportedThValue, sessionCurrDB, metaAss,importer);
					sessionCurrDB.save(newThresholdValue);
					metaLog.log("Inserted new Threshold Value " + newThresholdValue.getLabel());
					Integer newId = newThresholdValue.getIdThresholdValue();
					metaAss.insertCoupleThresholdValue(oldId, newId);
				}

			}
		} catch (Exception e) {
			if (exportedThValue != null) {
				logger.error("Error while importing exported threshold value with coe [" + exportedThValue.getLabel() + "].", e);
			}
			else{
				logger.error("Error while inserting threshold value ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}



	/**
	 * Import exported Threshold
	 * 
	 * @throws EMFUserError
	 */
	private void importThreshold(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiThreshold exportedTh = null;
		try {
			List exportedThresholds = importer.getAllExportedSbiObjects(sessionExpDB, "SbiThreshold", null);
			Iterator iterSbiTh= exportedThresholds.iterator();
			while (iterSbiTh.hasNext()) {
				exportedTh = (SbiThreshold) iterSbiTh.next();
				Integer oldId = exportedTh.getThresholdId();
				Integer existingThId = null;
				Map thIdAss = metaAss.getTresholdIDAssociation();
				Set thIdAssSet = thIdAss.keySet();
				if (thIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported threshold " + exportedTh.getCode() + " not inserted"
							+ " because it has the same label of an existing threshold ");
					continue;
				} else {
					existingThId = (Integer) thIdAss.get(oldId);
				}
				if (existingThId != null) {
					logger.info("The threshold with id:[" + exportedTh.getThresholdId() + "] is just present. It will be updated.");
					metaLog.log("The threshold with code = [" + exportedTh.getCode() + "] will be updated.");
					SbiThreshold existingTh = ImportUtilities.modifyExistingSbiThreshold(exportedTh, sessionCurrDB, existingThId, metaAss);
					sessionCurrDB.update(existingTh);
				} else {
					SbiThreshold newThreshold = ImportUtilities.makeNewSbiThreshold(exportedTh, sessionCurrDB, metaAss);
					sessionCurrDB.save(newThreshold);
					metaLog.log("Inserted new Threshold " + newThreshold.getCode());
					Integer newId = newThreshold.getThresholdId();
					metaAss.insertCoupleThreshold(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedTh != null) {
				logger.error("Error while importing exported threshold with coe [" + exportedTh.getCode() + "].", e);
			}
			else{
				logger.error("Error while inserting threshold ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}






	/**
	 * Import exported resources
	 * 
	 * @throws EMFUserError
	 */
	private void importResources(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiResources exportedResource = null;
		try {
			List exportedResources = importer.getAllExportedSbiObjects(sessionExpDB, "SbiResources", null);
			Iterator iterSbiResource = exportedResources.iterator();
			while (iterSbiResource.hasNext()) {
				exportedResource = (SbiResources) iterSbiResource.next();
				Integer oldId = exportedResource.getResourceId();
				Integer existingResourceId = null;
				Map resourcesIdAss = metaAss.getResourcesIDAssociation();
				Set resourcesIdAssSet = resourcesIdAss.keySet();
				if (resourcesIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported resources" + exportedResource.getResourceName() + " not inserted"
							+ " because it has the same name of an existing resource");
					continue;
				} else {
					existingResourceId = (Integer) resourcesIdAss.get(oldId);
				}
				if (existingResourceId != null) {
					logger.info("The resource with id:[" + exportedResource.getResourceId() + "] is just present. It will be updated.");
					metaLog.log("The resource with name = [" + exportedResource.getResourceName() + "] will be updated.");
					SbiResources existingResources = ImportUtilities.modifyExistingSbiResources(exportedResource, sessionCurrDB, existingResourceId, metaAss,importer);
					sessionCurrDB.update(existingResources);
				} else {
					SbiResources newResource = ImportUtilities.makeNewSbiResources(exportedResource, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newResource);
					metaLog.log("Inserted new resource " + newResource.getResourceName());
					Integer newId = newResource.getResourceId();
					metaAss.insertCoupleResources(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedResource != null) {
				logger.error("Error while importing exported resource with name [" + exportedResource.getResourceName() + "].", e);
			}
			else{
				logger.error("Error while inserting resource ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}





	/**
	 * Import exported resources
	 * 
	 * @throws EMFUserError
	 */
	private void importModelResources(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiKpiModelResources exportedModResource = null;
		try {
			List exportedModResources = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelResources", null);
			Iterator iterSbiModResource = exportedModResources.iterator();
			while (iterSbiModResource.hasNext()) {
				exportedModResource = (SbiKpiModelResources) iterSbiModResource.next();
				Integer oldId = exportedModResource.getKpiModelResourcesId();
				Integer existingModResourceId = null;
				Map modResourcesIdAss = metaAss.getModelResourcesIDAssociation();
				Set modResourcesIdAssSet = modResourcesIdAss.keySet();
				if (modResourcesIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported model resources with id " + exportedModResource.getKpiModelResourcesId() + " and" +
							" referencing resource with name "+exportedModResource.getSbiResources().getResourceName() + "" +
							" and model instance with label "+ exportedModResource.getSbiKpiModelInst().getLabel()+" not inserted  because it has the same name of an existing resource");
					continue;
				} else {
					existingModResourceId = (Integer) modResourcesIdAss.get(oldId);
				}
				if (existingModResourceId != null) {
					logger.info("The model resource with id:[" + exportedModResource.getKpiModelResourcesId() + "] is just present. It will be updated.");
					metaLog.log("The model resource referencing resource with name = [" + exportedModResource.getSbiResources().getResourceName() + "] nad model instance with label [ "+exportedModResource.getSbiKpiModelInst().getLabel() +" ]will be updated.");
					SbiKpiModelResources existingResources = ImportUtilities.modifyExistingSbiModelResources(exportedModResource, sessionCurrDB, existingModResourceId, metaAss,importer);
					sessionCurrDB.update(existingResources);
				} else {
					SbiKpiModelResources newModResource = ImportUtilities.makeNewSbiModelResource(exportedModResource, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newModResource);
					metaLog.log("Inserted new model resource between resource " + newModResource.getSbiResources().getResourceName()+" and model instance "+newModResource.getSbiKpiModelInst().getLabel());
					Integer newId = newModResource.getKpiModelResourcesId();
					metaAss.insertCoupleModelResources(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedModResource != null) {
				logger.error("Error while importing exported model resource with id [" + exportedModResource.getKpiModelResourcesId() + "].", e);
			}
			else{
				logger.error("Error while inserting model resources ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Import exported periodicity
	 * 
	 * @throws EMFUserError
	 */
	private void importPeriodicity(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiKpiPeriodicity exportedPeriodicity = null;
		try {
			List exportedPeriodicityList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiPeriodicity", null);
			Iterator iterSbiPeriodicity = exportedPeriodicityList.iterator();
			while (iterSbiPeriodicity.hasNext()) {
				exportedPeriodicity = (SbiKpiPeriodicity) iterSbiPeriodicity.next();
				Integer oldId = exportedPeriodicity.getIdKpiPeriodicity();
				Integer existingPeriodicityId = null;
				Map periodicityIdAss = metaAss.getPeriodicityIDAssociation();
				Set periodicityIdAssSet = periodicityIdAss.keySet();
				if (periodicityIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported periodicity" + exportedPeriodicity.getName() + " not inserted"
							+ " because it has the same name of an existing periodicity");
					continue;
				} else {
					existingPeriodicityId = (Integer) periodicityIdAss.get(oldId);
				}
				if (existingPeriodicityId != null) {
					logger.info("The periodicity with id:[" + exportedPeriodicity.getIdKpiPeriodicity() + "] is just present. It will be updated.");
					metaLog.log("The periodicity with name = [" + exportedPeriodicity.getName() + "] will be updated.");
					SbiKpiPeriodicity existingPeriodicity = ImportUtilities.modifyExistingSbiPeriodicity(exportedPeriodicity, sessionCurrDB, existingPeriodicityId, metaAss,importer);
					sessionCurrDB.update(existingPeriodicity);
				} else {
					SbiKpiPeriodicity newPeriodicity = ImportUtilities.makeNewSbiPeriodicity(exportedPeriodicity, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newPeriodicity);
					metaLog.log("Inserted new Periodicity " + newPeriodicity.getName());
					Integer newId = newPeriodicity.getIdKpiPeriodicity();
					metaAss.insertCouplePeriodicity(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedPeriodicity != null) {
				logger.error("Error while importing exported resource with name [" + exportedPeriodicity.getName() + "].", e);
			}
			else{
				logger.error("Error while inserting periodicity ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}





	/**
	 * Import exported kpiInstPeriod
	 * 
	 * @throws EMFUserError
	 */
	private void importKpiInstPeriod(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiKpiInstPeriod exportedKpiInstPeriod = null;
		try {
			List exportedKpiInstPeriodList = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiInstPeriod", null);
			Iterator iterSbiKpiInstPeriod = exportedKpiInstPeriodList.iterator();
			while (iterSbiKpiInstPeriod.hasNext()) {
				exportedKpiInstPeriod = (SbiKpiInstPeriod) iterSbiKpiInstPeriod.next();
				Integer oldId = exportedKpiInstPeriod.getKpiInstPeriodId();
				Integer existingKpiInstPeriodId = null;
				Map kpiInstPeriodIdAss = metaAss.getKpiInstPeriodIDAssociation();
				Set kpiInstPeriodIdAssSet = kpiInstPeriodIdAss.keySet();
				if (kpiInstPeriodIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported kpiInstPeriod with id " + exportedKpiInstPeriod.getKpiInstPeriodId() + " and" +
							" referencing Periodicity with name "+exportedKpiInstPeriod.getSbiKpiPeriodicity().getName() + "" +
							" and kpiInstance with previous id "+ exportedKpiInstPeriod.getSbiKpiInstance().getIdKpiInstance()+" not inserted  because it has the same name of an existing resource");
					continue;
				} else {
					existingKpiInstPeriodId = (Integer) kpiInstPeriodIdAss.get(oldId);
				}
				if (existingKpiInstPeriodId != null) {
					logger.info("The kpiInstPeriod with id:[" + exportedKpiInstPeriod.getKpiInstPeriodId() + "] is just present. It will be updated.");
					metaLog.log("The kpiInstPeriod referencing periodicity with name = [" + exportedKpiInstPeriod.getSbiKpiPeriodicity().getName() + "] and kpi instance with prev Id [ "+exportedKpiInstPeriod.getSbiKpiInstance().getIdKpiInstance() +" ]will be updated.");
					SbiKpiInstPeriod existingKpiInstPeriod = ImportUtilities.modifyExistingSbiKpiInstPeriod(exportedKpiInstPeriod, sessionCurrDB, existingKpiInstPeriodId, metaAss,importer);
					sessionCurrDB.update(existingKpiInstPeriod);
				} else {
					SbiKpiInstPeriod newKpiInstPeriod = ImportUtilities.makeNewSbiKpiInstPeriod(exportedKpiInstPeriod, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newKpiInstPeriod);
					metaLog.log("Inserted new kpiInstPeriod  referring to periodicity " + newKpiInstPeriod.getSbiKpiPeriodicity().getName()+" and kpi instance "+newKpiInstPeriod.getSbiKpiInstance().getIdKpiInstance());
					Integer newId = newKpiInstPeriod.getKpiInstPeriodId();
					metaAss.insertCoupleKpiInstPeriod(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedKpiInstPeriod != null) {
				logger.error("Error while importing exported kpi Inst Period  with id [" + exportedKpiInstPeriod.getKpiInstPeriodId() + "].", e);
			}
			else{
				logger.error("Error while inserting instance period ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}





	/**
	 * Import exported Alarms
	 * 
	 * @throws EMFUserError
	 */
	private void importAlarm(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiAlarm exportedAlarm = null;
		try {
			List exportedAlarms = importer.getAllExportedSbiObjects(sessionExpDB, "SbiAlarm", null);
			Iterator iterSbiAlarm = exportedAlarms.iterator();
			while (iterSbiAlarm.hasNext()) {
				exportedAlarm = (SbiAlarm) iterSbiAlarm.next();
				Integer oldId = exportedAlarm.getId();
				Integer existingAlarmId = null;
				Map alarmIdAss = metaAss.getAlarmIDAssociation();
				Set alarmIdAssSet = alarmIdAss.keySet();
				if (alarmIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported alarm " + exportedAlarm.getLabel() + " not inserted"
							+ " because it has the same label of an existing alarm");
					continue;
				} else {
					existingAlarmId = (Integer) alarmIdAss.get(oldId);
				}
				if (existingAlarmId != null) {
					logger.info("The Alarm with id:[" + exportedAlarm.getId() + "] is just present. It will be updated.");
					metaLog.log("The Alarm with label = [" + exportedAlarm.getLabel() + "] will be updated.");
					SbiAlarm existingAlarm = ImportUtilities.modifyExistingSbiAlarms(exportedAlarm, sessionCurrDB, existingAlarmId, metaAss,importer);
					sessionCurrDB.update(existingAlarm);
				} else {
					SbiAlarm newAlarm = ImportUtilities.makeNewSbiAlarms(exportedAlarm, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newAlarm);
					metaLog.log("Inserted new Alarm " + newAlarm.getLabel());
					Integer newId = newAlarm.getId();
					metaAss.insertCoupleAlarm(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedAlarm != null) {
				logger.error("Error while importing exported Alarm with label [" + exportedAlarm.getLabel() + "].", e);
			}
			else{
				logger.error("Error while inserting Alarm ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Import exported KpiModelAttr
	 * 
	 * @throws EMFUserError
	 */
	private void importKpiModelAttr(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		// TODO cambiare con i nuovi UDP VAlues
		/*
		SbiKpiModelAttr exportedKpiModelAttr = null;
		try {
			List exportedKpiModelAttrs = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelAttr", null);
			Iterator iterSbiKpiModelAttr = exportedKpiModelAttrs.iterator();
			while (iterSbiKpiModelAttr.hasNext()) {
				exportedKpiModelAttr = (SbiKpiModelAttr) iterSbiKpiModelAttr.next();
				Integer oldId = exportedKpiModelAttr.getKpiModelAttrId();
				Integer existingKpiModelAttrId = null;
				Map kpiModelAttrIdAss = metaAss.getSbiKpiModelAttrIDAssociation();
				Set kpiModelAttrIdAssSet = kpiModelAttrIdAss.keySet();
				if (kpiModelAttrIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported kpiModelAttr with code " + exportedKpiModelAttr.getKpiModelAttrCd() + " and " +
							" referring to domain "+ exportedKpiModelAttr.getSbiDomains().getDomainCd() +" / "+exportedKpiModelAttr.getSbiDomains().getValueCd() +" not inserted"
							+ " because it has the same label of an existing kpiModelATtr");
					continue;
				} else {
					existingKpiModelAttrId = (Integer) kpiModelAttrIdAss.get(oldId);
				}
				if (existingKpiModelAttrId != null) {
					logger.info("The KpiModelAttr with id:[" + exportedKpiModelAttr.getKpiModelAttrId() + "] is just present. It will be updated.");
					metaLog.log("The KpiModelAttr with label = [" + exportedKpiModelAttr.getKpiModelAttrCd() + " and referring to domain "+ exportedKpiModelAttr.getSbiDomains().getDomainCd() +" / "+exportedKpiModelAttr.getSbiDomains().getValueCd()+"] will be updated.");
					SbiKpiModelAttr existingSbiKpiModelAttr = ImportUtilities.modifyExistingSbiKpiModelAttr(exportedKpiModelAttr, sessionCurrDB, existingKpiModelAttrId, metaAss,importer);
					sessionCurrDB.update(existingSbiKpiModelAttr);
				} else {
					SbiKpiModelAttr newKpiModelAttr = ImportUtilities.makeNewSbiKpiModelAttr(exportedKpiModelAttr, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newKpiModelAttr);
					metaLog.log("Inserted new kpiModelAttr with label " + newKpiModelAttr.getKpiModelAttrCd()+ " and referring to domain " + newKpiModelAttr.getSbiDomains().getDomainCd() + " / " + newKpiModelAttr.getSbiDomains().getDomainCd());
					Integer newId = newKpiModelAttr.getKpiModelAttrId();
					metaAss.insertCoupleSbiKpiModelAttrID(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedKpiModelAttr!= null) {
				logger.error("Error while importing exported KpiModelAttr with label = [" + exportedKpiModelAttr.getKpiModelAttrCd() + " and referring to domain "+ exportedKpiModelAttr.getSbiDomains().getDomainCd() +" / "+exportedKpiModelAttr.getSbiDomains().getValueCd()+"] will be updated.", e);
			}
			else{
				logger.error("Error while inserting KpiModelAttr with label = [" + exportedKpiModelAttr.getKpiModelAttrCd() + " and referring to domain "+ exportedKpiModelAttr.getSbiDomains().getDomainCd() +" / "+exportedKpiModelAttr.getSbiDomains().getValueCd()+"] will be updated.", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}*/
	}


	/**
	 * Import exported KpiMdoelAttrVal
	 * 
	 * @throws EMFUserError
	 */
	private void importKpiModelAttrVal(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		// TODO cambiare con i nuovi UDP VAlues
		/*SbiKpiModelAttrVal exportedKpiModelAttrVal = null;
		try {
			List exportedKpiModelAttrVals = importer.getAllExportedSbiObjects(sessionExpDB, "SbiKpiModelAttrVal", null);
			Iterator iterSbiKpiModelAttrVal = exportedKpiModelAttrVals.iterator();
			while (iterSbiKpiModelAttrVal.hasNext()) {
				exportedKpiModelAttrVal = (SbiKpiModelAttrVal) iterSbiKpiModelAttrVal.next();
				Integer oldId = exportedKpiModelAttrVal.getKpiModelAttrValId();
				Integer existingKpiModelAttrValId = null;
				Map kpiModelAttrValIdAss = metaAss.getSbiKpiModelAttrValIDAssociation();
				Set kpiModelAttrValIdAssSet = kpiModelAttrValIdAss.keySet();
				if (kpiModelAttrValIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported kpiModelAttrVal referring to model " + exportedKpiModelAttrVal.getSbiKpiModel().getKpiModelNm() + " and " +
							" referring to attribute  "+ exportedKpiModelAttrVal.getSbiKpiModelAttr().getKpiModelAttrNm() +" not inserted"
							+ " because it has the same label of an existing KpiModelAttrVal");
					continue;
				} else {
					existingKpiModelAttrValId = (Integer) kpiModelAttrValIdAss.get(oldId);
				}
				if (existingKpiModelAttrValId != null) {
					logger.info("The KpiModelAttrVal with referring to model " + exportedKpiModelAttrVal.getSbiKpiModel().getKpiModelNm() + " and " +
							" referring to attribute  "+ exportedKpiModelAttrVal.getSbiKpiModelAttr().getKpiModelAttrNm() +"is just present. It will be updated.");
					metaLog.log("The KpiModelAttrVal referring to model " + exportedKpiModelAttrVal.getSbiKpiModel().getKpiModelNm() + " and " +
							" referring to attribute  "+ exportedKpiModelAttrVal.getSbiKpiModelAttr().getKpiModelAttrNm() +" will be updated");
					SbiKpiModelAttrVal existingSbiKpiModelAttrVal = ImportUtilities.modifyExistingSbiKpiModelAttrVal(exportedKpiModelAttrVal, sessionCurrDB, existingKpiModelAttrValId, metaAss,importer);
					sessionCurrDB.update(existingSbiKpiModelAttrVal);
				} else {
					SbiKpiModelAttrVal newKpiModelAttrVal = ImportUtilities.makeNewSbiKpiModelAttrVal(exportedKpiModelAttrVal, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newKpiModelAttrVal);
					metaLog.log("Inserted new kpiModelAttrVal referring to model " + newKpiModelAttrVal.getSbiKpiModel().getKpiModelNm() + " and " +
							" referring to attribute  "+ newKpiModelAttrVal.getSbiKpiModelAttr().getKpiModelAttrNm());
					Integer newId = newKpiModelAttrVal.getKpiModelAttrValId();
					metaAss.insertCoupleSbiKpiModelAttrValID(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedKpiModelAttrVal!= null) {
				logger.error("Error while importing exported KpiModelAttrVal referring to model " + exportedKpiModelAttrVal.getSbiKpiModel().getKpiModelNm() + " and " +
						" referring to attribute  "+ exportedKpiModelAttrVal.getSbiKpiModelAttr().getKpiModelAttrNm() +"  will be updated.", e);
			}
			else{
				logger.error("Error while inserting KpiModelAttrVal referring to model " + exportedKpiModelAttrVal.getSbiKpiModel().getKpiModelNm() + " and " +
						" referring to attribute  "+ exportedKpiModelAttrVal.getSbiKpiModelAttr().getKpiModelAttrNm() +"  will be updated.", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}*/
	}


	/**
	 * Import exported Alarms Contacts
	 * 
	 * @throws EMFUserError
	 */
	private void importAlarmContact(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiAlarmContact exportedAlarmContact = null;
		try {
			List exportedAlarmContacts = importer.getAllExportedSbiObjects(sessionExpDB, "SbiAlarmContact", null);
			Iterator iterSbiAlarmContact = exportedAlarmContacts.iterator();
			while (iterSbiAlarmContact.hasNext()) {
				exportedAlarmContact = (SbiAlarmContact) iterSbiAlarmContact.next();
				Integer oldId = exportedAlarmContact.getId();
				Integer existingAlarmContactId = null;
				Map alarmContactIdAss = metaAss.getAlarmContactIDAssociation();
				Set alarmContactIdAssSet = alarmContactIdAss.keySet();
				if (alarmContactIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported alarm Contact " + exportedAlarmContact.getName() + " not inserted"
							+ " because it has the same label of an existing alarm Contact");
					continue;
				} else {
					existingAlarmContactId = (Integer) alarmContactIdAss.get(oldId);
				}
				if (existingAlarmContactId != null) {
					logger.info("The Alarm Contact with id:[" + exportedAlarmContact.getId() + "] is just present. It will be updated.");
					metaLog.log("The Alarm Contact with name = [" + exportedAlarmContact.getName() + "] will be updated.");
					SbiAlarmContact existingAlarmContact = ImportUtilities.modifyExistingSbiAlarmContacts(exportedAlarmContact, sessionCurrDB, existingAlarmContactId, metaAss,importer);
					sessionCurrDB.update(existingAlarmContact);
				} else {
					SbiAlarmContact newAlarmContact = ImportUtilities.makeNewSbiAlarmContacts(exportedAlarmContact, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newAlarmContact);
					metaLog.log("Inserted new Alarm Contact " + newAlarmContact.getName());
					Integer newId = newAlarmContact.getId();
					metaAss.insertCoupleAlarmContact(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedAlarmContact != null) {
				logger.error("Error while importing exported Alarm Contact with label [" + exportedAlarmContact.getName() + "].", e);
			}
			else{
				logger.error("Error while inserting alarm contact ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Import exported ObjMetacontent
	 * 
	 * @throws EMFUserError
	 */
	private void importObjMetacontent(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiObjMetacontents exportedMetacontent = null;
		try {
			List exportedMetacontents = importer.getAllExportedSbiObjects(sessionExpDB, "SbiObjMetacontents", null);
			Iterator iterMetacontents = exportedMetacontents.iterator();
			while (iterMetacontents.hasNext()) {
				exportedMetacontent = (SbiObjMetacontents) iterMetacontents.next();
				Integer oldId = exportedMetacontent.getObjMetacontentId();
				Integer existingMetacontentsId = null;
				Map metaContentIdAss = metaAss.getObjMetacontentsIDAssociation();
				Set metaContentIdAssSet = metaContentIdAss.keySet();
				if (metaContentIdAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported metaContent with original id" + exportedMetacontent.getObjMetacontentId() + " not inserted"
							+ " because there isalready one asociation to the same object "+exportedMetacontent.getSbiObjects().getLabel()+" " +
							" and to the same metadata with id "+exportedMetacontent.getObjmetaId());
					continue;
				}

				// if the content is associated to a subobject wich has not been inserted ignore it
				Map subObjectIdAssociation = metaAss.getObjSubObjectIDAssociation();
				if(exportedMetacontent.getSbiSubObjects()!=null && 
						!subObjectIdAssociation.keySet().contains(exportedMetacontent.getSbiSubObjects().getSubObjId())){
					metaLog.log("Exported metaContent with original id" + exportedMetacontent.getObjMetacontentId() + " not inserted" +
					" becuase referring to a subobjects that will not be updated bcause already present" );	
					continue;
				}
				else {
					existingMetacontentsId = (Integer) metaContentIdAss.get(oldId);
				}
				if (existingMetacontentsId != null) {
					logger.info("The Metacontent with id:[" + exportedMetacontent.getObjMetacontentId() + "] is already present. It will be updated.");
					metaLog.log("The Metacontent with original id = " + exportedMetacontent.getObjMetacontentId() + "and associated to the object with label" + exportedMetacontent.getSbiObjects().getLabel() +"  will be updated.");
					SbiObjMetacontents existingObjMetacontents = ImportUtilities.modifyExistingSbiObjMetacontents(exportedMetacontent, sessionCurrDB, existingMetacontentsId, metaAss,importer);
					sessionCurrDB.update(existingObjMetacontents);
				} else {
					SbiObjMetacontents newMetacontents = ImportUtilities.makeNewSbiObjMetacontent(exportedMetacontent, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newMetacontents);
					String subObject = newMetacontents.getSbiSubObjects() != null ? newMetacontents.getSbiSubObjects().getName() : null;
					if( subObject != null){
						metaLog.log("Inserted new Metacontents associated to subobject "+subObject +" of object  " + 
								newMetacontents.getSbiObjects().getLabel());
					}
					else {
						metaLog.log("Inserted new Metacontents associated to object  " + 
								newMetacontents.getSbiObjects().getLabel());
					}
					Integer newId = newMetacontents.getObjMetacontentId();
					metaAss.insertCoupleObjMetacontentsIDAssociation(oldId, newId);
				}
			}
		} catch (Exception e) {
			if (exportedMetacontent != null) {
				logger.error("Error while importing exported Metacontent with original id [" + exportedMetacontent.getObjMetacontentId() + "]. and associated to object "+exportedMetacontent.getSbiObjects().getLabel(), e);
			}
			else{
				logger.error("Error while inserting SbiObjMetacontents ", e);
			}
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Import exported Udp
	 * 
	 * @throws EMFUserError
	 */
	private void importUdp(Integer udpId, boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		SbiUdp udp = null;
		try {

			List exportedUdps = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiUdp", "udpId", udpId);
			Iterator iterSbiUdp = exportedUdps.iterator();
			while (iterSbiUdp.hasNext()) {
				udp = (SbiUdp) iterSbiUdp.next();
				Integer oldId = udp.getUdpId();

				Integer existingUdpId = null;
				//other methd
				Map udpAss = metaAss.getUdpAssociation();
				Set udpAssSet = udpAss.keySet();
				if (udpAssSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported association between type id " + udp.getTypeId() + " "
							+ " and family id " + udp.getFamilyId() + " with label "
							+ udp.getLabel() + " not inserted"
							+ " because already existing into the current database");
					continue;
				} else {
					existingUdpId = (Integer) udpAss.get(oldId);
				}
				if (existingUdpId != null) {
					logger.info("The udp with label:[" + udp.getLabel() + "] is just present. It will be updated.");
					metaLog.log("The udp with label = [" + udp.getLabel() + "] will be updated.");
					SbiUdp existingUdp = ImportUtilities.modifyExistingUdp(udp, sessionCurrDB, existingUdpId);
					ImportUtilities.entitiesAssociationsSbiUdp(existingUdp, udp, sessionCurrDB,  metaAss, importer);
					sessionCurrDB.update(existingUdp);
				} else {
					SbiUdp newUdp = ImportUtilities.makeNewSbiUdp(udp, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newUdp);
					metaLog.log("Inserted new udp with label " + newUdp.getLabel()+" type id " + udp.getTypeId() + " "
								+ " and family id " + udp.getFamilyId());
					Integer newId = newUdp.getUdpId();
					sessionExpDB.evict(udp);
					metaAss.insertCoupleUdpAssociation(oldId, newId);
				}

			}
		} catch (Exception e) {
			if (udp != null) {
				logger.error("Error while importing exported udp with name [" + udp.getName() + "].", e);
			}
			logger.error("Error while inserting object ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}
	
	/**Import Udp values
	 * @param referenceId
	 * @param overwrite
	 * @param family
	 * @throws EMFUserError
	 */
	private void importUdpValues(Integer referenceId, String family, boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = sessionExpDB.createQuery(" from SbiUdpValue uv where uv.referenceId = ? and uv.family = ?");
			hibQuery.setInteger(0, referenceId);
			hibQuery.setString(1, family);
			
			List exportedUdpValue = (List<SbiUdpValue>) hibQuery.list();
			Iterator iterSbiUdpValue = exportedUdpValue.iterator();

			while (iterSbiUdpValue.hasNext()) {
				SbiUdpValue udpvalue = (SbiUdpValue) iterSbiUdpValue.next();
				Integer oldId = udpvalue.getUdpValueId();
				//import udp first
				importUdp(udpvalue.getSbiUdp().getUdpId(), overwrite);
				
				//then udp values

				Map assUdpValue = metaAss.getUdpValueAssociation();
				Integer existingUdpValueId = null;
				Set assUdpValueSet = assUdpValue.keySet();
				if (assUdpValueSet.contains(oldId) && !overwrite) {
					metaLog.log("Exported association udp value with udp with label " + udpvalue.getSbiUdp().getLabel() + " "
							+ " and family " + udpvalue.getFamily() + " with reference id "
							+  udpvalue.getReferenceId() + " not inserted"
							+ " because already existing into the current database");
					continue;
				} else {
					existingUdpValueId = (Integer) assUdpValue.get(oldId);
				}
				if (existingUdpValueId != null) {

					metaLog.log("The udp value with udp with label " + udpvalue.getSbiUdp().getLabel() + " "
							+ " and family " + udpvalue.getFamily() + " with reference id "
							+  udpvalue.getReferenceId()+ "] will be updated.");
					SbiUdpValue existingUdpValue = ImportUtilities.modifyExistingSbiUdpValue(udpvalue, sessionCurrDB, existingUdpValueId);
					ImportUtilities.entitiesAssociationsSbiUdpValue(udpvalue, existingUdpValue, sessionCurrDB, metaAss, importer);
					sessionCurrDB.update(existingUdpValue);
				} else {
					SbiUdpValue newUdpValue = ImportUtilities.makeNewSbiUdpValue(udpvalue, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newUdpValue);
					metaLog.log("The udp value with udp with label " + udpvalue.getSbiUdp().getLabel() + " "
							+ " and family " + udpvalue.getFamily() + " with reference id "
							+  udpvalue.getReferenceId()+" is inserted");
					Integer newId = newUdpValue.getUdpValueId();
					sessionExpDB.evict(udpvalue);
					metaAss.insertCoupleUdpValueAssociation(udpvalue.getUdpValueId(), newId);
				}

			}
		} catch (HibernateException he) {
			logger.error("Error while inserting udp value ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} catch (Exception e) {
			logger.error("Error while inserting udp value ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}	
	
	
	/**Import kpi relations
	 * @param kpiParentId
	 * @param overwrite
	 * @throws EMFUserError
	 */
	private void importKpiRel(Integer kpiParentId, boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedKpiRel = importer.getFilteredExportedSbiObjects(sessionExpDB, "SbiKpiRel", "sbiKpiByKpiFatherId.kpiId", kpiParentId);
			
			Iterator iterSbiKpiRel = exportedKpiRel.iterator();

			while (iterSbiKpiRel.hasNext()) {
				SbiKpiRel kpirel = (SbiKpiRel) iterSbiKpiRel.next();
				SbiKpi child = kpirel.getSbiKpiByKpiChildId();
				SbiKpi father = kpirel.getSbiKpiByKpiFatherId();
				if(child != null && father != null){
					
					Map assKpiRel = metaAss.getKpiRelAssociation();
					Integer existingKpiRelId = null;
					Set assKpiRelSet = assKpiRel.keySet();
					if (assKpiRelSet.contains(kpirel.getKpiRelId()) && !overwrite) {
						metaLog.log("Exported association between object " + kpirel.getSbiKpiByKpiFatherId().getName() + " "
								+ " and kpi " + kpirel.getSbiKpiByKpiChildId().getName() + " with parameter "
								+ kpirel.getParameter() + " not inserted"
								+ " because already existing into the current database");
						continue;
					} else {
						existingKpiRelId = (Integer) assKpiRel.get(kpirel.getKpiRelId());
					}
					if (existingKpiRelId != null) {
						metaLog.log("The relation between object " + kpirel.getSbiKpiByKpiFatherId().getName() + " "
								+ " and kpi " + kpirel.getSbiKpiByKpiChildId().getName() + " with parameter "
								+ kpirel.getParameter()+ "] will be updated.");
						SbiKpiRel existingKpiRel = ImportUtilities.modifyExistingKpiRel(kpirel, sessionCurrDB, existingKpiRelId);
						ImportUtilities.entitiesAssociationsSbiKpiRel(kpirel, existingKpiRel, sessionCurrDB, metaAss, importer);
						sessionCurrDB.update(existingKpiRel);
					} else {
						SbiKpiRel newRel = ImportUtilities.makeNewSbiKpiRel(kpirel, sessionCurrDB, metaAss, importer);
						sessionCurrDB.save(newRel);
						metaLog.log("Inserted new relation between object " + kpirel.getSbiKpiByKpiFatherId().getName() + " "
								+ " and kpi " + kpirel.getSbiKpiByKpiChildId().getName() + " with parameter "
								+ kpirel.getParameter());
						Integer newId = newRel.getKpiRelId();
						sessionExpDB.evict(kpirel);
						metaAss.insertCoupleKpiRelAssociation(kpirel.getKpiRelId(), newId);
					}

				}
			}
		} catch (HibernateException he) {
			logger.error("Error while inserting kpi relation ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} catch (Exception e) {
			logger.error("Error while inserting kpi relation ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}
	private void importOuGrants(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedGrants = (List<SbiOrgUnitGrant>)importer.getAllExportedSbiObjects(sessionExpDB, "SbiOrgUnitGrant", null);
			
			Iterator iterGrants = exportedGrants.iterator();

			while (iterGrants.hasNext()) {
				SbiOrgUnitGrant grant = (SbiOrgUnitGrant) iterGrants.next();
					
				Map assGrants = metaAss.getOuGrantAssociation();
				Integer existingGrantId = null;
				Set assGrantsSet = assGrants.keySet();
				if (assGrantsSet.contains(grant.getId()) && !overwrite) {
					metaLog.log("Exported association of grant with name " + grant.getName() + "  not inserted"
							+ " because already existing into the current database");
					continue;
				} else {
					existingGrantId = (Integer) assGrants.get(grant.getId());
				}
				if (existingGrantId != null) {
					metaLog.log("Exported association of grant with name " + grant.getName() + "] will be updated.");
					SbiOrgUnitGrant existingGrant = ImportUtilities.modifyExistingOuGrant(grant, sessionCurrDB, existingGrantId);
					ImportUtilities.entitiesAssociationsOuGrant(grant, existingGrant, sessionCurrDB, metaAss, importer);
					sessionCurrDB.update(existingGrant);
				} else {
					SbiOrgUnitGrant newGrant = ImportUtilities.makeNewOuGrant(grant, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newGrant);
					metaLog.log("Inserted new grant with name " + grant.getName() );
					Integer newId = newGrant.getId();
					sessionExpDB.evict(grant);
					metaAss.insertCoupleIdOuGrantAssociation(grant.getId(), newId);
				}

			}
		} catch (HibernateException he) {
			logger.error("Error while inserting grant ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} catch (Exception e) {
			logger.error("Error while inserting grant ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}
	private void importOuGrantNodes(boolean overwrite) throws EMFUserError {
		logger.debug("IN");
		try {
			List exportedGrantNodes = (List<SbiOrgUnitGrantNodes>)importer.getAllExportedSbiObjects(sessionExpDB, "SbiOrgUnitGrantNodes", null);
			
			Iterator iterGrantNodes = exportedGrantNodes.iterator();

			while (iterGrantNodes.hasNext()) {
				SbiOrgUnitGrantNodes grantNode = (SbiOrgUnitGrantNodes) iterGrantNodes.next();
					
				Map assGrantNodes = metaAss.getOuGrantNodesAssociation();
				SbiOrgUnitGrantNodesId existingGrantNodeId = null;
				Set assGrantNodesSet = assGrantNodes.keySet();
				if (assGrantNodesSet.contains(grantNode.getId()) && !overwrite) {
					metaLog.log("Exported association of grant node with grant id " + grantNode.getId().getGrantId()+ "  not inserted"
							+ " because already existing into the current database");
					continue;
				} else {
					existingGrantNodeId = (SbiOrgUnitGrantNodesId) assGrantNodes.get(grantNode.getId());
				}
				if (existingGrantNodeId != null) {
					metaLog.log("Exported association of grant node with grant id " + grantNode.getId().getGrantId() + "] will be updated.");
					SbiOrgUnitGrantNodes existingGrantNode = ImportUtilities.modifyExistingOuGrantNode(grantNode, sessionCurrDB, existingGrantNodeId);
					ImportUtilities.entitiesAssociationsOuGrantNode(existingGrantNode.getId(), grantNode, existingGrantNode, sessionCurrDB, metaAss, importer);
					sessionCurrDB.update(existingGrantNode);
				} else {
					SbiOrgUnitGrantNodes newGrantNode = ImportUtilities.makeNewOuGrantNode(grantNode, sessionCurrDB, metaAss, importer);
					sessionCurrDB.save(newGrantNode);
					metaLog.log("Inserted new grant node with grant id " + grantNode.getId().getGrantId() );
					SbiOrgUnitGrantNodesId newId = newGrantNode.getId();
					sessionExpDB.evict(grantNode);
					metaAss.insertCoupleIdOuGrantNodesAssociation(grantNode.getId(), newId);
				}

			}
		} catch (HibernateException he) {
			logger.error("Error while inserting grant node ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} catch (Exception e) {
			logger.error("Error while inserting grant node ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}
}
