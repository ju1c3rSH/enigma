package homes.gensokyo.enigma.`interface`


import homes.gensokyo.enigma.bean.QueryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
//TODO 这一块有很大弊病。可以通过设置不同的client的不同baseURl实现解耦。

    //todo 我去 大屎山
    @GET
    suspend fun fetchRole(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Response<String>

    @POST("sc/student/h5/dynamicParentViewQuery/query")
    @Headers("Content-Type: application/json")
    suspend fun Query(
        @HeaderMap headers: Map<String, String>,
        @Body request: QueryRequest
    ): Response<String>

    @POST
    suspend fun doLogin(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Body requestBody: String
    ): Response<String>

    @GET
    suspend fun fetchStudents(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Response<String>


    @GET
    suspend fun fetchBalance(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Response<String>


    @POST
    @Headers("Content-Type: application/json")
    suspend fun fetchMemberFlow(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Body requestBody: String
    ): Response<String>

    @GET
    suspend fun fetchAllowedSchoolList(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Response<String>

    @GET
    suspend fun fetchAllNotGraduateClasses4Tenant(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Response<String>

    @GET
    suspend fun fetchStudentDetails(
        @Url url: String,
        @QueryMap qm: Map<String, String>,
        @HeaderMap headers: Map<String, String>
    ): Response<String>

}

annotation class cipherText




