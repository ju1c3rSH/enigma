package homes.gensokyo.enigma

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson

import homes.gensokyo.enigma.databinding.ActivityMainBinding

import homes.gensokyo.enigma.`interface`.ApiService
import homes.gensokyo.enigma.`interface`.GithubApiService
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.service.ZhiWeiDataGetterService
import homes.gensokyo.enigma.ui.oobe.OOBEActivity
import homes.gensokyo.enigma.util.NetworkUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.TextUtils.compareVersions
import homes.gensokyo.enigma.util.TextUtils.toast
import homes.gensokyo.enigma.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val sharedViewModel: SharedViewModel by viewModels { ViewModelProvider.NewInstanceFactory() }
    private lateinit var binding: ActivityMainBinding
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var gson: Gson
        var repository: UserRepository = MainApplication.repository
        var apiService : ApiService = MainApplication.apiService
        var githubApiService: GithubApiService = MainApplication.githubApiService

        // lateinit var application: Application


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        gson = Gson()
        //apiService = NetworkUtils.retrofit.create(ApiService::class.java)
        //repository = UserRepository()
        // = NetworkUtils.retrofit.create(ApiService::class.java)
        if(get("isFirst",true)){
            startActivity(Intent(this, OOBEActivity::class.java))
            finish()
            return
        }
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkForUpdates()
        val navView: BottomNavigationView = binding.navView
        val serviceIntent = Intent(this, ZhiWeiDataGetterService::class.java)
        //startService(serviceIntent)

        if (!NetworkUtils().isNetworkAvailable(context)) {
            "No internet connection available".toast()
        }
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter==null) {
            "Not Support".toast()
        }
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard,R.id.navigation_face_scan, R.id.navigation_mine
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }
    private fun checkForUpdates() {

        val currentVersion = BuildConfig.VERSION_NAME

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = githubApiService.getLatestRelease()
                val latestVersion = response.tag_name
                val comparisonResult = compareVersions(currentVersion, latestVersion)

                when {
                    comparisonResult < 0 -> {
                        withContext(Dispatchers.Main) {
                            showUpdateDialog(latestVersion, response.name, response.body)
                        }
                    }
                    comparisonResult == 0 -> {
                        Log.d("UpdateCheck", "当前版本是最新的")
                    }
                    else -> {
                        Log.d("UpdateCheck", "当前版本比最新版本还新")
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateCheck", "Error fetching update", e)
            }
        }
    }

    private fun showUpdateDialog(latestVersion: String, name: String? = null, body: String? = null) {
        val message = StringBuilder("当前版本: ${BuildConfig.VERSION_NAME}\n最新版本: $latestVersion\n")
        name?.let { message.append("版本名: $it\n") }
        body?.let { message.append("\n更新内容:\n$it") }

        AlertDialog.Builder(this)
            .setTitle("新版本可用")
            .setMessage(message.toString())
            .setPositiveButton("更新") { dialog, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ju1c3rSH/enigma/releases/latest"))
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            .show()
    }


}