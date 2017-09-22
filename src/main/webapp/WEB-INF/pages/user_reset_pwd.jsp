<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Find Pwd For Send Email</title>
<base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
</head>
<body>
	
	<h1>丑哭了网——密码找回</h1>
	
	<c:if test="${empty requestScope.token}">
		<h2>发送邮件</h2>
		<form action="user/resetPwd/sendEmail" method="post">
			请输入您注册时填写的邮箱：<input type="text" name="email" /><br/>
			 					<input type="submit" value="提交" />
		</form>
	</c:if>
	
	<c:if test="${!empty requestScope.token }">
		<h2>重置密码</h2>
			<form action="user/resetPwd/updatePwd" method="post">
				<input type="hidden" name="token" value="${requestScope.token}" />
				请输入你的新密码：<input type="text" name="userPwd" /><br/>
				请再次输入新密码：<input type="text" name="userPwd2" /><br/>
						      <input type="submit" value="提交" />
		</form>
	</c:if>
	
	

</body>
</html>