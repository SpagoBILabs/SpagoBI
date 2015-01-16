/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.services;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.ConsoleEngineInstance;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.IServiceResponse;
import it.eng.spagobi.utilities.service.JSONResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */
public class WebServiceGenOrdersAction extends AbstractConsoleEngineAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 921161222986649682L;

	public static final String SERVICE_NAME = "MONITOR";

	// request parameters
	public static String USER_ID = "userId";
	public static String CALLBACK = "callback";
	public static String MESSAGE = "message";

	private final String DOC_YEAR = "anno";
	private final String DOC_MONTH_FROM = "mese_da";
	private final String DOC_MONTH_TO = "mese_a";
	private final String DOC_ID_PROVIDER = "ditta";
	private final String DOC_ID_UNITY = "unitaoperativa";

	private final String WS_DATA = "wsData";
	private final String GENORDERS_PROPERTIES = "genorders.properties";

	private final String FIELD_OPUNITY = "UNITA OPERATIVA";
	private final String FIELD_NAMEP = "PRESTAZIONE";
	private final String FIELD_PIVA = "PARTITA IVA";
	private final String FIELD_STORE = "CODICE MAGAZZINO";
	private final String FIELD_CDC = "CODICE CDC";
	private final String FIELD_AMOUNT = "TOTALE EROGATO";
	private final String FIELD_PRICE = "COSTO PRESTAZIONE";
	private final String FIELD_AUTH_NUMBER = "NUMERO AUTORIZZAZIONE";

	private final String TAG_NAMEP_OPEN = "<nota>";
	private final String TAG_NAMEP_CLOSE = "</nota>\n";
	private final String TAG_PIVA_OPEN = "<partitaIva>";
	private final String TAG_PIVA_CLOSE = "</partitaIva>\n";
	private final String TAG_STORE_OPEN = "<magazzino>";
	private final String TAG_STORE_CLOSE = "</magazzino>\n";
	private final String TAG_CDC_OPEN = "<cdcDest>";
	private final String TAG_CDC_CLOSE = "</cdcDest>\n";
	private final String TAG_AMOUNT_OPEN = "<quantita>";
	private final String TAG_AMOUNT_CLOSE = "</quantita>\n";
	private final String TAG_PRICE_OPEN = "<prezzo>";
	private final String TAG_PRICE_CLOSE = "</prezzo>\n";

	private final String TAG_SESSIONID = "SessionID";

	private final String TAG_EXCEPTION = "Exception";
	private final String TAG_EXCEPTION_CODE = "Code";

	private final String TAG_ERROR = "error";
	private final String TAG_ERROR_CODE = "errorCode";
	private final String TAG_ERROR_MESSAGE = "errorMessage";

	private final String TAG_RESPONSE_NUMBER = "numero";

	// private final Map<String, Boolean> finalResultBooleanMap = new HashMap<String, Boolean>();
	// private final Map<String, String> finalResultMessagesMap = new HashMap<String, String>();
	private final List<PLogOrder> ordersResult = new ArrayList<PLogOrder>();
	private String finalMessage = "";

	// logger component
	private static Logger logger = Logger.getLogger(WebServiceGenOrdersAction.class);
	ConsoleEngineInstance consoleEngineInstance;

	private int documentYear;
	private String documentMonthFrom;
	private String documentMonthTo;
	private String documentIdProvider;
	private String documentOpUnity;

	@Override
	public void service(SourceBean request, SourceBean response) {

		logger.debug("Method service(): Start");

		String message;
		String user;
		String callback;

		Monitor monitor = MonitorFactory.start("SpagoBI_Console.WebServiceGenOrdersAction.service");

		try {

			super.service(request, response);

			// check for mandatory parameters
			user = getAttributeAsString(USER_ID);
			logger.debug("Parameter [" + USER_ID + "] is equals to [" + user + "]");
			Assert.assertTrue(!StringUtilities.isEmpty(user), "Parameter [" + USER_ID + "] cannot be null or empty");

			String tempDocumentYear = getAttributeAsString(DOC_YEAR);
			logger.debug("Parameter [" + DOC_YEAR + "] is equals to [" + tempDocumentYear + "]");
			// Assert.assertTrue(!StringUtilities.isEmpty(tempDocumentYear), "Parameter [" + DOC_YEAR + "] cannot be null or empty");
			if (tempDocumentYear != null && !tempDocumentYear.equals("")) {
				this.documentYear = Integer.parseInt(tempDocumentYear);
			}

			documentMonthFrom = getAttributeAsString(DOC_MONTH_FROM);
			logger.debug("Parameter [" + DOC_MONTH_FROM + "] is equals to [" + documentMonthFrom + "]");
			// Assert.assertTrue(!StringUtilities.isEmpty(documentMonthFrom), "Parameter [" + DOC_MONTH_FROM + "] cannot be null or empty");

			documentMonthTo = getAttributeAsString(DOC_MONTH_TO);
			logger.debug("Parameter [" + DOC_MONTH_TO + "] is equals to [" + documentMonthTo + "]");
			// Assert.assertTrue(!StringUtilities.isEmpty(documentMonthTo), "Parameter [" + DOC_MONTH_TO + "] cannot be null or empty");

			documentIdProvider = getAttributeAsString(DOC_ID_PROVIDER);
			logger.debug("Parameter [" + DOC_ID_PROVIDER + "] is equals to [" + documentIdProvider + "]");
			// Assert.assertTrue(!StringUtilities.isEmpty(documentIdProvider), "Parameter [" + DOC_ID_PROVIDER + "] cannot be null or empty");

			documentOpUnity = getAttributeAsString(DOC_ID_UNITY);
			logger.debug("Parameter [" + DOC_ID_UNITY + "] is equals to [" + documentOpUnity + "]");

			callback = getAttributeAsString(CALLBACK);
			logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");

			// get static properties
			Properties genOrdersProps = getWSGenOrdersPropertiesFile(GENORDERS_PROPERTIES);

			// gets hashmap with all parameters
			LinkedHashMap<String, Object> params;
			params = getAttributesAsLinkedMap();

			// get sid from login web service
			String sid = login(genOrdersProps);

			Assert.assertTrue(!StringUtilities.isEmpty(sid), "Session ID from login call cannot be null or empty");

			String wsData = (String) params.get(WS_DATA);

			// logger.debug("WS DATA: " + wsDataJSON);
			Assert.assertTrue(!StringUtilities.isEmpty(wsData), "Select at least one row from Console.");
			JSONArray wsDataJSON = new JSONArray(wsData.toUpperCase());

			if (wsDataJSON != null && wsDataJSON.length() > 0) {

				callWSGenOrders(genOrdersProps, sid, wsDataJSON);

				manageResponseToClient();
			}
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			monitor.stop();
			logger.debug("Method service(): End");
		}
	}

	private Properties getWSGenOrdersPropertiesFile(String fileName) throws Throwable {

		logger.debug("Method getWSGenOrdersPropertiesFile(): Start");

		Properties genOrdersProps = new Properties();

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);

		genOrdersProps.load(inputStream);

		Assert.assertNotNull(genOrdersProps, "Impossible to call the Web Service without a valid properties file");

		logger.debug("Method getWSGenOrdersPropertiesFile(): End");

		return genOrdersProps;
	}

	private String login(Properties genOrdersProps) throws Throwable {

		logger.debug("Method login(): Start");

		String loginAddressEndpoint = genOrdersProps.getProperty("loginAddress");

		Assert.assertTrue(!StringUtilities.isEmpty(loginAddressEndpoint), "The address to call the login web service cannot be null or empty");

		String loginUsername = genOrdersProps.getProperty("username");
		String loginPassword = genOrdersProps.getProperty("password");
		String company = genOrdersProps.getProperty("azienda");
		String loginOffice = genOrdersProps.getProperty("ufficio");
		String loginRole = genOrdersProps.getProperty("ruolo");
		String loginEntity = genOrdersProps.getProperty("entita");
		String loginJobDate = "";

		logger.debug("Method login(): Create XML login string");

		String message = "<?xml version=\"1.0\" ?>\n" + "<Input>\n" + "<OpenSession>\n" + "<user>" + loginUsername + "</user>\n" + "<password>" + loginPassword + "</password>\n"
				+ "<azienda>" + company + "</azienda>\n" + "<ufficio>" + loginOffice + "</ufficio>\n" + "<ruolo>" + loginRole + "</ruolo>\n" + "<dataLavoro>" + loginJobDate
				+ "</dataLavoro>\n" + "<entita>" + loginEntity + "</entita>\n" + "</OpenSession>\n" + "</Input>";

		logger.debug("Method login(): Login XML request message is [ " + message + " ]");

		String loginMessageRet = send(message, loginAddressEndpoint);

		Assert.assertTrue(!StringUtilities.isEmpty(loginMessageRet), "The Login WS response cannot be null or empty");

		logger.debug("Method login(): Login XML response message is [ " + loginMessageRet + " ]");

		String sid = getSessionIdFromLogin(loginMessageRet);

		logger.debug("Method login(): End");
		return sid;
	}

	private String getSessionIdFromLogin(String loginResponseXML) throws Throwable {

		logger.debug("Method getSessionIdFromLogin(): Start");

		String sid = "";

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(loginResponseXML.getBytes("utf-8"))));

		if (doc.hasChildNodes()) {

			// sid = printSessionID(doc.getChildNodes());

			NodeList nodeList = doc.getElementsByTagName(TAG_SESSIONID);

			if (nodeList == null || nodeList.getLength() == 0) {
				logger.error("Login Error! The response from the server is [ " + loginResponseXML + " ]");
				throw new Throwable("Login Error! The response from the server is [ " + loginResponseXML + " ]");
				// throw new Throwable("Tag [ " + TAG_SESSIONID + " ] is not present in login response!");
			}

			for (int count = 0; count < nodeList.getLength(); count++) {

				Node tempNode = nodeList.item(count);
				if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

					sid = tempNode.getTextContent();
				}
			}

		}

		logger.debug("Method getSessionIdFromLogin(): End");
		return sid;
	}

	// private String printSessionID(NodeList nodeList) {
	//
	// logger.debug("Method printSessionID(): Start");
	//
	// String sessionID = "";
	//
	// for (int count = 0; count < nodeList.getLength(); count++) {
	//
	// Node tempNode = nodeList.item(count);
	//
	// // make sure it's element node.
	// if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
	//
	// String nodeName = tempNode.getNodeName();
	//
	// if (nodeName.equals(TAG_SESSIONID)) {
	// sessionID = tempNode.getTextContent();
	// break;
	// }
	//
	// if (tempNode.hasChildNodes()) {
	//
	// // loop again if has child nodes
	// sessionID = printSessionID(tempNode.getChildNodes());
	// break;
	// }
	//
	// }
	//
	// }
	//
	// logger.debug("Method printSessionID(): End");
	//
	// return sessionID;
	//
	// }

	private void callWSGenOrders(Properties genOrdersProps, String sid, JSONArray wsDataParam) throws Throwable {

		logger.debug("Method callWSGenOrders(): Start");

		String genOrdersEndpoint = genOrdersProps.getProperty("genOrdersAddress");

		Assert.assertTrue(!StringUtilities.isEmpty(genOrdersEndpoint), "The address to call the web service to submit orders cannot be null or empty");

		String wsPurchaserOffice = genOrdersProps.getProperty("ufficioOrdinante");
		String wsRefNumber = genOrdersProps.getProperty("numeroRiferimento");
		String wsOrderTypology = genOrdersProps.getProperty("tipologiaOrdine");
		// String wsOrderDate = genOrdersProps.getProperty("dataOrdine");

		String wsOrderState = genOrdersProps.getProperty("statoOrdine");
		String company = genOrdersProps.getProperty("azienda");

		// Map<String, List<JSONObject>> stores = getAllStores(wsDataParam);

		// get list of op unities to send different orders
		Map<String, List<JSONObject>> opUnities = getAllOpUnities(wsDataParam);

		if (opUnities != null && opUnities.size() > 0) {

			for (Map.Entry<String, List<JSONObject>> entry : opUnities.entrySet()) {

				java.sql.Timestamp orderTimestamp = new Timestamp(System.currentTimeMillis());
				String wsOrderDate = new SimpleDateFormat("dd/MM/yyyy").format(orderTimestamp);

				String xmlOrder = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Input SessionID=\"$$SID$$\">\n" + "<WebServicesOggettiComuni id=\"APPROV\">\n"
						+ "<GenerazioneOrdini>\n" + "<Ordine>\n" + "<azienda>"
						+ company
						+ "</azienda>\n"
						+ "<ufficioOrdinante>"
						+ wsPurchaserOffice
						+ "</ufficioOrdinante>\n"
						+ "<numeroRiferimento>"
						+ wsRefNumber
						+ "</numeroRiferimento>\n"
						+ "<tipologiaOrdine>"
						+ wsOrderTypology
						+ "</tipologiaOrdine>\n"
						+ "<dataOrdine>"
						+ wsOrderDate
						+ "</dataOrdine>\n"
						+ TAG_PIVA_OPEN
						+ "$$partitaIva$$"
						+ TAG_PIVA_CLOSE
						+ TAG_STORE_OPEN
						+ "$$store$$"
						+ TAG_STORE_CLOSE
						+ "<statoOrdine>"
						+ wsOrderState
						+ "</statoOrdine>\n"
						+ "<prodotti>\n";

				String partitaIva = "";
				String storeCod = "";
				String authNumber = "";
				List<JSONObject> productsForUnity = entry.getValue();

				for (JSONObject obj : productsForUnity) {

					if (!obj.isNull(FIELD_PIVA)) {
						Assert.assertTrue(!StringUtilities.isEmpty(obj.getString(FIELD_PIVA)), "The param [ " + FIELD_PIVA + " ] can't be null or empty");
						partitaIva = obj.getString(FIELD_PIVA);
					}

					if (!obj.isNull(FIELD_STORE)) {
						Assert.assertTrue(!StringUtilities.isEmpty(obj.getString(FIELD_STORE)), "The param [ " + FIELD_STORE + " ] can't be null or empty");
						storeCod = obj.getString(FIELD_STORE);
					}

					if (!obj.isNull(FIELD_AUTH_NUMBER)) {
						Assert.assertTrue(!StringUtilities.isEmpty(obj.getString(FIELD_AUTH_NUMBER)), "The param [ " + FIELD_AUTH_NUMBER + " ] can't be null or empty");
						authNumber = obj.getString(FIELD_AUTH_NUMBER);
					}

					String productXML = createProductXML(obj, genOrdersProps, authNumber);

					xmlOrder = xmlOrder + productXML;
				}

				xmlOrder = xmlOrder + "</prodotti>\n" + "</Ordine>\n" + "</GenerazioneOrdini>\n" + "</WebServicesOggettiComuni>\n" + "</Input>\n";

				xmlOrder = xmlOrder.replace("$$SID$$", sid);
				xmlOrder = xmlOrder.replace("$$partitaIva$$", partitaIva);
				xmlOrder = xmlOrder.replace("$$store$$", storeCod);

				logger.debug("Method callWSGenOrders(): Order XML request message is [ " + xmlOrder + " ]");

				String messageRet = send(xmlOrder, genOrdersEndpoint);

				Assert.assertTrue(!StringUtilities.isEmpty(messageRet), "The WS generate orders response cannot be null or empty");

				logger.debug("Method callWSGenOrders(): Order XML response message is [ " + messageRet + " ]");

				String errorMessage = checkResponseError(messageRet);

				PLogOrder logOrder = new PLogOrder();
				logOrder.setDocumentYearLog(this.documentYear);
				logOrder.setDocumentMonthFromLog(this.documentMonthFrom);
				logOrder.setDocumentMonthToLog(this.documentMonthTo);
				logOrder.setDocumentIdProviderLog(this.documentIdProvider);
				logOrder.setpIvaLog(partitaIva);
				logOrder.setOpUnityLog(entry.getKey());
				logOrder.setOrderTimestampLog(orderTimestamp);

				if (errorMessage != null) {

					logOrder.setOrderFailedLog(true);
					logOrder.setErrorLog(errorMessage);
					logOrder.setOrderNumberLog(null);

				} else {

					logOrder.setOrderFailedLog(false);
					logOrder.setErrorLog(null);
					logOrder.setOrderNumberLog(getXMLResponseOrderNumber(messageRet));
				}

				// add for client response message
				this.ordersResult.add(logOrder);

				// insert order log in DB log table for order results
				insertPLogOrders(logOrder);
			}
		}

		logger.debug("Method callWSGenOrders(): End");

	}

	private String createProductXML(JSONObject rowJSON, Properties genOrdersProps, String authNumber) throws Throwable {

		logger.debug("Method createProductXML(): Start");

		String wsProductCode = genOrdersProps.getProperty("codiceProdotto");
		String wsAuthsOffice = genOrdersProps.getProperty("ufficioAutorizzazioni");
		// String wsYear = genOrdersProps.getProperty("anno");
		Calendar actualCalendar = GregorianCalendar.getInstance();
		int actualYear = actualCalendar.get(Calendar.YEAR);
		String wsYear = Integer.toString(actualYear);

		// String wsNumber = genOrdersProps.getProperty("numero"); -> modifica 15/01/2015, parametro dinamico
		String wsNumber = authNumber;
		String company = genOrdersProps.getProperty("azienda");
		String wsSub = genOrdersProps.getProperty("sub");

		String productToAdd = "<Prodotto>\n" + "<codiceProdotto>" + wsProductCode + "</codiceProdotto>\n" + "<autorizzazione>\n" + "<azienda>" + company + "</azienda>\n"
				+ "<ufficioAutorizzazioni>" + wsAuthsOffice + "</ufficioAutorizzazioni>\n" + "<anno>" + wsYear + "</anno>\n" + "<numero>" + wsNumber + "</numero>\n" + "<sub>"
				+ wsSub + "</sub>\n" + "</autorizzazione>\n";

		if (!rowJSON.isNull(FIELD_CDC)) {
			productToAdd = productToAdd + TAG_CDC_OPEN + rowJSON.getString(FIELD_CDC) + TAG_CDC_CLOSE;
		}

		if (!rowJSON.isNull(FIELD_AMOUNT)) {
			productToAdd = productToAdd + TAG_AMOUNT_OPEN + rowJSON.getString(FIELD_AMOUNT) + TAG_AMOUNT_CLOSE;
		}

		if (!rowJSON.isNull(FIELD_PRICE)) {
			productToAdd = productToAdd + TAG_PRICE_OPEN + rowJSON.getString(FIELD_PRICE) + TAG_PRICE_CLOSE;
		}

		if (!rowJSON.isNull(FIELD_NAMEP)) {
			productToAdd = productToAdd + TAG_NAMEP_OPEN + rowJSON.getString(FIELD_NAMEP) + TAG_NAMEP_CLOSE;
		}

		productToAdd = productToAdd + "</Prodotto>\n";

		logger.debug("Method createProductXML(): End");
		return productToAdd;
	}

	private String send(String xml, String endpoint) throws Throwable {

		logger.debug("Method send(): Start");

		String xmlOut = "";

		Service service = new Service();
		Call call = (Call) service.createCall();

		MessageContext serviceMsgContext = null;

		if (extracted(service) != null) {
			serviceMsgContext = extracted(service).getMessageContext();
		}

		MessageContext callMsgContext = call.getMessageContext();

		if (serviceMsgContext != null) {
			callMsgContext.setProperty(HTTPConstants.HEADER_COOKIE, serviceMsgContext.getProperty(HTTPConstants.HEADER_COOKIE));
		}

		call.setTargetEndpointAddress(new java.net.URL(endpoint));
		call.setOperationName("call");
		call.addParameter("xml", XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType(XMLType.XSD_STRING);

		xmlOut = (String) call.invoke(new Object[] { xml });

		logger.debug("Method send(): End");
		return xmlOut;

	}

	private Call extracted(Service service) throws ServiceException {
		return service.getCall();
	}

	// /**
	// * This method retrieves all distinct stores inside selected products
	// *
	// * @param wsDataParam
	// * @return
	// */
	// private Map<String, List<JSONObject>> getAllStores(JSONArray wsDataParam) throws Throwable {
	// logger.debug("Method getAllStores(): Start");
	//
	// Map<String, List<JSONObject>> stores = new HashMap<String, List<JSONObject>>();
	//
	// for (int i = 0; i < wsDataParam.length(); i++) {
	// JSONObject obj = (JSONObject) wsDataParam.get(i);
	//
	// // check if the JSON Object has a valid key for param store
	// if (!obj.isNull(FIELD_STORE)) {
	//
	// // the value for this param cannot be null or empty
	// Assert.assertTrue(!StringUtilities.isEmpty(obj.getString(FIELD_STORE)), "Impossible to generate an order without a valid value for param [ " +
	// FIELD_STORE + " ]");
	//
	// String tempStore = obj.getString(FIELD_STORE).trim();
	//
	// // if the result map doesn't contain this store, add the new store as key and json object to linked prdocuts list
	// if (!stores.containsKey(tempStore)) {
	//
	// List<JSONObject> productsForStore = new ArrayList<JSONObject>();
	// productsForStore.add(obj);
	// stores.put(tempStore, productsForStore);
	//
	// logger.debug("Method getAllStores(): New store [ " + tempStore + " ] added to stores map");
	// } else {
	// List<JSONObject> productsForStore = stores.get(tempStore);
	// productsForStore.add(obj);
	// stores.put(tempStore, productsForStore);
	//
	// logger.debug("Method getAllStores(): Store [ " + tempStore + " ] updated with a new product");
	// }
	// }
	// }
	//
	// logger.debug("Method getAllStores(): End");
	// return stores;
	// }

	/**
	 * This method retrieves all distinct op unity inside selected products
	 *
	 * @param wsDataParam
	 * @return
	 */
	private Map<String, List<JSONObject>> getAllOpUnities(JSONArray wsDataParam) throws Throwable {

		logger.debug("Method getAllOpUnities(): Start");

		Map<String, List<JSONObject>> opUnities = new HashMap<String, List<JSONObject>>();

		for (int i = 0; i < wsDataParam.length(); i++) {
			JSONObject obj = (JSONObject) wsDataParam.get(i);

			// check if the JSON Object has a valid key for param op unity
			if (!obj.isNull(FIELD_OPUNITY)) {

				// the value for this param cannot be null or empty
				Assert.assertTrue(!StringUtilities.isEmpty(obj.getString(FIELD_OPUNITY)), "Impossible to generate an order without a valid value for param [ " + FIELD_OPUNITY
						+ " ]");

				String tempOpUnity = obj.getString(FIELD_OPUNITY).trim();

				// if the result map doesn't contain this store, add the new store as key and json object to linked prdocuts list
				if (!opUnities.containsKey(tempOpUnity)) {

					List<JSONObject> productsForStore = new ArrayList<JSONObject>();
					productsForStore.add(obj);
					opUnities.put(tempOpUnity, productsForStore);

					logger.debug("Method getAllOpUnities(): New op unity [ " + tempOpUnity + " ] added to unities map");
				} else {
					List<JSONObject> productsForUnity = opUnities.get(tempOpUnity);
					productsForUnity.add(obj);
					opUnities.put(tempOpUnity, productsForUnity);

					logger.debug("Method getAllOpUnities(): Op Unity [ " + tempOpUnity + " ] updated with a new product");
				}
			}
		}

		logger.debug("Method getAllOpUnities(): End");
		return opUnities;
	}

	private String getXMLResponseOrderNumber(String messageRet) throws Throwable {

		logger.debug("Method getXMLResponseOrderNumber(): Start");

		String result = "";

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(messageRet.getBytes("utf-8"))));

		if (doc.hasChildNodes()) {

			NodeList nodeListResponseNumber = doc.getElementsByTagName(TAG_RESPONSE_NUMBER);

			if (nodeListResponseNumber == null || nodeListResponseNumber.getLength() == 0) {
				throw new Throwable("No exception found in web service response, but it's impossible to read the Order Number tag");
			}

			for (int count = 0; count < nodeListResponseNumber.getLength(); count++) {

				Node tempNode = nodeListResponseNumber.item(count);
				if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

					result = tempNode.getTextContent();
				}
			}
		}

		logger.debug("Method getXMLResponseOrderNumber(): End");
		return result;

	}

	private String checkResponseError(String messageRet) throws Throwable {
		logger.debug("Method checkResponseErrorAndException(): Start");

		String error = null;

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(messageRet.getBytes("utf-8"))));

		if (doc.hasChildNodes()) {

			NodeList nodeListException = doc.getElementsByTagName(TAG_EXCEPTION);

			if (nodeListException != null && nodeListException.getLength() > 0) {

				NodeList nodeListCodeException = doc.getElementsByTagName(TAG_EXCEPTION_CODE);

				if (nodeListCodeException == null || nodeListCodeException.getLength() == 0) {
					throw new Throwable("Exception in web service response, but it's impossible to read exception code");
				}
				for (int count = 0; count < nodeListCodeException.getLength(); count++) {

					Node tempNode = nodeListCodeException.item(count);
					if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

						error = "<Exception><Code>" + tempNode.getTextContent() + "</Code></Exception>";
					}
				}

			} else {
				NodeList nodeListError = doc.getElementsByTagName(TAG_ERROR);
				{
					if (nodeListError != null && nodeListError.getLength() > 0) {

						NodeList nodeListCodeError = doc.getElementsByTagName(TAG_ERROR_CODE);
						NodeList nodeListMessageError = doc.getElementsByTagName(TAG_ERROR_MESSAGE);

						if (nodeListCodeError == null || nodeListCodeError.getLength() == 0 || nodeListMessageError == null || nodeListMessageError.getLength() == 0) {
							throw new Throwable("Error in web service response, but it's impossible to read error code or error message");
						}

						for (int count = 0; count < nodeListCodeError.getLength(); count++) {

							Node tempNode = nodeListCodeError.item(count);
							if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
								error = "<error><errorCode>" + tempNode.getTextContent() + "</errorCode>";
							}
						}

						for (int count = 0; count < nodeListMessageError.getLength(); count++) {

							Node tempNode = nodeListMessageError.item(count);
							if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

								error = error + "<errorMessage>" + tempNode.getTextContent() + "</errorMessage></error>";
							}
						}

					}
				}
			}

		}

		logger.debug("Method checkResponseErrorAndException(): End");
		return error;

	}

	private void manageResponseToClient() throws Throwable {

		logger.debug("Method manageResponseToClient(): Start");

		if (this.ordersResult != null && this.ordersResult.size() > 0) {
			for (PLogOrder logOrder : this.ordersResult) {
				if (logOrder.orderFailedLog) {
					finalMessage = finalMessage + " Ordine unita operativa [ " + logOrder.getOpUnityLog() + " ] - KO!";
				} else {
					finalMessage = finalMessage + " Ordine unita operativa [ " + logOrder.getOpUnityLog() + " ] - OK!";
				}
			}
		}

		replayToClient(null);

		logger.debug("Method manageResponseToClient(): End");
	}

	private void insertPLogOrders(PLogOrder pLogOrder) throws Throwable {

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			logger.debug("Method insertPLogOrders(): Start");

			String query = "INSERT INTO P_LOG_ORDINI " + "(NUMERO_ORDINE, " + "ANNO, " + "MESE_DA, " + "MESE_A, " + "ID_FORNITORE, " + "PARTITA_IVA, " + "ID_UNITA_OPERATIVA, "
					+ "ERRORE, " + "DATA_ORDINE) " + "VALUES " + "(?,?,?,?,?,?,?,?,?)";

			consoleEngineInstance = getConsoleEngineInstance();
			IDataSource ds = consoleEngineInstance.getDataSource();
			connection = ds.getConnection();
			connection.setAutoCommit(false);

			logger.debug("Inserting log [" + pLogOrder.toString() + "] into P_LOG_ORDINI");

			try {

				ps = connection.prepareStatement(query);

			} catch (SQLException e) {

				throw new SQLException("Impossible to create a prepared statement for query [" + query + "]", e);
			}

			try {
				ps.setString(1, pLogOrder.orderNumberLog);
				ps.setInt(2, pLogOrder.documentYearLog);
				ps.setString(3, pLogOrder.documentMonthFromLog);
				ps.setString(4, pLogOrder.documentMonthToLog);
				ps.setString(5, pLogOrder.documentIdProviderLog);
				ps.setString(6, pLogOrder.pIvaLog);
				ps.setString(7, pLogOrder.getOpUnityLog());
				ps.setString(8, pLogOrder.getErrorLog());
				ps.setTimestamp(9, pLogOrder.orderTimestampLog);
			} catch (SQLException e) {
				throw new SQLException("Impossible to set the value of the log in query [" + query + "]", e);
			}

			logger.debug("Prameters has been  succesfully replaced in statement  [" + ps.toString() + "]");

			try {
				ps.executeUpdate();
			} catch (SQLException e) {
				throw new SQLException("Impossible to execute statement [" + ps.toString() + "]", e);
			}
			logger.debug("Statement [" + ps.toString() + "] has been  succesfully executed");
			connection.commit();

		} catch (SQLException e) {
			throw new Throwable("Impossible to insert a new entry into P_LOG_ORDINI", e);
		} finally {

			releaseResources(connection, ps, null);
			logger.debug("Method insertPLogOrders(): End");
		}
	}

	private void replayToClient(final SpagoBIServiceException e) {

		try {

			writeBackToClient(new IServiceResponse() {

				public boolean isInline() {
					return false;
				}

				public int getStatusCode() {
					if (e != null) {
						return JSONResponse.FAILURE;
					}
					return JSONResponse.SUCCESS;
				}

				public String getFileName() {
					return null;
				}

				public String getContentType() {
					return "text/html";
				}

				public String getContent() throws IOException {
					JSONObject toReturn = new JSONObject();
					if (e != null) {
						try {
							toReturn.put("success", false);
							toReturn.put("msg", e.getMessage());
							return toReturn.toString();
						} catch (JSONException jSONException) {
							logger.error(jSONException);
						}
					}
					toReturn = new JSONObject();
					try {
						toReturn.put("success", true);
						toReturn.put("msg", finalMessage);
					} catch (JSONException jSONException) {
						logger.error(jSONException);
					}
					// return "{success:true, file:null}";
					return toReturn.toString();

				}

			});

		} catch (IOException ioException) {
			logger.error("Impossible to write back the responce to the client", ioException);
		}
	}

	// ==============================================================================================
	// Release resources
	// ==============================================================================================

	private void releaseResources(Connection connection, Statement statement, ResultSet resultSet) {

		logger.debug("IN");

		try {
			logger.debug("Relesing resources ...");
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [resultSet]", e);
				}
				logger.debug("[resultSet] released succesfully");
			}

			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [statement]", e);
				}
				logger.debug("[statement] released succesfully");
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [connection]", e);
				}
				logger.debug("[connection] released succesfully");
			}
			logger.debug("All resources have been released succesfully");
		} finally {
			logger.debug("OUT");
		}
	}

	class PLogOrder {

		private int documentYearLog;
		private String documentMonthFromLog;
		private String documentMonthToLog;
		private String documentIdProviderLog;

		private boolean orderFailedLog = false;

		private String pIvaLog;
		private String opUnityLog;

		private String orderNumberLog;
		private String errorLog;

		private java.sql.Timestamp orderTimestampLog;

		public PLogOrder() {

		}

		public int getDocumentYearLog() {
			return documentYearLog;
		}

		public void setDocumentYearLog(int documentYearLog) {
			this.documentYearLog = documentYearLog;
		}

		public String getDocumentMonthFromLog() {
			return documentMonthFromLog;
		}

		public void setDocumentMonthFromLog(String documentMonthFromLog) {
			this.documentMonthFromLog = documentMonthFromLog;
		}

		public String getDocumentMonthToLog() {
			return documentMonthToLog;
		}

		public void setDocumentMonthToLog(String documentMonthToLog) {
			this.documentMonthToLog = documentMonthToLog;
		}

		public String getDocumentIdProviderLog() {
			return documentIdProviderLog;
		}

		public void setDocumentIdProviderLog(String documentIdProviderLog) {
			this.documentIdProviderLog = documentIdProviderLog;
		}

		public boolean isOrderFailedLog() {
			return orderFailedLog;
		}

		public void setOrderFailedLog(boolean orderFailedLog) {
			this.orderFailedLog = orderFailedLog;
		}

		public String getpIvaLog() {
			return pIvaLog;
		}

		public void setpIvaLog(String pIvaLog) {
			this.pIvaLog = pIvaLog;
		}

		public String getOpUnityLog() {
			return opUnityLog;
		}

		public void setOpUnityLog(String opUnityLog) {
			this.opUnityLog = opUnityLog;
		}

		public String getOrderNumberLog() {
			return orderNumberLog;
		}

		public void setOrderNumberLog(String orderNumberLog) {
			this.orderNumberLog = orderNumberLog;
		}

		public String getErrorLog() {
			return errorLog;
		}

		public void setErrorLog(String errorLog) {
			this.errorLog = errorLog;
		}

		public java.sql.Timestamp getOrderTimestampLog() {
			return orderTimestampLog;
		}

		public void setOrderTimestampLog(java.sql.Timestamp orderTimestampLog) {
			this.orderTimestampLog = orderTimestampLog;
		}

		@Override
		public String toString() {
			return "PLogOrder [documentYearLog=" + documentYearLog + ", documentMonthFromLog=" + documentMonthFromLog + ", documentMonthToLog=" + documentMonthToLog
					+ ", documentIdProviderLog=" + documentIdProviderLog + ", orderFailedLog=" + orderFailedLog + ", pIvaLog=" + pIvaLog + ", opUnityLog=" + opUnityLog
					+ ", orderNumberLog=" + orderNumberLog + ", errorLog=" + errorLog + ", orderTimestampLog=" + orderTimestampLog + "]";
		}

	}
}
