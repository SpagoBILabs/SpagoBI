/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.analysis.bitly.pojos;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class BitlyLinkPojo {

	private String link;
	private int counter_clicks;

	public BitlyLinkPojo(String l, int cc) {
		this.link = l;
		this.counter_clicks = cc;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getCounter_clicks() {
		return counter_clicks;
	}

	public void setCounter_clicks(int counter_clicks) {
		this.counter_clicks = counter_clicks;
	}

	@Override
	public String toString() {
		return "BitlyLinkPojo [link=" + link + ", counter_clicks=" + counter_clicks + "]";
	}

}
