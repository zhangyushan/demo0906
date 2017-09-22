<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User Activate</title>
<base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
</head>
	<body>
		
		<c:if test="${!empty requestScope.email }">
			<h2>我们已经向此邮箱${requestScope.email }发送了激活邮件，马上登录邮箱进行账户激活操作吧！</h2>
		</c:if>
		
		<c:if test="${!empty requestScope.findPwdEmail }">
			<h2>我们已经向此邮箱${requestScope.email }发送了重置密码邮件，马上登录邮箱进行密码重置操作吧！</h2>
		</c:if>
	</body>
</html> 