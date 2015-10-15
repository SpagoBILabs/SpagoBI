package it.eng.spagobi.twitter.analysis.scheduler;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearchScheduler;
import it.eng.spagobi.twitter.analysis.enums.BooleanOperatorEnum;
import it.eng.spagobi.twitter.analysis.spider.search.TwitterSearchAPISpider;
import it.eng.spagobi.twitter.analysis.utilities.TwitterRScriptUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;

import twitter4j.Query.ResultType;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class HistoricalSearchJob implements Job {

	static final Logger logger = Logger.getLogger(HistoricalSearchJob.class);
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private long searchID;
	private String languageCode;
	private Calendar sinceCalendar;

	public HistoricalSearchJob() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			JobKey key = context.getJobDetail().getKey();

			TwitterSearch twitterSearch = twitterCache.findTwitterSearch(searchID);

			logger.debug("Instance " + key + " of HistoricalSearchJob. Executing search #: " + searchID);

			logger.debug("Method startHistoricalSearchThread(): Initializing Historical Search");

			TwitterSearchAPISpider searchAPI = initializeSearchAPI(twitterSearch);

			searchAPI.collectTweets();

			// historical search completed, loading = false;

			if (twitterSearch.isrAnalysis()) {

				logger.debug("Method startHistoricalSearchThread(): Historical Search completed. Processing results for topics and sentiment..");

				TwitterRScriptUtility.callTopicsRScript(twitterSearch.getSearchID());
				TwitterRScriptUtility.callSentimentRScript(twitterSearch.getSearchID());

				logger.debug("Method startHistoricalSearchThread(): R Scripts called. Update search loading field. Results ready");

			}

			twitterSearch.setLastActivationTime(GregorianCalendar.getInstance());
			twitterSearch.setLoading(false);

			twitterCache.updateTwitterSearch(twitterSearch);

			// TwitterAnalysisLauncher twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
			// twitterLauncher.startHistoricalSearch();

			logger.debug("Instance " + key + " of HistoricalSearchJob. Executing updating search scheduler on search #: " + searchID);

			TwitterSearchScheduler searchScheduler = twitterSearch.getTwitterSearchScheduler();

			int repeatFrequency = searchScheduler.getRepeatFrequency();
			String repeatType = searchScheduler.getRepeatType().toString();

			// Calendar startingCalendar = searchScheduler.getStartingTime();

			Calendar startingCalendar = GregorianCalendar.getInstance();

			startingCalendar.set(Calendar.SECOND, 0);
			startingCalendar.set(Calendar.MILLISECOND, 0);

			if (repeatType != null) {
				if (repeatType.equals("Day")) {
					startingCalendar.add(Calendar.DAY_OF_MONTH, repeatFrequency);

				} else if (repeatType.equals("Hour")) {
					startingCalendar.add(Calendar.HOUR_OF_DAY, repeatFrequency);
				}
			}

			searchScheduler.setStartingTime(startingCalendar);

			twitterCache.updateTwitterSearchScheduler(searchScheduler);
		}

		catch (Throwable t) {

			throw new SpagoBIRuntimeException("HistoricalSearchJob: An error occurred in historical search job", t);
		}

	}

	private TwitterSearchAPISpider initializeSearchAPI(TwitterSearch twitterSearch) {

		TwitterSearchAPISpider twitterSearchAPI = new TwitterSearchAPISpider();

		twitterSearchAPI.setCache((this.twitterCache));
		twitterSearchAPI.setLanguage(this.languageCode);

		twitterSearchAPI.setResultType(ResultType.recent);

		if (twitterSearch.getBooleanOperator() == BooleanOperatorEnum.FREE) {

			String textQuery = twitterSearch.getKeywords();
			twitterSearchAPI.setQuery(textQuery);

		} else if (twitterSearch.getBooleanOperator() == BooleanOperatorEnum.AND) {

			String textQuery = "";
			String[] keywordsArr = twitterSearch.getKeywords().split(",");

			// TODO: 5 as default, query too complex

			for (int i = 0; (i < keywordsArr.length) && (i < 5); i++) {
				if (i == 0) {
					textQuery = keywordsArr[i].trim();
				} else {
					textQuery = textQuery + " " + keywordsArr[i].trim();
				}
			}
			twitterSearchAPI.setQuery(textQuery);

		} else if (twitterSearch.getBooleanOperator() == BooleanOperatorEnum.OR) {

			String textQuery = "";
			String[] keywordsArr = twitterSearch.getKeywords().split(",");

			// TODO: 5 as default, query too complex

			for (int i = 0; (i < keywordsArr.length) && (i < 5); i++) {
				if (i == 0) {
					textQuery = keywordsArr[i].trim();
				} else {
					textQuery = textQuery + " OR " + keywordsArr[i].trim();
				}
			}
			twitterSearchAPI.setQuery(textQuery);

		}

		Integer daysAgo = twitterSearch.getDaysAgo();

		if (daysAgo != null) {
			logger.debug("Method initializeSearchAPI() for historical search job: Search with a starting date");

			int daysAgoValue = daysAgo.intValue();

			Calendar actualDate = GregorianCalendar.getInstance();

			this.sinceCalendar = actualDate;

			this.sinceCalendar.add(Calendar.DAY_OF_MONTH, -daysAgoValue);

		}

		twitterSearchAPI.setTwitterSearch(twitterSearch);
		twitterSearchAPI.setSinceDate(this.sinceCalendar);

		return twitterSearchAPI;

	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public void setSinceCalendar(Calendar sinceCalendar) {
		this.sinceCalendar = sinceCalendar;
	}

}
