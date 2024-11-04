package za.co.varsitycollege.serversamurai.flexforce.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import za.co.varsitycollege.serversamurai.flexforce.data.models.WorkoutEntity

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts WHERE userEmail = :email")
    fun getAllWorkouts(email: String): List<WorkoutEntity>?

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkout(workoutId: String): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE userEmail = :email ORDER BY completionCount DESC LIMIT 1")
    fun getFavouriteWorkout(email: String): WorkoutEntity?
}