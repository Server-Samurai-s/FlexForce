package za.co.varsitycollege.serversamurai.flexforce.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import za.co.varsitycollege.serversamurai.flexforce.service.Converters

@Entity
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Default value for id
    val workoutName: String,
    val workoutDay: String,
    @TypeConverters(Converters::class) val exercises: List<Exercise>
)