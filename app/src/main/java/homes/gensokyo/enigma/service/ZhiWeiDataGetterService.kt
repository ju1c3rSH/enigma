package homes.gensokyo.enigma.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import homes.gensokyo.enigma.MainActivity.Companion.repository
import homes.gensokyo.enigma.bean.MemberFlowJsonBuilder
import homes.gensokyo.enigma.`interface`.ApiService
import homes.gensokyo.enigma.util.CiperTextUtil
import homes.gensokyo.enigma.util.NetworkUtils
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.DateUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.Executors

class ZhiWeiDataGetterService : Service() {


        private val serviceScope = CoroutineScope(Dispatchers.IO)


        override fun onCreate() {
            super.onCreate()


            val apiService = NetworkUtils.retrofit.create(ApiService::class.java)

            serviceScope.launch {
                fetchData() // Call your suspending function here
            }
            val memberFlowRequest = MemberFlowJsonBuilder(
                get("kidUuid","1111"),
                listOf(2, 5, 6, 7),
                7,
                1,
                100,
                DateUtils.Date2Str(-30),
                DateUtils.Date2Str(1)
            )
            CoroutineScope(Dispatchers.IO).launch {
                val result = repository.fetchMemberFlow(memberFlowRequest,AppConstants.headerMap)

            }
        }

        override fun onBind(intent: Intent): IBinder? {
            return null
        }


        suspend private fun fetchData() {
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                serviceScope.launch {
                    try {
                        val cipherText = CiperTextUtil.encrypt(get("wxOaOpenid","sss"))//这些默认值也许可以更好
                        val resultGetRole = repository.fetchRole(cipherText, AppConstants.headerMap)
                        resultGetRole?.let {
                            Log.i("DataService", "Received GetRole info: $it")
                        }

                        val resultLogin = repository.doLogin(AppConstants.headerMap)
                        resultLogin?.let {
                            Log.i("DataService", "Received Login info: $it")
                        }

                        val resultKid = repository.fetchStudents(AppConstants.headerMap)
                        resultKid?.let {
                            Log.i("DataService", "Received Student info: $it")
                        }

                        val resultBalance = repository.fetchBalance(AppConstants.headerMap)
                        resultBalance?.let {
                            Log.i("DataService", "Received Balance info: $it")
                        }

                        val memberFlowRequest = MemberFlowJsonBuilder(
                            get("kidUuid","ss"),
                            listOf(2, 5, 6, 7),
                            7,
                            1,
                            100,
                            DateUtils.Date2Str(-30),
                            DateUtils.Date2Str(1)
                        )
                        val resultMemberFlow =
                            repository.fetchMemberFlow(memberFlowRequest, AppConstants.headerMap)
                        resultMemberFlow?.let {
                            Log.i("DataService", "Received MemberFlow info: $it")

                        }

                    } catch (e: IOException) {
                        e.message?.let { Log.e("DataService", it) }
                    }
                }
            }
    }
}

