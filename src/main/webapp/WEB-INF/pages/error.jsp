<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}/">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>error</title>

<script type="text/javascript">
	window.onload = function(){
		var backBtn = document.getElementById("backBtn");
		
		backBtn.onclick = function(){
			
			window.history.back();
		};
	};

</script>
</head>
<body>
	
	<h1>${exception.message }</h1>
	
	<button id="backBtn">返回上一级操作</button>
	
</body>
</html>