package com.atgugu.demo.web.handlers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.atguigu.demo.common.exception.LoginFailException;
import com.atguigu.demo.common.exception.SendEmailFailException;
import com.atguigu.demo.common.exception.TokenNotFoundException;
import com.atguigu.demo.common.exception.UserMessageUpdateFailException;
import com.atguigu.demo.common.exception.UserNameExistsRegistFailException;
import com.atguigu.demo.common.exception.UserNameNotActivityException;
import com.atguigu.demo.common.global.GlobalMessage;
import com.atguigu.demo.common.utils.DemoUtils;
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
	public String updateUserMessage(User user, @RequestParam("headPicture") MultipartFile headPicture,
			HttpSession session) throws Exception {

		// 先上传文件
		if (!headPicture.isEmpty()) {
			// 获取文件的字节数组
			byte[] bytes = headPicture.getBytes();

			// 获取文件的原始文件名
			String originalFilename = headPicture.getOriginalFilename();

			// 使用FastDFS的客户端执行上传
			DemoUtils fastDFSClientUtils = new DemoUtils("classpath:tracker.conf");
			String[] uploadFile = fastDFSClientUtils.uploadFile(bytes, originalFilename);
			String pictureGroupName = uploadFile[0];
			String pictureRemoteName = uploadFile[1];
			user.setPictureGroupName(pictureGroupName);
			user.setPictureRemoteName(pictureRemoteName);
		}
		// 更新用户信息
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/update";
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("user", user);
		HttpResponse response = DemoUtils.SendRequest(httpClient, requestConfig,url,userMap);
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

	// 用户登出操作
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
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("user", user);
		// 2.执行请求
		HttpResponse response = DemoUtils.SendRequest(httpClient, requestConfig, url, userMap);
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			throw new LoginFailException("用户名密码错误，登录失败");
		}

		String result = EntityUtils.toString(entity);
		// 根据目标类型创建TypeToken对象
		TypeToken<Map<String, User>> typeToken = new TypeToken<Map<String, User>>(){};

		// 通过TypeToken对象获取目标类型
		Type type = typeToken.getType();
		Map<String, User> map = new Gson().fromJson(result, type);
		User queryUser = map.get("user");
		int statusCode = queryUser.getStatus();

		if (statusCode == 0) {
			throw new UserNameNotActivityException("账户未激活，无法登录！");
		}

		session.setAttribute("user", queryUser);
		return "redirect:/index.jsp";
	}

	// 激活账户操作
	@RequestMapping("/user/activate")
	public String activateUser(@RequestParam("token") String token) throws Exception {
		// 1.设置请求的路径
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/activate?token=" + token;
		// 2.执行请求
		HttpResponse response = DemoUtils.SendRequest(httpClient, requestConfig, url, null);
		// 3.获取以及解析响应信息
		HttpEntity entity = response.getEntity();
		String responseValue = EntityUtils.toString(entity);
		// 4.根据响应信息作不同的操作
		if (responseValue.equalsIgnoreCase(GlobalMessage.TOKEN_MESSAGE_NOT_FOUND)) {
			throw new TokenNotFoundException("口令失效，激活失败！");
		}
			return "activate_success";
	}

	// 用户注册操作
	@RequestMapping("user/regist")
	public String regist(User user, Model model) throws Exception {

		// 1.设置请求的路径
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/regist";
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("user", user);
		// 2.执行请求
		HttpResponse response = DemoUtils.SendRequest(httpClient, requestConfig, url, userMap);
		// 3.获取响应体
		HttpEntity responseEntity = response.getEntity();
		// 4.把响应体responseEntity转换为字符串
		String result = EntityUtils.toString(responseEntity, "UTF-8");
		// 根据响应返回的数据处理不同请求
		if (result.equals(GlobalMessage.USER_NAME_OR_EMAIL_ALREADY_EXISTS)) {
			throw new UserNameExistsRegistFailException("用户名或邮箱已存在，注册失败。");
		}
		//将邮件转发到页面显示
		model.addAttribute("email", user.getEmail());

		return "user_email";
	}
	
	
	//发送找回密码的邮件
	@RequestMapping("/user/resetPwd/sendEmail")
	public String resetPwd_sendEmail(@RequestParam("email") String email,Model model) throws Exception, IOException {
		
		// 1.设置请求的路径
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/resetPwdForSendEMail?email=" + email;
		// 2.执行请求
		HttpResponse response = DemoUtils.SendRequest(httpClient, requestConfig, url, null);
		// 3.获取以及解析响应信息
		HttpEntity entity = response.getEntity();
		String responseValue = EntityUtils.toString(entity);
		
		if (responseValue.equals(GlobalMessage.SEND_EMAIL_FAILED)) {
			
			throw new SendEmailFailException("邮件发送失败，请检查邮件地址，网络是否异常");
		}
		model.addAttribute("findPwdEmail", email);
		
		return "user_email";
	}
	
	
	//跳转到找回密码的页面
	@RequestMapping("/user/toResetPwd")
	public String toResetPwd(@RequestParam("token") String token,Model model) throws ClientProtocolException, IOException, TokenNotFoundException {
		
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/activate?token=" + token;
		// 2.执行请求
		HttpResponse response = DemoUtils.SendRequest(httpClient, requestConfig, url, null);
		// 3.获取以及解析响应信息
		HttpEntity entity = response.getEntity();
		String responseValue = EntityUtils.toString(entity);
		// 4.根据响应信息作不同的操作
		if (responseValue.equalsIgnoreCase(GlobalMessage.TOKEN_MESSAGE_NOT_FOUND)) {
			throw new TokenNotFoundException("口令失效，请重新获取！");
		}
		
		model.addAttribute("token", token);
		return "user_reset_pwd";
	}
	
	//重置用户密码操作
	@RequestMapping("user/resetPwd/updatePwd")
	public String updatePwd(@RequestParam("token") String token,
							@RequestParam("userPwd") String userPwd,
							Model model) throws ClientProtocolException, IOException, UserMessageUpdateFailException {
		
		// 1.设置请求的路径
		String url = "http://localhost:8080/demo-user-web-service/services/remote/user/updatePwd?token="
		+ token + "&userPwd=" + userPwd;
		
		// 2.执行请求
		HttpResponse response = DemoUtils.SendRequest(httpClient, requestConfig, url, null);
		// 3.获取以及解析响应信息
		HttpEntity entity = response.getEntity();
		String responseValue = EntityUtils.toString(entity);
		System.out.println("=============" + responseValue  + "==================");
		
		if(responseValue.equals(GlobalMessage.TOKEN_MESSAGE_NOT_FOUND)) {
			throw new UserMessageUpdateFailException("更新失败，请重新再试");
		}
		
		return "user_login";
	}

}
