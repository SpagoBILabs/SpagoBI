/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.api;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.services.rest.annotations.UserConstraint;
import it.eng.spagobi.twitter.analysis.entities.TwitterMonitorScheduler;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearchScheduler;
import it.eng.spagobi.twitter.analysis.enums.BooleanOperatorEnum;
import it.eng.spagobi.twitter.analysis.enums.MonitorRepeatTypeEnum;
import it.eng.spagobi.twitter.analysis.enums.SearchRepeatTypeEnum;
import it.eng.spagobi.twitter.analysis.enums.SearchTypeEnum;
import it.eng.spagobi.twitter.analysis.enums.UpToTypeEnum;
import it.eng.spagobi.twitter.analysis.launcher.TwitterAnalysisLauncher;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.NamingException;
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

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Path("/historicalSearch")
public class TwitterHistoricalSearchAPI {

	static final Logger logger = Logger.getLogger(TwitterHistoricalSearchAPI.class);

	// Save a new Twitter Search
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String search(@Context HttpServletRequest req) {

		logger.debug("Method search(): Start");

		try {

			TwitterAnalysisLauncher twitterLauncher = null;
			TwitterSearchScheduler twitterSearchScheduler = null;
			TwitterMonitorScheduler twitterMonitorScheduler = null;
			TwitterSearch twitterSearch = new TwitterSearch();

			String languageCode = null;

			// reading the user input
			// String searchType = req.getParameter("searchType");
			String keywords = req.getParameter("keywords");
			String links = req.getParameter("links");
			String accounts = req.getParameter("accounts");
			String documents = req.getParameter("documents");

			String booleanOperator = req.getParameter("booleanOperator");

			// set ready parameters
			// twitterSearch.setLanguageCode(languageCode);

			if (booleanOperator != null && !booleanOperator.equals("")) {
				if (booleanOperator.equalsIgnoreCase("AND")) {
					twitterSearch.setBooleanOperator(BooleanOperatorEnum.AND);
				} else if (booleanOperator.equalsIgnoreCase("OR")) {
					twitterSearch.setBooleanOperator(BooleanOperatorEnum.OR);
				} else if (booleanOperator.equalsIgnoreCase("FREE")) {
					twitterSearch.setBooleanOperator(BooleanOperatorEnum.FREE);
				}
			}

			twitterSearch.setType(SearchTypeEnum.SEARCHAPI);
			twitterSearch.setLoading(true);
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
			String label = req.getParameter("label");

			if (label == null || label.trim().equals("")) {

				logger.debug("Method search(): Blank label. Creation from keywords");

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

			String repeatTypeField = req.getParameter("repeatType");
			String numberRepeat = req.getParameter("numberRepeat");

			if (req.getParameter("isRepeating") != null && numberRepeat != null && !numberRepeat.equals("") && repeatTypeField != null
					&& !repeatTypeField.equals("")) {

				logger.debug("Method search(): Search with scheduler");

				twitterSearchScheduler = new TwitterSearchScheduler();

				Calendar startingDate = GregorianCalendar.getInstance();

				startingDate.set(Calendar.SECOND, 0);
				startingDate.set(Calendar.MILLISECOND, 0);

				int repeatFrequency = Integer.parseInt(numberRepeat);

				if (repeatFrequency > 0) {

					if (repeatTypeField.equalsIgnoreCase(SearchRepeatTypeEnum.Day.toString())) {
						startingDate.add(Calendar.DAY_OF_MONTH, repeatFrequency);

						twitterSearchScheduler.setRepeatType(SearchRepeatTypeEnum.Day);

					} else if (repeatTypeField.equalsIgnoreCase(SearchRepeatTypeEnum.Hour.toString())) {
						startingDate.add(Calendar.HOUR_OF_DAY, repeatFrequency);

						twitterSearchScheduler.setRepeatType(SearchRepeatTypeEnum.Hour);
					}

					twitterSearchScheduler.setActive(true);
					twitterSearchScheduler.setStartingTime(startingDate);
					twitterSearchScheduler.setRepeatFrequency(repeatFrequency);
					twitterSearchScheduler.setTwitterSearch(twitterSearch);

				}

			}

			// set search scheduler
			twitterSearch.setTwitterSearchScheduler(twitterSearchScheduler);

			// now we take the decision abount the monitor scheduler. Check if there
			// resources to monitor..
			if ((links != null && !links.equals("")) || (accounts != null && !accounts.equals("")) || (documents != null && !documents.equals(""))) {
				//
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

					if (twitterSearch.getTwitterSearchScheduler() != null) {
						twitterMonitorScheduler.setActiveSearch(true);
						twitterMonitorScheduler.setActive(true);
					} else {
						twitterMonitorScheduler.setActiveSearch(false);

						if (Integer.parseInt(numberUpTo) <= 0) {
							twitterMonitorScheduler.setActive(false);
						} else {
							twitterMonitorScheduler.setActive(true);
						}
					}

					twitterMonitorScheduler.setAccounts(accounts);
					twitterMonitorScheduler.setLinks(links);
					twitterMonitorScheduler.setDocuments(documents);
					twitterMonitorScheduler.setLastActivationTime(GregorianCalendar.getInstance());
					twitterMonitorScheduler.setStartingTime(GregorianCalendar.getInstance());
					twitterMonitorScheduler.setEndingTime(this.setMonitorSchedulerEndingDate(twitterMonitorScheduler));

					twitterMonitorScheduler.setTwitterSearch(twitterSearch);

				}

			}
			// set monitor scheduler
			twitterSearch.setTwitterMonitorScheduler(twitterMonitorScheduler);

			String numberStartingFrom = req.getParameter("numberStartingFrom");

			if (req.getParameter("isStartingFrom") != null && numberStartingFrom != null && !numberStartingFrom.equals("")) {

				logger.debug("Method search(): Search with a starting date");

				Calendar actualDate = GregorianCalendar.getInstance();

				// Manage starting time with the user's input
				Calendar sinceDate = actualDate;

				sinceDate.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(numberStartingFrom));

				twitterSearch.setDaysAgo(Integer.parseInt(numberStartingFrom));

				twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
				twitterLauncher.setLanguageCode(languageCode);
				twitterLauncher.setSinceCalendar(sinceDate);

			} else {

				logger.debug("Method search(): Search without dates (except API limits)");

				twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
				twitterLauncher.setLanguageCode(languageCode);
			}

			long searchID = twitterLauncher.createhistoricalSearch();

			JSONObject resObj = new JSONObject();

			if (searchID > 0) {

				resObj.put("success", true);
				resObj.put("msg", "Twitter Search \"" + label + "\" inserted. Loading results..");

			} else {

				resObj.put("failure", true);
				resObj.put("msg", "Failure starting new search ");

			}

			logger.debug("Method search(): End");

			return resObj.toString();

		} catch (Throwable th) {

			throw new SpagoBIRuntimeException("Method search(): An error occurred in Twitter Historical Search API for REST service search ", th);
		}
	}

	// Get the list of all Twitter Search
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getListSearch(@Context HttpServletRequest req) throws NamingException {

		logger.debug("Method getListSearch(): Start");

		try {

			TwitterAnalysisLauncher launcher = new TwitterAnalysisLauncher();

			List<TwitterSearch> searchList = launcher.getTwitterSearchList(SearchTypeEnum.SEARCHAPI);

			JSONArray jsonArray = new JSONArray();

			for (TwitterSearch search : searchList) {

				jsonArray.put(search.toJSONObject());

			}

			// logger.debug("GET Result: " + jsonArray.toString());
			logger.debug("Method getListSearch(): End");
			return jsonArray.toString();
		}

		catch (Throwable th) {

			throw new SpagoBIRuntimeException("Method getListSearch(): An error occurred in Twitter Historical Search API for REST service getListSearch ", th);
		}

	}

	// Delete a new Twitter Search
	@Path("/deleteSearch")
	@POST
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String delete(@Context HttpServletRequest req) throws Exception {

		logger.debug("Method delete(): Start..");

		try {
			TwitterSearch twitterSearch = new TwitterSearch();

			// reading the user input
			String searchID = req.getParameter("searchID");

			// set ready parameters
			twitterSearch.setSearchID(Long.parseLong(searchID));

			TwitterAnalysisLauncher twitterAnalysisLauncher = new TwitterAnalysisLauncher(twitterSearch);
			String label = twitterAnalysisLauncher.deleteSearch();

			String result = "Historical search '" + label + "' deleted";
			return result;

		} catch (Throwable th) {

			throw new SpagoBIRuntimeException("Method delete(): An error occurred in Twitter Historical Search API for REST service delete ", th);
		}
	}

	// Stop the search scheduler and start monitor scheduler
	@Path("/stopSearchScheduler")
	@POST
	@UserConstraint(functionalities = { SpagoBIConstants.CREATE_SOCIAL_ANALYSIS })
	public String stopSearchScheduler(@Context HttpServletRequest req) throws Exception {

		logger.debug("Method stopSearchScheduler(): Start..");

		try {
			TwitterSearch twitterSearch = new TwitterSearch();

			// reading the user input
			String searchID = req.getParameter("searchID");

			// set ready parameters
			twitterSearch.setSearchID(Long.parseLong(searchID));

			TwitterAnalysisLauncher twitterAnalysisLauncher = new TwitterAnalysisLauncher(twitterSearch);
			twitterAnalysisLauncher.stopSearchScheduler();

			// JSONObject resObj = new JSONObject();

			// try {
			// resObj.put("success", true);
			// resObj.put("msg", "Historical search scheduler \"" + searchID + "\" stopped");
			//
			// } catch (JSONException e) {
			// logger.error("Method stopSearchScheduler(): Error trying to stop search scheduler " + searchID + " - " + e.getMessage());
			// }

			logger.debug("Method stopSearchScheduler(): End");

			return "Historical search scheduler " + searchID + " stopped";

		} catch (Throwable th) {

			throw new SpagoBIRuntimeException(
					"Method stopSearchScheduler(): An error occurred in Twitter Historical Search API for REST service stopSearchScheduler ", th);
		}
	}

	// Update a failed search from historic search table
	@Path("/updateFailedSearch")
	@POST
	// @Produces(MediaType.APPLICATION_JSON)
	public String updateFailedSearch(@Context HttpServletRequest req) throws Exception {

		logger.debug("Method updateFailedSearch(): Start");

		try {
			TwitterSearch twitterSearch = new TwitterSearch();

			// reading the user input
			String searchID = req.getParameter("searchID");

			// set ready parameters
			twitterSearch.setSearchID(Long.parseLong(searchID));

			TwitterAnalysisLauncher twitterAnalysisLauncher = new TwitterAnalysisLauncher(twitterSearch);
			String label = twitterAnalysisLauncher.updateFailedSearch();

			logger.debug("Method updateFailedSearch(): End");

			return "Historical failed search '" + label + "' updated. Partial results loaded";
		} catch (Throwable th) {

			throw new SpagoBIRuntimeException(
					"Method updateFailedSearch(): An error occurred in Twitter Historical Search API for REST service updateFailedSearch ", th);
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

	private Calendar setMonitorSchedulerEndingDate(TwitterMonitorScheduler twitterMonitorScheduler) {

		Calendar endingCalendar = GregorianCalendar.getInstance();
		int upToValue = twitterMonitorScheduler.getUpToValue();
		UpToTypeEnum upToType = twitterMonitorScheduler.getUpToType();

		// Calculating the ending date
		if (upToType == UpToTypeEnum.Day) {
			endingCalendar.add(Calendar.DAY_OF_MONTH, upToValue);
		} else if (upToType == UpToTypeEnum.Week) {
			endingCalendar.add(Calendar.DAY_OF_MONTH, (upToValue) * 7);
		} else if (upToType == UpToTypeEnum.Month) {
			endingCalendar.add(Calendar.DAY_OF_MONTH, (upToValue) * 30);
		}

		return endingCalendar;
	}
}
