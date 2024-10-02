package homes.gensokyo.enigma.ui.oobe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import homes.gensokyo.enigma.MainActivity
import homes.gensokyo.enigma.adapter.SchoolAdapter
import homes.gensokyo.enigma.bean.GradeBean
import homes.gensokyo.enigma.bean.School
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.SettingUtils.put
import homes.gensokyo.enigma.util.TextUtils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GradeItem(val gradeId: Int, val gradeName: String)
data class ClassItem(val classId: Int, val className: String)

class SettingViewModel : ViewModel() {

    private var _selectedClassId = MutableLiveData<Int>()

    private val _inputtuedName = MutableLiveData<String>()
    val inputtedName: LiveData<String> = _inputtuedName

    private val _inputtedCarNumber = MutableLiveData<String>()
    val inputtedCarNumber: LiveData<String> = _inputtedCarNumber


    private val _selectedGradeId = MutableLiveData<Int>()
    val selectedGradeId: LiveData<Int> = _selectedGradeId

    private val _classNamesLiveData = MutableLiveData<List<ClassItem>>()
    val classNamesLiveData: LiveData<List<ClassItem>> = _classNamesLiveData

    private val _gradeNamesLiveData = MutableLiveData<List<GradeItem>>()
    val gradeNamesLiveData: LiveData<List<GradeItem>> = _gradeNamesLiveData

    private val _schoolData = MutableLiveData<List<School>>()
    val schoolData: LiveData<List<School>> = _schoolData
    val schoolInput = MutableLiveData<String>()
    val isSchoolListVisible = MutableLiveData<Boolean>().apply { value = true }
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    var schoolAdapter: SchoolAdapter = SchoolAdapter(this,emptyList())

    init {

        schoolInput.observeForever { schoolName -> fetchAllowedSearchSchools(schoolName) }
    }

    private fun fetchAllowedSearchSchools(schoolName: String) {
        serviceScope.launch {
            try {
                val resultGetschool = MainActivity.repository.fetchAllowedSearchSchools(
                    schoolInput.value.toString(),
                    AppConstants.headerMap
                )
                Log.d("SettingViewModel", "Received school info: $resultGetschool")

                resultGetschool?.let {
                    withContext(Dispatchers.Main) {
                        _schoolData.postValue(it)
                        isSchoolListVisible.postValue(true)
                        Log.d("SettingViewModel", "isSchoolListVisible: ${isSchoolListVisible.value}")

                        Log.d("SettingViewModel", "Updated school adapter data ${schoolAdapter.getItemCount()} ")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingViewModel", "Error fetching school info: $e", e)
            }
        }
    }
    private var resultGetClasses: List<GradeBean>? = emptyList()
    private fun fetchAllNotGraduateClasses(tenantId:Int): List<GradeBean>? {
        serviceScope.launch {
            try {
                resultGetClasses = MainActivity.repository.fetchAllNotGraduateClasses4Tenant(
                    tenantId,
                    AppConstants.headerMap
                )
                Log.d("SettingViewModel", "Received class info: $resultGetClasses")
                if (resultGetClasses != null) {
                    val gradeList = mutableListOf<GradeItem>()
                    resultGetClasses!!.forEachIndexed { index, gradeInfo ->
                        //Log.d("SettingViewModel", "Class $index: ${classInfo.className}")
                        val gradeItem = GradeItem(gradeInfo.classId, gradeInfo.className)
                        gradeList.add(gradeItem)
                        //Log.d("SettingViewModel", "Added ClassItem: $classItem")
                    }
                    Log.d("SettingViewModel", "Grade list: ${gradeList}")
                    _gradeNamesLiveData.postValue(gradeList)
                }

            } catch (e: Exception) {
                Log.e("SettingViewModel", "Error fetching class info: $e", e)
            }
        }
        return null
    }
    fun submitStudentData(enteredName: String, selectedClassId: Int,enteredCardNumber:String) {
        serviceScope.launch {
            try {
                val result = MainActivity.repository.fetchStudentDetails(
                    enteredName,
                    selectedClassId,
                    AppConstants.headerMap
                )
                //TODO 做是否存在校验


                if (result == null){
                    "请确认您输入的信息没有错误。".toast()

                } else {
                        Log.d("SettingViewModel", "Received student info: ${result.studentId}")
                        put("isFirst", false)
                        put("kidUuid",result.uuid)
                        put("wxOaOpenid",result.parentStudents[0].parent.wxOaOpenid)
                        put("studentName",enteredName)
                        put("unilateralDeclarationCardNumber",enteredCardNumber)// 单方面声明的卡号
                    }
                } catch (e:Exception){
                Log.e("SettingViewModel", "Error fetching student info: ${e.message}")
            }
        }

    }
//TODO 终于做完啦 该做班级选择了！
    fun onSchoolSelected(school: School) {
        Log.d("SettingViewModel", "Selected school: $school")
        isSchoolListVisible.postValue(false)
        val AllClasses = fetchAllNotGraduateClasses(school.tenantId)
        Log.d("SettingViewModel", "All classes: ${AllClasses}")


    }


    //处理class逻辑
    fun onGradeSelected(gradeId: Int) {
        _selectedGradeId.value = gradeId
        val selectedGrade = resultGetClasses?.find { it.classId == gradeId }
        selectedGrade?.let {
            val classList = mutableListOf<ClassItem>()
            Log.d("GradeFinder", "Found Class: ${it.className}, Class ID: ${it.classId}")
            val childs = it.childs // List<ClassBean>
            childs!!.forEach { child ->
                val classItem = ClassItem(child.classId, child.className)
                classList.add(classItem)

            }
            _classNamesLiveData.postValue(classList)
            Log.d("GradeFinder", "Class: ${classList}")

        } ?: run {
            Log.w("GradeFinder", "No class found with ID: $gradeId")
        }
        //Log.d("ViewModel", "Grade selected. ID = $gradeId")
    }
    fun onClassSelected(classId: Int){
        _selectedClassId.value = classId
    }



}
