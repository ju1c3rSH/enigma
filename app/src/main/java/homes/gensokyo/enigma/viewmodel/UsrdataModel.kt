package homes.gensokyo.enigma.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import homes.gensokyo.enigma.BuildConfig
import homes.gensokyo.enigma.MainApplication.Companion.context
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.bean.*
import homes.gensokyo.enigma.util.CiperTextUtil
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.DateUtils
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.SettingUtils.sharedPreferences
import homes.gensokyo.enigma.util.TextUtils.toast
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class UsrdataModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsrdataModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsrdataModel(this, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class UsrdataModel(repository1: UsrdataModelFactory, private val repository: UserRepository) : ViewModel() {


    private val _studentData = MutableLiveData<DataState<UserDataBean>>()
    val studentData: LiveData<DataState<UserDataBean>> = _studentData

    private val _memberFlow = MutableLiveData<memberflowbean?>()
    val memberFlow : MutableLiveData<memberflowbean?> = _memberFlow

    private val _memberFlowAll = MutableLiveData<memberflowbean?>()
    val memberFlowAll : MutableLiveData<memberflowbean?> = _memberFlowAll

    private val _queryData = MutableLiveData<QueryResponse>()
    val queryData: LiveData<QueryResponse> = _queryData



    init {
        startPeriodicRefresh(headers = AppConstants.headerMap)
    }

    //定时刷新任务
    private fun startPeriodicRefresh(headers: Map<String, String>) {
        val intervalMillis: Long? = get("updateRate","60000").toLongOrNull()
        //TODO 这里实际上不是和Setting里面统一的
        viewModelScope.launch {
            flow {
                while (true) {
                    emit(Unit)
                    LogUtils.d("startPeriodicRefresh", intervalMillis.toString())
                    if (intervalMillis != null) {
                        delay(intervalMillis)
                    }
                }
            }.collect {
                refreshData(headers)
            }
        }
    }

    private fun findStudentIndex(resultKid: List<Student>?): Int {
        val savedName = get("studentName","默认名字")
        resultKid?.forEachIndexed { index, student ->
            //LogUtils.d("findStudentIndex", "savedName: ${savedName}")
            if (student.studentName == savedName) {
                LogUtils.d("findStudentIndex", "savedName: ${savedName} + $index")
                return index
            }
        }

        return -1
    }
    val dashboardUpdateLimit = get("dashboardUpdateLimit", -50)
    suspend fun refreshData(headers: Map<String, String>) {
        viewModelScope.launch {
            try {
                _studentData.postValue(
                    DataState.Loading
                )
                val cipherText = CiperTextUtil.encrypt(get("wxOaOpenid","000"))
                val resultGetRole = repository.fetchRole(cipherText, AppConstants.headerMap)
                resultGetRole?.let {
                    LogUtils.d("UsrMdl", "Received Role info: $it ；$cipherText   11"  +get("wxOaOpenid","000").toString())
                }

                val resultLogin = repository.doLogin(AppConstants.headerMap)
                resultLogin?.let {
                }
                val qrBuild = QueryRequest.Builder().setPage(1)
                    .setRows(30)
                    .setCopyPersonCode(get("kidUuid","111"))
                    .setTypeCode(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
                    .build()
                LogUtils.d("queryData", "Query info: $qrBuild")

                val resultQuery = repository.queryData(AppConstants.headerMap, qrBuild )!!
                resultQuery?.let {
                    LogUtils.d("queryData", "Received Query info: $it")
                }
                _queryData.postValue(resultQuery)
                //LogUtils.d("queryData", "Received Query info: $resultQuery")

                val resultKid = repository.fetchStudents(AppConstants.headerMap)

                resultKid?.joinToString(separator = "\n", prefix = "Students:\n") { student ->
                    "Name: ${student.studentName}, ID: ${student.studentId}, CN: ${student.cardNumber}"
                }?.let { LogUtils.d("StudentList", it) }
                //这里智威后台发癫，会返回所有同一parent的kid，并且kid顺序有变化

                LogUtils.d("UsrDataMdl", "$dashboardUpdateLimit")
                /*
                val resultBalance = repository.fetchBalance(AppConstants.headerMap)
                if (resultBalance != null) {
                    LogUtils.d("UsrMdl", "Received Balance info: $resultBalance")

                } else {
                    Log.e("UsrMdl", "Failed to fetch balance") // 记录错误信息
                    _studentData.postValue(DataState.Error("Failed to fetch balance")) // 更新 UI 状态

                }

                LogUtils.d("startDat",  DateUtils.Date2Str(-10,true) )
                val memberFlowRequest = MemberFlowJsonBuilder(
                    get("kidUuid","1111"),
                    listOf(2, 5, 6, 7),
                    7,
                    1,
                    100,
                    DateUtils.Date2Str(-10,true),
                    DateUtils.Date2Str(1)
                )
                val resultMemberFlow = repository.fetchMemberFlow(memberFlowRequest, AppConstants.headerMap)
                弃用代码
                 */
                val resultBalanceDeferred = async { repository.fetchBalance(AppConstants.headerMap) }
                val resultMemberFlowDeferred = async {
                    val memberFlowRequest = MemberFlowJsonBuilder(
                        get("kidUuid","1111"),
                        listOf(2, 5, 6, 7),
                        7,
                        1,
                        1000,
                        DateUtils.Date2Str(0,true),
                        DateUtils.Date2Str(1)
                    )
                    repository.fetchMemberFlow(memberFlowRequest, AppConstants.headerMap)
                }
                val resultMemberFlowAllDeferred = async {
                    val memberFlowAllRequest = MemberFlowJsonBuilder(
                        get("kidUuid","1111"),
                        listOf(2, 5, 6, 7),
                        7,
                        1,
                        1000,
                        DateUtils.Date2Str(dashboardUpdateLimit,false),
                        DateUtils.Date2Str(1)
                    )
                    repository.fetchMemberFlow(memberFlowAllRequest, AppConstants.headerMap)
                }

                fun restartApp() {

                    val packageManager = context.packageManager
                    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                    val componentName = intent?.component
                    val mainIntent = Intent.makeRestartActivityTask(componentName)
                    context.startActivity(mainIntent)
                    Runtime.getRuntime().exit(0)
                }
                val resultBalance = resultBalanceDeferred.await()!!
                val resultMemberFlowAll = resultMemberFlowAllDeferred.await()
                val resultMemberFlow = resultMemberFlowDeferred.await()
                if(!BuildConfig.DEBUG){
                    if(get("unilateralDeclarationCardNumber","fake") != resultBalance.cardNumber) {
                        //LogUtils.d("UsrDataModel", get("unilateralDeclarationCardNumber","fake") + " != " + resultBalance.cardNumber)
                        val editor = sharedPreferences!!.edit()
                        editor.clear()
                        editor.apply()
                        "卡号不正确，强制退出！".toast()
                        editor.clear()
                        editor.commit()
                        /*
                        if (sharedPreferences.all.isEmpty()) {
                            LogUtils.d("Preferences", "清除成功")
                        } else {
                            LogUtils.d("Preferences", "清除失败")
                        }
                         */
                        restartApp() }
                }



                LogUtils.d("UsrDataModel", "Received MemberFlow info: $resultMemberFlow")
                if (resultBalance != null && resultKid != null) {
                    val studentIndex = findStudentIndex(resultKid)
                    val studentName = resultKid[studentIndex].studentName ?: "默认姓名"
                    val className = resultKid[studentIndex].classes.className ?: ""
                    val studentNamePinyin = resultKid[studentIndex].studentNamePinyin ?: ""
                    val headSculpture = resultKid[studentIndex].headSculpture ?: ""

                    if (resultMemberFlow != null) {
                        LogUtils.d("refreshData", resultMemberFlow.toString())
                        _memberFlow.postValue(resultMemberFlow)
                        _memberFlowAll.postValue(resultMemberFlowAll)
                        _studentData.postValue(
                            DataState.Success(
                                UserDataBean(
                                    balance = resultBalance.balance.toString(),
                                    studentName = studentName,
                                    cardNumber = resultBalance.cardNumber,
                                    consumptionCount = resultMemberFlow.total.toString(),
                                    studentNamePinyin = studentNamePinyin,
                                    headSculpture = headSculpture,
                                    className = className,
                                )

                            )
                        )
                    }

                } else {
                    _studentData.postValue(
                        DataState.Error("err")
                    )
                    LogUtils.d("refreshData", "resultBalance is null")


                }
            }catch (e: Exception) {
            _studentData.postValue(e.message?.let { DataState.Error(it) })
        }
     }
    }
}
sealed class DataState<out T> {
    // 数据请求成功状态
    data class Success<T>(val data: UserDataBean) : DataState<T>()

    // 错误状态
    data class Error(val exception: String) : DataState<Nothing>()

    // 加载状态
    object Loading : DataState<Nothing>()
}

/*
public final data class UserDataBean(
    val balance: String,
    val studentName: String,
    val cardNumber: String,
    val consumptionCount: String,
    val resultMemberFlow: String
)
 */


