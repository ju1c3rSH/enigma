package homes.gensokyo.enigma.logic.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val userName: String,
    val userAppid: String,
    val dataTableName: String,
    val cardNumber: String?,
    val totalCount: Int? ,
    val aMonthCount: Int,
    val userObjUuid: String
)


