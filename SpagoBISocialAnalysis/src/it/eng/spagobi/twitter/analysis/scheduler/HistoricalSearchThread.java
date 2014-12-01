package it.eng.spagobi.twitter.analysis.scheduler;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.enums.BooleanOperatorEnum;
import it.eng.spagobi.twitter.analysis.spider.search.TwitterSearchAPISpider;
import it.eng.spagobi.twitter.analysis.utilities.TwitterRScriptUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.Calendar;

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
public class HistoricalSearchThread implements Job {

	static final Logger logger = Logger.getLogger(HistoricalSearchThread.class);
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private long searchID;
	private String languageCode;
	private Calendar sinceCalendar;

	public HistoricalSearchThread() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			JobKey key = context.getJobDetail().getKey();

			TwitterSearch twitterSearch = twitterCache.findTwitterSearch(searchID);

			logger.debug("Instance " + key + " of HistoricalSearchThread. Executing search #: " + searchID);

			TwitterSearchAPISpider searchAPI = initializeSearchAPI(twitterSearch);

			searchAPI.collectTweets();

			// historical search completed, loading = false;

			if (twitterSearch.isrAnalysis()) {

				logger.debug("Method HistoricalSearchThread: Historical Search completed. Processing results for topics and sentiment..");

				TwitterRScriptUtility.callTopicsRScript(twitterSearch.getSearchID());
				TwitterRScriptUtility.callSentimentRScript(twitterSearch.getSearchID());

				logger.debug("Method HistoricalSearchThread: R Scripts called. Update search loading field. Results ready");
			}

			twitterSearch.setLoading(false);

			twitterCache.updateTwitterSearch(twitterSearch);

		}

		catch (Throwable t) {

			throw new SpagoBIRuntimeException("HistoricalSearchThread: An error occurred in historical search thread", t);
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
