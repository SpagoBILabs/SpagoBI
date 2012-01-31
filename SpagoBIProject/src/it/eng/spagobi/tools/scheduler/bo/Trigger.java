/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.scheduler.bo;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class Trigger {
	String name;
	String groupName;
	String description;
	
	boolean runImmediately;
	
	String calendarName;
	Calendar startCalendar;
	Date startTime;
	Calendar endCalendar;
	Date endTime;
	
	String chronExpression;
	String chronString;
	
	Job job;
	
	public Trigger() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getChronExpression() {
		if(chronExpression == null && chronString != null) {
			chronExpression = parseChronString();
		}
		
		return this.chronExpression;
	}

	public void setChronExpression(String chronExpression) {
		this.chronExpression = chronExpression;
		this.chronString = null;
	}

	public String getChronString() {
		return chronString;
	}

	public void setChronString(String chronString) {
		this.chronString = chronString;
		this.chronExpression = null;
	}
	
	private String parseChronString() {
		String chronExpression = null;
		try{
			startCalendar.setTime(startTime);
			int day = startCalendar.get(Calendar.DAY_OF_MONTH);
			int month = startCalendar.get(Calendar.MONTH);
			int year = startCalendar.get(Calendar.YEAR);
			int hour = startCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = startCalendar.get(Calendar.MINUTE);
			String type = "";
	    	String params = "";
	    	if(chronString.indexOf("{")!=-1) {
	    		int indFirstBra = chronString.indexOf("{");
	    		type = chronString.substring(0, indFirstBra);
	    		params = chronString.substring((indFirstBra+1), (chronString.length()-1));
	    	} else {
	    		return chronExpression;
	    	}
	    	if(type.equals("single")) {
	    		return chronExpression; // this will be a normal trigger
	    	}
	    	if(type.equals("minute")) {
	    		int indeq = params.indexOf("=");
	    		String numrep = params.substring(indeq+1);
	    		chronExpression = "0 0/"+numrep+" * * * ? *";
	    	}
	    	if(type.equals("hour")) {
	    		int indeq = params.indexOf("=");
	    		String numrep = params.substring(indeq+1);
	    		chronExpression = "0 "+minute+" 0/"+numrep+" * * ? *";
	    	}
	    	if(type.equals("day")) {
	    		int indeq = params.indexOf("=");
	    		String numrep = params.substring(indeq+1);
	    		chronExpression = "0 "+minute+" "+hour+" 1/"+numrep+" * ? *";
	    	}
	    	if(type.equals("week")) {
	    		int indeq = params.indexOf("=");
	    		int indsplit = params.indexOf(";");
	    		int ind2eq = params.indexOf("=", (indeq + 1));
	    		String numrep = params.substring((indeq+1), indsplit);
	    		Integer numrepInt = new Integer(numrep);
	    		String daysstr = params.substring(ind2eq+1);
	    		if( (daysstr==null) || (daysstr.trim().equals(""))) daysstr = "MON";
	    		if(daysstr.endsWith(",")) daysstr = daysstr.substring(0, (daysstr.length() - 1));
	    		chronExpression = "0 "+minute+" "+hour+" ? * "+daysstr+"/"+numrep+" *";
	    	}
	    	if(type.equals("month")) {
	    		String numRep = "";
	    		String selmonths = "";
	    		String dayRep = "";
	    		String weeks = "";
	    		String days = "";
	    		String[] parchuncks = params.split(";");
	    		for(int i=0; i<parchuncks.length; i++) {
	    			String parchunk = parchuncks[i];
	    			String[] singleparchunks = parchunk.split("=");
	    			String key = singleparchunks[0];
	    			String value = singleparchunks[1];
	    			value = value.trim();
	    			if(value.endsWith(",")) {
    					value = value.substring(0, (value.length()-1));
    				}
	    			if(key.equals("numRepetition")) numRep= value;
	    			if(key.equals("months")) selmonths= value;
	    			if(key.equals("dayRepetition")) dayRep= value;
	    			if(key.equals("weeks")) weeks= value;
	    			if(key.equals("days")) days= value; 
	    		}
	            String monthcron = "";
	            if(selmonths.equals("NONE")){
	            	monthcron = (month + 1) + "/" + numRep;
	            } else {
	            	if(selmonths.equals("")) selmonths = "*";
	            	monthcron = selmonths;
	            }
	            String daycron = "?";
	            if( weeks.equals("NONE") && days.equals("NONE") ){
	            	if(dayRep.equals("0")) dayRep = "1";
	            	daycron = dayRep;
	            }
	            String dayinweekcron = "?";
	            if(!days.equals("NONE")){
	            	if(days.equals("")) days = "*";
	            	dayinweekcron = days;
	            }
	            if( !weeks.equals("NONE")  ){
	            	if(!weeks.equals("")) 
	            		if(weeks.equals("L")) dayinweekcron = dayinweekcron + weeks;
	            		else dayinweekcron = dayinweekcron + "#" + weeks;
	            		dayinweekcron = dayinweekcron.replaceFirst("SUN", "1");
	            		dayinweekcron = dayinweekcron.replaceFirst("MON", "2");
	            		dayinweekcron = dayinweekcron.replaceFirst("TUE", "3");
	            		dayinweekcron = dayinweekcron.replaceFirst("WED", "4");
	            		dayinweekcron = dayinweekcron.replaceFirst("THU", "5");
	            		dayinweekcron = dayinweekcron.replaceFirst("FRI", "6");
	            		dayinweekcron = dayinweekcron.replaceFirst("SAT", "7");
	            }
	    		chronExpression = "0 "+minute+" "+hour+" "+daycron+" "+monthcron+" "+dayinweekcron+ " *";
	    	}
	    } catch (Exception e) {
	    	SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(), 
	    						"getChronExpression", "Error while generating quartz chron expression", e);
	    }
		return chronExpression;
	}

	public Calendar getStartCalendar() {
		return startCalendar;
	}

	public void setStartCalendar(Calendar startCalendar) {
		this.startCalendar = startCalendar;
	}

	public Calendar getEndCalendar() {
		return endCalendar;
	}

	public void setEndCalendar(Calendar endCalendar) {
		this.endCalendar = endCalendar;
	}
	
	public boolean isSimpleTrigger() {
		return getChronExpression() == null;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public boolean isRunImmediately() {
		return runImmediately;
	}

	public void setRunImmediately(boolean runImmediately) {
		this.runImmediately = runImmediately;
	}
	
	
}
