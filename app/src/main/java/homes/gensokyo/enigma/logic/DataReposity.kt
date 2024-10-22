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
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class UserRepository {
    //private val apiService = NetworkUtils.retrofit.create(ApiService::class.java)
    private val gson = Gson()

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
            val response = apiService.doLogin(AppConstants.loginUrl, headerMap, AppConstants.jsonStr)
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
        return try {
            val response = apiService.fetchAllowedSchoolList(AppConstants.getAllowSearchSchoolUrl + schoolName, headerMap)
            if (response.isSuccessful) {
                LogUtils.d("fetchAllowedSearchSchools", response.body().toString())
                val listType = object : TypeToken<List<School>>() {}.type
                val schoolList: List<School> = gson.fromJson(response.body(), listType)

                return schoolList
            }else{
                null //TODO 解决null，让具象化
            }
        }catch (e:Exception){
            Log.e("fetchAllowedSearchSchools", "Error fetching balance : ${e.message}")
            null
        }
    }

    suspend fun fetchAllNotGraduateClasses4Tenant(tenantId:Int , headerMap: Map<String, String>): List<GradeBean>? {
        return try {
            val response = apiService.fetchAllNotGraduateClasses4Tenant(
                AppConstants.getAllNotGraduateClasses4TenantUrl + tenantId,
                headerMap
            )
            if (response.isSuccessful) {
                LogUtils.d("fetchAllNotGraduateClasses4Tenant", response.body().toString())
                val listType = object : TypeToken<List<GradeBean>>() {}.type
                val gradeList: List<GradeBean> = gson.fromJson(response.body(), listType)
                return gradeList
            }else{
                null
            }
        }catch (e:Exception){
            Log.e("fetchAllNotGraduateClasses4Tenant", "Error fetching balance : ${e.message}")
            null

        }
    }

    suspend fun fetchStudentDetails(studentNane:String,classId:Int, headerMap: Map<String, String>): Student? {
        val map = mapOf(
            "studentName" to studentNane.toString(),
            "classId" to classId.toString())
        return try {
            val response = apiService.fetchStudentDetails(AppConstants.getListByStudentNameUrl,map, headerMap)
            if (response.isSuccessful) {
                if (response.body()!!.isEmpty()){
                    return null
                }
                //LogUtils.d("DETAILS", response.body().toString())
                val listType = object : TypeToken<List<Student>>() {}.type
                val sul: List<Student> = gson.fromJson(response.body(), listType)

                LogUtils.d("DETAILS", sul[0].studentName)
                return sul[0]

                //val listType = object : TypeToken<List<Student>>() {}.type
                //val detailIst:Student = gson.fromJson(response.body(), listType)
                //return detailIst

            }else{
                Log.e("fetchStudentDetails", "Error fetching ")
                null
            }
        }catch (e:Exception){
            Log.e("fetchStudentDetails", "Error fetching balance : ${e.message}")
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
