package it.eng.spagobi.utilities;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class HelperForTest {

	private HelperForTest() {
	}

	public static String getJsonTest(String fileName, Class<?> clazz) throws IOException {
		InputStream in = clazz.getResourceAsStream(fileName);
		return IOUtils.toString(in, "UTF-8");
	}

	public static boolean all(boolean[] done) {
		for (int i = 1; i < done.length; i++) {
			if (done[i] != done[i - 1]) {
				return false;
			}
		}
		return true;
	}

}
