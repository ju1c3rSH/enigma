package homes.gensokyo.enigma.util

object  AppConstants {
    const val getNoAuth = "https://wx.ivxiaoyuan.com/sc/h5/officialAccountLoginManage/getUserRolesNoAuth?ciphertext="
    const val getBalance = "https://wx.ivxiaoyuan.com/sc/consume/h5/query/getMemberBalance?objectUuid="
    const val getAllStudentsOfParentUrl = "https://wx.ivxiaoyuan.com/sc/basic/h5/studentQuery/getAllStudentsOfParent?queryWay=5"
    const val getMemberFlow = "https://wx.ivxiaoyuan.com/sc/consume/h5/query/getMemberFlow"
    const val loginUrl = "https://wx.ivxiaoyuan.com/sc/h5/officialAccountLoginManage/login"
    const val getAllowSearchSchoolUrl = "https://wx.ivxiaoyuan.com/sc/om/basic/h5/schoolQuery/getAllowSearchSchool?paramStr="
    const val capturePhoto = "https://wx.ivxiaoyuan.com/sc/files/capturePhoto/"
    const val personPhotoBase = "https://wx.ivxiaoyuan.com/sc/files/personPhoto/"
    const val queryUrl = "https://wx.ivxiaoyuan.com/sc/student/h5/dynamicParentViewQuery/query"
    const val copyPersonCode = "cfb0961b2c844923902ed6544a878467"
    const val getListByStudentNameUrl = "https://wx.ivxiaoyuan.com/sc/collect/h5/infoManage/getListByStudentNameInClass"
    const val getAllNotGraduateClasses4TenantUrl = "https://wx.ivxiaoyuan.com/sc/collect/h5/infoManage/getAllNotGraduateClasses4Tenant"
    //{"objectUuid":"","types":[2,5,6,7],"objectType":7,"page":1,"rows":15,"startTime":"2023-06-24 00:00:00","endTime":"2023-09-24 23:59:59"}
    //String memberFlowJson = "{\"objectUuid\":\"\",\"types\":[2,5,6,7],\"objectType\":7,\"page\":1,\"rows\":15,\"startTime\":\"2023-06-24 00:00:00\",\"endTime\":\"2023-09-24 23:59:59\"}";

    const val jsonStr = "{\"appid\":\"wxddbbb3d7ad98c9a4\",\"role\":\"parent\"}"
    val headerMap = mapOf(
        "Accept" to "application/json, text/plain, */*",
        "Content-Type" to "application/json; charset=UTF-8",
        "User-Agent" to
        "Mozilla/5.0 (Linux; Android 13; M2102K1G Build/TKQ1.220829.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/130.0.6723.103 Mobile Safari/537.36 XWEB/1300333 MMWEBSDK/20241103 MMWEBID/5267 MicroMessenger/8.0.54.2741(0x28003641) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
    )
// TODO: url改成路由 

}