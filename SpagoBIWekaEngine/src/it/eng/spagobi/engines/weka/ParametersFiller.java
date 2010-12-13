/**
 *
 *	LICENSE: see COPYING file
 *
**/
package it.eng.spagobi.engines.weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

/**
 * @author Gioia
 *
 */
public class ParametersFiller {
	
	
	public static void fill(File template, File filledTemplate, Map params) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader(template));
		Writer writer = new FileWriter(filledTemplate);
		String line = null;
		while((line = reader.readLine()) != null) {
			int index = -1;
			while( (index = line.indexOf("$P{")) != -1) {
				String pname = line.substring(index + 3, line.indexOf("}"));
				line = line.substring(0, index) + params.get(pname) +
					   line.substring(line.indexOf("}") + 1 , line.length());
			}
			writer.write(line + "\n");
		}
		writer.flush();
		writer.close();
		reader.close();
	}
	
	public static void fill(Reader reader, Writer writer, Map params) throws IOException {
		BufferedReader bufferedReader = new BufferedReader( reader );
		String line = null;
		while((line = bufferedReader.readLine()) != null) {
			int index = -1;
			while( (index = line.indexOf("$P{")) != -1) {
				String pname = line.substring(index + 3, line.indexOf("}"));
				line = line.substring(0, index) + params.get(pname) +
					   line.substring(line.indexOf("}") + 1 , line.length());
			}
			writer.write(line + "\n");
		}
		writer.flush();
		writer.close();
		reader.close();
	}
	
}
