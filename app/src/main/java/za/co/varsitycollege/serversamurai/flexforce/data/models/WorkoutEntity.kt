package za.co.varsitycollege.serversamurai.flexforce.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import za.co.varsitycollege.serversamurai.flexforce.service.Converters

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,  // Use userEmail instead of userId
    val workoutName: String,
    val workoutDay: String,
    val exerciseEntities: List<ExerciseEntity>,
    val completionDate: String,
    val completionCount: Int
)