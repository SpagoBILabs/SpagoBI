<%--

LICENSE: see LICENSE.txt file 

--%>
<%@ page session="true" contentType="text/html; charset=UTF-8" 
		 import="org.dom4j.Document,
				 org.dom4j.Node,
				 java.io.InputStreamReader,
				 java.util.List,
				 com.thoughtworks.xstream.XStream,
				 it.eng.spagobi.jpivotaddins.bean.*,
				 it.eng.spagobi.jpivotaddins.util.*,
				 java.io.InputStream,
				 sun.misc.BASE64Decoder,
				 java.util.*,
				 org.apache.log4j.Logger,
				 com.tonbeller.jpivot.olap.model.OlapModel,
				 it.eng.spagobi.services.proxy.ContentServiceProxy,
				 it.eng.spagobi.services.proxy.ArtifactServiceProxy,
				 it.eng.spagobi.services.content.bo.Content,
				 it.eng.spagobi.services.proxy.DataSourceServiceProxy,
				 it.eng.spagobi.services.datasource.bo.SpagoBiDataSource,
				 it.eng.spagobi.tools.datasource.bo.*,
				 org.eigenbase.xom.Parser,
				 org.dom4j.io.SAXReader,
				 it.eng.spagobi.services.common.EnginConf"%>

<%@ page import="it.eng.spagobi.utilities.ParametersDecoder"%>
<%@ page import="it.eng.spagobi.commons.utilities.StringUtilities"%>
<%@ page import="it.eng.spago.security.IEngUserProfile"%>
<%@ page import="it.eng.spagobi.jpivotaddins.crossnavigation.SpagoBICrossNavigationConfig"%>
<%@ page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@ page import="it.eng.spagobi.jpivotaddins.schema.MondrianSchemaManager"%>

<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%!
	private static String CONNECTION_NAME = "connectionName";

    private Logger logger = Logger.getLogger("it.eng.spagobi.dynamicOlap_jsp");

    private Integer getArtifactVersionId(HttpServletRequest request) {
        try {
            if (request.getParameter(SpagoBIConstants.SBI_ARTIFACT_VERSION_ID) == null 
                    || request.getParameter(SpagoBIConstants.SBI_ARTIFACT_VERSION_ID).trim().equals("")) {
                throw new Exception("Request is missing artifact version id missing");
            }
            Integer id = new Integer(request.getParameter(SpagoBIConstants.SBI_ARTIFACT_VERSION_ID));
            return id;
        } catch (Exception e) {
            logger.error("Error while getting artifact version id", e);
            throw new RuntimeException("Error while getting artifact version id", e);
        }
    }
    
    private String getReference(HttpServletRequest request) {
        try {
        	HttpSession session = request.getSession();
        	String userId = (String) session.getAttribute("userId");
            ArtifactServiceProxy artifactProxy = new ArtifactServiceProxy(userId, session);
            MondrianSchemaManager schemaManager = new MondrianSchemaManager(artifactProxy);
            Integer artifactVersionId = getArtifactVersionId(request);
            logger.debug("Artifact version id :" + artifactVersionId);
            String reference = schemaManager.getMondrianSchemaURI(artifactVersionId);
            logger.debug("Reference: " + reference);
            // adjust reference
            if (!reference.startsWith("file:")) {
                reference = "file:" + reference;
                logger.debug("Reference changed to " + reference);
            }
            return reference;
        } catch (Exception e) {
            logger.error("Error while getting model reference", e);
            throw new RuntimeException("Error while getting model reference", e);
        }
    }
    
    private Document getTemplateAsDom4jDocument(HttpServletRequest request, HashMap requestParameters) {
    	InputStream is = null;
    	Document document = null;
    	try {
	    	HttpSession session = request.getSession();
	    	String userId = (String)session.getAttribute("userId");
	    	String documentId = (String) session.getAttribute("document");
	    	ContentServiceProxy contentProxy = new ContentServiceProxy(userId, session);
	        Content template = contentProxy.readTemplate(documentId, requestParameters);
	        BASE64Decoder bASE64Decoder = new BASE64Decoder();
	        byte[] templateContent = bASE64Decoder.decodeBuffer(template.getContent());
	        is = new java.io.ByteArrayInputStream(templateContent);
	        org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
	        document = reader.read(is);
	        return document;
    	} catch (Exception e) {
    		logger.error("Error while getting template as a XML document", e);
    		throw new RuntimeException("Error while getting template as a XML document", e);
    	} finally {
    		try {
	    		if (is != null) {
	    			is.close();
	    		}
    		} catch (Exception e) {
    			logger.error("Error while closing stream", e);
    		}
    	}
    }
    
    private AnalysisBean getSubobjectContentAsAnalysisBean(HttpServletRequest request) {
        InputStream is = null;
        InputStreamReader isr = null;
        AnalysisBean analysis = null;
        try {
            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("userId");
            String subObjectId = request.getParameter("subobjectId");
            ContentServiceProxy contentProxy = new ContentServiceProxy(userId, session);
            Content subObject = contentProxy.readSubObjectContent(subObjectId);
            String subobjdata64Coded = subObject.getContent();
            BASE64Decoder bASE64Decoder = new BASE64Decoder();
            byte[] subobjBytes = bASE64Decoder.decodeBuffer(subobjdata64Coded);
            is = new java.io.ByteArrayInputStream(subobjBytes);
            isr = new InputStreamReader(is);
            XStream dataBinder = new XStream();
            analysis = (AnalysisBean) dataBinder.fromXML(isr, new AnalysisBean());
            return analysis;
        } catch (Exception e) {
            logger.error("Error while loading AnalysisBean for analysis definition", e);
            throw new RuntimeException("Error while loading AnalysisBean for analysis definition", e);
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (Exception e) {
                logger.error("Error while closing InputStreamReader", e);
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.error("Error while closing ByteArrayInputStream", e);
            }
        }
    }
%>

<%
	List parameters = null;
	String reference = null, query = null;
	AnalysisBean analysis = null;
	Document document = null;
	try {
		SaveAnalysisBean analysisBean = (SaveAnalysisBean) session.getAttribute("save01");
		String nameSubObject = request.getParameter("nameSubObject");
		String userId = (String)session.getAttribute("userId");
		String documentId = (String)session.getAttribute("document");		
		ContentServiceProxy contentProxy = new ContentServiceProxy(userId, session);
		
		reference = this.getReference(request);

		String requestConnectionName = (String) request.getParameter(CONNECTION_NAME);
		logger.debug("Connection name found on request: " + requestConnectionName);
		
		//calls service for gets data source object		
		DataSourceServiceProxy proxyDS = new DataSourceServiceProxy(userId, session);
		IDataSource ds = null;
		if (requestConnectionName != null) {
		    ds = proxyDS.getDataSourceByLabel(requestConnectionName);
		} else {
		    ds = proxyDS.getDataSource(documentId);
		}

		
		if (nameSubObject != null) {
			// if request contains parameter "nameSubObject", the engine must run a subQuery
			
			HashMap requestParameters = new HashMap();
			//parameter that permits to tell to SpagoBI not to realize a control on the parameters passed.
			requestParameters.put("SBI_READ_ONLY_TEMPLATE","true");
			
			// remove the dimension_access_rules parameter because it produces an exception (not blocking but the exception 
			// is visilble on the console) since it is not compatible with multi value parameter encoding.
			// TODO move the cube profiling information from driver to engine 
			requestParameters.remove("dimension_access_rules");
			
			document = this.getTemplateAsDom4jDocument(request, requestParameters);
			
			// if subObject execution in the request there are the description and visibility
			String descrSO = request.getParameter("descriptionSubObject");
			if (descrSO == null) {
				descrSO = "";
			}
			String visSO = request.getParameter("visibilitySubObject");
			if (visSO == null) {
				visSO = "Private";
			}
			analysisBean.setAnalysisName(nameSubObject);
			analysisBean.setAnalysisDescription(descrSO);
			// the possible values of the visibility are (Private/Public)
			analysisBean.setAnalysisVisibility(visSO);			

			analysis = this.getSubobjectContentAsAnalysisBean(request);
			query = analysis.getMdxQuery();
				
		} else {
			// normal execution (no subObject)
			
			HashMap requestParameters = ParametersDecoder.getDecodedRequestParameters(request);
			
			// remove the dimension_access_rules parameter because it produces an exception (not blocking but the exception 
			// is visilble on the console) since it is not compatible with multi value parameter encoding.
			// TODO move the cube profiling information from driver to engine 
			requestParameters.remove("dimension_access_rules");
			
			document = this.getTemplateAsDom4jDocument(request, requestParameters);
		    
			query = document.selectSingleNode("//olap/MDXquery").getStringValue();
			
			parameters = document.selectNodes("//olap/MDXquery/parameter");
			analysis = new AnalysisBean();
			session.setAttribute("analysisBean", analysis);
	
		}
		//Check for Toolbar Configuration and put it in session...
		ToolbarBean tb = new ToolbarBean();
		tb.setValuesFromTemplate(document);
		session.setAttribute("toolbarButtonsVisibility", tb);
		
		//Check for cross navigation configuration and put it in session...
		Node crossNavigation = document.selectSingleNode("//olap/CROSS_NAVIGATION");
		if (crossNavigation != null) {
			SpagoBICrossNavigationConfig cninfo = new SpagoBICrossNavigationConfig(crossNavigation);
			session.setAttribute(SpagoBICrossNavigationConfig.ID, cninfo);
		}
		
	    //Read data access information and put it in session...
	    Node filtersData = document.selectSingleNode("//olap/DATA-ACCESS");
	    if (filtersData != null) {
	    	String filters = filtersData.asXML();
		    if (filters != null && filters.length() > 1){
		    	session.setAttribute("filters", filters);
		    }
	    }
		
		// SUBSTITUTE QUERY PARAMETERS
		query = ParameterUtilities.substituteQueryParameters(query, parameters, request);
		IEngUserProfile profile = (IEngUserProfile) session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		query = StringUtilities.substituteProfileAttributesInString(query, profile);
				
		// BASED ON CONNECTION TYPE WRITE THE RIGHT MONDRIAN QUERY TAG		
		if(ds != null  && ds.getJndi() != null && !ds.getJndi().equals("")) {
			String resName = ds.getJndi();
			if (ds.checkIsMultiSchema()){
				String schema=null;
				try {
						String attrname=ds.getSchemaAttribute();
						
						if (attrname!=null) schema = (String)profile.getUserAttribute(attrname);
						if (schema==null) logger.error("Cannot retrive ENTE");
						else resName=resName+schema;
				} catch (Exception e) {
					logger.error("Cannot retrive ENTE", e);
				}
			}
			
			
			//resName = resName.replaceAll("java:comp/env/","");
		%>
			
			
			
			
			<jp:mondrianQuery id="query01" dataSource="<%=resName%>"  catalogUri="<%=reference%>">
				<%=query%>
				
				<%
				if (document != null) {
				List clickables = document.selectNodes("//olap/MDXquery/clickable");
				if (clickables != null && clickables.size() > 0) {
					for (int i = 0; i < clickables.size(); i++) {
						Node clickable = (Node) clickables.get(i);
						String targetDocument = clickable.valueOf("@targetDocument");
						String target = clickable.valueOf("@target");
						String title = clickable.valueOf("@title");
						String targetDocumentParameters = "";
						List clickParameters = clickable.selectNodes("clickParameter");
						if (clickParameters != null && clickParameters.size() > 0) {
							for (int j = 0; j < clickParameters.size(); j++) {
								Node clickParameter = (Node) clickParameters.get(j);
								String clickParameterName = clickParameter.valueOf("@name");
								String clickParameterValue = clickParameter.valueOf("@value");
								targetDocumentParameters += clickParameterName + "=" + clickParameterValue + "&";
							}
						}
						String uniqueName = clickable.valueOf("@uniqueName");
						String urlPattern = "javascript:parent.execCrossNavigation(window.name, ''" + targetDocument + "'', ''" + targetDocumentParameters + "''";
						if(title!=null && target!=null && target.equalsIgnoreCase("tab")){
							urlPattern +=",null,''"+title+"'',''tab''";
						}else if(title!=null){
							urlPattern +=",null,''"+title+"''";
						}
						urlPattern +=");";
						%>
						<jp:clickable urlPattern="<%=urlPattern%>" uniqueName="<%=uniqueName%>"/>
						<%
					}
				}
				}
				%>
				
			</jp:mondrianQuery>
		<%	
		} else {
			logger.debug("Using Direct Connection:");
		%>
			 <jp:mondrianQuery id="query01" jdbcDriver="<%=ds.getDriver()%>" jdbcUrl="<%=ds.getUrlConnection()%>" 
			                   jdbcUser="<%=ds.getUser()%>" jdbcPassword="<%=ds.getPwd()%>" catalogUri="<%=reference%>" >
				<%=query%>
								
				<%
				if (document != null) {
				List clickables = document.selectNodes("//olap/MDXquery/clickable");
				if (clickables != null && clickables.size() > 0) {
					for (int i = 0; i < clickables.size(); i++) {
						Node clickable = (Node) clickables.get(i);
						String targetDocument = clickable.valueOf("@targetDocument");
						String target = clickable.valueOf("@target");
						String title = clickable.valueOf("@title");
						String targetDocumentParameters = "";
						List clickParameters = clickable.selectNodes("clickParameter");
						if (clickParameters != null && clickParameters.size() > 0) {
							for (int j = 0; j < clickParameters.size(); j++) {
								Node clickParameter = (Node) clickParameters.get(j);
								String clickParameterName = clickParameter.valueOf("@name");
								String clickParameterValue = clickParameter.valueOf("@value");
								targetDocumentParameters += clickParameterName + "=" + clickParameterValue + "&";
							}
						}
						String uniqueName = clickable.valueOf("@uniqueName");
						String urlPattern = "javascript:parent.execCrossNavigation(window.name, ''" + targetDocument + "'', ''" + targetDocumentParameters + "''";
						if(title!=null && target!=null && target.equalsIgnoreCase("tab")){
							urlPattern +=",null,''"+title+"'',''tab''";
						}else if(title!=null){
							urlPattern +=",null,''"+title+"''";
						}
						urlPattern +=");";
						%>
						<jp:clickable urlPattern="<%=urlPattern%>" uniqueName="<%=uniqueName%>"/>
						<%
					}
				}
				}
				%>
				
			</jp:mondrianQuery>	
		<%	
		}		
		
		
		
		// IN CASE OF SUBOBJECT EXECUTION PUT INTO SESSION THE ANALYSYS BEAN AND INCLUDE THE ANALYSIS JSP	
		if (nameSubObject != null) {
			session.setAttribute("analysisBean", analysis);
			%>
				<jsp:include page="customizeAnalysis.jsp"/>
			<%
		}
	
		/* */
		// CHECK IF THERE ARE DATA ACCESS FILTER AND IN CASE SET THE MONDRIAN ROLE
		OlapModel olapModel = (OlapModel) session.getAttribute("query01");
		String dimensionAccessRules = (String)session.getAttribute("dimension_access_rules");
		logger.debug(this.getClass().getName() + ":dimension access rules: " + dimensionAccessRules);
		DataSecurityManager dsm = new DataSecurityManager(olapModel, dimensionAccessRules, query);
		dsm.setMondrianRole();
		//FilteringUtilities.setMondrianRole(olapModel, dimensionAccessRules, query);
		
		
		
	} catch (Exception e){
		logger.error(this.getClass().getName() + ":error while executing query \n" + e);
		throw e;
	}%>
