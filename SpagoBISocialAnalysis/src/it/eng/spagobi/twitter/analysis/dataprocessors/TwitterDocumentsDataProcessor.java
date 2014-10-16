/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.entities.TwitterMonitorScheduler;
import it.eng.spagobi.twitter.analysis.pojos.TwitterDocumentPojo;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterDocumentsDataProcessor {

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	private static final Logger logger = Logger.getLogger(TwitterDocumentsDataProcessor.class);

	// TODO: conf table
	// private final String HOST = "http://localhost:";
	// private final String PORT = "8080";
	private final String CALL = "/servlet/AdapterHTTP?ACTION_NAME=EXECUTE_DOCUMENT_ACTION&NEW_SESSION=TRUE&OBJECT_LABEL=";
	// private final String SPAGOBI_SERVICE_URL = System.getProperty("spagobi_service_url");

	// fields to avoid logic into JSP
	private List<String> labels = new ArrayList<String>();
	private List<TwitterDocumentPojo> documents = new ArrayList<TwitterDocumentPojo>();

	public TwitterDocumentsDataProcessor() {

	}

	/**
	 * This method initializes the structures for a search docs
	 *
	 * @param searchIDStr
	 * @return
	 */
	public void initializeTwitterDocumentsDataProcessor(String searchID) {

		logger.debug("Method initializeTwitterDocumentsDataProcessor(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		EnginConf conf = EnginConf.getInstance();

		logger.debug("SpagoBI Service URL -> " + conf.getSpagoBiServerUrl());

		try {

			TwitterMonitorScheduler twitterMonitorScheduler = dpCache.getDocuments(searchId);

			if (twitterMonitorScheduler != null) {
				String documents = twitterMonitorScheduler.getDocuments();

				String[] documentsArr = documents.split(",");

				for (int i = 0; i < documentsArr.length; i++) {

					String label = documentsArr[i].trim();

					String encodedLabel = URLEncoder.encode(label, "UTF-8");

					String url = this.composeUrl(conf.getSpagoBiServerUrl(), encodedLabel);

					TwitterDocumentPojo documentPojo = new TwitterDocumentPojo(label, url);

					this.documents.add(documentPojo);
					this.labels.add(label);

				}
			}

			logger.debug("Method initializeTwitterDocumentsDataProcessor(): End");

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method initializeTwitterDocumentsDataProcessor(): An error occurred for search ID: " + searchID, t);
		}
	}

	/**
	 * This method composes url constants and adds the doc label
	 *
	 * @param docLabel
	 * @return
	 */
	private String composeUrl(String spagobiServiceUrl, String docLabel) {

		return spagobiServiceUrl + CALL + docLabel;

	}

	/**
	 * This method rounds timestamp mills, secs and mins
	 *
	 * @param dbTimestamp
	 * @return
	 */
	private Date roundSQLTimestamp(java.sql.Timestamp dbTimestamp) {

		Calendar calendarTime = GregorianCalendar.getInstance();
		calendarTime.setTime(dbTimestamp);

		calendarTime.set(Calendar.MILLISECOND, 0);
		calendarTime.set(Calendar.SECOND, 0);
		calendarTime.set(Calendar.MINUTE, 0);

		Date roundDate = new Date(calendarTime.getTimeInMillis());

		return roundDate;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<TwitterDocumentPojo> getDocuments() {
		return documents;
	}

	public void setDocuments(List<TwitterDocumentPojo> documents) {
		this.documents = documents;
	}

}
