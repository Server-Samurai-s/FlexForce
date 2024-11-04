package za.co.varsitycollege.serversamurai.flexforce.data.models

data class ApiResponse(
    val exercises: List<MuscleGroupExercises>
)

data class MuscleGroupExercises(
    val muscleGroup: String,
    val exercises: List<ApiExercise>
)

data class ApiExercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val equipment: String
)