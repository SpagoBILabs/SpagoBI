/* SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This program is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation, either version 2.1 
 * of the License, or (at your option) any later version. This program is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU  General Public License for more details. You should have received a copy of the GNU  General Public License along with 
 * this program. If not, see: http://www.gnu.org/licenses/. */
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
