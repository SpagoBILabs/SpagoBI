

<%@page import="com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter"%>
<%@page import="twitter4j.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="java.util.*" %>
<%@ page import="it.eng.spagobi.twitter.analysis.dataprocessors.*" %>
<%@ page import="it.eng.spagobi.twitter.analysis.pojos.*" %>
<%@ page import="org.quartz.SchedulerException" %>
<%@ page import=" org.quartz.JobKey" %>
<%@ page import="org.quartz.Scheduler" %>
<%@ page import="org.quartz.Trigger" %>
<%@ page import="org.quartz.impl.StdSchedulerFactory" %>
<%@ page import="org.quartz.impl.matchers.GroupMatcher" %>


    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link rel="stylesheet" type="text/css" href="css/twitter.css" />
	<link rel="stylesheet" type="text/css" href="css/jqcloud.css" />
	<link rel="stylesheet" type="text/css" href="css/socialAnalysis.css" >
    <script src="js/lib/others/jquery-2.1.1.min.js"></script>
	<script type="text/javascript" src="js/lib/others/jqcloud-1.0.4.js"></script>

	 
	
	<title>Twitter Analysis</title>
	
</head>
<body>


<% 

try
{
	Scheduler scheduler = new StdSchedulerFactory().getScheduler();
	
	for (String groupName : scheduler.getJobGroupNames()) {
	
		for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
	
			String jobName = jobKey.getName();
			String jobGroup = jobKey.getGroup();
	
			// get job's trigger
			List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
			Date nextFireTime = triggers.get(0).getNextFireTime();
			Date endTime = triggers.get(0).getEndTime();
	
			if(jobGroup.equalsIgnoreCase("groupMonitoring"))
			{
				out.print("<label style='color:blue;'> [jobName] : " + jobName + " [groupName] : " + jobGroup + " - [Next Fire At] : " + nextFireTime + " - [Ending Time At] : " + endTime + "</label><br/>");
			}
			else if(jobGroup.equalsIgnoreCase("groupHSearch"))
			{
				out.print("<label style='color:red;'> [jobName] : " + jobName + " [groupName] : " + jobGroup + " - [Next Fire At] : " + nextFireTime + " - [Ending Time At] : " + endTime + "</label><br/>");
			}
		}
	
	}
}
catch(SchedulerException ex)
{
	out.print("Quartz Console: Error loading active jobs" + ex.getMessage());
}

%>
	
			
</div>        	
			

		
</body>
</html>