package it.eng.spagobi.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import it.eng.spago.base.Constants;
import it.eng.spago.tracing.TracerSingleton;

public class DefaultCipher {
	private Cipher _cipher = null;
	private SecretKeySpec _skeySpec = null;
	private static final String _charSkey = "a3d480d3197daf43ade4c34cb53d4c2f";
	private static DefaultCipher defaultCipher = null;
	
	public DefaultCipher() {
	    try {
	        byte[] rawSkey = Hex.decodeHex(_charSkey.toCharArray());
	        _skeySpec = new SecretKeySpec(rawSkey, "AES");
	        _cipher = Cipher.getInstance("AES");
	    } // try 
	    catch (Exception e) {
	        TracerSingleton.log(
	            Constants.NOME_MODULO,
	            TracerSingleton.CRITICAL,
	            "DefaultCipher::DefaultCipher:",
	            e);
	    } // catch (Exception e)
	} // public DefaultCipher()
	
	public DefaultCipher(String charSkey) {
	    try {
	    	 byte[] rawSkey = Hex.decodeHex(charSkey.toCharArray());
	        _skeySpec = new SecretKeySpec(rawSkey, "AES");
	        _cipher = Cipher.getInstance("AES");
	    } // try 
	    catch (Exception e) {
	        TracerSingleton.log(
	            Constants.NOME_MODULO,
	            TracerSingleton.CRITICAL,
	            "DefaultCipher::DefaultCipher:",
	            e);
	    } // catch (Exception e)
	} // public DefaultCipher()
	
	public String encrypt(String charToEncrypt) {
	    String encrypted = null;
	    try {
	        _cipher.init(Cipher.ENCRYPT_MODE, _skeySpec);
	        byte[] rowToEncrypt = charToEncrypt.getBytes("utf-8");
	        byte[] rowEncrypted = _cipher.doFinal(rowToEncrypt);
	        char[] charEncrypted = Hex.encodeHex(rowEncrypted);
	        encrypted = new String(charEncrypted);
	    } // try 
	    catch (Exception e) {
	        TracerSingleton.log(
	            Constants.NOME_MODULO,
	            TracerSingleton.CRITICAL,
	            "DefaultCipher::encrypt:",
	            e);
	    } // catch (Exception e)
	    return encrypted;
	} // public String encrypt(String toEncrypt)
	
	public String decrypt(String charToDecrypt) {
	    String decrypted = null;
	    try {
	        _cipher.init(Cipher.DECRYPT_MODE, _skeySpec);
	        char[] charEncrypted = charToDecrypt.toCharArray();
	        byte[] rowToDecrypt = Hex.decodeHex(charEncrypted);
	        byte[] rowDecrypted = _cipher.doFinal(rowToDecrypt);
	        decrypted = new String(rowDecrypted, "utf-8");
	    } // try 
	    catch (Exception e) {
	        TracerSingleton.log(
	            Constants.NOME_MODULO,
	            TracerSingleton.CRITICAL,
	            "DefaultCipher::decrypt:",
	            e);
	    } // catch (Exception e)
	    return decrypted;
	} // public String decrypt(String toDecrypt)
} // public class DefaultCipher implements IFaceCipher
