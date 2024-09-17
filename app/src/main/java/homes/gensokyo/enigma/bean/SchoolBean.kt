package homes.gensokyo.enigma.bean

data class School(
    val schoolId: Int,
    val schoolName: String,
    val schoolShortName: String,
    val schoolType: String?,
    val agent: String?,
    val subsidiaryAgentId: Int?,
    val tenantId: Int,
    val schoolCode: String,
    val contacts: String?,
    val phone: String?,
    val address: String?,
    val openingDate: String?,
    val closingDate: String?,
    val trialStatus: Int,
    val trialPeriod: String?,
    val trialDate: String?,
    val serviceStatus: Int,
    val subStatus: Int,
    val status: Int,
    val creator: String?,
    val createTime: String,
    val modifier: String?,
    val modifyTime: String?,
    val remark: String?,
    val areaCode: String?,
    val coordinate: String?,
    val allowSearch: Int,
    val studySection: String?
)


