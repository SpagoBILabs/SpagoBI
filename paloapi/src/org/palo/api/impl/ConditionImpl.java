/*
*
* @file ConditionImpl.java
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
* @version $Id: ConditionImpl.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl;

import java.util.ArrayList;

import org.palo.api.Condition;

import com.tensegrity.palojava.PaloException;

/**
 * <code>ConditionImpl</code>
 * Implementation of the {@link Condition} interface
 * @author ArndHouben
 * @version $Id: ConditionImpl.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
class ConditionImpl implements Condition {

	private static final ArrayList<String> ALLOWED_CONDITIONS = 
		new ArrayList<String>(6);
	static {
			ALLOWED_CONDITIONS.add(Condition.EQ);
			ALLOWED_CONDITIONS.add(Condition.GT);
			ALLOWED_CONDITIONS.add(Condition.GTE);
			ALLOWED_CONDITIONS.add(Condition.LT);
			ALLOWED_CONDITIONS.add(Condition.LTE);
			ALLOWED_CONDITIONS.add(Condition.NEQ); 
	};
	
	public static final Condition getCondition(String condition) {
		if(isValid(condition))
			return new ConditionImpl(condition);
		else
			throw new PaloException("Unkown condition: \""+condition+"\"");
	}

	private String value;
	private final String condition;
	
	private ConditionImpl(String condition) {
		this.condition = condition;
	}
	
	public final synchronized String getValue() {
		return value;
	}

	public final synchronized void setValue(double value) {
		setValue(Double.toString(value));
	}

	public final void setValue(String value) {
		this.value = value;
	}
	
	public final String toString() {
		StringBuffer str = new StringBuffer();
		str.append(condition);
		str.append(value);
		return str.toString();
	}

	private static final boolean isValid(String condition) {
		return ALLOWED_CONDITIONS.contains(condition);
	}
}
