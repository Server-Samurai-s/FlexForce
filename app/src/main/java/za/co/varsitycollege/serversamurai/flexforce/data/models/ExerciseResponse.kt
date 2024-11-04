package za.co.varsitycollege.serversamurai.flexforce.data.models

data class ExerciseResponse(
    val exercises: List<ExerciseEntity>? = null,
    val message: String? = null,
    val success: Boolean = false
)