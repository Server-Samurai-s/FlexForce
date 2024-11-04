package za.co.varsitycollege.serversamurai.flexforce.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.FitnessEntryEntity

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): List<ExerciseEntity>?

    @Query("SELECT * FROM exercises WHERE muscleGroup IN (:muscleGroups)")
    fun getExercisesByMuscleGroup(muscleGroups: List<String>): List<ExerciseEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(exercises: List<ExerciseEntity>)

    @Query("DELETE FROM exercises")
    fun deleteAllExercises()
}