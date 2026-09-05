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
import homes.gensokyo.enigma.util.CipherTextUtil
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.DateUtils
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.SettingUtils.sharedPreferences
import homes.gensokyo.enigma.util.TextUtils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

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

    //必须在 init 之前初始化（Kotlin 按声明顺序执行初始化）
    private val refreshing = AtomicBoolean(false)

    init {
        startPeriodicRefresh(headers = AppConstants.headerMap)
    }

    //定时刷新任务
    private fun startPeriodicRefresh(headers: Map<String, String>) {
        val intervalMillis: Long? = get("updateRate","60000").toLongOrNull()?.coerceAtLeast(15000L)
        viewModelScope.launch {
            flow {
                while (true) {
                    emit(Unit)
                    LogUtils.d("startPeriodicRefresh", intervalMillis.toString())
                    delay(intervalMillis ?: 60000L)
                }
            }.collect {
                refreshData(headers)
            }
        }
    }

    private fun findStudentIndex(resultKid: List<Student>?): Int {
        val savedUuid = get("kidUuid", "")
        resultKid?.forEachIndexed { index, student ->
            if (savedUuid.isNotEmpty() && student.uuid == savedUuid) {
                LogUtils.d("findStudentIndex", "matched by uuid: ${student.studentName} #$index")
                return index
            }
        }
        val savedName = get("studentName", "默认名字")
        resultKid?.forEachIndexed { index, student ->
            if (student.studentName == savedName) {
                LogUtils.d("findStudentIndex", "matched by name: $savedName + $index")
                return index
            }
        }
        return -1
    }
    val dashboardUpdateLimit = get("dashboard_update_limit", 50)
    suspend fun refreshData(headers: Map<String, String>) {
        //防重入：上一轮未结束时跳过，避免请求堆积
        if (!refreshing.compareAndSet(false, true)) return
        //已有数据时不回退到 Loading，避免整页骨架屏闪烁
        if (_studentData.value !is DataState.Success) {
            _studentData.postValue(DataState.Loading)
        }
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val cipherText = CipherTextUtil.generateCipherText(get("wxOaOpenid","000"))
                val resultGetRole = repository.fetchRole(cipherText, AppConstants.headerMap)
                if (resultGetRole != null) {
                    LogUtils.d("UsrMdl", "Received Role info ok；$cipherText")
                }

                repository.doLogin(AppConstants.headerMap)

                val qrBuild = QueryRequest.Builder().setPage(1)
                    .setRows(30)
                    .setCopyPersonCode(get("kidUuid","111"))
                    .setTypeCode(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
                    .build()
                LogUtils.d("queryData", "Query info: $qrBuild")

                val resultQuery = repository.queryData(AppConstants.headerMap, qrBuild)
                resultQuery?.let { query ->
                    LogUtils.d("queryData", "Received Query info, ${query.datas?.size ?: 0} items")
                    _queryData.postValue(query)
                }

                val resultKid = repository.fetchStudents(AppConstants.headerMap)
                LogUtils.d("StudentList", "fetched ${resultKid?.size ?: 0} students")
                //这里智威后台发癫，会返回所有同一parent的kid，并且kid顺序有变化

                LogUtils.d("UsrDataMdl", "$dashboardUpdateLimit")
                /*
                弃用代码：旧版串行拉取，已被下方并发版取代
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
                        DateUtils.Date2Str(-dashboardUpdateLimit, false),
                        DateUtils.Date2Str(1)
                    )
                    repository.fetchMemberFlow(memberFlowAllRequest, AppConstants.headerMap)
                }

                val resultBalance = resultBalanceDeferred.await()
                val resultMemberFlowAll = resultMemberFlowAllDeferred.await()
                val resultMemberFlow = resultMemberFlowDeferred.await()

                if (resultBalance == null || resultKid == null) {
                    LogUtils.d("refreshData", "resultBalance is null=${resultBalance == null} resultKid is null=${resultKid == null} kidUuid=${get("kidUuid","")}")
                    _studentData.postValue(DataState.Error("获取余额或学生列表失败，请检查网络后重试"))
                    return@launch
                }
                if (!BuildConfig.DEBUG &&
                    !get("disableCardCheck", false) &&
                    get("unilateralDeclarationCardNumber","fake") != resultBalance.cardNumber
                ) {
                    LogUtils.d("UsrdataModel", "card mismatch saved=${get("unilateralDeclarationCardNumber","fake")} server=${resultBalance.cardNumber}")
                    val editor = sharedPreferences!!.edit()
                    editor.clear()
                    editor.commit()
                    withContext(Dispatchers.Main) {
                        "卡号不正确，强制退出！".toast()
                        forceRestartApp()
                    }
                    return@launch
                }
                val studentIndex = findStudentIndex(resultKid)
                if (studentIndex < 0) {
                    LogUtils.d("refreshData", "studentIndex not found kidUuid=${get("kidUuid","")} name=${get("studentName","")} resultKid size=${resultKid.size}")
                    _studentData.postValue(DataState.Error("未找到学生信息，请重新完成引导"))
                    return@launch
                }
                // memberFlow 允许为空（网络抖动），仍可展示余额与学籍信息
                val studentName = resultKid[studentIndex].studentName ?: "默认姓名"
                val className = resultKid[studentIndex].classes.className ?: ""
                val studentNamePinyin = resultKid[studentIndex].studentNamePinyin ?: ""
                val headSculpture = resultKid[studentIndex].headSculpture ?: ""
                //排序前置到后台线程，UI 不再每次重组重排
                val sortedFlow = resultMemberFlow?.copy(datas = resultMemberFlow.datas?.sortedByDescending { it.consumeTime })
                val sortedFlowAll = resultMemberFlowAll?.copy(datas = resultMemberFlowAll.datas?.sortedByDescending { it.consumeTime })
                sortedFlow?.let { _memberFlow.postValue(it) }
                sortedFlowAll?.let { _memberFlowAll.postValue(it) }
                val consumptionCount = resultMemberFlow?.total?.toString() ?: "0"
                _studentData.postValue(
                    DataState.Success(
                        UserDataBean(
                            balance = resultBalance.balance.toString(),
                            studentName = studentName,
                            cardNumber = resultBalance.cardNumber,
                            consumptionCount = consumptionCount,
                            studentNamePinyin = studentNamePinyin,
                            headSculpture = headSculpture,
                            className = className,
                        )
                    )
                )
            } catch (e: Exception) {
                LogUtils.d("refreshData", "exception ${e.message} ${e.stackTraceToString().take(300)}")
                _studentData.postValue(DataState.Error(e.message ?: "网络异常"))
            } finally {
                refreshing.set(false)
            }
        }
    }

    private fun forceRestartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        context.startActivity(Intent.makeRestartActivityTask(componentName))
        Runtime.getRuntime().exit(0)
    }

    private fun studentIndexValid(resultKid: List<Student>?): Boolean =
        findStudentIndex(resultKid) >= 0
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


