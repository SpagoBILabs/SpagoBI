/*
*
* @file BoundedInputStream.java
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
* @author ArndHouben
*
* @version $Id: BoundedInputStream.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents a bounded input stream, i.e. the number of bytes
 * which are read from the stream are restricted by constant integer value
 * 
 * @author ArndHouben
 * @version $Id: BoundedInputStream.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class BoundedInputStream extends InputStream {

	private final int maxBytes; //max number of bytes to read
	private int bytesRead;		//bytes already read
	private boolean isClosed;
	private InputStream inStream = null;

	public BoundedInputStream(InputStream inStream, int maxBytes) {
		this.maxBytes = maxBytes;
		this.inStream = inStream;
	}

	public final synchronized int read() throws IOException {
		if (isClosed)
			throw new IOException("InputStream is closed!");

		if (bytesRead >= maxBytes)
			return -1;

		bytesRead++;
		return this.inStream.read();
	}

	public final synchronized int read(byte[] b, int off, int len) throws java.io.IOException {
		if (isClosed)
			throw new IOException("InputStream is closed!");

		if (bytesRead >= maxBytes)
			return -1;

		//check length
		if (bytesRead + len > maxBytes) {
			len = (int) (maxBytes - bytesRead);
		}
		int count = this.inStream.read(b, off, len);
		bytesRead += count;
		return count;
	}

	public final int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public final synchronized void close() throws IOException {
		if (!isClosed) {
			try {
				//we read until end:
				byte bytes[] = new byte[1024];
				while (this.read(bytes) >= 0) {
					;
				}
			} finally {
				isClosed = true;
			}
		}
	}

	public final synchronized long skip(long n) throws IOException {
		long length = Math.min(n, maxBytes - bytesRead);
		length = this.inStream.skip(length);
		if (length > 0) {
			bytesRead += length;
		}
		return length;
	}

}
