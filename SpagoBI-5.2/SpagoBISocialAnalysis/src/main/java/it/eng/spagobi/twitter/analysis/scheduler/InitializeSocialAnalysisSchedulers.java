package it.eng.spagobi.twitter.analysis.scheduler;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.entities.TwitterMonitorScheduler;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearchScheduler;
import it.eng.spagobi.twitter.analysis.enums.SearchRepeatTypeEnum;
import it.eng.spagobi.twitter.analysis.launcher.TwitterAnalysisLauncher;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class InitializeSocialAnalysisSchedulers implements Job {

	static final Logger logger = Logger.getLogger(InitializeSocialAnalysisSchedulers.class);
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.debug("Initializing Twitter Stream");

		try {
			TwitterSearch twitterSearch = twitterCache.isPresentEnabledStream();

			if (twitterSearch != null) {

				// TwitterMonitorScheduler twitterMonitor = twitterSearch.getTwitterMonitorScheduler();
				//
				// if (twitterMonitor != null) {
				// twitterMonitor.setEndingTime(AnalysisUtility.setMonitorSchedulerEndingDate(twitterMonitor));
				// twitterMonitor.setActiveSearch(false);
				//
				// twitterCache.updateTwitterMonitorScheduler(twitterMonitor);

				String languageCode = null;

				// initializing the launcher with this search
				TwitterAnalysisLauncher twitterLauncher = new TwitterAnalysisLauncher(twitterSearch);
				twitterLauncher.setLanguageCode(languageCode);

				twitterLauncher.restartStreamingSearch();

			}

			List<TwitterSearch> hLoadingSearches = twitterCache.getHistoricalLoadingSearches();

			if (hLoadingSearches != null && hLoadingSearches.size() > 0) {
				for (TwitterSearch hLoadingSearch : hLoadingSearches) {
					hLoadingSearch.setLoading(false);
					twitterCache.updateTwitterSearch(hLoadingSearch);
				}
			}

			// logger.debug("All streams must to be loading = 0");
			// twitterCache.stopAllStreams();

			SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

			Scheduler sched = schedFact.getScheduler();

			sched.start();

			/*************************** SEARCH SCHEDULER **************************************/

			List<TwitterSearchScheduler> searchSchedulers = twitterCache.getAllActiveSearchSchedulers();

			if (searchSchedulers != null && searchSchedulers.size() > 0) {

				for (TwitterSearchScheduler searchScheduler : searchSchedulers) {
					long searchID = searchScheduler.getTwitterSearch().getSearchID();
					Calendar startingCalendar = searchScheduler.getStartingTime();
					int frequency = searchScheduler.getRepeatFrequency();
					SearchRepeatTypeEnum type = searchScheduler.getRepeatType();

					JobDetail hSearchJob = JobBuilder.newJob(HistoricalSearchJob.class).withIdentity("HSearchJob_" + searchID, "groupHSearch")
							.usingJobData("searchID", searchID).build();

					java.util.Date startingDateJob = new java.util.Date(startingCalendar.getTimeInMillis());

					if (type.toString().equalsIgnoreCase(SearchRepeatTypeEnum.Day.toString())) {

						Trigger trigger = TriggerBuilder.newTrigger().withIdentity("HSearchTgr_" + searchID, "groupHSearch").startAt(startingDateJob)
								.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(frequency, DateBuilder.IntervalUnit.DAY))
								.build();

						// Tell quartz to schedule the job using our trigger
						sched.scheduleJob(hSearchJob, trigger);

					} else if (type.toString().equalsIgnoreCase(SearchRepeatTypeEnum.Hour.toString())) {

						Trigger trigger = TriggerBuilder
								.newTrigger()
								.withIdentity("HSearchTgr_" + searchID, "groupHSearch")
								.startAt(startingDateJob)
								.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(frequency, DateBuilder.IntervalUnit.HOUR))
								.build();

						// Tell quartz to schedule the job using our trigger
						sched.scheduleJob(hSearchJob, trigger);

					}
				}
			}

			/************************ MONITOR SCHEDULER **********************************/

			List<TwitterMonitorScheduler> monitorSchedulers = twitterCache.getAllMonitorSchedulers();

			for (TwitterMonitorScheduler monitorScheduler : monitorSchedulers) {
				long searchID = monitorScheduler.getTwitterSearch().getSearchID();
				boolean activeSearch = monitorScheduler.isActiveSearch();

				JobDetail hSearchJob = JobBuilder.newJob(MonitoringResourcesJob.class).withIdentity("MonitoringJob_" + searchID, "groupMonitoring")
						.usingJobData("searchID", searchID).build();

				int repeatFrequency = monitorScheduler.getRepeatFrequency();
				String repeatType = monitorScheduler.getRepeatType().toString();

				Calendar startingCalendar = monitorScheduler.getStartingTime();

				if (activeSearch) {

					// search attiva, monitor senza end

					if (repeatType.equalsIgnoreCase("Day")) {

						// startingCalendar.add(Calendar.DAY_OF_MONTH, repeatFrequency);

						Date startingDateJob = new java.util.Date(startingCalendar.getTimeInMillis());

						Trigger trigger = TriggerBuilder
								.newTrigger()
								.withIdentity("MonitoringTgr_" + searchID, "groupMonitoring")
								.startAt(startingDateJob)
								.withSchedule(
										CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(repeatFrequency, DateBuilder.IntervalUnit.DAY))
								.build();

						sched.scheduleJob(hSearchJob, trigger);
					} else if (repeatType.equalsIgnoreCase("Hour")) {

						// startingCalendar.add(Calendar.HOUR_OF_DAY, repeatFrequency);

						Date startingDateJob = new java.util.Date(startingCalendar.getTimeInMillis());

						Trigger trigger = TriggerBuilder
								.newTrigger()
								.withIdentity("MonitoringTgr_" + searchID, "groupMonitoring")
								.startAt(startingDateJob)
								.withSchedule(
										CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(repeatFrequency, DateBuilder.IntervalUnit.HOUR))
								.build();

						sched.scheduleJob(hSearchJob, trigger);

					}

				} else {
					// search non attiva,
					// monitor con end

					java.util.Date endingDateJob = new Date();

					Calendar endingCalendar = monitorScheduler.getEndingTime();
					endingDateJob = new java.util.Date(endingCalendar.getTimeInMillis());

					if (repeatType.equalsIgnoreCase("Day")) {

						// startingCalendar.add(Calendar.DAY_OF_MONTH, repeatFrequency);

						if (endingCalendar.compareTo(startingCalendar) > 0) {

							Date startingDateJob = new java.util.Date(startingCalendar.getTimeInMillis());

							Trigger trigger = TriggerBuilder
									.newTrigger()
									.withIdentity("MonitoringTgr_" + searchID, "groupMonitoring")
									.startAt(startingDateJob)
									.endAt(endingDateJob)
									.withSchedule(
											CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(repeatFrequency,
													DateBuilder.IntervalUnit.DAY)).build();

							sched.scheduleJob(hSearchJob, trigger);
						}
					} else if (repeatType.equalsIgnoreCase("Hour")) {

						// startingCalendar.add(Calendar.HOUR_OF_DAY, repeatFrequency);

						if (endingCalendar.compareTo(startingCalendar) > 0) {

							Date startingDateJob = new java.util.Date(startingCalendar.getTimeInMillis());

							Trigger trigger = TriggerBuilder
									.newTrigger()
									.withIdentity("MonitoringTgr_" + searchID, "groupMonitoring")
									.startAt(startingDateJob)
									.endAt(endingDateJob)
									.withSchedule(
											CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(repeatFrequency,
													DateBuilder.IntervalUnit.HOUR)).build();

							sched.scheduleJob(hSearchJob, trigger);
						}

					}
				}
			}

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("InitializeSocialAnalysisSchedulers: Error initializing jobs for search and monitor schedulers", t);
		}
	}
}
