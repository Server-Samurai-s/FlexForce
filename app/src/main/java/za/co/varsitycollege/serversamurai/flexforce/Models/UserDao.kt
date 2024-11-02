package za.co.varsitycollege.serversamurai.flexforce.Models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Delete
    fun delete(user: User)
}