package com.bajins.clazz;

/**
 * 自定义进制
 */
public class BinOctDecHexTest {
    //设置字符数组
    //可以添加任意不重复字符，提高能转换的进制的上限
    static char chs[] = new char[36];

    static {
        for (int i = 0; i < 10; i++) {
            chs[i] = (char) ('0' + i);
        }
        for (int i = 10; i < chs.length; i++) {
            chs[i] = (char) ('A' + (i - 10));
        }
    }

    /**
     * 转换方法
     *
     * @param num       元数据字符串
     * @param fromRadix 元数据的进制类型
     * @param toRadix   目标进制类型
     * @return
     */
    static String transRadix(String num, int fromRadix, int toRadix) {
        int number = Integer.valueOf(num, fromRadix);
        StringBuilder sb = new StringBuilder();
        while (number != 0) {
            sb.append(chs[number % toRadix]);
            number = number / toRadix;
        }
        return sb.reverse().toString();
    }


    /**
     * 在进制表示中的字符集合。
     */
    final static char[] digits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    /**
     * 将十进制的数字转换为指定进制的字符串。
     *
     * @param i      十进制的数字。
     * @param system 指定的进制，常见的2/8/16。
     * @return 转换后的字符串。
     */
    public static String toCustomNumericString(int i, int system) {
        long num = 0;
        if (i < 0) {
            num = ((long) 2 * 0x7fffffff) + i + 2;
        } else {
            num = i;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((num / system) > 0) {
            buf[--charPos] = digits[(int) (num % system)];
            num /= system;
        }
        buf[--charPos] = digits[(int) (num % system)];
        return new String(buf, charPos, (32 - charPos));
    }

    /**
     * 由于用到了位移操作，所以它的进制表示只是局限于2/4/8/16/32，其它的就不能实现了。
     *
     * @param i     十进制的数字
     * @param shift 当shift为1时，表示二进制；当shift为2时，表示四进制；依次类推
     * @return
     */
    private static String toUnsignedString(int i, int shift) {
        char[] buf = new char[32];
        int charPos = 32;
        int radix = 1 << shift;
        int mask = radix - 1;
        do {
            buf[--charPos] = digits[i & mask];
            i >>>= shift;
        } while (i != 0);
        return new String(buf, charPos, (32 - charPos));
    }

    //测试
    public static void main(String[] args) {
        System.out.println(transRadix("YGL", 36, 26));

        System.out.println(toCustomNumericString(26,26));
        System.out.println(toUnsignedString(26,32));

        CustomDigit cd = new CustomDigit("1000000000", 36, 1);
        for (Integer i : cd.getFormattedNumberList()) {
            // 因为从1开始，所以这边只加上64，即从'A'输出到'Z'
            System.out.print((char) (i + 64));
        }
        System.out.println(Integer.parseInt("SAMURT", Character.MAX_RADIX));
    }

}
