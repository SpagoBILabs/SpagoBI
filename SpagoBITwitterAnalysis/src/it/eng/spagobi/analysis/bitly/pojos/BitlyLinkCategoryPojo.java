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
public class BitlyLinkCategoryPojo {

	private long link_id;
	private String type;
	private String category;
	private int clicks_count;
	private String link;

	public BitlyLinkCategoryPojo(long li, String t, String c, int cc) {
		this.link_id = li;
		this.type = t;
		this.category = c;
		this.clicks_count = cc;
	}

	public BitlyLinkCategoryPojo(String t, String c, int cc, String link) {
		this.type = t;
		this.category = c;
		this.clicks_count = cc;
		this.link = link;
	}

	public long getLink_id() {
		return link_id;
	}

	public void setLink_id(long link_id) {
		this.link_id = link_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getClicks_count() {
		return clicks_count;
	}

	public void setClicks_count(int clicks_count) {
		this.clicks_count = clicks_count;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "BitlyLinkCategoryPojo [link_id=" + link_id + ", type=" + type + ", category=" + category + ", clicks_count=" + clicks_count + ", link=" + link + "]";
	}

}
