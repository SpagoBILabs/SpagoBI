/*
*
* @file Account.java
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
* @version $Id: Account.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;



/**
 * This interface describes an account on a palo server for a certain {@link User}.
 * Usually a user can have more then one account.
 *
 * @version $Id: Account.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface Account extends DomainObject {

	/**
	 * Returns the login name.
	 * @return the login name
	 */
	public String getLoginName();
	
	/**
	 * Returns the password to use for login
	 * @return the password
	 */
	public String getPassword();
	/**
	 * Returns the palo connection to log in to.
	 * @return the palo connection to log in to
	 */
	public PaloConnection getConnection();	
	/**
	 * Returns the {@link AuthUser} to which this account is assigned to.
	 * @return the account owner
	 */
	public AuthUser getUser();	
		
	/**
	 * Logout from an established palo connection.
	 */
	public void logout();
	/** 
	 * Checks if this account currently has an established palo connection. 
	 * @return <code>true</code> if the user of this account is currently
	 * logged in, <code>false</code> otherwise 
	 */
	public boolean isLoggedIn();	
}
