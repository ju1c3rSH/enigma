package homes.gensokyo.enigma.bean
data class Student(
    val studentId: Int,
    val tenantId: Int,
    val uuid: String,
    val tuid: Int,
    val studentCode: String?,
    val campusCode: String?,
    val studentNumber: String,
    val classes: ClassDetail,
    val schoolYear: Int,
    val studentName: String,
    val studentNamePinyin: String,
    val headSculpture: String,
    val photo: String,
    val sex: String?,
    val height: String?,
    val weight: String?,
    val hobby: String?,
    val birthday: String?,
    val idcardType: String?,
    val idcard: String?,
    val censusRegister: String?,
    val addr: String?,
    val loginId: String?,
    val phoneno: String?,
    val wxOpenid: String?,
    val wxUnionid: String?,
    val wxOaOpenid: String?,
    val abcOpenid: String?,
    val dataOrigin: Int,
    val studyStatus: Int,
    val registrationStatus: Int,
    val graduatedSchool: String?,
    val operator: String,
    val status: Int,
    val accommodationType: String,
    val icCard1: String?,
    val icCard2: String?,
    val icCard3: String?,
    val subServStatus: Int,
    val subServContractName: String?,
    val subServLastValidTime: String?,
    val description: String?,
    val creator: String,
    val createTime: String,
    val modifier: String,
    val modifyTime: String,
    val cardNumber: String?,
    val roomId: String?,
    val parentStudents: List<ParentStudent>,
    val studentFaces: List<StudentFace>
)

data class ClassDetail(
    val classId: Int,
    val tenantId: Int,
    val parentId: Int,
    val className: String,
    val classCode: String?,
    val campusCode: String?,
    val schoolYear: Int,
    val deptType: Int,
    val graduateStatus: Int,
    val graduateTime: String?,
    val classOrder: Int,
    val description: String?,
    val classesWorkers: List<ClassWorker>
)

data class ClassWorker(
    val id: Int,
    val tenantId: Int,
    val workers: WorkerDetail,
    val dutyName: String,
    val dutyCode: String,
    val subjectName: String?,
    val subjectCode: String
)

data class WorkerDetail(
    val workersId: Int,
    val tenantId: Int,
    val uuid: String,
    val tuid: Int,
    val workersNumber: String?,
    val workersName: String,
    val workersNamePinyin: String,
    val workersCode: String?,
    val campusCode: String?,
    val loginId: String,
    val headSculpture: String,
    val photo: String,
    val sex: String?,
    val birthday: String?,
    val idcardType: Int,
    val idcard: String?,
    val job: String?,
    val phoneno: String,
    val censusRegister: String?,
    val addr: String?,
    val icCard1: String?,
    val icCard2: String?,
    val icCard3: String?,
    val wxOpenid: String?,
    val wxUnionid: String?,
    val wxOaOpenid: String?,
    val abcOpenid: String?,
    val officialAccountSubStatus: Int,
    val dataOrigin: Int,
    val status: Int,
    val description: String?,
    val creator: String,
    val createTime: String,
    val modifier: String,
    val modifyTime: String,
    val cardNumber: String?,
    val workersRoles: List<WorkerRole>,
    val workersFaces: List<WorkerFace>,
    val workersDeptRels: List<WorkerDeptRel>
)

data class WorkerRole(
    val id: Int,
    val tenantId: Int,
    val role: RoleDetail,
    val priority: Int
)

data class RoleDetail(
    val roleId: Int,
    val tenantId: Int,
    val roleName: String,
    val roleCode: String,
    val roleType: String,
    val extendType: String,
    val description: String,
    val addTime: String,
    val updateTime: String?,
    val orderCode: String,
    val hasChild: String,
    val isDefault: String,
    val deleted: String
)

data class WorkerFace(
    val faceId: Int,
    val tenantId: Int,
    val faceLoadStatus: Int,
    val photo: String,
    val orderNumber: Int,
    val photoOrigin: Int,
    val status: Int,
    val createTime: String,
    val modifyTime: String
)

data class WorkerDeptRel(
    val id: Int,
    val tenantId: Int,
    val workersDept: DeptDetail,
    val isManager: Int,
    val creator: String,
    val createTime: String
)

data class DeptDetail(
    val id: Int,
    val tenantId: Int,
    val pid: Int?,
    val deptName: String,
    val deptLevel: Int,
    val isDefault: Int,
    val status: Int,
    val creator: String,
    val createTime: String,
    val modifier: String?,
    val modifyTime: String?
)

data class ParentStudent(
    val id: Int,
    val tenantId: Int,
    val parent: ParentDetail,
    val kinship: String,
    val creator: String,
    val createTime: String,
    val modifier: String,
    val modifyTime: String
)

data class ParentDetail(
    val parentId: Int,
    val tenantId: Int,
    val uuid: String,
    val parentNumber: String?,
    val parentName: String,
    val parentCode: String?,
    val loginId: String,
    val headSculpture: String?,
    val wxNickName: String?,
    val wxHeadSculpture: String?,
    val photo: String?,
    val sex: String?,
    val birthday: String?,
    val idcardType: String?,
    val idcard: String?,
    val censusRegister: String?,
    val addr: String?,
    val phoneno: String,
    val wxOpenid: String?,
    val wxUnionid: String?,
    val wxOaOpenid: String?,
    val abcOpenid: String?,
    val officialAccountSubStatus: String?,
    val subServStatus: String?,
    val subServContractName: String?,
    val subServLastValidTime: String?,
    val dataOrigin: Int,
    val status: Int,
    val description: String?,
    val creator: String,
    val createTime: String,
    val modifier: String,
    val modifyTime: String,
    val parentFaces: List<ParentFace>
)

data class ParentFace(
    val faceId: Int,
    val tenantId: Int,
    val faceLoadStatus: Int,
    val photo: String,
    val orderNumber: Int,
    val photoOrigin: Int,
    val status: Int,
    val createTime: String,
    val modifyTime: String
)

data class StudentFace(
    val faceId: Int,
    val tenantId: Int,
    val faceLoadStatus: Int,
    val photo: String,
    val orderNumber: Int,
    val photoOrigin: Int,
    val originFaceUrl: String?,
    val status: Int,
    val createTime: String,
    val modifyTime: String
)
