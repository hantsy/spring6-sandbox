
<#macro head>
		<head>
			<title>Spring MVC with Freemarker</title>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
			        <!-- Bootstrap core CSS -->
	        <link href="<@spring.url '/'/>/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet"
	              media="screen"/>
	        <!-- Font awesome CSS -->
	        <link href="<@spring.url '/'/>/webjars/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet"
	              media="screen"/>	
		</head>
</#macro>
	
<#macro navbar fluid=true >
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<#if fluid><#assign class="container-fluid"/><#else><#assign class="container"/></#if>
		<div class="${class}">
			<#nested>
		</div>
	</div>
</#macro>
	
<#macro pageHeader title subtitle="">
	<div class="page-header">
	  <h1>${title!""} <#if subtitle??><small>${subtitle!""}</small></#if></h1>
	</div>
</#macro>	

<#macro container fluid=true>
	<#if fluid><#assign class="container-fluid"/><#else><#assign class="container"/></#if>
	<div class="${class}">
		<#nested>
	</div>
</#macro>
	
<#macro scripts>
	<script type="text/javascript" src="<@spring.url '/'/>/webjars/jquery/2.1.3/jquery.min.js"></script>
	<script type="text/javascript" src="<@spring.url '/'/>/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>	
</#macro>

