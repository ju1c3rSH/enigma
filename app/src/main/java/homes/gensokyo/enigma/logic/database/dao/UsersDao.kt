package homes.gensokyo.enigma.logic.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import homes.gensokyo.enigma.logic.database.model.User

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertUser(user: User): Long

    @Update
     fun updateUser(user: User)

    @Delete
     fun deleteUser(user: User): Int

    //@Query("DELETE FROM users")
    //suspend fun deleteAllUsers():Int

    @Query("SELECT * FROM users")
     fun getAllUsers(): List<User>


    @Query("select * from users where userId = :userId")
     fun getUserById(userId: Int): User?
}

