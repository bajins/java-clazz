package com.bajins.clazz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 编解码工具
 *
 * @author claer https://www.bajins.com
 * @program com.bajins.api.utils.encrypt
 * @create 2019-05-28 16:51
 */
public class CodecUtil {

    private static final String DEFAULT_URL_ENCODING = StandardCharsets.UTF_8.name();

    // 单个字符的正则表达式
    private static final String singlePattern = "[0-9|a-f|A-F]";
    // 4个字符的正则表达式
    private static final String pattern = singlePattern + singlePattern + singlePattern + singlePattern;


    /**
     * 把 \\u 开头的单字转成汉字，如 \\u6B65 ->　步
     *
     * @param str
     * @return java.lang.String
     */
    private static String ustartToCn(final String str) {
        StringBuilder sb = new StringBuilder().append("0x")
                .append(str, 2, 6);
        Integer codeInteger = Integer.decode(sb.toString());
        int code = codeInteger.intValue();
        char c = (char) code;
        return String.valueOf(c);
    }

    /**
     * 字符串是否以Unicode字符开头。约定Unicode字符以 \\u开头。
     *
     * @param str 字符串
     * @return boolean true表示以Unicode字符开头.
     */
    private static boolean isStartWithUnicode(final String str) {
        if (null == str || str.length() == 0) {
            return false;
        }
        if (!str.startsWith("\\u")) {
            return false;
        }
        // \u6B65
        if (str.length() < 6) {
            return false;
        }
        String content = str.substring(2, 6);
        return Pattern.matches(pattern, content);
    }

    /**
     * 字符串中，所有以 \\u 开头的UNICODE字符串，全部替换成汉字
     *
     * @param str
     * @return java.lang.String
     */
    public static String unicodeToCn(final String str) {
        // 用于构建新的字符串
        StringBuilder sb = new StringBuilder();
        // 从左向右扫描字符串。tmpStr是还没有被扫描的剩余字符串。
        // 下面有两个判断分支：
        // 1. 如果剩余字符串是Unicode字符开头，就把Unicode转换成汉字，加到StringBuilder中。然后跳过这个Unicode字符。
        // 2.反之， 如果剩余字符串不是Unicode字符开头，把普通字符加入StringBuilder，向右跳过1.
        int length = str.length();
        for (int i = 0; i < length; ) {
            String tmpStr = str.substring(i);
            if (isStartWithUnicode(tmpStr)) { // 分支1
                sb.append(ustartToCn(tmpStr));
                i += 6;
            } else { // 分支2
                sb.append(str, i, i + 1);
                i++;
            }
        }
        return sb.toString();
    }

    /**
     * 获取字符串的unicode编码
     * 汉字“木”的Unicode 码点为Ox6728
     *
     * @param s 中文
     * @return java.lang.String \ufeff\u6728  \ufeff控制字符 用来表示「字节次序标记（Byte Order Mark）」不占用宽度
     * 在java中一个char是采用unicode存储的 占用2个字节 比如 汉字木 就是 Ox6728 4bit+4bit+4bit+4bit=2字节
     */
    public static String stringToUnicode(String s) throws UnsupportedEncodingException {
        StringBuilder out = new StringBuilder();
        //直接获取字符串的unicode二进制
        byte[] bytes = s.getBytes("unicode");
        //然后将其byte转换成对应的16进制表示即可
        for (int i = 0; i < bytes.length - 1; i += 2) {
            out.append("\\u");
            String str = Integer.toHexString(bytes[i + 1] & 0xff);
            for (int j = str.length(); j < 2; j++) {
                out.append("0");
            }
            String str1 = Integer.toHexString(bytes[i] & 0xff);
            out.append(str1);
            out.append(str);
        }
        return out.toString();
    }

    /**
     * Unicode转 汉字字符串
     *
     * @param str \u6728
     * @return java.lang.String '木' 26408
     */
    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            //group 6728
            String group = matcher.group(2);
            //ch:'木' 26408
            ch = (char) Integer.parseInt(group, 16);
            //group1 \u6728
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }

    /**
     * 汉字 转换为对应的 UTF-8编码
     *
     * @param s
     * @return java.lang.String E69CA8
     */
    public static String convertStringToUTF8(String s) throws UnsupportedEncodingException {
        if (s == null || s.equals("")) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
                continue;
            }
            byte[] b;
            b = Character.toString(c).getBytes(DEFAULT_URL_ENCODING);
            for (int j = 0; j < b.length; j++) {
                int k = b[j];
                // 转换为unsigned integer  无符号integer
                /*if (k < 0) {
                    k += 256;
                }*/
                k = k < 0 ? k + 256 : k;
                //返回整数参数的字符串表示形式 作为十六进制（base16）中的无符号整数
                //该值以十六进制（base16）转换为ASCII数字的字符串
                sb.append(Integer.toHexString(k).toUpperCase());

                // url转置形式
                // sb.append("%" +Integer.toHexString(k).toUpperCase());
            }

        }
        return sb.toString();
    }

    /**
     * UTF-8编码 转换为对应的 汉字
     *
     * @param s E69CA8
     * @return java.lang.String
     */
    public static String convertUTF8ToString(String s) throws UnsupportedEncodingException {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.toUpperCase();
        int total = s.length() / 2;
        //标识字节长度
        int pos = 0;
        byte[] buffer = new byte[total];
        for (int i = 0; i < total; i++) {
            int start = i * 2;
            // 将字符串参数解析为第二个参数指定的基数中的有符号整数。
            buffer[i] = (byte) Integer.parseInt(s.substring(start, start + 2), 16);
            pos++;
        }
        // 通过使用指定的字符集解码指定的字节子阵列来构造一个新的字符串。
        // 新字符串的长度是字符集的函数，因此可能不等于子数组的长度。
        return new String(buffer, 0, pos, DEFAULT_URL_ENCODING);
    }

    public static void main(String[] args) {
        try {
            // URL 编码, Encode默认为UTF-8.
            String part = URLEncoder.encode("part", StandardCharsets.UTF_8.name());
            // URL 解码, Encode默认为UTF-8.
            String decode = URLDecoder.decode(part, StandardCharsets.UTF_8.name());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

}