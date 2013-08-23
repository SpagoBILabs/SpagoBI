/*
*
* @file XConverter.java
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
* @version $Id: XConverter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter;

import java.util.ArrayList;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.XPaloObject;
import com.tensegrity.palo.gwt.core.server.converter.admin.AccountConverter;
import com.tensegrity.palo.gwt.core.server.converter.admin.ConnectionConverter;
import com.tensegrity.palo.gwt.core.server.converter.admin.GroupConverter;
import com.tensegrity.palo.gwt.core.server.converter.admin.RoleConverter;
import com.tensegrity.palo.gwt.core.server.converter.admin.UserConverter;
import com.tensegrity.palo.gwt.core.server.converter.cubeviews.ViewConverter;
import com.tensegrity.palo.gwt.core.server.converter.palo.CubeConverter;
import com.tensegrity.palo.gwt.core.server.converter.palo.DatabaseConverter;
import com.tensegrity.palo.gwt.core.server.converter.palo.DimensionConverter;
import com.tensegrity.palo.gwt.core.server.converter.palo.ElementConverter;
import com.tensegrity.palo.gwt.core.server.converter.palo.ElementNodeConverter;
import com.tensegrity.palo.gwt.core.server.converter.palo.HierarchyConverter;

/**
 * <code>XConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XConverter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class XConverter {

	private static final List<BaseConverter> allConverters = new ArrayList<BaseConverter>();
	static {
		//admin:
		register(new AccountConverter());
		register(new ConnectionConverter());
		register(new GroupConverter());
		register(new RoleConverter());
		register(new UserConverter());
//		register(new AuthUserConverter());
		
		//cubeview:
//		register(new AxisHierarchyConverter());
		register(new ViewConverter());
		//folder:
		
		//palo:
		register(new DatabaseConverter());
		register(new CubeConverter());
		register(new DimensionConverter());
		register(new HierarchyConverter());
		register(new ElementConverter());
		register(new ElementNodeConverter());
		//subsets:
	}
	
//	public static final Object getNative(XObject xObj, AuthUser loggedInUser)
//			throws OperationFailedException {
//		Converter converter = getToNativeConverter(xObj.getClass());
//		if (converter != null)
//			return converter.toNative(xObj, loggedInUser);
//		return null;
//	}
	
	public static final XObject createX(Object nativeObj) {
		Converter converter = getToXConverter(nativeObj.getClass());
		if(converter != null)
			return converter.toXObject(nativeObj);
		return null;
	}

	public static final XPaloObject createX(Object nativeObj, String accountId) {
		Converter converter = getToXConverter(nativeObj.getClass());
		if(converter != null && converter instanceof PaloObjectConverter)
			return ((PaloObjectConverter)converter).toXObject(nativeObj, accountId);
		return null;
	}

	private static final void register(BaseConverter converter) {
//		xObject2Native.put(converter.getXObjectClass(), converter.getNativeClass());
//		allConverters.put(converter.getNativeClass(), converter);
		allConverters.add(converter);
	}
	
	private static final Converter getToXConverter(Class<?> clazz) {
		for(BaseConverter converter : allConverters) {
			if(converter.getNativeClass().isAssignableFrom(clazz))
				return converter;
		}
		return null;
	}
	private static final Converter getToNativeConverter(Class<?> clazz) {
		for(BaseConverter converter : allConverters) {
			if(converter.getXObjectClass().isAssignableFrom(clazz))
				return converter;
		}
		return null;
	}

}
