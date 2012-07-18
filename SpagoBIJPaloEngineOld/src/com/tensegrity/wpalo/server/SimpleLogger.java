/*
*
* @file SimpleLogger.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: SimpleLogger.java,v 1.3 2010/01/13 08:02:41 PhilippBouillon Exp $
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
