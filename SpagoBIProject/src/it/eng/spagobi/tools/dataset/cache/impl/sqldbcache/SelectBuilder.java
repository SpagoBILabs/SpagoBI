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
package it.eng.spagobi.tools.dataset.cache.impl.sqldbcache;


import java.util.ArrayList;
import java.util.List;

/**
 * @author John Krasnay - original code http://john.krasnay.ca/2010/02/15/building-sql-in-java.html
 *	Licensed under the Apache License, Version 2.0	http://www.apache.org/licenses/
 *
 */

public class SelectBuilder {

    private List<String> columns = new ArrayList<String>();

    private List<String> tables = new ArrayList<String>();

    private List<String> joins = new ArrayList<String>();

    private List<String> leftJoins = new ArrayList<String>();

    private List<String> wheres = new ArrayList<String>();

    private List<String> orderBys = new ArrayList<String>();

    private List<String> groupBys = new ArrayList<String>();

    private List<String> havings = new ArrayList<String>();

    public SelectBuilder() {

    }

    public SelectBuilder(String table) {
        tables.add(table);
    }

    private void appendList(StringBuilder sql, List<String> list, String init,
    		String sep) {
    	boolean first = true;
    	for (String s : list) {
    		if (first) {
    			sql.append(init);
    		} else {
    			sql.append(sep);
    		}
    		sql.append(s);
    		first = false;
    	}
    }

    public SelectBuilder column(String name) {
        columns.add(name);
        return this;
    }

    public SelectBuilder column(String name, boolean groupBy) {
        columns.add(name);
        if (groupBy) {
            groupBys.add(name);
        }
        return this;
    }

    public SelectBuilder from(String table) {
        tables.add(table);
        return this;
    }

    public SelectBuilder groupBy(String expr) {
        groupBys.add(expr);
        return this;
    }

    public SelectBuilder having(String expr) {
        havings.add(expr);
        return this;
    }

    public SelectBuilder join(String join) {
        joins.add(join);
        return this;
    }

    public SelectBuilder leftJoin(String join) {
        leftJoins.add(join);
        return this;
    }

    public SelectBuilder orderBy(String name) {
        orderBys.add(name);
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sql = new StringBuilder("select ");

        if (columns.size() == 0) {
            sql.append("*");
        } else {
            appendList(sql, columns, "", ", ");
        }

        appendList(sql, tables, " from ", ", ");
        appendList(sql, joins, " join ", " join ");
        appendList(sql, leftJoins, " left join ", " left join ");
        appendList(sql, wheres, " where ", " and ");
        appendList(sql, groupBys, " group by ", ", ");
        appendList(sql, havings, " having ", " and ");
        appendList(sql, orderBys, " order by ", ", ");

        return sql.toString();
    }

    public SelectBuilder where(String expr) {
        wheres.add(expr);
        return this;
    }
}
