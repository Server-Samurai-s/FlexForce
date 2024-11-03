package za.co.varsitycollege.serversamurai.flexforce.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uid: String,
    val name: String,
    val surname: String,
    val nickname: String,
    val email: String,
    val password: String
)