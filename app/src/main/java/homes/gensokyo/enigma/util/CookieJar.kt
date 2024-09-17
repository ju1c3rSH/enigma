package homes.gensokyo.enigma.util

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class MyCookieJar : CookieJar {

    // 保存所有域名相关的 Cookie
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
            // 保存和 URL 相关联的 cookies
            val host = url.host
            cookieStore[host] = mutableListOf()
            for (cookie in cookies) {
                cookieStore[host]?.add(cookie)
                Log.i("CookieJarUtils", "Saved cookie for $host: $cookie")
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val validCookies = mutableListOf<Cookie>()
        val storedCookies = cookieStore[host]

        if (storedCookies != null) {
            // 过滤出所有有效的（未过期的）cookies
            val currentTimeMillis = System.currentTimeMillis()
            val iterator = storedCookies.iterator()

            while (iterator.hasNext()) {
                val cookie = iterator.next()
                // 如果 cookie 没有过期，则可以使用
                if (cookie.expiresAt > currentTimeMillis) {
                    validCookies.add(cookie)
                } else {
                    // 删除已经过期的 cookie
                    iterator.remove()
                    Log.i("CookieJarUtils", "Removed expired cookie: $cookie")
                }
            }

            Log.i("CookieJarUtils", "Loaded cookies for $host: $validCookies")
        }

        return validCookies
    }
}
