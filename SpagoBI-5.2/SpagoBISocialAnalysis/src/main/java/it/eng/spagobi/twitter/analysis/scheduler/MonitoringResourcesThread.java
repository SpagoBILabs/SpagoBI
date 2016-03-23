package it.eng.spagobi.twitter.analysis.scheduler;

import it.eng.spagobi.bitly.analysis.utilities.BitlyCounterClicksUtility;
import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.entities.TwitterMonitorScheduler;
import it.eng.spagobi.twitter.analysis.enums.MonitorRepeatTypeEnum;
import it.eng.spagobi.twitter.analysis.utilities.TwitterUserInfoUtility;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MonitoringResourcesThread implements Job {

	static final Logger logger = Logger.getLogger(MonitoringResourcesJob.class);
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private long searchID;

	public MonitoringResourcesThread() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		// JobKey key = context.getJobDetail().getKey();

		try {

			TwitterMonitorScheduler monitorScheduler = twitterCache.getMonitorSchedulerFromSearch(searchID);

			Assert.assertNotNull(monitorScheduler, "Monitoring Resources Thread: Impossible to create a monitor job without a linked entity");

			String links = "";
			String accounts = "";

			links = monitorScheduler.getLinks();
			accounts = monitorScheduler.getAccounts();

			if (links != null && !links.equals("")) {
				BitlyCounterClicksUtility bitlyUtil = new BitlyCounterClicksUtility(links, searchID);
				bitlyUtil.startBitlyAnalysis();
			}

			TwitterUserInfoUtility userUtil = new TwitterUserInfoUtility(searchID);

			if (accounts != null && !accounts.equals("")) {
				accounts = accounts.replaceAll("@", "");
				String[] accountsArr = accounts.split(",");

				if (accountsArr != null && accountsArr.length > 0) {
					for (int i = 0; i < accountsArr.length; i++) {
						String account = accountsArr[i].trim();
						userUtil.saveFollowersCount(account);
					}
				}
			}

			Calendar startingCalendar = GregorianCalendar.getInstance();

			startingCalendar.set(Calendar.SECOND, 0);
			startingCalendar.set(Calendar.MILLISECOND, 0);

			if (monitorScheduler.getRepeatType() == MonitorRepeatTypeEnum.Day) {

				startingCalendar.add(Calendar.DAY_OF_MONTH, monitorScheduler.getRepeatFrequency());
			}

			else if (monitorScheduler.getRepeatType() == MonitorRepeatTypeEnum.Hour) {

				startingCalendar.add(Calendar.HOUR_OF_DAY, monitorScheduler.getRepeatFrequency());
			}
			monitorScheduler.setStartingTime(startingCalendar);

			Calendar endingTime = monitorScheduler.getEndingTime();

			if (endingTime.compareTo(startingCalendar) < 0 && !monitorScheduler.isActiveSearch()) {

				monitorScheduler.setActive(false);
			}

			twitterCache.updateTwitterMonitorScheduler(monitorScheduler);

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Monitoring Resources Thread: An error occurred in monitoring resources job", t);
		}

	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

}
