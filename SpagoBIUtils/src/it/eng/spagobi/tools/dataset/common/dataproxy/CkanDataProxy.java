package it.eng.spagobi.tools.dataset.common.dataproxy;

/* @author Alessandro Portosa (alessandro.portosa@eng.it)
 *
 */

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

public class CkanDataProxy extends AbstractDataProxy {

	String fileName;

	int maxResultsReader = -1;

	private static transient Logger logger = Logger.getLogger(CkanDataProxy.class);

	public CkanDataProxy(String resourcePath) {
		this.resPath = resourcePath;
	}

	public IDataStore load(String statement, IDataReader dataReader) throws EMFUserError {
		throw new UnsupportedOperationException("method CkanDataProxy not yet implemented");
	}

	public IDataStore load(IDataReader dataReader) {

		IDataStore dataStore = null;
		InputStream inputStream = null;

		try {
			// recover the file from resources!
			String filePath = this.resPath;
			String ckanApiKey = "05e90ca7-d788-47fc-a490-9510213218f7";
			inputStream = getInputStreamFromURL(filePath, ckanApiKey);
			dataReader.setMaxResults(this.getMaxResultsReader());
			dataStore = dataReader.read(inputStream);
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to load dataset", t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("Error closing input stream", e);
				}
			}
		}
		return dataStore;
	}

	// public String getCompleteFilePath() {
	// return resPath + File.separatorChar + fileName;
	// }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private InputStream getInputStreamFromURL(String fileURL, String ckanApiKey) throws IOException {
		logger.debug("IN");
		HttpClient httpClient = new HttpClient();
		GetMethod httpget = new GetMethod(fileURL);
		InputStream is = null;
		try {
			int statusCode = -1;
			initClient(httpClient);
			httpget.setRequestHeader("Authorization", ckanApiKey);
			statusCode = httpClient.executeMethod(httpget);
			if (statusCode == HttpStatus.SC_OK) {
				is = httpget.getResponseBodyAsStream();
			}
			logger.debug("OUT");
		} catch (Throwable t) {
			logger.error("Error while saving file into server: " + t);
			throw new SpagoBIServiceException("Error while saving file into server", t);
		}
		// return input stream from the HTTP connection
		return is;
	}

	private void initClient(HttpClient httpClient) {

		// Getting proxy properties set as JVM args
		// String proxyHost = System.getProperty("http.proxyHost");
		// String proxyPort = System.getProperty("http.proxyPort");
		// int proxyPortInt = CKANUtils.portAsInteger(proxyPort);
		// String proxyUsername = System.getProperty("http.proxyUsername");
		// String proxyPassword = System.getProperty("http.proxyPassword");

		String proxyHost = "proxy.eng.it";
		int proxyPortInt = 3128;
		String proxyUsername = "aportosa";
		String proxyPassword = "IBMDRL2013";

		logger.debug("Setting client to download CKAN resource");
		httpClient.setConnectionTimeout(500);

		if (proxyHost != null && proxyPortInt > 0) {
			if (proxyUsername != null && proxyPassword != null) {
				logger.debug("Setting proxy with authentication");
				httpClient.getHostConfiguration().setProxy(proxyHost, proxyPortInt);
				HttpState state = new HttpState();
				state.setProxyCredentials(null, null, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
				httpClient.setState(state);
				logger.debug("Proxy with authentication set");
			} else {
				// Username and/or password not acceptable. Trying to set proxy without credentials
				logger.debug("Setting proxy without authentication");
				httpClient.getHostConfiguration().setProxy(proxyHost, proxyPortInt);
				logger.debug("Proxy without authentication set");
			}
		}
		logger.debug("Client set");
	}

	private byte[] createChecksum() {
		logger.debug("IN");
		byte[] toReturn = null;
		InputStream fis = null;
		try {
			String filePath = this.resPath;
			String ckanApiKey = "05e90ca7-d788-47fc-a490-9510213218f7";
			fis = getInputStreamFromURL(filePath, ckanApiKey);

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;

			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);

			toReturn = complete.digest();

		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Cannot get file checksum", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("Error closing input stream", e);
				}
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	public String getMD5Checksum() {
		logger.debug("IN");
		byte[] checksum = this.createChecksum();
		BASE64Encoder encoder = new BASE64Encoder();
		String encoded = encoder.encode(checksum);
		logger.debug("OUT: returning [" + encoded + "]");
		return encoded;
	}

	public int getMaxResultsReader() {
		return maxResultsReader;
	}

	public void setMaxResultsReader(int maxResultsReader) {
		this.maxResultsReader = maxResultsReader;
	}
}