/**
 * 
 */
package it.eng.spagobi.writeback4j.mondrian.schema;

import java.util.Map;

import mondrian.olap.MondrianDef;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.Hierarchy;

import org.olap4j.metadata.Member;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class MondrianMemberCordinates {
	
	MondrianDef.CubeDimension dimension;
	MondrianDef.Hierarchy hieararchy;
	Map<MondrianDef.Level, Member> level2Member;
	
	public MondrianMemberCordinates(CubeDimension dimension, Hierarchy hieararchy,Map<mondrian.olap.MondrianDef.Level, Member> level2Member) {
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
	public Map<MondrianDef.Level, Member> getLevel2Member() {
		return level2Member;
	}
	public void setLevel2Member(Map<MondrianDef.Level, Member> level2Member) {
		this.level2Member = level2Member;
	}
	public boolean isAllMember(){
		return level2Member.size()==0;
	}
	
	
}