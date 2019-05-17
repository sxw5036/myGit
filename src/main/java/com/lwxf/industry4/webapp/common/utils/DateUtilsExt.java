package com.lwxf.industry4.webapp.common.utils;
import java.util.*;

import com.lwxf.commons.utils.DateUtil;

/**
 * 功能：查询时间
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/5/16 14:38
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class DateUtilsExt {

    private static Date today = new Date();

    private static Calendar cal = new GregorianCalendar();

    /**
     * 查询当前时间前days的日期
     * @return
     */
    public static String getDayDate(Integer days){
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -days);
        Date beforeDay = cal.getTime();
        String s = DateUtil.dateTimeToString(beforeDay, DateUtil.FORMAT_DATE);
        return s;
    }

    /**
     * 获得当月1号零时零分零秒
     * @return
     */
    public static Date getFirstDayCurMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 根据日期段获得每一天的日期
     * @param dBegin 开始日期
     * @param dEnd 结束日期
     * @return
     */
    public static List<Date> findDates(Date dBegin, Date dEnd)
    {
        List lDate = new ArrayList();
        lDate.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime()))
        {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }


}
