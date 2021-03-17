package com.bajins.clazz;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author claer https://www.bajins.com
 * @program com.bajins.api.utils.wechatutil
 * @description SHA
 * @create 2018-07-05 00:15
 */
public class JavaSHA {


    public static String getSHA1HexString(String str) throws Exception {
        // SHA1签名生成
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(str.getBytes());
        byte[] digest = md.digest();

        StringBuilder hexstr = new StringBuilder();
        String shaHex = "";
        for (int i = 0; i < digest.length; i++) {
            shaHex = Integer.toHexString(digest[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexstr.append(0);
            }
            hexstr.append(shaHex);
        }
        return hexstr.toString();
    }


    private final int[] abcde = {0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476, 0xc3d2e1f0};
    // 摘要数据存储数组
    private int[] digestInt = new int[5];
    // 计算过程中的临时数据存储数组
    private int[] tmpData = new int[80];

    // 计算sha-1摘要
    private int process_input_bytes(byte[] bytedata) {
        // 初试化常量
        System.arraycopy(abcde, 0, digestInt, 0, abcde.length);
        // 格式化输入字节数组，补10及长度数据
        byte[] newbyte = byteArrayFormatData(bytedata);
        // 获取数据摘要计算的数据单元个数
        int MCount = newbyte.length / 64;
        // 循环对每个数据单元进行摘要计算
        for (int pos = 0; pos < MCount; pos++) {
            // 将每个单元的数据转换成16个整型数据，并保存到tmpData的前16个数组元素中
            for (int j = 0; j < 16; j++) {
                tmpData[j] = byteArrayToInt(newbyte, (pos * 64) + (j * 4));
            }
            // 摘要计算函数
            encrypt();
        }
        return 20;
    }

    /**
     * 格式化输入字节数组格式
     *
     * @param bytedata
     * @return
     */
    private byte[] byteArrayFormatData(byte[] bytedata) {
        // 补0数量
        int zeros = 0;
        // 补位后总位数
        int size = 0;
        // 原始数据长度
        int n = bytedata.length;
        // 模64后的剩余位数
        int m = n % 64;
        // 计算添加0的个数以及添加10后的总长度
        if (m < 56) {
            zeros = 55 - m;
            size = n - m + 64;
        } else if (m == 56) {
            zeros = 63;
            size = n + 8 + 64;
        } else {
            zeros = 63 - m + 56;
            size = (n + 64) - m + 64;
        }
        // 补位后生成的新数组内容
        byte[] newbyte = new byte[size];
        // 复制数组的前面部分
        System.arraycopy(bytedata, 0, newbyte, 0, n);
        // 获得数组Append数据元素的位置
        int l = n;
        // 补1操作
        newbyte[l++] = (byte) 0x80;
        // 补0操作
        for (int i = 0; i < zeros; i++) {
            newbyte[l++] = (byte) 0x00;
        }
        // 计算数据长度，补数据长度位共8字节，长整型
        long N = (long) n * 8;
        byte h8 = (byte) (N & 0xFF);
        byte h7 = (byte) ((N >> 8) & 0xFF);
        byte h6 = (byte) ((N >> 16) & 0xFF);
        byte h5 = (byte) ((N >> 24) & 0xFF);
        byte h4 = (byte) ((N >> 32) & 0xFF);
        byte h3 = (byte) ((N >> 40) & 0xFF);
        byte h2 = (byte) ((N >> 48) & 0xFF);
        byte h1 = (byte) (N >> 56);
        newbyte[l++] = h1;
        newbyte[l++] = h2;
        newbyte[l++] = h3;
        newbyte[l++] = h4;
        newbyte[l++] = h5;
        newbyte[l++] = h6;
        newbyte[l++] = h7;
        newbyte[l++] = h8;
        return newbyte;
    }

    private int f1(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    private int f2(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private int f3(int x, int y, int z) {
        return (x & y) | (x & z) | (y & z);
    }

    private int f4(int x, int y) {
        return (x << y) | x >>> (32 - y);
    }

    // 单元摘要计算函数
    private void encrypt() {
        for (int i = 16; i <= 79; i++) {
            tmpData[i] = f4(tmpData[i - 3] ^ tmpData[i - 8] ^ tmpData[i - 14] ^
                    tmpData[i - 16], 1);
        }
        int[] tmpabcde = new int[5];
        for (int i1 = 0; i1 < tmpabcde.length; i1++) {
            tmpabcde[i1] = digestInt[i1];
        }
        for (int j = 0; j <= 19; j++) {
            int tmp = f4(tmpabcde[0], 5) +
                    f1(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] +
                    tmpData[j] + 0x5a827999;
            tmpabcde[4] = tmpabcde[3];
            tmpabcde[3] = tmpabcde[2];
            tmpabcde[2] = f4(tmpabcde[1], 30);
            tmpabcde[1] = tmpabcde[0];
            tmpabcde[0] = tmp;
        }
        for (int k = 20; k <= 39; k++) {
            int tmp = f4(tmpabcde[0], 5) +
                    f2(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] +
                    tmpData[k] + 0x6ed9eba1;
            tmpabcde[4] = tmpabcde[3];
            tmpabcde[3] = tmpabcde[2];
            tmpabcde[2] = f4(tmpabcde[1], 30);
            tmpabcde[1] = tmpabcde[0];
            tmpabcde[0] = tmp;
        }
        for (int l = 40; l <= 59; l++) {
            int tmp = f4(tmpabcde[0], 5) +
                    f3(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] +
                    tmpData[l] + 0x8f1bbcdc;
            tmpabcde[4] = tmpabcde[3];
            tmpabcde[3] = tmpabcde[2];
            tmpabcde[2] = f4(tmpabcde[1], 30);
            tmpabcde[1] = tmpabcde[0];
            tmpabcde[0] = tmp;
        }
        for (int m = 60; m <= 79; m++) {
            int tmp = f4(tmpabcde[0], 5) +
                    f2(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] +
                    tmpData[m] + 0xca62c1d6;
            tmpabcde[4] = tmpabcde[3];
            tmpabcde[3] = tmpabcde[2];
            tmpabcde[2] = f4(tmpabcde[1], 30);
            tmpabcde[1] = tmpabcde[0];
            tmpabcde[0] = tmp;
        }
        for (int i2 = 0; i2 < tmpabcde.length; i2++) {
            digestInt[i2] = digestInt[i2] + tmpabcde[i2];
        }
        for (int n = 0; n < tmpData.length; n++) {
            tmpData[n] = 0;
        }
    }

    /**
     * 4字节数组转换为整数
     *
     * @param byteData
     * @param i
     * @return
     */
    private int byteArrayToInt(byte[] byteData, int i) {
        return ((byteData[i] & 0xff) << 24) | ((byteData[i + 1] & 0xff) << 16) |
                ((byteData[i + 2] & 0xff) << 8) | (byteData[i + 3] & 0xff);
    }

    /**
     * 整数转换为4字节数组
     *
     * @param intValue
     * @param byteData
     * @param i
     */
    private void intToByteArray(int intValue, byte[] byteData, int i) {
        byteData[i] = (byte) (intValue >>> 24);
        byteData[i + 1] = (byte) (intValue >>> 16);
        byteData[i + 2] = (byte) (intValue >>> 8);
        byteData[i + 3] = (byte) intValue;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param ib
     * @return
     */
    private static String byteToHexString(byte ib) {
        char[] Digit = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
                'D', 'E', 'F'
        };
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytearray
     * @return
     */
    private static String byteArrayToHexString(byte[] bytearray) {
        String strDigest = "";
        for (int i = 0; i < bytearray.length; i++) {
            strDigest += byteToHexString(bytearray[i]);
        }
        return strDigest;
    }

    /**
     * 计算sha-1摘要，返回相应的字节数组
     *
     * @param byteData
     * @return
     */
    public byte[] getDigestOfBytes(byte[] byteData) {
        process_input_bytes(byteData);
        byte[] digest = new byte[20];
        for (int i = 0; i < digestInt.length; i++) {
            intToByteArray(digestInt[i], digest, i * 4);
        }
        return digest;
    }

    /**
     * 计算sha-1摘要，返回相应的十六进制字符串
     *
     * @param byteData
     * @return
     */
    public String getDigestOfString(byte[] byteData) {
        return byteArrayToHexString(getDigestOfBytes(byteData));
    }


    /**
     * 传入文本内容，返回 SHA-256 串
     *
     * @param strText
     * @return
     */

    public static String SHA256(final String strText) throws NoSuchAlgorithmException {
        return SHA(strText, "SHA-256");
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     *
     * @param strText
     * @return
     */
    public static String SHA512(final String strText) throws NoSuchAlgorithmException {
        return SHA(strText, "SHA-512");
    }

    /**
     * 字符串 SHA 加密
     *
     * @param strText
     * @return
     */
    private static String SHA(String strText, String strType) throws NoSuchAlgorithmException {
        // 是否是有效字符串
        if (strText.isEmpty()) {
            throw new IllegalArgumentException("字符串无效");
        }
        // SHA 加密开始
        // 创建加密对象 并传入加密类型
        MessageDigest messageDigest = MessageDigest.getInstance(strType);
        // 传入要加密的字符串
        messageDigest.update(strText.getBytes());
        // 得到 byte 类型结果
        byte byteBuffer[] = messageDigest.digest();

        // 將 byte 转换为 string
        StringBuilder strHexString = new StringBuilder();
        // 遍历 byte buffer
        for (int i = 0; i < byteBuffer.length; i++) {
            String hex = Integer.toHexString(0xff & byteBuffer[i]);
            if (hex.length() == 1) {
                strHexString.append('0');
            }
            strHexString.append(hex);
        }
        // 得到返回结果
        return strHexString.toString();
    }

    /***
     *  利用Apache的工具类实现SHA-256加密,需引入包commons-codec
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256Str(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(str.getBytes());
        return StringUtil.byte2Hex(hash);
    }

    /**
     * 利用java原生的摘要实现SHA256加密
     *
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(str.getBytes());
        return StringUtil.byte2Hex(messageDigest.digest());
    }

    /**
     * HMAC-SHA256实现
     *
     * @param data
     * @param key
     * @return
     */
    public static String HMAC_SHA256(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] bytes = sha256_HMAC.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder();
        String item;
        for (int n = 0; bytes != null && n < bytes.length; n++) {
            item = Integer.toHexString(bytes[n] & 0XFF);
            if (item.length() == 1) {
                sb.append('0');
            }
            sb.append(item);
        }
        /*for (byte item : bytes) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
        }*/
        return sb.toString().toLowerCase();
    }

    public static void main(String[] args) {

    }


}