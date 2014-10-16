package it.eng.spagobi.twitter.analysis.scheduler;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearchScheduler;
import it.eng.spagobi.twitter.analysis.launcher.TwitterAnalysisLauncher;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

@DisallowConcurrentExecution
public class HistoricalSearchJob implements Job {

	static final Logger logger = Logger.getLogger(HistoricalSearchJob.class);
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private long searchID;

	public HistoricalSearchJob() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			JobKey key = context.getJobDetail().getKey();

			TwitterSearch twitterSearch = twitterCache.findTwitterSearch(searchID);

			logger.debug("Instance " + key + " of HistoricalSearchJob. Executing search #: " + searchID);

			TwitterAnalysisLauncher twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
			twitterLauncher.startHistoricalSearch();

			logger.debug("Instance " + key + " of HistoricalSearchJob. Executing updating search scheduler on search #: " + searchID);

			TwitterSearchScheduler searchScheduler = twitterSearch.getTwitterSearchScheduler();

			int repeatFrequency = searchScheduler.getRepeatFrequency();
			String repeatType = searchScheduler.getRepeatType().toString();

			Calendar startingCalendar = searchScheduler.getStartingTime();

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

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

}
