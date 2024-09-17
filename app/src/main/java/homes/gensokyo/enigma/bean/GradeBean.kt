package homes.gensokyo.enigma.bean

data class GradeBean(
    val classId: Int,
    val tenantId: Any? = null,
    val parentId: Any? = null,
    val className: String,
    val classCode: Any? = null,
    val campusCode: Any? = null,
    val schoolYear: Int,
    val deptType: Any? = null,
    val graduateStatus: Any? = null,
    val graduateTime: Any? = null,
    val classOrder: Any? = null,
    val description: Any? = null,
    val classesWorkers: Any? = null,
    val childs: List<ClassBean> = listOf(),
    val totalStudent: Any? = null,
    val totalParent: Any? = null
)
data class ClassBean(

    val classId: Int,
    val tenantId: Any? = null,
    val parentId: Any? = null,
    val className: String,
    val classCode: Any? = null,
    val campusCode: Any? = null,
    val schoolYear: Int,
    val deptType: Any? = null,
    val graduateStatus: Any? = null,
    val graduateTime: Any? = null,
    val classOrder: Any? = null,
    val description: Any? = null,
    val classesWorkers: Any? = null,
    val childs: List<Any> = listOf(),
    val totalStudent: Any? = null,
    val totalParent: Any? = null

)