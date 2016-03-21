/**

SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 *
 */

package it.eng.spagobi.twitter.analysis.api;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.services.rest.annotations.UserConstraint;
import it.eng.spagobi.twitter.analysis.entities.TwitterMonitorScheduler;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.enums.BooleanOperatorEnum;
import it.eng.spagobi.twitter.analysis.enums.MonitorRepeatTypeEnum;
import it.eng.spagobi.twitter.analysis.enums.SearchTypeEnum;
import it.eng.spagobi.twitter.analysis.enums.UpToTypeEnum;
import it.eng.spagobi.twitter.analysis.launcher.TwitterAnalysisLauncher;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import twitter4j.JSONArray;
import twitter4j.JSONObject;

@Path("/streamingSearch")
public class TwitterStreamingSearchAPI {

	static final Logger logger = Logger.getLogger(TwitterStreamingSearchAPI.class);

	// Save a new Twitter Search
	@Path("/createEnabledStream")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String createEnabledStream(@Context HttpServletRequest req) {

		logger.debug("Method createEnabledStream(): Start");

		try {

			TwitterSearch twitterSearch = this.createStreamingSearch(req);

			if (twitterSearch.getTwitterMonitorScheduler() != null) {
				twitterSearch.getTwitterMonitorScheduler().setActiveSearch(true);
				twitterSearch.getTwitterMonitorScheduler().setActive(true);
			}

			String languageCode = null;

			// initializing the launcher with this search
			TwitterAnalysisLauncher twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
			twitterLauncher.setLanguageCode(languageCode);

			long searchID = twitterLauncher.createEnabledStreamingSearch();

			JSONObject resObj = new JSONObject();

			if (searchID > 0) {

				resObj.put("success", true);
				resObj.put("msg", "Streaming search \"" + twitterSearch.getLabel() + "\" inserted (enabled)");

			} else {

				resObj.put("failure", true);
				resObj.put("msg", "Failure inserting new search ");

			}

			logger.debug("Method createEnabledStream(): End");

			return resObj.toString();
		}

		catch (Throwable th) {

			throw new SpagoBIRuntimeException(
					"Method createEnabledStream(): An error occurred in Twitter Streaming Search API for REST service createEnabledStream ", th);
		}
	}

	// Save a new Twitter Search
	@Path("/createDisabledStream")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String createDisabledStream(@Context HttpServletRequest req) {

		logger.debug("Method createDisabledStream(): Start");

		try {

			TwitterSearch twitterSearch = this.createStreamingSearch(req);
			String languageCode = null;

			twitterSearch.setLoading(false);

			if (twitterSearch.getTwitterMonitorScheduler() != null) {
				twitterSearch.getTwitterMonitorScheduler().setActiveSearch(false);
				if (twitterSearch.getTwitterMonitorScheduler().getUpToValue() <= 0) {
					twitterSearch.getTwitterMonitorScheduler().setActive(false);
				} else {
					twitterSearch.getTwitterMonitorScheduler().setActive(true);
				}
			}

			// initializing the launcher with this search
			TwitterAnalysisLauncher twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
			twitterLauncher.setLanguageCode(languageCode);

			long searchID = twitterLauncher.createDisabledStreamingSearch();

			JSONObject resObj = new JSONObject();

			if (searchID > 0) {

				resObj.put("success", true);
				resObj.put("msg", "Streaming search \"" + twitterSearch.getLabel() + "\" inserted (enabled)");

			} else {

				resObj.put("failure", true);
				resObj.put("msg", "Failure inserting new search ");

			}

			logger.debug("Method createDisabledStream(): End");

			return resObj.toString();

		} catch (Throwable th) {

			throw new SpagoBIRuntimeException(
					"Method createDisabledStream(): An error occurred in Twitter Streaming Search API for REST service createDisabledStream ", th);
		}
	}

	@POST
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String start(@Context HttpServletRequest req) throws Exception {

		logger.debug("Method start(): Start");

		try {
			String languageCode = null;

			// reading the user input
			String sID = req.getParameter("searchID");

			if (sID != null && !sID.equals("")) {
				long searchID = Long.parseLong(sID);

				String keywords = req.getParameter("keywords");

				TwitterSearch twitterSearch = new TwitterSearch();

				twitterSearch.setKeywords(keywords);
				twitterSearch.setSearchID(searchID);

				TwitterAnalysisLauncher twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
				twitterLauncher.setLanguageCode(languageCode);

				twitterLauncher.startStreamingSearch();

			}

			// JSONObject resObj = new JSONObject();
			//
			// try {
			//
			// resObj.put("success", true);
			// resObj.put("msg", "Streaming search activated");
			//
			// } catch (JSONException e) {
			// logger.error("Method start(): ERROR - " + e);
			// }
			//
			logger.debug("Method start(): End");

			return "Streaming search activated";
		} catch (Throwable th) {

			throw new SpagoBIRuntimeException("Method start(): An error occurred in Twitter Streaming Search API for REST service start", th);
		}
	}

	// Get the list of all Twitter Search
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getListSearch(@Context HttpServletRequest req) {

		try {

			TwitterAnalysisLauncher launcher = new TwitterAnalysisLauncher();

			List<TwitterSearch> searchList = launcher.getTwitterSearchList(SearchTypeEnum.STREAMINGAPI);

			JSONArray jsonArray = new JSONArray();

			for (TwitterSearch search : searchList) {

				jsonArray.put(search.toJSONObject());

			}

			logger.debug("Method getListSearch(): End");
			return jsonArray.toString();

		} catch (Throwable th) {

			throw new SpagoBIRuntimeException("Method getListSearch(): An error occurred in Twitter Streaming Search API for REST service getListSearch", th);
		}

	}

	// Delete a Twitter Search
	@Path("/deleteSearch")
	@POST
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String delete(@Context HttpServletRequest req) throws Exception {

		logger.debug("Method delete(): Start..");

		try {

			// reading the user input
			String searchID = req.getParameter("searchID");
			boolean loading = Boolean.parseBoolean(req.getParameter("loading"));

			TwitterSearch twitterSearch = new TwitterSearch();

			// set ready parameters
			twitterSearch.setSearchID(Long.parseLong(searchID));
			twitterSearch.setType(SearchTypeEnum.STREAMINGAPI);
			twitterSearch.setLoading(loading);

			TwitterAnalysisLauncher twitterAnalysisLauncher = new TwitterAnalysisLauncher(twitterSearch);
			String label = twitterAnalysisLauncher.deleteSearch();

			// JSONObject resObj = new JSONObject();

			// try {
			// resObj.put("success", true);
			// resObj.put("msg", "Streaming search \"" + searchID + "\" deleted");
			//
			// } catch (JSONException e) {
			// logger.error("Method delete(): ERROR - " + e);
			// }

			logger.debug("Method delete(): End");

			return "Streaming search '" + label + "' deleted";

		}

		catch (Throwable th) {

			throw new SpagoBIRuntimeException("Method delete(): An error occurred in Twitter Streaming Search API for REST service delete", th);
		}
	}

	// Stop a Twitter Search
	@Path("/stopStreamingSearch")
	@POST
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String stopStream(@Context HttpServletRequest req) throws Exception {

		logger.debug("Method stopStream(): Start");

		try {

			// reading the user input
			String searchID = req.getParameter("searchID");

			// set ready parameters

			TwitterSearch twitterSearch = new TwitterSearch();

			twitterSearch.setSearchID(Long.parseLong(searchID));
			twitterSearch.setType(SearchTypeEnum.STREAMINGAPI);

			TwitterAnalysisLauncher twitterAnalysisLauncher = new TwitterAnalysisLauncher(twitterSearch);
			twitterAnalysisLauncher.stopStreamingSearch();

			// JSONObject resObj = new JSONObject();

			// try {
			// resObj.put("success", true);
			// resObj.put("msg", "Streaming search stopped");
			//
			// } catch (JSONException e) {
			// logger.error("Method delete(): ERROR - " + e);
			// }

			logger.debug("Method stopStream(): End");

			return "Streaming search stopped";

		} catch (Throwable th) {

			throw new SpagoBIRuntimeException("Method stopStream(): An error occurred in Twitter Streaming Search API for REST service stopStream", th);
		}
	}

	private boolean monitorFrequencyValidation(String value, String type) {
		if (value != null && !value.equals("") && type != null && !value.equals(""))
			return true;
		else
			return false;
	}

	private boolean monitorUpToValidation(String value, String type) {
		if (value != null && !value.equals("") && type != null && !value.equals(""))
			return true;
		else
			return false;
	}

	private TwitterSearch createStreamingSearch(HttpServletRequest req) {
		TwitterSearch twitterSearch = new TwitterSearch();
		TwitterMonitorScheduler twitterMonitorScheduler = null;

		// reading the user input

		// String searchType = req.getParameter("searchType");
		String label = req.getParameter("label");
		String keywords = req.getParameter("keywords");
		String links = req.getParameter("links");
		String accounts = req.getParameter("accounts");
		String documents = req.getParameter("documents");

		String booleanOperator = req.getParameter("booleanOperator");

		// set ready parameters

		if (booleanOperator != null && !booleanOperator.equals("")) {
			if (booleanOperator.equalsIgnoreCase("AND")) {
				twitterSearch.setBooleanOperator(BooleanOperatorEnum.AND);
			} else if (booleanOperator.equalsIgnoreCase("OR")) {
				twitterSearch.setBooleanOperator(BooleanOperatorEnum.OR);
			} else if (booleanOperator.equalsIgnoreCase("FREE")) {
				twitterSearch.setBooleanOperator(BooleanOperatorEnum.FREE);
			}
		}

		twitterSearch.setType(SearchTypeEnum.STREAMINGAPI);
		twitterSearch.setKeywords(keywords);

		twitterSearch.setCreationDate(GregorianCalendar.getInstance());
		twitterSearch.setLastActivationTime(GregorianCalendar.getInstance());

		// check if advanced option "R Analysis" is checked
		if (req.getParameter("ranalysis") != null) {
			twitterSearch.setrAnalysis(true);
		} else {
			twitterSearch.setrAnalysis(false);
		}

		// if user is not specifying the label, create it with the keywords
		if (label == null || label.trim().equals("")) {

			logger.debug("Method save(): Blank label. Creation from keywords");

			String[] keywordsArr = keywords.split(",");
			for (int i = 0; i < keywordsArr.length; i++) {

				String tempKeyword = keywordsArr[i].trim();

				if (i == keywordsArr.length - 1) {
					label = label + tempKeyword;
				} else {
					label = label + tempKeyword + "_";
				}
			}

			label = label + "_" + System.currentTimeMillis();
		}

		// set search label
		twitterSearch.setLabel(label);

		// parameters for monitoring scheduler

		// now we take the decision about the monitor scheduler. Check if there
		// resources to monitor..
		if ((links != null && !links.equals("")) || (accounts != null && !accounts.equals("")) || (documents != null && !documents.equals(""))) {

			String numberUpTo = req.getParameter("numberUpTo");
			String typeUpTo = req.getParameter("typeUpTo");

			String monitorFrequencyValue = req.getParameter("monitorFrequencyValue");
			String monitorFrequencyType = req.getParameter("monitorFrequencyType");

			if (monitorFrequencyValidation(monitorFrequencyValue, monitorFrequencyType) && monitorUpToValidation(numberUpTo, typeUpTo)) {

				twitterMonitorScheduler = new TwitterMonitorScheduler();

				twitterMonitorScheduler.setRepeatFrequency(Integer.parseInt(monitorFrequencyValue));

				if (monitorFrequencyType.equalsIgnoreCase(MonitorRepeatTypeEnum.Day.toString())) {
					twitterMonitorScheduler.setRepeatType(MonitorRepeatTypeEnum.Day);
				} else if (monitorFrequencyType.equalsIgnoreCase(MonitorRepeatTypeEnum.Hour.toString())) {
					twitterMonitorScheduler.setRepeatType(MonitorRepeatTypeEnum.Hour);
				}

				twitterMonitorScheduler.setUpToValue(Integer.parseInt(numberUpTo));

				if (typeUpTo.equalsIgnoreCase(UpToTypeEnum.Day.toString())) {
					twitterMonitorScheduler.setUpToType(UpToTypeEnum.Day);
				} else if (typeUpTo.equalsIgnoreCase(UpToTypeEnum.Week.toString())) {
					twitterMonitorScheduler.setUpToType(UpToTypeEnum.Week);
				} else if (typeUpTo.equalsIgnoreCase(UpToTypeEnum.Month.toString())) {
					twitterMonitorScheduler.setUpToType(UpToTypeEnum.Month);
				}

				twitterMonitorScheduler.setAccounts(accounts);
				twitterMonitorScheduler.setLinks(links);
				twitterMonitorScheduler.setDocuments(documents);
				twitterMonitorScheduler.setEndingTime(AnalysisUtility.setMonitorSchedulerEndingDate(twitterMonitorScheduler));
				twitterMonitorScheduler.setLastActivationTime(GregorianCalendar.getInstance());
				twitterMonitorScheduler.setStartingTime(GregorianCalendar.getInstance());

				twitterMonitorScheduler.setTwitterSearch(twitterSearch);

			}

		}

		// set monitor scheduler
		twitterSearch.setTwitterMonitorScheduler(twitterMonitorScheduler);

		return twitterSearch;
	}

}
