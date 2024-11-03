package za.co.varsitycollege.serversamurai.flexforce.Models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workout: WorkoutEntity)

    @Query("SELECT * FROM WorkoutEntity")
    fun getAllWorkouts(): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE id = :workoutId")
    fun getWorkout(workoutId: String): WorkoutEntity
}