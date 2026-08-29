package homes.gensokyo.enigma.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CipherTextUtil {
    private const val key = "intellv123456789"
    private const val iv = "iv12345677654321"
    private const val appid = "wxddbbb3d7ad98c9a4"
    private const val newKey = "finest.com.cn_ac"

    fun generateCipherText(wxOa: String): String {
        val text = "$appid#$wxOa#${System.currentTimeMillis()}"
        val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(key.toByteArray(), "AES"),
            IvParameterSpec(iv.toByteArray())
        )
        return Base64.encodeToString(cipher.doFinal(text.toByteArray()), Base64.DEFAULT)
    }

    fun encryptUpdateNews(text: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(newKey.toByteArray(), "AES"))
        return Base64.encodeToString(cipher.doFinal(text.toByteArray()), Base64.NO_WRAP)
    }

    fun decryptUpdateNews(text: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(newKey.toByteArray(), "AES"))
        return String(cipher.doFinal(Base64.decode(text.trim(), Base64.DEFAULT)))
    }
}
