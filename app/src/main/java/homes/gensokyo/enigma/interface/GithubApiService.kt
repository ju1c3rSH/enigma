package homes.gensokyo.enigma.`interface`

import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {

        @GET("repos/ju1c3rSH/enigma/releases/latest")
        suspend fun getLatestRelease(): ReleaseResponse
    }
//TODO 什么时候把自己的石山搞好
    data class ReleaseResponse(
        val tag_name: String,
        val name: String,
        val body: String
    )
