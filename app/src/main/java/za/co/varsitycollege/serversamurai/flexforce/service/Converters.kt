package za.co.varsitycollege.serversamurai.flexforce.service

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseEntity

class Converters {
    @TypeConverter
    fun fromExerciseList(value: List<ExerciseEntity>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<ExerciseEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toExerciseList(value: String): List<ExerciseEntity>? {
        val gson = Gson()
        val type = object : TypeToken<List<ExerciseEntity>>() {}.type
        return gson.fromJson(value, type)
    }

    fun ExerciseEntity.toParcelableExercise(): ExerciseEntity {
        return ExerciseEntity(
            name = this.name,
            sets = this.sets,
            reps = this.reps,
            equipment = this.equipment,
            muscleGroup = this.muscleGroup
        )
    }
}