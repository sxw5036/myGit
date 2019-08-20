package com.lwxf.industry4.webapp.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/5/17 15:04
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class NumberUtilsExt {

    public static String getPercent(Integer oneNumber,Integer twoNumber,String decimalDigits){
        if (twoNumber==null||twoNumber==0){
            return "";
        }
        if (oneNumber==null){
            return "";
        }
        double price = (double)oneNumber / (double)twoNumber;
        NumberFormat nt = NumberFormat.getPercentInstance();
        DecimalFormat df=new DecimalFormat(decimalDigits);
        String value = df.format(price);
        nt.setMaximumFractionDigits(2);
        String percent = nt.format(new BigDecimal(value));
        return percent;
    }
    /**
        数字四舍五入保留两位小数
     */
    public static String numberFormat(double num) {
        DecimalFormat df = new DecimalFormat(".00");
       return df.format(num);
    }
}
