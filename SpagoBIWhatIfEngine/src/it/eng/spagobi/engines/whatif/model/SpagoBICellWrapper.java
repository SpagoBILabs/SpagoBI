/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */

package it.eng.spagobi.engines.whatif.model;

import it.eng.spagobi.engines.whatif.model.SpagoBICellSetWrapper;
import it.eng.spagobi.engines.whatif.model.transform.CellRelation;
import it.eng.spagobi.utilities.assertion.UnreachableCodeException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.olap4j.AllocationPolicy;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapException;
import org.olap4j.Position;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.Property;

public class SpagoBICellWrapper implements Cell {

	private Cell cell;
	private SpagoBICellSetWrapper cellSetWrapper;
	private Object value;
	
	public SpagoBICellWrapper(Cell cell, SpagoBICellSetWrapper cellSetWrapper) {
		this.cell = cell;
		this.cellSetWrapper = cellSetWrapper;
		this.value = cell.getValue();
	}

	public CellSet getCellSet() {
		return this.cellSetWrapper;
	}

	public int getOrdinal() {
		return cell.getOrdinal();
	}

	public List<Integer> getCoordinateList() {
		return cell.getCoordinateList();
	}

	public Object getPropertyValue(Property property) {
		return cell.getPropertyValue(property);
	}

	public boolean isEmpty() {
		return cell.isEmpty();
	}

	public boolean isError() {
		return cell.isEmpty();
	}

	public boolean isNull() {
		return cell.isNull();
	}

	public double getDoubleValue() throws OlapException {
        Object o = cell.getValue();
        if (o instanceof Number) {
            Number number = (Number) o;
            return number.doubleValue();
        }
        throw new OlapException("not a number");
	}

	public String getErrorText() {
		return cell.getErrorText();
	}

	public Object getValue() {
		return value;
	}

	public String getFormattedValue() {
		// TODO Auto-generated method stub
		return this.getValue().toString();
	}

	public ResultSet drillThrough() throws OlapException {
		return cell.drillThrough();
	}

	public void setValue(Object value, AllocationPolicy allocationPolicy,
			Object... allocationArgs) throws OlapException {
		throw new UnreachableCodeException("You cannot invoke this method, since no AllocationPolicy is implemented");
	}
	
	public void setValue(Object value) {
		this.value = value;
		cellSetWrapper.notifyModifiedCell(this);
	}
	
    public Member[] getMembers () {
    	List<Integer> coordinates = this.getCoordinateList();
    	List<Member> members = new ArrayList<Member>();
    	for (int i = 0; i < coordinates.size(); i++) {
    		Integer aCoordinate = coordinates.get(i);
    		CellSetAxis axis = this.getCellSet().getAxes().get(i);
    		Position position = axis.getPositions().get(aCoordinate);
    		members.addAll(position.getMembers());
    	}
    	Member[] toReturn = new Member[members.size()];
    	toReturn = members.toArray(toReturn);
    	return toReturn;
    }
	
    public CellRelation getRelationTo(Member[] members) {
        int aboveCount = 0;
        int belowCount = 0;
        for (int i = 0; i < members.length; i++) {
            Member thatMember = members[i];
            org.olap4j.metadata.Member thisMember = this.getMembers()[i];
            // FIXME: isChildOrEqualTo is very inefficient. It should use
            // level depth as a guideline, at least.
            if (thatMember.isChildOrEqualTo(thisMember)) {
                if (thatMember.equals(thisMember)) {
                    // thisMember equals member
                } else {
                    // thisMember is ancestor of member
                    ++aboveCount;
                    if (belowCount > 0) {
                        return CellRelation.NONE;
                    }
                }
            } else if (thisMember.isChildOrEqualTo(thatMember)) {
                // thisMember is descendant of member
                ++belowCount;
                if (aboveCount > 0) {
                    return CellRelation.NONE;
                }
            } else {
                return CellRelation.NONE;
            }
        }
        assert aboveCount == 0 || belowCount == 0;
        if (aboveCount > 0) {
            return CellRelation.ABOVE;
        } else if (belowCount > 0) {
            return CellRelation.BELOW;
        } else {
            return CellRelation.EQUAL;
        }
    }
	
    public static SpagoBICellWrapper wrap(Cell cell, SpagoBICellSetWrapper cellSetWrapper) {
    	if (cell instanceof SpagoBICellWrapper) {
    		return (SpagoBICellWrapper) cell;
    	}
    	return new SpagoBICellWrapper(cell, cellSetWrapper);
    }
    
}
