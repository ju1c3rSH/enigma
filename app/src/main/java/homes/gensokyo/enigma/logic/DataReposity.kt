package homes.gensokyo.enigma.logic.logic

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import homes.gensokyo.enigma.MainActivity.Companion.apiService
import homes.gensokyo.enigma.bean.GradeBean
import homes.gensokyo.enigma.bean.MemberFlowJsonBuilder
import homes.gensokyo.enigma.bean.QueryRequest
import homes.gensokyo.enigma.bean.QueryResponse
import homes.gensokyo.enigma.bean.School
import homes.gensokyo.enigma.bean.Student
import homes.gensokyo.enigma.bean.balanceBean
import homes.gensokyo.enigma.bean.memberflowbean
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.CipherTextUtil
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class UserRepository {
    //private val apiService = NetworkUtils.retrofit.create(ApiService::class.java)
    private val gson = Gson()

    // 记录最近一次请求失败的真实原因，供上层透传到 UI，避免被笼统的"网络错误"掩盖
    var lastError: String? = null
        private set

     suspend fun fetchRole(cipherText:String, headerMap : Map<String, String>): String? {
        return try {
            //val cipherText = CiperTextUtil.encrypt(AppConstants.wxOa)
            val getRoleNoAuthUrlEncoded = AppConstants.getNoAuth + URLEncoder.encode(cipherText, "UTF-8")
            val response = apiService.fetchRole(getRoleNoAuthUrlEncoded, headerMap)
            if (response.isSuccessful) {
                response.body()
            } else {
                null // Handle unsuccessful response
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun queryData(headerMap: Map<String, String>, request: QueryRequest):QueryResponse? {
        return try {
            val response = apiService.Query(headerMap, request)
            LogUtils.d("queryData", "code=${response.code()} body=${response.body()?.take(200)}")
            if (response.isSuccessful) {
                //LogUtils.d("queryData", response.toString())
                return gson.fromJson(response.body(), object : TypeToken<QueryResponse>() {}.type)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }





    suspend fun doLogin(headerMap: Map<String, String>): String? {
        return try {
            val body = gson.toJson(mapOf("paramStr" to CipherTextUtil.encryptUpdateNews(AppConstants.jsonStr)))
            val response = apiService.doLogin(AppConstants.loginUrl, headerMap, body)
            LogUtils.d("doLogin", "code=${response.code()} body=${response.body()?.take(200)}")
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun fetchStudents(headerMap: Map<String, String>): List<Student>? {
        return try {
            val response = apiService.fetchStudents(AppConstants.getAllStudentsOfParentUrl, headerMap)
            if (response.isSuccessful) {
                LogUtils.d( "fetchStudents", response.body().toString())
                return gson.fromJson(response.body(), object : TypeToken<List<Student>>() {}.type)
            } else {
                null // Handle unsuccessful response
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun fetchBalance(headerMap: Map<String, String>): balanceBean? {
        return try {
            val response = apiService.fetchBalance(AppConstants.getBalance+ get("kidUuid","111"),headerMap)
            if (response.isSuccessful) {
                LogUtils.d("fetchBalance111", response.body().toString() + get("kidUuid","111"))
                return gson.fromJson(response.body(), balanceBean::class.java)
            } else {
                LogUtils.d("fetchBalance", response.errorBody().toString())
                null
            }
        } catch (e: Exception) {
            Log.e("fetchBalance", "Error fetching balance", e)
            null
        }
    }
    suspend fun fetchAllowedSearchSchools(schoolName: String, headerMap: Map<String, String>): List<School>? {
        lastError = null
        return try {
            val paramJson = gson.toJson(mapOf("schoolName" to schoolName))
            val body = gson.toJson(mapOf("paramStr" to CipherTextUtil.encryptUpdateNews(TextUtils.removeSpaces(paramJson))))
            LogUtils.d("fetchAllowedSearchSchools", "request schoolName=$schoolName body=${body.take(120)}")
            val response = apiService.fetchAllowedSchoolList(AppConstants.getAllowSearchSchoolUrl, headerMap, body)
            LogUtils.d("fetchAllowedSearchSchools", "code=${response.code()} body=${response.body()?.take(400)} err=${response.errorBody()?.string()?.take(200)}")
            if (response.isSuccessful) {
                val raw = response.body()
                if (raw == null) {
                    lastError = "响应体为空"
                    return null
                }
                LogUtils.d("fetchAllowedSearchSchools", "raw len=${raw.length} rawHead=${raw.take(80)}")
                val decrypted = CipherTextUtil.decryptUpdateNews(raw)
                LogUtils.d("fetchAllowedSearchSchools", "decrypted len=${decrypted.length} head=${decrypted.take(200)}")
                val listType = object : TypeToken<List<School>>() {}.type
                gson.fromJson<List<School>>(decrypted, listType)
            } else {
                lastError = "HTTP ${response.code()}"
                null
            }
        } catch (e: Exception) {
            lastError = "${e.javaClass.simpleName}: ${e.message}"
            Log.e("fetchAllowedSearchSchools", "Error", e)
            null
        }
    }

    suspend fun fetchAllNotGraduateClasses4Tenant(tenantId: Int, headerMap: Map<String, String>): List<GradeBean>? {
        return try {
            val paramJson = gson.toJson(mapOf("tenantId" to tenantId))
            val body = gson.toJson(mapOf("paramStr" to CipherTextUtil.encryptUpdateNews(TextUtils.removeSpaces(paramJson))))
            val response = apiService.fetchAllNotGraduateClasses4Tenant(AppConstants.getAllNotGraduateClasses4TenantUrl, headerMap, body)
            LogUtils.d("fetchAllClasses", "code=${response.code()} body=${response.body()?.take(300)}")
            if (response.isSuccessful) {
                val decrypted = CipherTextUtil.decryptUpdateNews(response.body() ?: return null)
                val listType = object : TypeToken<List<GradeBean>>() {}.type
                gson.fromJson<List<GradeBean>>(decrypted, listType)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("fetchAllNotGraduateClasses4Tenant", "Error: ${e.message}")
            null
        }
    }

    suspend fun fetchStudentDetails(studentNane: String, classId: Int, headerMap: Map<String, String>): Student? {
        return try {
            val paramJson = gson.toJson(mapOf("classId" to listOf(classId), "studentName" to studentNane))
            val body = gson.toJson(mapOf("paramStr" to CipherTextUtil.encryptUpdateNews(TextUtils.removeSpaces(paramJson))))
            val response = apiService.fetchStudentDetails(AppConstants.getListByStudentNameUrl, headerMap, body)
            LogUtils.d("fetchStudentDetails", "code=${response.code()} body=${response.body()?.take(300)}")
            if (response.isSuccessful) {
                val decrypted = CipherTextUtil.decryptUpdateNews(response.body() ?: return null)
                val list = gson.fromJson<List<Student>>(decrypted, object : TypeToken<List<Student>>() {}.type)
                list.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("fetchStudentDetails", "Error: ${e.message}")
            null
        }
    }

    suspend fun fetchMemberFlow(json: MemberFlowJsonBuilder, headerMap: Map<String, String>): memberflowbean? {
        return try {
            if(get("isFirst", true)){
                return memberflowbean(error = "Not been login")
            }
            val postJson = gson.toJson(json)
            LogUtils.d("fetchMemberFlow", "Requesting MemberFlow with JSON: $postJson and headers: $headerMap")
            val response = apiService.fetchMemberFlow(AppConstants.getMemberFlow, headerMap, postJson)

            if (response.isSuccessful) {
                val responseBody = response.body()
                LogUtils.d("fetchMemberFlow", "Successfully fetched MemberFlow: $responseBody")
                return  gson.fromJson(responseBody, memberflowbean::class.java)
            } else {
                Log.e("fetchMemberFlow", "API call failed, Response Code: ${response.code()}, Response Message: ${response.message()}")
                memberflowbean(error = "API call failed, Response Code: ${response.code()}, Response Message: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("fetchMemberFlow", "Error while fetching MemberFlow", e)
            memberflowbean(error = e.message)
        }
    }



}

