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
package it.eng.spagobi.twitter.analysis.launcher;

import it.eng.spagobi.utilities.assertion.Assert;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TestTwitterAnalysisLauncher {
	// TODO: ricordati di aggiungere il tuo nome negli author nel commento
	// iniziale

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// TODO: è solo una classe di test per provare tutto il giro
		// TODO: istanzio un oggetto TwitterAnalysisLauncher per lanciare una
		// tipologia di ricerca (magari prendendo argomenti dagli args)
		// String html = "<a href=\"http://www.techwars.io\" rel=\"nofollow\">TechWars</a>";
		// System.out.println(html.replaceAll("<.*?>", ""));
		// TwitterSearch twitterSearch = new TwitterSearch();
		// twitterSearch.setLabel("Hibernate4");
		// twitterSearch.setKeywords("spagobi");
		// twitterSearch.setCreationDate(GregorianCalendar.getInstance());
		// twitterSearch.setType(SearchTypeEnum.SEARCHAPI);
		// twitterSearch.setLastActivationTime(GregorianCalendar.getInstance());
		//
		// DaoService dao = new DaoService();
		// TwitterSearch insertedSearch = (TwitterSearch) dao.create(twitterSearch);
		// insertedSearch.toString();

		try {
			Properties bitlyProp = new Properties();

			String bitlyFile = "bitly.properties";

			InputStream inputStream = TestTwitterAnalysisLauncher.class.getClassLoader().getResourceAsStream(bitlyFile);

			bitlyProp.load(inputStream);

			Assert.assertNotNull(bitlyProp, "Impossible to call bitly API without a valid bitly.properties file");

			String accessToken = bitlyProp.getProperty("accessToken");
			System.out.print("ACESSTOKEN: " + accessToken);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
