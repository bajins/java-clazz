package com.bajins.clazz;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author claer https://www.bajins.com
 * @program com.bajins.api.utils.wechatutil
 * @description JavaMD5
 * @create 2018-07-05 00:17
 */
public class JavaMD5 {


    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f"};

    /**
     * 转换byte到16进制
     *
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }


    /**
     * MD5编码
     *
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("JavaMD5");
        md.update(origin.getBytes("UTF-8"));
        return byteArrayToHexString(md.digest());
    }

    /**
     * 普通MD5
     *
     * @param input
     * @return java.lang.String
     */
    public static String MD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("JavaMD5");
        char[] charArray = input.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 自定义盐算法加密
     *
     * @param password
     * @return java.lang.String
     */
    public static String MD5enOne(String password) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder(16);
        sb.append(StringUtil.getStringRandom(16));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append(StringUtil.getStringRandom(1));
            }
        }
        String salt = sb.toString();
        password = md5Hex(password + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return String.valueOf(cs);
    }

    /**
     * 自定义盐算法加密校验是否和原文一致
     *
     * @param password 输入明文
     * @param md5      数据库MD5
     * @return boolean
     */
    public static boolean MD5verifyOne(String password, String md5) throws NoSuchAlgorithmException {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = String.valueOf(cs2);
        return md5Hex(password + salt).equals(String.valueOf(cs1));
    }

    /**
     * 获取十六进制字符串形式的MD5摘要
     *
     * @param src
     * @return java.lang.String
     */
    private static String md5Hex(String src) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("JavaMD5");
        byte[] bs = md5.digest(src.getBytes());
        return StringUtil.byte2Hex(bs);
    }

    //----------------------------------------------------------------------------


    /**
     * 验证指定盐长度随机加密后的口令是否合法
     *
     * @param password   密码
     * @param digest     之前加盐的加密后的字符串
     * @param saltLength 盐长度
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static boolean MD5RandomSaltVerify(String password, String digest, int saltLength) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        // 将16进制字符串格式口令转换成字节数组
        byte[] pwd = StringUtil.hexStringToByte(digest);
        // 声明盐变量
        byte[] salt = new byte[saltLength];
        // 将盐从数据库中保存的口令字节数组中提取出来
        System.arraycopy(pwd, 0, salt, 0, saltLength);
        // 创建消息摘要对象
        MessageDigest md = MessageDigest.getInstance("JavaMD5");
        // 将盐数据传入消息摘要对象
        md.update(salt);
        // 将口令的数据传给消息摘要对象
        md.update(password.getBytes(Charset.defaultCharset()));
        // 从已加密的摘要中去除盐后的值就是密码
        byte[] digestBytes = new byte[pwd.length - saltLength];
        // 取得数据库中口令的消息摘要
        System.arraycopy(pwd, saltLength, digestBytes, 0, digestBytes.length);
        // 比较根据输入口令生成的消息摘要和数据库中消息摘要是否相同
        if (Arrays.equals(md.digest(), digestBytes)) {
            // 口令正确返回口令匹配消息
            return true;
        } else {
            // 口令不正确返回口令不匹配消息
            return false;
        }
    }

    /**
     * 获得指定盐长度随机加密后的16进制形式口令
     *
     * @param password   密码
     * @param saltLength 盐长度
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String MD5RandomSalt(String password, int saltLength) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        // 随机数生成器
        SecureRandom random = new SecureRandom();
        // 声明盐数组变量
        byte[] salt = new byte[saltLength];
        // 将随机数放入盐变量中
        random.nextBytes(salt);
        // 声明消息摘要对象
        MessageDigest md = MessageDigest.getInstance("JavaMD5");
        // 将盐数据传入消息摘要对象
        md.update(salt);
        // 将口令的数据传给消息摘要对象
        md.update(password.getBytes(Charset.defaultCharset()));
        // 获得消息摘要的字节数组
        byte[] digest = md.digest();

        // 声明加密后的口令数组变量，因为要在口令的字节数组中存放盐，所以加上盐的字节长度
        byte[] pwd = new byte[digest.length + saltLength];
        // 将盐的字节拷贝到生成的加密口令字节数组，以便在验证口令时取出盐
        System.arraycopy(salt, 0, pwd, 0, saltLength);
        // 将消息摘要拷贝到加密口令字节数组，放在盐的末尾
        System.arraycopy(digest, 0, pwd, saltLength, digest.length);
        // 将字节数组格式加密后的口令转化为16进制字符串格式的口令
        return StringUtil.byteToHexString(pwd);
    }


    /**
     * @param string
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String MD5enThree(String string) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("md5");//返回实现指定摘要算法的 MessageDigest 对象。
        md.update(string.getBytes());//使用指定的 byte 数组更新摘要。
        byte[] md5Bytes = md.digest();//通过执行诸如填充之类的最终操作完成哈希计算。在调用此方法之后，摘要被重置。
        return StringUtil.bytes2Hex(md5Bytes);//本信息摘要
    }
}

