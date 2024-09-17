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

    private val _memberFlow = MutableLiveData<memberflowbean>()
    private val memberFlow :LiveData<memberflowbean> = _memberFlow

    private val intervalMillis: Long = 180000

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
    fun updateMemberFlow(newMemberFlow: memberflowbean) {
        _memberFlow.postValue(newMemberFlow)
    }


    suspend fun refreshData(headers: Map<String, String>) {
        viewModelScope.launch {
            try {
                val cipherText = CiperTextUtil.encrypt(get("wxOaOpenid","000"))
                val resultGetRole = repository.fetchRole(cipherText, AppConstants.headerMap)
                resultGetRole?.let {

                }

                val resultLogin = repository.doLogin(AppConstants.headerMap)
                resultLogin?.let {

                }

                val resultKid = repository.fetchStudents(AppConstants.headerMap)
                resultKid?.let {

                }

                val resultBalance = repository.fetchBalance(AppConstants.headerMap)
                resultBalance?.let {
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
                val resultMemberFlow =
                    repository.fetchMemberFlow(memberFlowRequest, AppConstants.headerMap)
                Log.i("DataService", "Received MemberFlow info: $resultMemberFlow")
                if (resultBalance != null && resultKid != null) {
                    val studentName = resultKid.firstOrNull()?.studentName ?: "默认姓名"
                    if (resultMemberFlow != null) {
                        Log.i("refreshData", resultMemberFlow.toString())

                        _studentData.postValue(
                            DataState.Success(

                                UserDataBean(
                                    balance = resultBalance.balance.toString(),
                                    studentName = studentName,
                                    cardNumber = resultBalance.cardNumber,
                                    consumptionCount = resultMemberFlow.total.toString()
                                )

                            )
                        )
                    }
                } else {
                    _studentData.postValue(DataState.Error(Throwable(resultMemberFlow.toString() + resultKid)))
                }
            }catch (e: Exception) {
            _studentData.postValue(DataState.Error(e))
        }
     }
    }
}
sealed class DataState<out T> {
    // 数据请求成功状态
    data class Success<T>(val data: UserDataBean) : DataState<T>()

    // 错误状态
    data class Error(val exception: Throwable) : DataState<Nothing>()

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


