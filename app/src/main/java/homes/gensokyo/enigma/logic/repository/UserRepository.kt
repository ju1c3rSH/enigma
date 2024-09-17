package homes.gensokyo.enigma.logic.repository

import homes.gensokyo.enigma.logic.database.AppDatabase
import homes.gensokyo.enigma.logic.database.model.User

//TODO mayB OBJ?

class UserRepository (private var rdb: AppDatabase) {
    private val database get() = AppDatabase.instance
    private val dao get() = database.UsersDao()

     fun getAllUsers() = dao.getAllUsers()
    fun getUserById(id: Int) = dao.getUserById(id)
     fun insertUser(user: User) = dao.insertUser(user)

    fun updateUser(user: User) = dao.updateUser(user)

     fun deleteUser(user: User) = dao.deleteUser(user)



}

