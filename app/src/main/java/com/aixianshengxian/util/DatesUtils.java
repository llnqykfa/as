package com.aixianshengxian.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatesUtils {
	/**
	 * 获取现在时间
	 *
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 *
	 * @return 返回短时间字符串格式yyyy-MM
	 */
	public static String getStringDateShortYM() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取距离今天多少天后的日期
	 *
	 * @param day
	 *            整数往后推,负数往前移动
	 * @return
	 */
	public static String getStringDateShortFromToday(int day) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, day);// 正数把日期往后增加一天.
		date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}

	/**
	 * 获取距离今天多少天后的日期
	 *
	 * @param day
	 *            整数往后推,负数往前移动
	 * @return
	 */
	public static String getStringDateShortFromTodayYM(int day) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, day);// 正数把日期往后增加一天.
		date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		return formatter.format(date);
	}

	/**
	 * 获取距离某天多少天的日期
	 *
	 * @param day
	 *            整数往后推,负数往前移动
	 * @return
	 */
	public static Date getDateFromSomeday(String date, int day) {
		Date d = strToDate(date);// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		calendar.add(calendar.DATE, day);// 正数把日期往后增加一天.
		d = calendar.getTime();
		return d;
	}

	/**
	 * 获取距离某天多少天的日期
	 *
	 * @param day
	 *            整数往后推,负数往前移动
	 * @return
	 */
	public static String getStringDateFromSomeday(String date, int day) {
		Date d = strToDate(date);// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		calendar.add(calendar.DATE, day);// 正数把日期往后增加一天.
		d = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(d);
	}

	/**
	 * 根据日期字符串返回 几月几日格式
	 *
	 * @param data
	 * @return
	 */
	public static String getStringMonAndDay(String data) {
		String str = "";
		DateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");
		DateFormat format2 = new SimpleDateFormat("yyyy-mm-dd");
		Date date1 = null;
		try {
			date1 = format1.parse(data);
			str = format2.format(date1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 根据日期字符串返回 几月几日格式
	 *
	 * @param data
	 * @return
	 */
	public static String getStringHouAndMin(String data) {
		String str = "";
		DateFormat format1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		DateFormat format2 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		Date date1 = null;
		try {
			date1 = format1.parse(data);
			str = format2.format(date1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 获取时间 小时:分;秒 HH:mm:ss
	 *
	 * @return
	 */
	public static String getTimeShort(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date(123);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取制定日期的时间 小时:分 HH:mm
	 *
	 * @return
	 */
	public static String getDateOfTime(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 获取现在时间
	 *
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}


	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 *
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 *
	 * @param dateDate
	 * @return
	 */
	public static String dateToStrLong(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 *
	 * @param dateDate
	 * @return
	 */
	public static String convert(String mill) {
		if (mill == null || mill.equals("null") || mill.equals("")) {
			return "";
		}
		return String.format("%tF", Long.valueOf(mill) * 1000);
		// return String.format("%tF %<tT", mill);
	}

	/**
	 * 将短时间格式时间转换为字符串 yyyy-MM-dd
	 *
	 * @param dateDate
	 * @param k
	 * @return
	 */
	public static String dateToStr(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 转换为字符串 yyyy-MM-dd
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            天
	 * @return
	 */
	public static String intFormatToDateStr(int year, int month, int day) {
		String parten = "00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String dateStr = year + "-" + decimal.format(month) + "-"
				+ decimal.format(day);
		return dateStr;
	}

	/**
	 * 根据日期字符串，获取星期
	 * 
	 * @param pTime
	 * @return
	 */
	public static String getWeek(String pTime) {
		String Week = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(pTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "周天";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "周一";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "周二";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "周三";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "周四";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "周五";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "周六";
		}
		return Week;
	}

	/**
	 * 获取该时间与当前时间相差的小时数
	 * 
	 * @param old_time
	 * @return
	 */
	public static long getHourBetweenNow(String old_time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		long hour = 0;
		try {
			Date begin = df.parse(old_time);
			String now_time = getStringDate();
			Date end = df.parse(now_time);
			long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
			hour = between / 3600;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hour;
	}

	/**
	 * 计算两个日期相差天数
	 * 
	 * @param yDay
	 * @param tDay
	 * @return
	 */
	public static long castDate(String yDay, String tDay) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d1 = sdf.parse(yDay);
			Date d2 = sdf.parse(tDay);
			return ((d2.getTime() - d1.getTime()) / (3600L * 1000 * 24));
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 日期转long
	 * 
	 * @param date
	 * @return
	 */
	public static long convert2long(String date) {
		if (StringUtils.isEmpty2(date))
			return -1;
		long temp = 0;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			temp = sf.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * 日期转long String
	 * 
	 * @param date
	 * @return
	 */
	public static String convert2longString(String date) {
		if (StringUtils.isEmpty2(date))
			return "";
		long temp = 0;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			temp = sf.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp + "";
	}

	/**
	 * 年月 转long 用于服务器，要除以1000
	 * 
	 * @param date
	 * @return
	 */
	public static long convert2longYM(String date) {
		if (StringUtils.isEmpty2(date))
			return -1;
		long temp = 0;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
		try {
			temp = sf.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp / 1000;
	}

	/**
	 * long转年月 用于服务器
	 * 
	 * @param long_data
	 * @return
	 */
	public static String cvonvert2StringYM(String long_data) {
		if (StringUtils.isEmpty2(long_data))
			return "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		Date date = new Date(Long.parseLong(long_data) * 1000);
		return formatter.format(date);
	}
}
