package homes.gensokyo.enigma

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.gson.Gson
import homes.gensokyo.enigma.`interface`.ApiService
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.util.NetworkUtils
import homes.gensokyo.enigma.viewmodel.UsrdataModel

class MainApplication : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var gson: Gson
        lateinit var repository: UserRepository
        lateinit var apiService : ApiService
        lateinit var instance: MainApplication
        // lateinit var application: Application

//TODO lateinit需要解决

    }
    override fun onCreate() {
        super.onCreate()
        context = this
        gson = Gson()
        instance = this
        repository = UserRepository()
        apiService = NetworkUtils.retrofit.create(ApiService::class.java)
        MainActivity.context = applicationContext
        MainActivity.gson = Gson()
        MainActivity.apiService = NetworkUtils.retrofit.create(ApiService::class.java)
        MainActivity.repository = UserRepository()
    }
}