/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
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
