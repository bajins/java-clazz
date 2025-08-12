package com.bajins.clazz;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 将数字金额转为大写中文金额，支持到两位小数，最高支持万亿元。
 * 参考：https://leishen6.github.io/2020/02/09/Java_imp_chinease_monery
 *
 * @author bajins
 * @program com.bajins.api.utils
 * @description CnNumberUtil
 * @create 2018-11-23 21:59
 */
public class NumberCn {

    // 阿拉伯数字对应大写中文数字
    // 大写中文金额数字对应表
    // , "拾", "佰", "仟", "万", "亿", "兆", "京", "垓", "秭", "穰", "沟", "涧", "正", "载", "极", "恒河沙", "阿僧祇", "那由多", "不可思議", "无量大数"
    private final static String[] CN_NUMERALS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    // 低单位
    private final static String[] CN_NUM_UNIT = {"", "拾", "佰", "仟"};
    // 万元以上高单位
    private final static String[] CN_NUM_HIGHT_UNIT = {"", "万", "亿", "万亿", "兆", "京", "垓", "秭", "穰", "沟", "涧", "正", "载", "极", "恒河沙", "阿僧祇", "那由多", "不可思議", "无量大数"};
    // 小数位单位
    private final static String[] CN_NUM_DECIMAL_UNIT = {"", "角", "分", "厘", "毫", "微", "纳", "沙", "皮", "卡", "盎", "铢", "穰", "秭", "垓", "京", "垭", "杼", "穣", "溝", "澗", "正", "載", "極", "恒河沙", "阿僧祇", "那由多", "不可思議", "無量大数"};

    /**
     * 转换成中文大写金额（最高支持万亿和两位小数）
     *
     * @param num
     * @return
     */
    public static String toUppercase(Double num) {
        return toUppercase(String.valueOf(num));
    }

    /**
     * 转换成中文大写金额（最高支持万亿和两位小数）
     *
     * @param num
     * @return
     */
    public static String toUppercase(String num) {
        //判断输入的金额字符串是否符合要求
        if (!num.matches("(-)?[\\d]*(.)?[\\d]*")) {
            throw new IllegalArgumentException("抱歉，请输入数字！");
        }
        if ("0".equals(num) || "0.00".equals(num) || "0.0".equals(num)) {
            return "零圆";
        }
        num = num.replaceAll(",", "");//去掉","
        //判断是否存在负号"-"
        boolean flag = false;
        if (num.startsWith("-")) {
            flag = true;
            num = num.replaceAll("-", "");
        }
        String[] strNumArray = num.split("\\.");
        int strNumArrayLength = strNumArray.length;
        BigDecimal bigDecimal = new BigDecimal(strNumArray[0]);
        BigInteger bigInteger = bigDecimal.toBigInteger();

        char[] chars = bigInteger.toString().toCharArray();
        int intLength = chars.length;

        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < intLength; i++) {
            String curCnNum = CN_NUMERALS[charNumToInt(chars[i])];
            int ui = ((intLength - 1) - i) % 4;
            int hui = ((intLength - 1) - i) / 4;
            strBuilder.append(curCnNum);
            if (!isZero(curCnNum)) {
                strBuilder.append(CN_NUM_UNIT[ui]);
                String heightUnit = CN_NUM_HIGHT_UNIT[hui];
                // 如果剩余没有低金额单位或者后续都为零则加上高单位
                if (ui == 0 || isZeroInResidue(chars, i, ui)) {
                    strBuilder.append(heightUnit);
                    // 万单位以后的千单位如果不能为零则跳过零
                    if (heightUnit.equals(CN_NUM_HIGHT_UNIT[1]) && !isZeroOfIndex(chars, (ui + 1) + i)) {
                        i = i + skipZero(chars, i);
                    }
                }
            }
        }
        clearZero(0, strBuilder);
        // 小数位转换
        strBuilder.append("圆");
        if (strNumArrayLength == 1 || strNumArray[1].matches("^[0]*$")) {
            strBuilder.append("整");
            return strBuilder.toString();
        }
        char[] decimalChars = strNumArray[1].toCharArray();
        int decimalCharLength = decimalChars.length;
        for (int i = 1; i <= decimalCharLength; i++) {
            String cnNum = CN_NUMERALS[charNumToInt(decimalChars[i - 1])];
            if (!isZero(cnNum)) {
                strBuilder.append(cnNum).append(CN_NUM_DECIMAL_UNIT[i]);
            } else if (i == decimalCharLength) {
                return strBuilder.toString();
            } else {
                strBuilder.append(cnNum);
            }
        }
        if (flag) {
            //如果是负数，加上"负"
            strBuilder.insert(0, "负");
        }
        return strBuilder.toString();
    }

    /**
     * 递归清除多余的零
     *
     * @param startIndex
     * @param strBuilder
     */
    private static void clearZero(int startIndex, StringBuilder strBuilder) {
        int strLength = strBuilder.length();
        int zeroIndex = strBuilder.indexOf(CN_NUMERALS[0], startIndex);
        if (zeroIndex <= -1) {
            return;
        }
        if (strLength <= zeroIndex + 1) {
            strBuilder.deleteCharAt(zeroIndex);
            return;
        }
        if (zeroIndex > -1 && isZero(strBuilder.charAt(zeroIndex + 1))) {
            strBuilder.deleteCharAt(zeroIndex);
            clearZero(startIndex, strBuilder);
            return;
        }
        if (strLength > zeroIndex + 1) {
            clearZero(zeroIndex + 1, strBuilder);
            return;
        }
    }

    private static boolean isZero(String cnNum) {
        return cnNum.equals(CN_NUMERALS[0]);
    }

    private static boolean isZero(char cnNum) {
        return isZero(String.valueOf(cnNum));
    }

    /**
     * 跳过为零的下标
     *
     * @param chars
     * @param beginIndex
     * @return
     */
    private static int skipZero(char[] chars, int beginIndex) {
        int newIndex = 0;
        int charLength = chars.length;
        for (int i = beginIndex + 1; i < charLength; i++) {
            if (isZero(CN_NUMERALS[charNumToInt(chars[i])])) {
                newIndex++;
            } else {
                break;
            }
        }
        return newIndex;
    }

    /**
     * 指定下标值是否是为零
     *
     * @param chars
     * @param index
     * @return boolean
     */
    private static boolean isZeroOfIndex(char[] chars, int index) {
        int numInex = Integer.parseInt(String.valueOf(chars[index]), 10);
        String cnNum = CN_NUMERALS[numInex];
        return isZero(cnNum);
    }

    /**
     * 从begin开始至end结束是否都是零
     *
     * @param chars
     * @param begin
     * @param end
     * @return
     */
    private static boolean isZeroInResidue(char[] chars, int begin, int end) {
        for (int i = 1; i <= end; i++) {
            boolean r = isZeroOfIndex(chars, begin + i);
            if (!r) {
                return false;
            }
        }
        return true;
    }

    /**
     * char类型数值转换为int
     *
     * @param charNum
     * @return
     */
    private static int charNumToInt(char charNum) {
        String ns = String.valueOf(charNum);
        return Integer.parseInt(ns, 10);
    }

    public static void main(String[] args) {
        System.out.println(toUppercase("4567989123456677975434789.0156789"));
        String number = "12.56";
        System.out.println(number + ": " + toUppercase(number));

        number = "1234567890563886.123";
        System.out.println(number + ": " + toUppercase(number));

        number = "1600";
        System.out.println(number + ": " + toUppercase(number));

        number = "156,0";
        System.out.println(number + ": " + toUppercase(number));

        number = "-156,0";
        System.out.println(number + ": " + toUppercase(number));

        number = "0.12";
        System.out.println(number + ": " + toUppercase(number));

        number = "0.0";
        System.out.println(number + ": " + toUppercase(number));

        number = "01.12";
        System.out.println(number + ": " + toUppercase(number));

        number = "0125";
        System.out.println(number + ": " + toUppercase(number));

        number = "-0125";
        System.out.println(number + ": " + toUppercase(number));
    }
}