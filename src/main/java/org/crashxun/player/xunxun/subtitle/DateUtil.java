package org.crashxun.player.xunxun.subtitle;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	/**
	 * @return format 2013-07-01
	 */
	public static String get422Format() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}

	/**
	 * @return format 07-01-2013
	 */
	public static String get224Format() {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		return df.format(new Date());
	}

	/**
	 * @return format 2014-08-11 12:11:11
	 */
	public static String getTimePrefix() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

	public static String getTimePrefix2() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return df.format(new Date());
	}

	/**
	 * @return format 2014-08-11 12:11:11
	 */
	public static String getTimeHM(long time) {
		SimpleDateFormat df = new SimpleDateFormat("mm:ss");
		return df.format(new Date(time));
	}
	/**
	 * @return format 2014-08-11 12:11:11:232
	 */
	public static String getTimeMillisecondPrefix() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return df.format(new Date());
	}
	/**
	 * @return format 12:11:11:232
	 */
	public static String getHMSTimeMillisecondPrefix() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
		return df.format(new Date());
	}
	/**
	 * @return format
	 */
	public static String getTimePrefix(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}

	/**
	 * @param fixedHour
	 *            the hour of today
	 * @return he current day of the fixed time
	 */
	public static Calendar getCurrentDayOfFixedHour(int fixedHour) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, fixedHour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c;
	}

	/**
	 * @param fixedHour
	 *            the hour you want to set
	 * @return the tomorrow with fixed time point
	 */
	public static Calendar getTomorrowOfFixedHour(int fixedHour) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, fixedHour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);
		return c;
	}

	/**
	 * print the time of calendar presented.
	 * 
	 * @param cl
	 * @return time string.
	 */
	public static String printCalendar(Calendar cl) {
		int year = cl.get(Calendar.YEAR);
		int month = cl.get(Calendar.MONTH) + 1;
		int day = cl.get(Calendar.DATE);
		int hour = cl.get(Calendar.HOUR_OF_DAY);
		int minute = cl.get(Calendar.MINUTE);
		int ms = cl.get(Calendar.SECOND);
		String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + ms;
		return time;
	}

	public static String convertCalendarToLocalzoneTime(Calendar mCalendar) {
		if (mCalendar == null) {
			return "";
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(mCalendar.getTime());
	}

	public static Calendar getNextDayOfPreciseTime(String time) {
		Calendar c = setPreciseTime(time);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
		return c;
	}

	/**
	 * convert time String 2014-08-21 11:11:11 to Calendar object.<br/>
	 * If the time String exceed the time rule,it will return the current<br/>
	 * time.
	 * 
	 * @param time
	 *            string of time
	 * @return the time to set
	 */
	public static Calendar setPreciseTime(String time) {
		// 2014-08-20 13:30:08
		Calendar c = Calendar.getInstance();
		if (TextUtils.isEmpty(time)) {
			return c;
		}
		String ts[] = time.trim().split(" ");
		int len = ts.length;
		if (len == 0) {
			return c;
		}
		if (len == 1) {
			if (ts[0] != "") {
				String ymd[] = ts[0].trim().split("-");
				int year = Integer.valueOf(ymd[0]);
				int month = Integer.valueOf(ymd[1]);
				int day = Integer.valueOf(ymd[2]);
				if (month > 12) {
					return c;
				}
				if (day > 31) {
					return c;
				}
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, month - 1);
				c.set(Calendar.DAY_OF_MONTH, day);
			}
		} else if (len == 2) {
			String ymd[] = ts[0].trim().split("-");
			String hmm[] = ts[1].trim().split(":");
			int year = Integer.valueOf(ymd[0]);
			int month = Integer.valueOf(ymd[1]);
			int day = Integer.valueOf(ymd[2]);
			int hour = Integer.valueOf(hmm[0]);
			int minute = Integer.valueOf(hmm[1]);
			int second = Integer.valueOf(hmm[2]);
			if (month > 12) {
				return c;
			}
			if (day > 31) {
				return c;
			}
			if (hour > 24) {
				return c;
			}
			if (minute > 60) {
				return c;
			}
			if (second > 60) {
				return c;
			}
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month - 1);
			c.set(Calendar.DAY_OF_MONTH, day);
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			c.set(Calendar.SECOND, second);
		}
		return c;
	}

	/**
	 * 
	 * @param time
	 *            long ms
	 * @return like 2014-09-11
	 */
	public static String getYearMonthDay(long time) {
		Date dat = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(dat);
		return dateString;
	}

	/**
	 * get fixed time that realte to current time.e.g:2014-01-01
	 * 
	 * @param fixedDay
	 *            positive value represent next day, negative value represent
	 *            previous day.
	 * @return
	 */
	public static String getFixedDayTime(int fixedDay) {
		Calendar c = Calendar.getInstance();
		int currentDay = c.get(Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, currentDay + fixedDay);
		StringBuilder time = new StringBuilder();
		time.append(c.get(Calendar.YEAR));
		time.append("-");
		int month = c.get(Calendar.MONTH) + 1;
		time.append(month < 10 ? "0" + month : month);
		time.append("-");
		int day = c.get(Calendar.DAY_OF_MONTH);
		time.append(day < 10 ? "0" + day : day);
		return time.toString();
	}

	/**
	 * 
	 * @param time
	 *            2014-12-01
	 * @return 2014/12/01 星期一
	 */
	public static String showUiTime(String time) {
		StringBuilder builder = new StringBuilder();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(time);
			SimpleDateFormat me = new SimpleDateFormat("yyyy/MM/dd");
			String t0 = me.format(date);
			SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE");
			String w0 = sdf1.format(date);
			builder.append(t0 + " " + w0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * @return format 2013-07-01
	 */
	public static String get422Format(String time) {
		String t = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(time);
			t = sdf.format(date);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * convert time to long
	 * 
	 * @param time
	 *            2013-01-01
	 * @return ms
	 */
	public static long convertDate2ms(String time) {
		long ms = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(time);
			ms = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ms;
	}

	/**
	 * convert time to long
	 *
	 * @param time
	 *            2013-01-01 11:11:11
	 * @return ms
	 */
	public static long convertTime2ms(String time) {
		long ms = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(time);
			ms = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ms;
	}

	/**
     * 设置过时区,格式转毫秒,时区0
	 */
	public static long convertTimeNoYMD2ms(String time) {
		long ms = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss,SSS");
		try {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date = sdf.parse(time);
			ms = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ms;
	}

	/**
	 * convert time to long
	 *
	 * @param time
	 *            2013-01-01 11:11:11
	 * @return ms
	 */
	public static long convertTimeNoSS2ms(String time) {
		long ms = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date date = sdf.parse(time);
			ms = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ms;

	}
}
