package com.ah.hf.lj.wenjin.amy.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ah.hf.lj.wenjin.amy.entity.Author;

@RestController
@RequestMapping("/users")
public class UserController {
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private Author author;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showUsers() {
		// 获取用户列表
		return "showUsers";
	}

	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public String addUser() {
		// 创建User
		return "showUsers";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String queryById(@PathVariable Long id) {
		// 获取url中id值的User信息
		return "";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String updateById(@PathVariable int id) {
		// 处理"/users/{id}"的PUT请求，用来更新User信息
		return "";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delById(@PathVariable int id) {
		// 处理"/users/{id}"的DELETE请求，用来删除User
		return "";
	}

	@RequestMapping(value = "/hi", method = RequestMethod.GET)
	public String sayHi() {

		return "hi  " + author.getName() + "  随机字符串：" + author.getStrValue() + " [10,20]以内的随机数:"
				+ author.getRangeIntValue();
	}

	//@GetMapping("/executor")
	public String ScheduledExecutorService() {
		//
		ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				logger.info("ScheduledExecutorService定时任务执行：" + new Date());
			}
		}, 1, 1, TimeUnit.SECONDS);// 首次延迟1秒，之后每1秒执行一次
		logger.info("ScheduledExecutorService定时任务启动：" + new Date());
		return "ScheduledExecutorService!";
	}

}
