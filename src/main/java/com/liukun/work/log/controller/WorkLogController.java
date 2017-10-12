package com.liukun.work.log.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liukun.work.log.utils.DateUtils;

@RestController
@RequestMapping("/work")
public class WorkLogController {

	private static final Logger log = LoggerFactory.getLogger(WorkLogController.class);
	private static final String OK = "打卡成功";
	private static final String YOYO = "打卡失败！请重新打卡\n";
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private ConfigClass config;
	
	private String startSql = 
			"insert into worklog (work_date,work_time_begin,state) values ('{WORK_DATE}','{WORK_TIME_BEGIN}','on')";
	
	private String endSql = 
			"update worklog set work_time_end = '{WORK_TIME_END}', state='off' where work_date='{WORK_DATE}' and state='on'";
	
	private String cleanSummayHis = 
			"delete from worklog_summary where work_date='{WORK_DATE}'";
	
	private String summarySql = 
			 "insert into worklog_summary (work_date,work_time_extra,work_time_extra_money) "
			+"select work_date,"  
			+"  case"  
			+"    when sum_min>0 then sum_min/60 else 0"  
			+"  end extra_hour,"
			+"  case" 
			+"    when sum_min>0 then sum_min/60*{BASE_HOUR_PAY} else 0" 
			+"  end extra_hour_money " 
			+"from (" 
			+"  select work_date,sum(60*hour(timediff(work_time_end,work_time_begin))+minute(timediff(work_time_end,work_time_begin)))-{BASE_WORK_HOUR} sum_min" 
			+"    from worklog" 
			+"  where work_date='{WORK_DATE}' " 
			+"group by work_date) a";
	
	@RequestMapping("/start")
	public String startWorking() {
		String startSqlReal = startSql
				.replaceAll("\\{WORK_DATE}", DateUtils.getDate())
				.replaceAll("\\{WORK_TIME_BEGIN}", DateUtils.getDateTime());
		try{
			log.info("干活签到！ sql = {}",startSqlReal);
			jdbc.execute(startSqlReal);
			return "上班"+OK;
		}catch (Exception e) {
			return "上班"+YOYO + e.getMessage();
		}
	}
	
	@RequestMapping("/end")
	public String endWorking() {
		String endSqlReal = endSql
				.replaceAll("\\{WORK_DATE}", DateUtils.getDate())
				.replaceAll("\\{WORK_TIME_END}", DateUtils.getDateTime());
		log.info("休息签到！ sql = {}",endSqlReal);
		try {
			int result = jdbc.update(endSqlReal);
			if(result == 1) generateWorkSummay();
			return "下班"+OK;
		}catch (Exception e) {
			return "下班"+YOYO + e.getMessage();
		}
	}
	
	private void generateWorkSummay() {
		String cleanSummayHisReal = cleanSummayHis
				.replaceAll("\\{WORK_DATE}", DateUtils.getDate());
		String summarySqlReal = summarySql
				.replaceAll("\\{WORK_DATE}", DateUtils.getDate())
				.replaceAll("\\{BASE_WORK_HOUR}", config.getHour())
				.replaceAll("\\{BASE_HOUR_PAY}", config.getHourPay());
		log.info("清理历史！sql = {}",cleanSummayHisReal);
		jdbc.execute(cleanSummayHisReal);
		log.info("算加班没！sql = {}",summarySqlReal);
		jdbc.update(summarySqlReal);
	}
}
