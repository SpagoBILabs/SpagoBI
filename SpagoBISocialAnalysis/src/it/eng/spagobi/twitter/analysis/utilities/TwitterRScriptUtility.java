package it.eng.spagobi.twitter.analysis.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;

public class TwitterRScriptUtility {

	static final Logger logger = Logger.getLogger(TwitterRScriptUtility.class);

	// private static final String HOST = "athos.engilab.ewebpd.eng.it";
	// private static final int PORT = 1447;
	private static final String RSERVICEPATH_BASE = "http://localhost:8080/SpagoBIDataMiningEngine/external/1.0/execute?";
	private static final String RSCRIPT_SENTIMENT = "fileR=Sentiment_Analysis_Twitter_DB_unificato.r";
	private static final String RSCRIPT_SENTIMENT_LIBS = "RMySQL,DBI,tau,tm,plyr";

	private static final String RSCRIPT_TOPICS = "fileR=Topic_Modeling_Twitter_TM_outputDB_unificato.r";
	private static final String RSCRIPT_TOPICS_LIBS = "RMySQL,DBI,reshape2,lda,topicmodels,slam,stringr,tau,tm,pbapply,plyr";

	public static void callSentimentRScript(String userId, long searchId) throws Throwable {

		logger.debug("Method callSentimentRScript(): Calling Sentiment R Script for searchID = " + searchId);

		String urlString = RSERVICEPATH_BASE + RSCRIPT_SENTIMENT + "&searchId=" + searchId + "&libraries=" + RSCRIPT_SENTIMENT_LIBS;

		logger.debug("URL is: " + urlString);

		URL url = new URL(urlString);

		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		logger.debug("Method callSentimentRScript(): Reading Sentiment R Script result message..");

		String line = null;

		String json = "";

		while ((line = reader.readLine()) != null) {
			json = json + line;
		}

		reader.close();

		logger.debug("Method callSentimentRScript(): R Script result is " + json);

	}
}
