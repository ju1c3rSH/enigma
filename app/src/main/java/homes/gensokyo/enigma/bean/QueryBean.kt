package homes.gensokyo.enigma.bean

data class QueryResponse(
    val total: Int,
    val page: Int,
    val rows: Int,
    val sortName: String?,
    val sortOrder: String?,
    val datas: List<QueryData>,
    val typeCode: List<Int>,
    val copyPersonCode: String,
    val endDateTime: String
)

data class QueryData(
    val id: Int,
    val tenantId: Int,
    val originId: Int,
    val typeCode: Int,
    val typeName: String,
    val title: String,
    val publisherName: String,
    val publisherCode: String?,
    val receiverName: String?,
    val receiverCode: String?,
    val copyPersonName: String,
    val copyPersonCode: String,
    val content: String,
    val picture: String,
    val publishTime: String,
    val status: Int,
    val isRead: Int,
    val reserver1: String, // 可能需要解析为一个对象，如果内容复杂
    val reserver2: String,
    val reserver3: String?,
    val reserver4: String,
    val reserver5: String?
)
data class ReserverCoordinates(
    val x1: Int,
    val y1: Int,
    val x2: Int,
    val y2: Int
)
data class QueryRequest(
    val page: Int,
    val rows: Int,
    val copyPersonCode: String,
    val typeCode: List<Int>
) {
    class Builder {
        private var page: Int = 1
        private var rows: Int = 5
        private var copyPersonCode: String = ""
        private var typeCode: MutableList<Int> = mutableListOf()

        fun setPage(page: Int) = apply { this.page = page }
        fun setRows(rows: Int) = apply { this.rows = rows }
        fun setCopyPersonCode(copyPersonCode: String) = apply { this.copyPersonCode = copyPersonCode }
        fun setTypeCode(typeCode: List<Int>) = apply { this.typeCode = typeCode.toMutableList() }

        fun build(): QueryRequest {
            return QueryRequest(page, rows, copyPersonCode, typeCode)
        }
    }
}
