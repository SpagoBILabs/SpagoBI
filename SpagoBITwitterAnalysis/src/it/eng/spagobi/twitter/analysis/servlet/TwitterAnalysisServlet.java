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
package it.eng.spagobi.twitter.analysis.servlet;

import it.eng.spagobi.twitter.analysis.launcher.TwitterAnalysisLauncher;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterAnalysisServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -8767760518159744239L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// reading the user input
		String searchType = request.getParameter("searchType");
		String searchLabel = request.getParameter("searchLabel");
		String keyword = request.getParameter("keyword");
		String link = request.getParameter("link");
		// TODO gestire i diversi valori che possono essere inseriti da input
		String account = request.getParameter("account");

		// System.out.println("SearchType: " + searchType + "\n SearchLabel: " +
		// searchLabel + "\n Keyword: " + keyword + "\n Link: " + link +
		// "\n Account: " + account);

		// TODO per il momento gli altri input sono inseriti nel codice, ma
		// inseguito dovranno essere quelli inseriti dall'utente
		String languageCode = null;
		String dbType = "MySQL";

		TwitterAnalysisLauncher twitterLauncher = new TwitterAnalysisLauncher(keyword, languageCode, searchType, searchLabel, link, account, dbType);

		twitterLauncher.startSearch();

		RequestDispatcher requetsDispatcherObj = request.getRequestDispatcher("/searchlist.jsp");
		requetsDispatcherObj.forward(request, response);
	}
}
