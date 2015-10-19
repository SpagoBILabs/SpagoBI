package it.eng.spagobi.twitter.analysis.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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

	public static void callSentimentRScript(long searchId) {

		logger.debug("Method callSentimentRScript(): Calling Sentiment R Script for searchID = " + searchId);

		try {

			Properties rservicesProp = new Properties();

			String rservicesProperties = "rservices.properties";

			InputStream inputStream = TwitterRScriptUtility.class.getClassLoader().getResourceAsStream(rservicesProperties);

			rservicesProp.load(inputStream);

			// Assert.assertNotNull(rservicesProp, "Impossible to call R REST services without a valid rservices.properties file");

			if (rservicesProp != null) {

				String base_path = rservicesProp.getProperty("rservicesBasePath");

				if (base_path != null && !base_path.trim().equals("")) {

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

					logger.debug("Method callSentimentRScript(): R Script result is " + json);

					reader.close();
				}
			}

		} catch (FileNotFoundException e) {
			logger.debug("Method callSentimentRScript(): Sentiment script not found.");
		} catch (IOException e) {
			logger.debug("Method callSentimentRScript(): Sentiment script not found.");
		}

	}

	public static void callTopicsRScript(long searchId) {

		logger.debug("Method callTopicsRScript(): Calling Topics R Script for searchID = " + searchId);

		try {

			Properties rservicesProp = new Properties();

			String rservicesProperties = "rservices.properties";

			InputStream inputStream = TwitterRScriptUtility.class.getClassLoader().getResourceAsStream(rservicesProperties);

			rservicesProp.load(inputStream);

			// Assert.assertNotNull(rservicesProp, "Impossible to call R REST services without a valid rservices.properties file");

			if (rservicesProp != null) {

				String base_path = rservicesProp.getProperty("rservicesBasePath");

				if (base_path != null && !base_path.trim().equals("")) {

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

					logger.debug("Method callTopicsRScript(): R Script result is " + json);

					reader.close();
				}
			}

		} catch (FileNotFoundException e) {
			logger.debug("Method callTopicsRScript(): Topics script not found.");
		} catch (IOException e) {
			logger.debug("Method callTopicsRScript(): Topics script not found.");
		}

	}
}
