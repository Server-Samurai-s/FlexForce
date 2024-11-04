package za.co.varsitycollege.serversamurai.flexforce.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import za.co.varsitycollege.serversamurai.flexforce.data.models.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userEntity: UserEntity)

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): UserEntity?

    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<UserEntity>

    @Delete
    fun delete(userEntity: UserEntity)
}