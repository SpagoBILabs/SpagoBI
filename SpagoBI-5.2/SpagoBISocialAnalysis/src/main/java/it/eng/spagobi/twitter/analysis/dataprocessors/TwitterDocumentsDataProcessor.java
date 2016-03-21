/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.pojos.TwitterDocumentPojo;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	private final String PARAMETERS = "&PARAMETERS=";
	private final String startDateParam = "startDate=";
	private final String lastActivationTimeParam = "&lastActivation=";
	// private final String SPAGOBI_SERVICE_URL = System.getProperty("spagobi_service_url");

	// fields to avoid logic into JSP
	private List<String> labels = new ArrayList<String>();
	private List<TwitterDocumentPojo> documents = new ArrayList<TwitterDocumentPojo>();

	private final int SEARCHSTARTPOS = 0;
	private final int TMSLASTACTIVATIONPOS = 1;
	private final int TMSDOCUMENTSPOS = 2;

	public TwitterDocumentsDataProcessor() {

	}

	/**
	 * This method initializes the structures for a search docs
	 *
	 * @param searchIDStr
	 * @return
	 */
	public void initializeTwitterDocumentsDataProcessor(String searchID) {

		logger.debug("Method initializeTwitterDocumentsDataProcessor(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		long searchId = AnalysisUtility.isLong(searchID);

		EnginConf conf = EnginConf.getInstance();

		logger.debug("SpagoBI Service URL -> " + conf.getSpagoBiServerUrl());

		try {

			Object[] twitterMonitorScheduler = dpCache.getDocuments(searchId);

			if (twitterMonitorScheduler != null && twitterMonitorScheduler.length > 0) {

				String documents = (String) twitterMonitorScheduler[TMSDOCUMENTSPOS];
				Calendar startDate = (Calendar) twitterMonitorScheduler[SEARCHSTARTPOS];
				Calendar lastActivation = (Calendar) twitterMonitorScheduler[TMSLASTACTIVATIONPOS];

				lastActivation = roundSQLTimestamp(lastActivation);

				if (documents != null && !documents.equals("")) {
					String[] documentsArr = documents.split(",");

					for (int i = 0; i < documentsArr.length; i++) {

						String label = documentsArr[i].trim();

						String encodedLabel = URLEncoder.encode(label, "UTF-8");

						String url = this.composeUrl(conf.getSpagoBiServerUrl(), encodedLabel, startDate, lastActivation);

						TwitterDocumentPojo documentPojo = new TwitterDocumentPojo(label, url);

						this.documents.add(documentPojo);
						this.labels.add(label);

					}
				}
			}

			long endMills = System.currentTimeMillis() - initMills;

			logger.debug("Method initializeTwitterDocumentsDataProcessor(): End in " + endMills + "s");

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
	private String composeUrl(String spagobiServiceUrl, String docLabel, Calendar startDate, Calendar lastActivation) {

		return spagobiServiceUrl + CALL + docLabel + "&PARAMETERS=startDate%3D" + formatCalendar(startDate) + "%26lastActivation%3D"
				+ formatCalendar(lastActivation);
		// PARAMETERS + startDateParam + this.formatCalendar(startDate) + lastActivationTimeParam
		// + this.formatCalendar(lastActivation);

	}

	/**
	 * This method rounds timestamp mills, secs and mins
	 *
	 * @param dbTimestamp
	 * @return
	 */
	private Calendar roundSQLTimestamp(Calendar calendarTime) {

		calendarTime.set(Calendar.MILLISECOND, 0);
		calendarTime.set(Calendar.SECOND, 0);
		calendarTime.set(Calendar.MINUTE, 0);

		return calendarTime;
	}

	private String formatCalendar(Calendar calendarToFormat) {

		SimpleDateFormat simpleDataFormatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm");

		Date tempDate = new Date(calendarToFormat.getTimeInMillis());

		return simpleDataFormatter.format(tempDate);
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
