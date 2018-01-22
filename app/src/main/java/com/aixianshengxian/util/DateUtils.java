package com.aixianshengxian.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.aixianshengxian.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {
    public static final String formatYMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String formatYMD = "yyyy-MM-dd";
    public static final String formatHMS = "HH:mm:ss";

    /**
     * 格式化日期
     *
     * @param timestamp   日期长整型
     * @param toFormatStr 字符串格式（可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @return 格式结果，解析失败返回空
     */
    public static String DateToString(long timestamp, String toFormatStr) {
        try {
            return new SimpleDateFormat(toFormatStr).format(Long.valueOf(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化日期
     *
     * @param date        日期Date
     * @param toFormatStr 字符串格式（可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @return 格式结果，解析失败返回空
     */
    public static String DateToString(Date date, String toFormatStr) {
        try {
            return new SimpleDateFormat(toFormatStr).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串转日期
     *
     * @param dateString 日期字符串
     * @param strFormat  字符串格式（可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @return 返回转化后的日期Date，解析失败返回null
     */
    public static Date StringToDate(String dateString, String strFormat) {
        try {
            return StringToDate2(dateString, strFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转日期
     *
     * @param dateString 日期字符串
     * @param strFormat  字符串格式（可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @return 返回转化后的日期Date，出错报异常
     */
    public static Date StringToDate2(String dateString, String strFormat) throws ParseException {
        Date date = new SimpleDateFormat(strFormat).parse(dateString);
        return date;
    }

    /**
     * 字符串转时间戳
     *
     * @param dateString 日期字符串
     * @param strFormat  字符串格式（可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @return 返回转化后的时间戳
     */
    public static Timestamp stringToTimestamp(String dateString, String strFormat) {
        return new Timestamp(StringToDate(dateString, strFormat).getTime());
    }

    /**
     * 字符串转时间戳，并加天数
     *
     * @param dateString 日期字符串
     * @param strFormat  字符串格式（可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @param days       欲加天数
     * @return 返回转化后的时间戳
     */
    public static Timestamp stringToTimestampAddDays(String dateString, String strFormat, int days) {
        return new Timestamp(StringToDate(dateString, strFormat).getTime() + days * 1000 * 60 * 60 * 24);
    }

    /**
     * 日期转时间戳
     *
     * @param date 日期
     * @return 返回转化后的时间戳
     */
    public static Timestamp toTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * 获取某日期的几天后（前）的日期字符串
     *
     * @param day    后/前几天（大于0为后，小于0为前）
     * @param now    日期（也可以是当时间）
     * @param format （可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @return 返回日期格式化后的字符串，异常返回空
     */
    public static String getSomeDate(int day, Date now, String format) {
        try {
            Calendar date = Calendar.getInstance();
            date.setTime(now);
            date.add(Calendar.DAY_OF_MONTH, day);
            return DateToString(date.getTime(), format);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前时间的几天后（前）的日期字符串
     *
     * @param day 后/前几天（大于0为后，小于0为前）
     * @return 返回日期格式化后的字符串，异常返回空
     */
    public static String getSomeDate(int day) {
        try {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DAY_OF_MONTH, day);
            return DateToString(date.getTime(), "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断两日期是否是同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return true同一天，false 不同一天
     */
    public static boolean compareEqualDate(Date date1, Date date2) {
        try {
            String c1 = DateToString(date1, "yyyy-MM-dd");
            String c2 = DateToString(date2, "yyyy-MM-dd");
            return c1.equals(c2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 比较两日期时间
     *
     * @param date1 日期时间1
     * @param date2 日期时间2
     * @return true 日期时间1<日期时间2; false 日期时间1相等或晚于日期时间2
     */
    public static boolean compareDate(Date date1, Date date2) {
        try {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(date1);
            c2.setTime(date2);
            return !c1.before(c2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取两个时间字符串表示的时间最大的值
     *
     * @param date1 日期字符串1，格式：yyyy-MM-dd
     * @param date2 日期字符串2，格式：yyyy-MM-dd
     * @return 时间最大的那个值
     */
    public static String getMaxDate(String date1, String date2) {
        Date comDate1 = StringToDate(date1, "yyyy-MM-dd");
        Date comDate2 = StringToDate(date2, "yyyy-MM-dd");
        if (comDate1.after(comDate2)) {
            return date1;
        }
        return date2;
    }

    /**
     * 获取某日期往后/前N年的日期（格式：yyyy-MM-dd）
     *
     * @param date 日期
     * @param year 后/前N年（后为正，前为负）
     * @return 返回某日期往后/前N年后的日期（格式：yyyy-MM-dd）
     */
    public static String getDateByYear(Date date, int year) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.YEAR, year);
            return DateToString(cal.getTime(), "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取某日期往后/前N月的日期（格式：yyyy-MM-dd）
     *
     * @param date  日期
     * @param month 后/前N年（后为正，前为负）
     * @return 返回某日期往后/前N年后的日期（格式：yyyy-MM-dd）
     */
    public static String getDateByMonth(Date date, int month) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int maxDay = cal.get(Calendar.DAY_OF_MONTH);
            cal.add(Calendar.MONTH, month);
            int maxDay2 = cal.get(Calendar.DAY_OF_MONTH);
            if (maxDay2 < maxDay) {
                cal.set(Calendar.DAY_OF_MONTH, maxDay2);
            }
            return DateToString(cal.getTime(), "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得两个日期的月份间隔
     *
     * @param date1 日期1（格式：yyyy-MM-dd）
     * @param date2 日期1（格式：yyyy-MM-dd）
     * @return 返回间隔月份
     * @throws ParseException
     */
    public static Integer getMonthSpace(String date1, String date2)
            throws ParseException {
        Integer result = Integer.valueOf(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(sdf.parse(date1));
        c2.setTime(sdf.parse(date2));
        result = Integer.valueOf(c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH));
        return Math.abs(result.intValue());
    }

    /**
     * 获得当前时间
     *
     * @param formatStr 格式（可以自己写，也可以调用本类中的：formatYMDHMS,formatYMD）
     * @return 日期格式
     */
    public static String getNowTime(String formatStr) {
        SimpleDateFormat df = new SimpleDateFormat(formatStr);
        return df.format(new Date());
    }

    /**
     * 给日期输入框加入触发选择日期
     * @param context
     * @param input 输入框对象
     * @param showTime 是否显示时间
     * @param listener 改变时回调（空不回调）
     */
    public static void initDatePick(final Context context, final TextView input, final boolean showTime, final EditText.OnFocusChangeListener listener) {
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                if (!TextUtils.isEmpty(input.getText().toString())) {
                    Date date = null;
                    if (showTime) {
                        date = StringToDate(input.getText().toString(), formatYMDHMS);
                    } else {
                        date = StringToDate(input.getText().toString(), formatYMD);
                    }
                    if (date == null) {
                        cal.setTimeInMillis(System.currentTimeMillis());
                    } else {
                        cal.setTime(date);
                    }
                } else {
                    cal.setTimeInMillis(System.currentTimeMillis());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = View.inflate(context, R.layout.window_datepicker, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
                TextView tv_time_picker = (TextView) view.findViewById(R.id.tv_time_picker);
                if (showTime) {
                    timePicker.setIs24HourView(true);
                    timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(Calendar.MINUTE);
                    timePicker.setVisibility(View.VISIBLE);
                } else {
                    tv_time_picker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                }
                builder.setView(view);
                builder.setTitle("选取时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",datePicker.getYear(), datePicker.getMonth() + 1,datePicker.getDayOfMonth()));
                        if (showTime) {
                            sb.append(" ").append(timePicker.getCurrentHour()).append(":").append(timePicker.getCurrentMinute());
                        }
                        input.setText(sb);
                        input.clearFocus();
                        dialog.dismiss();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public static void tvDatePick(final Context context, final TextView input, final boolean showTime) {
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                if (!TextUtils.isEmpty(input.getText().toString())) {
                    Date date = null;
                    if (showTime) {
                        date = StringToDate(input.getText().toString(), formatYMDHMS);
                    } else {
                        date = StringToDate(input.getText().toString(), formatYMD);
                    }
                    if (date == null) {
                        cal.setTimeInMillis(System.currentTimeMillis());
                    } else {
                        cal.setTime(date);
                    }
                } else {
                    cal.setTimeInMillis(System.currentTimeMillis());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = View.inflate(context, R.layout.window_datepicker, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
                TextView tv_time_picker = (TextView) view.findViewById(R.id.tv_time_picker);
                if (showTime) {
                    timePicker.setIs24HourView(true);
                    timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(Calendar.MINUTE);
                    timePicker.setVisibility(View.VISIBLE);
                } else {
                    tv_time_picker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                }
                builder.setView(view);
                builder.setTitle("选取时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",datePicker.getYear(), datePicker.getMonth() + 1,datePicker.getDayOfMonth()));
                        if (showTime) {
                            sb.append(" ").append(timePicker.getCurrentHour()).append(":").append(timePicker.getCurrentMinute());
                        }
                        input.setText(sb);
                        input.clearFocus();
                        dialog.dismiss();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }
/***
 * 秒数转成时分秒格式
 *
 */
public static String secToTime(long time) {
    String timeStr = null;
    long hour = 0;
    long minute = 0;
    long second = 0;
    if (time <= 0)
        return "00:00";
    else {
        minute = time / 60;
        if (minute < 60) {
            second = time % 60;
            timeStr = unitFormat(minute) + ":" + unitFormat(second);
        } else {
            hour = minute / 60;
            if (hour > 99)
                return "99:59:59";
            minute = minute % 60;
            second = time - hour * 3600 - minute * 60;
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
        }
    }
    return timeStr;
}

    public static String unitFormat(long i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}