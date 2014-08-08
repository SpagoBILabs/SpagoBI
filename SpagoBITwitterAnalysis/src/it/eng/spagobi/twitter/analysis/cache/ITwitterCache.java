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
package it.eng.spagobi.twitter.analysis.cache;

import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkCategoryPojo;
import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkPojo;
import it.eng.spagobi.twitter.analysis.pojos.TwitterAccountToMonitorPojo;

import java.sql.Connection;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import twitter4j.Status;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public interface ITwitterCache {

	// TODO: interfaccia per metodi da implementare su tutte le tipologie di
	// Cache sui diversi db
	public Connection openConnection();

	public void closeConnection();

	public long insertTwitterSearch(String keywords, String searchType, String searchLabel);

	public void saveTweet(Status tweet, String keyword, long searchID) throws Exception;

	public void insertBitlyAnalysis(BitlyLinkPojo linkPojo, List<BitlyLinkCategoryPojo> linkCategoryPojos, long searchID);

	public void insertAccountToMonitor(TwitterAccountToMonitorPojo accountToMonitor);

	public CachedRowSet runQuery(String sqlQuery);

	// TODO: se possibile lo schema da salvare lo renderei gestibile da un'altra
	// classe (magari Abstract) in modo tale da non ripetere
	// le estrazioni delle informazioni dal singolo tweet su ogni
	// implementazione diversa della cache
	// sulle singole implementazioni della cache dovrebbe cambiare la sintassi
	// della INSERT a seconda del tipo di db

}
