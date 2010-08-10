package it.eng.spagobi.sdk.proxy;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Stub;

public abstract class AbstractSDKServiceProxy {

	boolean proxyRequirementAuthentication;

	public void setProxyHost(String proxyHost) {
		if(proxyHost!=null){
			AxisProperties.setProperty("http.proxyHost", proxyHost);
		}	
	}
	public void setProxyPort(String proxyPort) {
		if(proxyPort!=null){
			AxisProperties.setProperty("http.proxyPort", proxyPort);
		}
	}
	public void setProxyUserId(String proxyUserId) {
		if(proxyUserId!=null){
			AxisProperties.setProperty("http.proxyUser", proxyUserId);
		}	
	}
	public void setProxyPassword(String proxyPassword) {
		if(proxyPassword!=null){
			AxisProperties.setProperty("http.proxyPassword", proxyPassword); 
		}
	}


//	public void initProxyProperties(){
//	if(proxyHost!=null)
//	AxisProperties.setProperty("http.proxyHost", proxyHost);
//	if(proxyPort!=null)
//	AxisProperties.setProperty("http.proxyPort", proxyPort);
//	if(proxyUserId!=null)
//	AxisProperties.setProperty("http.proxyUser", proxyUserId);
//	if(proxyPassword!=null)
//	AxisProperties.setProperty("http.proxyPassword", proxyPassword); 
//	}

}
