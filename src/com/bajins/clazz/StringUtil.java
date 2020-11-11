package com.bajins.clazz;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String类型的数据操作
 *
 * @program: com.zd966.file.cloud.utils
 * @description: StringUtil
 * @author:
 * @create: 2018-04-14 16:51
 */
public class StringUtil {


    /**
     * 生成随机数字和字母
     *
     * @param length 需求生成多少位
     * @return java.lang.String
     */
    public static String getStringRandom(int length) {
        String val = "";
        Random random = new Random();
        //length为几位密码
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 使用JavaRandom获取随机Int
     *
     * @param maximum 最大数字
     * @return int 返回int类型
     */
    public static int getIntRandom(int maximum) {
        Random random = new Random();
        return random.nextInt(maximum) + 1;
    }


    /**
     * 利用JDK的Arrays类将逗号分隔的字符串转换为List
     *
     * @param str
     * @return
     */
    public static List<String> stringToListJDK(String str) {
        return Arrays.asList(str.split(","));
    }


    /**
     * 判断字符串是否为空
     * 等同于org.apache.commons.lang3.StringUtils.isBlank
     *
     * @param str
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        String trim = str.trim();
        if (trim.equals("") || trim.isEmpty() || trim.length() <= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 等同于org.apache.commons.lang3.StringUtils.isNotBlank
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Java正则Mathcer查找指定字符在字符串中的位置
     *
     * @param strs 字符串
     * @param str  字符
     * @return int
     */
    public static int searchIndex(String strs, String str, int index) {
        //这里是通过Java正则Mathcer获取指定字符的位置
        Matcher slashMatcher = Pattern.compile(str).matcher(strs);
        int mIdx = 0;
        //开始搜索
        while (slashMatcher.find()) {
            mIdx++;
            //当"/"符号第k次出现的位置
            int N = index;
            if (mIdx == N) {
                break;
            }
        }
        return slashMatcher.start();
    }

    /**
     * 在一段字符中，找出一个字的第几次出现位置
     *
     * @param str    字符串
     * @param letter 字符
     * @param num    位置
     * @return int
     */
    public int findNumber(String str, String letter, int num) {
        int i = 0;
        int m = 0;
        char c = new String(letter).charAt(0);
        char[] ch = str.toCharArray();
        for (int j = 0; j < ch.length; j++) {
            if (ch[j] == c) {
                i++;
                if (i == num) {
                    m = j;
                    break;
                }
            }
        }
        return m;
    }

    /**
     * 字符串去重
     *
     * @param s
     * @return
     */
    public static String removeRepeatedChar(String s) {
        Objects.requireNonNull(s);
        StringBuilder sb = new StringBuilder();
        int i = 0, len = s.length();
        while (i < len) {
            char c = s.charAt(i);
            sb.append(c);
            i++;
            // 这个是如果这两个值相等，就让i+1取下一个元素
            while (i < len && s.charAt(i) == c) {
                i++;
            }
        }
        return sb.toString();
    }

    /**
     * 字符串数组split后用HashSet特性去重
     *
     * @param s
     * @return
     */
    public static String removeCharArray(String s) {
        Objects.requireNonNull(s);
        String[] arr = s.split(",");
        // 实例化一个set集合
        Set<String> set = new HashSet<String>();
        // 遍历数组并存入集合,如果元素已存在则不会重复存入
        for (int i = 0; i < arr.length; i++) {
            set.add(arr[i]);
        }
        // 返回Set集合的数组形式
        return String.join(",", set);
    }

    /**
     * 字符串替换中间几位
     *
     * @param str     原始字符
     * @param replace 替换字符
     * @param before  开头保留字符位数
     * @param after   末尾保留字符位数
     * @return
     */
    public String replaceString(String str, String replace, int before, int after) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (i < before || i >= (str.length() - after)) {
                sb.append(str.charAt(i));
            } else {
                sb.append(replace);
            }
        }
        return sb.toString();
    }

    /**
     * 通过身份证号码获取出生日期、性别、年龄
     *
     * @param idCard
     * @return 返回的出生日期格式：1990-01-01 性别格式：F-女，M-男
     */
    public static Map<String, String> getBirAgeSex(String idCard) {
        String birthday = "";
        String age = "";
        String sexCode = "";

        int year = Calendar.getInstance().get(Calendar.YEAR);
        char[] number = idCard.toCharArray();
        boolean flag = true;
        // 当身份证号是15位时
        if (number.length == 15) {
            for (int x = 0; x < number.length; x++) {
                if (!flag) {
                    return new HashMap<String, String>();
                }
                // 判断身份证是否为数字
                flag = Character.isDigit(number[x]);
            }
            // 当身份证号是18位时
        } else if (number.length == 18) {
            for (int x = 0; x < number.length - 1; x++) {
                if (!flag) {
                    return new HashMap<String, String>();
                }
                flag = Character.isDigit(number[x]);
            }
        }
        if (flag && idCard.length() == 15) {
            birthday = "19" + idCard.substring(6, 8) + "-" + idCard.substring(8, 10) + "-" + idCard.substring(10, 12);
            sexCode = Integer.parseInt(idCard.substring(idCard.length() - 3)) % 2 == 0 ? "F" : "M";
            age = (year - Integer.parseInt("19" + idCard.substring(6, 8))) + "";
        } else if (flag && idCard.length() == 18) {
            birthday = idCard.substring(6, 10) + "-" + idCard.substring(10, 12) + "-" + idCard.substring(12, 14);
            sexCode = Integer.parseInt(idCard.substring(idCard.length() - 4, idCard.length() - 1)) % 2 == 0 ? "F" : "M";
            age = (year - Integer.parseInt(idCard.substring(6, 10))) + "";
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("birthday", birthday);
        map.put("age", age);
        map.put("sexCode", sexCode);
        return map;
    }


    /**
     * 子字符串modelStr在字符串str中第count次出现时的下标
     *
     * @param str      字符串
     * @param modelStr 子串
     * @param count    第几次出现的下标
     * @return int
     */
    public static int getFromIndex(String str, String modelStr, Integer count) {
        // 对子字符串进行匹配
        Matcher slashMatcher = Pattern.compile(modelStr).matcher(str);
        int index = 0;
        // matcher.find();尝试查找与该模式匹配的输入序列的下一个子序列
        while (slashMatcher.find()) {
            index++;
            // 当modelStr字符第count次出现的位置
            if (index == count) {
                break;
            }
        }
        // matcher.start();返回以前匹配的初始索引。
        return slashMatcher.start();
    }


    /**
     * 用indexOf方法从指定下标查找并统计子串出现在字符串中的次数
     *
     * @param srcStr
     * @param findStr
     * @return int
     */
    public static int indexCount(String srcStr, String findStr) {
        int count = 0;
        int index = 0;
        /*
         * indexOf()的用法：返回字符中indexOf（String）中子串String在父串中首次出现的位置，从0开始！没有返回-1
         * 方便判断和截取字符串！
         */
        while ((index = srcStr.indexOf(findStr, index)) != -1) {// 如果key在str中存在
            index = index + findStr.length();
            count++;// 找到一次统计一次
        }
        return count;
    }

    /**
     * 使用indexOf和subString方法，循环判断并截取并统计子串出现在字符串中的次数
     *
     * @param srcStr
     * @param findStr
     * @return int
     */
    public static int subCount(String srcStr, String findStr) {
        int count = 0;
        int index = 0;
        while ((index = srcStr.indexOf(findStr, index)) != -1) {// 如果key在str中存在
            srcStr = srcStr.substring(srcStr.indexOf(findStr) + findStr.length());
            count++;
        }

        return count;
    }

    /**
     * 使用replace方法将字符串替换为空，然后求长度并统计子串出现在字符串中的次数
     *
     * @param srcStr
     * @param findStr
     * @return int
     */
    public static int replaceCount(String srcStr, String findStr) {
        int count = (srcStr.length() - srcStr.replace(findStr, "").length()) / findStr.length();
        return count;
    }

    /**
     * 将指定byte数组转换成二进制字符串
     * <p>
     * 补充：
     * 为了显示一个byte型的单字节十六进制(两位十六进制表示)的编码，请使用：
     * Integer.toHexString((byteVar & 0x000000FF) | 0xFFFFFF00).substring(6);
     * byteVar & 0x000000FF的作用是，如果byteVar 是负数，则会清除前面24个零，正的byte整型不受影响。
     * (...) | 0xFFFFFF00的作用是，如果byteVar 是正数，则置前24位为一，
     * 这样toHexString输出一个小于等于15的byte整型的十六进制时，倒数第二位为零且不会被丢弃，
     * 这样可以通过substring方法进行截取最后两位即可。
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuilder StringBuilder = new StringBuilder();
        String temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                StringBuilder.append("0");
            }
            StringBuilder.append(temp);
        }
        return StringBuilder.toString();
    }

    /**
     * 将指定byte数组转换成二进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytes2Hex(byte[] bytes) {
        StringBuilder strBuf = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] >= 0 && bytes[i] < 16) {
                strBuf.append("0");
            }
            strBuf.append(Integer.toHexString(bytes[i] & 0xFF));
        }
        return strBuf.toString();
    }

    /**
     * 十六进制字符串转二进制字符串
     *
     * @param hexadecimal 十六进制字符串
     * @return 十六进制字符串解码为字节数组
     */
    public static byte[] hexToBinary(String hexadecimal) {
        byte[] binary = new byte[hexadecimal.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hexadecimal.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    /**
     * 二进制字符串转十六进制字符串
     *
     * @param array 要转换的字节数组
     * @return length*2 编码字节数组的字符串
     */
    public static String binaryToHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        String hexNumsStr = "0123456789ABCDEF";
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (hexNumsStr.indexOf(hexChars[pos]) << 4 | hexNumsStr.indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     * 字符串判断编码并转换
     *
     * @param str
     * @return 返回转换后的字符串
     * @throws UnsupportedEncodingException
     */
    public static String getEncoding(String str) throws UnsupportedEncodingException {
        // str.equals(new String(str.getBytes(encode), encode))
        if (Charset.forName("ISO-8859-1").newEncoder().canEncode(str)) {
            return new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } else if (Charset.forName("UTF-8").newEncoder().canEncode(str)) {
            return str;
        } else if (Charset.forName("GBK").newEncoder().canEncode(str)) {
            return new String(str.getBytes("GBK"), "UTF-8");
        } else if (Charset.forName("UNICODE").newEncoder().canEncode(str)) {
            return new String(str.getBytes("UNICODE"), "UTF-8");
        }
        return str;
    }

    public static void main(String[] args) {
        String[] addr = {"北京", "南京", "重庆", "西安"};
        // 数组转List
        List<String> list = Arrays.asList(addr);

        char[] charArray = {'a', 'b', 'c'};
        int[] intArray = {1, 2, 3, 4};

        System.out.println("------------- 固定分隔符 -------------");

        SystemLearning.println("CharArray转String：", String.copyValueOf(charArray));

        // 必须将普通数组 boxed才能在 map 里面 toString
        String str2 = Arrays.stream(intArray).boxed().map(i -> i.toString()).reduce("", String::concat);
        SystemLearning.println("IntArray转String：", str2);

        // // 必须将普通数组 boxed才能在 map 里面引用 Object::toString
        String str3 = Arrays.stream(intArray).boxed().map(Object::toString).reduce("", String::concat);
        SystemLearning.println("IntArray转String：", str3);


        System.out.println("------------- 可自定义分隔符 -------------");

        // 必须将普通数组 boxed才能在 map 里面 toString
        String str1 = Arrays.stream(intArray).boxed().map(i -> i.toString()).collect(Collectors.joining(","));
        SystemLearning.println("IntArray转String：", str1);

        SystemLearning.println("StringArray转String：", Arrays.stream(addr).collect(Collectors.joining(",")));
        SystemLearning.println("StringArray转String：", Arrays.toString(addr));

        SystemLearning.println("Array转String：", String.join(",", addr));
        SystemLearning.println("List转String：", String.join(",", list));
        SystemLearning.println("List转String：", list.stream().collect(Collectors.joining(",")));

        String str = "abcdefg";
        String childStr = "c";
        // 获取子字符串在字符串中倒数第二次出现的下标
        int i = str.lastIndexOf(childStr, str.lastIndexOf(childStr) + 1);

        // 获取子字符串在字符串中第二次出现的下标
        int i1 = str.indexOf(childStr, str.indexOf(childStr) + 1);

        // 使用 java.util.concurrent.ThreadLocalRandom 来生成有边界的Int，专为多线程并发使用的随机数生成器
        int i2 = ThreadLocalRandom.current().nextInt(1, 10);


        System.out.println("==================== 测试拼接效率 ====================");


        String clazz = "class";
        String method = "method";

        long start1 = System.nanoTime();
        String join = String.join(".", clazz, method);
        long end1 = System.nanoTime();
        SystemLearning.println("String.join耗时:", end1 - start1);

        long start2 = System.nanoTime();
        String add = clazz + "." + method;
        long end2 = System.nanoTime();
        SystemLearning.println("使用+号耗时:", end2 - start2);

        long start3 = System.nanoTime();
        StringBuilder stringBuilder = new StringBuilder(clazz);
        stringBuilder.append(".");
        stringBuilder.append(method);
        String s1 = stringBuilder.toString();
        long end3 = System.nanoTime();
        SystemLearning.println("StringBuilder耗时:", end3 - start3);

        long start4 = System.nanoTime();
        StringBuffer stringBuffer = new StringBuffer(clazz);
        stringBuffer.append(".");
        stringBuffer.append(method);
        String s = stringBuffer.toString();
        long end4 = System.nanoTime();
        SystemLearning.println("StringBuffer耗时:", end4 - start4);

        long start5 = System.nanoTime();
        String format = String.format("%s.%s", clazz, method);
        long end5 = System.nanoTime();
        SystemLearning.println("String.format %s.%s耗时:", end5 - start5);

        long start6 = System.nanoTime();
        String format1 = String.format("%1$s.%2$s", clazz, method);
        long end6 = System.nanoTime();
        SystemLearning.println("String.format %1$s.%2$s耗时:", end6 - start6);

    }

}
