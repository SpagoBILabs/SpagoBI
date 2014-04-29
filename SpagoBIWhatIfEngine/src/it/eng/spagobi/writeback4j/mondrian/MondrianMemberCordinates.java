
/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.writeback4j.mondrian;

import it.eng.spagobi.writeback4j.IMemberCordinates;
import it.eng.spagobi.writeback4j.sql.EquiJoin;
import it.eng.spagobi.writeback4j.sql.TableEntry;

import java.util.Map;

import mondrian.olap.MondrianDef;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.Hierarchy;

import org.eigenbase.xom.NodeDef;
import org.olap4j.metadata.Member;

/**
 * @author ghedin
 *
 */
public class MondrianMemberCordinates implements IMemberCordinates{
	
	MondrianDef.CubeDimension dimension;
	MondrianDef.Hierarchy hieararchy;
	Map<TableEntry, Member> level2Member;
	
	public MondrianMemberCordinates(CubeDimension dimension, Hierarchy hieararchy,Map<TableEntry, Member> level2Member) {
		super();
		this.dimension = dimension;
		this.hieararchy = hieararchy;
		this.level2Member = level2Member;
	}
	public MondrianDef.CubeDimension getDimension() {
		return dimension;
	}
	public void setDimension(MondrianDef.CubeDimension dimension) {
		this.dimension = dimension;
	}
	public MondrianDef.Hierarchy getHieararchy() {
		return hieararchy;
	}
	public void setHieararchy(MondrianDef.Hierarchy hieararchy) {
		this.hieararchy = hieararchy;
	}
	public Map<TableEntry, Member> getLevel2Member() {
		return level2Member;
	}
	public void setLevel2Member(Map<TableEntry, Member> level2Member) {
		this.level2Member = level2Member;
	}
	public boolean isAllMember(){
		return level2Member.size()==0;
	}
	
	public String getTableName(){
		String tableName = getHieararchy().primaryKeyTable;
		if(tableName==null){
			NodeDef[] children = getHieararchy().getChildren();
			for(int i=0; i<children.length; i++){
				NodeDef node = children[i];
				if(node instanceof MondrianDef.Table){
					tableName = ((MondrianDef.Table)node).name;
					break;
				}
			}
		}
		return tableName;
	}
	
	public String getPrimaryKey(){
		return getHieararchy().primaryKey;
	}
	
	public String getForeignKey(){
		return getDimension().foreignKey;
	}
	
	public EquiJoin getInnerDimensionJoinConditions(){
		MondrianDef.RelationOrJoin relOrJoin = getHieararchy().relation;
		if(relOrJoin instanceof MondrianDef.Join){
			MondrianDef.Join join = (MondrianDef.Join) relOrJoin;
			MondrianDef.Table leftT = (MondrianDef.Table) join.left;
			MondrianDef.Table rightT = (MondrianDef.Table) join.right;
			TableEntry leftTable = new TableEntry(join.leftKey, leftT.name);
			TableEntry rightTable = new TableEntry(join.rightKey, rightT.name);
			return new EquiJoin(leftTable, rightTable);
		}
		return null;
	}
}