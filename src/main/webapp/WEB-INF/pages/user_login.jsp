<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login</title>
<base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
</head>
<body>
	<h1>用户登录</h1><br/>
	
	<form action="user/login" method="post">
			
			用户账号：<input type="text" name="userName" value="admin" /><br/>
			用户密码：<input type="text" name="userPwd" value="admin" /><br/>
				    <input type="submit" value="登录" />
		</form>
</body>
</html>