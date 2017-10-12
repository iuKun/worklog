package com.liukun.work.log.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static String getDate() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	public static String getDateTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
}
