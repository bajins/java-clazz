package com.bajins.clazz;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * 封装各种格式的编码解码工具类.
 * 4.JDK提供的URLEncoder
 * 5.DES 加密解密
 * 6.JavaMD5 消息摘要和加盐
 * 7.Base64
 *
 * @see javax.crypto
 * @see java.security
 * @see MessageDigest getInstance() 支持标准：MD5、SHA-1、SHA-256
 * @see java.util.Base64
 *
 * @description: EncryptUtil
 * @author: claer
 * @create: 2018-04-14 15:21
 */
public class EncryptUtil {


    /**
     * 对输入字符串进行md5散列
     *
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] md5(byte[] input) throws NoSuchAlgorithmException {
        return digest(input, "JavaMD5", null, 1);
    }

    public static byte[] md5(byte[] input, int iterations) throws NoSuchAlgorithmException {
        return digest(input, "JavaMD5", null, iterations);
    }

    /**
     * 对输入字符串进行sha1散列
     *
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] sha1(byte[] input) throws NoSuchAlgorithmException {
        return digest(input, "SHA-1", null, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt) throws NoSuchAlgorithmException {
        return digest(input, "SHA-1", salt, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt, int iterations) throws NoSuchAlgorithmException {
        return digest(input, "SHA-1", salt, iterations);
    }

    /**
     * 对字符串进行散列, 支持md5与sha1算法
     *
     * @param input
     * @param algorithm
     * @param salt
     * @param iterations
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) throws NoSuchAlgorithmException {
        //初始化sha1的加密算法
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        // 加入盐
        if (salt != null) {
            digest.update(salt);
        }
        //加密
        byte[] result = digest.digest(input);
        // 进行多少次散列
        for (int i = 1; i < iterations; i++) {
            digest.reset();
            result = digest.digest(result);
        }
        return result;
    }

    private static byte[] digest(InputStream input, String algorithm) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        int bufferLength = 8 * 1024;
        byte[] buffer = new byte[bufferLength];
        int read = input.read(buffer, 0, bufferLength);

        while (read > -1) {
            messageDigest.update(buffer, 0, read);
            read = input.read(buffer, 0, bufferLength);
        }

        return messageDigest.digest();
    }

    /**
     * 生成随机的Byte[]作为salt.
     *
     * @param numBytes byte数组的大小
     */
    public static byte[] generateSalt(int numBytes) {
        if (!(numBytes > 0)) {
            throw new IllegalArgumentException(String.format("numBytes argument must be a positive integer (1 or " +
                    "larger)", numBytes));
        }
        byte[] bytes = new byte[numBytes];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    /**
     * 对文件进行md5散列.
     */
    public static byte[] md5(InputStream input) throws IOException, NoSuchAlgorithmException {
        return digest(input, "JavaMD5");
    }

    /**
     * 对文件进行sha1散列.
     */
    public static byte[] sha1(InputStream input) throws IOException, NoSuchAlgorithmException {
        return digest(input, "SHA-1");
    }


    /**
     * DES加密
     *
     * @param data
     * @return java.lang.String
     */
    public static String encryptBasedDes(String data) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        byte[] desKey = {21, 1, -110, 82, -32, -85, -128, -65};
        DESKeySpec deskey = new DESKeySpec(desKey);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(deskey);
        // 加密对象
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);
        return String.valueOf(cipher.doFinal(data.getBytes()));
    }

    /**
     * DES解密
     *
     * @param cryptData
     * @return
     */
    public static String decryptBasedDes(String cryptData) throws InvalidKeyException, InvalidKeySpecException,
            NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        byte[] desKey = {21, 1, -110, 82, -32, -85, -128, -65};
        DESKeySpec deskey = new DESKeySpec(desKey);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(deskey);
        // 解密对象
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key, sr);
        // 把字符串进行解码，解码为为字节数组，并解密
        return String.valueOf(cipher.doFinal(cryptData.getBytes()));
    }


    /**
     * 对输入的密码进行验证
     *
     * @param attemptedPassword 待验证的密码
     * @param encryptedPassword 密文
     * @param salt              盐值
     * @return 是否验证成功
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static boolean PBKDF2authenticate(String attemptedPassword, String encryptedPassword, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 用相同的盐值对用户输入的密码进行加密
        String encryptedAttemptedPassword = getPBKDF2Encrypted(attemptedPassword, salt);
        // 把加密后的密文和原密文进行比较，相同则验证成功，否则失败
        return encryptedAttemptedPassword.equals(encryptedPassword);
    }


    /**
     * 通过提供加密的强随机数生成器 生成盐
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String generateSaltPBKDF2() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[32 / 2];
        random.nextBytes(salt);
        return StringUtil.binaryToHex(salt);
    }

    /**
     * 生成密文
     *
     * @param password 明文密码
     * @param salt     盐值
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static String getPBKDF2Encrypted(String password, String salt) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        // 迭代1000次，盐长度为128 * 4
        KeySpec spec = new PBEKeySpec(password.toCharArray(), StringUtil.hexToBinary(salt), 1000, 128 * 4);
        String pbkdf2Algorithm = "PBKDF2WithHmacSHA1";
        SecretKeyFactory f = SecretKeyFactory.getInstance(pbkdf2Algorithm);
        return StringUtil.binaryToHex(f.generateSecret(spec).getEncoded());
    }

    public static void main(String[] args) {
        // 编码，会提示内部使用API，可能会在未来某个版本删除
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String encode = base64Encoder.encode("111".getBytes());

        // 解码，会提示内部使用API，可能会在未来某个版本删除
        BASE64Decoder base64Decoder = new BASE64Decoder();
        try {
            byte[] bytes = base64Decoder.decodeBuffer(encode);
            // 将解码得到的数据转String
            String s = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 推荐，Java 8的java.util套件中Base64，
            // 比sun.misc套件提供的还要快至少11倍，比Apache Commons Codec快至少3倍
            final Base64.Decoder decoder = Base64.getDecoder();
            final Base64.Encoder encoder = Base64.getEncoder();
            final String text = "字串文字";
            final byte[] textByte = text.getBytes("UTF-8");
            //编码
            final String encodedText = encoder.encodeToString(textByte);
            System.out.println(encodedText);
            //解码
            System.out.println(new String(decoder.decode(encodedText), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
