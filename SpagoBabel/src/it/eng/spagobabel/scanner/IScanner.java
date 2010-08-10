package it.eng.spagobabel.scanner;


import java.io.File;
import java.io.Reader;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IScanner {
	Set scan(File file);
	Set scan(Reader r);
	Set scan(String s);
}
