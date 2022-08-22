package com.bajins.clazz;


import java.util.Arrays;

/**
 * 数字辅助工具<br />六十二进制内的数字互转（seo:62进制内的数字互转）
 * <p>
 * https://blog.csdn.net/cnds123321/article/details/117663591
 * https://www.cnblogs.com/cnkaihua/p/5974337.html
 * Integer.toBinaryString(); // 转换二进制
 * Integer.toHexString(); // 转换16进制 https://blog.csdn.net/qq_43560721/article/details/102460645
 * Integer.toOctalString(); // 转换八进制
 * Integer.toString(); // 转换成字符串或转换成指定进制的字符串
 * Integer.toUnsignedLong();
 * Integer.toUnsignedString(); // 转换成指定进制的字符串
 * Integer.toUnsignedString0();
 * Byte.toUnsignedInt();
 */
public class NumberDigit {
    /**
     * 支持的最小进制
     */
    public static int MIN_RADIX = 2;
    /**
     * 支持的最大进制
     */
    public static int MAX_RADIX = 62;

    /**
     * 锁定创建
     */
    private NumberDigit() {
    }

    /**
     * 0-9a-zA-Z表示62进制内的 0到61。
     */
    private static final String num62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 返回一字符串，包含 number 以 toRadix 进制的表示。<br />
     * number 本身的进制由 fromRadix 指定。fromRadix 和 toRadix 都只能在 2 和 62 之间（包括 2 和 62）。<br />
     * 高于十进制的数字用字母 a-zA-Z 表示，例如 a 表示 10，b 表示 11 以及 Z 表示 62。<br />
     *
     * @param number    需要转换的数字
     * @param fromRadix 输入进制
     * @param toRadix   输出进制
     * @return 指定输出进制的数字
     */
    public static String baseConver(String number, int fromRadix, int toRadix) {
        long dec = any2Dec(number, fromRadix);
        return dec2Any(dec, toRadix);
    }

    /**
     * 返回一字符串，包含 十进制 number 以 radix 进制的表示。
     *
     * @param dec     需要转换的数字
     * @param toRadix 输出进制。当不在转换范围内时，此参数会被设定为 2，以便及时发现。
     * @return 指定输出进制的数字
     */
    public static String dec2Any(long dec, int toRadix) {
        if (toRadix < MIN_RADIX || toRadix > MAX_RADIX) {
            toRadix = 2;
        }
        if (toRadix == 10) {
            return String.valueOf(dec);
        }
        // -Long.MIN_VALUE 转换为 2 进制时长度为65
        char[] buf = new char[65]; //
        int charPos = 64;
        boolean isNegative = (dec < 0);
        if (!isNegative) {
            dec = -dec;
        }
        while (dec <= -toRadix) {
            buf[charPos--] = num62.charAt((int) (-(dec % toRadix)));
            dec = dec / toRadix;
        }
        buf[charPos] = num62.charAt((int) (-dec));
        if (isNegative) {
            buf[--charPos] = '-';
        }
        return new String(buf, charPos, (65 - charPos));
    }

    /**
     * 返回一字符串，包含 number 以 10 进制的表示。<br />
     * fromBase 只能在 2 和 62 之间（包括 2 和 62）。
     *
     * @param number    输入数字
     * @param fromRadix 输入进制
     * @return 十进制数字
     */
    public static long any2Dec(String number, int fromRadix) {
        long dec = 0;
        long digitValue = 0;
        int len = number.length() - 1;
        for (int t = 0; t <= len; t++) {
            digitValue = num62.indexOf(number.charAt(t));
            dec = dec * fromRadix + digitValue;
        }
        return dec;
    }

    /**
     * Java Hex(十六进制)字符串与byte数组互转
     */
    public static abstract class Converts {
        private final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'};
        private final static byte[] undigits = new byte[128];

        static {
            char[] lower = "0123456789abcdef".toCharArray();
            char[] upper = "0123456789ABCDEF".toCharArray();
            Arrays.fill(undigits, (byte) -1);
            for (byte i = 0; i < 16; i++) {
                undigits[lower[i]] = i;
            }
            for (byte i = 0; i < 16; i++) {
                undigits[upper[i]] = i;
            }
        }

        public static String bytesToHex(byte[] bytes) {
            char[] chars = new char[bytes.length << 1];
            for (int i = 0; i < bytes.length; i++) {
                chars[i << 1] = digits[(bytes[i] >> 4) & 0xF];
                chars[(i << 1) + 1] = digits[bytes[i] & 0xF];
            }
            return new String(chars);
        }

        public static byte[] hexToBytes(String hex) {
            byte[] bytes = new byte[hex.length() >> 1];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (undigits[hex.charAt(i << 1)] << 4 | undigits[hex.charAt((i << 1) + 1)]);
            }
            return bytes;
        }


        /**
         * 字符串转byte数组
         *
         * @param src
         * @return
         */
        public static byte[] hexStr2Bytes(String src) {
            src = src.replaceAll(" ", "");
            System.out.println(src);
            int m = 0, n = 0;
            int l = src.length() / 2;
            System.out.println(l);
            byte[] ret = new byte[l];
            for (int i = 0; i < l; i++) {
                m = i * 2 + 1;
                n = m + 1;
                String sss = "0x" + src.substring(i * 2, m) + src.substring(m, n);
                System.out.println("sss[" + i + "]:" + sss);
                try {
                    ret[i] = Byte.decode(sss);
                } catch (Exception e) {
                    // TODO: handle exception
                    int s = Integer.decode(sss);
                    ret[i] = (byte) s;
                }
            }
            return ret;
        }

        public static void main(String[] args) {
            String hexstr = "1B 43 DD 0D 0A C9 00 00";
            byte[] b = hexStr2Bytes(hexstr);
            for (int c : b) {
                System.out.println(c);
            }

            /*try {
                ret[i] = Byte.decode(sss);
            } catch (Exception e) { // 如果转换的hex大于128，则会报异常
                int s = Integer.decode(sss);
                ret[i] = (byte)s;
            }*/
        }
    }
}

