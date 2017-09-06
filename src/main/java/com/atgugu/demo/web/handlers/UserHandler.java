package com.atgugu.demo.web.handlers;

import java.lang.reflect.Type;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.atguigu.demo.common.exception.LoginFailException;
import com.atguigu.demo.common.exception.UserMessageUpdateFailException;
import com.atguigu.demo.common.exception.UserNameExistsRegistFailException;
import com.atguigu.demo.common.global.GlobalMessage;
import com.atguigu.demo.common.utils.FastDFSClientUtils;
import com.atguigu.demo.common.utils.SendRequestUtils;
import com.atguigu.demo.pojo.enties.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
public class UserHandler {

	@Autowired
	private CloseableHttpClient httpClient;

	@Autowired
	private RequestConfig requestConfig;

	// 用户更新信息操作
	@RequestMapping("/user/update")
	public String updateUserMessage(User user, @RequestParam("headPicture") MultipartFile headPicture,HttpSession session)
			throws Exception {

		// 先上传文件

		if (!headPicture.isEmpty()) {
			// 获取文件的字节数组
			byte[] bytes = headPicture.getBytes();

			// 获取文件的原始文件名
			String originalFilename = headPicture.getOriginalFilename();

			// 4.使用FastDFS的客户端执行上传
			FastDFSClientUtils fastDFSClientUtils = new FastDFSClientUtils("classpath:tracker.conf");

			String[] uploadFile = fastDFSClientUtils.uploadFile(bytes, originalFilename);

			String pictureGroupName = uploadFile[0];

			String pictureRemoteName = uploadFile[1];

			user.setPictureGroupName(pictureGroupName);

			user.setPictureRemoteName(pictureRemoteName);
		}
		// 更新用户信息
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/update";

		HttpResponse response = SendRequestUtils.SendRequest(httpClient, requestConfig, url, "user", user);

		HttpEntity entity = response.getEntity();

		if (entity == null) {
			throw new UserMessageUpdateFailException("更新失败，请重新再试");
		}
		
		String result = EntityUtils.toString(entity);

		// 根据目标类型创建TypeToken对象
		TypeToken<Map<String, User>> typeToken = new TypeToken<Map<String, User>>() {};

		// 通过TypeToken对象获取目标类型
		Type type = typeToken.getType();

		Map<String, User> map = new Gson().fromJson(result, type);

		User queryUser = map.get("user");
		session.removeAttribute("user");
		session.setAttribute("user", queryUser);
		
		return "redirect:/index.jsp";

	}

	//用户登出操作
	@RequestMapping("/user/logout")
	public String logout(HttpSession session) {
		
		session.removeAttribute("user");
		return "redirect:/index.jsp";
	}
	
	// 用户登录操作
	@RequestMapping("/user/login")
	public String login(User user, HttpSession session) throws Exception {

		// 1.设置请求的路径
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/login";

		// 2.执行请求
		HttpResponse response = SendRequestUtils.SendRequest(httpClient, requestConfig, url, "user", user);
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			throw new LoginFailException("用户名密码错误，登录失败");
		}

		String result = EntityUtils.toString(entity);

		// 根据目标类型创建TypeToken对象
		TypeToken<Map<String, User>> typeToken = new TypeToken<Map<String, User>>() {
		};

		// 通过TypeToken对象获取目标类型
		Type type = typeToken.getType();

		Map<String, User> map = new Gson().fromJson(result, type);

		User queryUser = map.get("user");

		session.setAttribute("user", queryUser);

		return "redirect:/index.jsp";
	}

	// 用户注册操作
	@RequestMapping("user/regist")
	public String regist(User user) throws Exception {

		// 1.设置请求的路径
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/regist";
		// 2.执行请求
		HttpResponse response = SendRequestUtils.SendRequest(httpClient, requestConfig, url, "user", user);

		// 3.获取响应体
		HttpEntity responseEntity = response.getEntity();

		// 4.把响应体responseEntity转换为字符串
		String result = EntityUtils.toString(responseEntity, "UTF-8");

		// 根据响应返回的数据处理不同请求
		if (result.equals(GlobalMessage.USER_NAME_ALREADY_EXISTS)) {
			throw new UserNameExistsRegistFailException("用户名已存在，注册失败。");
		}

		return "user_login";
	}

}
