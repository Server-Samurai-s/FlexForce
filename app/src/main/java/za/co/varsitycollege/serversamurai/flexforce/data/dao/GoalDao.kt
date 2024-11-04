package za.co.varsitycollege.serversamurai.flexforce.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import za.co.varsitycollege.serversamurai.flexforce.data.models.GoalEntity


@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE userEmail = :email")
    fun getAllGoalsForUser(email: String): List<GoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoal(goal: GoalEntity)
}