<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}/">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Regist</title>
</head>
<body>
	
		<h1>用户注册</h1>
		
		<form action="user/regist" method="post">
			
			用户账号：<input type="text" name="userName" /><br/>
			用户密码：<input type="text" name="userPwd" /><br/>
			用户邮箱：<input type="text" name="email" /><br/>
			
				    <input type="submit" value="提交" />
		</form>
	</body>
</html>