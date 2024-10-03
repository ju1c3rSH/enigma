package homes.gensokyo.enigma.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import homes.gensokyo.enigma.util.MyCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {

    companion object {
        private const val DEFAULT_BASE_URL = "http://wx.ivxiaoyuan.com"
        private val cookieJar = MyCookieJar()
        private val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://wx.ivxiaoyuan.com")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        fun createRetrofit(baseUrl: String = DEFAULT_BASE_URL): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
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
