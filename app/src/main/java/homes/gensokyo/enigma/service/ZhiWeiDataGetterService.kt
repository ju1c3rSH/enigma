package homes.gensokyo.enigma.service

import android.app.Application
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import homes.gensokyo.enigma.MainActivity.Companion.repository
import homes.gensokyo.enigma.MainApplication
import homes.gensokyo.enigma.bean.MemberFlowJsonBuilder
import homes.gensokyo.enigma.`interface`.ApiService
import homes.gensokyo.enigma.util.CiperTextUtil
import homes.gensokyo.enigma.util.NetworkUtils
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.DateUtils
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.Executors

class ZhiWeiDataGetterService : Service() {
        private lateinit var sharedViewModel: SharedViewModel

        private val serviceScope = CoroutineScope(Dispatchers.IO)


        override fun onCreate() {
            super.onCreate()
            //val apiService = NetworkUtils.retrofit.create(ApiService::class.java)

            /*
            val memberFlowRequest = MemberFlowJsonBuilder(
                get("kidUuid","1111"),
                listOf(2, 5, 6, 7),
                7,
                1,
                100,
                DateUtils.Date2Str(-30),
                DateUtils.Date2Str(1)
            )

            serviceScope.launch {
                val result = repository.fetchMemberFlow(memberFlowRequest,AppConstants.headerMap)
            }
            TODO 暂时没用上
             */

            serviceScope.launch {
                fetchData()
            }
        }

        override fun onBind(intent: Intent): IBinder? {
            return null
        }

        val isFirstRun = get("isFirst", true)

        suspend private fun fetchData() {
                if(!isFirstRun){
                    LogUtils.d("DataService", "isFirst:$isFirstRun")
                    serviceScope.launch {
                    try {
                        val cipherText = CiperTextUtil.encrypt(get("wxOaOpenid","sss"))//这些默认值也许可以更好
                        val resultGetRole = repository.fetchRole(cipherText, AppConstants.headerMap)
                        resultGetRole?.let {
                            LogUtils.d("DataService", "Received GetRole info: $it")
                        }

                        val resultLogin = repository.doLogin(AppConstants.headerMap)
                        resultLogin?.let {
                            LogUtils.d("DataService", "Received Login info: $it")
                        }

                        val resultKid = repository.fetchStudents(AppConstants.headerMap)
                        resultKid?.let {
                            LogUtils.d("DataService", "Received Student info: $it")
                        }

                        val resultBalance = repository.fetchBalance(AppConstants.headerMap)
                        resultBalance?.let {
                            LogUtils.d("DataService", "Received Balance info: $it")
                        }

                        val memberFlowRequest = MemberFlowJsonBuilder(
                            get("kidUuid","ss"),
                            listOf(2, 5, 6, 7),
                            7,
                            1,
                            200,
                            DateUtils.Date2Str(0,true),
                            DateUtils.Date2Str(1)
                        )
                        val resultMemberFlow =
                            repository.fetchMemberFlow(memberFlowRequest, AppConstants.headerMap)
                        val memberFlowRequestAll = MemberFlowJsonBuilder(
                            get("kidUuid","ss"),
                            listOf(2, 5, 6, 7),
                            7,
                            1,
                            1000,
                            DateUtils.Date2Str(get("dashboardUpdateLimit",-50),true),
                            DateUtils.Date2Str(1)
                        )
                        val resultMemberFlowAll =
                            repository.fetchMemberFlow(memberFlowRequestAll, AppConstants.headerMap)
                        resultMemberFlow?.let {
                            it.error?.takeIf { it.isNotEmpty() }?.let { error ->
                                Log.e("DataService", "Error fetching MemberFlow: $error")
                            }

                            LogUtils.d("DataService", "Received MemberFlow info: $it")

                        }
                        mapOf("memberFlow" to resultMemberFlow, "balance" to resultBalance)
                        //todo 怎么看起来怪怪的

                    } catch (e: IOException) {
                        e.message?.let { Log.e("DataService", it) }
                    }
                }
            }
        }

}

