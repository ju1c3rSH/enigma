package homes.gensokyo.enigma.util

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class MyCookieJar : CookieJar {

    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
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
            val currentTimeMillis = System.currentTimeMillis()
            val iterator = storedCookies.iterator()

            while (iterator.hasNext()) {
                val cookie = iterator.next()
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
