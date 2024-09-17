package homes.gensokyo.enigma.bean


data class balanceBean(
    val memberId: Int,
    val tenantId: Int,
    val objectUuid: String,
    val objectType: Int,
    val memberCode: String,
    val cardNumber: String,
    val password: String,
    val cardStatus: Int,
    val cardCreator: String,
    val cardCreateTime: String,
    val balance: Double,
    val rechargeBalance: Double,
    val subsidyBalance: Double,
    val totalDailyQuota: Double,
    val groupBDailyQuota: Double,
    val groupCDailyQuota: Double,
    val quotaMode: Int,
    val quotaModifier: String,
    val quotaTime: String,
    val quotaStatus: Int,
    val faceStatus: String,
    val synStatus: Int,
    val faceCollectStatus: Int,
    val contractStatus: Int,
    val mobilePhone: String,
    val status: Int,
    val remark: String
)
