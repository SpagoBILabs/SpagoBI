package it.eng.spagobabel.scanner;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class AbstractScanner implements IScanner {
	
	public Set scan(File file) {
		Set results;
		Reader reader;
		
		// assert file not null
		// file exist
		// assert file is not a directory
		
		reader = null;
		
		// try all
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		results = scan(reader);
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// catch all
		
		// assert results not null
		
		return results;
	}
	
	public Set scan(Reader r) {
		Set results, lineResults;
		BufferedReader reader;
		String line;
		// try all
		results = new HashSet();
		reader = new BufferedReader(r);
		try {
			while( (line = reader.readLine()) != null) {
				lineResults = scan(line);
				results.addAll(lineResults);
			}
		} catch (IOException e)  {
			e.printStackTrace();
		}
		// catch all
		
		// assert results not null
		
		return results;
	}
}
