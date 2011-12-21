/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPIVOT.LICENSE.txt file
 * 
 */
package it.eng.spagobi.jpivotaddins.engines.jpivot.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import sun.misc.BASE64Decoder;

public class SecurityUtilities {

	private transient Logger logger = null;
	
	public SecurityUtilities(Logger log) {
		logger = log;
	}
	
	/**
	 * Get the SpagoBI Public Key for a DSA alghoritm
	 * @return Public Key for SpagoBI (DSA alghoritm)
	 */
	public PublicKey getPublicKey() {
		PublicKey pubKey = null;
		SAXReader reader = new SAXReader();
		Document document = null;
		try{
			document = reader.read(getClass().getResourceAsStream("/security-config.xml"));
			Node publicKeyNode = document.selectSingleNode( "//SECURITY-CONFIGURATION/KEYS/SPAGOBI_PUBLIC_KEY_DSA");
			String namePubKey =  publicKeyNode.valueOf("@keyname");
		    InputStream publicKeyIs = this.getClass().getClassLoader().getResourceAsStream(namePubKey);	    
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024];
		    int len;
		    while ((len = publicKeyIs.read(buffer)) >= 0)
		    	baos.write(buffer, 0, len);
		    publicKeyIs.close();
		    baos.close();
		    byte[] pubKeyByte = baos.toByteArray();
		    // get the public key from bytes  
		    KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pubKeyByte);
		    pubKey = keyFactory.generatePublic(publicKeySpec);
		}catch(DocumentException de){
			logger.error("Engines"+ this.getClass().getName()+ "getPublicKey:"+
						 "Error during parsing of the security configuration file", de);
		} catch (IOException e) {
			logger.error("Engines"+ this.getClass().getName()+ "getPublicKey:"+
					 "Error retriving the key file", e);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Engines"+ this.getClass().getName()+ "getPublicKey:"+
					 "DSA Alghoritm not avaiable", e);
		} catch (InvalidKeySpecException e) {
			logger.error("Engines"+ this.getClass().getName()+ "getPublicKey:"+
					 "Invalid Key", e);
		}
		return pubKey;
	}
	
	
	/**
	 * Decode a Base64 String into a byte array
	 * @param encoded String encoded with Base64 algorithm
	 * @return byte array decoded
	 */
	public byte[] decodeBase64(String encoded) {
		byte[] clear = null;
		try{
			BASE64Decoder decoder = new BASE64Decoder();
			clear = decoder.decodeBuffer(encoded);
			return clear;
		} catch (IOException ioe) {
			logger.error("Engines"+ this.getClass().getName()+ "getPublicKey:"+
					     "Error during base64 decoding", ioe);
		}
		return clear;
	}
	
	
	/**
	 * Verify the signature 
	 * @param tokenclear Clear data
	 * @param tokensign Signed data
	 * @return
	 */
	public boolean verifySignature(byte[] tokenclear, byte[] tokensign, PublicKey publicKeyDSASbi) {
		try {
			Signature sign = Signature.getInstance("DSA");
			sign.initVerify(publicKeyDSASbi);
			sign.update(tokenclear);
			return sign.verify(tokensign);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Engines"+ this.getClass().getName()+ "verifySignature:"+
				     "DSA Algorithm not avaiable", e);
			return false;
		} catch (InvalidKeyException e) {
			logger.error("Engines"+ this.getClass().getName()+ "verifySignature:"+
				     "Invalid Key", e);
			return false;
		} catch (SignatureException e) {
			logger.error("Engines"+ this.getClass().getName()+ "verifySignature:"+
				     "Error while verifing the exception", e);
			return false;
		} 
	}
	
	
	/**
	 * Authenticate the caller (must be SpagoBI)
	 * @param request HttpRequest
	 * @param response HttpResponse
	 * @return boolean, true if autheticated false otherwise
	 */
	public boolean authenticate(String token, String tokenclear, PublicKey publicKey) {
		if(token==null) {
			logger.error("Engines"+ this.getClass().getName()+ "authenticate:"+
		                 "Token null");
		    return false;
		}
		if(tokenclear==null) {
			logger.error("Engines"+ this.getClass().getName()+ "authenticate:"+
		                 "Token clear null");
		    return false;
		}
		byte[] tokenClear = tokenclear.getBytes();
		String tokenSign64 = token;
		byte[] tokenSign = decodeBase64(tokenSign64); 
		if(tokenSign==null) {
			logger.error("Engines"+ this.getClass().getName()+ "authenticate:"+
		                 "Token null after base 64 decoding");
		    return false;
		}
		// verify the signature
		boolean sign = verifySignature(tokenClear, tokenSign, publicKey);
		return sign;
	}
	
	/**
	 * Decodes (using byte64 decoding function) all the value contained into the input map
	 * @param parMap Map containing value to be decoded
	 * @return Map with value decoded
	 */
	public Map decodeParameterMap(Map parMap){
		Map decMap = new HashMap();
		Set keys = parMap.keySet();
		Iterator iterKeys = keys.iterator();
		while(iterKeys.hasNext()) {
			String key = (String)iterKeys.next();
			Object[] valueEncArr = (Object[])parMap.get(key);
			String valueEnc = valueEncArr[0].toString();
			byte[] valueDecBytes =  decodeBase64(valueEnc);
			String valueDec = new String(valueDecBytes);
			decMap.put(key, valueDec);
		}
		return decMap;
	}
	
}
