/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.weka;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class TextEncodingTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		String testMsg = "riga1\n\nriga5\nrig8";
		
		byte[] b = testMsg.getBytes("US-ASCII");
		for(int i = 0; i < b.length; i++) {
			System.out.println(b[i] + " = " + testMsg.charAt(i));
		}
		
		System.out.println("===");
		
		testMsg = testMsg.replaceAll("\n", "\r\n");
		
		b = testMsg.getBytes("US-ASCII");
		for(int i = 0; i < b.length; i++) {
			System.out.println(b[i] + " = " + testMsg.charAt(i));
		}
		
		FileOutputStream outputStream = new FileOutputStream("C:\\ProgramFiles\\apache-tomcat-6.0.18\\resources\\weka\\outputfiles\\test.txt");
		outputStream.write(testMsg.getBytes("US-ASCII"));
		//outputStream.write(testMsg.getBytes());
		outputStream.flush();
		outputStream.close();
	}

}
