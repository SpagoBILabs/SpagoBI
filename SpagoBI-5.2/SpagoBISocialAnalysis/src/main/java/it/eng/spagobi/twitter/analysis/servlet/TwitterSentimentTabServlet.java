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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterSentimentTabServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 7583471366754241389L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// reading the user input
		// String searchID = request.getParameter("searchID");
		//
		// System.out.println("SearchID: " + searchID);
		//
		// BitlyCounterClicksUtility bitUtil = new BitlyCounterClicksUtility();
		// bitUtil.getLinkToMonitor(searchID);
		// bitUtil.getLinkToMonitorCategory(searchID);
		//
		// TwitterUserInfoUtility userInfoUtil = new TwitterUserInfoUtility();
		// userInfoUtil.getFollowersAccountsToMonitor(searchID);
		//

		RequestDispatcher requetsDispatcherObj = request.getRequestDispatcher("/WEB-INF/jsp/tabs/sentiment.jsp");
		requetsDispatcherObj.forward(request, response);
	}
}
