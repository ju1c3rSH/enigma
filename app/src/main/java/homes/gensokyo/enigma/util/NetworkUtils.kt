package homes.gensokyo.enigma.util

import homes.gensokyo.enigma.util.MyCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {

    companion object {
        // 实例化自定义的 CookieJar
        private val cookieJar = MyCookieJar()

        // 创建 OkHttpClient 并设置 CookieJar
        private val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()

        // 创建 Retrofit 实例
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://wx.ivxiaoyuan.com") // 替换为你的实际 baseURL
            .client(client) // 使用带有 CookieJar 的 OkHttpClient
            .addConverterFactory(ScalarsConverterFactory.create()) // 允许返回 String 类型
            .addConverterFactory(GsonConverterFactory.create()) // 允许使用 JSON 转换对象
            .build()
    }
/*
    private interface ApiService {

        @GET
        suspend fun get(
            @Url url: String,
            @HeaderMap headers: Map<String, String>
        ): Response<String>

        @POST
        suspend fun post(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
            @Body body: String
        ): Response<String>
    }

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

        //这俩玩意似乎没用了 但是不敢删
    suspend fun get(url: String, headers: Map<String, String> = emptyMap()): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.get(url, headers)
                if (response.isSuccessful) {
                    // 返回响应主体（body）
                    response.body() ?: throw IOException("Empty response body")
                } else {
                    throw IOException("Request failed with status: ${response.code()}")
                }
            } catch (e: Exception) {
                throw IOException("Network request failed", e)
            }
        }
    }


    suspend fun post(url: String, headers: Map<String, String> = emptyMap(), body: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.post(url, headers, body)
                if (response.isSuccessful) {
                    // 返回响应主体（body）
                    response.body() ?: throw IOException("Empty response body")
                } else {
                    throw IOException("Request failed with status: ${response.code()}")
                }
            } catch (e: Exception) {
                throw IOException("Network request failed", e)
            }
        }
    }

 */
}
