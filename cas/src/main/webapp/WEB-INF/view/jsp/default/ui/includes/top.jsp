<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
    <meta name="generator" content="HTML Tidy for Windows (vers 1 June 2005), see www.w3.org" />
    <title>SpagoBI Login</title>
    <style>
      body {
	       padding: 0;
	       margin: 0;
      }
    </style>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <script language="javascript" type="text/javascript">
    //<![CDATA[
    function login(){
      var lgn = document.getElementById('lgn');
      var pwd = document.getElementById('pwd');
      document.getElementById('msgMandatory').style.display='none';
      document.getElementById('message').style.display='none';
      if(lgn.value=="" || pwd.value=="") {
         displayMsgMandatory();
      }else {
        document.getElementById('login-form').submit();
    //    setTimeout('displaybis()',1000);
      }
    } 
    function displayMsgMandatory(){
      document.getElementById('msgMandatory').style.display='inline';
    }
    
    function displaybis(){
      document.getElementById('message').style.display='inline';
    }
    
    function cleanMessage(){
      document.getElementById('message').style.display='none';
    }
    //]]>
    </script>

</head>

	<!--<body id="cas" onload="init()">-->
  <body id="cas" >
  <div id="background" style="width:100%;height:100%;background-image:url(./images/wapp/background.jpg);background-repeat:no-repeat;background-position: top left;"> 
      <div id="backgroundlogo" style="width:100%;height:100%;background-image:url(./images/wapp/backgroundlogo.jpg);background-repeat:no-repeat;background-position: bottom right;"> 
        <div id="header" style="width:100%;height:70px;">
            <div id="logotitle" style="height:57px;background-image:url(./images/wapp/titlelogo.gif);background-repeat:no-repeat;background-position: top left;"> 
            </div>
            <div id="menubar" style="width:100%;height:20px;border-top:1px solid gray;border-bottom:1px solid gray;background-image:url(./images/wapp/backgroundMenuBar.jpg);background-repeat:repeat-x;"> 
            </div>
        </div>

	    <div id="content" style="width:100%;">
	      
	    	<div style="background-color:white;width:500px;height:150px;border:1px solid gray;margin-top:130px;margin-left:50px;" >
            <!--<h3><spring:message code="screen.welcome.instructions" /></h3>-->