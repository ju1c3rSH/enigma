package homes.gensokyo.enigma

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import homes.gensokyo.enigma.R

import homes.gensokyo.enigma.databinding.ActivityMainBinding

import homes.gensokyo.enigma.`interface`.ApiService
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.service.ZhiWeiDataGetterService
import homes.gensokyo.enigma.ui.setting.SettingActivity
import homes.gensokyo.enigma.util.NetworkUtils
import homes.gensokyo.enigma.util.SettingUtils.get

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var gson: Gson
        lateinit var repository: UserRepository
        lateinit var apiService : ApiService

        // lateinit var application: Application


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        gson = Gson()
        apiService = NetworkUtils.retrofit.create(ApiService::class.java)
        repository = UserRepository()
        // = NetworkUtils.retrofit.create(ApiService::class.java)
        if(get("isFirst",true)){
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val serviceIntent = Intent(this, ZhiWeiDataGetterService::class.java)
        startService(serviceIntent)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }
}