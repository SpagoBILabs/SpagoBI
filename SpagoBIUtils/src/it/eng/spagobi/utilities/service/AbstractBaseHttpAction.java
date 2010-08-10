/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/
package it.eng.spagobi.utilities.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.action.AbstractHttpAction;
import it.eng.spagobi.container.IBeanContainer;
import it.eng.spagobi.container.SpagoBIHttpSessionContainer;
import it.eng.spagobi.container.SpagoBIRequestContainer;
import it.eng.spagobi.container.SpagoBIResponseContainer;
import it.eng.spagobi.container.SpagoBISessionContainer;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractBaseHttpAction extends AbstractHttpAction {
	
	private SpagoBIRequestContainer spagoBIRequestContainer;
	private SpagoBIResponseContainer spagoBIResponseContainer;
	private SpagoBISessionContainer spagoBISessionContainer;
	private SpagoBIHttpSessionContainer spagoHttpBISessionContainer;
	
	
	/**
     * Logger component
     */
    private static transient Logger logger = Logger.getLogger(AbstractBaseHttpAction.class);
    
	
	public void init(SourceBean config) {
        super.init(config);
    } 
	

	// =================================================================================================
	// REQUEST utility methods 
	// =================================================================================================
	
	
	protected SpagoBIRequestContainer getSpagoBIRequestContainer() {
		return spagoBIRequestContainer;
		
	}

	protected void setSpagoBIRequestContainer(SourceBean request) {
		spagoBIRequestContainer = new SpagoBIRequestContainer( request );
	}
	
	public boolean requestContainsAttribute(String attrName) {		
		return !getSpagoBIRequestContainer().isNull( attrName );
	}
	
	public boolean requestContainsAttribute(String attrName, String attrValue) {
		return ( requestContainsAttribute(attrName) && getAttribute(attrName).toString().equalsIgnoreCase(attrValue) );
	}
		
	public Object getAttribute(String attrName) {
		return getSpagoBIRequestContainer().get(attrName);
	}
	
	public String getAttributeAsString(String attrName) {
		return getSpagoBIRequestContainer().getString( attrName );
	}
	
	public Integer getAttributeAsInteger(String attrName) {
		return getSpagoBIRequestContainer().getInteger( attrName );
	}
	
	public boolean getAttributeAsBoolean(String attrName) {
		return getAttributeAsBoolean(attrName, false);
	}

	public boolean getAttributeAsBoolean(String attrName, boolean defaultValue) {
		if( getAttribute(attrName) == null ) return defaultValue;
		return getSpagoBIRequestContainer().getBoolean( attrName ).booleanValue();
	}
	
	public List getAttributeAsList(String attrName) {
		return getSpagoBIRequestContainer().toList( attrName );
	}
	
	public List getAttributeAsCsvStringList(String attrName, String separator) {
		return getSpagoBIRequestContainer().toCsvList( attrName );
	}
	
	public JSONObject getAttributeAsJSONObject(String attrName) {
		return getSpagoBIRequestContainer().toJSONObject( attrName );
	}	
	
	public JSONArray getAttributeAsJSONArray(String attrName) {
		return getSpagoBIRequestContainer().toJSONArray( attrName );
	}	
	
	public Map<String,Object> getAttributesAsMap() {
		
		List attributeNames;
		String attributeName;
		Object attributeVaue;
		HashMap<String , Object> attributesMap;
		Iterator it;
		
		attributesMap = new HashMap <String , Object> ();
		attributeNames = getSpagoBIRequestContainer().getKeys();
		
		it = attributeNames.iterator();
		while( it.hasNext() ) {
			attributeName = (String)it.next();
			attributeVaue = getAttribute(attributeName);
			attributesMap.put(attributeName, attributeVaue);
		}
		
		return attributesMap;
	}
	
	
	// =================================================================================================
	// RESPONSE utility methods
	// =================================================================================================
	
	protected SpagoBIResponseContainer getSpagoBIResponseContainer() {
		return this.spagoBIResponseContainer;
	}

	protected void setSpagoBIResponseContainer(SourceBean response) {
		this.spagoBIResponseContainer = new SpagoBIResponseContainer( response );
	}
	
	/**
	 * Sets the attribute.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void setAttribute(String key, Object value) {
		getSpagoBIResponseContainer().set(key, value);
	}
	
	public boolean tryToWriteBackToClient(String message) {
		try {
			writeBackToClient(message);
		} catch (IOException e) {
			logger.error("Impossible to write back to the client the message: [" + message + "]", e);
			return false;
		}
		
		return true;
	}
	
	public void writeBackToClient(String message) throws IOException {
		writeBackToClient(200, message,
				true,
				"service-response",
				"text/plain");
	}
	
	public void writeBackToClient(IServiceResponse response) throws IOException {
		writeBackToClient(response.getStatusCode(),
				response.getContent(),
				response.isInline(),
				response.getFileName(),
				response.getContentType());
	}
	
	public void writeBackToClient(int statusCode, String content, boolean inline, String fileName, String contentType) throws IOException {
		freezeHttpResponse();
		
		// setup response header
		getHttpResponse().setHeader("Content-Disposition", (inline?"inline":"attachment") + "; filename=\"" + fileName + "\";");
		getHttpResponse().setContentType( contentType );
		// encoding content using UTF8, since content length depends on encoding
		String utf8EncodedContent = null;
		if (content != null) {
			utf8EncodedContent = new String(content.getBytes("UTF-8"), "UTF-8");
		} else {
			utf8EncodedContent = "";
		}
		//getHttpResponse().setContentLength( utf8EncodedContent.length() );
		getHttpResponse().setCharacterEncoding( "UTF-8" );
		getHttpResponse().setStatus(statusCode);
		
		getHttpResponse().getWriter().print(utf8EncodedContent);
		getHttpResponse().getWriter().flush();
	}
	
	public void writeBackToClient(File file, IStreamEncoder encoder, 
			boolean inline, String contentName, String contentType) throws IOException {
		BufferedInputStream bis = null;
		
		logger.debug("Flushing file [" + file.getName() +"] - " + inline + " - " + contentType);
		
		bis = new BufferedInputStream( new FileInputStream(file) );
		try {
			writeBackToClient(bis, encoder, inline, contentName, contentType);
		} finally {
			bis.close();
		}		
	}
	
	public void writeBackToClient(InputStream in, IStreamEncoder encoder, boolean inline, String contentName, String contentType) throws IOException {		
		int contentLength = 0;
		int b = -1;
		
		freezeHttpResponse();
		
		// setup response header
		getHttpResponse().setHeader("Content-Disposition", (inline?"inline":"attachment") + "; filename=\"" + contentName + "\";");
		getHttpResponse().setContentType( contentType );
		
		if(encoder == null) {
			/*
			byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }

			 */
			byte[] buf = new byte[1024];
			while((b = in.read(buf)) != -1) {
				getHttpResponse().getOutputStream().write(buf, 0, b);
				contentLength+=b;
			}	
			getHttpResponse().setContentLength( contentLength );
		} else {
			encoder.encode(in, getHttpResponse().getOutputStream());
		}
		getHttpResponse().getOutputStream().flush();
		
		
	}
	
	// =================================================================================================
	// SESSION utility methods
	// =================================================================================================
	
	public SessionContainer getSessionContainer() {
		return getRequestContainer().getSessionContainer();
	}
	
	public IBeanContainer getSpagoBISessionContainer() {
		if(spagoBISessionContainer == null) {
			spagoBISessionContainer = new SpagoBISessionContainer( getSessionContainer() );
		}
		
		return spagoBISessionContainer;
	}
	
	public boolean sessionContainsAttribute(String attrName) {
		return !getSpagoBISessionContainer().isNull(attrName);
	}
		
	public Object getAttributeFromSession(String attrName) {
		return getSpagoBISessionContainer().get( attrName );
	}

	public String getAttributeFromSessionAsString(String attrName) {
		return getSpagoBISessionContainer().getString( attrName );
	}

	public boolean getAttributeFromSessionAsBoolean(String attrName) {
		return getAttributeFromSessionAsBoolean(attrName, false);
	}

	public boolean getAttributeFromSessionAsBoolean(String attrName, boolean defaultValue) {
		if( !sessionContainsAttribute(attrName) ) return defaultValue;
		return getSpagoBISessionContainer().getBoolean( attrName ).booleanValue();
	}
	
	public void delAttributeFromSession(String attrName) {
		if( sessionContainsAttribute(attrName) ) {
			getSpagoBISessionContainer().remove(attrName);
		}
	}
		
	
	public void setAttributeInSession(String attrName, Object attrValue) {
		delAttributeFromSession(attrName);
		getSpagoBISessionContainer().set(attrName, attrValue);
	}	
	
	
	// =================================================================================================
	// HTTP-SESSION utility methods
	// =================================================================================================
	
	
	public HttpSession getHttpSession() {		
		return getHttpRequest().getSession();
	}
	
	public IBeanContainer getSpagoBIHttpSessionContainer() {
		if(spagoHttpBISessionContainer == null) {
			spagoHttpBISessionContainer = new SpagoBIHttpSessionContainer( getHttpSession() );
		}
		
		return spagoHttpBISessionContainer;
	}
	
	public boolean httpSessionContainsAttribute(String attrName) {
		return !getSpagoBIHttpSessionContainer().isNull(attrName);
	}
	
	
	public Object getAttributeFromHttpSession(String attrName) {
		return getSpagoBIHttpSessionContainer().get(attrName);
	}
	
	
	public String getAttributeFromHttpSessionAsString(String attrName) {
		return getSpagoBIHttpSessionContainer().getString(attrName);
	}
	
	
	public boolean getAttributeFromHttpSessionAsBoolean(String attrName) {
		return getAttributeFromHttpSessionAsBoolean(attrName, false);
	}

	
	public boolean getAttributeFromHttpSessionAsBoolean(String attrName, boolean defaultValue) {
		if( !httpSessionContainsAttribute(attrName) ) return defaultValue;
		return getSpagoBIHttpSessionContainer().getBoolean( attrName ).booleanValue();
	}
	
	public void delAttributeFromHttpSession(String attrName) {
		if( httpSessionContainsAttribute(attrName) ) {
			getSpagoBIHttpSessionContainer().remove(attrName);
		}
	}
		
	public void setAttributeInHttpSession(String attrName, Object attrValue) {
		delAttributeFromHttpSession(attrName);
		getSpagoBIHttpSessionContainer().set(attrName, attrValue);
	}	

}
