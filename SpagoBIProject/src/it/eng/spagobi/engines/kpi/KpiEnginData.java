/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.kpi;

import it.eng.spago.security.IEngUserProfile;

import java.util.Locale;

public class KpiEnginData {
	public KpiEnginData() {
		super();
		// TODO Auto-generated constructor stub
	}

	private Locale locale;
	private IEngUserProfile profile;
	private String internationalizedFormat;
	private String formatServer;
	private String lang;
	private String country;
	private boolean executionModalityScheduler;

	public KpiEnginData(Locale locale,
			IEngUserProfile profile, String internationalizedFormat,
			String formatServer, String lang, String country,
			boolean executionModalityScheduler) {
		this.locale = locale;
		this.profile = profile;
		this.internationalizedFormat = internationalizedFormat;
		this.formatServer = formatServer;
		this.lang = lang;
		this.country = country;
		this.executionModalityScheduler = executionModalityScheduler;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public IEngUserProfile getProfile() {
		return profile;
	}

	public void setProfile(IEngUserProfile profile) {
		this.profile = profile;
	}

	public String getInternationalizedFormat() {
		return internationalizedFormat;
	}

	public void setInternationalizedFormat(String internationalizedFormat) {
		this.internationalizedFormat = internationalizedFormat;
	}

	public String getFormatServer() {
		return formatServer;
	}

	public void setFormatServer(String formatServer) {
		this.formatServer = formatServer;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isExecutionModalityScheduler() {
		return executionModalityScheduler;
	}

	public void setExecutionModalityScheduler(
			boolean executionModalityScheduler) {
		this.executionModalityScheduler = executionModalityScheduler;
	}
}
