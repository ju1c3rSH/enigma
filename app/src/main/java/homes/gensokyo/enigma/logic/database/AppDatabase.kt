package homes.gensokyo.enigma.logic.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import homes.gensokyo.enigma.MainActivity.Companion.context
import homes.gensokyo.enigma.logic.database.dao.UsersDao
import homes.gensokyo.enigma.logic.database.model.User


@Database(
    version = 11,
    entities = [User::class],
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun UsersDao(): UsersDao

    companion object {
        val instance by lazy {
            Room.databaseBuilder(
                context, AppDatabase::class.java, "app_database"
            ).apply {
                fallbackToDestructiveMigration()
            }.build()
        }
    }
}

/*
val createTableQuery = """
    CREATE TABLE IF NOT EXISTS `$tableName` (
        `flow_id` TEXT PRIMARY KEY NOT NULL,
        `tenant_id` INTEGER NOT NULL,
        `object_uuid` TEXT NOT NULL,
        `object_type` INTEGER NOT NULL,
        `member_name` TEXT NOT NULL,
        `member_code` TEXT NOT NULL,
        `member_tuid` INTEGER NOT NULL,
        `dept_name` TEXT NOT NULL,
        `dept_code` TEXT NOT NULL,
        `member_wallet_id` INTEGER NOT NULL,
        `wallet_name` TEXT NOT NULL,
        `related_business` INTEGER NOT NULL,
        `merchant_name` TEXT NOT NULL,
        `merchant_id` INTEGER NOT NULL,
        `device_name` TEXT NOT NULL,
        `device_code` TEXT NOT NULL,
        `place_id` INTEGER NOT NULL,
        `place_name` TEXT NOT NULL,
        `serial_number` TEXT NOT NULL,
        `trading_mode` INTEGER NOT NULL,
        `type` INTEGER NOT NULL,
        `charge_mode` TEXT,
        `charge_status` INTEGER,
        `consume_mode` INTEGER NOT NULL,
        `consume_time` TEXT NOT NULL,
        `refund_mode` TEXT,
        `refund_reason` TEXT,
        `subsidy_type` TEXT,
        `card_number` TEXT NOT NULL,
        `amount` REAL NOT NULL,
        `recharge_balance_deduction` REAL NOT NULL,
        `subsidy_balance_deduction` REAL NOT NULL,
        `balance` REAL NOT NULL,
        `correction_status` INTEGER,
        `correction_serial_number` TEXT,
        `correction_original_amount` REAL,
        `correction_reason` TEXT,
        `correction_version` INTEGER,
        `operator` TEXT,
        `generate_time` TEXT NOT NULL,
        `create_time` TEXT NOT NULL,
        `remark` TEXT
    )
""".trimIndent()
 */