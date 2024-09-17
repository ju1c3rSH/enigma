package homes.gensokyo.enigma.bean

data class MemberFlowJsonBuilder(
    val objectUuid: String,
    val types: List<Int> = listOf(2, 5, 6, 7),
    val objectType: Int = 7,
    val page: Int = 1,
    val rows: Int=200,
    val startTime: String,
    val endTime: String
    /*
    listOf(2, 5, 6, 7),
                7,
                1,
                100,
     */
)

data class QueryJsonBuilder(
    val page: Int,
    val rows: Int,
    val copyPersonCode: String,
    val typeCode: List<Int>
)