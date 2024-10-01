package homes.gensokyo.enigma.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.bean.*
import homes.gensokyo.enigma.util.CiperTextUtil
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.DateUtils
import homes.gensokyo.enigma.util.SettingUtils.get
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

    private val intervalMillis: Long = 15000

    init {
        startPeriodicRefresh(headers = AppConstants.headerMap)
    }

    //定时刷新任务
    private fun startPeriodicRefresh(headers: Map<String, String>) {
        viewModelScope.launch {
            flow {
                while (true) {
                    emit(Unit)
                    delay(intervalMillis)
                }
            }.collect {
                refreshData(headers)
            }
        }
    }

    private fun findStudentIndex(resultKid: List<Student>?): Int {
        val savedName = get("studentName","默认名字")
        resultKid?.forEachIndexed { index, student ->
            //Log.i("findStudentIndex", "savedName: ${savedName}")
            if (student.studentName == savedName) {
                Log.i("findStudentIndex", "savedName: ${savedName} + $index")
                return index
            }
        }

        return -1
    }

    suspend fun refreshData(headers: Map<String, String>) {
        viewModelScope.launch {
            try {
                val cipherText = CiperTextUtil.encrypt(get("wxOaOpenid","000"))
                val resultGetRole = repository.fetchRole(cipherText, AppConstants.headerMap)
                resultGetRole?.let {
                    Log.i("UsrMdl", "Received Role info: $it ；$cipherText   11"  +get("wxOaOpenid","000").toString())
                }

                val resultLogin = repository.doLogin(AppConstants.headerMap)
                resultLogin?.let {
                }

                val resultKid = repository.fetchStudents(AppConstants.headerMap)

                resultKid?.joinToString(separator = "\n", prefix = "Students:\n") { student ->
                    "Name: ${student.studentName}, ID: ${student.studentId}, CN: ${student.cardNumber}"
                }?.let { Log.d("StudentList", it) }
                //这里智威后台发癫，会返回所有同一parent的kid，并且kid顺序有变化


                /*
                val resultBalance = repository.fetchBalance(AppConstants.headerMap)
                if (resultBalance != null) {
                    Log.i("UsrMdl", "Received Balance info: $resultBalance")

                } else {
                    Log.e("UsrMdl", "Failed to fetch balance") // 记录错误信息
                    _studentData.postValue(DataState.Error("Failed to fetch balance")) // 更新 UI 状态

                }

                Log.i("startDat",  DateUtils.Date2Str(-10,true) )
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
                        100,
                        DateUtils.Date2Str(-10,true),
                        DateUtils.Date2Str(1)
                    )
                    repository.fetchMemberFlow(memberFlowRequest, AppConstants.headerMap)
                }

                val resultBalance = resultBalanceDeferred.await()
                val resultMemberFlow = resultMemberFlowDeferred.await()




                Log.i("UsrDataModel", "Received MemberFlow info: $resultMemberFlow")
                if (resultBalance != null && resultKid != null) {
                    val studentIndex = findStudentIndex(resultKid)
                    val studentName = resultKid[studentIndex].studentName ?: "默认姓名"
                    val className = resultKid[studentIndex].classes.className ?: ""
                    val studentNamePinyin = resultKid[studentIndex].studentNamePinyin ?: ""
                    val headSculpture = resultKid[studentIndex].headSculpture ?: ""

                    if (resultMemberFlow != null) {
                        Log.i("refreshData", resultMemberFlow.toString())
                        _memberFlow.postValue(resultMemberFlow)
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
                    Log.i("refreshData", "resultBalance is null")


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


