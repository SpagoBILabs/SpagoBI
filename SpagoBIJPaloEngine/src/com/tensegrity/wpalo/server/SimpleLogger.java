/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server;

public class SimpleLogger {
	private final int level;
	
	public SimpleLogger(int level) {
		if (level < 1 || level > 4) {
			level = 5;
		}
		this.level = level;
	}
	
	public void comment(String msg) {
		if (level > 1) {
			return;
		}
		System.out.println(msg);
	}
	
	public void debug(String msg) {
		if (level > 2) {
			return;
		}
		System.out.println(msg);
	}
	
	public void warn(String msg) {
		if (level > 3) {
			return;
		}
		System.out.println("WARN: " + msg);
	}
	
	public void error(String msg, Throwable t) {
		if (level > 4) {
			return;
		}
		System.err.println("ERROR: " + msg);
		if (t != null) {
			t.printStackTrace();
		}
	}
	
	public static void profile(String method, long ... times) {
		System.err.print(method + " times: [");
		for (int i = 0, n = times.length; i < (n - 1); i++) {
			System.err.print((times[i + 1] - times[i]));
			if (i < (n - 2)) {
				System.err.print(", ");
			}
		}
		System.err.println("]");
	}
}
