package com.bajins.clazz;


import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @see NumberFormat
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
     * https://blog.csdn.net/qq_44750696/article/details/121230073
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
     * @param value   需要转换的数值
     * @param maximum 保留的小数位数
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

    /**
     * 流水号补位
     *
     * @param i      数值
     * @param length 长度
     * @return 补位后的流水号字符串
     */
    public static String fillSerialNumber(int i, int length) {
        // 自动填充用`0`，比如`new DecimalFormat("0.00")`保留两位小数
        // 不填充则用`#`，比如`new DecimalFormat("0.##")`小数有就保留没有就去掉
        DecimalFormat dFormat = new DecimalFormat("0.##");// 保留两位小数
        dFormat.setGroupingUsed(false); // 设置是否使用分组
        //dFormat.setMaximumFractionDigits(length); // 设置最大小数位数
        //dFormat.setMinimumFractionDigits(length);
        dFormat.setMaximumIntegerDigits(length); // 设置最大整数位数
        dFormat.setMinimumIntegerDigits(length);
        dFormat.setRoundingMode(RoundingMode.DOWN);// 不四舍五入
        return dFormat.format(i);

        /*NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(false); // 设置是否使用分组
        nf.setMaximumIntegerDigits(length); // 设置最大整数位数
        nf.setMinimumIntegerDigits(length); // 设置最小整数位数
        return nf.format(i);*/
        //return String.format("%0" + length + "d", i);
    }


    /**
     * 校验字符串连续多少位是纯数字或者纯字母，默认6位(字母区分大小写)
     * <p/>
     * 实现思路：统一转成ASCII进行计数判断，纯数字、纯字母<br/>
     * 纯数字(数字0 -- 数字9,对应ASCII为48 -- 57)<br/>
     * 大写纯字母(大写字母A -- 大写字母Z,对应ASCII为65 -- 90)<br/>
     * 小写纯字母(小写字母a -- 小写字母z，对应ASCII为97 -- 122)<br/>
     *
     * @param value  需要校验的值
     * @param length 校验长度,默认6位
     */
    public static boolean simpleLetterAndNumCheck(String value, int length) {
        int i = 0;
        //计数器
        int counter = 1;
        //
        while (i < value.length() - 1) {
            //当前ascii值
            int currentAscii = value.charAt(i);
            //下一个ascii值
            int nextAscii = value.charAt(i + 1);
            //满足区间进行判断
            if ((rangeInDefined(currentAscii, 48, 57) || rangeInDefined(currentAscii, 65, 90) || rangeInDefined(currentAscii, 97, 122))
                    && (rangeInDefined(nextAscii, 48, 57) || rangeInDefined(nextAscii, 65, 90) || rangeInDefined(nextAscii, 97, 122))) {
                //计算两数之间差一位则为连续
                if (Math.abs((nextAscii - currentAscii)) == 1) {
                    //计数器++
                    counter++;
                } else {
                    //否则计数器重新计数
                    counter = 1;
                }
            }
            //满足连续数字或者字母
            if (counter >= length)
                return true;
            //
            i++;
        }
        return false;
    }

    /**
     * 判断一个数字是否在某个区间
     *
     * @param current 当前比对值
     * @param min     最小范围值
     * @param max     最大范围值
     */
    public static boolean rangeInDefined(int current, int min, int max) {
        //
        return Math.max(min, current) == Math.min(current, max);
    }

    public static void main(String[] args) {
        // BigInteger https://blog.csdn.net/m0_37602827/article/details/102547542
        BigDecimal decimal = new BigDecimal("10");
        BigDecimal decima2 = new BigDecimal("11");
        // compareTo 相等返回0，小于返回-1，大于返回1
        int i = decimal.compareTo(decima2);

        // 加
        BigDecimal add = decimal.add(decima2);
        // 减
        BigDecimal subtract = decimal.subtract(decima2);
        // 乘
        BigDecimal multiply = decimal.multiply(decima2);
        // 除，推荐传入保留小数位数参数
        BigDecimal divide = decimal.divide(decima2, 2, RoundingMode.HALF_UP);
        BigDecimal divide1 = decimal.divide(decima2, 2, BigDecimal.ROUND_HALF_UP);
        // 乘方
        BigDecimal pow = divide.pow(2);
        // 绝对值：负数变为正数
        BigDecimal abs = divide.abs();
        // 取反
        BigDecimal negate = divide.negate();
        // 1正数，0就是0，-1负数
        int signum = divide.signum();

        String money = "1.054841";
        // 四舍五入保留两位小数
        BigDecimal setScale = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
        // 不四舍五入保留两位小数
        BigDecimal setScale1 = new BigDecimal(money).setScale(2, RoundingMode.DOWN);
        // 去除末尾多余的0，并输出非科学计数法的字符串
        System.out.println(new BigDecimal("100.000").stripTrailingZeros().toPlainString());

        // 3个toString方法的区别
        BigDecimal number = new BigDecimal("1E11");
        // 有必要时使用工程计数法。工程记数法是一种工程计算中经常使用的记录数字的方法，与科学计数法类似，但要求10的幂必须是3的倍数
        System.out.println(number.toEngineeringString());
        // 不使用任何指数（永不使用科学计数法）
        System.out.println(number.toPlainString());
        // 有必要时使用科学计数法
        System.out.println(number.toString());

        // 去掉小数点后无用的0
        BigDecimal bigDecimal = new BigDecimal("3000.000000");
        System.out.println(bigDecimal.stripTrailingZeros().toPlainString());

    }

}