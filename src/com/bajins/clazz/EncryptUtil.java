package com.bajins.clazz;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * 封装各种格式的编码解码工具类.
 * 4.JDK提供的URLEncoder
 * 5.DES 加密解密
 * 6.JavaMD5 消息摘要和加盐
 * 7.Base64
 *
 * @description: EncryptUtil
 * @author: https://www.bajins.com
 * @create: 2018-04-14 15:21
 * @see java.security JCA(Java Cryptography Architecture)，提供了基本Java加密框架，包含证书、数字签名、消息摘要、秘钥对生成器
 * @see MessageDigest 消息摘要 getInstance() 支持标准：MD5、SHA-1、SHA-256
 * @see Key 有SecretKey（对称秘钥）、PublicKey、PrivateKey等子接口，是秘钥的抽象，对称加密算法的秘钥由SecretKey提供
 * @see KeyPair 非对称加密算法秘钥对的抽象
 * @see KeyPairGenerator 秘钥对的生成
 * @see KeyFactory 根据指定的规范生成秘钥
 * @see KeyGenerator
 * @see KeyAgreement
 * @see KeyStore 秘钥库的抽象，用于管理秘钥和证书
 * @see Signature 生成和验证数字签名
 * @see Mac 消息认证代码，与消息摘要类似，但是使用其他密钥来加密消息摘要
 * @see Provider
 * @see SecureRandom
 * <p>
 * @see java.security.cert
 * @see CertificateFactory
 * @see CertPathBuilder
 * @see CertPathValidator
 * @see CertStore
 * @see java.security.spec
 * @see java.security.interfaces
 * </p>
 * @see javax.crypto JCE(Java Cryptography Extension)是JCA的扩展，加密操作的类和接口
 * @see Cipher 加密和解密
 * <p>
 * @see javax.crypto.spec
 * @see javax.crypto.interfaces
 * </p>
 * <p>
 * @see java.util.Base64 编码
 * <pre>
 *  多个编码方式 https://github.com/rbuck/java-codecs
 *  Base85 https://github.com/fzakaria/ascii85
 *  Quoted Printable
 *  Percent Encoded
 *  XXencode
 *  UUencode https://github.com/biagioT/java-uudecoder
 * </pre>
 * </p>
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
            throw new IllegalArgumentException("numBytes argument must be a positive integer (1 or larger)");
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
        cipher.init(Cipher.ENCRYPT_MODE, key, sr); // 设置模式为加密
        return Arrays.toString(cipher.doFinal(data.getBytes()));
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
        cipher.init(Cipher.DECRYPT_MODE, key, sr); // 设置模式为解密
        // 把字符串进行解码，解码为为字节数组，并解密
        return Arrays.toString(cipher.doFinal(cryptData.getBytes()));
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
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return StringUtil.binaryToHex(f.generateSecret(spec).getEncoded());
    }

    public static void main(String[] args) {
        try {
            URLEncoder.encode("", StandardCharsets.UTF_8.name());
            URLDecoder.decode("", StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // import sun.misc.BASE64Decoder;
        // import sun.misc.BASE64Encoder;
        // 编码，会提示内部使用API，可能会在未来某个版本删除
        /*BASE64Encoder base64Encoder = new BASE64Encoder();
        String encode = base64Encoder.encode("111".getBytes());

        // 解码，会提示内部使用API，可能会在未来某个版本删除
        BASE64Decoder base64Decoder = new BASE64Decoder();
        try {
            byte[] bytes = base64Decoder.decodeBuffer(encode);
            // 将解码得到的数据转String
            String s = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // 推荐，Java 8的java.util套件中Base64，比sun.misc套件提供的还要快至少11倍，比Apache Commons Codec快至少3倍
        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        final String text = "字串文字";
        final byte[] textByte = text.getBytes(StandardCharsets.UTF_8);
        //编码
        final String encodedText = encoder.encodeToString(textByte);
        System.out.println(encodedText);
        //解码
        System.out.println(new String(decoder.decode(encodedText), StandardCharsets.UTF_8));

        /*
         * 获取支持的加密算法
         */
        // import sun.security.jca.ProviderList;
        // import sun.security.jca.Providers;
        /*ProviderList providerList = Providers.getProviderList();
        for (Provider provider : providerList.providers()) {
            for (Map.Entry<Object, Object> entry : provider.entrySet()) {
                *//*String key = (String) entry.getKey();
                if (key.startsWith("KeyGenerator.")
                        || key.startsWith("Signature")
                        || key.startsWith("MessageDigest")) {*//*
                System.out.println(entry.getKey() + " -> " + entry.getValue());
                //}
            }
            System.out.println("--------------");
        }*/

        System.out.println("----------------------------------------------");

        String password = "123456";
        String content = "ssgfmjhdsfdnkbmvvld,lgdfm";
        /**
         * 3DES（3重DES）
         */
        try {
            // 获得KEY
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede"); // TripleDES
            //keyGenerator.init(112); // 设置密钥长度，默认值为168，也可设置为112
            keyGenerator.init(new SecureRandom());
            SecretKey key = keyGenerator.generateKey();
            byte[] bytes = key.getEncoded();
            String keyStr = encoder.encodeToString(bytes);
            System.out.println("3EDS-密钥:" + keyStr);
            // DESede KEY 转换为DES KEY
            /*DESKeySpec deskeyspec = new DESKeySpec(bytes);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            Key key_ = factory.generateSecret(deskeyspec);
            bytes = key_.getEncoded();
            String keyStr_ = encoder.encodeToString(bytes);
            System.out.println("EDS-密钥:" + keyStr_);*/

            // 加密
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key); // 设置模式为加密
            bytes = cipher.doFinal(content.getBytes());
            String desRes = encoder.encodeToString(bytes);
            System.out.println("3EDS-加密:" + desRes);

            // 解密
            cipher.init(Cipher.DECRYPT_MODE, key); // 设置模式为解密
            bytes = cipher.doFinal(decoder.decode(desRes));
            System.out.println("3EDS-解密:" + new String(bytes, StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------");

        /**
         * AES
         */
        try {
            // 获得key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // 设置密钥长度 128, 192 or 256
            //keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] bytes = secretKey.getEncoded();
            String keyStr = encoder.encodeToString(bytes);
            System.out.println("AES-密钥:" + keyStr);

            // 加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            bytes = cipher.doFinal(content.getBytes());
            String aesRes = encoder.encodeToString(bytes);
            System.out.println("AES-加密:" + aesRes);

            // 解密
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            bytes = cipher.doFinal(decoder.decode(aesRes));
            System.out.println("AES-解密:" + new String(bytes, StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------");

        /**
         * PBE
         */
        try {
            // 口令和密钥
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray()); // 生成密钥转换对象
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
            SecretKey key = factory.generateSecret(pbeKeySpec);
            byte[] bytes = key.getEncoded();
            String keyStr = encoder.encodeToString(bytes);
            System.out.println("PBE-密钥:" + keyStr);
            // 初始化盐
            SecureRandom random = new SecureRandom();
            byte[] salt = random.generateSeed(8);

            // 加密，实例化PBE对象的一个输入的材料：参数分别为"盐和迭代次数"
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
            bytes = cipher.doFinal(content.getBytes());
            String pbeRes = encoder.encodeToString(bytes);
            System.out.println("PBE-加密:" + pbeRes);

            // 解密
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);
            bytes = cipher.doFinal(decoder.decode(pbeRes));
            System.out.println("PBE-解密:" + new String(bytes, StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------");

        /**
         * RSA
         */
        try {
            // 获取秘钥KeyPair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024); // 1024，2048，...
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 获取String公钥
            PublicKey publicKey = keyPair.getPublic();
            byte[] bytes = publicKey.getEncoded();
            String pubKey = encoder.encodeToString(bytes);
            System.out.println("RSA-公钥:" + pubKey);

            // 获取String私钥
            PrivateKey privateKey = keyPair.getPrivate();
            bytes = privateKey.getEncoded();
            String priKey = encoder.encodeToString(bytes);
            System.out.println("RSA-私钥:" + priKey);

            // 将字符串转换成为PublicKey公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoder.decode(pubKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);

            // 将字符串转换成为PrivateKey公钥
            PKCS8EncodedKeySpec keySpec_ = new PKCS8EncodedKeySpec(decoder.decode(priKey));
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec_);

            // 数据用公钥加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            bytes = cipher.doFinal(content.getBytes());
            String rsaRes = encoder.encodeToString(bytes);
            System.out.println("RSA-加密:" + rsaRes);

            // 数据用私钥解密
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] raw = cipher.doFinal(decoder.decode(rsaRes));
            System.out.println("RSA-解密:" + new String(raw, StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}
