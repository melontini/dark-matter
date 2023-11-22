package me.melontini.dark_matter.api.base.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

@UtilityClass
@ApiStatus.Experimental
public final class BadCrypt {

    @UtilityClass
    public static final class Base64Based {

        public static String encryptToStr(byte[] str, byte[] b, MessageDigest d) throws Exception {
            return Base64.getEncoder().encodeToString(BadCrypt.encrypt(str, b, d));
        }

        public static String encryptToStr(String str, byte[] b, MessageDigest d) throws Exception {
            return encryptToStr(str.getBytes(), b, d);
        }

        public static String decryptToStr(byte[] encrypted, byte[] b, MessageDigest d) throws Exception {
            return new String(BadCrypt.decrypt(Base64.getDecoder().decode(encrypted), b, d));
        }

        public static String decryptToStr(String encrypted, byte[] b, MessageDigest d) throws Exception {
            return decryptToStr(encrypted.getBytes(), b, d);
        }
    }

    @UtilityClass
    private static class Holder {
        static final SecureRandom random = new SecureRandom();
    }

    public static byte[] encrypt(byte[] str, byte[] b, MessageDigest d) throws Exception {
        IvParameterSpec iv = genIV();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretFromBytes(b, d), iv);

        return ArrayUtils.addAll(iv.getIV(), cipher.doFinal(str));
    }

    public static byte[] decrypt(byte[] encrypted, byte[] b, MessageDigest d) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(encrypted, 0, 16);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, getSecretFromBytes(b, d), iv);

        return cipher.doFinal(encrypted, 16, encrypted.length - 16);
    }

    private static IvParameterSpec genIV() {
        byte[] iv = new byte[16];
        SecureRandom random = Holder.random;
        random.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static SecretKeySpec getSecretFromBytes(byte[] key, MessageDigest d) {
        return new SecretKeySpec(Arrays.copyOf(d.digest(key), 16), "AES");
    }

    public static String digestToHexString(byte[] b, MessageDigest digest) {
        return HexFormat.of().formatHex(digest.digest(b));
    }

    public static String digestToHexString(String s, MessageDigest digest) {
        return digestToHexString(s.getBytes(), digest);
    }
}
