package homes.gensokyo.enigma.util;

import android.util.Base64;

import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CiperTextUtil {

    private final static String key = "intellv123456789";
    private final static String iv = "iv12345677654321";
    static String timestamp = String.valueOf(new Date().getTime());
    private final static String appid = "wxddbbb3d7ad98c9a4";


    public static String encrypt(String wxOa) throws Exception{

        String text = appid + "#" + wxOa +"#" + String.valueOf(new Date().getTime());;

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(CiperTextUtil.iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE,secretKey, iv);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);


    }
}
