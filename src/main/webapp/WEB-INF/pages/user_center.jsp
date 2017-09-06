<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User Center</title>
<base
	href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
</head>
<body>

	<h1>个人中心</h1>

	<table>
		<tr>
			<th>账号</th>
			<th>昵称</th>
			<th>头像</th>
			<th>职业</th>
			<th>家乡</th>
			<th>性别</th>
			<th>简介</th>
		</tr>
		<tr>
			<td>${sessionScope.user.userName}</td>
			<td>${sessionScope.user.nickName}</td>
			<td><img src="http://192.168.60.66/${sessionScope.user.pictureGroupName}/${sessionScope.user.pictureRemoteName}" height="50" width="50" /></td>
			<td>${sessionScope.user.job}</td>
			<td>${sessionScope.user.hometown}</td>
			<td>${sessionScope.user.gender}</td>
			<td>${sessionScope.user.userDescribe }</td>
		</tr>
	</table>

	<hr>

	<h2>修改个人信息</h2>
	<form action="user/update" method="post" enctype="multipart/form-data">
		<input type="hidden" name="userId" value="${sessionScope.user.userId}" />
		<table>
			<tr>
				<td>昵称：</td>
				<td><input type="text" name="nickName" /></td>
			</tr>

			<tr>
				<td>头像：</td>
				<td><input type="file" name="headPicture" /></td>
			</tr>
			<tr>
				<td>职业：</td>
				<td><input type="text" name="job" /></td>
			</tr>
			<tr>
				<td>家乡：</td>
				<td><input type="text" name="hometown" /></td>
			</tr>
			
			<tr>
				<td>性别：</td>
				<td><input type="text" name="gender" /></td>
			</tr>
			<tr>
				<td>简介：</td>
				<td><textarea cols="21" rows="10" name="userDescribe"></textarea></td>
			</tr>
			
			<tr>
				<td align="left" colspan="2"><input type="submit" value="更新" /></td>
			</tr>
		</table>
	</form>



</body>
</html>