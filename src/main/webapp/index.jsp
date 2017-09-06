<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>	 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>index</title>
<base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
</head>
<body>


	<h1>Welcome</h1>
	
	<c:if test="${empty sessionScope.user }">
		<h2>用户操作</h2>
			<a href="user/toRegist">前往用户注册页面</a><br/>
			<a href="user/toLogin">前往用户登录页面</a><br/>
	</c:if>	
	<c:if test="${!empty sessionScope.user }">
		<h2>用户操作</h2>
			<div>欢迎您:${user.nickName}</div> &nbsp; &nbsp; &nbsp;<div><a href="user/logout">退出登录</a></div><br/>
			<a href="user/toPersonalCenter">前往个人中心</a><br/>
	</c:if>	
		
		<hr>
	<h2>用户操作</h2>
			<a href="#">前往相册页面</a>
	</body>
</html>