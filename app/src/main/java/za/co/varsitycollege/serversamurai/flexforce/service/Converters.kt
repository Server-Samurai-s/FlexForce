package za.co.varsitycollege.serversamurai.flexforce.service

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import za.co.varsitycollege.serversamurai.flexforce.data.models.Exercise

class Converters {
    @TypeConverter
    fun fromExerciseList(value: List<Exercise>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Exercise>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toExerciseList(value: String): List<Exercise>? {
        val gson = Gson()
        val type = object : TypeToken<List<Exercise>>() {}.type
        return gson.fromJson(value, type)
    }

    fun Exercise.toParcelableExercise(): Exercise {
        return Exercise(
            id = this.id,
            name = this.name,
            sets = this.sets,
            reps = this.reps,
            equipment = this.equipment,
            muscleGroup = this.muscleGroup
        )
    }
}