package it.eng.spagobi.twitter.analysis.utilities;

import it.eng.spagobi.utilities.assertion.Assert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class TwitterRScriptUtility {

	static final Logger logger = Logger.getLogger(TwitterRScriptUtility.class);

	// private static final String HOST = "athos.engilab.ewebpd.eng.it";
	// private static final int PORT = 1447;
	private static final String RSERVICEPATH_BASE = "http://localhost:8080/SpagoBIDataMiningEngine/external/1.0/execute?";
	private static final String RSCRIPT_SENTIMENT = "fileR=Sentiment_Analysis_Twitter_DB_unificato.r";
	private static final String RSCRIPT_SENTIMENT_LIBS = "RMySQL,DBI,tau,tm,plyr";

	private static final String RSCRIPT_TOPICS = "fileR=Topic_Modeling_Twitter_TM_outputDB_unificato.r";
	private static final String RSCRIPT_TOPICS_LIBS = "RMySQL,DBI,reshape2,topicmodels,slam,stringr,tau,tm,plyr";

	public static void callSentimentRScript(long searchId) throws Throwable {

		logger.debug("Method callSentimentRScript(): Calling Sentiment R Script for searchID = " + searchId);

		Properties rservicesProp = new Properties();

		String rservicesProperties = "rservices.properties";

		InputStream inputStream = TwitterRScriptUtility.class.getClassLoader().getResourceAsStream(rservicesProperties);

		rservicesProp.load(inputStream);

		Assert.assertNotNull(rservicesProp, "Impossible to call R REST services without a valid rservices.properties file");

		String base_path = rservicesProp.getProperty("rservicesBasePath");

		// String urlString = RSERVICEPATH_BASE + RSCRIPT_SENTIMENT + "&searchId=" + searchId + "&libraries=" + RSCRIPT_SENTIMENT_LIBS;
		String urlString = base_path + RSCRIPT_SENTIMENT + "&search_id=" + searchId + "&libraries=" + RSCRIPT_SENTIMENT_LIBS;

		logger.debug("Method callSentimentRScript() - URL is: " + urlString);

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

	public static void callTopicsRScript(long searchId) throws Throwable {

		logger.debug("Method callTopicsRScript(): Calling Topics R Script for searchID = " + searchId);

		Properties rservicesProp = new Properties();

		String rservicesProperties = "rservices.properties";

		InputStream inputStream = TwitterRScriptUtility.class.getClassLoader().getResourceAsStream(rservicesProperties);

		rservicesProp.load(inputStream);

		Assert.assertNotNull(rservicesProp, "Impossible to call R REST services without a valid rservices.properties file");

		String base_path = rservicesProp.getProperty("rservicesBasePath");

		String urlString = base_path + RSCRIPT_TOPICS + "&search_id=" + searchId + "&libraries=" + RSCRIPT_TOPICS_LIBS;

		logger.debug("URL is: " + urlString);

		URL url = new URL(urlString);

		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		logger.debug("Method callTopicsRScript(): Reading Topics R Script result message..");

		String line = null;

		String json = "";

		while ((line = reader.readLine()) != null) {
			json = json + line;
		}

		reader.close();

		logger.debug("Method callTopicsRScript(): R Script result is " + json);

	}
}
