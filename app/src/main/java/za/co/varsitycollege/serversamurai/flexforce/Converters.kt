package za.co.varsitycollege.serversamurai.flexforce.Models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import za.co.varsitycollege.serversamurai.flexforce.Exercise

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

    fun ApiDataModels.Exercise.toParcelableExercise(): Exercise {
        return Exercise(
            name = this.name,
            sets = this.sets,
            reps = this.reps,
            muscleGroup = "", // Add muscleGroup if needed
            equipment = this.equipment
        )
    }
}