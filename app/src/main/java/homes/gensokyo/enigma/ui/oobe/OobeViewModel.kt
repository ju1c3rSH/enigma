package homes.gensokyo.enigma.ui.oobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import homes.gensokyo.enigma.MainApplication.Companion.repository
import homes.gensokyo.enigma.bean.ClassBean
import homes.gensokyo.enigma.bean.School
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.util.SettingUtils.put
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

data class GradeItem(val gradeId: Int, val gradeName: String)
data class ClassItem(val classId: Int, val className: String)

data class OobeUiState(
    val step: Int = 1,
    val query: String = "",
    val searching: Boolean = false,
    val schools: List<School> = emptyList(),
    val selectedSchool: School? = null,
    val grades: List<GradeItem> = emptyList(),
    val classesLoading: Boolean = false,
    val selectedGradeId: Int? = null,
    val classes: List<ClassItem> = emptyList(),
    val selectedClassId: Int? = null,
    val name: String = "",
    val cardNumber: String = "",
    val submitting: Boolean = false,
    val error: String? = null,
)

class OobeViewModel : ViewModel() {

    private val _state = MutableStateFlow(OobeUiState())
    val state = _state

    private val _finished = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val finished = _finished.asSharedFlow()

    private var searchJob: Job? = null
    private var gradeBeans: List<homes.gensokyo.enigma.bean.GradeBean> = emptyList()

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q, error = null) }
        searchJob?.cancel()
        if (q.isBlank()) {
            _state.update { it.copy(schools = emptyList(), searching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _state.update { it.copy(searching = true) }
            val result = try {
                repository.fetchAllowedSearchSchools(q.trim(), AppConstants.headerMap)
            } catch (e: Exception) {
                LogUtils.d("OobeViewModel", "search error $e")
                null
            }
            _state.update { it.copy(searching = false, schools = result.orEmpty()) }
        }
    }

    fun selectSchool(school: School) {
        _state.update {
            it.copy(
                selectedSchool = school,
                step = 2,
                grades = emptyList(),
                classes = emptyList(),
                selectedGradeId = null,
                selectedClassId = null,
                classesLoading = true,
                error = null
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = try {
                repository.fetchAllNotGraduateClasses4Tenant(school.tenantId, AppConstants.headerMap)
            } catch (e: Exception) {
                LogUtils.d("OobeViewModel", "grade error $e")
                null
            }
            gradeBeans = result.orEmpty()
            val grades = gradeBeans.map { GradeItem(it.classId, it.className) }
            _state.update {
                it.copy(
                    grades = grades,
                    classesLoading = false,
                    error = if (grades.isEmpty()) "未获取到年级信息，请更换学校或稍后重试" else null
                )
            }
        }
    }

    fun selectGrade(gradeId: Int) {
        val classes = gradeBeans.find { it.classId == gradeId }
            ?.childs.orEmpty()
            .map { ClassItem(it.classId, it.className) }
        _state.update {
            it.copy(
                selectedGradeId = gradeId,
                classes = classes,
                selectedClassId = null
            )
        }
    }

    fun selectClass(classId: Int) {
        _state.update { it.copy(selectedClassId = classId) }
    }

    fun changeSchool() {
        _state.update { it.copy(step = 1, selectedSchool = null, grades = emptyList(), classes = emptyList(), selectedGradeId = null, selectedClassId = null) }
    }

    fun goNext() {
        val s = _state.value
        if (s.step == 2 && s.selectedClassId != null) {
            _state.update { it.copy(step = 3, error = null) }
        }
    }

    fun goBack(): Boolean {
        val s = _state.value
        return when (s.step) {
            2 -> {
                _state.update { it.copy(step = 1, selectedSchool = null) }
                true
            }
            3 -> {
                _state.update { it.copy(step = 2, selectedClassId = null, error = null) }
                true
            }
            else -> false
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v, error = null) }
    fun onCardNumberChange(v: String) = _state.update { it.copy(cardNumber = v.filter { c -> c.isLetterOrDigit() }, error = null) }

    fun submit() {
        val s = _state.value
        val classId = s.selectedClassId ?: return
        if (s.name.isBlank() || s.cardNumber.isBlank()) {
            _state.update { it.copy(error = "请填写姓名和卡号") }
            return
        }
        _state.update { it.copy(submitting = true, error = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = try {
                repository.fetchStudentDetails(s.name.trim(), classId, AppConstants.headerMap)
            } catch (e: Exception) {
                LogUtils.d("OobeViewModel", "submit error $e")
                null
            }
            if (result == null || result.uuid == null) {
                _state.update { it.copy(submitting = false, error = "校验失败，请确认姓名与班级无误后重试") }
                _finished.tryEmit(false)
                return@launch
            }
            put("isFirst", false)
            put("kidUuid", result.uuid)
            val openid = result.parentStudents.firstOrNull()?.parent?.wxOaOpenid
            openid?.let { put("wxOaOpenid", it) }
            put("studentName", s.name.trim())
            put("unilateralDeclarationCardNumber", s.cardNumber.trim())
            _state.update { it.copy(submitting = false) }
            _finished.tryEmit(true)
        }
    }
}
