package homes.gensokyo.enigma.util

object  AppConstants {
    const val getNoAuth = "https://wx.ivxiaoyuan.com/sc/h5/officialAccountLoginManage/getUserRolesNoAuth?ciphertext="
    const val getBalance = "https://wx.ivxiaoyuan.com/sc/consume/h5/query/getMemberBalance?objectUuid="
    const val getAllStudentsOfParentUrl = "https://wx.ivxiaoyuan.com/sc/basic/h5/studentQuery/getAllStudentsOfParent?queryWay=5"
    const val getMemberFlow = "https://wx.ivxiaoyuan.com/sc/consume/h5/query/getMemberFlow"
    const val loginUrl = "https://wx.ivxiaoyuan.com/sc/h5/officialAccountLoginManage/login?appid=wxddbbb3d7ad98c9a4&role=parent"
    const val loginParams = "appid=wxddbbb3d7ad98c9a4&role=parent"
    const val getAllowSearchSchoolUrl = "https://wx.ivxiaoyuan.com/sc/om/basic/h5/schoolQuery/getAllowSearchSchool?schoolName="//schoolName=&t=1726471689712
    const val capturePhoto = "https://wx.ivxiaoyuan.com/sc/files/capturePhoto/"
    const val queryUrl = "https://wx.ivxiaoyuan.com/sc/student/h5/dynamicParentViewQuery/query"
    const val copyPersonCode = "cfb0961b2c844923902ed6544a878467"
    const val getListByStudentNameUrl = "https://wx.ivxiaoyuan.com/sc/basic/h5/studentQuery/getListByStudentNameInClass"
    const val getAllNotGraduateClasses4TenantUrl = "https://wx.ivxiaoyuan.com/sc/basic/h5/classesManage/getAllNotGraduateClasses4Tenant?tenantId="//https://wx.ivxiaoyuan.com/sc/basic/h5/classesManage/getAllNotGraduateClasses4Tenant?tenantId=641&t=1726559987083
    //{"objectUuid":"","types":[2,5,6,7],"objectType":7,"page":1,"rows":15,"startTime":"2023-06-24 00:00:00","endTime":"2023-09-24 23:59:59"}
    //String memberFlowJson = "{\"objectUuid\":\"\",\"types\":[2,5,6,7],\"objectType\":7,\"page\":1,\"rows\":15,\"startTime\":\"2023-06-24 00:00:00\",\"endTime\":\"2023-09-24 23:59:59\"}";

    val jsonStr = "{\"appid\": \"wxddbbb3d7ad98c9a4\", \"role\": \"parent\"}"
    val headerMap = HashMap<String, String>().apply {
        put(
            "User-Agent",
            "FuckYou/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) > AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 > Chrome/37.0.0.0 Mobile Safari/537.36 > MicroMessenger/6.0.2.56_r958800.520 NetType/WIFI"
        )
    }

}