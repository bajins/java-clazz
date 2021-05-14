package com.bajins.clazz;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一切与数字操作相关的工具类
 * <p>
 *
 * @author claer
 * @program com.bajins.api.utils
 * @description NumberUtil
 * @create 2018-07-18 22:24
 * @see DecimalFormat
 * @see BigDecimal
 * @see BigInteger
 * @see RoundingMode
 * @see Number 所有包装类（Byte、Short、Integer、Long、Character、Float、Double、Boolean）和Math包下的BigDecimal、BigInteger的父类
 */
public class NumberUtil {


    /**
     * 判断整数（int）
     *
     * @param str
     * @return boolean
     */
    public static boolean isInteger(String str) {
        Objects.requireNonNull(str, "请输入参数");
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断浮点数（double和float）
     *
     * @param str
     * @return boolean
     */
    public static boolean isDouble(String str) {
        Objects.requireNonNull(str, "请输入参数");
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 用正则表达式判断字符串是否为两位小数的数值
     *
     * @param str
     * @return boolean
     */
    public static boolean isNumberRegex(String str) {
        Objects.requireNonNull(str, "请输入参数");
        // 判断小数点后2位的数字的正则表达式
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 用JAVA自带的函数判断是否位数字
     *
     * @param str
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        Objects.requireNonNull(str, "请输入参数");
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 当浮点型数据位数超过10位之后，数据变成科学计数法显示。用此方法可以使其正常显示。
     *
     * @param value
     * @return String
     * @author: https://www.bajins.com
     * @date: 2019年1月7日 下午2:27:04
     */
    public static String formatFloatNumber(Double value) {
        Objects.requireNonNull(value, "请输入参数");
        if (value.doubleValue() != 0.00) {
            DecimalFormat df = new DecimalFormat("########.00");
            return df.format(value.doubleValue());
        } else {
            return "0.00";
        }
    }

    /**
     * 使用NumberFormat把Double转百分比
     *
     * @param value需要转换的数值
     * @param maximum保留的小数位数
     * @return java.lang.String
     */
    public static String doubleToPercentage(Double value, Integer maximum) {
        Objects.requireNonNull(value, "请输入参数");
        Objects.requireNonNull(maximum, "请输入参数");
        NumberFormat percent = NumberFormat.getPercentInstance();
        if (maximum != null && maximum > 0) {
            // 保存结果到小数点后几位，特别声明：这个结果已经先*100了
            percent.setMaximumFractionDigits(maximum);
        }
        String format = percent.format(value);//自动四舍五入
        return format;
    }

    /**
     * 使用DecimalFormat把BigDecimal转百分比
     *
     * @param value   需要转换的数值
     * @param maximum 保留的小数位数
     * @return java.lang.String
     */
    public static String doubleToPercentage(BigDecimal value, Integer maximum) {
        Objects.requireNonNull(value, "请输入参数");
        Objects.requireNonNull(maximum, "请输入参数");
        DecimalFormat df = new DecimalFormat("0%");
        if (maximum != null && maximum > 0) {
            // 保存结果到小数点后几位，特别声明：这个结果已经先*100了
            df.setMaximumFractionDigits(maximum);
        }
        String format = df.format(value);//自动四舍五入
        return format;
    }

    /**
     * 使用NumberFormat把百分比转换为小数
     *
     * @param value
     * @return double
     */
    public static double percentageToDoubleNumberFormat(String value) throws ParseException {
        Objects.requireNonNull(value, "请输入参数");
        NumberFormat percent = NumberFormat.getPercentInstance();
        Number parse = percent.parse(value);
        return parse.doubleValue();
    }

    /**
     * 使用DecimalFormat把百分比转换为小数
     *
     * @param value
     * @return double
     */
    public static double percentageToDoubleDecimalFormat(String value) throws ParseException {
        Objects.requireNonNull(value, "请输入需要转换的数值");
        DecimalFormat df = new DecimalFormat("0.00");
        Number parse = df.parse(value);
        return parse.doubleValue();
    }

    public static void main(String[] args) {
        BigDecimal decimal = new BigDecimal("10");
        BigDecimal decima2 = new BigDecimal("11");
        // compareTo 相等返回0，小于返回-1，大于返回1
        int i = decimal.compareTo(decima2);

        // 相加
        BigDecimal add = decimal.add(decima2);
        // 相减
        BigDecimal subtract = decimal.subtract(decima2);
        // 相乘
        BigDecimal multiply = decimal.multiply(decima2);
        // 相除，推荐传入保留小数位数参数
        BigDecimal divide = decimal.divide(decima2, 2, RoundingMode.HALF_UP);
        BigDecimal divide1 = decimal.divide(decima2, 2, BigDecimal.ROUND_HALF_UP);
        // 绝对值
        BigDecimal abs = divide.abs();

        String money = "1.054841";
        // 四舍五入保留两位小数
        BigDecimal setScale = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
        // 不四舍五入保留两位小数
        BigDecimal setScale1 = new BigDecimal(money).setScale(2, RoundingMode.DOWN);
        // 去除末尾多余的0，并输出非科学计数法的字符串
        System.out.println(new BigDecimal("100.000").stripTrailingZeros().toPlainString());

        /**
         * DecimalFormat
         */
        double moneyD = 0.1585454545451545;
        // 自动填充用`0`，比如`new DecimalFormat("0.00")`保留两位小数
        // 不填充则用`#`，比如`new DecimalFormat("0.##")`小数有就保留没有就去掉
        DecimalFormat dFormat = new DecimalFormat("0.00");// 保留两位小数
        dFormat.setRoundingMode(RoundingMode.DOWN);// 不四舍五入
        String format = dFormat.format(moneyD);

    }

}