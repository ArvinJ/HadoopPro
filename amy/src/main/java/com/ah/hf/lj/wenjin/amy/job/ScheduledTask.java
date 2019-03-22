package com.ah.hf.lj.wenjin.amy.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledTask {
	private final static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 自动扫描，启动时间点之后5(5000)秒执行一次
	 */
	@Scheduled(fixedRate = 500000000)
	public void getCurrentDate() {
		logger.info("Scheduled定时任务执行：" + new Date());
	}
}