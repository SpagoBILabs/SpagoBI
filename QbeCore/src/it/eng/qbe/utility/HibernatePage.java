/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.utility;


import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;

// TODO: Auto-generated Javadoc
/**
 * This class is taken by Hibernate wiki.
 * 
 * This class provides pagination for displaying results from a large result set
 * over a number of pages (i.e. with a given number of results per page).
 * 
 * Taken from http://blog.hibernate.org/cgi-bin/blosxom.cgi/2004/08/14#fn.html.
 * 
 * @author Gavin King
 * @author Eric Broyles
 */
public class HibernatePage {

	/** The results. */
	private List results;

	/** The page size. */
	private int pageSize;

	/** The page. */
	private int page;

	/** The scrollable results. */
	private ScrollableResults scrollableResults;

	/** The total results. */
	private int totalResults = 0;

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(HibernatePage.class);
	
	
	
	/**
	 * Construct a new Page. Page numbers are zero-based, so the first page is
	 * page 0.
	 * 
	 * @param query the Hibernate Query
	 * @param page the page number (zero-based)
	 * @param pageSize the number of results to display on the page
	 * 
	 * @throws HibernateException the hibernate exception
	 */

	public HibernatePage(Query query, int page, int pageSize) throws HibernateException {
		this.page = page;
		this.pageSize = pageSize;
		try {
			scrollableResults = query.scroll();
			/*
			 * We set the max results to one more than the specfied pageSize to
			 * determine if any more results exist (i.e. if there is a next page
			 * to display). The result set is trimmed down to just the pageSize
			 * before being displayed later (in getList()).
			 */
			results = query.setFirstResult(page * pageSize).setMaxResults(
					pageSize + 1).list();
		} catch (HibernateException e) {
            e.printStackTrace();
            logger.error("Failed to get paginated results: " + e.getMessage());
            throw e;
		}

	}

	/**
	 * Checks if is first page.
	 * 
	 * @return true, if is first page
	 */
	public boolean isFirstPage() {
		return page == 0;
	}

	/**
	 * Checks if is last page.
	 * 
	 * @return true, if is last page
	 */
	public boolean isLastPage() {
		return page >= getLastPageNumber();
	}

	/**
	 * Checks for next page.
	 * 
	 * @return true, if successful
	 */
	public boolean hasNextPage() {
		return results.size() > pageSize;
	}

	/**
	 * Checks for previous page.
	 * 
	 * @return true, if successful
	 */
	public boolean hasPreviousPage() {
		return page > 0;
	}

	/**
	 * Gets the last page number.
	 * 
	 * @return the last page number
	 */
	public int getLastPageNumber() {
		/*
		 * We use the Math.floor() method because page numbers are zero-based
		 * (i.e. the first page is page 0).
		 */
		double totalResults = new Integer(getTotalResults()).doubleValue();
		return new Double(Math.floor(totalResults / pageSize)).intValue();
	}

	/**
	 * Gets the list.
	 * 
	 * @return the list
	 */
	public List getList() {
		/*
		 * Since we retrieved one more than the specified pageSize when the
		 * class was constructed, we now trim it down to the pageSize if a next
		 * page exists.
		 */
		return hasNextPage() ? results.subList(0, pageSize) : results;
	}

	
	/**
	 * Gets the total results.
	 * 
	 * @return the total results
	 */
	public int getTotalResults() {
		try {
			getScrollableResults().last();
			totalResults = getScrollableResults().getRowNumber();
		} catch (HibernateException e) {
			logger.error( "Failed to get last row number from scollable results: "
						  + e.getMessage());
		}
		return totalResults;
	}

	/**
	 * Gets the first result number.
	 * 
	 * @return the first result number
	 */
	public int getFirstResultNumber() {
		return page * pageSize + 1;
	}

	/**
	 * Gets the last result number.
	 * 
	 * @return the last result number
	 */
	public int getLastResultNumber() {
		int fullPage = getFirstResultNumber() + pageSize - 1;
		return getTotalResults() < fullPage ? getTotalResults() : fullPage;
	}

	/**
	 * Gets the next page number.
	 * 
	 * @return the next page number
	 */
	public int getNextPageNumber() {
		return page + 1;
	}

	/**
	 * Gets the previous page number.
	 * 
	 * @return the previous page number
	 */
	public int getPreviousPageNumber() {
		return page - 1;
	}

	/**
	 * Gets the scrollable results.
	 * 
	 * @return the scrollable results
	 */
	protected ScrollableResults getScrollableResults() {
		return scrollableResults;
	}

}

